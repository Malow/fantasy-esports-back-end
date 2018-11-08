package com.github.malow.FantasyEsports.services;

import org.springframework.http.HttpStatus;

import com.github.malow.malowlib.GsonSingleton;

public class HttpResponseException extends Exception
{
  private HttpStatus httpStatus;
  private ErrorCode errorCode;
  private String errorMessage;

  public HttpResponseException(HttpStatus httpStatus, ErrorCode errorCode, String errorMessage)
  {
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  public HttpStatus getHttpStatus()
  {
    return this.httpStatus;
  }

  public ErrorCode getErrorCode()
  {
    return this.errorCode;
  }

  public String getErrorMessage()
  {
    return this.errorMessage;
  }

  public static class HttpResponseExceptionJsonFormat
  {
    public int httpStatus;
    public String errorCode;
    public String errorMessage;

    public HttpResponseExceptionJsonFormat(HttpResponseException e)
    {
      this.httpStatus = e.getHttpStatus().value();
      this.errorCode = e.getErrorCode().toString();
      this.errorMessage = e.getErrorMessage();
    }
  }

  public String getJsonData()
  {
    return GsonSingleton.toJson(new HttpResponseExceptionJsonFormat(this));
  }

  public static class BadJsonRequestException extends HttpResponseException
  {
    public BadJsonRequestException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.BADLY_FORMED_JSON, "Badly formed json");
    }
  }

  public static class MissingMandatoryFieldException extends HttpResponseException
  {
    public MissingMandatoryFieldException(String missingField)
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_MANDATORY_FIELD, "Missing mandatory field: " + missingField);
    }
  }

  public static class UnauthorizedException extends HttpResponseException
  {
    public UnauthorizedException()
    {
      super(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED, "You do not have permission to do that");
    }
  }
}
