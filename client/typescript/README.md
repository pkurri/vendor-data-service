# Vendor Data Service TypeScript Client

A TypeScript/Node.js client library for interacting with the Vendor Data Service API with M2M (Machine-to-Machine) authentication support.

## Features

- **M2M Authentication**: OAuth2 Client Credentials flow support
- **Type-Safe API**: Full TypeScript type definitions
- **Retry Logic**: Automatic retry with exponential backoff
- **Promise-based**: Modern async/await API
- **Error Handling**: Comprehensive error types

## Installation

```bash
npm install @vendor/data-client
# or
yarn add @vendor/data-client
# or
pnpm add @vendor/data-client
```

## Usage

### Basic Setup

```typescript
import { VendorDataClient } from '@vendor/data-client';

const client = new VendorDataClient({
  baseUrl: 'https://api.vendor.com',
  clientId: 'your-client-id',
  clientSecret: 'your-client-secret',
  tokenUrl: 'https://auth.vendor.com/oauth/token',
});

// Perform search
const response = await client.search({
  firstName: 'John',
  lastName: 'Doe',
  dateOfBirth: '1990-01-01',
});

console.log(`Found ${response.totalRecords} records`);
```

### Advanced Configuration

```typescript
const client = new VendorDataClient({
  baseUrl: 'https://api.vendor.com',
  clientId: 'your-client-id',
  clientSecret: 'your-client-secret',
  tokenUrl: 'https://auth.vendor.com/oauth/token',
  scope: 'vendor.search',
  timeout: 60000,
  maxRetries: 3,
  retryDelay: 1000,
});
```

### Search Examples

```typescript
// Search by name and DOB
const result1 = await client.search({
  firstName: 'John',
  lastName: 'Doe',
  dateOfBirth: '1990-01-01',
  page: 1,
  pageSize: 50,
});

// Search by case number
const result2 = await client.search({
  caseNumber: 'CR-2024-12345',
  includeCharges: true,
  includeSentences: true,
});

// Search with date range
const result3 = await client.search({
  lastName: 'Smith',
  filingDateFrom: '2024-01-01',
  filingDateTo: '2024-12-31',
  county: 'MARION',
});

// Health check
const isHealthy = await client.healthCheck();
```

## API Reference

See the [API documentation](./docs/api.md) for detailed information.

## Requirements

- Node.js 18 or higher
- TypeScript 5.0+ (for development)

## License

MIT
