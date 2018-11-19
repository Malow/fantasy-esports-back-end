package com.github.malow.FantasyEsports.apidoc;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.malow.FantasyEsports.ConfigSingleton;
import com.github.malow.FantasyEsports.FantasyEsportsServer;
import com.github.malow.FantasyEsports.apidoc.deseralizer.LogbookDeserializer;
import com.github.malow.FantasyEsports.config.DeployMode;
import com.github.malow.FantasyEsports.config.FantasyEsportsServerConfig;
import com.github.malow.FantasyEsports.services.Controller;

@CrossOrigin(maxAge = 3600)
@RestController
public class ApiDocController extends Controller
{
  private ApiDocData data;

  public ApiDocController()
  {
    Set<Class<?>> controllerClasses = new Reflections(FantasyEsportsServer.class.getPackage().getName())
        .getTypesAnnotatedWith(RestController.class);
    controllerClasses = controllerClasses.stream().filter(c -> !c.equals(this.getClass()))
        .filter(c -> Arrays.stream(c.getAnnotations()).anyMatch(a -> a.annotationType().equals(RestController.class)))
        .collect(Collectors.toSet());
    this.data = new ApiDocData(controllerClasses);
  }

  @GetMapping(value = { "/apidoc" })
  public ResponseEntity<String> listLeagues()
  {
    FantasyEsportsServerConfig config = ConfigSingleton.getConfig();
    if (config.deployMode.equals(DeployMode.DEVELOP))
    {
      List<String> stringLines = Arrays.stream(LogbookConfiguration.baos.toString().split("\n")).collect(Collectors.toList());
      List<String> bannedCorells = stringLines.stream().filter(s -> s.contains("/apidoc"))
          .map(s ->
          {
            Matcher matcher = Pattern.compile("\"correlation\":\"([0-9a-f-]*)\"").matcher(s);
            matcher.find();
            return matcher.group(1);
          })
          .collect(Collectors.toList());
      for (String corell : bannedCorells)
      {
        stringLines = stringLines.stream().filter(s -> !s.contains(corell)).collect(Collectors.toList());
      }
      this.data.addRequestsAndResponses(LogbookDeserializer.deserialize(stringLines));
      return ResponseEntity.ok(this.data.toString());
    }
    return ResponseEntity.status(404).body("");
  }
}
