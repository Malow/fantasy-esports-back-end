package com.github.malow.FantasyEsports.services.league;

import java.time.ZonedDateTime;

import org.springframework.data.annotation.Id;

public class League
{
  @Id
  private String id;

  private String name;
  private String ownerDisplayName;
  public ZonedDateTime startDate;
  public ZonedDateTime endDate;

  public League(String name, String ownerDisplayName, ZonedDateTime startDate, ZonedDateTime endDate)
  {
    this.name = name;
    this.ownerDisplayName = ownerDisplayName;
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

  public String getOwnerDisplayName()
  {
    return this.ownerDisplayName;
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
