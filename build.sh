#!/bin/bash

echo "Building Ticket OAuth2 Demo Application..."

# Clean and build the project
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo "JAR file location: target/ticket-oauth2-demo-1.0.0.jar"
else
    echo "Build failed!"
    exit 1
fi