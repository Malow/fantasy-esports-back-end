package com.github.malow.FantasyEsports.services.account.responses;

import org.springframework.http.HttpStatus;

import com.github.malow.FantasyEsports.services.ErrorCode;
import com.github.malow.FantasyEsports.services.HttpResponseException;

public class AccountExceptions
{
  public static class EmailTakenException extends HttpResponseException
  {
    public EmailTakenException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.EMAIL_EXISTS, "Email is already taken");
    }
  }

  public static class DisplayNameTakenException extends HttpResponseException
  {
    public DisplayNameTakenException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.DISPLAY_NAME_EXISTS, "DisplayName is already taken");
    }
  }

  public static class EmailNotRegisteredException extends HttpResponseException
  {
    public EmailNotRegisteredException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.USER_DOES_NOT_EXIST, "No account for that email exists");
    }
  }

  public static class WrongPasswordException extends HttpResponseException
  {
    public WrongPasswordException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.PASSWORD_INCORRECT, "Wrong password");
    }
  }
}
