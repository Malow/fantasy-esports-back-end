package com.github.malow.FantasyEsports.services.league.responses;

import org.springframework.http.HttpStatus;

import com.github.malow.FantasyEsports.services.ErrorCode;
import com.github.malow.FantasyEsports.services.HttpResponseException;

public class LeagueExceptions
{
  public static class CreateNameTakenException extends HttpResponseException
  {
	private static final long serialVersionUID = 4636121792292443012L;

	public CreateNameTakenException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.LEAGUE_NAME_EXISTS, "Name is already taken");
    }
  }

  public static class NoLeagueFoundException extends HttpResponseException
  {
	private static final long serialVersionUID = 6285286746483259058L;

	public NoLeagueFoundException()
    {
      super(HttpStatus.NOT_FOUND, ErrorCode.LEAGUE_DOES_NOT_EXIST, "No league found");
    }
  }

  public static class UserIsAlreadyMemberInLeagueException extends HttpResponseException
  {
	private static final long serialVersionUID = -9037279303944845371L;

	public UserIsAlreadyMemberInLeagueException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.USER_ALREADY_MEMBER_IN_LEAGUE, "That user is already a member in this league");
    }
  }

  public static class UserIsAlreadyInvitedToLeagueException extends HttpResponseException
  {
	private static final long serialVersionUID = 6741087373024959870L;

	public UserIsAlreadyInvitedToLeagueException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.USER_ALREADY_INVITED_TO_LEAGUE, "That user is already invited to this league");
    }
  }
}
