package com.github.malow.FantasyEsports.regressiontests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.github.malow.FantasyEsports.ConvenienceMethods;
import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.HttpResponseException.UnauthorizedException;
import com.github.malow.FantasyEsports.services.account.requests.LoginRequest;
import com.github.malow.FantasyEsports.services.account.requests.ModifyAccountRequest;
import com.github.malow.FantasyEsports.services.account.requests.RegisterRequest;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.DisplayNameTakenException;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.EmailNotRegisteredException;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.EmailTakenException;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.WrongPasswordException;
import com.github.malow.FantasyEsports.services.account.responses.GetOwnAccountResponse;
import com.github.malow.malowlib.GsonSingleton;
import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.HttpResponse;

public class AccountTests extends FantasyEsportsTestFixture
{
  @Test
  public void testRegisterSuccessfully() throws Exception
  {
    RegisterRequest request = new RegisterRequest("tester123@test.com", "tester123", "tester123pw");

    HttpResponse<String> response = this.makePostRequest("/account/register", request);

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getBody().toString()).containsPattern("\\{\"sessionKey\":\"([0-9a-f-]+)\"\\}");
  }

  @Test
  public void testRegisterEmailInUse() throws Exception
  {
    ConvenienceMethods.register(new TestUser("tester123@test.com", "tester123", "tester123pw", null));
    RegisterRequest request = new RegisterRequest("tester123@test.com", "tester123", "tester123pw");

    HttpResponse<String> response = this.makePostRequest("/account/register", request);

    this.assertThatResponseEqualsException(response, new EmailTakenException());
  }

  @Test
  public void testRegisterDisplayNameInUse() throws Exception
  {
    ConvenienceMethods.register(new TestUser("tester123@test.com", "tester123", "tester123pw", null));
    RegisterRequest request = new RegisterRequest("tester124@test.com", "tester123", "tester123pw");

    HttpResponse<String> response = this.makePostRequest("/account/register", request);

    this.assertThatResponseEqualsException(response, new DisplayNameTakenException());
  }

  @Test
  public void testLoginSuccessfully() throws Exception
  {
    LoginRequest request = new LoginRequest(PRE_REGISTERED_USER1.email, PRE_REGISTERED_USER1.password);

    HttpResponse<String> response = this.makePostRequest("/account/login", request);

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getBody().toString()).containsPattern("\\{\"sessionKey\":\"([0-9a-f-]+)\"\\}");
  }

  @Test
  public void testLoginBadPassword() throws Exception
  {
    TestUser user = PRE_REGISTERED_USER1;
    user.password = "otherPassword";
    LoginRequest request = new LoginRequest(user.email, user.password);

    HttpResponse<String> response = this.makePostRequest("/account/login", request);

    this.assertThatResponseEqualsException(response, new WrongPasswordException());
  }

  @Test
  public void testLoginNotRegistered() throws Exception
  {
    TestUser user = PRE_REGISTERED_USER1;
    user.email = "Not@registered.email";
    LoginRequest request = new LoginRequest(user.email, user.password);

    HttpResponse<String> response = this.makePostRequest("/account/login", request);

    this.assertThatResponseEqualsException(response, new EmailNotRegisteredException());
  }

  @Test
  public void testGetOwnAccountSuccessfully() throws Exception
  {
    HttpResponse<String> response = this.makeGetRequest("/account", ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    assertThat(response.getStatus()).isEqualTo(200);
    GetOwnAccountResponse responseObject = GsonSingleton.fromJson(response.getBody().toString(), GetOwnAccountResponse.class);
    assertThat(responseObject.displayName).isEqualTo(PRE_REGISTERED_USER1.displayName);
    assertThat(responseObject.email).isEqualTo(PRE_REGISTERED_USER1.email);
  }

  @Test
  public void testGetOwnAccountWithBadSessionKey() throws Exception
  {
    HttpResponse<String> response = this.makeGetRequest("/account", ImmutableMap.of("Session-Key", "badSessionKey"));

    this.assertThatResponseEqualsException(response, new UnauthorizedException());
  }

  @Test
  public void testModifyAccountSuccessfully() throws Exception
  {
    ModifyAccountRequest request = new ModifyAccountRequest(PRE_REGISTERED_USER1.password, "newPass", "newEmail@email.com", "newDisplayName");

    HttpResponse<String> response = this.makePatchRequest("/account", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getBody().toString()).isEqualTo("");
    GetOwnAccountResponse getResponse = GsonSingleton.fromJson(
        this.makeGetRequest("/account", ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey)).getBody().toString(),
        GetOwnAccountResponse.class);
    assertThat(getResponse.displayName).isEqualTo("newDisplayName");
    assertThat(getResponse.email).isEqualTo("newEmail@email.com");
    LoginRequest loginRequest = new LoginRequest("newEmail@email.com", "newPass");
    HttpResponse<String> loginResponse = this.makePostRequest("/account/login", loginRequest);
    assertThat(loginResponse.getStatus()).isEqualTo(200);
    assertThat(loginResponse.getBody().toString()).containsPattern("\\{\"sessionKey\":\"([0-9a-f-]+)\"\\}");
  }

  @Test
  public void testModifyAccountWrongPassword() throws Exception
  {
    ModifyAccountRequest request = new ModifyAccountRequest("badPass", "newPass", "newEmail@email.com", "newDisplayName");

    HttpResponse<String> response = this.makePatchRequest("/account", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new WrongPasswordException());
  }

  @Test
  public void testModifyAccountEmailTaken() throws Exception
  {
    ModifyAccountRequest request = new ModifyAccountRequest(PRE_REGISTERED_USER1.password, "newPass", PRE_REGISTERED_USER2.email, "newDisplayName");

    HttpResponse<String> response = this.makePatchRequest("/account", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new EmailTakenException());
  }

  @Test
  public void testModifyAccountDisplayNameTaken() throws Exception
  {
    ModifyAccountRequest request = new ModifyAccountRequest(PRE_REGISTERED_USER1.password, "newPass", "newEmail@email.com",
        PRE_REGISTERED_USER2.displayName);

    HttpResponse<String> response = this.makePatchRequest("/account", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new DisplayNameTakenException());
  }
}