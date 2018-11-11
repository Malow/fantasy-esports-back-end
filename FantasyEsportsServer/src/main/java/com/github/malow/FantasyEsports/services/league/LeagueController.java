package com.github.malow.FantasyEsports.services.league;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.github.malow.FantasyEsports.services.Controller;
import com.github.malow.FantasyEsports.services.HttpResponseException;
import com.github.malow.FantasyEsports.services.account.Account;
import com.github.malow.FantasyEsports.services.account.AccountService;
import com.github.malow.FantasyEsports.services.league.requests.CreateLeagueRequest;
import com.github.malow.FantasyEsports.services.league.requests.InviteManagerRequest;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.NoLeagueFoundException;
import com.github.malow.malowlib.GsonSingleton;

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

  @GetMapping(value = { "/league" })
  public ResponseEntity<String> listLeagues()
  {
    List<League> leagues = this.leagueRepository.findAll();
    return ResponseEntity.ok(GsonSingleton.toJson(leagues));
  }

  @GetMapping(value = { "/league/{id}" })
  public ResponseEntity<String> getLeague(@PathVariable String id)
  {
    try
    {
      Optional<League> league = this.leagueRepository.findById(id);
      if (league.isPresent())
      {
        return ResponseEntity.ok(GsonSingleton.toJson(league.get()));
      }
      throw new NoLeagueFoundException();
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }

  @PostMapping(value = { "/league" })
  public ResponseEntity<String> createLeague(@RequestBody String payload, @RequestHeader(value = "Session-Key", required = false) String sessionKey)
  {
    try
    {
      Account account = this.accountService.authorize(sessionKey);
      CreateLeagueRequest request = this.getValidRequest(payload, CreateLeagueRequest.class);
      League league = this.leagueService.createLeague(request, account);
      return ResponseEntity.ok(GsonSingleton.toJson(league));
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }

  @GetMapping(value = { "/league/{id}/manager" })
  public ResponseEntity<String> getManagers(@PathVariable String id)
  {
    try
    {
      return ResponseEntity.ok(GsonSingleton.toJson(this.leagueService.getManagersForLeague(id)));
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }

  @PostMapping(value = { "/league/{id}/manager" })
  public ResponseEntity<String> inviteManager(@RequestBody String payload, @PathVariable String id,
      @RequestHeader(value = "Session-Key", required = false) String sessionKey)
  {
    try
    {
      Account inviterAccount = this.accountService.authorize(sessionKey);
      InviteManagerRequest request = this.getValidRequest(payload, InviteManagerRequest.class);
      Account inviteeAccount = this.accountService.getAccount(request.inviteeAccountId);
      this.leagueService.inviteManager(inviteeAccount, id, inviterAccount);
      return ResponseEntity.ok("");
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }
}
