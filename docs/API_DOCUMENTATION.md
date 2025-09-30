# Vendor Data Service API Documentation

## Overview

The Vendor Data Service provides a secure, RESTful API for searching court case records. The service is authenticated using JWT tokens issued by the `vendor-auth-service`.

**Base URL**: `https://api.example.com/api/v1`

**API Version**: v1

## Authentication

All API endpoints require JWT bearer token authentication.

### Headers

```http
Authorization: Bearer <jwt_token>
X-Request-Id: <unique-request-id> (optional, for idempotency)
Content-Type: application/json
```

### Required Scopes

- `vendor.search` - Required for all search endpoints

### Obtaining a JWT Token

Contact the `vendor-auth-service` to obtain a JWT token:

```bash
POST https://auth.example.com/api/v1/authenticate/vendor/{vendorId}
Content-Type: application/json

{
  "username": "your-username",
  "password": "your-password"
}
```

## Endpoints

### 1. Search Cases (GET)

Search for court case records using query parameters.

**Endpoint**: `GET /api/v1/search`

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name_last` | string | No* | Last name (uppercase/normalized) |
| `name_first` | string | No* | First name |
| `dob` | date | No* | Date of birth (YYYY-MM-DD) |
| `ssn_last4` | string | No* | Last 4 digits of SSN (4 digits) |
| `filed_date_from` | date | No | Filed date from (YYYY-MM-DD) |
| `filed_date_to` | date | No | Filed date to (YYYY-MM-DD) |
| `county_codes` | integer[] | No | County codes (0-67) |
| `case_type` | string[] | No | Case types to filter |
| `include_dockets` | boolean | No | Include docket entries (default: false) |
| `include_charges` | boolean | No | Include charges (default: false) |
| `include_events` | boolean | No | Include court events (default: false) |
| `include_defendants` | boolean | No | Include defendants (default: true) |
| `page` | integer | No | Page number, 1-based (default: 1) |
| `page_size` | integer | No | Page size, max 500 (default: 100) |
| `sort_by` | string | No | Sort field |
| `sort_dir` | string | No | Sort direction: asc/desc (default: asc) |
| `match_mode` | string | No | Match mode: loose/exact (default: loose) |

\* At least one of `name_last` + `name_first`, `dob`, or `ssn_last4` must be provided.

**Example Request**:

```bash
GET /api/v1/search?name_last=SMITH&name_first=JOHN&dob=1980-01-15&page=1&page_size=50
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 2. Search Cases (POST)

Search for court case records using JSON body.

**Endpoint**: `POST /api/v1/search`

**Request Body**:

```json
{
  "name_last": "SMITH",
  "name_first": "JOHN",
  "dob": "1980-01-15",
  "ssn_last4": "1234",
  "filed_date_from": "2020-01-01",
  "filed_date_to": "2023-12-31",
  "county_codes": [12, 25],
  "case_type": ["CR", "CV"],
  "include_dockets": true,
  "include_charges": true,
  "include_events": false,
  "include_defendants": true,
  "page": 1,
  "page_size": 100,
  "sort_by": "filed_date",
  "sort_dir": "desc",
  "match_mode": "loose",
  "client_request_id": "req-12345"
}
```

**Example Request**:

```bash
POST /api/v1/search
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
X-Request-Id: req-12345

{
  "name_last": "SMITH",
  "name_first": "JOHN",
  "dob": "1980-01-15",
  "page": 1,
  "page_size": 50
}
```

## Response Format

### Success Response (200 OK)

```json
{
  "api_version": "v1",
  "client_request_id": "req-12345",
  "generated_at": "2025-09-29T22:00:00Z",
  "page": 1,
  "page_size": 100,
  "total_records_is_estimate": true,
  "warnings": [],
  "data": [
    {
      "case_id": "12345",
      "case_number": "2020-CF-001234",
      "ucn": "FL12345678",
      "county_id": 12,
      "court_type": "Circuit",
      "severity_code": "F",
      "case_type": "CR",
      "case_status_code": 1,
      "judge_code": "JUDGE001",
      "outstanding_warrant": false,
      "contested": true,
      "jury_trial": false,
      "filed_date": "2020-05-15",
      "disposition_date": "2021-03-20",
      "last_name": "SMITH",
      "first_name": "JOHN",
      "middle_name": "MICHAEL",
      "date_of_birth": "1980-01-15",
      "sex_code": "M",
      "race_code": "W",
      "charges": [
        {
          "charge_sequence_number": "001",
          "initial_filing_date": "2020-05-15",
          "offense_date": "2020-05-10",
          "initial_fl_statute_number": "812.014",
          "initial_fl_statute_description": "THEFT",
          "initial_charge_level_code": "F",
          "initial_charge_degree_code": "3",
          "prosecutor_fl_statute_number": "812.014",
          "prosecutor_fl_statute_description": "THEFT",
          "court_fl_statute_number": "812.014",
          "court_fl_statute_description": "THEFT",
          "sentences": [
            {
              "sentence_sequence_number": "001",
              "sentence_status_code": 1,
              "sentence_imposed_date": "2021-03-20",
              "sentence_code": 10,
              "judge_code_at_sentence": "JUDGE001"
            }
          ]
        }
      ],
      "dockets": [
        {
          "docket_id": "D001",
          "docket_action_date": "2020-05-15",
          "docket_code": "FILED",
          "standard_docket_code": 100,
          "docket_text": "Case filed"
        }
      ],
      "events": [
        {
          "event_id": "E001",
          "court_appearance_date": "2020-06-15T09:00:00Z",
          "judge_code": "JUDGE001",
          "court_event_description": "Arraignment",
          "standard_court_event_code": 200,
          "court_location": "Courtroom A",
          "court_room": "101"
        }
      ],
      "defendants": [
        {
          "party_id": "P001",
          "last_name": "SMITH",
          "first_name": "JOHN",
          "middle_name": "MICHAEL",
          "dob": "1980-01-15",
          "sex": "M",
          "race": "W",
          "aka": ["JOHNNY SMITH", "J. SMITH"]
        }
      ]
    }
  ]
}
```

### Error Responses

#### 400 Bad Request

```json
{
  "error_code": "MISSING_SEARCH_KEY",
  "message": "Provide at least one search key (name_last + name_first, dob, or ssn_last4)",
  "timestamp": "2025-09-29T22:00:00Z"
}
```

#### 401 Unauthorized

```json
{
  "error": "unauthorized",
  "error_description": "Full authentication is required to access this resource"
}
```

#### 403 Forbidden

```json
{
  "error": "insufficient_scope",
  "error_description": "The request requires higher privileges than provided by the access token"
}
```

#### 429 Too Many Requests

```json
{
  "error_code": "RATE_LIMIT_EXCEEDED",
  "message": "Too many requests. Please try again later.",
  "timestamp": "2025-09-29T22:00:00Z"
}
```

## Data Model

### Case Record

Complete case information including all nested objects.

**Fields**:

- `case_id` (string): Unique case identifier
- `case_number` (string): Court case number
- `ucn` (string): Uniform Case Number
- `county_id` (integer): County code (0-67)
- `court_type` (string): Type of court
- `severity_code` (string): Severity classification
- `case_type` (string): Case type code
- `filed_date` (date): Date case was filed
- `disposition_date` (date): Date of disposition
- `last_name` (string): Defendant last name
- `first_name` (string): Defendant first name
- `date_of_birth` (date): Defendant date of birth
- `charges` (array): Array of charge objects
- `dockets` (array): Array of docket entries
- `events` (array): Array of court events
- `defendants` (array): Array of defendant/party information

### Charge Object

**Fields**:

- `charge_sequence_number` (string): Charge sequence
- `initial_filing_date` (date): Initial filing date
- `offense_date` (date): Date of offense
- `initial_fl_statute_number` (string): Florida statute number
- `initial_fl_statute_description` (string): Statute description
- `initial_charge_level_code` (string): Charge level (F=Felony, M=Misdemeanor)
- `initial_charge_degree_code` (string): Degree of charge
- `sentences` (array): Array of sentence objects

### Sentence Object

**Fields**:

- `sentence_sequence_number` (string): Sentence sequence
- `sentence_status_code` (integer): Status code
- `sentence_imposed_date` (date): Date imposed
- `sentence_effective_date` (date): Effective date
- `sentence_code` (integer): Sentence type code
- `judge_code_at_sentence` (string): Judge code

### Docket Object

**Fields**:

- `docket_id` (string): Docket identifier
- `docket_action_date` (date): Action date
- `docket_code` (string): Docket code
- `standard_docket_code` (integer): Standard code
- `docket_text` (string): Docket text (max 8000 chars)

### Event Object

**Fields**:

- `event_id` (string): Event identifier
- `court_appearance_date` (datetime): Appearance date/time
- `judge_code` (string): Judge code
- `court_event_description` (string): Event description
- `standard_court_event_code` (integer): Standard event code
- `court_location` (string): Location
- `court_room` (string): Room number

### Defendant Object

**Fields**:

- `party_id` (string): Party identifier
- `last_name` (string): Last name
- `first_name` (string): First name
- `middle_name` (string): Middle name
- `dob` (date): Date of birth
- `sex` (string): Sex code
- `race` (string): Race code
- `aka` (array): Array of alias names

## Rate Limiting

- Rate limits are enforced per client/vendor
- Default: 100 requests per minute
- Rate limit headers included in response:
  - `X-RateLimit-Limit`
  - `X-RateLimit-Remaining`
  - `X-RateLimit-Reset`

## Idempotency

Use the `X-Request-Id` header to ensure idempotent requests. Duplicate request IDs within a 24-hour window will be rejected with a 409 Conflict error.

## Best Practices

1. **Always use HTTPS** in production
2. **Store JWT tokens securely** - never in client-side code
3. **Use pagination** for large result sets
4. **Include only needed data** using include flags
5. **Implement retry logic** with exponential backoff
6. **Monitor rate limits** to avoid throttling
7. **Validate input** before sending requests
8. **Handle errors gracefully** with proper error handling

## Support

For API support, contact: api-support@example.com

## Changelog

### v1.0.0 (2025-09-29)
- Initial release
- Complete JSON schema implementation
- JWT authentication integration
- Comprehensive search capabilities
- Nested object support (charges, sentences, dockets, events, defendants)
