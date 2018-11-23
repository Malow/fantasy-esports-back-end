package com.github.malow.FantasyEsports.regressiontests.league;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.account.responses.ResponseManager;
import com.github.malow.FantasyEsports.services.league.LeagueRole;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.NoLeagueFoundException;
import com.github.malow.malowlib.GsonSingleton;
import com.mashape.unirest.http.HttpResponse;

public class GetManagersTests extends FantasyEsportsTestFixture
{
  @Test
  public void testSuccessful() throws Exception
  {
    String leagueId = createLeague("test123", PRE_REGISTERED_USER1.sessionKey).id;

    HttpResponse<String> response = makeGetRequest("/league/" + leagueId + "/manager");

    assertThat(response.getStatus()).isEqualTo(200);
    List<ResponseManager> managers = GsonSingleton.fromJsonAsList(response.getBody().toString(), ResponseManager[].class);
    assertThat(managers.size()).isEqualTo(1);
    assertThat(managers.get(0).leagueRole).isEqualTo(LeagueRole.OWNER);
    assertThat(managers.get(0).accountId).isEqualTo(PRE_REGISTERED_USER1.accountId);
    assertThat(managers.get(0).score).isEqualTo(0);
    assertThat(managers.get(0).displayName).isEqualTo(PRE_REGISTERED_USER1.displayName);
    assertThat(managers.get(0).leagueId).isEqualTo(leagueId);
  }

  @Test
  public void testNoLeagueFound() throws Exception
  {
    HttpResponse<String> response = makeGetRequest("/league/badLeagueId/manager");

    this.assertThatResponseEqualsException(response, new NoLeagueFoundException());
  }
}