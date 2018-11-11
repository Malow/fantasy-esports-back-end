package com.github.malow.FantasyEsports.regressiontests.league;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.malow.FantasyEsports.ConvenienceMethods;
import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.league.LeagueRole;
import com.github.malow.FantasyEsports.services.league.Manager;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.NoLeagueFoundException;
import com.github.malow.malowlib.GsonSingleton;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;

public class GetManagersTests extends FantasyEsportsTestFixture
{
  @Test
  public void testSuccessful() throws Exception
  {
    String leagueId = ConvenienceMethods.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);

    HttpResponse<String> response = this.makeGetRequest("/league/" + leagueId + "/manager");

    assertThat(response.getStatus()).isEqualTo(200);
    List<Manager> managers = GsonSingleton.fromJson(response.getBody().toString(), new TypeToken<ArrayList<Manager>>()
    {
    }.getType());
    assertThat(managers.size()).isEqualTo(1);
    assertThat(managers.get(0).getLeagueRole()).isEqualTo(LeagueRole.OWNER);
    assertThat(managers.get(0).getAccountId()).isEqualTo(PRE_REGISTERED_USER1.accountId);
    assertThat(managers.get(0).getScore()).isEqualTo(0);
  }

  @Test
  public void testNoLeagueFound() throws Exception
  {
    HttpResponse<String> response = this.makeGetRequest("/league/badLeagueId/manager");

    this.assertThatResponseEqualsException(response, new NoLeagueFoundException());
  }
}