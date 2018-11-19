package com.github.malow.FantasyEsports.apidoc.deseralizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.malow.malowlib.GsonSingleton;
import com.github.malow.malowlib.MaloWLogger;

public class LogbookDeserializer
{
  public static class RequestResponsePair
  {
    @Override
    public boolean equals(Object obj)
    {
      if (GsonSingleton.toJson(this).equals(GsonSingleton.toJson(obj)))
      {
        return true;
      }
      return false;
    }

    public String path;
    public String method;

    public Request request;
    public Response response;

    public RequestResponsePair(Entry e1, Entry e2)
    {
      if (e1.type.equals("request"))
      {
        this.path = e1.uri.replaceFirst(".*:[1-9]...", "");
        this.method = e1.method;
        this.request = new Request(e1);
        this.response = new Response(e2);
      }
      else
      {
        this.path = e2.uri.replaceFirst(".*:[1-9]...", "");
        this.method = e2.method;
        this.request = new Request(e2);
        this.response = new Response(e1);
      }
    }
  }

  public static class Entry
  {
    public String type;
    public String correlation;
    public String method;
    public String uri;
    public String body;
    public Integer status;
    public Map<String, String[]> headers;
  }

  public static List<RequestResponsePair> deserialize(List<String> stringLines)
  {
    List<RequestResponsePair> requestResponsePairs = new ArrayList<>();
    List<Entry> entries = stringLines.stream().map(s -> s.replaceAll(".*Logbook              : ", ""))
        .map(s -> GsonSingleton.fromJson(s, Entry.class))
        .collect(Collectors.toList());

    while (entries.size() > 0)
    {
      Entry entry1 = entries.remove(0);
      Optional<Entry> entry2 = entries.stream().filter(e -> e.correlation.equals(entry1.correlation)).findFirst();
      if (!entry2.isPresent())
      {
        MaloWLogger.warning("only found 1 for corell: " + entry1.correlation);
        continue;
      }
      entries.remove(entry2.get());
      requestResponsePairs.add(new RequestResponsePair(entry1, entry2.get()));
    }

    Map<String, Map<String, List<RequestResponsePair>>> mappedStuff = new HashMap<>();

    requestResponsePairs.stream().forEach(r ->
    {
      if (!mappedStuff.containsKey(r.path))
      {
        mappedStuff.put(r.path, new HashMap<>());
      }

      Map<String, List<RequestResponsePair>> uriMap = mappedStuff.get(r.path);

      if (!uriMap.containsKey(r.method))
      {
        uriMap.put(r.method, new ArrayList<>());
      }

      List<RequestResponsePair> uriMethodList = uriMap.get(r.method);

      if (!uriMethodList.contains(r))
      {
        uriMethodList.add(r);
      }

      uriMap.put(r.method, uriMethodList);
      mappedStuff.put(r.path, uriMap);
    });
    return requestResponsePairs;
  }
}
