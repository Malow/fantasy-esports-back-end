package com.github.malow.FantasyEsports.regressiontests.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;

import org.junit.Test;

import com.github.malow.FantasyEsports.ConvenienceMethods;
import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.HttpResponseException.UnauthorizedException;
import com.github.malow.FantasyEsports.services.account.responses.LoginResponse;
import com.github.malow.FantasyEsports.services.league.requests.CreateLeagueRequest;
import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.HttpResponse;

public class LogoutTests extends FantasyEsportsTestFixture
{
  @Test
  public void testSuccessful() throws Exception
  {
    LoginResponse loginResponse = ConvenienceMethods.login(PRE_REGISTERED_USER1);

    HttpResponse<String> logoutResponse = this.makePostRequest("/account/logout", ImmutableMap.of("Session-Key", loginResponse.sessionKey));
    assertThat(logoutResponse.getStatus()).isEqualTo(200);

    CreateLeagueRequest createLeagueRequest = new CreateLeagueRequest("test123", ZonedDateTime.now().plusHours(1), ZonedDateTime.now().plusMonths(1));
    HttpResponse<String> createLeagueResponse = this.makePostRequest("/league", createLeagueRequest,
        ImmutableMap.of("Session-Key", loginResponse.sessionKey));
    this.assertThatResponseEqualsException(createLeagueResponse, new UnauthorizedException());
  }

  @Test
  public void testWithBadSession() throws Exception
  {
    HttpResponse<String> logoutResponse = this.makePostRequest("/account/logout", ImmutableMap.of("Session-Key", "BadSessionKey"));

    this.assertThatResponseEqualsException(logoutResponse, new UnauthorizedException());
  }
}