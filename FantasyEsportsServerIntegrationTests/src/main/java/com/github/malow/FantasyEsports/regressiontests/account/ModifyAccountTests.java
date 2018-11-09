package com.github.malow.FantasyEsports.regressiontests.account;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.account.requests.LoginRequest;
import com.github.malow.FantasyEsports.services.account.requests.ModifyAccountRequest;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.DisplayNameTakenException;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.EmailTakenException;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.WrongPasswordException;
import com.github.malow.FantasyEsports.services.account.responses.GetOwnAccountResponse;
import com.github.malow.malowlib.GsonSingleton;
import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.HttpResponse;

public class ModifyAccountTests extends FantasyEsportsTestFixture
{
  @Test
  public void testSuccessful() throws Exception
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
  public void testMandatoryParameters() throws Exception
  {
    ModifyAccountRequest request = new ModifyAccountRequest(null, "newPass", "newEmail@email.com", "newDisplayName");
    HttpResponse<String> response = this.makePatchRequest("/account", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));
    assertThat(response.getStatus()).isEqualTo(400);
    assertThat(response.getBody().toString()).contains("Missing mandatory field: currentPassword");
  }

  @Test
  public void testUpdateOnlyDisplayName() throws Exception
  {
    ModifyAccountRequest request = new ModifyAccountRequest(PRE_REGISTERED_USER1.password, null, null, "newDisplayName");

    HttpResponse<String> response = this.makePatchRequest("/account", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getBody().toString()).isEqualTo("");
    GetOwnAccountResponse getResponse = GsonSingleton.fromJson(
        this.makeGetRequest("/account", ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey)).getBody().toString(),
        GetOwnAccountResponse.class);
    assertThat(getResponse.displayName).isEqualTo("newDisplayName");
    assertThat(getResponse.email).isEqualTo(PRE_REGISTERED_USER1.email);
    LoginRequest loginRequest = new LoginRequest(PRE_REGISTERED_USER1.email, PRE_REGISTERED_USER1.password);
    HttpResponse<String> loginResponse = this.makePostRequest("/account/login", loginRequest);
    assertThat(loginResponse.getStatus()).isEqualTo(200);
    assertThat(loginResponse.getBody().toString()).containsPattern("\\{\"sessionKey\":\"([0-9a-f-]+)\"\\}");
  }

  @Test
  public void testWrongPassword() throws Exception
  {
    ModifyAccountRequest request = new ModifyAccountRequest("badPass", "newPass", "newEmail@email.com", "newDisplayName");

    HttpResponse<String> response = this.makePatchRequest("/account", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new WrongPasswordException());
  }

  @Test
  public void testEmailTaken() throws Exception
  {
    ModifyAccountRequest request = new ModifyAccountRequest(PRE_REGISTERED_USER1.password, "newPass", PRE_REGISTERED_USER2.email, "newDisplayName");

    HttpResponse<String> response = this.makePatchRequest("/account", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new EmailTakenException());
  }

  @Test
  public void testDisplayNameTaken() throws Exception
  {
    ModifyAccountRequest request = new ModifyAccountRequest(PRE_REGISTERED_USER1.password, "newPass", "newEmail@email.com",
        PRE_REGISTERED_USER2.displayName);

    HttpResponse<String> response = this.makePatchRequest("/account", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new DisplayNameTakenException());
  }
}
