package com.github.malow.FantasyEsports.services;

import org.springframework.http.ResponseEntity;

import com.github.malow.FantasyEsports.services.HttpResponseException.BadJsonRequestException;
import com.github.malow.FantasyEsports.services.HttpResponseException.MissingMandatoryFieldException;
import com.github.malow.malowlib.GsonSingleton;

public abstract class Controller
{
  protected <T extends Request> T getValidRequest(String body, Class<T> requestClass) throws BadJsonRequestException, MissingMandatoryFieldException
  {
    T request = GsonSingleton.fromJson(body, requestClass);
    if (request == null)
    {
      throw new BadJsonRequestException();
    }
    request.validate();
    return request;
  }

  protected ResponseEntity<String> handleHttpResponseException(HttpResponseException e)
  {
    return ResponseEntity.status(e.getHttpStatus()).body(e.getJsonData());
  }
}
