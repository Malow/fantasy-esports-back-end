package com.github.malow.FantasyEsports.services.account.responses;

import org.springframework.http.HttpStatus;

import com.github.malow.FantasyEsports.services.ErrorCode;
import com.github.malow.FantasyEsports.services.HttpResponseException;

public class AccountExceptions
{
  public static class EmailTakenException extends HttpResponseException
  {
	private static final long serialVersionUID = -1890528360278830763L;

	public EmailTakenException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.EMAIL_EXISTS, "Email is already taken");
    }
  }

  public static class DisplayNameTakenException extends HttpResponseException
  {
	private static final long serialVersionUID = -5165348242848222817L;

	public DisplayNameTakenException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.DISPLAY_NAME_EXISTS, "DisplayName is already taken");
    }
  }

  public static class EmailNotRegisteredException extends HttpResponseException
  {
	private static final long serialVersionUID = 4393907716743599756L;

	public EmailNotRegisteredException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.USER_DOES_NOT_EXIST, "No account for that email exists");
    }
  }

  public static class WrongPasswordException extends HttpResponseException
  {
	private static final long serialVersionUID = 6703087196038731174L;

	public WrongPasswordException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.PASSWORD_INCORRECT, "Wrong password");
    }
  }

  public static class AccountNotFoundException extends HttpResponseException
  {
	private static final long serialVersionUID = 5998159687181611336L;

	public AccountNotFoundException()
    {
      super(HttpStatus.NOT_FOUND, ErrorCode.ACCOUNT_NOT_FOUND, "Couldn't find an account with that id");
    }
  }
}
