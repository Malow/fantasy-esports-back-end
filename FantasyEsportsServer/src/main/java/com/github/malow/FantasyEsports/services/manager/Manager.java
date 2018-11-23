package com.github.malow.FantasyEsports.services.manager;

import org.springframework.data.annotation.Id;

import com.github.malow.FantasyEsports.services.league.LeagueRole;

public class Manager
{
  @Id
  private String id;

  private String accountId;
  private String leagueId;
  private int score;
  private LeagueRole leagueRole;

  public Manager(String accountId, String leagueId, LeagueRole leagueRole)
  {
    this.accountId = accountId;
    this.leagueId = leagueId;
    this.leagueRole = leagueRole;
    this.score = 0;
  }

  public String getId()
  {
    return this.id;
  }

  public String getAccountId()
  {
    return this.accountId;
  }

  public String getLeagueId()
  {
    return this.leagueId;
  }

  public int getScore()
  {
    return this.score;
  }

  public LeagueRole getLeagueRole()
  {
    return this.leagueRole;
  }

  public void setLeagueRole(LeagueRole leagueRole)
  {
    this.leagueRole = leagueRole;
  }
}
