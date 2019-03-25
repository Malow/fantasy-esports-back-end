package com.github.malow.FantasyEsports.services;

import org.springframework.http.HttpStatus;

import com.github.malow.malowlib.GsonSingleton;

public class HttpResponseException extends Exception
{
  /**
	 * 
	 */
	private static final long serialVersionUID = -1124925468779522861L;
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
	private static final long serialVersionUID = -5483468344860823984L;

	public BadJsonRequestException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.BADLY_FORMED_JSON, "Badly formed json");
    }
  }

  public static class IllegalValueException extends HttpResponseException
  {
	private static final long serialVersionUID = -2932315467495447312L;

	public IllegalValueException(String badField)
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.ILLEGAL_VALUE, "Illegal value for field: " + badField);
    }
  }

  public static class MissingMandatoryFieldException extends HttpResponseException
  {
	private static final long serialVersionUID = -5189054125455246913L;

	public MissingMandatoryFieldException(String missingField)
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_MANDATORY_FIELD, "Missing mandatory field: " + missingField);
    }
  }

  public static class UnauthorizedException extends HttpResponseException
  {
	private static final long serialVersionUID = -4076938813445623845L;

	public UnauthorizedException()
    {
      super(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED, "You're missing authorization");
    }
  }

  public static class ForbiddenException extends HttpResponseException
  {
	private static final long serialVersionUID = -5762294637834792418L;

	public ForbiddenException()
    {
      super(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, "You do not have permission to do that");
    }
  }

  public static class NoChangeMadeException extends HttpResponseException
  {
	private static final long serialVersionUID = 17549103586046369L;

	public NoChangeMadeException()
    {
      super(HttpStatus.BAD_REQUEST, ErrorCode.NO_CHANGE_MADE, "No change was made");
    }
  }
}
