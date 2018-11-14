package com.github.malow.FantasyEsports.regressiontests.league;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.malow.FantasyEsports.ConvenienceMethods;
import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.HttpResponseException.MissingMandatoryFieldException;
import com.github.malow.FantasyEsports.services.HttpResponseException.UnauthorizedException;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.AccountNotFoundException;
import com.github.malow.FantasyEsports.services.league.LeagueRole;
import com.github.malow.FantasyEsports.services.league.Manager;
import com.github.malow.FantasyEsports.services.league.requests.InviteManagerRequest;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.NoLeagueFoundException;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.UserIsAlreadyMemberInLeagueException;
import com.github.malow.malowlib.GsonSingleton;
import com.google.common.collect.ImmutableMap;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;

public class InviteManagerTests extends FantasyEsportsTestFixture
{
  @Test
  public void testSuccessful() throws Exception
  {
    String leagueId = ConvenienceMethods.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    InviteManagerRequest request = new InviteManagerRequest(PRE_REGISTERED_USER2.accountId);

    HttpResponse<String> response = this.makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getBody().toString()).isEqualTo("");

    request = new InviteManagerRequest(PRE_REGISTERED_USER3.accountId);

    response = this.makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getBody().toString()).isEqualTo("");

    response = this.makeGetRequest("/league/" + leagueId + "/manager");
    List<Manager> managers = GsonSingleton.fromJson(response.getBody().toString(), new TypeToken<ArrayList<Manager>>()
    {
    }.getType());
    assertThat(managers).hasSize(3);
    Manager user2Manager = managers.stream().filter(m -> m.getAccountId().equals(PRE_REGISTERED_USER2.accountId)).findAny().get();
    assertThat(user2Manager.getLeagueRole()).isEqualTo(LeagueRole.INVITED);
    assertThat(user2Manager.getScore()).isEqualTo(0);
    Manager user3Manager = managers.stream().filter(m -> m.getAccountId().equals(PRE_REGISTERED_USER3.accountId)).findAny().get();
    assertThat(user3Manager.getLeagueRole()).isEqualTo(LeagueRole.INVITED);
    assertThat(user3Manager.getScore()).isEqualTo(0);
  }

  @Test
  public void testMandatoryParameters() throws Exception
  {
    String leagueId = ConvenienceMethods.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    InviteManagerRequest request = new InviteManagerRequest(null);

    HttpResponse<String> response = this.makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new MissingMandatoryFieldException("inviteeAccountId"));
  }


  @Test
  public void testWithBadSession() throws Exception
  {
    String leagueId = ConvenienceMethods.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    InviteManagerRequest request = new InviteManagerRequest(PRE_REGISTERED_USER2.accountId);

    HttpResponse<String> response = this.makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", "BadSessionKey"));

    this.assertThatResponseEqualsException(response, new UnauthorizedException());
  }

  @Test
  public void testNoLeagueFound() throws Exception
  {
    InviteManagerRequest request = new InviteManagerRequest(PRE_REGISTERED_USER2.accountId);

    HttpResponse<String> response = this.makePostRequest("/league/badLeagueId/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new NoLeagueFoundException());
  }

  @Test
  public void testInviteNonexistentUser() throws Exception
  {
    String leagueId = ConvenienceMethods.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    InviteManagerRequest request = new InviteManagerRequest("badAccountId");

    HttpResponse<String> response = this.makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new AccountNotFoundException());
  }

  @Test
  public void testInviteSelf() throws Exception
  {
    String leagueId = ConvenienceMethods.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    InviteManagerRequest request = new InviteManagerRequest(PRE_REGISTERED_USER1.accountId);

    HttpResponse<String> response = this.makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new UserIsAlreadyMemberInLeagueException());
  }

  @Test
  public void testInviteTwice() throws Exception
  {
    String leagueId = ConvenienceMethods.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    ConvenienceMethods.inviteManager(leagueId, PRE_REGISTERED_USER1.sessionKey, PRE_REGISTERED_USER2.accountId);
    InviteManagerRequest request = new InviteManagerRequest(PRE_REGISTERED_USER2.accountId);

    HttpResponse<String> response = this.makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new UserIsAlreadyMemberInLeagueException());
  }

  @Test
  public void testInviteAsNonMember() throws Exception
  {
    String leagueId = ConvenienceMethods.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    InviteManagerRequest request = new InviteManagerRequest(PRE_REGISTERED_USER3.accountId);

    HttpResponse<String> response = this.makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER2.sessionKey));

    this.assertThatResponseEqualsException(response, new UnauthorizedException());
  }

  @Test
  public void testInviteAsNonOwner() throws Exception
  {
    String leagueId = ConvenienceMethods.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    ConvenienceMethods.inviteManager(leagueId, PRE_REGISTERED_USER1.sessionKey, PRE_REGISTERED_USER2.accountId);
    InviteManagerRequest request = new InviteManagerRequest(PRE_REGISTERED_USER3.accountId);

    HttpResponse<String> response = this.makePostRequest("/league/" + leagueId + "/manager", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER2.sessionKey));

    this.assertThatResponseEqualsException(response, new UnauthorizedException());
  }
}