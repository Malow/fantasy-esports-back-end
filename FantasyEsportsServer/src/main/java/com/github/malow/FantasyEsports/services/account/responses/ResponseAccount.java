package com.github.malow.FantasyEsports.services.account.responses;

import com.github.malow.FantasyEsports.services.account.Account;

public class ResponseAccount
{
  public String accountId;
  public String email;
  public String displayName;

  public ResponseAccount(Account account)
  {
    this.accountId = account.getId();
    this.email = account.getEmail();
    this.displayName = account.getDisplayName();
  }
}
