package com.github.malow.FantasyEsports.services.account.responses;

import com.github.malow.FantasyEsports.services.league.LeagueRole;
import com.github.malow.FantasyEsports.services.manager.Manager;

public class ResponseManager
{
  public String displayName;
  public LeagueRole leagueRole;
  public String id;
  public String accountId;
  public String leagueId;
  public int score;

  public ResponseManager(Manager manager, String displayName)
  {
    this.leagueRole = manager.getLeagueRole();
    this.id = manager.getId();
    this.accountId = manager.getAccountId();
    this.leagueId = manager.getLeagueId();
    this.score = manager.getScore();
    this.displayName = displayName;
  }
}
