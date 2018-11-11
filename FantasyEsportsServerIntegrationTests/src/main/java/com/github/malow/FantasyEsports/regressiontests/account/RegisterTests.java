package com.github.malow.FantasyEsports.regressiontests.account;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.github.malow.FantasyEsports.ConvenienceMethods;
import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.account.requests.RegisterRequest;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.DisplayNameTakenException;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.EmailTakenException;
import com.github.malow.FantasyEsports.services.account.responses.LoginResponse;
import com.github.malow.malowlib.GsonSingleton;
import com.mashape.unirest.http.HttpResponse;

public class RegisterTests extends FantasyEsportsTestFixture
{
  @Test
  public void testSuccessful() throws Exception
  {
    RegisterRequest request = new RegisterRequest("tester123@test.com", "tester123", "tester123pw");

    HttpResponse<String> httpResponse = this.makePostRequest("/account/register", request);

    assertThat(httpResponse.getStatus()).isEqualTo(200);
    LoginResponse response = GsonSingleton.fromJson(httpResponse.getBody().toString(), LoginResponse.class);
    assertThat(response.sessionKey).matches("[0-9a-f-]+");
    assertThat(response.accountId).matches("[0-9a-f-]+");
  }

  @Test
  public void testMandatoryParameters() throws Exception
  {
    RegisterRequest request = new RegisterRequest(null, "tester123", "tester123pw");
    HttpResponse<String> response = this.makePostRequest("/account/register", request);
    assertThat(response.getStatus()).isEqualTo(400);
    assertThat(response.getBody().toString()).contains("Missing mandatory field: email");

    request = new RegisterRequest("tester123@test.com", null, "tester123pw");
    response = this.makePostRequest("/account/register", request);
    assertThat(response.getStatus()).isEqualTo(400);
    assertThat(response.getBody().toString()).contains("Missing mandatory field: displayName");

    request = new RegisterRequest("tester123@test.com", "tester123", null);
    response = this.makePostRequest("/account/register", request);
    assertThat(response.getStatus()).isEqualTo(400);
    assertThat(response.getBody().toString()).contains("Missing mandatory field: password");
  }

  @Test
  public void testEmailInUse() throws Exception
  {
    ConvenienceMethods.register(new TestUser("tester123@test.com", "tester123", "tester123pw"));
    RegisterRequest request = new RegisterRequest("tester123@test.com", "tester123", "tester123pw");

    HttpResponse<String> response = this.makePostRequest("/account/register", request);

    this.assertThatResponseEqualsException(response, new EmailTakenException());
  }

  @Test
  public void testDisplayNameInUse() throws Exception
  {
    ConvenienceMethods.register(new TestUser("tester123@test.com", "tester123", "tester123pw"));
    RegisterRequest request = new RegisterRequest("tester124@test.com", "tester123", "tester123pw");

    HttpResponse<String> response = this.makePostRequest("/account/register", request);

    this.assertThatResponseEqualsException(response, new DisplayNameTakenException());
  }
}
