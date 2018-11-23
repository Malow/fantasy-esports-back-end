package com.github.malow.FantasyEsports.regressiontests.league;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.HttpResponseException.ForbiddenException;
import com.github.malow.FantasyEsports.services.HttpResponseException.MissingMandatoryFieldException;
import com.github.malow.FantasyEsports.services.HttpResponseException.UnauthorizedException;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.AccountNotFoundException;
import com.github.malow.FantasyEsports.services.account.responses.ResponseManager;
import com.github.malow.FantasyEsports.services.league.LeagueRole;
import com.github.malow.FantasyEsports.services.league.requests.InviteManagerRequest;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.NoLeagueFoundException;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.UserIsAlreadyInvitedToLeagueException;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.UserIsAlreadyMemberInLeagueException;
import com.github.malow.malowlib.GsonSingleton;
import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.HttpResponse;

public class InviteManagerTests extends FantasyEsportsTestFixture
{
  @Test
  public void testSuccessful() throws Exception
  {
    String leagueId = createLeague("test123", PRE_REGISTERED_USER1.sessionKey).id;
    InviteManagerRequest request = new InviteManagerRequest(PRE_REGISTERED_USER2.accountId);

    HttpResponse<String> response = makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getBody().toString()).isEqualTo("");

    request = new InviteManagerRequest(PRE_REGISTERED_USER3.accountId);

    response = makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getBody().toString()).isEqualTo("");

    response = makeGetRequest("/league/" + leagueId + "/manager");
    List<ResponseManager> managers = GsonSingleton.fromJsonAsList(response.getBody().toString(), ResponseManager[].class);
    assertThat(managers).hasSize(3);
    ResponseManager user2Manager = managers.stream().filter(m -> m.accountId.equals(PRE_REGISTERED_USER2.accountId)).findAny().get();
    assertThat(user2Manager.leagueRole).isEqualTo(LeagueRole.INVITED);
    assertThat(user2Manager.score).isEqualTo(0);
    ResponseManager user3Manager = managers.stream().filter(m -> m.accountId.equals(PRE_REGISTERED_USER3.accountId)).findAny().get();
    assertThat(user3Manager.leagueRole).isEqualTo(LeagueRole.INVITED);
    assertThat(user3Manager.score).isEqualTo(0);
  }

  @Test
  public void testMandatoryParameters() throws Exception
  {
    String leagueId = createLeague("test123", PRE_REGISTERED_USER1.sessionKey).id;
    InviteManagerRequest request = new InviteManagerRequest(null);

    HttpResponse<String> response = makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new MissingMandatoryFieldException("inviteeAccountId"));
  }


  @Test
  public void testWithBadSession() throws Exception
  {
    String leagueId = createLeague("test123", PRE_REGISTERED_USER1.sessionKey).id;
    InviteManagerRequest request = new InviteManagerRequest(PRE_REGISTERED_USER2.accountId);

    HttpResponse<String> response = makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", "BadSessionKey"));

    this.assertThatResponseEqualsException(response, new UnauthorizedException());
  }

  @Test
  public void testNoLeagueFound() throws Exception
  {
    InviteManagerRequest request = new InviteManagerRequest(PRE_REGISTERED_USER2.accountId);

    HttpResponse<String> response = makePostRequest("/league/badLeagueId/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new NoLeagueFoundException());
  }

  @Test
  public void testInviteNonexistentUser() throws Exception
  {
    String leagueId = createLeague("test123", PRE_REGISTERED_USER1.sessionKey).id;
    InviteManagerRequest request = new InviteManagerRequest("badAccountId");

    HttpResponse<String> response = makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new AccountNotFoundException());
  }

  @Test
  public void testInviteSelf() throws Exception
  {
    String leagueId = createLeague("test123", PRE_REGISTERED_USER1.sessionKey).id;
    InviteManagerRequest request = new InviteManagerRequest(PRE_REGISTERED_USER1.accountId);

    HttpResponse<String> response = makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new UserIsAlreadyMemberInLeagueException());
  }

  @Test
  public void testInviteTwice() throws Exception
  {
    String leagueId = createLeague("test123", PRE_REGISTERED_USER1.sessionKey).id;
    inviteManager(leagueId, PRE_REGISTERED_USER1.sessionKey, PRE_REGISTERED_USER2.accountId);
    InviteManagerRequest request = new InviteManagerRequest(PRE_REGISTERED_USER2.accountId);

    HttpResponse<String> response = makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new UserIsAlreadyInvitedToLeagueException());
  }

  @Test
  public void testInviteAlreadyMember() throws Exception
  {
    String leagueId = createLeague("test123", PRE_REGISTERED_USER1.sessionKey).id;
    inviteManager(leagueId, PRE_REGISTERED_USER1.sessionKey, PRE_REGISTERED_USER2.accountId);
    acceptInvite(leagueId, PRE_REGISTERED_USER2.sessionKey, PRE_REGISTERED_USER2.accountId);
    InviteManagerRequest request = new InviteManagerRequest(PRE_REGISTERED_USER2.accountId);

    HttpResponse<String> response = makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new UserIsAlreadyMemberInLeagueException());
  }

  @Test
  public void testInviteAsNonMember() throws Exception
  {
    String leagueId = createLeague("test123", PRE_REGISTERED_USER1.sessionKey).id;
    InviteManagerRequest request = new InviteManagerRequest(PRE_REGISTERED_USER3.accountId);

    HttpResponse<String> response = makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER2.sessionKey));

    this.assertThatResponseEqualsException(response, new ForbiddenException());
  }

  @Test
  public void testInviteAsNonOwner() throws Exception
  {
    String leagueId = createLeague("test123", PRE_REGISTERED_USER1.sessionKey).id;
    inviteManager(leagueId, PRE_REGISTERED_USER1.sessionKey, PRE_REGISTERED_USER2.accountId);
    InviteManagerRequest request = new InviteManagerRequest(PRE_REGISTERED_USER3.accountId);

    HttpResponse<String> response = makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER2.sessionKey));

    this.assertThatResponseEqualsException(response, new ForbiddenException());
  }
}