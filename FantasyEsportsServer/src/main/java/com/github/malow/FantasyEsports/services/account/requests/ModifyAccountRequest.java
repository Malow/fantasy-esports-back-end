package com.github.malow.FantasyEsports.services.account.requests;

import com.github.malow.FantasyEsports.services.Request;

public class ModifyAccountRequest extends Request
{
  @Mandatory
  public String currentPassword;
  public String password;
  public String email;
  public String displayName;

  public ModifyAccountRequest(String currentPassword, String password, String email, String displayName)
  {
    this.currentPassword = currentPassword;
    this.password = password;
    this.email = email;
    this.displayName = displayName;
  }
}
