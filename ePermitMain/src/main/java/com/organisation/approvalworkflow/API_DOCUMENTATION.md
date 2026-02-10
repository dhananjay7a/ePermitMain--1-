# Approval Workflow API Documentation

## Base URL
```
http://localhost:8080/ePermit/api/approvals
```

---

## Endpoints

### 1. Approve/Reject Registration (Encrypted Request)

**Endpoint:** `POST /approveRejectRegistration`

**Description:** Process approval or rejection of a registration using AES-encrypted payload.

**Content-Type:** `application/json`

**Request Body:** Base64-encoded AES-encrypted JSON string

**Example Request (Encrypted):**
```json
"1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s0t1u2v3w4x5y6z7a8b9c0d1e="
```

**Decrypted Payload:**
```json
{
  "orgId": "ORG001",
  "regStatus": "a",
  "approverType": "SCRUTINY",
  "orgCategory": "TRADER",
  "licenseExists": false,
  "orgContactEmail": "organization@example.com",
  "isAutoApprove": "N",
  "regFeeValidity": "2026-12-31",
  "resNo": "RES/2026/001",
  "remarks": "Approved after initial scrutiny",
  "approverUserId": "APPR001"
}
```

**Success Response (200 OK):**
```json
{
  "errorCode": 0,
  "message": "Registration approved successfully",
  "status": "SUCCESS",
  "orgId": "ORG001",
  "orgName": "Sample Trading Organization",
  "newStatus": "SCRUTINY_APPROVED"
}
```

**Error Response (400 Bad Request):**
```json
{
  "status": "FAILED",
  "errorCode": 1234567890,
  "message": "Organization not found with ID: ORG999",
  "orgId": "ORG999"
}
```

---

### 2. Approve/Reject Registration (Plain JSON Request)

**Endpoint:** `POST /approveRejectRegistration/plaintext`

**Description:** Process approval or rejection using plain JSON payload. Useful for testing and direct integration.

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "orgId": "ORG001",
  "regStatus": "a",
  "approverType": "FINAL_APPROVER",
  "orgCategory": "PROCESSOR",
  "licenseExists": false,
  "orgContactEmail": "processor@example.com",
  "isAutoApprove": "N",
  "regFeeValidity": "2027-06-30",
  "resNo": "RES/2026/002",
  "remarks": "Approved by final approver",
  "approverUserId": "APPR002"
}
```

**Success Response (200 OK):**
```json
{
  "errorCode": 0,
  "message": "Registration approved successfully",
  "status": "SUCCESS",
  "orgId": "ORG001",
  "orgName": "Sample Processing Organization",
  "newStatus": "APPROVED"
}
```

---

### 3. Get Approval History

**Endpoint:** `GET /history/{orgId}`

**Description:** Retrieve complete approval history for a specific organization.

**Path Parameters:**
- `orgId` (string, required): Organization ID

**Example Request:**
```
GET /api/approvals/history/ORG001
```

**Success Response (200 OK):**
```json
[
  {
    "trackerId": 1,
    "orgId": "ORG001",
    "orgName": "Sample Organization",
    "orgCategory": "TRADER",
    "orgRegistrationStatus": "SUBMITTED",
    "approverType": "SCRUTINY",
    "approverAction": "APPROVE",
    "approverUserId": "APPR001",
    "remarks": "Approved after scrutiny",
    "newStatus": "SCRUTINY_APPROVED",
    "createdOn": "2026-02-06T10:30:00",
    "createdBy": "APPR001"
  },
  {
    "trackerId": 2,
    "orgId": "ORG001",
    "orgName": "Sample Organization",
    "orgCategory": "TRADER",
    "orgRegistrationStatus": "SCRUTINY_APPROVED",
    "approverType": "FINAL_APPROVER",
    "approverAction": "APPROVE",
    "approverUserId": "APPR002",
    "remarks": "Final approval granted",
    "newStatus": "APPROVED",
    "createdOn": "2026-02-07T15:45:00",
    "createdBy": "APPR002"
  }
]
```

**No History Response (200 OK):**
```json
{
  "status": "NO_HISTORY",
  "orgId": "ORG999",
  "message": "No approval history found for this organization",
  "errorCode": 0
}
```

---

### 4. Health Check

**Endpoint:** `GET /health`

**Description:** Check if the approval workflow service is running.

**Example Request:**
```
GET /api/approvals/health
```

**Success Response (200 OK):**
```json
{
  "status": "UP",
  "service": "ApprovalWorkflow",
  "timestamp": 1738850400000
}
```

---

## Request DTO Fields

### ApproveRejectRegistrationDTO

| Field | Type | Required | Description | Valid Values |
|-------|------|----------|-------------|--------------|
| `orgId` | String | Yes | Organization ID | Any non-empty string |
| `regStatus` | String | Yes | Approval action | `a` (Approve), `r` (Reject) |
| `approverType` | String | Yes | Type of approver | `SCRUTINY`, `FINAL_APPROVER`, `DIGITAL_APPROVER` |
| `orgCategory` | String | Yes | Organization category | `TRADER`, `PROCESSOR`, `COMMISSION_AGENT`, etc. |
| `licenseExists` | Boolean | Yes | Existing license status | `true`, `false` |
| `orgContactEmail` | String | Yes | Contact email | Valid email format |
| `isAutoApprove` | String | No | Auto-approval flag | `Y`, `N` |
| `regFeeValidity` | Date | No | Registration fee validity | `YYYY-MM-DD` format |
| `resNo` | String | No | Resolution number | Any string |
| `remarks` | String | No | Approval/rejection remarks | Any text |
| `approverUserId` | String | Yes | ID of approver | Any non-empty string |

---

## Validation Rules

### Status Transitions

#### SCRUTINY Approver
- **Current Status Must Be:** `SUBMITTED` or `PENDING_SCRUTINY`
- **Approval Result:** `SCRUTINY_APPROVED`
- **Rejection Result:** `REJECTED`

#### FINAL_APPROVER
- **Current Status Must Be:** `SCRUTINY_APPROVED` or `PENDING_FINAL_APPROVAL`
- **Approval Result:** `APPROVED`
- **Rejection Result:** `REJECTED`
- **Additional Requirements:** 
  - `regFeeValidity` is required if `licenseExists` is `false`
  - Validity date must be within 1 year past and 3 years future

#### DIGITAL_APPROVER
- **Current Status Must Be:** `APPROVED` or `PENDING_DIGITAL_SIGNATURE`
- **Approval Result:** `DIGITALLY_SIGNED`
- **Rejection Result:** `REJECTED`

### Email Validation
- Must follow standard email format: `user@domain.com`

### Fee Validity Validation
- **Range:** Must be within 1 year before and 3 years after current date
- **Format:** `YYYY-MM-DD`

---

## Error Responses

### 400 Bad Request - Validation Error

```json
{
  "status": "VALIDATION_FAILED",
  "errorCode": -1234567890,
  "timestamp": 1738850400000,
  "errors": {
    "orgId": "Organization ID is required",
    "approverType": "Approver type is required",
    "orgContactEmail": "Valid email is required"
  }
}
```

### 400 Bad Request - Status Transition Error

```json
{
  "status": "FAILED",
  "errorCode": 123456789,
  "message": "Invalid status transition for approver type FINAL_APPROVER from current status SUBMITTED"
}
```

### 404 Not Found - Registration Not Found

```json
{
  "status": "FAILED",
  "errorCode": 987654321,
  "message": "Registration with Organization ID 'ORG999' not found",
  "orgId": "ORG999"
}
```

### 500 Internal Server Error

```json
{
  "status": "FAILED",
  "errorCode": 500,
  "message": "An unexpected error occurred: Database connection failed"
}
```

---

## cURL Examples

### 1. Plain JSON Approval Request

```bash
curl -X POST http://localhost:8080/ePermit/api/approvals/approveRejectRegistration/plaintext \
  -H 'Content-Type: application/json' \
  -d '{
    "orgId": "ORG001",
    "regStatus": "a",
    "approverType": "SCRUTINY",
    "orgCategory": "TRADER",
    "licenseExists": false,
    "orgContactEmail": "org@example.com",
    "isAutoApprove": "N",
    "regFeeValidity": "2026-12-31",
    "resNo": "RES/2026/001",
    "remarks": "Approved",
    "approverUserId": "APPR001"
  }'
```

### 2. Get Approval History

```bash
curl -X GET http://localhost:8080/ePermit/api/approvals/history/ORG001 \
  -H 'Content-Type: application/json'
```

### 3. Health Check

```bash
curl -X GET http://localhost:8080/ePermit/api/approvals/health
```

### 4. Reject Registration

```bash
curl -X POST http://localhost:8080/ePermit/api/approvals/approveRejectRegistration/plaintext \
  -H 'Content-Type: application/json' \
  -d '{
    "orgId": "ORG002",
    "regStatus": "r",
    "approverType": "SCRUTINY",
    "orgCategory": "COMMISSION_AGENT",
    "licenseExists": true,
    "orgContactEmail": "agent@example.com",
    "remarks": "Rejected due to incomplete documents",
    "approverUserId": "APPR001"
  }'
```

---

## JavaScript/Fetch Examples

### Approval with Plain JSON

```javascript
const approvalData = {
  orgId: 'ORG001',
  regStatus: 'a',
  approverType: 'SCRUTINY',
  orgCategory: 'TRADER',
  licenseExists: false,
  orgContactEmail: 'org@example.com',
  isAutoApprove: 'N',
  regFeeValidity: '2026-12-31',
  resNo: 'RES/2026/001',
  remarks: 'Approved after scrutiny',
  approverUserId: 'APPR001'
};

fetch('http://localhost:8080/ePermit/api/approvals/approveRejectRegistration/plaintext', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(approvalData)
})
.then(response => response.json())
.then(data => console.log('Success:', data))
.catch((error) => console.error('Error:', error));
```

### Get Approval History

```javascript
fetch('http://localhost:8080/ePermit/api/approvals/history/ORG001', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log('History:', data))
.catch((error) => console.error('Error:', error));
```

---

## Common Scenarios

### Scenario 1: Complete Approval Flow

1. **Scrutiny Approval:**
   ```
   Status: SUBMITTED → SCRUTINY_APPROVED
   Approver: SCRUTINY
   ```

2. **Final Approval:**
   ```
   Status: SCRUTINY_APPROVED → APPROVED
   Approver: FINAL_APPROVER
   Required: regFeeValidity (if no existing license)
   ```

3. **Digital Signature (Optional):**
   ```
   Status: APPROVED → DIGITALLY_SIGNED
   Approver: DIGITAL_APPROVER
   ```

### Scenario 2: Rejection at Any Stage

```json
{
  "orgId": "ORG002",
  "regStatus": "r",
  "approverType": "SCRUTINY",
  "orgCategory": "TRADER",
  "licenseExists": false,
  "orgContactEmail": "org@example.com",
  "remarks": "Rejected: Incomplete KYC documents",
  "approverUserId": "APPR001"
}
```

Result: Status → `REJECTED`

---

## Testing Checklist

- [ ] Plain JSON approval with all required fields
- [ ] Plain JSON approval with optional fields
- [ ] Encrypted payload approval
- [ ] Rejection at different approval levels
- [ ] Invalid status transition
- [ ] Missing registration
- [ ] Invalid email format
- [ ] Fee validity outside allowed range
- [ ] Approval history retrieval
- [ ] Health check endpoint
- [ ] Exception handling and error responses

---

## Performance Notes

- All database operations are indexed for optimal query performance
- Approval operations use transactions for data consistency
- Typical approval processing: < 500ms
- History retrieval with 100+ records: < 1s

---

For more details, refer to README.md in the approvalworkflow module.
