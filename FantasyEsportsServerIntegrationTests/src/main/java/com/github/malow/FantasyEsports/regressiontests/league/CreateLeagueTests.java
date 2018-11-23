package com.github.malow.FantasyEsports.regressiontests.league;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;

import org.junit.Test;

import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.HttpResponseException.MissingMandatoryFieldException;
import com.github.malow.FantasyEsports.services.HttpResponseException.UnauthorizedException;
import com.github.malow.FantasyEsports.services.account.responses.ResponseLeague;
import com.github.malow.FantasyEsports.services.league.LeagueRole;
import com.github.malow.FantasyEsports.services.league.requests.CreateLeagueRequest;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.CreateNameTakenException;
import com.github.malow.malowlib.GsonSingleton;
import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.HttpResponse;

public class CreateLeagueTests extends FantasyEsportsTestFixture
{
  final ZonedDateTime startDate = ZonedDateTime.now().plusHours(1);
  final ZonedDateTime endDate = ZonedDateTime.now().plusMonths(1);

  @Test
  public void testSuccessful() throws Exception
  {
    CreateLeagueRequest request = new CreateLeagueRequest("test123", this.startDate, this.endDate);

    HttpResponse<String> response = makePostRequest("/league", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    assertThat(response.getStatus()).isEqualTo(200);
    ResponseLeague league = GsonSingleton.fromJson(response.getBody().toString(), ResponseLeague.class);
    assertThat(league.id).matches("[0-9a-f-]+");
    assertThat(league.name).isEqualTo("test123");
    assertThat(league.startDate).isEqualTo(this.startDate);
    assertThat(league.endDate).isEqualTo(this.endDate);
    assertThat(league.managers).hasSize(1);
    assertThat(league.managers.get(0).displayName).isEqualTo(PRE_REGISTERED_USER1.displayName);
    assertThat(league.managers.get(0).accountId).isEqualTo(PRE_REGISTERED_USER1.accountId);
    assertThat(league.managers.get(0).leagueId).isEqualTo(league.id);
    assertThat(league.managers.get(0).leagueRole).isEqualTo(LeagueRole.OWNER);
    assertThat(league.managers.get(0).score).isEqualTo(0);
  }

  @Test
  public void testMandatoryParameters() throws Exception
  {
    CreateLeagueRequest request = new CreateLeagueRequest(null, this.startDate, this.endDate);
    HttpResponse<String> response = makePostRequest("/league", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));
    this.assertThatResponseEqualsException(response, new MissingMandatoryFieldException("name"));

    request = new CreateLeagueRequest("test123", null, this.endDate);
    response = makePostRequest("/league", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));
    this.assertThatResponseEqualsException(response, new MissingMandatoryFieldException("startDate"));

    request = new CreateLeagueRequest("test123", this.startDate, null);
    response = makePostRequest("/league", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));
    this.assertThatResponseEqualsException(response, new MissingMandatoryFieldException("endDate"));
  }

  @Test
  public void testWithoutSession() throws Exception
  {
    CreateLeagueRequest request = new CreateLeagueRequest("test123", this.startDate, this.endDate);

    HttpResponse<String> response = makePostRequest("/league", request);

    this.assertThatResponseEqualsException(response, new UnauthorizedException());
  }

  @Test
  public void testWithBadSession() throws Exception
  {
    CreateLeagueRequest request = new CreateLeagueRequest("test123", this.startDate, this.endDate);

    HttpResponse<String> response = makePostRequest("/league", request, ImmutableMap.of("Session-Key", "badSession"));

    this.assertThatResponseEqualsException(response, new UnauthorizedException());
  }

  @Test
  public void testWithSameName() throws Exception
  {
    createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    CreateLeagueRequest request = new CreateLeagueRequest("test123", this.startDate, this.endDate);

    HttpResponse<String> response = makePostRequest("/league", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new CreateNameTakenException());
  }
}