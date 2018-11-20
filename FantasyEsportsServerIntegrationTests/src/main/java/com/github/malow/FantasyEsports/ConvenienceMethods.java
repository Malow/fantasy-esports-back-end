package com.github.malow.FantasyEsports;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;

import com.github.malow.FantasyEsports.FantasyEsportsTestFixture.TestUser;
import com.github.malow.FantasyEsports.services.account.requests.LoginRequest;
import com.github.malow.FantasyEsports.services.account.requests.RegisterRequest;
import com.github.malow.FantasyEsports.services.account.responses.LoginResponse;
import com.github.malow.FantasyEsports.services.league.League;
import com.github.malow.FantasyEsports.services.league.requests.CreateLeagueRequest;
import com.github.malow.FantasyEsports.services.league.requests.InviteManagerRequest;
import com.github.malow.malowlib.GsonSingleton;
import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class ConvenienceMethods
{
  public static LoginResponse login(TestUser user) throws Exception
  {
    String responseBody = Unirest.post(Config.HOST + "/account/login").body(GsonSingleton.toJson(new LoginRequest(user.email, user.password)))
        .asJson().getBody()
        .toString();
    LoginResponse response = GsonSingleton.fromJson(responseBody, LoginResponse.class);
    assertThat(response.sessionKey).matches("[0-9a-f-]+");
    assertThat(response.accountId).matches("[0-9a-f-]+");
    return response;
  }

  public static LoginResponse register(TestUser user) throws Exception
  {
    String responseBody = Unirest.post(Config.HOST + "/account/register")
        .body(GsonSingleton.toJson(new RegisterRequest(user.email, user.displayName, user.password))).asJson().getBody()
        .toString();

    LoginResponse response = GsonSingleton.fromJson(responseBody, LoginResponse.class);
    assertThat(response.sessionKey).matches("[0-9a-f-]+");
    assertThat(response.accountId).matches("[0-9a-f-]+");
    return response;
  }

  public static String createLeague(String name, String sessionKey) throws Exception
  {
    return createLeague(name, ZonedDateTime.now().plusHours(1), ZonedDateTime.now().plusMonths(1), sessionKey);
  }

  public static String createLeague(String name, ZonedDateTime startDate, ZonedDateTime endDate, String sessionKey) throws Exception
  {
    String responseBody = Unirest.post(Config.HOST + "/league").header("Session-Key", sessionKey)
        .body(GsonSingleton.toJson(new CreateLeagueRequest(name, startDate, endDate))).asJson()
        .getBody().toString();
    League league = GsonSingleton.fromJson(responseBody, League.class);
    assertThat(league.getId()).matches("[0-9a-f-]+");
    assertThat(league.getName()).isEqualTo(name);
    assertThat(league.getStartDate()).isEqualTo(startDate);
    assertThat(league.getEndDate()).isEqualTo(endDate);
    return league.getId();
  }

  public static void inviteManager(String leagueId, String sessionKey, String inviteeAccountid) throws Exception
  {
    InviteManagerRequest request = new InviteManagerRequest(inviteeAccountid);

    HttpResponse<String> response = Unirest.post(Config.HOST + "/league/" + leagueId + "/manager").headers(ImmutableMap.of("Session-Key", sessionKey))
        .body(GsonSingleton.toJson(request)).asString();

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getBody().toString()).isEqualTo("");
  }
}
