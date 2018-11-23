package com.github.malow.FantasyEsports.regressiontests.league;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.HttpResponseException.IllegalValueException;
import com.github.malow.FantasyEsports.services.HttpResponseException.UnauthorizedException;
import com.github.malow.FantasyEsports.services.account.responses.ResponseLeague;
import com.github.malow.malowlib.GsonSingleton;
import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.HttpResponse;

public class ListLeaguesTests extends FantasyEsportsTestFixture
{
  final ZonedDateTime startDate = ZonedDateTime.now().plusHours(1);
  final ZonedDateTime endDate = ZonedDateTime.now().plusMonths(1);

  @Test
  public void testSuccessful() throws Exception
  {
    createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    String league2Id = createLeague("test124", PRE_REGISTERED_USER2.sessionKey).id; // Other user creates a league that we don't get in response
    inviteManager(league2Id, PRE_REGISTERED_USER2.sessionKey, PRE_REGISTERED_USER1.accountId);

    HttpResponse<String> response = makeGetRequest("/league", ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    assertThat(response.getStatus()).isEqualTo(200);
    List<ResponseLeague> leagues = GsonSingleton.fromJsonAsList(response.getBody().toString(), ResponseLeague[].class);
    assertThat(leagues.stream().map(l -> l.name).collect(Collectors.toList())).containsExactlyInAnyOrder("test123", "test124");
  }

  @Test
  public void testWithBadSession() throws Exception
  {
    createLeague("test123", PRE_REGISTERED_USER1.sessionKey);

    HttpResponse<String> response = makeGetRequest("/league", ImmutableMap.of("Session-Key", "BadSessionKey"));

    this.assertThatResponseEqualsException(response, new UnauthorizedException());
  }

  @Test
  public void testFilterByRoleSuccessful() throws Exception
  {
    createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    String league2Id = createLeague("test124", PRE_REGISTERED_USER2.sessionKey).id;
    inviteManager(league2Id, PRE_REGISTERED_USER2.sessionKey, PRE_REGISTERED_USER1.accountId);

    HttpResponse<String> responseOwner = makeGetRequest("/league?role=OWNER", ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));
    HttpResponse<String> responseInvited = makeGetRequest("/league?role=INVITED", ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    assertThat(responseOwner.getStatus()).isEqualTo(200);
    List<ResponseLeague> leagues = GsonSingleton.fromJsonAsList(responseOwner.getBody().toString(), ResponseLeague[].class);
    assertThat(leagues.stream().map(l -> l.name).collect(Collectors.toList())).containsExactlyInAnyOrder("test123");
    assertThat(responseInvited.getStatus()).isEqualTo(200);
    leagues = GsonSingleton.fromJsonAsList(responseInvited.getBody().toString(), ResponseLeague[].class);
    assertThat(leagues.stream().map(l -> l.name).collect(Collectors.toList())).containsExactlyInAnyOrder("test124");
  }

  @Test
  public void testFilterByBadRole() throws Exception
  {
    HttpResponse<String> response = makeGetRequest("/league?role=BADLEAGUEROLE",
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new IllegalValueException("role"));
  }
}