package com.github.malow.FantasyEsports.services.league.responses;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class LeagueExceptions
{
  @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Name is already taken")
  public static class CreateNameTakenException extends RuntimeException
  {
    private static final long serialVersionUID = 4636121792292443012L;
  }

  @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No league found")
  public static class NoLeagueFoundException extends RuntimeException
  {
    private static final long serialVersionUID = 6285286746483259058L;
  }
}
