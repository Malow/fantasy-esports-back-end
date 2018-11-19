package com.github.malow.FantasyEsports.services.account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.malow.FantasyEsports.services.HttpResponseException;
import com.github.malow.FantasyEsports.services.HttpResponseException.UnauthorizedException;
import com.github.malow.FantasyEsports.services.account.requests.LoginRequest;
import com.github.malow.FantasyEsports.services.account.requests.ModifyAccountRequest;
import com.github.malow.FantasyEsports.services.account.requests.RegisterRequest;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.AccountNotFoundException;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.DisplayNameTakenException;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.EmailNotRegisteredException;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.EmailTakenException;
import com.github.malow.FantasyEsports.services.account.responses.AccountExceptions.WrongPasswordException;
import com.github.malow.FantasyEsports.services.account.responses.LoginResponse;

@Component
public class AccountService
{
  @Autowired
  private AccountRepository repository;

  public Account authorize(String sessionKey) throws UnauthorizedException
  {
    if (sessionKey == null || sessionKey.isEmpty())
    {
      throw new UnauthorizedException();
    }
    Account account = this.repository.findBySessionKey(sessionKey);
    if (account == null)
    {
      throw new UnauthorizedException();
    }
    return account;
  }

  public LoginResponse register(RegisterRequest request) throws HttpResponseException
  {
    if (this.repository.findByEmail(request.email) != null)
    {
      throw new EmailTakenException();
    }
    if (this.repository.findByDisplayName(request.displayName) != null)
    {
      throw new DisplayNameTakenException();
    }
    Account account = new Account(request.email, request.displayName, PasswordHandler.hashPassword(request.password));
    String sessionKey = UUID.randomUUID().toString();
    account.setSessionKey(sessionKey);
    account = this.repository.insert(account);
    return new LoginResponse(sessionKey, account.getId());
  }

  public LoginResponse login(LoginRequest request) throws HttpResponseException
  {
    Account account = this.repository.findByEmail(request.email);
    if (account == null)
    {
      throw new EmailNotRegisteredException();
    }
    if (PasswordHandler.checkPassword(request.password, account.getPassword()))
    {
      String sessionKey = account.getSessionKey();
      if (sessionKey == null)
      {
        sessionKey = UUID.randomUUID().toString();
      }
      account.setSessionKey(sessionKey);
      account = this.repository.save(account);
      return new LoginResponse(sessionKey, account.getId());
    }
    else
    {
      throw new WrongPasswordException();
    }
  }

  public void modifyAccount(ModifyAccountRequest request, Account account) throws HttpResponseException
  {
    if (!PasswordHandler.checkPassword(request.currentPassword, account.getPassword()))
    {
      throw new WrongPasswordException();
    }

    if (request.password != null)
    {
      account.setPassword(PasswordHandler.hashPassword(request.password));
    }

    if (request.email != null)
    {
      if (this.repository.findByEmail(request.email) != null)
      {
        throw new EmailTakenException();
      }
      else
      {
        account.setEmail(request.email);
      }
    }

    if (request.displayName != null)
    {
      if (this.repository.findByDisplayName(request.displayName) != null)
      {
        throw new DisplayNameTakenException();
      }
      else
      {
        account.setDisplayName(request.displayName);
      }
    }

    account = this.repository.save(account);
  }

  public Account getAccount(String id) throws AccountNotFoundException
  {
    Optional<Account> account = this.repository.findById(id);
    if (account.isPresent())
    {
      return account.get();
    }
    throw new AccountNotFoundException();
  }

  public List<Account> findAccount(String displayName)
  {
    return this.repository.findByDisplayNameIgnoreCaseContaining(displayName);
  }
}
