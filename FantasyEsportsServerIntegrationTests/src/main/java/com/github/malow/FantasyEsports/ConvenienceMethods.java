package com.github.malow.FantasyEsports;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.malow.FantasyEsports.FantasyEsportsTestFixture.TestUser;
import com.github.malow.FantasyEsports.services.account.requests.LoginRequest;
import com.github.malow.FantasyEsports.services.account.requests.RegisterRequest;
import com.github.malow.FantasyEsports.services.league.League;
import com.github.malow.FantasyEsports.services.league.requests.CreateLeagueRequest;
import com.github.malow.malowlib.GsonSingleton;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.Unirest;

public class ConvenienceMethods
{
  public static void login(TestUser user) throws Exception
  {
    String responseBody = Unirest.post(Config.HOST + "/account/login").body(GsonSingleton.toJson(new LoginRequest(user.email, user.password)))
        .asJson().getBody()
        .toString();
    assertThat(responseBody).containsPattern("\\{\"sessionKey\":\"([0-9a-f-]+)\"\\}");
  }

  public static String register(TestUser user) throws Exception
  {
    String responseBody = Unirest.post(Config.HOST + "/account/register")
        .body(GsonSingleton.toJson(new RegisterRequest(user.email, user.displayName, user.password))).asJson().getBody()
        .toString();

    assertThat(responseBody).containsPattern("\\{\"sessionKey\":\"([0-9a-f-]+)\"\\}");
    Matcher matcher = Pattern.compile("\\{\"sessionKey\":\"([0-9a-f-]+)\"\\}").matcher(responseBody);
    matcher.find();
    return matcher.group(1);
  }

  public static void createLeague(String name, String sessionKey) throws Exception
  {
    createLeague(name, ZonedDateTime.now().plusHours(1), ZonedDateTime.now().plusMonths(1), sessionKey);
  }

  public static void createLeague(String name, ZonedDateTime startDate, ZonedDateTime endDate, String sessionKey) throws Exception
  {
    String responseBody = Unirest.post(Config.HOST + "/league").header("Session-Key", sessionKey)
        .body(GsonSingleton.toJson(new CreateLeagueRequest(name, startDate, endDate))).asJson()
        .getBody().toString();
    assertThat(responseBody).isEqualTo("{}");
  }

  public static List<League> getLeagues() throws Exception
  {
    return GsonSingleton.fromJson(Unirest.get(Config.HOST + "/league").asJson().getBody().toString(), new TypeToken<ArrayList<League>>()
    {
    }.getType());
  }
}
