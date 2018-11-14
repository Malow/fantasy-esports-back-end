package com.github.malow.FantasyEsports.services.account.requests;

import com.github.malow.FantasyEsports.services.Request;

public class FindAccountRequest extends Request
{
  @Mandatory
  public String displayName;

  public String getDisplayName()
  {
    return this.displayName;
  }

  public void setDisplayName(String displayName)
  {
    this.displayName = displayName;
  }
}
