package com.github.malow.FantasyEsports.apidoc.deseralizer;

import java.util.HashMap;
import java.util.Map;

import com.github.malow.FantasyEsports.apidoc.deseralizer.LogbookDeserializer.Entry;
import com.github.malow.malowlib.GsonSingleton;

public class Request
{
  public Map<String, String> headers = new HashMap<>();
  public String body;

  public Request(Entry entry)
  {
    for (Map.Entry<String, String[]> header : entry.headers.entrySet())
    {
      if (header.getKey().equals("Session-Key".toLowerCase()))
      {
        this.headers.put("Session-Key", header.getValue()[0]);
      }
    }
    this.body = entry.body;
  }

  public String generatePrintableString()
  {
    String s = GsonSingleton.toJson(this);
    s = s.replace("\"headers\":{},", "");
    return s;
  }
}
