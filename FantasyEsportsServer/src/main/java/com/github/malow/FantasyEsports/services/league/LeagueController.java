package com.github.malow.FantasyEsports.services.league;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.github.malow.FantasyEsports.services.Controller;
import com.github.malow.FantasyEsports.services.account.Account;
import com.github.malow.FantasyEsports.services.account.AccountService;
import com.github.malow.FantasyEsports.services.league.requests.CreateLeagueRequest;
import com.github.malow.FantasyEsports.services.league.responses.LeagueExceptions.CreateNameTakenException;
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

  @GetMapping(value = { "/league" })
  public String listLeagues()
  {
    List<League> leagues = this.leagueRepository.findAll();
    return GsonSingleton.toJson(leagues);
  }

  @GetMapping(value = { "/league/{id}" })
  public String getLeague(@PathVariable String id)
  {
    Optional<League> league = this.leagueRepository.findById(id);
    if (league.isPresent())
    {
      return GsonSingleton.toJson(league.get());
    }
    throw new NoLeagueFoundException();
  }

  @PostMapping(value = { "/league" })
  public void createLeague(@RequestBody String payload, @RequestHeader(value = "Session-Key") String sessionKey)
  {
    Account account = this.accountService.authorize(sessionKey);
    CreateLeagueRequest request = this.getValidRequest(payload, CreateLeagueRequest.class);
    if (this.leagueRepository.findByName(request.name) != null)
    {
      throw new CreateNameTakenException();
    }
    League league = new League(request.name, account.getDisplayName(), request.startDate, request.endDate);
    this.leagueRepository.insert(league);
  }
}
