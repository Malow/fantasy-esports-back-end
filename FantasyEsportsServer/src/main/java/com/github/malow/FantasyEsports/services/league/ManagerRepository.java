package com.github.malow.FantasyEsports.services.league;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ManagerRepository extends MongoRepository<Manager, String>
{
  public List<Manager> findByAccountId(String id);

  public List<Manager> findByLeagueId(String id);
}
