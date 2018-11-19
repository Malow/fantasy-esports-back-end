package com.github.malow.FantasyEsports.apidoc;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.DefaultHttpLogWriter;
import org.zalando.logbook.DefaultHttpLogWriter.Level;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.StreamHttpLogWriter;

import com.github.malow.FantasyEsports.ConfigSingleton;
import com.github.malow.FantasyEsports.config.DeployMode;
import com.github.malow.FantasyEsports.config.FantasyEsportsServerConfig;

@Configuration
public class LogbookConfiguration
{
  public class CustomPrintStream extends PrintStream
  {
    private Logger logger;

    public CustomPrintStream(OutputStream out) throws UnsupportedEncodingException
    {
      super(out, true, "UTF-8");
      this.logger = LoggerFactory.getLogger("org.zalando.logbook.Logbook");
    }

    @Override
    public void println(String x)
    {
      this.logger.info(x);
      super.println(x);
    }
  }

  public static ByteArrayOutputStream baos = new ByteArrayOutputStream();

  @Bean
  public HttpLogWriter writer(final Logger httpLogger) throws UnsupportedEncodingException
  {
    FantasyEsportsServerConfig config = ConfigSingleton.getConfig();
    DeployMode deployMode = config.deployMode;
    if (deployMode.equals(DeployMode.DEVELOP))
    {
      return new StreamHttpLogWriter(new CustomPrintStream(baos));
    }
    else
    {
      return new DefaultHttpLogWriter(LoggerFactory.getLogger("org.zalando.logbook.Logbook"), Level.INFO);
    }
  }
}
