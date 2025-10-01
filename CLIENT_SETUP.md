# M2M Client Setup Guide

This guide explains how to set up and use the M2M (Machine-to-Machine) clients for the Vendor Data Service.

## Overview

Two client libraries are provided:
- **Java Client**: For Java/Spring Boot applications
- **TypeScript/Node.js Client**: For Node.js applications

Both clients support OAuth2 Client Credentials flow for M2M authentication.

## Java Client Setup

### 1. Add Dependency

**Gradle:**
```gradle
dependencies {
    implementation project(':client:java')
}
```

**Maven:**
```xml
<dependency>
    <groupId>com.vendor</groupId>
    <artifactId>vendor-data-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Configure Client

```java
import com.vendor.client.VendorDataClient;
import com.vendor.client.config.ClientConfig;

ClientConfig config = ClientConfig.builder()
    .baseUrl("https://api.vendor.com")
    .clientId("your-client-id")
    .clientSecret("your-client-secret")
    .tokenUrl("https://auth.vendor.com/oauth/token")
    .scope("vendor.search")
    .build();

VendorDataClient client = new VendorDataClient(config);
```

### 3. Perform Search

```java
import com.vendor.client.dto.SearchRequest;
import com.vendor.client.dto.SearchResponse;

SearchRequest request = SearchRequest.builder()
    .firstName("John")
    .lastName("Doe")
    .dateOfBirth("1990-01-01")
    .build();

SearchResponse response = client.search(request);
System.out.println("Found: " + response.getTotalRecords());
```

### 4. Build Java Client

```bash
# From project root
.\gradlew.bat :client:java:build
```

## TypeScript/Node.js Client Setup

### 1. Install Dependencies

```bash
cd client/typescript
npm install
```

### 2. Build Client

```bash
npm run build
```

### 3. Use in Your Project

```bash
# In your project
npm install file:../vendor-data-service/client/typescript
# or after publishing
npm install @vendor/data-client
```

### 4. Configure and Use

```typescript
import { VendorDataClient } from '@vendor/data-client';

const client = new VendorDataClient({
  baseUrl: 'https://api.vendor.com',
  clientId: 'your-client-id',
  clientSecret: 'your-client-secret',
  tokenUrl: 'https://auth.vendor.com/oauth/token',
});

const response = await client.search({
  firstName: 'John',
  lastName: 'Doe',
  dateOfBirth: '1990-01-01',
});

console.log(`Found ${response.totalRecords} records`);
```

## OAuth2 Configuration

### Required OAuth2 Setup

1. **Client Credentials**: Obtain from your OAuth2 provider
   - Client ID
   - Client Secret
   - Token URL

2. **Scope**: Default is `vendor.search`

3. **Token URL Examples**:
   - Keycloak: `https://auth.example.com/realms/vendor/protocol/openid-connect/token`
   - Auth0: `https://your-domain.auth0.com/oauth/token`
   - Azure AD: `https://login.microsoftonline.com/{tenant}/oauth2/v2.0/token`

### Environment Variables (Recommended)

Instead of hardcoding credentials, use environment variables:

**Java:**
```java
ClientConfig config = ClientConfig.builder()
    .baseUrl(System.getenv("VENDOR_API_URL"))
    .clientId(System.getenv("VENDOR_CLIENT_ID"))
    .clientSecret(System.getenv("VENDOR_CLIENT_SECRET"))
    .tokenUrl(System.getenv("VENDOR_TOKEN_URL"))
    .build();
```

**TypeScript:**
```typescript
const client = new VendorDataClient({
  baseUrl: process.env.VENDOR_API_URL!,
  clientId: process.env.VENDOR_CLIENT_ID!,
  clientSecret: process.env.VENDOR_CLIENT_SECRET!,
  tokenUrl: process.env.VENDOR_TOKEN_URL!,
});
```

## Advanced Configuration

### Java Client Options

```java
ClientConfig config = ClientConfig.builder()
    .baseUrl("https://api.vendor.com")
    .clientId("client-id")
    .clientSecret("client-secret")
    .tokenUrl("https://auth.vendor.com/oauth/token")
    .scope("vendor.search")
    .connectTimeout(30000)        // 30 seconds
    .readTimeout(60000)           // 60 seconds
    .maxRetries(3)                // Retry failed requests
    .retryDelay(1000)             // Initial retry delay (exponential backoff)
    .debugLogging(true)           // Enable debug logs
    .tokenRefreshBuffer(60)       // Refresh token 60s before expiry
    .build();
```

### TypeScript Client Options

```typescript
const client = new VendorDataClient({
  baseUrl: 'https://api.vendor.com',
  clientId: 'client-id',
  clientSecret: 'client-secret',
  tokenUrl: 'https://auth.vendor.com/oauth/token',
  scope: 'vendor.search',
  timeout: 60000,               // 60 seconds
  maxRetries: 3,                // Retry failed requests
  retryDelay: 1000,             // Initial retry delay (exponential backoff)
  debug: true,                  // Enable debug logs
  tokenRefreshBuffer: 60,       // Refresh token 60s before expiry
});
```

## Examples

Complete examples are available in:
- Java: `client/java/src/main/java/com/vendor/client/examples/`
- TypeScript: `client/typescript/examples/`

## Testing

### Java Client Tests

```bash
.\gradlew.bat :client:java:test
```

### TypeScript Client Tests

```bash
cd client/typescript
npm test
```

## Publishing

### Java Client (Maven Central)

```bash
.\gradlew.bat :client:java:publish
```

### TypeScript Client (npm)

```bash
cd client/typescript
npm publish
```

## Troubleshooting

### Authentication Errors

- Verify client ID and secret are correct
- Check token URL is accessible
- Ensure scope matches server configuration

### Connection Errors

- Verify base URL is correct
- Check network connectivity
- Review firewall/proxy settings

### Token Expiration

- Clients automatically refresh tokens before expiry
- Adjust `tokenRefreshBuffer` if needed

## Support

For issues or questions:
- Check the main [README.md](./README.md)
- Review [QUICK_START.md](./QUICK_START.md)
- Check error codes in [errorcode.md](./errorcode.md)
