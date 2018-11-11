package com.github.malow.FantasyEsports.services.league;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.malow.FantasyEsports.services.HttpResponseException;
import com.github.malow.FantasyEsports.services.HttpResponseException.UnauthorizedException;
import com.github.malow.FantasyEsports.services.account.Account;
import com.github.malow.FantasyEsports.services.league.requests.CreateLeagueRequest;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.CreateNameTakenException;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.NoLeagueFoundException;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.UserIsAlreadyMemberInLeagueException;

@Component("LeagueServiceBeanName")
public class LeagueService
{
  @Autowired
  private LeagueRepository leagueRepository;
  @Autowired
  private ManagerRepository managerRepository;

  public League createLeague(CreateLeagueRequest request, Account account) throws HttpResponseException
  {
    if (this.leagueRepository.findByName(request.name) != null)
    {
      throw new CreateNameTakenException();
    }
    League league = new League(request.name, request.startDate, request.endDate);
    league = this.leagueRepository.insert(league);
    Manager manager = new Manager(account.getId(), league.getId(), LeagueRole.OWNER);
    manager = this.managerRepository.insert(manager);
    return league;
  }

  public League getLeague(String id) throws NoLeagueFoundException
  {
    Optional<League> league = this.leagueRepository.findById(id);
    if (league.isPresent())
    {
      return league.get();
    }
    throw new NoLeagueFoundException();
  }

  public List<Manager> getManagersForLeague(String leagueId) throws NoLeagueFoundException
  {
    League league = this.getLeague(leagueId);
    return this.managerRepository.findByLeagueId(league.getId());
  }

  public List<Manager> getManagersForLeague(League league) throws NoLeagueFoundException
  {
    return this.managerRepository.findByLeagueId(league.getId());
  }

  public void inviteManager(Account inviteeAccount, String leagueId, Account inviterAccount)
      throws NoLeagueFoundException, UserIsAlreadyMemberInLeagueException, UnauthorizedException
  {
    League league = this.getLeague(leagueId);
    List<Manager> managers = this.getManagersForLeague(league);
    if (!managers.stream().anyMatch(m -> m.getAccountId().equals(inviterAccount.getId()) && m.getLeagueRole().equals(LeagueRole.OWNER)))
    {
      throw new UnauthorizedException();
    }
    if (managers.stream().anyMatch(m -> m.getAccountId().equals(inviteeAccount.getId())))
    {
      throw new UserIsAlreadyMemberInLeagueException();
    }
    Manager manager = new Manager(inviteeAccount.getId(), league.getId(), LeagueRole.INVITED);
    manager = this.managerRepository.insert(manager);
  }
}
