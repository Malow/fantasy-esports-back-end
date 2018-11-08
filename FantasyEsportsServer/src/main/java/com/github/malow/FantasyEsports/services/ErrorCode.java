package com.github.malow.FantasyEsports.services;

public enum ErrorCode
{
  EMAIL_EXISTS("email-exists"),
  DISPLAY_NAME_EXISTS("display-name-exists"),
  PASSWORD_INCORRECT("password-incorrect"),
  USER_DOES_NOT_EXIST("user-does-not-exist"),
  BADLY_FORMED_JSON("badly-formed-json"),
  MISSING_MANDATORY_FIELD("missing-mandatory-field"),
  UNAUTHORIZED("unauthorized"),
  LEAGUE_NAME_EXISTS("league-name-exists"),
  LEAGUE_DOES_NOT_EXIST("league-does-not-exist");

  private final String errorCode;

  private ErrorCode(final String errorCode)
  {
    this.errorCode = errorCode;
  }

  @Override
  public String toString()
  {
    return this.errorCode;
  }
}
