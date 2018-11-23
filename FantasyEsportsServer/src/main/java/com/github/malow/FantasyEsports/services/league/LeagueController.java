package com.github.malow.FantasyEsports.services.league;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.malow.FantasyEsports.apidoc.ApiDoc;
import com.github.malow.FantasyEsports.services.Controller;
import com.github.malow.FantasyEsports.services.HttpResponseException;
import com.github.malow.FantasyEsports.services.HttpResponseException.IllegalValueException;
import com.github.malow.FantasyEsports.services.account.Account;
import com.github.malow.FantasyEsports.services.account.AccountService;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.AccountNotFoundException;
import com.github.malow.FantasyEsports.services.account.responses.ResponseLeague;
import com.github.malow.FantasyEsports.services.account.responses.ResponseManager;
import com.github.malow.FantasyEsports.services.league.requests.CreateLeagueRequest;
import com.github.malow.FantasyEsports.services.league.requests.InviteManagerRequest;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.NoLeagueFoundException;
import com.github.malow.FantasyEsports.services.manager.Manager;
import com.github.malow.FantasyEsports.services.manager.ManagerService;
import com.github.malow.malowlib.GsonSingleton;

@ApiDoc("Represents leagues")
@CrossOrigin(maxAge = 3600)
@RestController
public class LeagueController extends Controller
{
  @Autowired
  private LeagueRepository leagueRepository;
  @Autowired
  private AccountService accountService;
  @Autowired
  private LeagueService leagueService;
  @Autowired
  private ManagerService managerService;

  @ApiDoc("Lists all leagues that the logged in user is related to")
  @GetMapping(value = { "/league" })
  public ResponseEntity<String> listLeagues(@RequestParam(value = "role", required = false) String role,
      @RequestHeader(value = "Session-Key", required = false) String sessionKey)
  {
    try
    {
      Account account = this.accountService.authorize(sessionKey);
      List<Manager> managers = this.leagueService.getManagersForAccount(account);
      if (role != null)
      {
        LeagueRole leagueRole;
        try
        {
          leagueRole = LeagueRole.valueOf(role);
        }
        catch (IllegalArgumentException e)
        {
          throw new IllegalValueException("role");
        }
        managers = managers.stream().filter(m -> m.getLeagueRole().equals(leagueRole)).collect(Collectors.toList());
      }
      List<League> leagues = managers.stream().map(m -> this.leagueService.getLeagueForManager(m))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toList());
      List<ResponseLeague> responseLeagues = new ArrayList<>();
      for (League league : leagues)
      {
        responseLeagues.add(this.convertToResponseLeague(league));
      }
      return ResponseEntity.ok(GsonSingleton.toJson(responseLeagues));
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }

  @ApiDoc("Get details for a specific league with the provided id")
  @GetMapping(value = { "/league/{leagueId}" })
  public ResponseEntity<String> getLeague(@PathVariable String leagueId)
  {
    try
    {
      Optional<League> league = this.leagueRepository.findById(leagueId);
      if (league.isPresent())
      {
        ResponseLeague responseLeague = this.convertToResponseLeague(league.get());
        return ResponseEntity.ok(GsonSingleton.toJson(responseLeague));
      }
      throw new NoLeagueFoundException();
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }

  @ApiDoc("Creates a new league")
  @PostMapping(value = { "/league" })
  public ResponseEntity<String> createLeague(@RequestBody String payload, @RequestHeader(value = "Session-Key", required = false) String sessionKey)
  {
    try
    {
      Account account = this.accountService.authorize(sessionKey);
      CreateLeagueRequest request = this.getValidRequest(payload, CreateLeagueRequest.class);
      League league = this.leagueService.createLeague(request, account);
      ResponseLeague responseLeague = this.convertToResponseLeague(league);
      return ResponseEntity.ok(GsonSingleton.toJson(responseLeague));
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }

  @ApiDoc("Lists all managers for a specific league with the provided id")
  @GetMapping(value = { "/league/{leagueId}/manager" })
  public ResponseEntity<String> getManagers(@PathVariable String leagueId)
  {
    try
    {
      List<ResponseManager> responseManagers = new ArrayList<>();
      for (Manager manager : this.leagueService.getManagersForLeague(leagueId))
      {
        responseManagers.add(this.managerService.convertToResponseManager(manager));
      }
      return ResponseEntity.ok(GsonSingleton.toJson(responseManagers));
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }

  @ApiDoc("Invites a new manager to the specific league with the provided id")
  @PostMapping(value = { "/league/{leagueId}/manager" })
  public ResponseEntity<String> inviteManager(@RequestBody String payload, @PathVariable String leagueId,
      @RequestHeader(value = "Session-Key", required = false) String sessionKey)
  {
    try
    {
      Account inviterAccount = this.accountService.authorize(sessionKey);
      InviteManagerRequest request = this.getValidRequest(payload, InviteManagerRequest.class);
      Account inviteeAccount = this.accountService.getAccount(request.inviteeAccountId);
      this.leagueService.inviteManager(inviteeAccount, leagueId, inviterAccount);
      return ResponseEntity.ok("");
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }

  /*
   *
   */

  private ResponseLeague convertToResponseLeague(League league) throws NoLeagueFoundException, AccountNotFoundException
  {
    List<ResponseManager> responseManagers = new ArrayList<>();
    for (Manager manager : this.leagueService.getManagersForLeague(league))
    {
      responseManagers.add(this.managerService.convertToResponseManager(manager));
    }
    return new ResponseLeague(league, responseManagers);
  }
}
