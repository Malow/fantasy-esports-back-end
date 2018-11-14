package com.github.malow.FantasyEsports.regressiontests.account;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.HttpResponseException.MissingMandatoryFieldException;
import com.github.malow.FantasyEsports.services.account.requests.LoginRequest;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.EmailNotRegisteredException;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.WrongPasswordException;
import com.github.malow.FantasyEsports.services.account.responses.LoginResponse;
import com.github.malow.malowlib.GsonSingleton;
import com.mashape.unirest.http.HttpResponse;

public class LoginTests extends FantasyEsportsTestFixture
{

  @Test
  public void testSuccessful() throws Exception
  {
    LoginRequest request = new LoginRequest(PRE_REGISTERED_USER1.email, PRE_REGISTERED_USER1.password);

    HttpResponse<String> httpResponse = this.makePostRequest("/account/login", request);

    assertThat(httpResponse.getStatus()).isEqualTo(200);
    LoginResponse response = GsonSingleton.fromJson(httpResponse.getBody().toString(), LoginResponse.class);
    assertThat(response.sessionKey).matches("[0-9a-f-]+");
    assertThat(response.accountId).matches("[0-9a-f-]+");
  }

  @Test
  public void testMandatoryParameters() throws Exception
  {
    LoginRequest request = new LoginRequest(null, PRE_REGISTERED_USER1.password);
    HttpResponse<String> response = this.makePostRequest("/account/login", request);
    this.assertThatResponseEqualsException(response, new MissingMandatoryFieldException("email"));

    request = new LoginRequest(PRE_REGISTERED_USER1.email, null);
    response = this.makePostRequest("/account/login", request);
    this.assertThatResponseEqualsException(response, new MissingMandatoryFieldException("password"));
  }

  @Test
  public void testBadPassword() throws Exception
  {
    TestUser user = PRE_REGISTERED_USER1;
    user.password = "otherPassword";
    LoginRequest request = new LoginRequest(user.email, user.password);

    HttpResponse<String> response = this.makePostRequest("/account/login", request);

    this.assertThatResponseEqualsException(response, new WrongPasswordException());
  }

  @Test
  public void testNotRegistered() throws Exception
  {
    TestUser user = PRE_REGISTERED_USER1;
    user.email = "Not@registered.email";
    LoginRequest request = new LoginRequest(user.email, user.password);

    HttpResponse<String> response = this.makePostRequest("/account/login", request);

    this.assertThatResponseEqualsException(response, new EmailNotRegisteredException());
  }

  @Test
  public void testThatIfYouLoginTwiceYouGetTheSameSessionKey() throws Exception
  {
    LoginRequest request = new LoginRequest(PRE_REGISTERED_USER1.email, PRE_REGISTERED_USER1.password);

    HttpResponse<String> httpResponse = this.makePostRequest("/account/login", request);
    LoginResponse response = GsonSingleton.fromJson(httpResponse.getBody().toString(), LoginResponse.class);
    assertThat(response.sessionKey).isEqualTo(PRE_REGISTERED_USER1.sessionKey);

    httpResponse = this.makePostRequest("/account/login", request);
    response = GsonSingleton.fromJson(httpResponse.getBody().toString(), LoginResponse.class);
    assertThat(response.sessionKey).isEqualTo(PRE_REGISTERED_USER1.sessionKey);
  }
}