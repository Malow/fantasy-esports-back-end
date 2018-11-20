package com.github.malow.FantasyEsports.services.account.responses;

import java.time.ZonedDateTime;
import java.util.List;

import com.github.malow.FantasyEsports.services.league.League;

public class ResponseLeague
{
  public String id;
  public String name;
  public ZonedDateTime startDate;
  public ZonedDateTime endDate;
  public List<ResponseManager> managers;

  public ResponseLeague(League league, List<ResponseManager> managers)
  {
    this.id = league.getId();
    this.name = league.getName();
    this.startDate = league.getStartDate();
    this.endDate = league.getEndDate();
    this.managers = managers;
  }
}
