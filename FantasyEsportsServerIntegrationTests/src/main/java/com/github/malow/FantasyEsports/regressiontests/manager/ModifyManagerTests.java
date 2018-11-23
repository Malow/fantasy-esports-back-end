package com.github.malow.FantasyEsports.regressiontests.manager;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.github.malow.FantasyEsports.Config;
import com.github.malow.FantasyEsports.ConvenienceMethods;
import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.services.HttpResponseException.ForbiddenException;
import com.github.malow.FantasyEsports.services.HttpResponseException.IllegalValueException;
import com.github.malow.FantasyEsports.services.HttpResponseException.NoChangeMadeException;
import com.github.malow.FantasyEsports.services.HttpResponseException.UnauthorizedException;
import com.github.malow.FantasyEsports.services.account.responses.ResponseLeague;
import com.github.malow.FantasyEsports.services.account.responses.ResponseManager;
import com.github.malow.FantasyEsports.services.league.LeagueRole;
import com.github.malow.FantasyEsports.services.manager.requests.ModifyManagerRequest;
import com.github.malow.FantasyEsports.services.manager.responses.ManagerExceptions.NoManagerFoundException;
import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class ModifyManagerTests extends FantasyEsportsTestFixture
{
  @Test
  public void testAcceptInviteSuccessful() throws Exception
  {
    ResponseLeague league = ConvenienceMethods.createLeague("Test123", PRE_REGISTERED_USER1.sessionKey);
    ConvenienceMethods.inviteManager(league.id, PRE_REGISTERED_USER1.sessionKey, PRE_REGISTERED_USER2.accountId);
    ResponseManager inviteeManager = ConvenienceMethods.getManagersForLeague(league.id).stream()
        .filter(m -> m.accountId.equals(PRE_REGISTERED_USER2.accountId)).findFirst().get();
    ModifyManagerRequest request = new ModifyManagerRequest(LeagueRole.MEMBER);

    HttpResponse<String> response = this.makePatchRequest("/manager/" + inviteeManager.id, request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER2.sessionKey));

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getBody().toString()).isEqualTo("");
    inviteeManager = ConvenienceMethods.getManagersForLeague(league.id).stream()
        .filter(m -> m.accountId.equals(PRE_REGISTERED_USER2.accountId)).findFirst().get();
    assertThat(inviteeManager.leagueRole).isEqualTo(LeagueRole.MEMBER);
  }

  @Test
  public void testWithBadSession() throws Exception
  {
    ResponseLeague league = ConvenienceMethods.createLeague("Test123", PRE_REGISTERED_USER1.sessionKey);
    String managerId = league.managers.get(0).id;
    ModifyManagerRequest request = new ModifyManagerRequest(LeagueRole.MEMBER);

    HttpResponse<String> response = this.makePatchRequest("/manager/" + managerId, request, ImmutableMap.of("Session-Key", "badSession"));

    this.assertThatResponseEqualsException(response, new UnauthorizedException());
  }

  @Test
  public void testWithBadManagerId() throws Exception
  {
    ConvenienceMethods.createLeague("Test123", PRE_REGISTERED_USER1.sessionKey);
    ModifyManagerRequest request = new ModifyManagerRequest(LeagueRole.MEMBER);

    HttpResponse<String> response = this.makePatchRequest("/manager/" + "badManagerId", request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new NoManagerFoundException());
  }

  @Test
  public void testWithInvalidValueForLeagueRole() throws Exception
  {
    ResponseLeague league = ConvenienceMethods.createLeague("Test123", PRE_REGISTERED_USER1.sessionKey);
    ConvenienceMethods.inviteManager(league.id, PRE_REGISTERED_USER1.sessionKey, PRE_REGISTERED_USER2.accountId);
    ResponseManager inviteeManager = ConvenienceMethods.getManagersForLeague(league.id).stream()
        .filter(m -> m.accountId.equals(PRE_REGISTERED_USER2.accountId)).findFirst().get();

    HttpResponse<String> response = Unirest.patch(Config.HOST + "/manager/" + inviteeManager.id)
        .headers(ImmutableMap.of("Session-Key", PRE_REGISTERED_USER2.sessionKey)).body("{\"leagueRole\":\"BadLeagueRole\"}").asString();

    this.assertThatResponseEqualsException(response, new NoChangeMadeException());
  }

  @Test
  public void testModifyOthersManager() throws Exception
  {
    ResponseLeague league = ConvenienceMethods.createLeague("Test123", PRE_REGISTERED_USER1.sessionKey);
    ConvenienceMethods.inviteManager(league.id, PRE_REGISTERED_USER1.sessionKey, PRE_REGISTERED_USER2.accountId);
    ResponseManager inviteeManager = ConvenienceMethods.getManagersForLeague(league.id).stream()
        .filter(m -> m.accountId.equals(PRE_REGISTERED_USER2.accountId)).findFirst().get();
    ModifyManagerRequest request = new ModifyManagerRequest(LeagueRole.MEMBER);

    HttpResponse<String> response = this.makePatchRequest("/manager/" + inviteeManager.id, request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new ForbiddenException());
  }

  @Test
  public void testModifyOwnRoleFromInvitedToOwner() throws Exception
  {
    ResponseLeague league = ConvenienceMethods.createLeague("Test123", PRE_REGISTERED_USER1.sessionKey);
    ConvenienceMethods.inviteManager(league.id, PRE_REGISTERED_USER1.sessionKey, PRE_REGISTERED_USER2.accountId);
    ResponseManager inviteeManager = ConvenienceMethods.getManagersForLeague(league.id).stream()
        .filter(m -> m.accountId.equals(PRE_REGISTERED_USER2.accountId)).findFirst().get();
    ModifyManagerRequest request = new ModifyManagerRequest(LeagueRole.OWNER);

    HttpResponse<String> response = this.makePatchRequest("/manager/" + inviteeManager.id, request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER2.sessionKey));

    this.assertThatResponseEqualsException(response, new IllegalValueException("leagueRole"));
  }

  @Test
  public void testModifyOwnRoleOwnerToMember() throws Exception
  {
    ResponseLeague league = ConvenienceMethods.createLeague("Test123", PRE_REGISTERED_USER1.sessionKey);
    ResponseManager manager = ConvenienceMethods.getManagersForLeague(league.id).get(0);
    ModifyManagerRequest request = new ModifyManagerRequest(LeagueRole.MEMBER);

    HttpResponse<String> response = this.makePatchRequest("/manager/" + manager.id, request,
        ImmutableMap.of("Session-Key", PRE_REGISTERED_USER1.sessionKey));

    this.assertThatResponseEqualsException(response, new IllegalValueException("leagueRole"));
  }
}
