package com.github.malow.FantasyEsports.services.league;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.malow.FantasyEsports.services.HttpResponseException;
import com.github.malow.FantasyEsports.services.account.Account;
import com.github.malow.FantasyEsports.services.league.requests.CreateLeagueRequest;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.CreateNameTakenException;

@Component("LeagueServiceBeanName")
public class LeagueService
{
  @Autowired
  private LeagueRepository leagueRepository;

  public void createLeague(CreateLeagueRequest request, Account account) throws HttpResponseException
  {
    if (this.leagueRepository.findByName(request.name) != null)
    {
      throw new CreateNameTakenException();
    }
    League league = new League(request.name, account.getDisplayName(), request.startDate, request.endDate);
    this.leagueRepository.insert(league);
  }
}
