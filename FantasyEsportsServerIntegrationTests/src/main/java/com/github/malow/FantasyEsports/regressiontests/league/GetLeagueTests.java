package com.github.malow.FantasyEsports.regressiontests.league;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.github.malow.FantasyEsports.ConvenienceMethods;
import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.league.League;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.NoLeagueFoundException;
import com.github.malow.malowlib.GsonSingleton;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;

public class GetLeagueTests extends FantasyEsportsTestFixture
{
  final ZonedDateTime startDate = ZonedDateTime.now().plusHours(1);
  final ZonedDateTime endDate = ZonedDateTime.now().plusMonths(1);

  @Test
  public void testGetLeaguesSuccessful() throws Exception
  {
    ConvenienceMethods.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    ConvenienceMethods.createLeague("test124", PRE_REGISTERED_USER1.sessionKey);

    HttpResponse<String> response = this.makeGetRequest("/league");

    assertThat(response.getStatus()).isEqualTo(200);
    List<League> leagues = GsonSingleton.fromJson(response.getBody().toString(), new TypeToken<ArrayList<League>>()
    {
    }.getType());
    assertThat(leagues.stream().map(League::getName).collect(Collectors.toList())).containsExactlyInAnyOrder("test123", "test124");
  }

  @Test
  public void testGetLeagueSuccessful() throws Exception
  {
    ConvenienceMethods.createLeague("test123", this.startDate, this.endDate, PRE_REGISTERED_USER1.sessionKey);
    List<League> leagues = ConvenienceMethods.getLeagues();

    HttpResponse<String> response = this.makeGetRequest("/league/" + leagues.get(0).getId());

    assertThat(response.getStatus()).isEqualTo(200);
    League league = GsonSingleton.fromJson(response.getBody().toString(), League.class);
    assertThat(league.getName()).isEqualTo("test123");
    assertThat(league.getStartDate()).isEqualTo(this.startDate);
    assertThat(league.getEndDate()).isEqualTo(this.endDate);
  }

  @Test
  public void testGetLeagueNoLeagueFound() throws Exception
  {
    HttpResponse<String> response = this.makeGetRequest("/league/" + "badLeagueId");

    this.assertThatResponseEqualsException(response, new NoLeagueFoundException());
  }
}