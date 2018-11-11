package com.github.malow.FantasyEsports.services.account;

import org.springframework.data.annotation.Id;

public class Account
{
  @Id
  private String id;

  private String email;
  private String displayName;
  private String password;
  private String sessionKey;

  public Account(String email, String displayName, String password)
  {
    this.email = email;
    this.displayName = displayName;
    this.password = password;
  }

  public String getId()
  {
    return this.id;
  }

  public String getEmail()
  {
    return this.email;
  }

  public String getDisplayName()
  {
    return this.displayName;
  }

  public String getPassword()
  {
    return this.password;
  }

  public void setSessionKey(String sessionKey)
  {
    this.sessionKey = sessionKey;
  }

  public String getSessionKey()
  {
    return this.sessionKey;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public void setDisplayName(String displayName)
  {
    this.displayName = displayName;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }
}
