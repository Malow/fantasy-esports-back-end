package com.github.malow.FantasyEsports.services.manager.responses;

import org.springframework.http.HttpStatus;

import com.github.malow.FantasyEsports.services.ErrorCode;
import com.github.malow.FantasyEsports.services.HttpResponseException;

public class ManagerExceptions
{
  public static class NoManagerFoundException extends HttpResponseException
  {
    public NoManagerFoundException()
    {
      super(HttpStatus.NOT_FOUND, ErrorCode.LEAGUE_DOES_NOT_EXIST, "No league found");
    }
  }
}
