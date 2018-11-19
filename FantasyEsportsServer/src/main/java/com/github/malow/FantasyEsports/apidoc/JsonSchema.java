package com.github.malow.FantasyEsports.apidoc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.github.malow.malowlib.MaloWLogger;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonSchema
{
  public static class JsonSchemaNode
  {
    public String type;
    public boolean required = true;
  }

  public static class JsonSchemaParent extends JsonSchemaNode
  {
    public JsonSchemaNode child;
  }

  public static class JsonSchemaProperty extends JsonSchemaParent
  {
    public String name;

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + (this.name == null ? 0 : this.name.hashCode());
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
      JsonSchemaProperty other = (JsonSchemaProperty) obj;
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
      return true;
    }
  }

  public static class JsonSchemaObject extends JsonSchemaNode
  {
    public Set<JsonSchemaProperty> properties = new HashSet<>();
  }

  public JsonSchemaNode root;

  public static JsonSchema fromString(String json)
  {
    if (json != null)
    {
      return new JsonSchema(json);
    }
    return null;
  }

  public static JsonSchema fromStrings(List<String> jsons)
  {
    if (jsons != null && !jsons.isEmpty())
    {
      return new JsonSchema(jsons);
    }
    return null;
  }

  private JsonSchema(String json)
  {
    JsonElement je = new JsonParser().parse(json);
    this.root = createChildrenRecursively(je);
  }

  private JsonSchema(List<String> jsons)
  {
    String first = jsons.remove(0);
    JsonElement je = new JsonParser().parse(first);
    this.root = createChildrenRecursively(je);
    for (String json : jsons)
    {
      je = new JsonParser().parse(json);
      this.root = mergeNodesRecursively(this.root, createChildrenRecursively(je));
    }
  }

  private static String getType(JsonElement je)
  {
    if (je.isJsonPrimitive())
    {
      if (je.getAsJsonPrimitive().isBoolean())
      {
        return "boolean";
      }
      else if (je.getAsJsonPrimitive().isString())
      {
        return "string";
      }
      else if (je.getAsJsonPrimitive().isNumber())
      {
        return "number";
      }
    }
    if (je.isJsonArray())
    {
      return "array";
    }
    if (je.isJsonObject())
    {
      return "object";
    }
    MaloWLogger.error("Error, unsupported json type: " + je.getAsJsonPrimitive().toString());
    return "";
  }

  private static JsonSchemaNode mergeNodesRecursively(JsonSchemaNode n1, JsonSchemaNode n2)
  {
    if (n1 == null || n2 == null)
    {
      return null;
    }
    if (!n1.getClass().equals(n2.getClass()))
    {
      MaloWLogger.error("Class missmatch: " + n1.getClass().getSimpleName() + " + " + n2.getClass().getSimpleName());
    }

    if (n1 instanceof JsonSchemaObject)
    {
      JsonSchemaObject n1Obj = (JsonSchemaObject) n1;
      JsonSchemaObject n2Obj = (JsonSchemaObject) n2;
      JsonSchemaObject merged = new JsonSchemaObject();
      merged.type = "object";
      Set<JsonSchemaProperty> mergedProperties = new HashSet<>();
      for (JsonSchemaProperty prop1 : n1Obj.properties)
      {
        Optional<JsonSchemaProperty> prop2 = n2Obj.properties.stream().filter(p2 -> p2.equals(prop1)).findFirst();
        if (prop2.isPresent())
        {
          JsonSchemaProperty mergedProp = new JsonSchemaProperty();
          mergedProp.name = prop1.name;
          mergedProp.type = prop1.type;
          mergedProp.required = prop1.required && prop2.get().required;
          mergedProp.child = mergeNodesRecursively(prop1.child, prop2.get().child);
          mergedProperties.add(mergedProp);
        }
        else
        {
          prop1.required = false;
          mergedProperties.add(prop1);
        }
      }
      for (JsonSchemaProperty prop2 : n2Obj.properties)
      {
        prop2.required = false;
        mergedProperties.add(prop2);
      }
      merged.properties = mergedProperties;
      return merged;
    }
    else if (n1 instanceof JsonSchemaParent)
    {
      JsonSchemaParent merged = new JsonSchemaParent();
      JsonSchemaParent n1Prop = (JsonSchemaParent) n1;
      JsonSchemaParent n2Prop = (JsonSchemaParent) n2;
      JsonSchemaNode mergedChild = mergeNodesRecursively(n1Prop.child, n2Prop.child);
      merged = n1Prop;
      merged.child = mergedChild;
      return merged;
    }
    return n1;
  }

  private static JsonSchemaNode createChildrenRecursively(JsonElement je)
  {
    if (je.isJsonNull())
    {
      return null;
    }

    String type = getType(je);
    if ("boolean".equals(type) || "string".equals(type) || "number".equals(type))
    {
      JsonSchemaNode node = new JsonSchemaNode();
      node.type = type;
      return node;
    }
    else if ("array".equals(type))
    {
      JsonSchemaParent node = new JsonSchemaParent();
      node.type = type;
      List<JsonSchemaNode> children = new ArrayList<>();
      for (JsonElement e : je.getAsJsonArray())
      {
        children.add(createChildrenRecursively(e));
      }
      if (children.size() > 0)
      {
        node.child = children.remove(0);
        for (JsonSchemaNode child : children)
        {
          node.child = mergeNodesRecursively(node.child, child);
        }
        return node;
      }
      else
      {
        return null;
      }
    }
    else if ("object".equals(type))
    {
      JsonSchemaObject schemaObject = new JsonSchemaObject();
      schemaObject.type = type;
      for (Map.Entry<String, JsonElement> e : je.getAsJsonObject().entrySet())
      {
        JsonSchemaProperty property = new JsonSchemaProperty();
        property.name = e.getKey();
        String childType = getType(e.getValue());
        property.type = childType;
        if ("object".equals(childType))
        {
          property.child = createChildrenRecursively(e.getValue());
          schemaObject.properties.add(property);
        }
        else if ("array".equals(childType))
        {
          property.child = createChildrenRecursively(e.getValue());
          if (property.child != null)
          {
            schemaObject.properties.add(property);
          }
        }
        else
        {
          schemaObject.properties.add(property);
        }
      }
      return schemaObject;
    }
    MaloWLogger.warning("Error, hit the end of createChildrenRecursively: " + je.toString());
    return null;
  }
}
