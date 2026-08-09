# HTTP Essentials for Testers

## API Testing Wizard — Senior SDET / QA Architect Level

> **Target Level:** SDET / QA Architect  
> **Focus:** HTTP fundamentals, request/response analysis, HTTP semantics, idempotency, headers, status codes, content negotiation, performance, resilience, and production-grade API testing.

---

## Table of Contents

1. [HTTP Request](#1-http-request)
2. [HTTP Methods](#2-http-methods)
3. [Safe vs Idempotent Methods](#3-safe-vs-idempotent-methods)
4. [Idempotency](#4-idempotency)
5. [POST vs PUT](#5-post-vs-put)
6. [Idempotency Keys](#6-idempotency-keys)
7. [URL Anatomy](#7-url-anatomy)
8. [Path Parameters](#8-path-parameters)
9. [Query Parameters](#9-query-parameters)
10. [Path vs Query Parameters](#10-path-vs-query-parameters)
11. [HTTP Headers](#11-http-headers)
12. [Content-Type vs Accept](#12-content-type-vs-accept)
13. [Cookies](#13-cookies)
14. [Request Body](#14-request-body)
15. [Response Anatomy](#15-response-anatomy)
16. [HTTP Status Codes](#16-http-status-codes)
17. [4xx vs 5xx](#17-4xx-vs-5xx)
18. [401 vs 403](#18-401-vs-403)
19. [404 vs 400](#19-404-vs-400)
20. [Response Content-Type](#20-response-content-type)
21. [Response Timing](#21-response-timing)
22. [Average vs Percentile Latency](#22-average-vs-percentile-latency)
23. [API Testing Validation Pyramid](#23-api-testing-validation-pyramid)
24. [Production-Grade API Test](#24-production-grade-api-test)
25. [REST Assured Example](#25-rest-assured-example)
26. [Senior API Testing Mindset](#26-senior-api-testing-mindset)
27. [HTTP Request Testing Checklist](#27-http-request-testing-checklist)
28. [HTTP Response Testing Checklist](#28-http-response-testing-checklist)
29. [Negative Testing](#29-negative-testing)
30. [Boundary Testing](#30-boundary-testing)
31. [Security Testing](#31-security-testing)
32. [Resilience Testing](#32-resilience-testing)
33. [API Test Design](#33-api-test-design)
34. [Common API Testing Mistakes](#34-common-api-testing-mistakes)
35. [Senior-Level API Testing Principles](#35-senior-level-api-testing-principles)
36. [Final API Testing Mental Model](#36-final-api-testing-mental-model)

---

# 1. HTTP Request

An HTTP request is the message sent by a client to a server.

```text
HTTP Request
│
├── Method
│
├── Request Target
│   ├── Scheme
│   ├── Host
│   ├── Port
│   ├── Path
│   └── Query Parameters
│
├── Headers
├── Cookies
└── Body
```

## Example

```http
POST https://api.example.com:443/v1/orders/123?expand=payment
Authorization: Bearer <token>
Content-Type: application/json
Accept: application/json
X-Correlation-ID: 8f91a
Cookie: sessionId=abc123

{
  "quantity": 2,
  "paymentMethod": "CARD"
}
```

What does each part of the request means?

| Component | Purpose |
|---|---|
| Method | Defines intended operation |
| Scheme | Defines communication protocol |
| Host | Identifies target server |
| Port | Identifies network port |
| Path | Identifies resource |
| Query Parameters | Modifies/filter retrieval |
| Headers | Metadata and instructions |
| Cookies | Client/session state |
| Body | Payload/data |

---

# 2. HTTP Methods

HTTP methods communicate the intended operation.

| Method | Typical Purpose | Safe | Idempotent |
|---|---|---:|---:|
| `GET` | Retrieve resource | Yes | Yes |
| `HEAD` | Retrieve headers | Yes | Yes |
| `OPTIONS` | Discover capabilities | Yes | Yes |
| `POST` | Create/process/action | No | No* |
| `PUT` | Create/replace resource | No | Yes |
| `PATCH` | Partial modification | No | Not inherently |
| `DELETE` | Delete resource | No | Yes |
| `TRACE` | Diagnostic operation | Yes | Yes |
| `CONNECT` | Establish tunnel | No | No |

> `POST` is generally non-idempotent, but an application can implement idempotent POST behavior using an idempotency key.

## GET

Used to retrieve a representation of a resource.

```http
GET /users/123
```

Typical validation:

```text
200 OK
Correct response schema
Correct headers
Correct business data
Authorization
Caching behavior
Performance
```

## POST

Typically used for:

- Creating resources
- Triggering operations
- Processing commands
- Submitting data

```http
POST /users
```

## PUT

Typically used to create or completely replace a resource representation at a known URI.

```http
PUT /users/123
```

## PATCH

Used for partial modification.

```http
PATCH /users/123
```

PATCH is **not inherently idempotent**. Its idempotency depends on the semantics of the specific operation.

## DELETE

Used to remove a resource.

```http
DELETE /users/123
```

A subsequent DELETE may return `404 Not Found`, while DELETE remains idempotent because the resource remains absent.

---

# 3. Safe vs Idempotent Methods

These concepts are frequently confused.

## Safe Method

A method is **safe** when the requested operation is intended to be read-only with respect to the target resource.

Common safe methods:

```text
GET
HEAD
OPTIONS
TRACE
```

Example:

```http
GET /users/123
```

The request is not intended to modify user `123`.

## Safe Does Not Mean "No Server Activity"

A GET request may still generate:

- Access logs
- Metrics
- Audit records
- Cache entries
- Monitoring events

Therefore:

> Safe means the requested operation is not intended to modify the target resource. It does not mean the server performs zero internal work.

---

# 4. Idempotency

An operation is **idempotent** when making the same request multiple times has the same intended effect on server state as making it once.

Conceptually:

```text
f(f(x)) = f(x)
```

Common idempotent methods:

```text
GET
HEAD
OPTIONS
TRACE
PUT
DELETE
```

## Important Distinction

Idempotency is about:

```text
SERVER STATE
```

It does **not** require:

```text
IDENTICAL RESPONSE
```

## DELETE Example

First request:

```http
DELETE /users/100
```

```text
204 No Content
```

Second request:

```http
DELETE /users/100
```

```text
404 Not Found
```

The responses differ, but the final state remains:

```text
User 100 does not exist
```

Therefore DELETE remains idempotent.

## Testing Idempotency

For an idempotent operation:

```text
Request 1
   ↓
State A → State B

Request 2
   ↓
State B → State B

Request 3
   ↓
State B → State B
```

A senior tester validates the resulting state, not merely status codes.

---

# 5. POST vs PUT

## POST

```http
POST /users
```

The server generally determines the resource identifier.

```text
POST /users
    ↓
User 101

POST /users
    ↓
User 102
```

Repeated POST requests may create multiple resources.

Therefore:

```text
POST → Generally non-idempotent
```

## PUT

```http
PUT /users/101
```

The client targets a known resource.

Repeated identical requests should produce the same final state.

```text
PUT /users/101
    ↓
Desired state of user 101
```

Therefore:

```text
PUT → Idempotent
```

## Pointers of Distinction

| POST | PUT |
|---|---|
| Usually collection/action oriented | Specific resource |
| Server may generate identifier | Client identifies target URI |
| Generally non-idempotent | Idempotent |
| Commonly creates/initiates | Creates/replaces |
| Repeated requests may create duplicates | Repeated identical requests produce same state |

---

# 6. Idempotency Keys

Idempotency is particularly important for:

- Payments
- Orders
- Money transfers
- Bookings
- Subscription creation
- Resource provisioning

Example:

```http
POST /payments
Idempotency-Key: 7d8f9a21
Content-Type: application/json
```

```json
{
  "amount": 5000,
  "currency": "INR"
}
```

Suppose the payment succeeds but the client experiences a network timeout.

The client cannot determine whether the operation completed.

It retries:

```http
POST /payments
Idempotency-Key: 7d8f9a21
```

The server recognizes the key and avoids creating a second logical transaction.

## Expected Flow

```text
First request
      ↓
Payment created
      ↓
Response lost
      ↓
Client retries
      ↓
Same Idempotency-Key
      ↓
Server recognizes previous operation
      ↓
No duplicate payment
```

## Idempotency Test Matrix

Test:

```text
Same key + same payload
Same key + different payload
Different key + same payload
Missing key
Empty key
Malformed key
Expired key
Concurrent requests using same key
Retry after timeout
Retry after 500
Retry after 502
Retry after 503
Retry after 504
```

---

# 7. URL Anatomy

Consider:

```text
https://api.example.com:443/v1/orders/123/items?status=active&limit=10
```

Breakdown:

```text
https
 ↓
Scheme

api.example.com
 ↓
Host

443
 ↓
Port

/v1/orders/123/items
 ↓
Path

?status=active&limit=10
 ↓
Query String
```

## Scheme

```text
https
```

Indicates HTTPS communication.

## Host

```text
api.example.com
```

Identifies the server/domain.

## Port

```text
443
```

HTTPS commonly uses port `443`.

HTTP commonly uses port `80`.

## Path

```text
/v1/orders/123/items
```

Identifies the resource hierarchy.

## Query String

```text
?status=active&limit=10
```

Contains query parameters.

---

# 8. Path Parameters

Path parameters identify a resource or resource hierarchy.

Example:

```http
GET /users/123
```

Here:

```text
123
↓
Path Parameter
```

Another example:

```http
GET /users/123/orders/456
```

Conceptually:

```text
User
 └── 123
      └── Order
           └── 456
```

## Path Parameter Test Cases

Test:

```text
Valid ID
Non-existing ID
Zero
Negative ID
Very large ID
Invalid data type
Special characters
URL-encoded values
Missing ID
Unauthorized resource ID
Resource belonging to another user
```

## IDOR / BOLA Testing

Suppose:

```http
GET /users/100/orders
```

works for the authenticated user.

Change it to:

```http
GET /users/101/orders
```

If the user can access another user's data, investigate:

```text
Insecure Direct Object Reference (IDOR)
Broken Object Level Authorization (BOLA)
```

---

# 9. Query Parameters

Query parameters commonly control:

- Filtering
- Searching
- Sorting
- Pagination
- Projection
- Expansion

## Filtering

```http
GET /users?country=IN&status=ACTIVE
```

## Pagination

```http
GET /users?page=2&limit=50
```

## Sorting

```http
GET /users?sort=createdAt&order=desc
```

## Searching

```http
GET /users?search=tanishq
```

## Expansion

```http
GET /orders/123?expand=payment
```

---

# 10. Path vs Query Parameters

## Path Parameter

```http
GET /users/123
```

Usually identifies:

```text
Which resource?
```

## Query Parameter

```http
GET /users?id=123
```

Usually controls:

```text
How should the resource collection be retrieved?
```

## Mental Model

```text
PATH
 ↓
Resource identity

QUERY
 ↓
Filtering / searching / sorting / pagination / representation options
```

---

# 11. HTTP Headers

Headers provide metadata and instructions.

Example:

```http
Authorization: Bearer <token>
Content-Type: application/json
Accept: application/json
User-Agent: PostmanRuntime/7
X-Correlation-ID: abc123
Cache-Control: no-cache
```

## Important Headers

| Header | Purpose |
|---|---|
| `Authorization` | Authentication credentials |
| `Content-Type` | Request representation |
| `Accept` | Desired response representation |
| `Cookie` | Client/session state |
| `Location` | Resource location |
| `ETag` | Resource/entity version |
| `If-Match` | Conditional update |
| `If-None-Match` | Conditional request |
| `Cache-Control` | Cache behavior |
| `Retry-After` | Retry guidance |
| `X-Correlation-ID` | Request tracing |

## Authorization

Example:

```http
Authorization: Bearer eyJ...
```

Test:

```text
Missing token
Invalid token
Expired token
Malformed token
Insufficient scope
Tampered token
```

## Correlation ID

In microservices:

```text
Client
  ↓
API Gateway
  ↓
Order Service
  ↓
Payment Service
  ↓
Notification Service
```

A correlation ID allows a single request to be traced across services.

Example:

```http
X-Correlation-ID: abc123
```

A senior tester should verify that the ID is correctly generated, propagated and available for troubleshooting without exposing sensitive information.

---

# 12. Content-Type vs Accept

## Content-Type

`Content-Type` describes the representation being sent.

```http
Content-Type: application/json
```

Body:

```json
{
  "name": "Tanishq"
}
```

## Accept

`Accept` communicates the response representation the client can accept or prefers.

```http
Accept: application/json
```

## Mental Model

```text
Content-Type
     ↓
What am I sending?

Accept
     ↓
What response representation do I want?
```

## Can They Be Different?

Yes.

```http
Content-Type: application/json
Accept: application/xml
```

Meaning:

```text
Request body
    ↓
JSON

Preferred response
    ↓
XML
```

## Wrong Content-Type

Suppose the API expects:

```http
Content-Type: application/json
```

but receives:

```http
Content-Type: text/plain
```

Possible response:

```text
415 Unsupported Media Type
```

A Tester should validate:

```text
Status code
Error schema
Error message
Response Content-Type
Correlation ID
No unintended state change
```

---

# 13. Cookies

Cookies allow state to be maintained across requests.

Example:

```http
Cookie: sessionId=abc123
```

Common use cases:

- Session management
- Authentication
- Personalization
- Tracking
- CSRF-related mechanisms

## Important Cookie Attributes

### Secure

Cookie should only be transmitted over secure connections.

### HttpOnly

Helps prevent client-side scripts from accessing the cookie.

### SameSite

Controls cross-site cookie behavior.

### Domain

Defines applicable domain scope.

### Path

Defines applicable URL path scope.

### Expires / Max-Age

Controls cookie lifetime.

## Cookie Testing

Validate:

```text
Cookie creation
Cookie expiration
Cookie deletion
Secure
HttpOnly
SameSite
Domain
Path
Logout invalidation
Session reuse
Session fixation
```

---

# 14. Request Body

The request body contains application data.

Example:

```json
{
  "name": "Tanishq",
  "email": "test@example.com",
  "age": 28
}
```

A senior tester validates:

```text
Schema
Required fields
Data types
Nullability
Boundary values
Business rules
Unexpected fields
Injection payloads
```

## Schema Validation

```text
name  → String
email → String
age   → Integer
```

## Required Fields

Test:

```text
Missing name
Missing email
Missing age
```

## Null Values

```json
{
  "name": null
}
```

Determine whether `null` is:

```text
Allowed
Rejected
Treated differently from missing
```

## Empty Values

```json
{
  "name": ""
}
```

Do not assume:

```text
null == empty string == missing field
```

They can have different meanings.

## Boundary Values

For `age`:

```text
-1
0
1
Minimum allowed
Minimum - 1
Maximum allowed
Maximum + 1
Very large value
```

## Unexpected Fields

```json
{
  "name": "Tanishq",
  "admin": true
}
```

Verify whether the API:

```text
Rejects it
Ignores it
Persists it
Accidentally interprets it
```

Unexpected fields can become a security concern when APIs use unsafe object binding.

---

# 15. Response Anatomy

An HTTP response contains:

```text
HTTP Response
│
├── Status Code
├── Headers
├── Cookies
├── Body
└── Timing
```

Example:

```http
HTTP/1.1 200 OK
Content-Type: application/json
X-Correlation-ID: abc123

{
  "id": 123,
  "name": "Tanishq"
}
```

## Response Validation

A mature test should consider:

```text
Status
Headers
Cookies
Content-Type
Schema
Field values
Business rules
Response time
Security
```

---

# 16. HTTP Status Codes

Do not merely memorize status codes. Understand their meaning and expected behavior.

## Status Code Classes

| Range | Category |
|---|---|
| `1xx` | Informational |
| `2xx` | Successful |
| `3xx` | Redirection |
| `4xx` | Client/request issue |
| `5xx` | Server-side failure |

## 1xx — Informational

Examples:

```text
100 Continue
101 Switching Protocols
```

These are less common in typical REST API automation.

## 2xx — Success

Important codes:

```text
200 OK
201 Created
202 Accepted
204 No Content
206 Partial Content
```

## 200 OK

The request was successfully processed.

But:

> `200` alone does not prove that the API is functionally correct.

Also validate:

```text
Response schema
Business data
Headers
Security
Performance
```

## 201 Created

Indicates successful creation of a resource.

Potential expectations:

```text
Status = 201
Resource exists
Correct representation
Correct resource identifier
Location header if contract specifies it
```

## 202 Accepted

Indicates:

> The request has been accepted for processing, but processing may not be complete.

Example:

```text
POST /reports
       ↓
202 Accepted
       ↓
Background processing
       ↓
GET /reports/123
       ↓
PROCESSING
       ↓
COMPLETED
```

Do not immediately assert that the final business operation has completed.

## 204 No Content

Indicates successful processing with no response body.

```http
DELETE /users/123
```

```text
204 No Content
```

Validate:

```text
Status = 204
Body = empty
```

Avoid blindly parsing JSON.

---

# 17. 4xx vs 5xx

## 4xx

Generally indicates a client/request-side issue.

Examples:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
405 Method Not Allowed
409 Conflict
415 Unsupported Media Type
422 Unprocessable Content
429 Too Many Requests
```

## 5xx

Generally indicates server-side failure.

Examples:

```text
500 Internal Server Error
501 Not Implemented
502 Bad Gateway
503 Service Unavailable
504 Gateway Timeout
```

## Seasoned Testing Principle

A `4xx` response should not automatically be considered a defect.

Example:

```text
Invalid user input
       ↓
400
```

That may be correct behavior.

Similarly:

```text
Missing authorization
       ↓
401
```

may be exactly what the API contract requires.

The tester must validate the expected behavior, not simply search for `2xx`.

---

# 18. 401 vs 403

## 401 Unauthorized

Generally indicates an authentication problem.

Mental model:

> **Who are you?**

Examples:

```text
Missing token
Invalid token
Expired token
Malformed token
```

## 403 Forbidden

Generally indicates that the request is understood but the caller is not permitted to perform the operation.

Mental model:

> **I know who you are, but you cannot do this.**

Example:

```text
Regular User
     ↓
DELETE /admin/users/123
     ↓
403 Forbidden
```

## Testing Matrix

```text
No token
    ↓
401

Invalid token
    ↓
401

Expired token
    ↓
401

Valid token + insufficient permission
    ↓
403

Valid token + sufficient permission
    ↓
Expected success
```

> Exact status-code behavior should always follow the API's contract and authentication scheme.

---

# 19. 404 vs 400

## 400 Bad Request

The request itself is invalid or malformed.

Example:

```http
GET /users/abc
```

when the endpoint requires an integer identifier.

Potential response:

```text
400 Bad Request
```

## 404 Not Found

The requested resource cannot be found.

Example:

```http
GET /users/999999
```

when user `999999` does not exist.

Potential response:

```text
404 Not Found
```

## Mental Model

```text
400
↓
The request is invalid.

404
↓
The requested resource cannot be found.
```

Exact behavior depends on the API contract.

---

# 20. Response Content-Type

Never assume every API response is JSON.

Possible responses:

```http
Content-Type: application/json
```

```http
Content-Type: application/xml
```

```http
Content-Type: text/plain
```

```http
Content-Type: text/html
```

## Why It Matters

Bad automation:

```java
response.jsonPath();
```

Imagine the server returns:

```http
500 Internal Server Error
Content-Type: text/html
```

Body:

```html
<html>
    <body>Internal Server Error</body>
</html>
```

Blind JSON parsing may produce:

```text
JSON Parsing Exception
```

The real failure is:

```text
HTTP 500
```

The automation has hidden the actual failure behind a parsing failure.

## Better Framework Design

```java
String contentType = response.getContentType();

if (contentType != null &&
    contentType.contains("application/json")) {

    // Parse JSON

} else {

    // Handle non-JSON response
}
```

A reusable framework should understand the response representation before parsing it.

---

# 21. Response Timing

API validation should not stop at:

```text
Status = 200
```

Response performance may include:

```text
DNS lookup
Connection establishment
TLS handshake
Server processing
Time to first byte
Data transfer
Total response time
```

## Functional vs Performance Validation

An API can be:

```text
Functionally correct
```

but:

```text
Performance unacceptable
```

Example:

```text
Expected SLA: < 500 ms
Actual: 4 seconds
```

The response may still be:

```text
200 OK
```

but it violates the performance requirement.

---

# 22. Average vs Percentile Latency

Suppose:

```text
Requests = 10,000
Average = 300 ms
```

This does not tell the complete story.

You might have:

```text
p50 = 200 ms
p95 = 900 ms
p99 = 4 sec
```

This indicates significant tail latency.

## Important Percentiles

### p50

50% of requests are at or below this latency.

Also known as:

```text
Median
```

### p90

90% of requests are at or below this latency.

### p95

95% of requests are at or below this latency.

### p99

99% of requests are at or below this latency.

## Example SLA

```text
GET /users

p95 < 500 ms
p99 < 1000 ms
```

Suppose:

```text
p50 = 200 ms
p95 = 450 ms
p99 = 3 sec
```

Then:

```text
p95 SLA → PASS
p99 SLA → FAIL
```

A seasoned tester should report the distinction instead of saying simply:

```text
API is slow
```

---

# 23. API Testing Validation Pyramid

A production-grade API test strategy should cover multiple dimensions.

```text
                    API TEST
                       │
       ┌───────────────┼───────────────┐
       │               │               │
   Transport       Contract        Business
       │               │               │
    Method          Schema           Rules
    Status          Types            Calculations
    Headers         Fields           State
    Cookies         Required         Workflow
       │               │               │
       └───────────────┼───────────────┘
                       │
                    Security
                       │
             Authentication
             Authorization
             Injection
             Data exposure
                       │
                  Performance
                       │
             Latency / SLA / Load
                       │
                   Resilience
                       │
             Retry / Timeout
             Dependency failure
             Concurrency
```

## Transport Validation

Validate:

```text
HTTP method
URL
Status code
Headers
Cookies
Protocol expectations
```

## Contract Validation

Validate:

```text
Schema
Data types
Required fields
Optional fields
Nullable fields
Content-Type
Backward compatibility
```

## Business Validation

Validate:

```text
Business rules
Calculations
State transitions
Workflow
Cross-field relationships
```

## Security Validation

Validate:

```text
Authentication
Authorization
Object-level access
Injection
Sensitive data exposure
Token handling
Rate limiting
```

## Performance Validation

Validate:

```text
Response time
p50
p95
p99
Throughput
SLA
```

## Resilience Validation

Validate:

```text
Timeout
Retry
Dependency failure
Network failure
Duplicate requests
Concurrency
Partial failure
Recovery
```

---

# 24. Production-Grade API Test

Consider:

```http
POST /v1/orders
```

Request:

```json
{
  "productId": "P100",
  "quantity": 2,
  "paymentMethod": "CARD"
}
```

A basic API test might verify:

```text
201 Created
```

A seasoned-level test should validate multiple dimensions.

## Transport Validation

```text
HTTP method = POST
Status = 201
Correct headers
Correct Content-Type
```

## Contract Validation

Example response:

```json
{
  "orderId": "ORD-123",
  "status": "CREATED",
  "createdAt": "2026-08-09T10:30:00Z"
}
```

Validate:

```text
orderId → String
status → String
createdAt → ISO timestamp
```

## Business Validation

If:

```text
Unit price = ₹500
Quantity = 2
```

Then:

```text
Expected total = ₹1000
```

Validate according to the business contract:

```text
Quantity
Total
Status
Tax
Discount
Currency
```

## State Validation

An order creation may trigger:

```text
Order created
Inventory reduced
Payment initiated
Notification generated
```

The API test should determine which state transitions are contractually guaranteed.

## Security Validation

Test:

```text
Missing token
Invalid token
Expired token
Insufficient permissions
Another user's order
Tampered identifiers
```

## Negative Testing

Test:

```text
quantity = 0
quantity = -1
Invalid product
Out-of-stock product
Invalid payment method
Missing productId
Missing quantity
Invalid data type
Malformed JSON
```

## Resilience Testing

Test:

```text
Payment timeout
Inventory service unavailable
Network timeout
Duplicate request
Retry
Concurrent requests
```

---

# 25. REST Assured Example

Basic test:

```java
given()
    .baseUri("https://api.example.com")
    .header("Authorization", "Bearer " + token)
    .header("Content-Type", "application/json")
    .header("Accept", "application/json")
    .body(requestBody)
.when()
    .post("/v1/orders")
.then()
    .statusCode(201)
    .contentType(ContentType.JSON)
    .body("status", equalTo("CREATED"))
    .body("orderId", notNullValue());
```

## Better Senior-Level Structure

Avoid creating one enormous test that validates everything.

Prefer logical layers:

```text
Transport Validation
        ↓
Contract Validation
        ↓
Business Validation
        ↓
Security Validation
        ↓
Performance Validation
        ↓
Resilience Validation
```

Benefits:

```text
Better diagnostics
Better maintainability
Reusable validators
Cleaner reporting
Easier debugging
```

---

# 26. Seasoned API Testing Mindset

The biggest difference between basic API testing and senior API testing is the level of reasoning.

## Basic Tester

> Does the API return `200`?

## Experienced Tester

> Is the HTTP method correct?

## Senior Tester

> Is the operation safe and idempotent?

## Production-Focused Tester

> What happens if the client retries?

## Distributed-Systems Tester

> What happens if the server processes the request but the response is lost?

## QA Architect

> Can the automation distinguish transport failure, contract failure, business failure, dependency failure, infrastructure failure and performance failure?

---

# 27. HTTP Request Testing Checklist

For every endpoint, validate the request.

## Method

```text
☐ Correct HTTP method
☐ Method matches API semantics
☐ Method is safe/idempotent where expected
```

## URL

```text
☐ Correct scheme
☐ Correct host
☐ Correct port
☐ Correct API version
☐ Correct path
☐ Correct path parameters
☐ Correct query parameters
```

## Headers

```text
☐ Authorization
☐ Content-Type
☐ Accept
☐ Correlation ID
☐ Required custom headers
```

## Cookies

```text
☐ Required cookies
☐ Session state
☐ Cookie scope
```

## Body

```text
☐ Valid schema
☐ Required fields
☐ Correct data types
☐ Correct nullability
☐ Boundary values
☐ Unexpected fields
☐ Malformed payload
```

---

# 28. HTTP Response Testing Checklist

## Status

```text
☐ Expected status code
☐ Correct error status
☐ Correct success status
```

## Headers

```text
☐ Content-Type
☐ Cache-Control
☐ Location
☐ ETag
☐ Retry-After
☐ Correlation ID
```

## Body

```text
☐ Schema
☐ Required fields
☐ Data types
☐ Values
☐ Business rules
☐ Error structure
```

## Cookies

```text
☐ Cookie creation
☐ Cookie deletion
☐ Expiration
☐ Security attributes
```

## Performance

```text
☐ Response time
☐ SLA
☐ Percentiles
```

---

# 29. Negative Testing

Negative testing validates how the API behaves when something goes wrong.

A mature API suite should intentionally send invalid requests.

## Invalid Authentication

```text
Missing token
Invalid token
Expired token
Malformed token
```

## Invalid Input

```text
Missing required field
Wrong data type
Null
Empty string
Negative value
Out-of-range value
Malformed value
```

## Invalid Resource

```text
Non-existing ID
Deleted resource
Unauthorized resource
```

## Invalid HTTP Behavior

```text
Unsupported method
Unsupported Content-Type
Unsupported Accept
Malformed URL
Invalid query parameter
```

## Expected Negative-Test Properties

A good negative test should verify:

```text
Correct status
Correct error code
Correct error message/schema
No unintended state change
No sensitive data leakage
Correlation ID if applicable
Consistent error handling
```

---

# 30. Boundary Testing

Boundary testing is especially important for APIs.

Suppose:

```text
quantity = 1 to 100
```

Test:

```text
0
1
2
99
100
101
```

For a string:

```text
Maximum length = 100
```

Test:

```text
99 characters
100 characters
101 characters
```

For numeric values:

```text
Minimum - 1
Minimum
Minimum + 1
Maximum - 1
Maximum
Maximum + 1
```

## Why Boundary Testing Matters

Many production defects occur at boundaries because implementations commonly contain errors around:

```text
>
>=
<
<=
```

Expected:

```text
quantity <= 100
```

Defective implementation:

```text
quantity < 100
```

Then:

```text
quantity = 100
```

incorrectly fails.

---

# 31. Security Testing

API security testing should include multiple dimensions.

## Authentication

Test:

```text
Missing credentials
Invalid credentials
Expired credentials
Malformed credentials
```

## Authorization

Test:

```text
User → User resource
User → Another user's resource
User → Admin resource
Admin → Admin resource
```

## Object-Level Authorization

Example:

```http
GET /users/101/orders
```

while authenticated as user `100`.

Expected behavior depends on the authorization policy.

## Sensitive Data Exposure

Check responses for:

```text
Passwords
Access tokens
Refresh tokens
API keys
Internal identifiers
PII
Secrets
Internal infrastructure information
```

## Injection

Depending on the technology stack and input surface, test for:

```text
SQL Injection
NoSQL Injection
Command Injection
LDAP Injection
Template Injection
Path Traversal
```

Security testing should be performed responsibly and only in authorized environments.

---

# 32. Resilience Testing

Modern APIs operate in distributed systems.

Failures are normal.

Test:

```text
Network timeout
Service timeout
Dependency failure
Gateway failure
Connection reset
Rate limiting
Duplicate requests
Concurrent requests
Partial failure
Retry behavior
Recovery
```

## Timeout Scenario

```text
Client
  ↓
Order Service
  ↓
Payment Service
  X
Timeout
```

Questions:

```text
Was the order created?
Was payment initiated?
Was the transaction rolled back?
Can the request be safely retried?
Is an idempotency key required?
```

## Dependency Failure

Suppose:

```text
Order Service
      ↓
Payment Service
      ↓
503
```

Test whether Order Service:

```text
Returns appropriate error
Does not create invalid state
Handles retry correctly
Records correlation information
Recovers correctly
```

---

# 33. API Test Design

A mature API automation suite should be designed around maintainability.

## Avoid

```text
Hard-coded URLs
Hard-coded tokens
Hard-coded test data
Duplicate request construction
Duplicate assertions
Tests depending on execution order
```

## Prefer

```text
Configuration
Environment variables
Request builders
Reusable clients
Reusable validators
Schema validators
Test-data factories
Authentication providers
Centralized logging
Centralized reporting
```

## Suggested Architecture

```text
API Automation Framework
│
├── Config
│
├── API Clients
│   ├── UserClient
│   ├── OrderClient
│   └── PaymentClient
│
├── Request Builders
│
├── Response Validators
│
├── Schema Validators
│
├── Authentication
│
├── Test Data
│
├── Utilities
│
├── Tests
│
└── Reporting
```

## Test Independence

Avoid:

```text
Test 1 creates user
      ↓
Test 2 uses user
      ↓
Test 3 deletes user
```

If Test 1 fails:

```text
Test 2 fails
Test 3 fails
```

This creates cascading failures.

Prefer controlled test data and independent setup/cleanup mechanisms.

---

# 34. Common API Testing Mistakes

## Mistake 1 — Only Checking Status Code

Bad:

```java
.then()
.statusCode(200);
```

Better:

```text
Status
Schema
Business values
Headers
Security
Performance
```

## Mistake 2 — Assuming Every Response Is JSON

Bad:

```java
response.jsonPath();
```

Better:

```text
Inspect Content-Type
↓
Parse according to representation
```

## Mistake 3 — Blindly Retrying POST

Especially dangerous for:

```text
Payments
Orders
Bookings
Transfers
```

Understand idempotency first.

## Mistake 4 — Treating Every 4xx as a Defect

A `4xx` may be exactly what the API contract expects.

## Mistake 5 — Ignoring Business State

An API may return:

```text
201 Created
```

while downstream state is incorrect.

Example:

```text
Order created
BUT
Inventory not reduced
```

Functional success cannot always be determined solely from HTTP status.

## Mistake 6 — Testing Only Happy Paths

A mature suite must include:

```text
Happy path
Negative path
Boundary
Security
Concurrency
Retry
Timeout
Dependency failure
```

## Mistake 7 — Ignoring Performance

An API returning:

```text
200
```

in:

```text
20 seconds
```

may still be unusable.

---

# 35. Senior-Level API Testing Principles

## Principle 1 — Status Code Is Only One Signal

```text
Status
+
Headers
+
Body
+
Business state
+
Security
+
Performance
```

## Principle 2 — Test State, Not Just Response

Ask:

> What changed because of this API call?

## Principle 3 — Understand Failure Semantics

Ask:

> What happens when the operation succeeds but the response is lost?

## Principle 4 — Understand Retry Semantics

Ask:

> Is retrying this request safe?

## Principle 5 — Validate the Contract

Verify:

```text
Schema
Types
Required fields
Optional fields
Content-Type
Backward compatibility
```

## Principle 6 — Separate Transport From Business Validation

A `200 OK` can still contain incorrect business data.

## Principle 7 — Treat APIs as Distributed-System Boundaries

Think about:

```text
Timeouts
Retries
Concurrency
Partial failures
Eventual consistency
Duplicate messages
Dependency failures
```

## Principle 8 — Build Automation That Diagnoses Failures

A test should tell you:

```text
What failed?
Where did it fail?
What was sent?
What was received?
Which contract was violated?
What was the correlation ID?
```

It should not simply say:

```text
JSON parsing failed
```

when the actual problem was:

```text
500 Internal Server Error
```

---

# 36. Final API Testing Mental Model

Whenever you encounter an API, ask these questions:

```text
1. What resource am I interacting with?

2. Why is this HTTP method being used?

3. Is the method safe?

4. Is it idempotent?

5. What does the path identify?

6. What do the query parameters control?

7. What does each header mean?

8. What representation am I sending?

9. What representation do I expect back?

10. What state should change?

11. What happens if the request is repeated?

12. What happens when something fails halfway?
```

## Senior API Testing Thought Process

```text
                    API REQUEST
                         │
                         ▼
                Is the request valid?
                         │
                         ▼
                Is the method correct?
                         │
                         ▼
                Is it safe/idempotent?
                         │
                         ▼
              Is authentication valid?
                         │
                         ▼
              Is authorization valid?
                         │
                         ▼
                Is the contract valid?
                         │
                         ▼
             Is the response correct?
                         │
                         ▼
             Is business state correct?
                         │
                         ▼
              What if it is retried?
                         │
                         ▼
              What if it times out?
                         │
                         ▼
           What if dependency fails?
                         │
                         ▼
              What about concurrency?
                         │
                         ▼
              What about performance?
                         │
                         ▼
              What about security?
                         │
                         ▼
             Can automation diagnose
                  failures correctly?
```

## Final Takeaway

> **A junior API tester validates the response.**
>
> **A senior API tester validates the request, response, contract and business state.**
>
> **A QA architect additionally validates failure semantics, idempotency, security, resilience, concurrency, observability and performance.**

The ultimate goal is not:

```text
"Does the API return 200?"
```

It is:

```text
"Does the API behave correctly under valid, invalid, repeated,
concurrent, slow, failed and unexpected conditions?"
```

That is the mindset required for **API testing expertise**.


````text
1. Status checks plus schema validation reveals contract and business issues.

2. Resource = Noun, Resource representations - JSON as payload, Statelessness -> Statelessness means each client request must contain all information necessary for
            the server to process it. The server does not rely on client-specific session state stored from previous requests. 
            This allows requests to be processed independently and makes horizontal scaling easier. , Uniform Interface which uses HTTP verbs consistently

3. How to maintain state for REST if it is stateless -> 
    We maintain business state in persistent or shared stores such as databases and caches, 
    while keeping the API interaction stateless by ensuring every request carries the context 
    required to process it—for example, an access token, resource ID, query parameters, or request metadata. 
    This allows any API instance behind a load balancer to process any request without relying on its local session memory.
    Common places for state ->
    | State                  | Typical Location         |
    | ---------------------- | ------------------------ |
    | Authentication context | JWT/access token         |
    | User data              | Database                 |
    | Order state            | Database                 |
    | Cart                   | Database/cache           |
    | Temporary data         | Cache                    |
    | Async workflow         | Database/message broker  |
    | UI state               | Client                   |
    | Request correlation    | Header                   |
    | Pagination context     | Request parameters/token |

4. RestAssured is Java domain specific language for HTTP and Assertions.
5. Given When & Then is nothing but Arrange-Act-Assert
6. Enable conditional Logging for failures only
7. Log Minimal Request Detail in shared specs
