package com.github.malow.FantasyEsports.services.account.requests;

import com.github.malow.FantasyEsports.services.Request;

public class RegisterRequest extends Request
{
  @Mandatory
  public String email;
  @Mandatory
  public String displayName;
  @Mandatory
  public String password;

  public RegisterRequest(String email, String displayName, String password)
  {
    this.email = email;
    this.displayName = displayName;
    this.password = password;
  }
}
