# Nginx Reverse Proxy Configuration

## What Is a Reverse Proxy?

A **forward proxy** sits in front of **clients** — it makes requests on behalf of users (like a VPN or corporate proxy). The server doesn't know who the real client is.

A **reverse proxy** sits in front of **servers** — it receives requests from the internet and forwards them to backend services. The client doesn't know which server actually handled the request.

```
Forward Proxy:   Client → [Proxy] → Internet → Server
                 (hides the client)

Reverse Proxy:   Client → Internet → [Proxy] → Server
                 (hides the server)
```

It's called "reverse" because it's the opposite direction — instead of proxying outbound requests for clients, it proxies inbound requests for servers.

### Why Use a Reverse Proxy?

1. **SSL Termination** — Nginx handles HTTPS encryption/decryption so the Spring Boot app only deals with plain HTTP internally
2. **Security** — The backend app is never directly exposed to the internet. Only Nginx listens on ports 80/443
3. **Security Headers** — Centralized place to add headers like HSTS, X-Frame-Options, etc.
4. **File Upload Limits** — Nginx can reject oversized requests before they reach the app
5. **Future Flexibility** — Can add load balancing, rate limiting, static file serving, or multiple backends without changing the app

---

## Configuration Breakdown

### Server Block 1: HTTP (Port 80)

```nginx
server {
    listen 80;
    server_name api.caseyrquinn.com;

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        return 301 https://$host$request_uri;
    }
}
```

**What it does:**
- Listens on port 80 (plain HTTP) for requests to `api.caseyrquinn.com`
- The `/.well-known/acme-challenge/` location serves files that Certbot places in `/var/www/certbot` during SSL certificate renewal. This is how Let's Encrypt verifies you own the domain (called the "HTTP-01 challenge")
- Everything else gets a **301 permanent redirect** to HTTPS. This ensures no one accidentally uses the unencrypted version

**Why 301 and not 302?**
- 301 (permanent) tells browsers and search engines to always use HTTPS going forward. They'll cache this and never try HTTP again
- 302 (temporary) would cause browsers to check HTTP every time before redirecting

---

### Server Block 2: HTTPS (Port 443)

```nginx
server {
    listen 443 ssl;
    http2 on;
    server_name api.caseyrquinn.com;
```

- Listens on port 443 with SSL enabled
- `http2 on` enables HTTP/2, which is faster than HTTP/1.1 (multiplexed requests, header compression, binary protocol)

#### SSL Certificate Paths

```nginx
    ssl_certificate /etc/letsencrypt/live/api.caseyrquinn.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.caseyrquinn.com/privkey.pem;
```

- `fullchain.pem` — the SSL certificate + intermediate certificates (the full chain of trust)
- `privkey.pem` — the private key that proves you own the certificate
- These are managed by Certbot and auto-renewed. See [CERTBOT_SSL.md](CERTBOT_SSL.md) for details

#### SSL Hardening

```nginx
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
```

- **`ssl_protocols`** — Only allows TLS 1.2 and 1.3. Older versions (SSL 3.0, TLS 1.0, TLS 1.1) have known vulnerabilities and are disabled
- **`ssl_ciphers HIGH:!aNULL:!MD5`** — Uses only high-strength cipher suites. `!aNULL` excludes ciphers with no authentication (vulnerable to man-in-the-middle). `!MD5` excludes the broken MD5 hash algorithm
- **`ssl_prefer_server_ciphers on`** — The server chooses the cipher suite, not the client. This prevents clients from negotiating a weaker cipher

#### Security Headers

```nginx
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
```

| Header | Purpose |
|--------|---------|
| `X-Frame-Options: SAMEORIGIN` | Prevents the site from being embedded in iframes on other domains (clickjacking protection) |
| `X-Content-Type-Options: nosniff` | Prevents browsers from guessing (MIME-sniffing) the content type. Forces them to trust the `Content-Type` header |
| `X-XSS-Protection: 1; mode=block` | Enables the browser's built-in XSS filter. If an attack is detected, the page is blocked entirely |
| `Strict-Transport-Security` | Tells browsers to only use HTTPS for the next 31,536,000 seconds (1 year). `includeSubDomains` applies this to all subdomains too. This is called HSTS |

The `always` keyword ensures these headers are added even on error responses (4xx, 5xx).

#### Upload Size Limit

```nginx
    client_max_body_size 15M;
```

- Rejects any request body larger than 15MB at the Nginx level
- Matches the Spring Boot `spring.servlet.multipart.max-request-size` setting
- Protects the backend from processing massive uploads — Nginx returns 413 (Request Entity Too Large) immediately

#### Proxy Pass (The Actual Reverse Proxy)

```nginx
    location / {
        proxy_pass http://app:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
```

This is where the reverse proxying happens:

- **`proxy_pass http://app:8080`** — Forwards all requests to the Spring Boot app. `app` is the Docker Compose service name, which Docker DNS resolves to the container's IP. Port 8080 is the app's internal port (never exposed to the internet)
- **`Host $host`** — Passes the original hostname (`api.caseyrquinn.com`) to the app so it knows which domain was requested
- **`X-Real-IP $remote_addr`** — Passes the client's real IP address. Without this, the app would see Nginx's IP for every request
- **`X-Forwarded-For $proxy_add_x_forwarded_for`** — Appends the client IP to the forwarding chain. This is what `HttpRequestUtils.extractIpAddress()` reads in our app for rate limiting and contact form logging
- **`X-Forwarded-Proto $scheme`** — Tells the app whether the original request was HTTP or HTTPS. Spring uses this (via `server.forward-headers-strategy: framework` in production config) to generate correct URLs in responses

---

## How It All Fits Together in Docker

```
Internet                Docker Network (app-network)
   │                    ┌─────────────────────────────┐
   │  :80/:443          │                             │
   └──────────► [nginx] ──── http://app:8080 ──► [spring boot]
                        │                             │
                        │            [postgres:5432]   │
                        │            [redis:6379]      │
                        └─────────────────────────────┘
```

- Only Nginx exposes ports 80 and 443 to the internet
- PostgreSQL and Redis have no port mappings — they're only accessible within the Docker network
- The Spring Boot app listens on 8080 but only within the Docker network

---

## Useful Nginx Commands

```bash
# Test configuration for syntax errors (run inside container)
docker exec personal-website-nginx nginx -t

# Reload config without downtime
docker exec personal-website-nginx nginx -s reload

# View access logs
docker logs personal-website-nginx

# View real-time logs
docker logs -f personal-website-nginx
```

---

## References

- [Nginx Reverse Proxy Guide](https://docs.nginx.com/nginx/admin-guide/web-server/reverse-proxy/)
- [Nginx SSL Termination](https://docs.nginx.com/nginx/admin-guide/security-controls/terminating-ssl-http/)
- [Mozilla SSL Configuration Generator](https://ssl-config.mozilla.org/) — generates production-grade SSL configs for Nginx
- [OWASP Secure Headers](https://owasp.org/www-project-secure-headers/) — reference for security headers
- [HTTP/2 Explained](https://http2-explained.haxx.se/) — deep dive into HTTP/2 protocol