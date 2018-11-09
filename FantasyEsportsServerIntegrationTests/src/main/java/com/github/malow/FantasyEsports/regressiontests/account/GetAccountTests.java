package com.github.malow.FantasyEsports.regressiontests.account;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.HttpResponseException.UnauthorizedException;
import com.github.malow.FantasyEsports.services.account.responses.GetOwnAccountResponse;
import com.github.malow.malowlib.GsonSingleton;
import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.HttpResponse;

public class GetAccountTests extends FantasyEsportsTestFixture
{
  @Test
  public void testSuccessful() throws Exception
  {
    HttpResponse<String> response = this.makeGetRequest("/account", ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    assertThat(response.getStatus()).isEqualTo(200);
    GetOwnAccountResponse responseObject = GsonSingleton.fromJson(response.getBody().toString(), GetOwnAccountResponse.class);
    assertThat(responseObject.displayName).isEqualTo(PRE_REGISTERED_USER1.displayName);
    assertThat(responseObject.email).isEqualTo(PRE_REGISTERED_USER1.email);
  }

  @Test
  public void testWithBadSessionKey() throws Exception
  {
    HttpResponse<String> response = this.makeGetRequest("/account", ImmutableMap.of("Session-Key", "badSessionKey"));

    this.assertThatResponseEqualsException(response, new UnauthorizedException());
  }
}
