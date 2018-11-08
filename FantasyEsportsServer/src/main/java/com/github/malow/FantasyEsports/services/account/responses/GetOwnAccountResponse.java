package com.github.malow.FantasyEsports.services.account.responses;

import com.github.malow.FantasyEsports.services.account.Account;

public class GetOwnAccountResponse
{
  public String email;
  public String displayName;

  public GetOwnAccountResponse(Account account)
  {
    this.email = account.getEmail();
    this.displayName = account.getDisplayName();
  }
}
