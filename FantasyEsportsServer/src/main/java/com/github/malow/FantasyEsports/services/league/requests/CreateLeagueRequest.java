package com.github.malow.FantasyEsports.services.league.requests;

import java.time.ZonedDateTime;

import com.github.malow.malowlib.network.https.HttpRequest;

public class CreateLeagueRequest implements HttpRequest
{
  public String name;
  public ZonedDateTime startDate;
  public ZonedDateTime endDate;

  public CreateLeagueRequest(String name, ZonedDateTime startDate, ZonedDateTime endDate)
  {
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  @Override
  public boolean isValid()
  {
    if (this.name == null || this.name.isEmpty())
    {
      return false;
    }

    if (this.startDate == null)
    {
      return false;
    }

    if (this.endDate == null)
    {
      return false;
    }

    return true;
  }
}
