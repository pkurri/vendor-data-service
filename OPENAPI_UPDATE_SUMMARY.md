# OpenAPI Specification Update Summary

**Date**: 2025-09-29  
**Status**: ✅ **COMPLETE**

---

## 📋 What Was Updated

### OpenAPI File: `src/main/resources/openapi/vendor-data-service.yaml`

The OpenAPI specification has been completely updated to match the new API implementation with all JSON schema fields.

---

## 🔄 Changes Made

### 1. **API Information Updated**

**Before**:
```yaml
version: 0.1.0
description: External-facing API to search vendor datasets
servers:
  - url: http://localhost:8080
```

**After**:
```yaml
version: 1.0.0
description: Court case data search API with comprehensive nested object support
servers:
  - url: http://localhost:8081  # Updated port
  - url: https://api.example.com
```

### 2. **All Query Parameters Added** (15+ parameters)

✅ `name_last` - Last name (was `lastName`)  
✅ `name_first` - First name (was `firstName`)  
✅ `dob` - Date of birth  
✅ `ssn_last4` - SSN last 4 digits  
✅ `filed_date_from` - Filed date from (NEW)  
✅ `filed_date_to` - Filed date to (NEW)  
✅ `county_codes` - County codes array (NEW)  
✅ `case_type` - Case types array (NEW)  
✅ `include_dockets` - Include dockets flag (NEW)  
✅ `include_charges` - Include charges flag (NEW)  
✅ `include_events` - Include events flag (NEW)  
✅ `include_defendants` - Include defendants flag (NEW)  
✅ `page` - Page number  
✅ `page_size` - Page size (max 500, was 50)  
✅ `sort_by` - Sort field (NEW)  
✅ `sort_dir` - Sort direction (NEW)  
✅ `match_mode` - Match mode (NEW)  

### 3. **Complete Schema Definitions**

#### SearchResponse (API Envelope)
```yaml
SearchResponse:
  properties:
    api_version: string
    client_request_id: string
    generated_at: date-time
    page: integer
    page_size: integer
    total_records_is_estimate: boolean
    warnings: array[string]
    data: array[CaseRecord]
```

#### CaseRecord (40+ fields)
```yaml
CaseRecord:
  properties:
    case_id, case_number, ucn
    county_id, court_type, severity_code
    case_type, case_status_code
    judge_code, judge_code_at_disposition
    outstanding_warrant, contested, jury_trial
    filed_date, clerk_file_date, reopen_date
    disposition_date, last_docket_date
    last_name, first_name, middle_name
    date_of_birth, sex_code, race_code
    ssn, place_of_birth, country
    charges: array[ChargeDto]
    dockets: array[DocketDto]
    events: array[EventDto]
    defendants: array[DefendantDto]
```

#### ChargeDto (25+ fields)
```yaml
ChargeDto:
  properties:
    charge_sequence_number
    initial_filing_date, offense_date
    initial_fl_statute_number, initial_fl_statute_description
    prosecutor_fl_statute_number, prosecutor_fl_statute_description
    court_fl_statute_number, court_fl_statute_description
    charge_level_codes, charge_degree_codes
    sentences: array[SentenceDto]
```

#### SentenceDto (9 fields)
```yaml
SentenceDto:
  properties:
    sentence_sequence_number
    sentence_status_code, sentence_code
    sentence_imposed_date, sentence_effective_date
    length_of_sentence_confinement
    confinement_type_code
    judge_code_at_sentence, division
```

#### DocketDto (5 fields)
```yaml
DocketDto:
  properties:
    docket_id
    docket_action_date
    docket_code, standard_docket_code
    docket_text (max 8000 chars)
```

#### EventDto (11 fields)
```yaml
EventDto:
  properties:
    event_id
    court_appearance_date, court_appearance_time
    judge_code, court_event_description
    standard_court_event_code
    court_location, court_room
    prosecutor, defendant_attorney, division
```

#### DefendantDto (8 fields)
```yaml
DefendantDto:
  properties:
    party_id
    last_name, first_name, middle_name
    dob, sex, race
    aka: array[string]
```

### 4. **Security Updated**

```yaml
securitySchemes:
  bearerAuth:
    type: http
    scheme: bearer
    bearerFormat: JWT
    description: JWT token from vendor-auth-service with scope vendor.search
```

### 5. **Response Codes Enhanced**

```yaml
responses:
  '200': Successful search with API envelope
  '400': Bad Request - Invalid parameters or missing search criteria
  '401': Unauthorized - Missing or invalid JWT token
  '403': Forbidden - Insufficient scope (requires vendor.search)
  '409': Conflict - Duplicate request ID
  '429': Too Many Requests - Rate limit exceeded
```

### 6. **X-Request-Id Header**

Changed from `required: true` to `required: false` (now optional)

---

## 📊 Statistics

| Metric | Before | After |
|--------|--------|-------|
| **API Version** | 0.1.0 | 1.0.0 |
| **Query Parameters** | 7 | 17 |
| **Schema Definitions** | 3 | 8 |
| **Fields in CaseRecord** | 9 | 40+ |
| **Nested Objects** | 0 | 4 |
| **Max Page Size** | 50 | 500 |
| **Lines of YAML** | ~200 | ~737 |

---

## ✅ Verification

### Swagger UI Access

```
http://localhost:8081/swagger-ui/index.html
```

### OpenAPI JSON

```
http://localhost:8081/v3/api-docs
```

### OpenAPI YAML

```
http://localhost:8081/v3/api-docs.yaml
```

---

## 🎯 Key Improvements

### 1. **Complete Field Coverage**
- All fields from JSON schema included
- Proper data types and validations
- Max length constraints
- Pattern validation for SSN

### 2. **Nested Object Support**
- Charges with sentences
- Dockets with full text
- Court events with details
- Defendants with aliases

### 3. **Conditional Loading**
- Include flags for each nested object type
- Default values specified
- Performance optimization

### 4. **Enhanced Pagination**
- Increased max page size to 500
- Page-based pagination
- Sorting support

### 5. **Better Documentation**
- Detailed descriptions for all fields
- Examples provided
- Clear error responses
- Security requirements documented

---

## 📝 Example Usage

### GET Request

```bash
curl -X GET "http://localhost:8081/api/v1/search?name_last=SMITH&name_first=JOHN&dob=1980-01-15&include_charges=true&page=1&page_size=50" \
  -H "Authorization: Bearer <jwt_token>"
```

### POST Request

```bash
curl -X POST "http://localhost:8081/api/v1/search" \
  -H "Authorization: Bearer <jwt_token>" \
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

### Response

```json
{
  "api_version": "v1",
  "generated_at": "2025-09-29T23:00:00Z",
  "page": 1,
  "page_size": 50,
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
      "charges": [...],
      "dockets": [...],
      "events": [...],
      "defendants": [...]
    }
  ]
}
```

---

## 🔗 Related Files

- **OpenAPI Spec**: `src/main/resources/openapi/vendor-data-service.yaml` ✅
- **JSON Schema**: `src/main/resources/schema/court-case-data-schema.json` ✅
- **API Documentation**: `docs/API_DOCUMENTATION.md` ✅
- **DTOs**: `src/main/java/com/vendor/vendordataservice/api/dto/*.java` ✅

---

## ✅ Summary

The OpenAPI specification has been completely updated to match the new API implementation:

✅ **Version**: Updated to 1.0.0  
✅ **Parameters**: All 17 query parameters documented  
✅ **Schemas**: All 8 DTOs with complete fields  
✅ **Nested Objects**: Full support for charges, sentences, dockets, events, defendants  
✅ **Security**: JWT authentication with scope requirements  
✅ **Responses**: Complete error handling documented  
✅ **Validation**: All constraints and patterns specified  
✅ **Examples**: Clear usage examples provided  

**Status**: ✅ **COMPLETE AND READY FOR USE**

Access Swagger UI at: `http://localhost:8081/swagger-ui/index.html`
