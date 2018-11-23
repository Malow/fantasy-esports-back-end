package com.github.malow.FantasyEsports;

import java.util.Map;

import com.github.malow.FantasyEsports.services.Request;
import com.github.malow.malowlib.GsonSingleton;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public abstract class HttpClient
{
  protected static HttpResponse<String> makePatchRequest(String subPath, Request request) throws UnirestException
  {
    return makePostRequest(subPath, request, null);
  }

  protected static HttpResponse<String> makePatchRequest(String subPath, Request request, Map<String, String> headers) throws UnirestException
  {
    return Unirest.patch(Config.HOST + subPath).headers(headers).body(GsonSingleton.toJson(request)).asString();
  }

  protected static HttpResponse<String> makePostRequest(String subPath, Request request) throws UnirestException
  {
    return makePostRequest(subPath, request, null);
  }

  protected static HttpResponse<String> makePostRequest(String subPath, Request request, Map<String, String> headers) throws UnirestException
  {
    return Unirest.post(Config.HOST + subPath).headers(headers).body(GsonSingleton.toJson(request)).asString();
  }

  protected static HttpResponse<String> makePostRequest(String subPath, Map<String, String> headers) throws UnirestException
  {
    return Unirest.post(Config.HOST + subPath).headers(headers).asString();
  }

  protected static HttpResponse<String> makeGetRequest(String subPath, Map<String, String> headers) throws UnirestException
  {
    return Unirest.get(Config.HOST + subPath).headers(headers).asString();
  }

  protected static HttpResponse<String> makeGetRequest(String subPath) throws UnirestException
  {
    return makeGetRequest(subPath, null);
  }
}
