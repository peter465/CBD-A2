#!/bin/bash
# Automated deployment script

set -e  # Exit on any error

echo "=== Starting Deployment ==="

# Build the application
echo "Building JAR..."
mvn clean package -DskipTests

# Build Docker image
echo "Building Docker image..."
docker build -t rest-api:latest .

# Stop and remove old container
echo "Removing old container..."
docker stop rest-api 2>/dev/null || true
docker rm rest-api 2>/dev/null || true

# Run new container
echo "Starting new container..."
docker run -d \
  --name rest-api \
  --restart unless-stopped \
  -p 8080:8080 \
  rest-api:latest

# Wait for startup
echo "Waiting for application to start..."
sleep 10

# Verify deployment
echo "Verifying deployment..."
if curl -f http://localhost:8080/; then
  echo "=== Deployment Successful ==="
  exit 0
else
  echo "=== Deployment Failed ==="
  docker logs rest-api
  exit 1
fi