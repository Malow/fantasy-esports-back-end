package com.github.malow.FantasyEsports.config;

import com.github.malow.malowlib.confighandler.Config;

public class FantasyEsportsServerConfig extends Config
{
  public DeployMode deployMode = DeployMode.DEVELOP;
  public boolean printRequestResponseLogs = true;

  @Override
  public String getVersion()
  {
    return "1.0";
  }

  @Override
  public Class<?> getNextVersionClass()
  {
    return null;
  }

  @Override
  public Class<?> getPreviousVersionClass()
  {
    return null;
  }

  @Override
  public void upgradeTranslation(Config oldVersion)
  {
  }
}

