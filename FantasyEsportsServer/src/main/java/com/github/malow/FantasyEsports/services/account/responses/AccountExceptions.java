package com.github.malow.FantasyEsports.services.account.responses;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AccountExceptions
{
  @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Email is already taken")
  public static class RegisterEmailTakenException extends RuntimeException
  {
    private static final long serialVersionUID = -124610181451124151L;
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "DisplayName is already taken")
  public static class RegisterDisplayNameTakenException extends RuntimeException
  {
    private static final long serialVersionUID = 2692392566907588868L;
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No account for that email exists")
  public static class LoginEmailNotRegisteredException extends RuntimeException
  {
    private static final long serialVersionUID = -1844662937870957018L;
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Wrong password")
  public static class LoginWrongPasswordException extends RuntimeException
  {
    private static final long serialVersionUID = 7446386946216802574L;
  }
}
