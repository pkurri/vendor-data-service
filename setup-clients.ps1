# Setup script for M2M clients
# Run this script to initialize and build the client libraries

Write-Host "=== Vendor Data Service - M2M Client Setup ===" -ForegroundColor Cyan

# Check if Gradle wrapper exists
if (-not (Test-Path "gradlew.bat")) {
    Write-Host "`nGenerating Gradle wrapper..." -ForegroundColor Yellow
    gradle wrapper --gradle-version 8.9
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Failed to generate Gradle wrapper. Please ensure Gradle is installed." -ForegroundColor Red
        exit 1
    }
}

# Build Java client
Write-Host "`n=== Building Java Client ===" -ForegroundColor Cyan
.\gradlew.bat :client:java:build
if ($LASTEXITCODE -ne 0) {
    Write-Host "Java client build failed!" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Java client built successfully" -ForegroundColor Green

# Setup TypeScript client
Write-Host "`n=== Setting up TypeScript Client ===" -ForegroundColor Cyan
Push-Location client\typescript

if (Test-Path "node_modules") {
    Write-Host "Dependencies already installed, skipping..." -ForegroundColor Yellow
} else {
    Write-Host "Installing dependencies..." -ForegroundColor Yellow
    npm install
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Failed to install TypeScript client dependencies!" -ForegroundColor Red
        Pop-Location
        exit 1
    }
}

Write-Host "Building TypeScript client..." -ForegroundColor Yellow
npm run build
if ($LASTEXITCODE -ne 0) {
    Write-Host "TypeScript client build failed!" -ForegroundColor Red
    Pop-Location
    exit 1
}

Pop-Location
Write-Host "✓ TypeScript client built successfully" -ForegroundColor Green

# Summary
Write-Host "`n=== Setup Complete ===" -ForegroundColor Cyan
Write-Host "
Client libraries are ready to use:

Java Client:
  - Location: client/java/
  - Documentation: client/java/README.md
  - Examples: client/java/src/main/java/com/vendor/client/examples/

TypeScript Client:
  - Location: client/typescript/
  - Documentation: client/typescript/README.md
  - Examples: client/typescript/examples/

Next Steps:
  1. Review CLIENT_SETUP.md for integration instructions
  2. Configure OAuth2 credentials (see .env.example)
  3. Run examples to test the clients
  4. Integrate into your applications

For more information, see:
  - README.md (main documentation)
  - CLIENT_SETUP.md (client setup guide)
  - QUICK_START.md (service setup)
" -ForegroundColor White

Write-Host "✓ All clients ready!" -ForegroundColor Green
