package com.github.malow.FantasyEsports.services.manager;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.malow.FantasyEsports.services.HttpResponseException.ForbiddenException;
import com.github.malow.FantasyEsports.services.HttpResponseException.IllegalValueException;
import com.github.malow.FantasyEsports.services.HttpResponseException.NoChangeMadeException;
import com.github.malow.FantasyEsports.services.account.Account;
import com.github.malow.FantasyEsports.services.account.AccountService;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.AccountNotFoundException;
import com.github.malow.FantasyEsports.services.account.responses.ResponseManager;
import com.github.malow.FantasyEsports.services.league.LeagueRole;
import com.github.malow.FantasyEsports.services.manager.requests.ModifyManagerRequest;
import com.github.malow.FantasyEsports.services.manager.responses.ManagerExceptions.NoManagerFoundException;

@Component
public class ManagerService
{
  @Autowired
  private ManagerRepository managerRepository;
  @Autowired
  private AccountService accountService;

  public ResponseManager convertToResponseManager(Manager manager) throws AccountNotFoundException
  {
    Account account = this.accountService.getAccount(manager.getAccountId());
    return new ResponseManager(manager, account.getDisplayName());
  }

  public Manager getManager(String managerId) throws NoManagerFoundException
  {
    Optional<Manager> manager = this.managerRepository.findById(managerId);
    if (manager.isPresent())
    {
      return manager.get();
    }
    throw new NoManagerFoundException();
  }

  public void modifyManager(String managerId, ModifyManagerRequest request, Account account)
      throws NoManagerFoundException, IllegalValueException, NoChangeMadeException, ForbiddenException
  {
    Manager manager = this.getManager(managerId);
    if (request.leagueRole != null)
    {
      if (manager.getAccountId().equals(account.getId())) // Trying to modify self
      {
        if (manager.getLeagueRole().equals(LeagueRole.INVITED) && request.leagueRole.equals(LeagueRole.MEMBER)) // Accepting invite
        {
          manager.setLeagueRole(LeagueRole.MEMBER);
        }
        else
        {
          throw new IllegalValueException("leagueRole");
        }
      }
      else
      {
        throw new ForbiddenException();
      }
    } // else if for other modifications
    else
    {
      throw new NoChangeMadeException();
    }
    this.managerRepository.save(manager);
  }
}
