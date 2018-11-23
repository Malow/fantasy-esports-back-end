package com.github.malow.FantasyEsports.regressiontests.league;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;

import org.junit.Test;

import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.account.responses.ResponseLeague;
import com.github.malow.FantasyEsports.services.league.LeagueRole;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.NoLeagueFoundException;
import com.github.malow.malowlib.GsonSingleton;
import com.mashape.unirest.http.HttpResponse;

public class GetLeagueTests extends FantasyEsportsTestFixture
{
  final ZonedDateTime startDate = ZonedDateTime.now().plusHours(1);
  final ZonedDateTime endDate = ZonedDateTime.now().plusMonths(1);

  @Test
  public void testSuccessful() throws Exception
  {
    String leagueId = createLeague("test123", this.startDate, this.endDate, PRE_REGISTERED_USER1.sessionKey).id;

    HttpResponse<String> response = makeGetRequest("/league/" + leagueId);

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
  public void testNoLeagueFound() throws Exception
  {
    HttpResponse<String> response = makeGetRequest("/league/" + "badLeagueId");

    this.assertThatResponseEqualsException(response, new NoLeagueFoundException());
  }
}