package com.github.malow.FantasyEsports.regressiontests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.github.malow.FantasyEsports.FantasyEsportsTestFixture;
import com.github.malow.FantasyEsports.ServerConnection;

public class LeagueTests extends FantasyEsportsTestFixture
{
  @Test
  public void testCreateLeagueSuccessfully() throws Exception
  {
    String responseBody = ServerConnection.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    assertThat(responseBody).isEqualTo("{}");
  }

  @Test
  public void testCreateLeagueWithoutSession() throws Exception
  {
    String responseBody = ServerConnection.createLeague("test123", "");
    assertThat(responseBody).contains("You do not have permission to do that");
    assertThat(responseBody).contains("\"status\":401");
  }

  @Test
  public void testCreateLeagueWithSameName() throws Exception
  {
    ServerConnection.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    String responseBody = ServerConnection.createLeague("test123", PRE_REGISTERED_USER1.sessionKey);
    assertThat(responseBody).contains("Name is already taken");
    assertThat(responseBody).contains("\"status\":400");
  }
}