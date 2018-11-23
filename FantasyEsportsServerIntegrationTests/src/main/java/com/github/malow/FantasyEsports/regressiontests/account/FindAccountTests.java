package com.github.malow.FantasyEsports.regressiontests.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;

import org.junit.Test;

import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.HttpResponseException.MissingMandatoryFieldException;
import com.github.malow.FantasyEsports.services.account.responses.FindAccountResponse;
import com.github.malow.FantasyEsports.services.account.responses.ResponseAccount;
import com.github.malow.malowlib.GsonSingleton;
import com.mashape.unirest.http.HttpResponse;

public class FindAccountTests extends FantasyEsportsTestFixture
{
  TestUser user1 = new TestUser("email1@email.com", "FirstGuy", "pw");
  TestUser user2 = new TestUser("email2@email.com", "SecondGuy", "pw");

  @Test
  public void testSingleSuccessful() throws Exception
  {
    this.user1.accountId = register(this.user1).accountId;
    register(this.user2);

    HttpResponse<String> response = makeGetRequest("/account/find?displayName=FirstGuy");

    assertThat(response.getStatus()).isEqualTo(200);
    FindAccountResponse responseObject = GsonSingleton.fromJson(response.getBody().toString(), FindAccountResponse.class);
    assertThat(responseObject.accounts).hasSize(1);
    ResponseAccount account = responseObject.accounts.get(0);
    assertThat(account.email).isEqualTo(this.user1.email);
    assertThat(account.displayName).isEqualTo(this.user1.displayName);
    assertThat(account.accountId).isEqualTo(this.user1.accountId);
  }

  @Test
  public void testMultipleSuccessful() throws Exception
  {
    register(this.user1);
    register(this.user2);

    HttpResponse<String> response = makeGetRequest("/account/find?displayName=Guy");

    assertThat(response.getStatus()).isEqualTo(200);
    FindAccountResponse responseObject = GsonSingleton.fromJson(response.getBody().toString(), FindAccountResponse.class);
    assertThat(responseObject.accounts).hasSize(2);
    assertThat(responseObject.accounts.stream().map(a -> a.displayName).collect(Collectors.toList()))
        .containsExactlyInAnyOrder(this.user1.displayName, this.user2.displayName);
  }

  @Test
  public void testNoneSuccessful() throws Exception
  {
    register(this.user1);
    register(this.user2);

    HttpResponse<String> response = makeGetRequest("/account/find?displayName=DisplayNameThatIsNotFound");

    assertThat(response.getStatus()).isEqualTo(200);
    FindAccountResponse responseObject = GsonSingleton.fromJson(response.getBody().toString(), FindAccountResponse.class);
    assertThat(responseObject.accounts).hasSize(0);
  }

  @Test
  public void testWithoutParameter() throws Exception
  {
    register(this.user1);
    register(this.user2);

    HttpResponse<String> response = makeGetRequest("/account/find");

    this.assertThatResponseEqualsException(response, new MissingMandatoryFieldException("displayName"));

    response = makeGetRequest("/account/find?displayName=");

    this.assertThatResponseEqualsException(response, new MissingMandatoryFieldException("displayName"));
  }
}
