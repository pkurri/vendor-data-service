# Quick Start Guide - Vendor Data Service

## Prerequisites

- Java 17+
- SQL Server (or compatible database)
- Access to vendor-auth-service for JWT tokens
- Gradle (or use wrapper)

## Setup Steps

### 1. Database Setup

Run the comprehensive schema:

```sql
-- Execute this file on your SQL Server instance
-- File: docs/sql/comprehensive_schema.sql
```

This creates:
- `cases` table (main case data)
- `defendants` table (party information)
- `defendant_aka` table (aliases)
- `charges` table (charge details)
- `sentences` table (sentencing information)
- `dockets` table (docket entries)
- `court_events` table (court events)
- All necessary indexes

### 2. Environment Configuration

Create a `.env` file or set environment variables:

```properties
# JWT Authentication (from vendor-auth-service)
JWT_ISSUER_URI=https://your-auth-service.com
JWT_JWK_SET_URI=https://your-auth-service.com/.well-known/jwks.json
APP_JWT_AUDIENCE=vendor-data-api

# Database
SPRING_DATASOURCE_URL=jdbc:sqlserver://localhost:1433;databaseName=courtdata;encrypt=true;trustServerCertificate=true
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# CORS
CORS_ALLOWED_ORIGINS=https://your-frontend.com

# Server
SERVER_PORT=8080
```

### 3. Build the Application

```powershell
# Windows PowerShell
.\gradlew.bat clean build

# Or if gradlew doesn't exist, generate it first:
gradle wrapper --gradle-version 8.9
.\gradlew.bat clean build
```

### 4. Run the Application

```powershell
# Run with environment variables
.\gradlew.bat bootRun

# Or run the JAR directly
java -jar build\libs\app.jar
```

### 5. Verify Installation

Check health endpoint:

```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

### 6. Access Swagger UI

Open browser to:
```
http://localhost:8080/swagger-ui/index.html
```

## Testing the API

### Step 1: Get JWT Token

From vendor-auth-service:

```bash
curl -X POST https://your-auth-service.com/api/v1/authenticate/vendor/test-vendor \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your-username",
    "password": "your-password"
  }'
```

Response:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

### Step 2: Search Cases

Using the JWT token:

```bash
curl -X GET "http://localhost:8080/api/v1/search?name_last=SMITH&name_first=JOHN&page=1&page_size=50" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

Or with POST:

```bash
curl -X POST http://localhost:8080/api/v1/search \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "name_last": "SMITH",
    "name_first": "JOHN",
    "dob": "1980-01-15",
    "include_charges": true,
    "include_dockets": true,
    "page": 1,
    "page_size": 50
  }'
```

## Common Issues

### Issue: "JWT configuration missing"

**Solution**: Ensure JWT_ISSUER_URI or JWT_JWK_SET_URI is set in environment variables.

### Issue: "403 Forbidden - insufficient_scope"

**Solution**: Ensure your JWT token includes the `vendor.search` scope.

### Issue: Database connection failed

**Solution**: 
1. Verify SQL Server is running
2. Check connection string in environment variables
3. Verify username/password
4. Check firewall settings

### Issue: "MISSING_SEARCH_KEY" error

**Solution**: Provide at least one of:
- `name_last` + `name_first`
- `dob`
- `ssn_last4`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/search` | Search with query parameters |
| POST | `/api/v1/search` | Search with JSON body |
| GET | `/actuator/health` | Health check |
| GET | `/swagger-ui/index.html` | API documentation |

## Sample Request/Response

### Request

```json
POST /api/v1/search
{
  "name_last": "SMITH",
  "name_first": "JOHN",
  "dob": "1980-01-15",
  "include_charges": true,
  "page": 1,
  "page_size": 10
}
```

### Response

```json
{
  "api_version": "v1",
  "client_request_id": null,
  "generated_at": "2025-09-29T22:00:00Z",
  "page": 1,
  "page_size": 10,
  "total_records_is_estimate": true,
  "warnings": [],
  "data": [
    {
      "case_id": "12345",
      "case_number": "2020-CF-001234",
      "filed_date": "2020-05-15",
      "last_name": "SMITH",
      "first_name": "JOHN",
      "date_of_birth": "1980-01-15",
      "charges": [
        {
          "charge_sequence_number": "001",
          "offense_date": "2020-05-10",
          "initial_fl_statute_number": "812.014",
          "initial_fl_statute_description": "THEFT",
          "sentences": []
        }
      ],
      "dockets": [],
      "events": [],
      "defendants": []
    }
  ]
}
```

## Next Steps

1. **Populate Database**: Add test data to verify searches
2. **Configure Production**: Set up production JWT issuer
3. **Monitor**: Set up Application Insights or logging
4. **Scale**: Configure load balancing if needed

## Documentation

- **API Documentation**: `docs/API_DOCUMENTATION.md`
- **Implementation Summary**: `IMPLEMENTATION_SUMMARY.md`
- **Database Schema**: `docs/sql/comprehensive_schema.sql`
- **Main README**: `README.md`

## Support

For issues or questions:
1. Check `IMPLEMENTATION_SUMMARY.md` for architecture details
2. Review `API_DOCUMENTATION.md` for API specifications
3. Check application logs in `logs/` directory
4. Contact: api-support@example.com
