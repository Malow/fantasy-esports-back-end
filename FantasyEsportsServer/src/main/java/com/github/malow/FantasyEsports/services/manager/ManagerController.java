package com.github.malow.FantasyEsports.services.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.github.malow.FantasyEsports.apidoc.ApiDoc;
import com.github.malow.FantasyEsports.services.Controller;
import com.github.malow.FantasyEsports.services.HttpResponseException;
import com.github.malow.FantasyEsports.services.account.Account;
import com.github.malow.FantasyEsports.services.account.AccountService;
import com.github.malow.FantasyEsports.services.account.responses.ResponseManager;
import com.github.malow.FantasyEsports.services.manager.requests.ModifyManagerRequest;
import com.github.malow.malowlib.GsonSingleton;

@ApiDoc("Represents the connection between a user and a league")
@CrossOrigin(maxAge = 3600)
@RestController
public class ManagerController extends Controller
{
  @Autowired
  private ManagerService managerService;
  @Autowired
  private AccountService accountService;

  @ApiDoc("Get details for a specific manager with the provided id")
  @GetMapping(value = { "/manager/{managerId}" })
  public ResponseEntity<String> getManager(@PathVariable String managerId)
  {
    try
    {
      Manager manager = this.managerService.getManager(managerId);
      ResponseManager responseManager = this.managerService.convertToResponseManager(manager);
      return ResponseEntity.ok(GsonSingleton.toJson(responseManager));
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }

  @ApiDoc("Modifies a manager")
  @PatchMapping(value = { "/manager/{managerId}" })
  public ResponseEntity<String> modifyManager(@PathVariable String managerId, @RequestBody String payload,
      @RequestHeader(value = "Session-Key", required = false) String sessionKey)
  {
    try
    {
      Account account = this.accountService.authorize(sessionKey);
      ModifyManagerRequest request = this.getValidRequest(payload, ModifyManagerRequest.class);
      this.managerService.modifyManager(managerId, request, account);
      return ResponseEntity.ok("");
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }
}
