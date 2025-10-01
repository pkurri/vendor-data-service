# Vendor Data Service

**Version**: 1.0.0  
**Status**: ✅ Ready for Testing

Spring Boot 3 (Java 17) service providing a comprehensive court case data search API with complete JSON schema implementation. Secured using JWT bearer tokens with OAuth2 M2M (Machine-to-Machine) authentication.

> 📚 **[View Complete Documentation Index](DOCUMENTATION_INDEX.md)** - Navigate all project documentation

## 📦 M2M Client Libraries

Pre-built client libraries are available for easy integration:

- **Java Client**: `client/java/` - Full-featured Java client with OAuth2 support
- **TypeScript/Node.js Client**: `client/typescript/` - Modern async/await API for Node.js

See the respective README files in each client directory for installation and usage instructions.

## 🎯 Features

- **Complete JSON Schema Implementation** - All fields from court case data specification
- **Nested Object Support** - Charges, Sentences, Dockets, Events, Defendants
- **JWT Authentication** - OAuth2 Resource Server with scope-based authorization
- **Flexible Search** - Name, DOB, SSN, date ranges, counties, case types
- **Conditional Loading** - Include flags for nested objects
- **Pagination** - Up to 500 records per page
- **Rate Limiting** - Per-request throttling
- **Idempotency** - X-Request-Id header support
- **API Documentation** - Complete OpenAPI/Swagger UI

## 🏗️ Tech Stack

- **Spring Boot** 3.3.3
- **Java** 17
- **Gradle** 8.9
- **JWT** OAuth2 Resource Server
- **Database** SQL Server
- **JSON Schema Validation** networknt/json-schema-validator
- **OpenAPI** springdoc-openapi 2.6.0
- **Docker** + Kubernetes support

## 📋 Quick Start

See **[QUICK_START.md](QUICK_START.md)** for detailed setup instructions.

### Prerequisites
- Java 17+
- SQL Server
- Access to vendor-auth-service for JWT tokens

### Setup
1. Run database schema: `docs/sql/comprehensive_schema.sql`
2. Configure environment variables (see below)
3. Build: `.\gradlew.bat clean build`
4. Run: `.\gradlew.bat bootRun`

## 🔐 Security

JWT authentication required for all endpoints (except health/swagger).

### Environment Variables
```properties
JWT_ISSUER_URI=https://your-auth-service.com
JWT_JWK_SET_URI=https://your-auth-service.com/.well-known/jwks.json
APP_JWT_AUDIENCE=vendor-data-api
```

### Required Scope
- `vendor.search` - Access to search endpoints

## 🌐 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/search` | Search with query parameters |
| POST | `/api/v1/search` | Search with JSON body |
| GET | `/actuator/health` | Health check |
| GET | `/swagger-ui/index.html` | API documentation |
| GET | `/v3/api-docs` | OpenAPI specification |

## 📊 Data Model

### Response Envelope
```json
{
  "api_version": "v1",
  "client_request_id": "req-123",
  "generated_at": "2025-09-29T22:00:00Z",
  "page": 1,
  "page_size": 100,
  "total_records_is_estimate": true,
  "warnings": [],
  "data": [...]
}
```

### Case Record Structure
- **Case Information** - 40+ fields including case number, UCN, court type, dates
- **Charges** - Statute numbers, descriptions, levels, degrees
- **Sentences** - Imposed dates, codes, confinement details
- **Dockets** - Action dates, codes, text (up to 8000 chars)
- **Events** - Court appearances, judges, locations
- **Defendants** - Party information, demographics, aliases

## Quickstart (local)
1. Ensure Java 17 is installed.
2. Generate Gradle wrapper and build:
   - Windows PowerShell
     ```powershell
     gradle wrapper --gradle-version 8.9
     .\gradlew.bat clean bootJar
     java -jar .\build\libs\app.jar
     ```
3. Browse Swagger UI: http://localhost:8080/swagger-ui/index.html

## Configuration
Environment variables (see `.env.example`):
- `SERVER_PORT` (default 8080)
- `JWT_ISSUER_URI` or `JWT_JWK_SET_URI`
- `CORS_ALLOWED_ORIGINS` (default `*`)

## Docker
Build the jar, then build and run the container:
```bash
# After .\\gradlew.bat bootJar
docker build -t vendor-data-service:0.1.0 .
docker run -p 8080:8080 --rm vendor-data-service:0.1.0
```

## Kubernetes
- OpenShift: `k8s/openshift/deployment.yaml` (Deployment + Service + Route)
- Azure AKS: `k8s/azure/deployment.yaml`, `service.yaml`, `ingress.yaml`

Replace image names with your registry values and apply manifests.

## Shared JARs from vendor-auth-service
Drop shared libraries into `libs/` and they will be included automatically via Gradle `fileTree`.

## Sequence Diagram
Mermaid file: `docs/sequence.mmd`.

## Testing
```bash
# Windows
.\gradlew.bat test
```

## Notes
- Liveness/Readiness: `/actuator/health/liveness` and `/actuator/health/readiness`
- CORS is configurable via `app.security.cors.allowed-origins`

## Azure Government Deployment

Use this section when deploying to Azure Government (US Gov).

- __Portals/hosts__
  - Azure portal: https://portal.azure.us
  - App Service default host: `<app>.azurewebsites.us`
  - ACR registry domain: `<registry>.azurecr.us`
  - Many data-plane endpoints end with `.usgovcloudapi.net` (e.g., Key Vault)

- __App Service (recommended to start)__
  - Build JAR: `./gradlew bootJar`
  - Deploy with GitHub Actions workflow: `.github/workflows/deploy-azure-gov.yml`
  - Add secret: `AZUREAPPSERVICE_PUBLISHPROFILE_GOV` (App Service → Get publish profile)
  - Set App Settings (env vars) and use Key Vault references where possible
  - Custom domain: CNAME `api.example.com` → `<app>.azurewebsites.us`, enable Managed Certificate

- __Kubernetes on AKS (Gov)__
  - Use ACR `.azurecr.us` and update image references (see `k8s/azure/deployment.yaml`)
  - Ingress/DNS/TLS as in commercial Azure; just ensure Gov endpoints are used

- __Database & Redis__
  - Use the FQDNs from the Azure Government portal
  - JDBC examples are in `.env.example` (Azure SQL, PostgreSQL, MySQL)
  - Enforce SSL/TLS; use Private Endpoints for production

- __Networking (production)__
  - VNet integration for App Service
  - Private Endpoints for DB/Key Vault/Redis + Private DNS zones (Gov suffixes)
  - If WAF/edge is needed, use Application Gateway WAF v2

- __Observability__
  - Application Insights (workspace-based) + Azure Monitor alerts

Tip: If you need SPN-based login in CI, use `azure/login@v2` with `environment: azureUSGovernment`.
