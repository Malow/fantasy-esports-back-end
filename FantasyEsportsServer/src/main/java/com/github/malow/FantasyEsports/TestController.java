package com.github.malow.FantasyEsports;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.malow.FantasyEsports.apidoc.ApiDoc;
import com.github.malow.FantasyEsports.config.DeployMode;
import com.github.malow.FantasyEsports.config.FantasyEsportsServerConfig;
import com.github.malow.FantasyEsports.services.Controller;
import com.github.malow.FantasyEsports.services.HttpResponseException;
import com.github.malow.FantasyEsports.services.account.Account;
import com.github.malow.FantasyEsports.services.account.AccountRepository;
import com.github.malow.FantasyEsports.services.account.AccountService;
import com.github.malow.FantasyEsports.services.account.requests.RegisterRequest;
import com.github.malow.FantasyEsports.services.account.responses.LoginResponse;
import com.github.malow.FantasyEsports.services.league.League;
import com.github.malow.FantasyEsports.services.league.LeagueRepository;
import com.github.malow.FantasyEsports.services.league.LeagueRole;
import com.github.malow.FantasyEsports.services.league.LeagueService;
import com.github.malow.FantasyEsports.services.league.requests.CreateLeagueRequest;
import com.github.malow.FantasyEsports.services.manager.Manager;
import com.github.malow.FantasyEsports.services.manager.ManagerRepository;
import com.github.malow.FantasyEsports.services.manager.ManagerService;
import com.github.malow.FantasyEsports.services.manager.requests.ModifyManagerRequest;
import com.github.malow.malowlib.MaloWLogger;

@ApiDoc("Contains various methods used during testing that are not available in PRODUCTION-mode.")
@CrossOrigin(maxAge = 3600)
@RestController
public class TestController extends Controller
{
  @Autowired
  private AccountRepository accountRepository;
  @Autowired
  private LeagueRepository leagueRepository;
  @Autowired
  private ManagerRepository managerRepository;

  @Autowired
  private AccountService accountService;
  @Autowired
  private LeagueService leagueService;
  @Autowired
  private ManagerService managerService;

  @ApiDoc("Resets the database used, and pre-seeds it with some data.")
  @GetMapping(value = { "/resetdatabase" })
  public ResponseEntity<String> resetDatabase()
  {
    FantasyEsportsServerConfig config = ConfigSingleton.getConfig();
    if (config.deployMode.equals(DeployMode.PRODUCTION))
    {
      return ResponseEntity.status(404).body("");
    }

    // Reset
    this.accountRepository.deleteAll();
    this.leagueRepository.deleteAll();
    this.managerRepository.deleteAll();

    // Pre-seed
    try
    {
      RegisterRequest registerRequest = new RegisterRequest("owner@gmail.com", "Tony_Stark", "Asdf1234");
      LoginResponse ownerLoginResponse = this.accountService.register(registerRequest);
      registerRequest = new RegisterRequest("member@gmail.com", "Chris_Pratt", "Asdf1234");
      LoginResponse memberLoginResponse = this.accountService.register(registerRequest);
      registerRequest = new RegisterRequest("invited@gmail.com", "Bruce_Banner", "Asdf1234");
      LoginResponse invitedLoginResponse = this.accountService.register(registerRequest);
      registerRequest = new RegisterRequest("not_invited@gmail.com", "Peter_Parker", "Asdf1234");
      LoginResponse notInvitedLoginResponse = this.accountService.register(registerRequest);

      Account ownerAccount = this.accountService.authorize(ownerLoginResponse.sessionKey);
      Account memberAccount = this.accountService.authorize(memberLoginResponse.sessionKey);
      Account invitedAccount = this.accountService.authorize(invitedLoginResponse.sessionKey);
      this.accountService.authorize(notInvitedLoginResponse.sessionKey);

      CreateLeagueRequest createLeagueRequest = new CreateLeagueRequest("The One League", ZonedDateTime.now().plusDays(1),
          ZonedDateTime.now().plusMonths(1).plusDays(1));
      League league = this.leagueService.createLeague(createLeagueRequest, ownerAccount);

      this.leagueService.inviteManager(memberAccount, league.getId(), ownerAccount);
      Manager memberManager = this.leagueService.getManagersForLeague(league).stream().filter(m -> m.getLeagueRole().equals(LeagueRole.INVITED))
          .findFirst()
          .get();
      ModifyManagerRequest modifyManagerRequest = new ModifyManagerRequest(LeagueRole.MEMBER);
      this.managerService.modifyManager(memberManager.getId(), modifyManagerRequest, memberAccount);

      this.leagueService.inviteManager(invitedAccount, league.getId(), ownerAccount);
    }
    catch (HttpResponseException e)
    {
      MaloWLogger.error("Unexpeceted error when seeding database: ", e);
    }

    return ResponseEntity.ok("");
  }
}
