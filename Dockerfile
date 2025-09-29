FROM openjdk:17-jdk-slim

LABEL maintainer="ticket-oauth2-demo"
LABEL description="Spring Boot 3.x OAuth2 Ticket Management Demo"

WORKDIR /app

COPY target/ticket-oauth2-demo-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1