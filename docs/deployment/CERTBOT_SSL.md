# Certbot & Let's Encrypt SSL Certificates

## What Is Certbot?

Certbot is a free, open-source tool that automates obtaining and renewing SSL/TLS certificates from [Let's Encrypt](https://letsencrypt.org/). Let's Encrypt is a Certificate Authority (CA) that provides free certificates trusted by all major browsers.

Before Let's Encrypt, SSL certificates cost $50-$300/year and required manual installation. Certbot made HTTPS free and automatic.

---

## How SSL Certificate Issuance Works

When you request a certificate, Let's Encrypt needs to verify you control the domain. This is called a **challenge**. Our setup uses the **HTTP-01 challenge**:

```
1. Certbot asks Let's Encrypt: "I want a cert for api.caseyrquinn.com"

2. Let's Encrypt responds: "Prove you own it. Place this token at
   http://api.caseyrquinn.com/.well-known/acme-challenge/<token>"

3. Certbot writes the token file to /var/www/certbot/.well-known/acme-challenge/

4. Let's Encrypt makes an HTTP request to that URL from their servers

5. If the token matches → certificate issued
   If it fails → certificate denied
```

This is why the Nginx config has:

```nginx
location /.well-known/acme-challenge/ {
    root /var/www/certbot;
}
```

It serves the challenge files that Certbot places there.

---

## How It Works in Our Docker Setup

### The Certbot Container

```yaml
certbot:
  image: certbot/certbot
  volumes:
    - ./certbot/www:/var/www/certbot        # Challenge files (shared with Nginx)
    - ./certbot/conf:/etc/letsencrypt       # Certificates (shared with Nginx)
  entrypoint: "/bin/sh -c 'trap exit TERM; while :; do certbot renew; sleep 12h & wait $${!}; done;'"
```

**Volumes (shared storage between containers):**

| Volume | Purpose | Shared With |
|--------|---------|-------------|
| `./certbot/www` | Where Certbot writes HTTP-01 challenge files | Nginx reads these to serve challenge responses |
| `./certbot/conf` | Where certificates are stored after issuance | Nginx reads these for SSL termination |

**The entrypoint command explained:**

```bash
trap exit TERM;           # Handle graceful shutdown signals
while :; do               # Infinite loop
  certbot renew;          # Check all certificates, renew if expiring within 30 days
  sleep 12h & wait $${!}; # Sleep 12 hours, then check again
done;
```

This means Certbot checks for renewal every 12 hours. Let's Encrypt certificates expire after 90 days, but Certbot renews them when they have fewer than 30 days left — so in practice, certificates are renewed roughly every 60 days.

### The Certificate Files

After issuance, Certbot creates these files in `/etc/letsencrypt/live/<domain>/`:

| File | Contents | Used By |
|------|----------|---------|
| `fullchain.pem` | Your certificate + intermediate CA certificates | Nginx `ssl_certificate` |
| `privkey.pem` | Your private key (never share this) | Nginx `ssl_certificate_key` |
| `cert.pem` | Just your certificate (without chain) | Rarely used directly |
| `chain.pem` | Just the intermediate CA certificates | Rarely used directly |

Nginx needs `fullchain.pem` (not just `cert.pem`) because browsers need the full chain of trust to verify the certificate.

---

## Initial Certificate Setup

The first certificate must be obtained before Nginx can start with SSL. Here's the process used during initial VPS setup:

```bash
# 1. Start Nginx with a temporary HTTP-only config (no SSL block)
#    This allows Certbot to serve the HTTP-01 challenge

# 2. Run Certbot to obtain the certificate
docker compose -f docker-compose.prod.yml run --rm certbot \
  certonly --webroot \
  --webroot-path /var/www/certbot \
  -d api.caseyrquinn.com \
  --email your-email@example.com \
  --agree-tos \
  --no-eff-email

# 3. Switch Nginx to the full config with SSL
# 4. Restart Nginx
```

**Certbot flags explained:**

| Flag | Purpose |
|------|---------|
| `certonly` | Only obtain the certificate, don't try to install it |
| `--webroot` | Use the webroot plugin (place files in a directory served by an existing web server) |
| `--webroot-path /var/www/certbot` | Directory where challenge files are written |
| `-d api.caseyrquinn.com` | The domain to get a certificate for |
| `--email` | Contact email for urgent renewal/security notices |
| `--agree-tos` | Accept Let's Encrypt Terms of Service |
| `--no-eff-email` | Don't share email with Electronic Frontier Foundation |

---

## Certificate Renewal

Renewal is fully automatic via the Certbot container's loop. To manually test renewal:

```bash
# Dry run (doesn't actually renew, just tests the process)
docker compose -f docker-compose.prod.yml run --rm certbot renew --dry-run

# Force renewal (even if not expiring soon)
docker compose -f docker-compose.prod.yml run --rm certbot renew --force-renewal
```

After renewal, Nginx needs to reload to pick up the new certificates:

```bash
docker exec personal-website-nginx nginx -s reload
```

### Automating Nginx Reload After Renewal

The Certbot container's renewal loop doesn't automatically reload Nginx. For a production setup, you could add a cron job on the host:

```bash
# Add to crontab: crontab -e
0 */12 * * * docker exec personal-website-nginx nginx -s reload 2>/dev/null
```

This reloads Nginx every 12 hours (matching Certbot's renewal check interval). Nginx reload is graceful — no dropped connections.

---

## Troubleshooting

### Check certificate status
```bash
# View certificate details and expiry date
docker compose -f docker-compose.prod.yml run --rm certbot certificates
```

### Certificate not renewing
```bash
# Check Certbot logs
docker logs personal-website-certbot

# Common issues:
# - Port 80 not accessible (firewall blocking HTTP-01 challenge)
# - DNS not pointing to the correct IP
# - Rate limits hit (5 duplicate certs per week per domain)
```

### Test SSL configuration
```bash
# Check from outside the server
curl -vI https://api.caseyrquinn.com
```

---

## Let's Encrypt Rate Limits

Be aware of these limits during setup (not usually an issue after initial setup):

| Limit | Value |
|-------|-------|
| Certificates per registered domain | 50 per week |
| Duplicate certificates | 5 per week |
| Failed validations | 5 per hour |
| New registrations | 500 per 3 hours |

Use `--dry-run` when testing to avoid hitting rate limits.

---

## References

- [Let's Encrypt: How It Works](https://letsencrypt.org/how-it-works/) — official overview of the ACME protocol
- [Certbot Documentation](https://eff-certbot.readthedocs.io/) — full Certbot docs
- [ACME Protocol (RFC 8555)](https://tools.ietf.org/html/rfc8555) — the actual protocol specification
- [Let's Encrypt Rate Limits](https://letsencrypt.org/docs/rate-limits/) — detailed rate limit documentation
- [SSL Labs Server Test](https://www.ssllabs.com/ssltest/) — test your SSL configuration grade
- [Why 90-day Certificates?](https://letsencrypt.org/2015/11/09/why-90-days.html) — Let's Encrypt's rationale for short-lived certificates