# URL Shortener Service

A high-performance URL shortener service with caching support using Redis.

## Features
- Generate short URLs for given long URLs.
- Retrieve original URLs from shortened ones.
- Fast performance with Redis caching.
- Scalable architecture to handle high traffic.

## Tech Stack
- **Backend**: Spring Boot
- **Database**: PostgreSQL
- **Cache**: Redis
- **ORM**: Hibernate
- **Build Tool**: Maven

## How It Works
1. **Shorten URL**:
    - A long URL is submitted via the `/shorten` API endpoint.
    - A unique hash is generated for the URL.
    - The mapping is stored in both PostgreSQL and Redis.

2. **Resolve URL**:
    - When a shortened URL is accessed, Redis is queried first.
    - If Redis has the mapping, the original URL is returned immediately.
    - If not, the database is queried, and the result is cached in Redis.

3. **Caching with Redis**:
    - Redis is used to store mappings for quick lookups.
    - Cached entries may expire after a configurable TTL (time-to-live).

## Traffic Handling
- **Read-Heavy**: Optimized for resolving URLs quickly using Redis.
- **Write Operations**: Less frequent, handled by both Redis and PostgreSQL.

## Endpoints
### Shorten URL
- **POST** `/shorten`
- **Request**:
  ```json
  {
    "url": "https://example.com"
  }
- **Response**:
- ```json
  {
  "shortenedUrl": "http://short.ly/abc123"
  }

### Resolve URL
- **GET** /resolve/{shortenedUrl}
- ```json
  
   {
   "shortenedUrl": "http://short.ly/abc123"
   }
### Response
- ```json
  {
  "originalUrl": "https://example.com"
   }

### Reference for System Design, here are some useful links:
    -[System Design- AlgoMaster](https://blog.algomaster.io/p/design-a-url-shortener)
    -[System Design- GeeksforGeeks](https://www.geeksforgeeks.org/system-design-url-shortening-service/)
    -[System Design- systemdesign.one](https://systemdesign.one/url-shortening-system-design/)


