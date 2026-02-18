package com.example.cachingproxy.controller;

import com.example.cachingproxy.model.CacheEntry;
import com.example.cachingproxy.service.CacheService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@RestController
public class ProxyController {

  private final CacheService cacheService;
  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${origin}")
  private String origin;

  public ProxyController(CacheService cacheService) {
    this.cacheService = cacheService;
  }

  // clear-cache endpoint (POST to clear)
  @PostMapping("/clear-cache")
  public ResponseEntity<String> clearCache() {
    cacheService.clear();
    return ResponseEntity.ok("OK");
  }

  @RequestMapping("/**")
  public ResponseEntity<byte[]> proxy(HttpServletRequest request, @RequestBody(required = false) byte[] body) throws IOException {
    String path = request.getRequestURI(); // includes context path
    String query = request.getQueryString();
    String fullUrl = origin + path + (query != null ? "?" + query : "");
    String method = request.getMethod();

    String key = buildCacheKey(method, fullUrl, body);

    // Check cache
    var maybe = cacheService.get(key);
    if (maybe.isPresent()) {
      CacheEntry e = maybe.get();
      HttpHeaders headers = new HttpHeaders();
      headers.putAll(e.getHeaders());
      headers.set("X-Cache", "HIT");
      return new ResponseEntity<>(e.getBody(), headers, e.getStatus());
    }

    // Not in cache -> forward
    HttpMethod httpMethod = HttpMethod.resolve(method);
    HttpHeaders outgoingHeaders = buildForwardHeaders(request);

    HttpEntity<byte[]> outgoingEntity = new HttpEntity<>(body, outgoingHeaders);

    ResponseEntity<byte[]> originResp = restTemplate.exchange(URI.create(fullUrl), httpMethod, outgoingEntity, byte[].class);

    HttpHeaders returnedHeaders = filterHopByHopHeaders(originResp.getHeaders());

    // Add X-Cache: MISS
    returnedHeaders.set("X-Cache", "MISS");

    // Put into cache
    CacheEntry entry = new CacheEntry(originResp.getStatusCode(), returnedHeaders, originResp.getBody());
    cacheService.put(key, entry);

    return new ResponseEntity<>(originResp.getBody(), returnedHeaders, originResp.getStatusCode());
  }

  private HttpHeaders filterHopByHopHeaders(HttpHeaders headers) {
    HttpHeaders copy = new HttpHeaders();
    headers.forEach((k, v) -> {
      String lower = k.toLowerCase(Locale.ROOT);
      if (List.of("connection","keep-alive","proxy-authenticate","proxy-authorization","te","trailers","transfer-encoding","upgrade").contains(lower)) {
        return;
      }
      copy.put(k, v);
    });
    return copy;
  }

  private HttpHeaders buildForwardHeaders(HttpServletRequest request) {
    HttpHeaders headers = new HttpHeaders();
    var headerNames = Collections.list(request.getHeaderNames());
    for (String name : headerNames) {
      if (name.equalsIgnoreCase("host")) continue; // origin will set host
      var values = Collections.list(request.getHeaders(name));
      headers.put(name, values);
    }
    return headers;
  }

  private String buildCacheKey(String method, String url, byte[] body){
    String bodyHash = "";
    if (body != null && body.length>0) {
      try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(body);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b));
        bodyHash = sb.toString();
      } catch (Exception ex) {
        bodyHash = Arrays.toString(body);
      }
    }
    return method + " " + url + " " + bodyHash;
  }
}