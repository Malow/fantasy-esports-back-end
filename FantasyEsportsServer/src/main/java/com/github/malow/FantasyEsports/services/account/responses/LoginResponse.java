package com.github.malow.FantasyEsports.services.account.responses;

public class LoginResponse
{
  public String sessionKey;
  public String accountId;

  public LoginResponse(String sessionKey, String accountId)
  {
    this.sessionKey = sessionKey;
    this.accountId = accountId;
  }
}
