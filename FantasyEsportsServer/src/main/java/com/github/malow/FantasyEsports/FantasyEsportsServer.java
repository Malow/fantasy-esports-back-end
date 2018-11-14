package com.github.malow.FantasyEsports;

import java.util.HashMap;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@EnableAutoConfiguration
@SpringBootApplication
public class FantasyEsportsServer extends SpringBootServletInitializer
{
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
  {
    return application.sources(FantasyEsportsServer.class);
  }

  public static void main(String[] args) throws Exception
  {
    HashMap<String, Object> props = new HashMap<>();
    props.put("logging.level.org.springframework.web", "ERROR");
    props.put("logging.level.org.mongodb.driver", "ERROR");
    props.put("logging.level.org.zalando.logbook", "TRACE");
    props.put("server.port", 8754);
    props.put("security.require-ssl", true);
    props.put("server.ssl.key-store", "LetsEncryptCerts/domain.p12");
    props.put("server.ssl.key-store-password", "password");
    props.put("server.ssl.keyStoreType", "PKCS12");
    props.put("server.ssl.keyAlias", "tomcat");
    props.put("spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS", false);
    props.put("spring.data.mongodb.uri", "mongodb+srv://admin:asdf@cluster0-u4tzo.mongodb.net/FantasyEsportsDevelop?retryWrites=true");
    new SpringApplicationBuilder()
        .sources(FantasyEsportsServer.class)
        .properties(props)
        .run(args);
  }
}
