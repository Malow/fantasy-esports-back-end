package com.github.malow.FantasyEsports.regressiontests;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.github.malow.FantasyEsports.Config;
import com.github.malow.FantasyEsports.ConvenienceMethods;
import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.league.League;
import com.github.malow.FantasyEsports.services.league.requests.CreateLeagueRequest;
import com.github.malow.malowlib.GsonSingleton;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.Unirest;

public class LeagueTests extends FantasyEsportsTestFixture
{
  final ZonedDateTime startDate = ZonedDateTime.now().plusHours(1);
  final ZonedDateTime endDate = ZonedDateTime.now().plusMonths(1);

  @Test
  public void testCreateLeagueSuccessfully() throws Exception
  {
    String responseBody = Unirest.post(Config.HOST + "/league").header("Session-Key", PRE_REGISTERED_USER1.sessionKey)
        .body(GsonSingleton.toJson(new CreateLeagueRequest("test123", this.startDate, this.endDate))).asJson()
        .getBody().toString();

    assertThat(responseBody).isEqualTo("{}");
  }

  @Test
  public void testCreateLeagueWithoutSession() throws Exception
  {
    String responseBody = Unirest.post(Config.HOST + "/league")
        .body(GsonSingleton.toJson(new CreateLeagueRequest("test123", this.startDate, this.endDate))).asJson()
        .getBody().toString();

    assertThat(responseBody).contains("Missing request header 'Session-Key'");
    assertThat(responseBody).contains("\"status\":400");
  }

  @Test
  public void testCreateLeagueWithBadSession() throws Exception
  {
    String responseBody = Unirest.post(Config.HOST + "/league").header("Session-Key", "badSession")
        .body(GsonSingleton.toJson(new CreateLeagueRequest("test123", this.startDate, this.endDate))).asJson()
        .getBody().toString();

    assertThat(responseBody).contains("You do not have permission to do that");
    assertThat(responseBody).contains("\"status\":401");
  }

  @Test
  public void testCreateLeagueWithSameName() throws Exception
  {
    ConvenienceMethods.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);

    String responseBody = Unirest.post(Config.HOST + "/league").header("Session-Key", PRE_REGISTERED_USER1.sessionKey)
        .body(GsonSingleton.toJson(new CreateLeagueRequest("test123", this.startDate, this.endDate))).asJson()
        .getBody().toString();

    assertThat(responseBody).contains("Name is already taken");
    assertThat(responseBody).contains("\"status\":400");
  }

  @Test
  public void testGetLeaguesSucessful() throws Exception
  {
    ConvenienceMethods.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    ConvenienceMethods.createLeague("test124", PRE_REGISTERED_USER1.sessionKey);

    String responseBody = Unirest.get(Config.HOST + "/league").asJson().getBody().toString();

    List<League> leagues = GsonSingleton.fromJson(responseBody, new TypeToken<ArrayList<League>>()
    {
    }.getType());
    assertThat(leagues.stream().map(League::getName).collect(Collectors.toList())).containsExactlyInAnyOrder("test123", "test124");
  }

  @Test
  public void testGetLeagueSucessful() throws Exception
  {
    ConvenienceMethods.createLeague("test123", this.startDate, this.endDate, PRE_REGISTERED_USER1.sessionKey);
    List<League> leagues = ConvenienceMethods.getLeagues();

    String responseBody = Unirest.get(Config.HOST + "/league/" + leagues.get(0).getId()).asJson().getBody().toString();

    League league = GsonSingleton.fromJson(responseBody, League.class);
    assertThat(league.getName()).isEqualTo("test123");
    assertThat(league.getOwnerDisplayName()).isEqualTo(PRE_REGISTERED_USER1.displayName);
    assertThat(league.getStartDate()).isEqualTo(this.startDate);
    assertThat(league.getEndDate()).isEqualTo(this.endDate);
  }

  @Test
  public void testGetLeagueNoLeagueFound() throws Exception
  {
    String responseBody = Unirest.get(Config.HOST + "/league/" + "badLeagueId").asJson().getBody().toString();

    assertThat(responseBody).contains("No league found");
    assertThat(responseBody).contains("\"status\":404");
  }
}