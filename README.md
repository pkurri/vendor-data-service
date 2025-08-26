# Vendor Data Service

Spring Boot 3 (Java 17) service that exposes an external-facing Search API to query vendor data by SSN last 4, DoB, and name. Secured using JWT bearer tokens compatible with `vendor-auth-service`.

## Tech
- Spring Boot 3.3, Java 17, Gradle
- JWT Resource Server (OAuth2)
- OpenAPI/Swagger UI
- Actuator liveness/readiness
- Docker + K8s (OpenShift and Azure AKS)

## Endpoints
- `GET /api/v1/search` (query params)
- `POST /api/v1/search` (JSON body)
- Swagger UI: `/swagger-ui/index.html`
- OpenAPI: `/v3/api-docs` or `src/main/resources/openapi/vendor-data-service.yaml`

## Security
- JWT issuer and JWK set URI configurable via env:
  - `JWT_ISSUER_URI`
  - `JWT_JWK_SET_URI`

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
