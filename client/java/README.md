# Vendor Data Service Java Client

A Java client library for interacting with the Vendor Data Service API with M2M (Machine-to-Machine) authentication support.

## Features

- **M2M Authentication**: OAuth2 Client Credentials flow support
- **Type-Safe API**: Strongly typed request/response models
- **Retry Logic**: Automatic retry with exponential backoff
- **Connection Pooling**: Efficient HTTP connection management
- **Error Handling**: Comprehensive exception handling

## Installation

### Maven

```xml
<dependency>
    <groupId>com.vendor</groupId>
    <artifactId>vendor-data-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.vendor:vendor-data-client:1.0.0'
```

## Usage

### Basic Setup

```java
import com.vendor.client.VendorDataClient;
import com.vendor.client.config.ClientConfig;
import com.vendor.client.dto.SearchRequest;
import com.vendor.client.dto.SearchResponse;

import java.time.LocalDate;

// Configure the client for vendor-auth-server (M2M OAuth2)
ClientConfig config = ClientConfig.builder()
    .baseUrl("http://localhost:8081")  // vendor-data-service
    .clientId("m2m-client")            // Default M2M client from vendor-auth-server
    .clientSecret("m2m-secret")        // Default M2M secret
    .tokenUrl("http://localhost:9000/oauth2/token")  // vendor-auth-server token endpoint
    .scope("read write")               // M2M scopes
    .build();

// Create client instance
VendorDataClient client = new VendorDataClient(config);

// Perform search
SearchRequest request = SearchRequest.builder()
    .nameFirst("John")
    .nameLast("Doe")
    .dob(LocalDate.of(1990, 1, 1))
    .build();

SearchResponse response = client.search(request);
```

### Advanced Configuration

```java
ClientConfig config = ClientConfig.builder()
    .baseUrl("https://api.vendor.com")
    .clientId("your-client-id")
    .clientSecret("your-client-secret")
    .tokenUrl("https://auth.vendor.com/oauth/token")
    .scope("vendor.search")
    .connectTimeout(30000)
    .readTimeout(60000)
    .maxRetries(3)
    .retryDelay(1000)
    .build();
```

## Examples

See the `examples/` directory for complete working examples.

## Requirements

- Java 17 or higher
- Spring Boot 3.x (if using Spring integration)
