package com.github.malow.FantasyEsports.regressiontests.manager;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.github.malow.FantasyEsports.ConvenienceMethods;
import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.account.responses.ResponseLeague;
import com.github.malow.FantasyEsports.services.account.responses.ResponseManager;
import com.github.malow.FantasyEsports.services.league.LeagueRole;
import com.github.malow.FantasyEsports.services.manager.responses.ManagerExceptions.NoManagerFoundException;
import com.github.malow.malowlib.GsonSingleton;
import com.mashape.unirest.http.HttpResponse;

public class GetManagerTests extends FantasyEsportsTestFixture
{
  @Test
  public void testSuccessful() throws Exception
  {
    ResponseLeague league = ConvenienceMethods.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);

    HttpResponse<String> response = this.makeGetRequest("/manager/" + league.managers.get(0).id);

    assertThat(response.getStatus()).isEqualTo(200);
    ResponseManager manager = GsonSingleton.fromJson(response.getBody().toString(), ResponseManager.class);
    assertThat(manager.id).isEqualTo(league.managers.get(0).id);
    assertThat(manager.displayName).isEqualTo(PRE_REGISTERED_USER1.displayName);
    assertThat(manager.leagueRole).isEqualTo(LeagueRole.OWNER);
    assertThat(manager.accountId).isEqualTo(PRE_REGISTERED_USER1.accountId);
    assertThat(manager.leagueId).isEqualTo(league.id);
    assertThat(manager.score).isEqualTo(0);
  }

  @Test
  public void testNoManagerFound() throws Exception
  {
    HttpResponse<String> response = this.makeGetRequest("/manager/" + "badManagerId");

    this.assertThatResponseEqualsException(response, new NoManagerFoundException());
  }
}
