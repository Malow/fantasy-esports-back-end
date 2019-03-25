package com.github.malow.FantasyEsports.services.manager.responses;

import org.springframework.http.HttpStatus;

import com.github.malow.FantasyEsports.services.ErrorCode;
import com.github.malow.FantasyEsports.services.HttpResponseException;

public class ManagerExceptions
{
  public static class NoManagerFoundException extends HttpResponseException
  {
	private static final long serialVersionUID = -6466311795373630862L;

	public NoManagerFoundException()
    {
      super(HttpStatus.NOT_FOUND, ErrorCode.MANAGER_DOES_NOT_EXIST, "No manager found");
    }
  }
}
