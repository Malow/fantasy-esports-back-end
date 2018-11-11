package com.github.malow.FantasyEsports.services.account.responses;

import com.github.malow.FantasyEsports.services.account.Account;

public class GetAccountResponse
{
  public String email;
  public String displayName;

  public GetAccountResponse(Account account)
  {
    this.email = account.getEmail();
    this.displayName = account.getDisplayName();
  }
}
