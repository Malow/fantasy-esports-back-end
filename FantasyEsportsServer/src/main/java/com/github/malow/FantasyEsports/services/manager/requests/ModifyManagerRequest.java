package com.github.malow.FantasyEsports.services.manager.requests;

import com.github.malow.FantasyEsports.services.Request;
import com.github.malow.FantasyEsports.services.league.LeagueRole;

public class ModifyManagerRequest extends Request
{
  public LeagueRole leagueRole;

  public ModifyManagerRequest(LeagueRole leagueRole)
  {
    this.leagueRole = leagueRole;
  }
}
