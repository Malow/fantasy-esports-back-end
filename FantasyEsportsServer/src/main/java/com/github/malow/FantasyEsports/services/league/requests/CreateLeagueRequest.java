package com.github.malow.FantasyEsports.services.league.requests;

import java.time.ZonedDateTime;

import com.github.malow.FantasyEsports.services.Request;

public class CreateLeagueRequest extends Request
{
  @Mandatory
  public String name;
  @Mandatory
  public ZonedDateTime startDate;
  @Mandatory
  public ZonedDateTime endDate;

  public CreateLeagueRequest(String name, ZonedDateTime startDate, ZonedDateTime endDate)
  {
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
  }
}
