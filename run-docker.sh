#!/bin/bash

echo "Starting Ticket OAuth2 Demo with Docker Compose..."

# Build the application first
./build.sh

if [ $? -eq 0 ]; then
    echo "Starting services with Docker Compose..."
    docker-compose up --build -d

    echo ""
    echo "Services are starting up..."
    echo ""
    echo "Keycloak Admin Console: http://localhost:9090"
    echo "Username: admin"
    echo "Password: admin123"
    echo ""
    echo "Ticket API: http://localhost:8080"
    echo ""
    echo "Waiting for services to be ready..."

    # Wait for Keycloak to be ready
    echo "Checking Keycloak readiness..."
    while ! curl -s http://localhost:9090/health/ready > /dev/null; do
        echo "Waiting for Keycloak..."
        sleep 5
    done

    # Wait for API to be ready
    echo "Checking API readiness..."
    while ! curl -s http://localhost:8080/actuator/health > /dev/null; do
        echo "Waiting for API..."
        sleep 5
    done

    echo ""
    echo "✅ All services are ready!"
    echo ""
    echo "Test users available:"
    echo "- user1/password123 (USER role)"
    echo "- admin1/admin123 (ADMIN role)"
    echo ""
    echo "Check logs with: docker-compose logs -f"
    echo "Stop services with: docker-compose down"
else
    echo "Build failed, cannot start Docker services"
    exit 1
fi