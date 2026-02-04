# Docker Environment Setup

This directory contains the Docker Compose configuration for local development.

## Services

### PostgreSQL
- **Image**: postgres:16-alpine
- **Port**: 5432
- **Database**: casquinn_personal_website
- **Username**: casquinn
- **Password**: Ku^a4MoADdqLf4n>ka,d

### Redis
- **Image**: redis:7-alpine
- **Port**: 6379
- **Password**: vHZBDJUzyG}1AE6dBMZY

## Quick Start

### Start the services
```bash
cd src/main/resources/docker
docker-compose up -d
```

### Check service status
```bash
docker-compose ps
```

### View logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f postgres
docker-compose logs -f redis
```

### Stop the services
```bash
docker-compose down
```

### Stop and remove volumes (CAUTION: This deletes all data)
```bash
docker-compose down -v
```

## Connecting to Services

### PostgreSQL
```bash
# Using psql inside the container
docker-compose exec postgres psql -U casquinn -d casquinn_personal_website

# Or using local psql client
psql -h localhost -p 5432 -U casquinn -d casquinn_personal_website
```

### Redis
```bash
# Using redis-cli inside the container
docker-compose exec redis redis-cli -a vHZBDJUzyG}1AE6dBMZY

# Test connection
docker-compose exec redis redis-cli -a vHZBDJUzyG}1AE6dBMZY ping
```

## Health Checks

Both services include health checks to ensure they're ready before the application connects:

```bash
# Check PostgreSQL health
docker-compose exec postgres pg_isready -U casquinn -d casquinn_personal_website

# Check Redis health
docker-compose exec redis redis-cli -a vHZBDJUzyG}1AE6dBMZY ping
```

## Data Persistence

Data is persisted in Docker volumes:
- `postgres_data`: PostgreSQL database files
- `redis_data`: Redis persistence files

These volumes persist even after `docker-compose down`, ensuring your data is safe.

## Database Initialization Scripts

Place any `.sql` initialization scripts in the `init-db/` directory. They will be executed automatically when the PostgreSQL container is first created.

## Troubleshooting

### Port already in use
If you get a port conflict error:
```bash
# Find what's using the port
netstat -ano | findstr :5432
netstat -ano | findstr :6379

# Stop the conflicting service or change the port mapping in docker-compose.yml
```

### Connection refused
Ensure services are healthy:
```bash
docker-compose ps
docker-compose logs postgres
docker-compose logs redis
```

### Reset everything
To start fresh (WARNING: deletes all data):
```bash
docker-compose down -v
docker-compose up -d
```