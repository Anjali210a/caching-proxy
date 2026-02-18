package com.example.cachingproxy.service;

import com.example.cachingproxy.model.CacheEntry;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CacheService {
  private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

  public Optional<CacheEntry> get(String key) {
    return Optional.ofNullable(cache.get(key));
  }

  public void put(String key, CacheEntry entry) {
    cache.put(key, entry);
  }

  public void clear() {
    cache.clear();
  }

  public int size() {
    return cache.size();
  }
}