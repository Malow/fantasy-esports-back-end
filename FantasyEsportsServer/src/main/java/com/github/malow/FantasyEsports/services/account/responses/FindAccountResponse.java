package com.github.malow.FantasyEsports.services.account.responses;

import java.util.List;
import java.util.stream.Collectors;

import com.github.malow.FantasyEsports.services.account.Account;

public class FindAccountResponse
{
  public List<ResponseAccount> accounts;

  public FindAccountResponse(List<Account> accounts)
  {
    this.accounts = accounts.stream().map(ResponseAccount::new).collect(Collectors.toList());
  }
}
