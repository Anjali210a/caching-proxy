package com.example.cachingproxy.model;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CacheEntry {
  private final HttpStatus status;
  private final HttpHeaders headers;
  private final byte[] body;
}