package com.github.malow.FantasyEsports.services.league;

import java.time.ZonedDateTime;

import org.springframework.data.annotation.Id;

public class League
{
  @Id
  private String id;

  private String name;
  private ZonedDateTime startDate;
  private ZonedDateTime endDate;

  public League(String name, ZonedDateTime startDate, ZonedDateTime endDate)
  {
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public String getId()
  {
    return this.id;
  }

  public String getName()
  {
    return this.name;
  }

  public ZonedDateTime getStartDate()
  {
    return this.startDate;
  }

  public ZonedDateTime getEndDate()
  {
    return this.endDate;
  }

}
