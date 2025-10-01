# M2M Security Guide

## Overview

The M2M clients use OAuth2 Client Credentials flow for secure machine-to-machine authentication. This guide covers security best practices.

## Authentication Flow

```
Client Application
    ↓ (1) Request token with client_id + client_secret
OAuth2 Server
    ↓ (2) Return access_token + expires_in
Client Library (auto-managed)
    ↓ (3) Use token in Authorization: Bearer header
Vendor Data Service API
```

## Security Best Practices

### 1. Credential Management

**❌ NEVER Do This:**
```java
// Hardcoded credentials - INSECURE!
ClientConfig config = ClientConfig.builder()
    .clientId("my-client-id")
    .clientSecret("my-secret-123")  // ❌ Exposed in code
    .build();
```

**✅ DO This:**
```java
// Use environment variables
ClientConfig config = ClientConfig.builder()
    .clientId(System.getenv("VENDOR_CLIENT_ID"))
    .clientSecret(System.getenv("VENDOR_CLIENT_SECRET"))
    .tokenUrl(System.getenv("VENDOR_TOKEN_URL"))
    .baseUrl(System.getenv("VENDOR_API_URL"))
    .build();
```

**✅ Or Use Secret Management:**
```java
// AWS Secrets Manager
String secret = secretsManager.getSecretValue(
    new GetSecretValueRequest().withSecretId("vendor-api-credentials")
).getSecretString();

// Azure Key Vault
String clientSecret = keyVaultClient.getSecret(
    "https://myvault.vault.azure.net",
    "vendor-client-secret"
).value();

// HashiCorp Vault
VaultResponse response = vault.logical().read("secret/vendor-api");
String clientSecret = response.getData().get("client_secret");
```

### 2. Environment Variables

**Development (.env file):**
```bash
# .env (add to .gitignore!)
VENDOR_API_URL=https://api-dev.vendor.com
VENDOR_CLIENT_ID=dev-client-id
VENDOR_CLIENT_SECRET=dev-secret-xyz
VENDOR_TOKEN_URL=https://auth-dev.vendor.com/oauth/token
```

**Production (System/Container Environment):**
```bash
# Kubernetes Secret
kubectl create secret generic vendor-api-creds \
  --from-literal=client-id='prod-client-id' \
  --from-literal=client-secret='prod-secret-xyz'

# Docker Compose
environment:
  - VENDOR_CLIENT_ID=${VENDOR_CLIENT_ID}
  - VENDOR_CLIENT_SECRET=${VENDOR_CLIENT_SECRET}
```

### 3. Network Security

**✅ Always Use HTTPS:**
```java
// Correct - HTTPS
.baseUrl("https://api.vendor.com")
.tokenUrl("https://auth.vendor.com/oauth/token")

// ❌ Never use HTTP in production
.baseUrl("http://api.vendor.com")  // INSECURE!
```

**✅ Certificate Validation:**
```java
// Clients validate SSL certificates by default
// Only disable for local development with self-signed certs
```

**✅ Network Isolation:**
- Deploy services in private VPC/VNet
- Use private endpoints for API communication
- Implement network security groups/firewall rules

### 4. Token Security

**Automatic Token Management:**
```java
// ✅ Tokens are automatically managed
// - Stored in memory only (not disk)
// - Refreshed before expiration
// - Never logged or exposed

VendorDataClient client = new VendorDataClient(config);
// Token acquired automatically on first request
// Token refreshed automatically when needed
```

**Token Refresh Buffer:**
```java
// Refresh token 60 seconds before expiration (default)
ClientConfig config = ClientConfig.builder()
    .tokenRefreshBuffer(60)  // Adjust as needed
    .build();
```

### 5. Scope Limitation

**✅ Request Minimum Required Scope:**
```java
// Only request the scope you need
ClientConfig config = ClientConfig.builder()
    .scope("vendor.search")  // Not "vendor.* or admin"
    .build();
```

**Scope Hierarchy:**
- `vendor.search` - Read-only search access (recommended for most clients)
- `vendor.write` - Write access (only if needed)
- `vendor.admin` - Administrative access (rarely needed)

### 6. Error Handling

**✅ Don't Expose Sensitive Info in Logs:**
```java
try {
    SearchResponse response = client.search(request);
} catch (VendorClientException e) {
    // ✅ Log error without exposing credentials
    logger.error("Search failed: {}", e.getMessage());
    
    // ❌ Don't log full config or tokens
    // logger.error("Failed with config: {}", config);  // May expose secrets
}
```

**✅ Sanitize Error Responses:**
```java
// Client automatically sanitizes errors
// - No credentials in exception messages
// - No tokens in stack traces
```

### 7. Rate Limiting & Throttling

**Built-in Retry Logic:**
```java
ClientConfig config = ClientConfig.builder()
    .maxRetries(3)              // Limit retries
    .retryDelay(1000)           // Exponential backoff
    .build();
```

**Implement Circuit Breaker (Recommended):**
```java
// Using Resilience4j
CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("vendorApi");

Supplier<SearchResponse> decoratedSupplier = CircuitBreaker
    .decorateSupplier(circuitBreaker, () -> client.search(request));

SearchResponse response = decoratedSupplier.get();
```

### 8. Monitoring & Auditing

**✅ Log API Usage:**
```java
// Enable debug logging in non-production
ClientConfig config = ClientConfig.builder()
    .debugLogging(isDevelopment)  // Only in dev/staging
    .build();

// Custom logging
logger.info("API request: user={}, endpoint={}, requestId={}", 
    userId, endpoint, requestId);
```

**✅ Monitor for Anomalies:**
- Track failed authentication attempts
- Monitor unusual request patterns
- Alert on rate limit violations
- Track token refresh failures

### 9. Credential Rotation

**Regular Rotation Schedule:**
```bash
# Rotate credentials every 90 days
# 1. Generate new client secret in OAuth2 provider
# 2. Update environment variables/secrets
# 3. Deploy updated configuration
# 4. Revoke old credentials after grace period
```

**Zero-Downtime Rotation:**
```java
// Support multiple credentials during rotation
// Keep old credentials active for grace period
// Gradually migrate to new credentials
```

### 10. Production Checklist

- [ ] Credentials stored in secret management system (not code)
- [ ] HTTPS enforced for all API calls
- [ ] Minimum required OAuth2 scope requested
- [ ] Network isolated (VPC/VNet, private endpoints)
- [ ] Certificate validation enabled
- [ ] Debug logging disabled in production
- [ ] Rate limiting configured
- [ ] Circuit breaker implemented
- [ ] Monitoring and alerting configured
- [ ] Credential rotation schedule established
- [ ] Security scanning in CI/CD pipeline
- [ ] Regular dependency updates scheduled

## OAuth2 Provider Configuration

### Recommended Settings

**Token Lifetime:**
- Access Token: 1 hour (3600 seconds)
- Refresh buffer: 60 seconds (client-side)

**Client Configuration:**
- Grant Type: `client_credentials`
- Token Endpoint Auth Method: `client_secret_post` or `client_secret_basic`
- Scope: `vendor.search` (or as required)

**Security Features:**
- Enable token introspection
- Enable token revocation
- Require HTTPS for token endpoint
- Implement rate limiting on token endpoint
- Log all token requests

## Example: Secure Spring Boot Integration

```java
@Configuration
public class VendorClientConfig {
    
    @Bean
    public VendorDataClient vendorDataClient(
            @Value("${vendor.api.url}") String apiUrl,
            @Value("${vendor.oauth.client-id}") String clientId,
            @Value("${vendor.oauth.client-secret}") String clientSecret,
            @Value("${vendor.oauth.token-url}") String tokenUrl) {
        
        ClientConfig config = ClientConfig.builder()
                .baseUrl(apiUrl)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tokenUrl(tokenUrl)
                .scope("vendor.search")
                .connectTimeout(30000)
                .readTimeout(60000)
                .maxRetries(3)
                .debugLogging(false)  // Never in production
                .build();
        
        return new VendorDataClient(config);
    }
}
```

**application.yml:**
```yaml
vendor:
  api:
    url: ${VENDOR_API_URL}
  oauth:
    client-id: ${VENDOR_CLIENT_ID}
    client-secret: ${VENDOR_CLIENT_SECRET}
    token-url: ${VENDOR_TOKEN_URL}
```

## Security Incident Response

**If Credentials Are Compromised:**

1. **Immediately** revoke the compromised credentials in OAuth2 provider
2. Generate new client credentials
3. Update all applications with new credentials
4. Review audit logs for unauthorized access
5. Notify security team and stakeholders
6. Document incident and lessons learned

## Compliance

**Data Protection:**
- Client libraries do not store PII
- Tokens are memory-only (not persisted)
- No sensitive data in logs (when debug disabled)

**Audit Requirements:**
- All API requests include X-Request-Id for tracing
- OAuth2 provider logs all authentication attempts
- Application logs track API usage

## Additional Resources

- [OAuth 2.0 Client Credentials Grant](https://oauth.net/2/grant-types/client-credentials/)
- [OWASP API Security Top 10](https://owasp.org/www-project-api-security/)
- [NIST Digital Identity Guidelines](https://pages.nist.gov/800-63-3/)
