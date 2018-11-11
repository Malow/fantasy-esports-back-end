package com.github.malow.FantasyEsports.services.league.requests;

import com.github.malow.FantasyEsports.services.Request;

public class InviteManagerRequest extends Request
{
  @Mandatory
  public String inviteeAccountId;

  public InviteManagerRequest(String inviteeAccountId)
  {
    this.inviteeAccountId = inviteeAccountId;
  }
}
