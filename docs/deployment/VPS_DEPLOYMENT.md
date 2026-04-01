# VPS Deployment Guide

## Architecture

```
Internet → Nginx (SSL termination) → Spring Boot App → PostgreSQL / Redis
           ↑                          (port 8080)
      ports 80/443
      Let's Encrypt SSL
```

All services run in Docker Compose on a single Hetzner VPS. Only ports 22, 80, and 443 are exposed to the internet. PostgreSQL and Redis are accessible only within the Docker network.

## Prerequisites

- Hetzner account with a CX22 VPS (2 vCPU, 4GB RAM, ~4 EUR/mo)
- Domain `caseyrquinn.com` with DNS access
- GitHub repository with Actions enabled

---

## Step 1: Provision the VPS

1. Create a Hetzner CX22 server with **Ubuntu 24.04 LTS**
2. Add your SSH public key during creation
3. Note the server IP address

## Step 2: Initial Server Setup

SSH into the server as root:

```bash
ssh root@YOUR_VPS_IP
```

### Create deploy user

```bash
adduser deploy
usermod -aG sudo deploy

# Copy SSH key to deploy user
mkdir -p /home/deploy/.ssh
cp ~/.ssh/authorized_keys /home/deploy/.ssh/
chown -R deploy:deploy /home/deploy/.ssh
chmod 700 /home/deploy/.ssh
chmod 600 /home/deploy/.ssh/authorized_keys
```

### Harden SSH

```bash
sed -i 's/PermitRootLogin yes/PermitRootLogin no/' /etc/ssh/sshd_config
sed -i 's/#PasswordAuthentication yes/PasswordAuthentication no/' /etc/ssh/sshd_config
systemctl restart sshd
```

### Set up firewall

```bash
ufw default deny incoming
ufw default allow outgoing
ufw allow 22/tcp
ufw allow 80/tcp
ufw allow 443/tcp
ufw enable
```

## Step 3: Install Docker

Log in as the deploy user:

```bash
ssh deploy@YOUR_VPS_IP

curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker deploy
```

Log out and back in for the Docker group to take effect.

## Step 4: DNS Configuration

At your domain registrar, create an A record:

| Type | Name  | Value        | TTL |
|------|-------|--------------|-----|
| A    | api   | YOUR_VPS_IP  | 300 |

Wait for DNS propagation (check with `dig api.caseyrquinn.com`).

## Step 5: Prepare the Deployment Directory

```bash
sudo mkdir -p /opt/personal-website/nginx/conf.d
sudo mkdir -p /opt/personal-website/certbot/www
sudo mkdir -p /opt/personal-website/certbot/conf
sudo chown -R deploy:deploy /opt/personal-website
```

Copy these files from the repository to the VPS:

```bash
# From your local machine
scp docker-compose.prod.yml deploy@YOUR_VPS_IP:/opt/personal-website/
scp nginx/conf.d/default.conf deploy@YOUR_VPS_IP:/opt/personal-website/nginx/conf.d/
scp .env.example deploy@YOUR_VPS_IP:/opt/personal-website/.env
```

Then SSH in and edit `/opt/personal-website/.env` with your real production values.

```bash
chmod 600 /opt/personal-website/.env
```

## Step 6: Authenticate with GitHub Container Registry

Create a GitHub Personal Access Token (PAT) with `read:packages` scope at https://github.com/settings/tokens.

```bash
echo "YOUR_GITHUB_PAT" | docker login ghcr.io -u caseythecoder90 --password-stdin
```

## Step 7: SSL Certificate Setup

For the initial deploy, you need to temporarily use an HTTP-only nginx config so Certbot can complete the ACME challenge.

### 7a. Create temporary HTTP-only nginx config

On the VPS, edit `/opt/personal-website/nginx/conf.d/default.conf` to contain ONLY:

```nginx
server {
    listen 80;
    server_name api.caseyrquinn.com;

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        return 200 'Setting up SSL...';
        add_header Content-Type text/plain;
    }
}
```

### 7b. Start nginx

```bash
cd /opt/personal-website
docker compose -f docker-compose.prod.yml up -d nginx
```

### 7c. Request SSL certificate

```bash
docker compose -f docker-compose.prod.yml run --rm certbot \
  certonly --webroot --webroot-path=/var/www/certbot \
  --email YOUR_EMAIL@example.com --agree-tos --no-eff-email \
  -d api.caseyrquinn.com
```

### 7d. Restore full nginx config

Replace the temporary config with the full config from the repository (the one with both HTTP and HTTPS server blocks), then reload nginx:

```bash
# Copy the full default.conf back
docker compose -f docker-compose.prod.yml exec nginx nginx -s reload
```

## Step 8: First Full Deploy

```bash
cd /opt/personal-website
docker compose -f docker-compose.prod.yml up -d
```

Verify:

```bash
curl https://api.caseyrquinn.com/actuator/health
# Should return: {"status":"UP"}

curl https://api.caseyrquinn.com/api/v1/projects
# Should return project data
```

## Step 9: Set Up GitHub Actions CI/CD

Add these secrets in your GitHub repo under **Settings > Secrets and variables > Actions**:

| Secret         | Value                              |
|----------------|------------------------------------|
| `VPS_HOST`     | Your VPS IP address                |
| `VPS_USER`     | `deploy`                           |
| `VPS_SSH_KEY`  | Private SSH key for deploy user    |

### Generate a deploy-specific SSH key

```bash
# On your local machine
ssh-keygen -t ed25519 -C "github-actions-deploy" -f ~/.ssh/deploy_key

# Copy public key to VPS
ssh-copy-id -i ~/.ssh/deploy_key.pub deploy@YOUR_VPS_IP

# Add the PRIVATE key content as the VPS_SSH_KEY secret in GitHub
cat ~/.ssh/deploy_key
```

Now every push to `main` will automatically build, push, and deploy.

## Step 10: SSL Auto-Renewal

The certbot container handles renewal automatically. Add a cron job to reload nginx after renewals:

```bash
crontab -e
```

Add:

```
0 */12 * * * cd /opt/personal-website && docker compose -f docker-compose.prod.yml exec -T nginx nginx -s reload 2>/dev/null
```

---

## Ongoing Operations

### View logs

```bash
cd /opt/personal-website

# All services
docker compose -f docker-compose.prod.yml logs -f

# Just the app
docker compose -f docker-compose.prod.yml logs -f app

# Last 100 lines
docker compose -f docker-compose.prod.yml logs --tail 100 app
```

### Restart a service

```bash
docker compose -f docker-compose.prod.yml restart app
```

### Manual deploy (without CI/CD)

```bash
cd /opt/personal-website
docker compose -f docker-compose.prod.yml pull app
docker compose -f docker-compose.prod.yml up -d app
docker image prune -f
```

### Rollback to a specific version

Each CI build pushes a SHA-tagged image. To rollback:

1. Find the commit SHA you want: `git log --oneline`
2. Edit `docker-compose.prod.yml` on the VPS, change the app image tag from `:latest` to `:COMMIT_SHA`
3. Run `docker compose -f docker-compose.prod.yml pull app && docker compose -f docker-compose.prod.yml up -d app`

### Database backup

```bash
docker compose -f docker-compose.prod.yml exec postgres \
  pg_dump -U casquinn casquinn_personal_website > backup_$(date +%Y%m%d).sql
```

### Database restore

```bash
cat backup_file.sql | docker compose -f docker-compose.prod.yml exec -T postgres \
  psql -U casquinn casquinn_personal_website
```

---

## Cost Estimate

| Item                  | Monthly Cost |
|-----------------------|-------------|
| Hetzner CX22 VPS     | ~4 EUR      |
| Domain (annual/12)    | ~1 EUR      |
| **Total**             | **~5 EUR**  |

## Resource Usage (4GB RAM VPS)

| Service       | RAM Usage  |
|---------------|------------|
| Spring Boot   | ~400-512MB |
| PostgreSQL    | ~100-200MB |
| Redis         | ~50MB      |
| Nginx         | ~10MB      |
| Docker + OS   | ~500MB     |
| **Total**     | **~1.2GB** |
| **Free**      | **~2.8GB** |
