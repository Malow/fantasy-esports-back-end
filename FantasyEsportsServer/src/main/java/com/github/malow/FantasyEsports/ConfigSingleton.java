package com.github.malow.FantasyEsports;

import com.github.malow.FantasyEsports.config.FantasyEsportsServerConfig;
import com.github.malow.malowlib.MaloWLogger;
import com.github.malow.malowlib.confighandler.ConfigHandler;
import com.github.malow.malowlib.confighandler.ConfigHandler.ConfigException;

public class ConfigSingleton
{
  private static FantasyEsportsServerConfig config = null;

  static
  {
    try
    {
      config = ConfigHandler.loadConfig("config.txt", FantasyEsportsServerConfig.class);
    }
    catch (ConfigException e)
    {
      MaloWLogger.error("Failed to load config: ", e);
    }
  }

  private ConfigSingleton()
  {

  }

  public static FantasyEsportsServerConfig getConfig()
  {
    return config;
  }
}