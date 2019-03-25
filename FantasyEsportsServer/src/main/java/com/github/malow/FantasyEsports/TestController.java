package com.github.malow.FantasyEsports;

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
import com.github.malow.FantasyEsports.services.account.AccountRepository;
import com.github.malow.FantasyEsports.services.account.AccountService;
import com.github.malow.FantasyEsports.services.account.requests.RegisterRequest;
import com.github.malow.FantasyEsports.services.league.LeagueRepository;
import com.github.malow.FantasyEsports.services.manager.ManagerRepository;
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
      for (int i = 1; i < 6; i++)
      {
        RegisterRequest registerRequest = new RegisterRequest("test" + i + "@gmail.com", "test" + i, "test" + i);
        this.accountService.register(registerRequest);
      }
    }
    catch (HttpResponseException e)
    {
      MaloWLogger.error("Unexpeceted error when seeding database: ", e);
    }

    return ResponseEntity.ok("");
  }
}
