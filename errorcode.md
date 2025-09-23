# Vendor-Facing API Error Codes (Final Canonical List)

| HTTP Status | Error Code              | Title / Description               | Typical Cause / When to Use              | Client Remediation                          |
|-------------|------------------------|---------------------------------|-----------------------------------------|--------------------------------------------|
| 400         | INVALID_REQUEST        | Malformed or invalid request     | Missing or invalid parameters           | Check request payload and parameters        |
| 401         | UNAUTHORIZED           | Missing or invalid credentials   | Auth token missing, expired, or invalid | Provide valid authentication token          |
| 403         | FORBIDDEN              | Not authorized to perform action | Access denied due to insufficient rights| Request necessary permissions                |
| 404         | NOT_FOUND              | Resource or endpoint not found   | Requested resource does not exist       | Verify resource identifier or endpoint URL  |
| 409         | CONFLICT               | Resource or ID conflict          | Duplicate request ID or conflicting data| Use unique request IDs or resolve conflicts |
| 413         | PAYLOAD_TOO_LARGE      | Request body exceeds size limit  | Payload size exceeds max configured limit| Reduce payload size                          |
| 415         | UNSUPPORTED_MEDIA_TYPE | Unsupported content type         | Content-Type header not application/json| Use Content-Type: application/json header   |
| 422         | UNPROCESSABLE_ENTITY   | Validation or business rule failed| Semantic or field validation errors     | Fix validation errors as detailed in response|
| 429         | RATE_LIMIT_EXCEEDED    | Too many requests                | Client exceeded allowed request quota   | Back off and retry after specified wait time|
| 500         | INTERNAL_SERVER_ERROR  | Server-side unexpected error     | Unhandled exception or failure          | Retry later or contact support               |
| 502         | UPSTREAM_ERROR         | Dependency server error          | Upstream service failure                 | Retry with backoff and monitor upstream     |
| 503         | SERVICE_UNAVAILABLE    | Temporary service downtime       | Maintenance or temporary failure         | Retry after some time                        |
| 504         | GATEWAY_TIMEOUT        | Gateway timeout                  | Upstream service timeout                  | Retry after delay                            |
