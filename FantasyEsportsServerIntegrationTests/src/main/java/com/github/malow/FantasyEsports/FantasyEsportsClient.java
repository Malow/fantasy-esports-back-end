package com.github.malow.FantasyEsports;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.List;

import com.github.malow.FantasyEsports.FantasyEsportsTestFixture.TestUser;
import com.github.malow.FantasyEsports.services.account.requests.LoginRequest;
import com.github.malow.FantasyEsports.services.account.requests.RegisterRequest;
import com.github.malow.FantasyEsports.services.account.responses.LoginResponse;
import com.github.malow.FantasyEsports.services.account.responses.ResponseLeague;
import com.github.malow.FantasyEsports.services.account.responses.ResponseManager;
import com.github.malow.FantasyEsports.services.league.LeagueRole;
import com.github.malow.FantasyEsports.services.league.requests.CreateLeagueRequest;
import com.github.malow.FantasyEsports.services.league.requests.InviteManagerRequest;
import com.github.malow.FantasyEsports.services.manager.requests.ModifyManagerRequest;
import com.github.malow.malowlib.GsonSingleton;
import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.HttpResponse;

public class FantasyEsportsClient extends HttpClient
{

  public static LoginResponse login(TestUser user) throws Exception
  {
    HttpResponse<String> response = makePostRequest("/account/login", new LoginRequest(user.email, user.password));

    assertThat(response.getStatus()).isEqualTo(200);
    LoginResponse loginResponse = GsonSingleton.fromJson(response.getBody().toString(), LoginResponse.class);
    assertThat(loginResponse.sessionKey).matches("[0-9a-f-]+");
    assertThat(loginResponse.accountId).matches("[0-9a-f-]+");
    return loginResponse;
  }

  public static LoginResponse register(TestUser user) throws Exception
  {
    HttpResponse<String> response = makePostRequest("/account/register", new RegisterRequest(user.email, user.displayName, user.password));

    assertThat(response.getStatus()).isEqualTo(200);
    LoginResponse loginResponse = GsonSingleton.fromJson(response.getBody().toString(), LoginResponse.class);
    assertThat(loginResponse.sessionKey).matches("[0-9a-f-]+");
    assertThat(loginResponse.accountId).matches("[0-9a-f-]+");
    return loginResponse;
  }

  public static ResponseLeague createLeague(String name, String sessionKey) throws Exception
  {
    return createLeague(name, ZonedDateTime.now().plusHours(1), ZonedDateTime.now().plusMonths(1), sessionKey);
  }

  public static ResponseLeague createLeague(String name, ZonedDateTime startDate, ZonedDateTime endDate, String sessionKey) throws Exception
  {
    HttpResponse<String> response = makePostRequest("/league", new CreateLeagueRequest(name, startDate, endDate),
        ImmutableMap.of("Session-Key", sessionKey));

    assertThat(response.getStatus()).isEqualTo(200);
    ResponseLeague league = GsonSingleton.fromJson(response.getBody().toString(), ResponseLeague.class);
    assertThat(league.id).matches("[0-9a-f-]+");
    assertThat(league.name).isEqualTo(name);
    assertThat(league.startDate).isEqualTo(startDate);
    assertThat(league.endDate).isEqualTo(endDate);
    assertThat(league.managers).hasSize(1);
    assertThat(league.managers.get(0).displayName).matches("[0-9a-zA-Z]+");
    assertThat(league.managers.get(0).accountId).matches("[0-9a-f-]+");
    assertThat(league.managers.get(0).leagueId).isEqualTo(league.id);
    assertThat(league.managers.get(0).leagueRole).isEqualTo(LeagueRole.OWNER);
    assertThat(league.managers.get(0).score).isEqualTo(0);
    return league;
  }

  public static void inviteManager(String leagueId, String sessionKey, String inviteeAccountid) throws Exception
  {
    InviteManagerRequest request = new InviteManagerRequest(inviteeAccountid);

    HttpResponse<String> response = makePostRequest("/league/" + leagueId + "/manager", request, ImmutableMap.of("Session-Key", sessionKey));

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getBody().toString()).isEqualTo("");
  }

  public static ResponseLeague getLeague(String id) throws Exception
  {
    HttpResponse<String> response = makeGetRequest("/league/" + id);
    assertThat(response.getStatus()).isEqualTo(200);
    return GsonSingleton.fromJson(response.getBody().toString(), ResponseLeague.class);
  }

  public static List<ResponseManager> getManagersForLeague(String leagueId) throws Exception
  {
    HttpResponse<String> response = makeGetRequest("/league/" + leagueId + "/manager");

    assertThat(response.getStatus()).isEqualTo(200);
    List<ResponseManager> managers = GsonSingleton.fromJsonAsList(response.getBody().toString(), ResponseManager[].class);
    for (ResponseManager manager : managers)
    {
      assertThat(manager.id).matches("[0-9a-f-]+");
      assertThat(manager.displayName).matches("[0-9a-zA-Z]+");
      assertThat(manager.leagueRole).isNotNull();
      assertThat(manager.accountId).matches("[0-9a-f-]+");
      assertThat(manager.leagueId).isEqualTo(leagueId);
      assertThat(manager.score).isNotNull();
    }
    return managers;
  }

  public static void acceptInvite(String leagueId, String sessionKey, String inviteeAccountid) throws Exception
  {
    ResponseManager inviteeManager = getManagersForLeague(leagueId).stream()
        .filter(m -> m.accountId.equals(inviteeAccountid)).findFirst().get();
    ModifyManagerRequest request = new ModifyManagerRequest(LeagueRole.MEMBER);

    HttpResponse<String> response = makePatchRequest("/manager/" + inviteeManager.id, request,
        ImmutableMap.of("Session-Key", sessionKey));

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getBody().toString()).isEqualTo("");
  }

}
