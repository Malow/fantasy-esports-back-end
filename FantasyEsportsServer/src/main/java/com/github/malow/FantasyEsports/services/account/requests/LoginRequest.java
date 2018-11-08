package com.github.malow.FantasyEsports.services.account.requests;

import com.github.malow.FantasyEsports.services.Request;

public class LoginRequest extends Request
{
  @Mandatory
  public String email;
  @Mandatory
  public String password;

  public LoginRequest(String email, String password)
  {
    this.email = email;
    this.password = password;
  }
}
