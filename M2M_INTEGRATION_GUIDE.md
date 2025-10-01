# M2M Integration Guide - Vendor Auth Server

## Overview

This system uses **vendor-auth-server** as the OAuth2 Authorization Server for M2M (Machine-to-Machine) authentication. The authentication flow uses **OAuth2 Client Credentials grant type only** - no user authentication, no passwords, no login forms.

## Architecture

```
┌──────────────────┐         ┌──────────────────┐         ┌──────────────────┐
│   Client App     │         │ vendor-auth-     │         │ vendor-data-     │
│   (Java/TS)      │         │    server        │         │    service       │
│                  │         │  (Port 9000)     │         │  (Port 8081)     │
└────────┬─────────┘         └────────┬─────────┘         └────────┬─────────┘
         │                            │                            │
         │ 1. POST /oauth2/token      │                            │
         │    client_credentials      │                            │
         ├───────────────────────────>│                            │
         │                            │                            │
         │ 2. JWT Access Token        │                            │
         │<───────────────────────────┤                            │
         │                            │                            │
         │ 3. API Request             │                            │
         │    Authorization: Bearer   │                            │
         ├────────────────────────────┼───────────────────────────>│
         │                            │                            │
         │                            │ 4. Validate JWT via JWKS   │
         │                            │<───────────────────────────┤
         │                            │                            │
         │ 5. API Response            │                            │
         │<───────────────────────────┼────────────────────────────┤
         │                            │                            │
```

## Components

### 1. vendor-auth-server (OAuth2 Authorization Server)

**Purpose**: Issues JWT access tokens for M2M clients

**Configuration**:
- **Port**: 9000
- **Token Endpoint**: `http://localhost:9000/oauth2/token`
- **JWKS Endpoint**: `http://localhost:9000/oauth2/jwks`
- **Issuer**: `http://localhost:9000`
- **Grant Type**: `client_credentials` (M2M only)

**Default M2M Client**:
```yaml
Client ID:     m2m-client
Client Secret: m2m-secret
Scopes:        read, write
Token TTL:     1 hour (3600 seconds)
```

**Key Features**:
- JWT token signing with RSA keys
- JWKS endpoint for public key distribution
- OpenID Connect discovery
- Key rotation support
- No user authentication (M2M only)

### 2. vendor-data-service (Resource Server)

**Purpose**: Protected API that validates JWT tokens

**Configuration**:
- **Port**: 8081
- **Auth Type**: OAuth2 Resource Server with JWT validation
- **Issuer URI**: `http://localhost:9000`
- **JWKS URI**: `http://localhost:9000/oauth2/jwks`

**Protected Endpoints**:
- `POST /api/v1/search` - Requires valid JWT token
- `GET /api/v1/search` - Requires valid JWT token

**Public Endpoints** (no auth required):
- `GET /actuator/health`
- `GET /swagger-ui.html`
- `GET /v3/api-docs`

### 3. Client Libraries (Java & TypeScript)

**Purpose**: Simplify M2M authentication for applications

**Features**:
- Automatic token acquisition using Client Credentials flow
- Automatic token refresh before expiration
- Retry logic with exponential backoff
- Connection pooling
- Type-safe request/response models

## Setup Instructions

### Step 1: Start vendor-auth-server

```bash
cd vendor-auth-server

# Generate keystore (first time only)
cd src/main/resources/keystore
keytool -genkeypair -alias auth-key -keyalg RSA -keysize 2048 \
  -keystore auth-jwt.p12 -storetype PKCS12 \
  -storepass changeit -keypass changeit -validity 3650 \
  -dname "CN=Vendor Auth Server, OU=Security, O=Kestrel, L=City, ST=State, C=US"
cd ../../../..

# Start the auth server
./gradlew bootRun
```

**Verify it's running**:
```bash
# Health check
curl http://localhost:9000/actuator/health

# JWKS endpoint
curl http://localhost:9000/oauth2/jwks
```

### Step 2: Start vendor-data-service

```bash
cd vendor-data-service

# Start the data service
./gradlew bootRun
```

**Verify it's running**:
```bash
curl http://localhost:8081/actuator/health
```

### Step 3: Test M2M Authentication

**Get Access Token**:
```bash
curl -X POST http://localhost:9000/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u m2m-client:m2m-secret \
  -d "grant_type=client_credentials&scope=read write"
```

**Response**:
```json
{
  "access_token": "eyJraWQiOiJhdXRoLWtleSIsImFsZyI6IlJTMjU2In0...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "read write"
}
```

**Use Token to Call API**:
```bash
TOKEN="<access_token_from_above>"

curl -X POST http://localhost:8081/api/v1/search \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name_last": "Smith",
    "name_first": "John",
    "page": 1,
    "page_size": 10
  }'
```

## Client Library Usage

### Java Client

```java
import com.vendor.client.VendorDataClient;
import com.vendor.client.config.ClientConfig;
import com.vendor.client.dto.SearchRequest;
import com.vendor.client.dto.SearchResponse;

import java.time.LocalDate;
import java.util.Arrays;

public class Example {
    public static void main(String[] args) {
        // Configure M2M client
        ClientConfig config = ClientConfig.builder()
                .baseUrl("http://localhost:8081")
                .clientId("m2m-client")
                .clientSecret("m2m-secret")
                .tokenUrl("http://localhost:9000/oauth2/token")
                .scope("read write")
                .build();
        
        // Create client (handles token management automatically)
        try (VendorDataClient client = new VendorDataClient(config)) {
            
            // Perform search
            SearchRequest request = SearchRequest.builder()
                    .nameLast("Smith")
                    .nameFirst("John")
                    .dob(LocalDate.of(1990, 1, 1))
                    .page(1)
                    .pageSize(10)
                    .build();
            
            SearchResponse response = client.search(request);
            System.out.println("Found: " + response.getData().size() + " records");
        }
    }
}
```

### TypeScript Client

```typescript
import { VendorDataClient } from '@vendor/data-client';

// Configure M2M client
const client = new VendorDataClient({
  baseUrl: 'http://localhost:8081',
  clientId: 'm2m-client',
  clientSecret: 'm2m-secret',
  tokenUrl: 'http://localhost:9000/oauth2/token',
  scope: 'read write',
});

// Perform search (token managed automatically)
const response = await client.search({
  nameLast: 'Smith',
  nameFirst: 'John',
  dob: '1990-01-01',
  page: 1,
  pageSize: 10,
});

console.log(`Found ${response.data.length} records`);
```

## Production Configuration

### Environment Variables

**vendor-auth-server**:
```bash
# Keystore configuration
KEYSTORE_PASSWORD=<strong-password>
KEY_PASSWORD=<strong-password>
KEYSTORE_PATH=/secure/path/auth-jwt.p12

# Server
SERVER_PORT=9000
```

**vendor-data-service**:
```bash
# OAuth2 Resource Server
JWT_ISSUER_URI=https://auth.vendor.com
JWT_JWK_SET_URI=https://auth.vendor.com/oauth2/jwks
APP_JWT_AUDIENCE=vendor-data-service

# Server
SERVER_PORT=8081

# Database
DB_URL=jdbc:sqlserver://...
DB_USERNAME=...
DB_PASSWORD=...
```

**Client Applications**:
```bash
# M2M Credentials (use secret management in production)
VENDOR_API_URL=https://api.vendor.com
VENDOR_CLIENT_ID=<your-client-id>
VENDOR_CLIENT_SECRET=<your-client-secret>
VENDOR_TOKEN_URL=https://auth.vendor.com/oauth2/token
```

### Security Best Practices

1. **Use HTTPS in Production**
   - All endpoints must use HTTPS
   - Enable TLS/SSL on both auth server and data service

2. **Secure Credential Storage**
   - Never hardcode credentials
   - Use environment variables or secret management (AWS Secrets Manager, Azure Key Vault, HashiCorp Vault)

3. **Rotate Credentials Regularly**
   - Rotate client secrets every 90 days
   - Rotate signing keys annually
   - Use key rotation support in vendor-auth-server

4. **Limit Scopes**
   - Request only necessary scopes
   - Use `read` for read-only access
   - Use `write` only when needed

5. **Network Security**
   - Deploy in private VPC/VNet
   - Use private endpoints
   - Implement firewall rules

6. **Monitoring**
   - Monitor failed authentication attempts
   - Track token usage
   - Alert on anomalies

## Adding New M2M Clients

### In vendor-auth-server

Edit `AuthorizationServerConfig.java`:

```java
@Bean
public RegisteredClientRepository registeredClientRepository() {
    RegisteredClient client1 = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("m2m-client")
            .clientSecret("{noop}m2m-secret")  // Use {bcrypt} in production
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .scope("read")
            .scope("write")
            .tokenSettings(TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofHours(1))
                    .build())
            .build();
    
    // Add new client
    RegisteredClient client2 = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("new-client")
            .clientSecret("{bcrypt}$2a$10$...")  // BCrypt encoded
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .scope("read")
            .build();
    
    return new InMemoryRegisteredClientRepository(client1, client2);
}
```

### For Production: Use Database

```java
@Bean
public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
    return new JdbcRegisteredClientRepository(jdbcTemplate);
}
```

## Troubleshooting

### Token Validation Fails

**Symptoms**: 401 Unauthorized errors

**Solutions**:
1. Verify vendor-auth-server is running on port 9000
2. Check JWKS endpoint is accessible: `curl http://localhost:9000/oauth2/jwks`
3. Verify issuer URI matches in both services
4. Check token hasn't expired
5. Ensure client has correct scopes

### Cannot Get Token

**Symptoms**: 401 from token endpoint

**Solutions**:
1. Verify client credentials are correct
2. Check Authorization header format: `Basic base64(clientId:clientSecret)`
3. Ensure grant_type is `client_credentials`
4. Verify vendor-auth-server is running

### JWKS Endpoint Not Found

**Symptoms**: 404 on JWKS endpoint

**Solutions**:
1. Verify keystore is generated in vendor-auth-server
2. Check application.yml configuration
3. Ensure vendor-auth-server started successfully
4. Check logs for keystore errors

## Key Differences from User Authentication

| Feature | M2M (Client Credentials) | User Authentication |
|---------|-------------------------|---------------------|
| **Grant Type** | `client_credentials` | `authorization_code`, `password` |
| **Credentials** | Client ID + Secret | Username + Password |
| **User Context** | No user, service-to-service | Specific user |
| **Token Contains** | Client ID, scopes | User ID, roles, scopes |
| **Use Case** | Backend services, APIs | Web/mobile apps with users |
| **Login UI** | None | Login form required |

## References

- **vendor-auth-server**: `../vendor-auth-server/README.md`
- **Client Setup**: `CLIENT_SETUP.md`
- **Security Guide**: `M2M_SECURITY_GUIDE.md`
- **OAuth2 Spec**: https://oauth.net/2/grant-types/client-credentials/
- **Spring Authorization Server**: https://spring.io/projects/spring-authorization-server
