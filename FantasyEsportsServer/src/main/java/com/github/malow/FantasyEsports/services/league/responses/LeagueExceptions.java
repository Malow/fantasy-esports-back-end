package com.github.malow.FantasyEsports.services.league.responses;

import org.springframework.http.HttpStatus;

import com.github.malow.FantasyEsports.services.ErrorCode;
import com.github.malow.FantasyEsports.services.HttpResponseException;

public class LeagueExceptions
{
  public static class CreateNameTakenException extends HttpResponseException
  {
    public CreateNameTakenException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.LEAGUE_NAME_EXISTS, "Name is already taken");
    }
  }

  public static class NoLeagueFoundException extends HttpResponseException
  {
    public NoLeagueFoundException()
    {
      super(HttpStatus.NOT_FOUND, ErrorCode.LEAGUE_DOES_NOT_EXIST, "No league found");
    }
  }

  public static class UserIsAlreadyMemberInLeagueException extends HttpResponseException
  {
    public UserIsAlreadyMemberInLeagueException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.USER_ALREADY_MEMBER_IN_LEAGUE, "That user is already a member in this league");
    }
  }
}
