package com.github.malow.FantasyEsports;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.slf4j.LoggerFactory;

import com.github.malow.malowlib.MaloWLogger;
import com.mashape.unirest.http.Unirest;
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

    public TestUser(String email, String displayName, String password, String sessionKey)
    {
      this.email = email;
      this.displayName = displayName;
      this.password = password;
      this.sessionKey = sessionKey;
    }
  }

  public static final TestUser PRE_REGISTERED_USER1 = new TestUser("tester1@test.com", "tester1", "testerpw", null);
  public static final TestUser PRE_REGISTERED_USER2 = new TestUser("tester2@test.com", "tester2", "testerpw", null);

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

  private static final String MONGODB_URL = "mongodb+srv://admin:asdf@cluster0-u4tzo.mongodb.net/test?retryWrites=true";
  public static MongoClient mongo = new MongoClient(new MongoClientURI(MONGODB_URL));
  public static MongoDatabase database = mongo.getDatabase("FantasyEsports");

  private void resetDatabase() throws Exception
  {
    database.getCollection("account").drop();
    database.getCollection("league").drop();
  }

  private void preRegisterAccounts() throws Exception
  {
    PRE_REGISTERED_USER1.sessionKey = ConvenienceMethods.register(PRE_REGISTERED_USER1);
    PRE_REGISTERED_USER2.sessionKey = ConvenienceMethods.register(PRE_REGISTERED_USER2);
  }
}
