package com.github.malow.FantasyEsports.apidoc.deseralizer;

import com.github.malow.FantasyEsports.apidoc.deseralizer.LogbookDeserializer.Entry;

public class Response
{
  public int status;
  public String body;

  public Response(Entry entry)
  {
    this.status = entry.status;
    this.body = entry.body;
  }
}
