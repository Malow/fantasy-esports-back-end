package com.github.malow.FantasyEsports;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.slf4j.LoggerFactory;

import com.github.malow.FantasyEsports.services.HttpResponseException;
import com.github.malow.FantasyEsports.services.Request;
import com.github.malow.FantasyEsports.services.account.responses.LoginResponse;
import com.github.malow.malowlib.GsonSingleton;
import com.github.malow.malowlib.MaloWLogger;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class FantasyEsportsTestFixture
{
  public static class TestUser
  {
    public String email;
    public String displayName;
    public String password;
    public String sessionKey;
    public String accountId;

    public TestUser(String email, String displayName, String password)
    {
      this.email = email;
      this.displayName = displayName;
      this.password = password;
    }
  }

  public static final TestUser PRE_REGISTERED_USER1 = new TestUser("tester1@test.com", "tester1", "testerpw");
  public static final TestUser PRE_REGISTERED_USER2 = new TestUser("tester2@test.com", "tester2", "testerpw");
  public static final TestUser PRE_REGISTERED_USER3 = new TestUser("tester3@test.com", "tester3", "testerpw");

  @Before
  public void beforeTest() throws Exception
  {
    this.resetDatabase();
    this.preRegisterAccounts();
  }

  static
  {
    ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);
    ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.apache.http").setLevel(Level.ERROR);

    try
    {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, new TrustManager[] { new X509TrustManager()
      {
        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
          return new X509Certificate[0];
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType)
        {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType)
        {
        }
      } }, new SecureRandom());
      Unirest.setHttpClient(HttpClients.custom().setSSLContext(sc).setSSLHostnameVerifier(new HostnameVerifier()
      {
        @Override
        public boolean verify(String hostname, SSLSession session)
        {
          return true;
        }
      }).build());
    }
    catch (Exception e)
    {
      MaloWLogger.error("Error intializing Unirest https accept all", e);
    }
  }

  private static final String MONGODB_URL = "mongodb+srv://admin:asdf@cluster0-u4tzo.mongodb.net/?retryWrites=true";
  public static MongoClient mongo = new MongoClient(new MongoClientURI(MONGODB_URL));
  public static MongoDatabase database = mongo.getDatabase("FantasyEsportsDevelop");

  private void resetDatabase() throws Exception
  {
    database.getCollection("account").drop();
    database.getCollection("league").drop();
    database.getCollection("manager").drop();
  }

  private void preRegisterAccounts() throws Exception
  {
    LoginResponse response = ConvenienceMethods.register(PRE_REGISTERED_USER1);
    PRE_REGISTERED_USER1.sessionKey = response.sessionKey;
    PRE_REGISTERED_USER1.accountId = response.accountId;
    response = ConvenienceMethods.register(PRE_REGISTERED_USER2);
    PRE_REGISTERED_USER2.sessionKey = response.sessionKey;
    PRE_REGISTERED_USER2.accountId = response.accountId;
    response = ConvenienceMethods.register(PRE_REGISTERED_USER3);
    PRE_REGISTERED_USER3.sessionKey = response.sessionKey;
    PRE_REGISTERED_USER3.accountId = response.accountId;
  }

  protected HttpResponse<String> makePatchRequest(String subPath, Request request) throws UnirestException
  {
    return this.makePostRequest(subPath, request, null);
  }

  protected HttpResponse<String> makePatchRequest(String subPath, Request request, Map<String, String> headers) throws UnirestException
  {
    return Unirest.patch(Config.HOST + subPath).headers(headers).body(GsonSingleton.toJson(request)).asString();
  }

  protected HttpResponse<String> makePostRequest(String subPath, Request request) throws UnirestException
  {
    return this.makePostRequest(subPath, request, null);
  }

  protected HttpResponse<String> makePostRequest(String subPath, Request request, Map<String, String> headers) throws UnirestException
  {
    return Unirest.post(Config.HOST + subPath).headers(headers).body(GsonSingleton.toJson(request)).asString();
  }

  protected HttpResponse<String> makePostRequest(String subPath, Map<String, String> headers) throws UnirestException
  {
    return Unirest.post(Config.HOST + subPath).headers(headers).asString();
  }

  protected HttpResponse<String> makeGetRequest(String subPath, Map<String, String> headers) throws UnirestException
  {
    return Unirest.get(Config.HOST + subPath).headers(headers).asString();
  }

  protected HttpResponse<String> makeGetRequest(String subPath) throws UnirestException
  {
    return this.makeGetRequest(subPath, null);
  }

  protected void assertThatResponseEqualsException(HttpResponse<String> response, HttpResponseException e)
  {
    assertThat(response.getStatus()).isEqualTo(e.getHttpStatus().value());
    assertThat(response.getBody().toString()).isEqualTo(e.getJsonData());
  }
}
