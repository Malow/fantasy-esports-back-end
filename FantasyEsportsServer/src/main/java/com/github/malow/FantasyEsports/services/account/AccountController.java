package com.github.malow.FantasyEsports.services.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.malow.FantasyEsports.apidoc.ApiDoc;
import com.github.malow.FantasyEsports.services.Controller;
import com.github.malow.FantasyEsports.services.HttpResponseException;
import com.github.malow.FantasyEsports.services.account.requests.FindAccountRequest;
import com.github.malow.FantasyEsports.services.account.requests.LoginRequest;
import com.github.malow.FantasyEsports.services.account.requests.ModifyAccountRequest;
import com.github.malow.FantasyEsports.services.account.requests.RegisterRequest;
import com.github.malow.FantasyEsports.services.account.responses.FindAccountResponse;
import com.github.malow.FantasyEsports.services.account.responses.LoginResponse;
import com.github.malow.FantasyEsports.services.account.responses.ResponseAccount;
import com.github.malow.malowlib.GsonSingleton;

@ApiDoc("Represents user accounts")
@CrossOrigin(maxAge = 3600)
@RestController
public class AccountController extends Controller
{
  @Autowired
  private AccountService accountService;

  @ApiDoc("Registers an acocunt for a user")
  @PostMapping(value = { "/account/register" })
  public ResponseEntity<String> register(@RequestBody String payload)
  {
    try
    {
      RegisterRequest request = this.getValidRequest(payload, RegisterRequest.class);
      LoginResponse response = this.accountService.register(request);
      return ResponseEntity.ok(GsonSingleton.toJson(response));
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }

  @ApiDoc("Logs a user in")
  @PostMapping(value = { "/account/login" })
  public ResponseEntity<String> login(@RequestBody String payload)
  {
    try
    {
      LoginRequest request = this.getValidRequest(payload, LoginRequest.class);
      LoginResponse response = this.accountService.login(request);
      return ResponseEntity.ok(GsonSingleton.toJson(response));
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }

  @ApiDoc("Logs a user out")
  @PostMapping(value = { "/account/logout" })
  public ResponseEntity<String> logout(@RequestHeader(value = "Session-Key", required = false) String sessionKey)
  {
    try
    {
      Account account = this.accountService.authorize(sessionKey);
      this.accountService.logout(account);
      return ResponseEntity.ok("");
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }

  @ApiDoc("Returns a logged-in user's own account")
  @GetMapping(value = { "/account" })
  public ResponseEntity<String> getOwnAccount(@RequestHeader(value = "Session-Key", required = false) String sessionKey)
  {
    try
    {
      Account account = this.accountService.authorize(sessionKey);
      return ResponseEntity.ok(GsonSingleton.toJson(new ResponseAccount(account)));
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }

  @ApiDoc("Returns a list of accounts where the displayName contains the string provided")
  @RequestMapping(value = "/account/find", method = RequestMethod.GET)
  public ResponseEntity<String> findAccount(FindAccountRequest request)
  {
    try
    {
      request.validate();
      String displayName = request.displayName;
      List<Account> accounts = this.accountService.findAccount(displayName);
      return ResponseEntity.ok(GsonSingleton.toJson(new FindAccountResponse(accounts)));
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }

  @ApiDoc("Returns a specific account with the provided id")
  @GetMapping(value = { "/account/{accountId}" })
  public ResponseEntity<String> getAccount(@PathVariable String accountId)
  {
    try
    {
      Account account = this.accountService.getAccount(accountId);
      return ResponseEntity.ok(GsonSingleton.toJson(new ResponseAccount(account)));
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }

  @ApiDoc("Updates details for a logged-in user's account")
  @PatchMapping(value = { "/account" })
  public ResponseEntity<String> modifyAccount(@RequestBody String payload, @RequestHeader(value = "Session-Key", required = false) String sessionKey)
  {
    try
    {
      Account account = this.accountService.authorize(sessionKey);
      ModifyAccountRequest request = this.getValidRequest(payload, ModifyAccountRequest.class);
      this.accountService.modifyAccount(request, account);
      return ResponseEntity.ok("");
    }
    catch (HttpResponseException e)
    {
      return this.handleHttpResponseException(e);
    }
  }
}