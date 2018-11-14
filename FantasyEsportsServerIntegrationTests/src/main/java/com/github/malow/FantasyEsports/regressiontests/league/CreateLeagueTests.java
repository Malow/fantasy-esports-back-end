package com.github.malow.FantasyEsports.regressiontests.league;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;

import org.junit.Test;

import com.github.malow.FantasyEsports.ConvenienceMethods;
import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.HttpResponseException.MissingMandatoryFieldException;
import com.github.malow.FantasyEsports.services.HttpResponseException.UnauthorizedException;
import com.github.malow.FantasyEsports.services.league.League;
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

    HttpResponse<String> response = this.makePostRequest("/league", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    assertThat(response.getStatus()).isEqualTo(200);
    League league = GsonSingleton.fromJson(response.getBody().toString(), League.class);
    assertThat(league.getId()).matches("[0-9a-f-]+");
    assertThat(league.getName()).isEqualTo("test123");
    assertThat(league.getStartDate()).isEqualTo(this.startDate);
    assertThat(league.getEndDate()).isEqualTo(this.endDate);
  }

  @Test
  public void testMandatoryParameters() throws Exception
  {
    CreateLeagueRequest request = new CreateLeagueRequest(null, this.startDate, this.endDate);
    HttpResponse<String> response = this.makePostRequest("/league", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));
    this.assertThatResponseEqualsException(response, new MissingMandatoryFieldException("name"));

    request = new CreateLeagueRequest("test123", null, this.endDate);
    response = this.makePostRequest("/league", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));
    this.assertThatResponseEqualsException(response, new MissingMandatoryFieldException("startDate"));

    request = new CreateLeagueRequest("test123", this.startDate, null);
    response = this.makePostRequest("/league", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));
    this.assertThatResponseEqualsException(response, new MissingMandatoryFieldException("endDate"));
  }

  @Test
  public void testWithoutSession() throws Exception
  {
    CreateLeagueRequest request = new CreateLeagueRequest("test123", this.startDate, this.endDate);

    HttpResponse<String> response = this.makePostRequest("/league", request);

    this.assertThatResponseEqualsException(response, new UnauthorizedException());
  }

  @Test
  public void testWithBadSession() throws Exception
  {
    CreateLeagueRequest request = new CreateLeagueRequest("test123", this.startDate, this.endDate);

    HttpResponse<String> response = this.makePostRequest("/league", request, ImmutableMap.of("Session-Key", "badSession"));

    this.assertThatResponseEqualsException(response, new UnauthorizedException());
  }

  @Test
  public void testWithSameName() throws Exception
  {
    ConvenienceMethods.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    CreateLeagueRequest request = new CreateLeagueRequest("test123", this.startDate, this.endDate);

    HttpResponse<String> response = this.makePostRequest("/league", request, ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new CreateNameTakenException());
  }
}