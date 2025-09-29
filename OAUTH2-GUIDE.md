# OAuth2 Authentication Deep Dive

## 🎓 Understanding OAuth2 in This Demo

This document provides a comprehensive explanation of how OAuth2 authentication works in our Ticket Management application.

## 🔐 OAuth2 Fundamentals

### What is OAuth2?

OAuth2 (Open Authorization 2.0) is an authorization framework that enables applications to obtain limited access to user accounts. It works by delegating user authentication to the service that hosts the user account.

### Key Components in Our Demo

1. **Resource Server** (Ticket API)
   - Hosts protected resources (tickets)
   - Validates access tokens
   - Enforces authorization policies

2. **Authorization Server** (Keycloak)
   - Authenticates users
   - Issues access tokens
   - Manages client applications

3. **Client** (Your application/Postman/curl)
   - Requests access tokens
   - Uses tokens to access protected resources

4. **Resource Owner** (End user)
   - The entity that can grant access to a protected resource

## 🔄 OAuth2 Flow Types

### Resource Owner Password Credentials Grant (Used in Demo)

This flow is used for testing and demonstration purposes. **Not recommended for production client applications.**

```
     +----------+
     | Resource |
     |   Owner  |
     |          |
     +----------+
          v
          |    Resource Owner
         (A) Password Credentials
          |
          v
     +---------+                                  +---------------+
     |         |>--(B)---- Resource Owner ------->|               |
     |         |         Password Credentials     | Authorization |
     | Client  |                                  |     Server    |
     |         |<--(C)---- Access Token ---------<|               |
     |         |    (w/ Optional Refresh Token)   |               |
     +---------+                                  +---------------+
          |
          |
         (D)
          |
          v
     +---------+                                  +---------------+
     |         |>--(E)----- Access Token -------->|    Resource   |
     |         |                                  |     Server    |
     |         |<--(F)--- Protected Resource ---<|               |
     +---------+                                  +---------------+
```

### Steps Explained:

**(A)** The client collects the resource owner's username and password.

**(B)** The client authenticates with the authorization server and presents the resource owner's credentials.

**(C)** The authorization server validates the credentials and issues an access token.

**(D)** The client stores the access token securely.

**(E)** The client makes a request to the resource server with the access token.

**(F)** The resource server validates the token and returns the protected resource.

## 🎫 JWT Tokens Explained

### What is JWT?

JSON Web Token (JWT) is a compact, URL-safe means of representing claims between two parties. In our demo, JWT tokens contain:

- **Header**: Algorithm and token type
- **Payload**: Claims (user info, roles, expiration)
- **Signature**: Cryptographic signature for verification

### JWT Structure

```
eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.signature
│─────────── Header ──────────────│────────────── Payload ─────────────────│─ Signature ─│
```

### JWT Claims in Our Demo

```json
{
  "exp": 1704123600,          // Expiration time
  "iat": 1704120000,          // Issued at
  "jti": "uuid-here",         // JWT ID
  "iss": "http://localhost:9090/realms/ticket-realm", // Issuer
  "sub": "user-uuid",         // Subject (user ID)
  "typ": "Bearer",            // Token type
  "azp": "ticket-client",     // Authorized party
  "preferred_username": "user1", // Username
  "realm_access": {           // Keycloak realm roles
    "roles": ["user"]
  },
  "scope": "openid profile email" // OAuth2 scopes
}
```

## 🔒 Security Configuration Deep Dive

### Spring Security Filter Chain

Our `SecurityConfig` creates a filter chain that:

1. **Permits public endpoints** (H2 console, actuator)
2. **Secures API endpoints** with role-based access
3. **Configures OAuth2 resource server** for JWT validation
4. **Extracts roles** from JWT claims

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/h2-console/**").permitAll()           // Public
            .requestMatchers(HttpMethod.GET, "/api/tickets/**").hasAnyRole("USER", "ADMIN")  // Read
            .requestMatchers(HttpMethod.POST, "/api/tickets/**").hasAnyRole("USER", "ADMIN") // Write
            .requestMatchers(HttpMethod.PUT, "/api/tickets/**").hasAnyRole("USER", "ADMIN")  // Update
            .requestMatchers(HttpMethod.DELETE, "/api/tickets/**").hasRole("ADMIN")          // Delete
            .anyRequest().authenticated()                           // All others
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .jwtAuthenticationConverter(jwtAuthenticationConverter()) // Custom converter
            )
        );
    return http.build();
}
```

### JWT Authentication Converter

This component converts JWT claims into Spring Security authorities:

```java
@Bean
public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
    authoritiesConverter.setAuthorityPrefix("");
    authoritiesConverter.setAuthoritiesClaimName("scope");

    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(jwt -> {
        // Extract OAuth2 scopes
        Collection<GrantedAuthority> authorities = authoritiesConverter.convert(jwt);
        // Extract Keycloak realm roles
        Collection<GrantedAuthority> realmRoles = extractRealmRoles(jwt);
        // Combine both
        return Stream.concat(authorities.stream(), realmRoles.stream()).toList();
    });

    return converter;
}
```

### Role Extraction Process

1. **OAuth2 Scopes**: Extracted from `scope` claim
   - Example: `"scope": "openid profile email"`
   - Becomes: `["openid", "profile", "email"]`

2. **Realm Roles**: Extracted from `realm_access.roles` claim
   - Example: `"realm_access": {"roles": ["user", "admin"]}`
   - Becomes: `["ROLE_USER", "ROLE_ADMIN"]` (with ROLE_ prefix)

## 🔍 Token Validation Process

### How the API Validates JWT Tokens

1. **Receive Request**: Client sends request with `Authorization: Bearer <token>`

2. **Extract Token**: Spring Security extracts token from header

3. **Validate Signature**:
   - Fetch Keycloak's public keys from JWK endpoint
   - Verify token signature using RSA-256 algorithm

4. **Validate Claims**:
   - Check expiration (`exp` claim)
   - Verify issuer (`iss` claim)
   - Validate audience if configured

5. **Extract Authorities**: Convert claims to Spring Security authorities

6. **Create Authentication**: Create `JwtAuthenticationToken` object

7. **Authorize Request**: Check if user has required roles for the endpoint

### JWK (JSON Web Key) Endpoint

The API fetches Keycloak's public keys from:
```
http://localhost:9090/realms/ticket-realm/protocol/openid-connect/certs
```

This allows the API to verify JWT signatures without contacting Keycloak for every request.

## 🎯 Authorization Strategies

### Method-Level Security

We use `@PreAuthorize` annotations for fine-grained access control:

```java
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public ResponseEntity<List<TicketResponse>> getAllTickets() {
    // Only users with USER or ADMIN role can access
}

@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
    // Only users with ADMIN role can delete
}
```

### SpEL (Spring Expression Language)

`@PreAuthorize` uses SpEL expressions:

- `hasRole('ADMIN')`: Check for specific role
- `hasAnyRole('USER', 'ADMIN')`: Check for any of the roles
- `authentication.name == #username`: Check if authenticated user matches parameter
- `hasAuthority('SCOPE_read')`: Check for specific authority

### Alternative: URL-Based Security

Could also be configured in `SecurityFilterChain`:

```java
.requestMatchers(HttpMethod.DELETE, "/api/tickets/**").hasRole("ADMIN")
.requestMatchers(HttpMethod.GET, "/api/tickets/**").hasAnyRole("USER", "ADMIN")
```

## 🔄 Token Lifecycle

### Token Expiration

- **Access Token**: 5 minutes (300 seconds) in our demo
- **Refresh Token**: Not used in password grant flow
- **ID Token**: Contains user identity information (not used for API access)

### Token Refresh (Not Implemented in Demo)

In production applications, you would typically:

1. Store refresh tokens securely
2. Use refresh token to obtain new access tokens
3. Implement automatic token refresh in client

## 🚨 Security Considerations

### What We Do Right

✅ **JWT Signature Validation**: Ensures token integrity
✅ **Role-Based Access Control**: Granular permissions
✅ **HTTPS in Production**: Encrypt tokens in transit
✅ **Token Expiration**: Limits exposure window
✅ **Separate Authorization Server**: Centralized identity management

### Production Improvements Needed

⚠️ **Don't Use Password Grant**: Use Authorization Code flow instead
⚠️ **Implement PKCE**: For public clients
⚠️ **Add Rate Limiting**: Prevent abuse
⚠️ **Audit Logging**: Track access attempts
⚠️ **Refresh Token Rotation**: Enhanced security
⚠️ **Scope-Based Authorization**: More granular than roles

## 🔧 Debugging OAuth2 Issues

### Common Problems and Solutions

1. **401 Unauthorized**
   ```bash
   # Check if token is present
   curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/tickets

   # Verify token is not expired
   echo $TOKEN | cut -d. -f2 | base64 -d | jq .exp
   ```

2. **403 Forbidden**
   ```bash
   # Check user roles in token
   echo $TOKEN | cut -d. -f2 | base64 -d | jq .realm_access.roles

   # Verify endpoint requires correct role
   ```

3. **JWT Validation Errors**
   ```bash
   # Check JWK endpoint is accessible
   curl http://localhost:9090/realms/ticket-realm/protocol/openid-connect/certs

   # Verify issuer URI configuration
   ```

### Useful Tools

- **jwt.io**: Decode and inspect JWT tokens
- **Keycloak Admin Console**: Manage users, roles, clients
- **Postman**: Test OAuth2 flows
- **curl**: Command-line API testing

## 📚 Further Learning

### OAuth2 Flows to Explore

1. **Authorization Code Flow**: For web applications
2. **Client Credentials Flow**: For service-to-service communication
3. **Device Authorization Flow**: For devices without browsers
4. **PKCE**: Enhanced security for public clients

### Advanced Topics

- **OpenID Connect**: Identity layer on top of OAuth2
- **JWT vs Opaque Tokens**: Different token strategies
- **Token Introspection**: Alternative to JWT validation
- **OAuth2 Scopes**: Fine-grained permissions
- **Multi-tenant Authorization**: Supporting multiple organizations

---

This guide provides the foundation for understanding OAuth2 in modern applications. The demo serves as a practical starting point for implementing secure API authentication and authorization.