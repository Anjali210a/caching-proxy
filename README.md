https://roadmap.sh/projects/caching-server

---

# 🚀 Caching Reverse Proxy Server (Spring Boot)

## 📌 Overview

This project is a **Caching Reverse Proxy Server** built using Spring Boot.

It forwards incoming HTTP requests to an origin server, caches the responses in memory, and serves cached responses for repeated requests to improve performance.

The proxy also adds a custom response header:

- `X-Cache: MISS` → First request (fetched from origin)
- `X-Cache: HIT` → Returned from cache

---

## 🧠 Problem It Solves

Calling backend APIs repeatedly increases:
- Server load
- Latency
- Infrastructure cost

This proxy reduces redundant calls by caching responses.

---

## 🏗 Architecture

Client → Proxy Server → Origin Server  
Client ← Proxy (Cache HIT) ←

The proxy:
1. Receives request
2. Checks cache
3. If present → returns cached response
4. If not present → forwards to origin
5. Stores response in cache
6. Returns response to client

---

## ⚙️ Tech Stack

- Java 17
- Spring Boot 3
- Maven
- ConcurrentHashMap (In-memory cache)
- REST API

---

## 📂 Project Structure

```
src/main/java/com/example/cachingproxy
 ├── Application.java
 ├── controller/ProxyController.java
 ├── service/CacheService.java
 └── model/CacheEntry.java
```

---

## ▶️ How To Run

### 1️⃣ Build Project

```
mvn clean package
```

### 2️⃣ Run Application

```
java -jar target/caching-proxy-0.0.1-SNAPSHOT.jar --server.port=3000 --origin=http://dummyjson.com
```

---

## 🧪 Testing With Curl

### First Request (MISS)

```
curl -i http://localhost:3000/products
```

Response Header:
```
X-Cache: MISS
```

### Second Request (HIT)

```
curl -i http://localhost:3000/products
```

Response Header:
```
X-Cache: HIT
```

---

## 🧹 Clear Cache

```
curl -X POST http://localhost:3000/clear-cache
```

---

## 🔑 Key Concepts Implemented

- Reverse Proxy
- HTTP Forwarding
- Request/Response header handling
- In-memory caching
- Thread-safe data structure (ConcurrentHashMap)
- Custom response headers
- Cache invalidation

---

## 🚀 Possible Improvements

- Add TTL (Time To Live)
- Add cache size limit (LRU)
- Use Redis for distributed caching
- Add logging & monitoring
- Add authentication for clear-cache endpoint

---

## 🎯 Interview Highlights

This project demonstrates understanding of:

- How HTTP works
- Reverse proxy architecture
- Concurrency in Spring Boot
- Caching strategies
- Backend system design basics

---

## 👨‍💻 Author

Your Name
](https://roadmap.sh/projects/caching-server)
](https://roadmap.sh/projects/caching-server)](https://roadmap.sh/projects/caching-server)
