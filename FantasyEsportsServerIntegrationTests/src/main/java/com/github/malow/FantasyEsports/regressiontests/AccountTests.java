package com.github.malow.FantasyEsports.regressiontests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.github.malow.FantasyEsports.Config;
import com.github.malow.FantasyEsports.ConvenienceMethods;
import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.account.requests.LoginRequest;
import com.github.malow.FantasyEsports.services.account.requests.RegisterRequest;
import com.github.malow.malowlib.GsonSingleton;
import com.mashape.unirest.http.Unirest;

public class AccountTests extends FantasyEsportsTestFixture
{
  @Test
  public void testRegisterSuccessfully() throws Exception
  {
    String responseBody = Unirest.post(Config.HOST + "/account/register")
        .body(GsonSingleton.toJson(new RegisterRequest("tester123@test.com", "tester123", "tester123pw"))).asJson().getBody()
        .toString();

    assertThat(responseBody).containsPattern("\\{\"sessionKey\":\"([0-9a-f-]+)\"\\}");
  }

  @Test
  public void testRegisterEmailInUse() throws Exception
  {
    ConvenienceMethods.register(new TestUser("tester123@test.com", "tester123", "tester123pw", null));

    String responseBody = Unirest.post(Config.HOST + "/account/register")
        .body(GsonSingleton.toJson(new RegisterRequest("tester123@test.com", "tester123", "tester123pw"))).asJson().getBody()
        .toString();

    assertThat(responseBody).contains("Email is already taken");
    assertThat(responseBody).contains("\"status\":400");
  }

  @Test
  public void testRegisterDisplayNameInUse() throws Exception
  {
    ConvenienceMethods.register(new TestUser("tester123@test.com", "tester123", "tester123pw", null));

    String responseBody = Unirest.post(Config.HOST + "/account/register")
        .body(GsonSingleton.toJson(new RegisterRequest("tester124@test.com", "tester123", "tester123pw"))).asJson().getBody()
        .toString();

    assertThat(responseBody).contains("DisplayName is already taken");
    assertThat(responseBody).contains("\"status\":400");
  }

  @Test
  public void testLoginSuccessfully() throws Exception
  {
    String responseBody = Unirest.post(Config.HOST + "/account/login")
        .body(GsonSingleton.toJson(new LoginRequest(PRE_REGISTERED_USER1.email, PRE_REGISTERED_USER1.password))).asJson().getBody()
        .toString();

    assertThat(responseBody).containsPattern("\\{\"sessionKey\":\"([0-9a-f-]+)\"\\}");
  }

  @Test
  public void testLoginBadPassword() throws Exception
  {
    TestUser user = PRE_REGISTERED_USER1;
    user.password = "otherPassword";

    String responseBody = Unirest.post(Config.HOST + "/account/login")
        .body(GsonSingleton.toJson(new LoginRequest(user.email, user.password))).asJson().getBody()
        .toString();

    assertThat(responseBody).contains("Wrong password");
    assertThat(responseBody).contains("\"status\":400");
  }

  @Test
  public void testLoginNotRegistered() throws Exception
  {
    TestUser user = PRE_REGISTERED_USER1;
    user.email = "Not@registered.email";

    String responseBody = Unirest.post(Config.HOST + "/account/login")
        .body(GsonSingleton.toJson(new LoginRequest(user.email, user.password))).asJson().getBody()
        .toString();

    assertThat(responseBody).contains("No account for that email exists");
    assertThat(responseBody).contains("\"status\":400");
  }
}