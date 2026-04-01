# GitHub Actions CI/CD Pipeline

## What Is CI/CD?

- **CI (Continuous Integration)** — automatically build and test code every time you push
- **CD (Continuous Deployment)** — automatically deploy the build to production after it passes

Our pipeline does both: push to `main` triggers a Docker image build, pushes it to a registry, then deploys it to the VPS.

---

## The Full Pipeline Flow

```
Developer pushes to main
        │
        ▼
┌─────────────────────────┐
│  Job 1: build-and-push  │
│                         │
│  1. Checkout code       │
│  2. Login to GHCR       │
│  3. Build Docker image  │
│  4. Push to GHCR        │
└────────────┬────────────┘
             │ (only runs if Job 1 succeeds)
             ▼
┌─────────────────────────┐
│  Job 2: deploy          │
│                         │
│  1. SSH into VPS        │
│  2. Pull new image      │
│  3. Restart app         │
│  4. Clean up old images │
└─────────────────────────┘
```

---

## Workflow File Breakdown

The workflow lives at `.github/workflows/deploy.yml`. GitHub automatically detects and runs any YAML file in this directory.

### Trigger

```yaml
name: Build and Deploy

on:
  push:
    branches: [main]
```

| Property | What It Does |
|----------|-------------|
| `name` | Display name shown in the GitHub Actions UI |
| `on: push: branches: [main]` | Triggers this workflow only when code is pushed to the `main` branch. Pull requests, other branches, and tags are ignored |

### Environment Variables

```yaml
env:
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}
```

| Variable | Value | Purpose |
|----------|-------|---------|
| `REGISTRY` | `ghcr.io` | GitHub Container Registry — GitHub's built-in Docker image registry |
| `IMAGE_NAME` | `caseythecoder90/personal-website-backend` | `${{ github.repository }}` automatically resolves to `owner/repo`. Used as the Docker image name |

### Job 1: Build and Push

```yaml
jobs:
  build-and-push:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write
```

| Property | What It Does |
|----------|-------------|
| `runs-on: ubuntu-latest` | Runs on a fresh Ubuntu virtual machine hosted by GitHub (free for public repos) |
| `permissions: contents: read` | Allows the job to checkout (read) the repository code |
| `permissions: packages: write` | Allows the job to push Docker images to GitHub Container Registry. **This is required** — without it, the push fails with `permission_denied: write_package` |

#### Step 1: Checkout Code

```yaml
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
```

Clones the repository onto the runner machine. Without this, the runner starts with an empty filesystem. `@v4` is the version of the checkout action.

#### Step 2: Login to GHCR

```yaml
      - name: Log in to GitHub Container Registry
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
```

| Property | What It Does |
|----------|-------------|
| `registry: ghcr.io` | Authenticates with GitHub Container Registry (not Docker Hub) |
| `username: ${{ github.actor }}` | The GitHub user who triggered the workflow |
| `password: ${{ secrets.GITHUB_TOKEN }}` | An **automatic** token GitHub generates for every workflow run. You don't create this — GitHub provides it. It has the permissions specified in the `permissions` block above |

#### Step 3: Extract Metadata

```yaml
      - name: Extract metadata
        id: meta
        uses: docker/metadata-action@v5
        with:
          images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
          tags: |
            type=sha,prefix=
            type=raw,value=latest
```

Generates Docker image tags automatically:

| Tag Type | Example | Purpose |
|----------|---------|---------|
| `type=sha,prefix=` | `ghcr.io/caseythecoder90/personal-website-backend:abc1234` | Tags the image with the short Git commit SHA. This creates a unique, traceable version for every build |
| `type=raw,value=latest` | `ghcr.io/caseythecoder90/personal-website-backend:latest` | Always points to the most recent build. This is what the VPS pulls |

The `id: meta` assigns an ID so the next step can reference the generated tags via `${{ steps.meta.outputs.tags }}`.

#### Step 4: Build and Push

```yaml
      - name: Build and push Docker image
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}
```

| Property | What It Does |
|----------|-------------|
| `context: .` | Uses the repository root as the Docker build context (respects `.dockerignore`) |
| `push: true` | After building, push the image to GHCR |
| `tags` | Applies both tags from the metadata step (SHA + latest) |
| `labels` | Adds metadata labels (repo URL, commit info, etc.) to the image |

This step runs the `Dockerfile` — the multi-stage build that compiles the Java app and creates the runtime image. See [DOCKER.md](DOCKER.md) for Dockerfile details.

### Job 2: Deploy

```yaml
  deploy:
    needs: build-and-push
    runs-on: ubuntu-latest
```

| Property | What It Does |
|----------|-------------|
| `needs: build-and-push` | This job only runs after `build-and-push` succeeds. If the build fails, deploy is skipped |
| `runs-on: ubuntu-latest` | Runs on a new, separate runner (not the same machine as the build) |

#### SSH Into VPS and Deploy

```yaml
    steps:
      - name: Deploy to VPS via SSH
        uses: appleboy/ssh-action@v1
        with:
          host: ${{ secrets.VPS_HOST }}
          username: ${{ secrets.VPS_USER }}
          key: ${{ secrets.VPS_SSH_KEY }}
          script: |
            cd /opt/personal-website
            docker compose -f docker-compose.prod.yml pull app
            docker compose -f docker-compose.prod.yml up -d app
            docker image prune -f
```

This step uses the `appleboy/ssh-action` to SSH from the GitHub runner into your VPS and execute commands.

**Secrets (configured in GitHub repo settings):**

| Secret | Value | Purpose |
|--------|-------|---------|
| `VPS_HOST` | VPS IP address | Where to SSH |
| `VPS_USER` | `deploy` | The SSH username |
| `VPS_SSH_KEY` | SSH private key (full contents) | Authentication (no password needed) |

**Script commands:**

| Command | What It Does |
|---------|-------------|
| `cd /opt/personal-website` | Navigate to the project directory on the VPS |
| `docker compose -f docker-compose.prod.yml pull app` | Pulls the new `latest` image from GHCR. Only downloads changed layers (fast) |
| `docker compose -f docker-compose.prod.yml up -d app` | Recreates the app container with the new image. `-d` runs it in the background. PostgreSQL, Redis, and Nginx are untouched — only the app restarts |
| `docker image prune -f` | Removes old, unused images to free disk space. `-f` skips the confirmation prompt |

---

## GitHub Secrets Setup

These secrets are stored encrypted in GitHub and are **never** exposed in logs.

Navigate to: **Repository → Settings → Secrets and variables → Actions → New repository secret**

| Secret Name | Where to Get It |
|------------|----------------|
| `VPS_HOST` | Your VPS IP address (from Hetzner dashboard) |
| `VPS_USER` | The deploy user created during VPS setup (e.g., `deploy`) |
| `VPS_SSH_KEY` | Contents of `~/.ssh/id_ed25519` (or `id_rsa`) — the private key that can SSH into the VPS |

### Repository Permissions

The workflow also requires:
- **Settings → Actions → General → Workflow permissions** set to **"Read and write permissions"**
- The GHCR package must grant write access to the repository under **Package settings → Manage Actions access**

---

## What Happens On Each Push to Main

1. You push code (or merge a PR) to `main`
2. GitHub detects the push and starts the workflow (~2-5 seconds)
3. **Build job** (~3-5 minutes):
   - Checks out code
   - Builds the Docker image using the multi-stage Dockerfile
   - Pushes to `ghcr.io/caseythecoder90/personal-website-backend:latest`
4. **Deploy job** (~10-30 seconds):
   - SSHs into VPS
   - Pulls the new image (only changed layers)
   - Restarts the app container
   - Cleans up old images
5. The app boots with the health check (~30-60 seconds for Spring Boot startup)
6. Total time from push to live: **~4-6 minutes**

---

## Monitoring Deployments

```bash
# Watch the current workflow run in real-time
gh run watch

# List recent workflow runs
gh run list --limit 5

# View details of a specific run
gh run view <run-id>

# View logs of a failed run
gh run view <run-id> --log-failed

# Re-run a failed workflow
gh run rerun <run-id>
```

You can also view all runs in the browser at: `https://github.com/caseythecoder90/personal-website-backend/actions`

---

## Rollback

If a deployment breaks, you can roll back to a previous image:

```bash
# SSH into VPS
ssh deploy@<vps-ip>

# Pull a specific version by commit SHA
cd /opt/personal-website
docker compose -f docker-compose.prod.yml pull app  # won't help — pulls latest

# Instead, edit docker-compose.prod.yml temporarily to pin a version:
# image: ghcr.io/caseythecoder90/personal-website-backend:<commit-sha>
# Then:
docker compose -f docker-compose.prod.yml up -d app
```

Every build is tagged with its commit SHA, so you can always go back to a known good version.

---

## References

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Workflow Syntax Reference](https://docs.github.com/en/actions/reference/workflow-syntax-for-github-actions)
- [GitHub Container Registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry)
- [GITHUB_TOKEN Permissions](https://docs.github.com/en/actions/security-for-github-actions/security-guides/automatic-token-authentication)
- [appleboy/ssh-action](https://github.com/appleboy/ssh-action) — the SSH deploy action
- [docker/build-push-action](https://github.com/docker/build-push-action) — the Docker build action
- [Docker Layer Caching](https://docs.docker.com/build/cache/) — how Docker caching works