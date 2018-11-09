package com.github.malow.FantasyEsports.regressiontests.account;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.account.requests.LoginRequest;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.EmailNotRegisteredException;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.WrongPasswordException;
import com.mashape.unirest.http.HttpResponse;

public class LoginTests extends FantasyEsportsTestFixture
{

  @Test
  public void testSuccessful() throws Exception
  {
    LoginRequest request = new LoginRequest(PRE_REGISTERED_USER1.email, PRE_REGISTERED_USER1.password);

    HttpResponse<String> response = this.makePostRequest("/account/login", request);

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getBody().toString()).containsPattern("\\{\"sessionKey\":\"([0-9a-f-]+)\"\\}");
  }

  @Test
  public void testMandatoryParameters() throws Exception
  {
    LoginRequest request = new LoginRequest(null, PRE_REGISTERED_USER1.password);
    HttpResponse<String> response = this.makePostRequest("/account/login", request);
    assertThat(response.getStatus()).isEqualTo(400);
    assertThat(response.getBody().toString()).contains("Missing mandatory field: email");

    request = new LoginRequest(PRE_REGISTERED_USER1.email, null);
    response = this.makePostRequest("/account/login", request);
    assertThat(response.getStatus()).isEqualTo(400);
    assertThat(response.getBody().toString()).contains("Missing mandatory field: password");
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
}