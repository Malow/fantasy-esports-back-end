package com.github.malow.FantasyEsports.apidoc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.malow.FantasyEsports.apidoc.JsonSchema.JsonSchemaNode;
import com.github.malow.FantasyEsports.apidoc.JsonSchema.JsonSchemaObject;
import com.github.malow.FantasyEsports.apidoc.JsonSchema.JsonSchemaParent;
import com.github.malow.FantasyEsports.apidoc.JsonSchema.JsonSchemaProperty;
import com.github.malow.FantasyEsports.apidoc.deseralizer.LogbookDeserializer.RequestResponsePair;
import com.github.malow.FantasyEsports.services.Request;
import com.github.malow.FantasyEsports.services.Request.Mandatory;
import com.github.malow.malowlib.GsonSingleton;
import com.github.malow.malowlib.MaloWLogger;

public class ApiDocData
{
  public ApiDocData(Set<Class<?>> controllerClasses)
  {
    for (Class<?> clazz : controllerClasses)
    {
      EntityData entityData = new EntityData();
      entityData.name = clazz.getSimpleName().replace("Controller", "");
      ApiDoc apiDocAnnotation = clazz.getAnnotation(ApiDoc.class);
      if (apiDocAnnotation != null)
      {
        entityData.description = apiDocAnnotation.value();
      }
      for (Method method : clazz.getMethods())
      {
        PathMethod pathMethod = this.getPathAndMethod(method);
        if (pathMethod != null)
        {
          PathMethodData pathMethodData = new PathMethodData();
          pathMethodData.method = pathMethod.method;
          pathMethodData.name = method.getName();
          ApiDoc apiDocMethodAnnotation = method.getAnnotation(ApiDoc.class);
          if (apiDocMethodAnnotation != null)
          {
            pathMethodData.description = apiDocMethodAnnotation.value();
          }

          List<QueryParameter> queryParameters = new ArrayList<>();
          Optional<Parameter> queryRequestParameter = Arrays.stream(method.getParameters())
              .filter(p -> Request.class.isAssignableFrom(p.getType())).findFirst();
          if (queryRequestParameter.isPresent())
          {
            Class<?> parameterClass = queryRequestParameter.get().getType();
            for (Field field : parameterClass.getFields())
            {
              QueryParameter qp = new QueryParameter();
              qp.name = field.getName();
              qp.dataType = getQueryParameterDataTypeForClass(field.getType());
              if (field.getAnnotation(Mandatory.class) != null)
              {
                qp.required = true;
              }
              queryParameters.add(qp);
            }
          }
          List<Parameter> queryAnnotatedParameters = Arrays.stream(method.getParameters()).filter(p -> p.getAnnotation(RequestParam.class) != null)
              .collect(Collectors.toList());
          for (Parameter parameter : queryAnnotatedParameters)
          {
            QueryParameter qp = new QueryParameter();
            qp.name = parameter.getAnnotation(RequestParam.class).value();
            qp.dataType = getQueryParameterDataTypeForClass(parameter.getType());
            if (parameter.getAnnotation(RequestParam.class).required())
            {
              qp.required = true;
            }
            queryParameters.add(qp);
          }
          pathMethodData.queryParameters = queryParameters;
          entityData.addPathMethodData(pathMethod.path, pathMethodData);
        }
      }
      this.data.add(entityData);
    }
  }

  private static String getQueryParameterDataTypeForClass(Class<?> clazz)
  {
    if (clazz.equals(String.class))
    {
      return "string";
    }
    else if (Enum.class.isAssignableFrom(clazz))
    {
      return "string"; // TODO: add the different enum options
    }
    else if (Optional.class.equals(clazz))
    {
      return "string"; // TODO: God no, RIP
    }
    else
    {
      MaloWLogger.error("Missing mapping");
    }
    return "string";
  }

  public static class QueryParameter
  {
    public String name;
    public String dataType;
    public boolean required = false;
  }

  public static class PathMethod
  {
    public String path;
    public String method;
  }

  private PathMethod getPathAndMethod(Method method)
  {
    PathMethod pathMethod = new PathMethod();
    if (method.isAnnotationPresent(PostMapping.class))
    {
      pathMethod.path = method.getAnnotation(PostMapping.class).value()[0];
      pathMethod.method = "post";
    }
    else if (method.isAnnotationPresent(GetMapping.class))
    {
      pathMethod.path = method.getAnnotation(GetMapping.class).value()[0];
      pathMethod.method = "get";
    }
    else if (method.isAnnotationPresent(PatchMapping.class))
    {
      pathMethod.path = method.getAnnotation(PatchMapping.class).value()[0];
      pathMethod.method = "patch";
    }
    else if (method.isAnnotationPresent(RequestMapping.class))
    {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
      if (requestMapping.method()[0].equals(RequestMethod.GET))
      {
        pathMethod.path = requestMapping.value()[0];
        pathMethod.method = "get";
      }
      else if (requestMapping.method()[0].equals(RequestMethod.POST))
      {
        pathMethod.path = requestMapping.value()[0];
        pathMethod.method = "post";
      }
    }
    if (pathMethod.path == null || pathMethod.method == null)
    {
      return null;
    }
    return pathMethod;
  }


  private List<EntityData> data = new ArrayList<>();

  public static class EntityData
  {
    public String name;
    public String description;
    private List<PathData> paths = new ArrayList<>();

    public void addPathMethodData(String path, PathMethodData pathMethodData)
    {
      for (PathData pd : this.paths)
      {
        if (pd.path.equals(path))
        {
          pd.pathMethods.add(pathMethodData);
          return;
        }
      }
      PathData pathData = new PathData();
      pathData.path = path;
      pathData.pathMethods.add(pathMethodData);
      this.paths.add(pathData);
    }
  }

  public static class PathData
  {
    public String path;
    public List<PathMethodData> pathMethods = new ArrayList<>();
  }

  public static class ParameterData
  {
    public String name;
    public boolean required;
    public String dataType;
    public String parameterType;

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + (this.name == null ? 0 : this.name.hashCode());
      result = prime * result + (this.parameterType == null ? 0 : this.parameterType.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }
      if (obj == null)
      {
        return false;
      }
      if (this.getClass() != obj.getClass())
      {
        return false;
      }
      ParameterData other = (ParameterData) obj;
      if (this.name == null)
      {
        if (other.name != null)
        {
          return false;
        }
      }
      else if (!this.name.equals(other.name))
      {
        return false;
      }
      if (this.parameterType == null)
      {
        if (other.parameterType != null)
        {
          return false;
        }
      }
      else if (!this.parameterType.equals(other.parameterType))
      {
        return false;
      }
      return true;
    }
  }

  public static class PathMethodData
  {
    public String name;
    public String method;
    public String description;
    public List<RequestResponsePair> requestResponsePairs = new ArrayList<>();
    public List<QueryParameter> queryParameters;

    public SummerizedRequestResponseData getSummerizedRequestResponseData()
    {
      SummerizedRequestResponseData summerizedData = new SummerizedRequestResponseData();
      List<RequestResponsePair> ungroupedPairs = new ArrayList<>(this.requestResponsePairs);
      Map<Integer, List<RequestResponsePair>> groupedPairs = new HashMap<>();
      while (!ungroupedPairs.isEmpty())
      {
        RequestResponsePair pair = ungroupedPairs.remove(0);
        List<RequestResponsePair> thisGroupPairs = groupedPairs.get(pair.response.status);
        if (thisGroupPairs == null)
        {
          thisGroupPairs = new ArrayList<>();
        }
        thisGroupPairs.add(pair);
        groupedPairs.put(pair.response.status, thisGroupPairs);
      }
      for (Map.Entry<Integer, List<RequestResponsePair>> entry : groupedPairs.entrySet())
      {
        ResponseData responseData = new ResponseData();
        responseData.httpStatus = entry.getKey();
        List<String> responseBodies = entry.getValue().stream().map(rr -> rr.response.body).filter(s -> s != null).collect(Collectors.toList());
        responseData.responseBodyJsonSchema = JsonSchema.fromStrings(responseBodies);
        responseData.description = responseBodies.stream().map(s ->
        {
          Matcher matcher = Pattern.compile("\"errorCode\": ?\"([a-z-]*)\"").matcher(s);
          if (matcher.find())
          {
            return matcher.group(1);
          }
          return null;
        }).filter(s -> s != null && !s.isEmpty()).distinct().collect(Collectors.joining(", "));
        if (responseData.description == null || responseData.description.isEmpty())
        {
          responseData.description = HttpStatus.valueOf(entry.getKey()).name();
        }
        summerizedData.responses.add(responseData);
      }

      List<RequestResponsePair> okRequestResponses = groupedPairs.get(200);
      if (okRequestResponses != null && !okRequestResponses.isEmpty())
      {
        for (RequestResponsePair requestResponsePair : okRequestResponses)
        {
          List<ParameterData> params = new ArrayList<>();
          for (Map.Entry<String, String> header : requestResponsePair.request.headers.entrySet())
          {
            ParameterData param = new ParameterData();
            param.name = header.getKey();
            param.required = true;
            param.dataType = "string";
            param.parameterType = "header";
            params.add(param);
          }
          for (ParameterData header : summerizedData.requestHeaders)
          {
            if (!params.contains(header))
            {
              header.required = false;
            }
          }
          for (ParameterData header : params)
          {
            if (!summerizedData.requestHeaders.contains(header))
            {
              summerizedData.requestHeaders.add(header);
            }
          }
        }

        List<String> requestBodies = okRequestResponses.stream().map(rr -> rr.request.body).filter(s -> s != null).collect(Collectors.toList());
        summerizedData.requestBodyJsonSchema = JsonSchema.fromStrings(requestBodies);
      }

      return summerizedData;
    }
  }

  public static class SummerizedRequestResponseData
  {
    public List<ParameterData> requestHeaders = new ArrayList<>();
    public JsonSchema requestBodyJsonSchema;
    public List<ResponseData> responses = new ArrayList<>();
  }

  public static class ResponseData
  {
    public int httpStatus;
    public String description;
    public JsonSchema responseBodyJsonSchema;
    public List<String> errorCodes = new ArrayList<>();
  }

  private static boolean matchPathsHard(String controllerPath, String realDataPath)
  {
    if (controllerPath.equalsIgnoreCase(realDataPath))
    {
      return true;
    }
    realDataPath = realDataPath.split("\\?")[0];
    controllerPath = controllerPath.replaceAll("\\{[0-9a-zA-Z]*\\}", "{id}");
    realDataPath = realDataPath.replaceAll("/[0-9a-f]{24}", "/{id}");
    return controllerPath.equalsIgnoreCase(realDataPath);
  }

  private static boolean matchPathsSoft(String controllerPath, String realDataPath)
  {
    if (controllerPath.equalsIgnoreCase(realDataPath))
    {
      return true;
    }
    realDataPath = realDataPath.split("\\?")[0];
    controllerPath = controllerPath.replaceAll("\\{[0-9a-zA-Z]*\\}", "{id}");

    String[] controllerPaths = controllerPath.split("/");
    String[] realDataPaths = realDataPath.split("/");
    if (controllerPaths.length != realDataPaths.length)
    {
      return false;
    }
    for (int i = 0; i < controllerPaths.length; i++)
    {
      if (controllerPaths[i].equals(realDataPaths[i]) || controllerPaths[i].equals("{id}"))
      {
        continue;
      }
      else
      {
        return false;
      }
    }
    return true;
  }

  public void addRequestsAndResponses(List<RequestResponsePair> requestResponsePairs)
  {
    for (RequestResponsePair requestResponsePair : requestResponsePairs)
    {
      boolean matched = false;
      for (EntityData entity : this.data)
      {
        for (PathData pathData : entity.paths)
        {
          for (PathMethodData pathMethodData : pathData.pathMethods)
          {
            if (requestResponsePair.method.equalsIgnoreCase(pathMethodData.method) && matchPathsHard(pathData.path, requestResponsePair.path))
            {
              pathMethodData.requestResponsePairs.add(requestResponsePair);
              matched = true;
            }
          }
        }
      }
      if (!matched)
      {
        for (EntityData entity : this.data)
        {
          for (PathData pathData : entity.paths)
          {
            for (PathMethodData pathMethodData : pathData.pathMethods)
            {
              if (requestResponsePair.method.equalsIgnoreCase(pathMethodData.method) && matchPathsSoft(pathData.path, requestResponsePair.path))
              {
                pathMethodData.requestResponsePairs.add(requestResponsePair);
                matched = true;
              }
            }
          }
        }
        if (!matched)
        {
          MaloWLogger.error("Failed to match: " + GsonSingleton.toJson(requestResponsePair));
        }
      }
    }
  }

  // Output

  private StringBuffer sb = new StringBuffer();

  @Override
  public String toString()
  {
    this.data.sort(new Comparator<EntityData>()
    {
      @Override
      public int compare(EntityData e1, EntityData e2)
      {
        return e1.name.compareToIgnoreCase(e2.name);
      }
    });

    for (EntityData entity : this.data)
    {
      entity.paths.sort(new Comparator<PathData>()
      {
        @Override
        public int compare(PathData p1, PathData p2)
        {
          return p1.path.compareToIgnoreCase(p2.path);
        }
      });
      for (PathData pathData : entity.paths)
      {
        pathData.pathMethods.sort(new Comparator<PathMethodData>()
        {
          @Override
          public int compare(PathMethodData pm1, PathMethodData pm2)
          {
            return pm1.method.compareToIgnoreCase(pm2.method);
          }
        });
      }
    }
    this.sb = new StringBuffer();

    this.append("openapi: 3.0.1");
    this.append("info:");
    this.append("  title: FantasyEsports");
    this.append("  description: The API description for FantasyEsports");
    this.append("  version: \"0.2\"");
    this.append("servers:");
    this.append("  - url: 'https://malow.duckdns.org:8754'");
    this.append("tags:");
    for (EntityData entity : this.data)
    {
      this.append("  - name: " + entity.name);
      if (entity.description != null)
      {
        this.append("    description: " + entity.description);
      }
    }
    this.append("paths:");
    for (EntityData entity : this.data)
    {
      for (PathData pathData : entity.paths)
      {
        this.append("  '" + pathData.path + "':");
        for (PathMethodData pathMethodData : pathData.pathMethods)
        {
          SummerizedRequestResponseData summarizedData = pathMethodData.getSummerizedRequestResponseData();
          this.append("    " + pathMethodData.method + ":");
          this.append("      tags:");
          this.append("        - " + entity.name);

          List<String> parameterNames = new ArrayList<>();
          Matcher matcher = Pattern.compile("\\{([0-9a-zA-Z]*)\\}").matcher(pathData.path);
          while (matcher.find())
          {
            parameterNames.add(matcher.group(1));
          }
          if (!parameterNames.isEmpty() || !pathMethodData.queryParameters.isEmpty() || !summarizedData.requestHeaders.isEmpty())
          {
            this.append("      parameters:");
            for (String parameterName : parameterNames)
            {
              this.append("        - name: " + parameterName);
              this.append("          in: path");
              this.append("          required: true");
              this.append("          schema:");
              this.append("            type: string");
            }
            for (QueryParameter queryParameter : pathMethodData.queryParameters)
            {
              this.append("        - name: " + queryParameter.name);
              this.append("          in: query");
              this.append("          required: " + (queryParameter.required ? "true" : "false"));
              this.append("          schema:");
              this.append("            type: " + queryParameter.dataType);
              /* Possible todo: support complex queryParameter inputs
                parameters:
                  - name: status
                    in: query
                    description: Status values that need to be considered for filter
                    required: true
                    style: form
                    schema:
                      type: array
                      items:
                        type: string
                        enum:
                          - available
                          - pending
                          - sold
                        default: available
               */
            }
            for (ParameterData requestHeader : summarizedData.requestHeaders)
            {
              this.append("        - name: " + requestHeader.name);
              this.append("          in: header");
              this.append("          required: " + (requestHeader.required ? "true" : "false"));
              this.append("          schema:");
              this.append("            type: " + requestHeader.dataType);
              /* Possible todo: support complex queryParameter inputs
                parameters:
                  - name: api_key
                    in: header
                    required: false
                    schema:
                      type: string
                    example: "Bearer <TOKEN>"
                */
            }
          }

          this.append("      summary: " + pathMethodData.name);
          if (pathMethodData.description != null)
          {
            this.append("      description: " + pathMethodData.description);
          }
          if ((pathMethodData.method.equals("post") || pathMethodData.method.equals("patch")) && summarizedData.requestBodyJsonSchema != null)
          {
            this.append("      requestBody:");
            this.append("        content:");
            this.append("          application/json:");
            this.append("            schema:");
            this.appendJsonSchema(summarizedData.requestBodyJsonSchema, "              ");
          }
          this.append("      responses:");
          for (ResponseData responseData : summarizedData.responses)
          {
            this.append("        '" + responseData.httpStatus + "':");
            this.append("          description: " + responseData.description);
            if (responseData.responseBodyJsonSchema != null)
            {
              this.append("          content:");
              this.append("            application/json:");
              this.append("              schema:");
              this.appendJsonSchema(responseData.responseBodyJsonSchema, "                ");
            }
          }
        }
      }
    }
    return this.sb.toString();
  }

  private void appendJsonSchema(JsonSchema schema, String indention)
  {
    this.append("type: " + schema.root.type, indention);
    this.appendRecursively(schema.root, indention);
  }

  public void appendRecursively(JsonSchemaNode node, String indention)
  {
    if (node instanceof JsonSchemaObject)
    {
      JsonSchemaObject nodeObject = (JsonSchemaObject) node;
      List<JsonSchemaProperty> requriedProps = nodeObject.properties.stream().filter(p -> p.required).collect(Collectors.toList());
      if (!requriedProps.isEmpty())
      {
        this.append("required:", indention);
        for (JsonSchemaProperty prop : requriedProps)
        {
          this.append("- " + prop.name, indention + "  ");
        }
      }
      this.append("properties:", indention);
      for (JsonSchemaProperty prop : nodeObject.properties)
      {
        this.append(prop.name + ":", indention + "  ");
        this.append("type: " + prop.type, indention + "    ");
        if (prop.child != null)
        {
          this.appendRecursively(prop.child, indention + "    ");
        }
      }
    }
    else if (node instanceof JsonSchemaParent)
    {
      JsonSchemaParent nodeParent = (JsonSchemaParent) node;
      this.append("items: ", indention);
      this.append("type: " + nodeParent.child.type, indention + "  ");
      if (nodeParent.child instanceof JsonSchemaObject || nodeParent.child instanceof JsonSchemaParent)
      {
        this.appendRecursively(nodeParent.child, indention + "  ");
      }
    }
    else
    {
      MaloWLogger.error("Got wrong class: " + node.getClass());
    }
  }

  private void append(String s, String indention)
  {
    this.append(indention + s);
  }

  private void append(String s)
  {
    this.sb.append(s);
    this.sb.append("\n");
  }
}
