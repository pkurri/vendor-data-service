# Documentation Index

## Quick Navigation

### 🚀 Getting Started

1. **[README.md](README.md)** - Main project overview
   - Features and tech stack
   - Quick start guide
   - API endpoints
   - Data model overview

2. **[QUICK_START.md](QUICK_START.md)** - Detailed setup instructions
   - Prerequisites
   - Database setup
   - Configuration
   - Running the service

### 🔐 M2M Authentication

1. **[M2M_INTEGRATION_GUIDE.md](M2M_INTEGRATION_GUIDE.md)** - Complete M2M setup
   - Architecture diagram
   - vendor-auth-server setup
   - vendor-data-service configuration
   - Client library usage
   - Production deployment
   - Troubleshooting

2. **[M2M_SECURITY_GUIDE.md](M2M_SECURITY_GUIDE.md)** - Security best practices
   - Credential management
   - Environment variables
   - Network security
   - Token security
   - Production checklist

### 📦 Client Libraries

1. **[CLIENT_SETUP.md](CLIENT_SETUP.md)** - Client library setup
   - Java client installation
   - TypeScript client installation
   - Configuration examples
   - Usage examples

2. **[client/java/README.md](client/java/README.md)** - Java client documentation
   - Installation
   - Configuration
   - API reference
   - Examples

3. **[client/typescript/README.md](client/typescript/README.md)** - TypeScript client documentation
   - Installation
   - Configuration
   - API reference
   - Examples

### 📚 Reference

1. **[errorcode.md](errorcode.md)** - Error code reference
   - HTTP status codes
   - Error response format
   - Common errors
   - Troubleshooting

2. **[docs/API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md)** - Detailed API documentation
   - Request/response schemas
   - Field descriptions
   - Query parameters
   - Examples

## Documentation Structure

```text
vendor-data-service/
├── README.md                      # Main overview
├── QUICK_START.md                 # Setup guide
├── M2M_INTEGRATION_GUIDE.md       # M2M authentication setup
├── M2M_SECURITY_GUIDE.md          # Security best practices
├── CLIENT_SETUP.md                # Client library setup
├── errorcode.md                   # Error reference
├── DOCUMENTATION_INDEX.md         # This file
│
├── client/
│   ├── java/
│   │   └── README.md              # Java client docs
│   └── typescript/
│       └── README.md              # TypeScript client docs
│
└── docs/
    ├── API_DOCUMENTATION.md       # Detailed API docs
    └── sql/
        └── comprehensive_schema.sql
```

## Reading Order

### For First-Time Users

1. Start with **README.md** for overview
2. Follow **QUICK_START.md** to set up the service
3. Read **M2M_INTEGRATION_GUIDE.md** for authentication
4. Review **CLIENT_SETUP.md** to integrate client libraries

### For Developers

1. **README.md** - Understand the project
2. **docs/API_DOCUMENTATION.md** - API details
3. **CLIENT_SETUP.md** - Client integration
4. **errorcode.md** - Error handling

### For Security/DevOps

1. **M2M_SECURITY_GUIDE.md** - Security practices
2. **M2M_INTEGRATION_GUIDE.md** - Production deployment
3. **QUICK_START.md** - Configuration options

### For API Consumers

1. **CLIENT_SETUP.md** - Get started with clients
2. **client/*/README.md** - Language-specific docs
3. **M2M_SECURITY_GUIDE.md** - Secure integration
4. **errorcode.md** - Error handling

## Additional Resources

### Interactive Documentation

- **Swagger UI**: `http://localhost:8081/swagger-ui.html` (when service is running)
- **OpenAPI Spec**: `http://localhost:8081/v3/api-docs`

### Examples

- **Java Examples**: `client/java/src/main/java/com/vendor/client/examples/`
- **TypeScript Examples**: `client/typescript/examples/`

### Configuration

- **Environment Variables**: `.env.example`
- **Application Config**: `src/main/resources/application.yml`

## Support

For questions or issues:

1. Check **errorcode.md** for common errors
2. Review **M2M_INTEGRATION_GUIDE.md** troubleshooting section
3. Consult the relevant README for your use case
4. Check Swagger UI for interactive API testing

## Version Information

- **Service Version**: 1.0.0
- **API Version**: v1
- **Last Updated**: 2025-10-01
