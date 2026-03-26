# Doc-Service

Microservice quản lý tài liệu trong hệ thống **Docube** — nền tảng mua bán và chia sẻ tài liệu học thuật. Được xây dựng với **Spring Boot 3.5.6 + Kotlin**, tuân theo kiến trúc CQRS và Clean Architecture.

---

## Mục lục

- [Tổng quan kiến trúc](#tổng-quan-kiến-trúc)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc thư mục](#cấu-trúc-thư-mục)
- [Domain Models](#domain-models)
- [API Endpoints](#api-endpoints)
- [Luồng nghiệp vụ](#luồng-nghiệp-vụ)
- [Tích hợp hệ thống](#tích-hợp-hệ-thống)
- [Xử lý lỗi](#xử-lý-lỗi)
- [Cấu hình](#cấu-hình)
- [Chạy ứng dụng](#chạy-ứng-dụng)

---

## Tổng quan kiến trúc

```
┌──────────────────────────────────────────────────────────────┐
│                        Doc-Service                            │
│                                                              │
│   API Layer (Controllers)                                    │
│   ├── DocumentCommand/QueryController                        │
│   ├── BookmarkCommand/QueryController                        │
│   ├── PurchaseCommand/QueryController                        │
│   ├── PaymentWebhookController (SePay)                       │
│   ├── School/DepartmentCommand/QueryController               │
│                                                              │
│   Service Layer (CQRS)                                       │
│   ├── *CommandHandler  →  ghi (create, update, delete)       │
│   └── *QueryHandler    →  đọc (get, list, download)          │
│                                                              │
│   Domain Layer                                               │
│   ├── Models: Document, Bookmark, Purchase, School, Dept     │
│   └── Repository Interfaces (Port)                           │
│                                                              │
│   Infrastructure Layer                                       │
│   ├── PostgreSQL (Spring Data JPA)                           │
│   ├── MinIO (Object Storage)                                 │
│   ├── Redis (Cache)                                          │
│   └── Kafka (Event Producer)                                 │
└──────────────────────────────────────────────────────────────┘
         │                   │               │
         ▼                   ▼               ▼
   [PostgreSQL]           [MinIO]        [Kafka]
   horob1_docub_          docube-        → blockchain-service
   doc_service            documents      → (access grant)
```

**Patterns áp dụng:**
- **CQRS** — Tách biệt Command (ghi) và Query (đọc)
- **Repository Pattern** — Domain interface + Infrastructure implementation
- **Event-Driven** — Kafka events cho blockchain integration
- **Soft Delete** — Document không bị xóa vật lý, chỉ đổi status

---

## Công nghệ sử dụng

| Công nghệ | Version | Mục đích |
|-----------|---------|---------|
| Kotlin | 1.9.25 | Ngôn ngữ chính |
| Spring Boot | 3.5.6 | Application framework |
| Spring Cloud | 2025.0.0 | Service discovery, circuit breaker |
| Spring Data JPA | - | ORM với Hibernate |
| PostgreSQL | - | Cơ sở dữ liệu chính |
| MinIO | SDK 8.5.14 | Object storage (lưu file tài liệu) |
| Redis | - | Cache |
| Apache Kafka | - | Async event streaming |
| Spring Security | - | Authentication (header-based) |
| Eureka Client | - | Service discovery |
| Resilience4J | - | Circuit breaker |
| SePay | - | Cổng thanh toán QR Code (ngân hàng Việt Nam) |

---

## Cấu trúc thư mục

```
Doc-Service/
├── src/main/kotlin/com/horob1/doc_service/
│   ├── DocServiceApplication.kt          # Entry point
│   │
│   ├── api/                              # Presentation Layer
│   │   ├── controller/
│   │   │   ├── DocumentCommandController.kt
│   │   │   ├── DocumentQueryController.kt
│   │   │   ├── BookmarkCommandController.kt
│   │   │   ├── BookmarkQueryController.kt
│   │   │   ├── PurchaseCommandController.kt
│   │   │   ├── PurchaseQueryController.kt
│   │   │   ├── PaymentWebhookController.kt
│   │   │   ├── SchoolCommandController.kt
│   │   │   ├── SchoolQueryController.kt
│   │   │   ├── DepartmentCommandController.kt
│   │   │   └── DepartmentQueryController.kt
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   ├── CreateDocumentDto.kt
│   │   │   │   ├── UpdateDocumentDto.kt
│   │   │   │   ├── CreatePurchaseDto.kt
│   │   │   │   ├── CreateUpdateSchoolDto.kt
│   │   │   │   ├── CreateUpdateDepartmentDto.kt
│   │   │   │   └── SepayWebhookDto.kt
│   │   │   └── response/
│   │   │       ├── DocumentResponse.kt
│   │   │       ├── DocumentSummaryResponse.kt
│   │   │       ├── PurchaseResponse.kt
│   │   │       ├── SchoolResponse.kt
│   │   │       └── DepartmentResponse.kt
│   │   └── exception/
│   │       ├── DocumentError.kt
│   │       ├── BookmarkError.kt
│   │       ├── PurchaseError.kt
│   │       ├── SchoolError.kt
│   │       └── DepartmentError.kt
│   │
│   ├── config/                           # Spring Bean Configuration
│   │   ├── MinioConfig.kt
│   │   ├── SepayConfig.kt
│   │   ├── SecurityConfig.kt
│   │   └── RedisConfig.kt
│   │
│   ├── domain/                           # Domain Layer (Business Core)
│   │   ├── model/
│   │   │   ├── AbstractEntity.kt         # Base class (id, audit fields)
│   │   │   ├── document/
│   │   │   │   ├── Document.kt
│   │   │   │   └── DocumentExtension.kt
│   │   │   ├── bookmark/Bookmark.kt
│   │   │   ├── purchase/Purchase.kt
│   │   │   ├── school/School.kt
│   │   │   └── department/Department.kt
│   │   └── repository/                   # Port interfaces
│   │       ├── DocumentRepository.kt
│   │       ├── BookmarkRepository.kt
│   │       ├── PurchaseRepository.kt
│   │       ├── SchoolRepository.kt
│   │       └── DepartmentRepository.kt
│   │
│   ├── service/                          # Application Layer (CQRS)
│   │   ├── command/
│   │   │   ├── DocumentCommandHandler.kt
│   │   │   ├── BookmarkCommandHandler.kt
│   │   │   ├── PurchaseCommandHandler.kt
│   │   │   ├── SchoolCommandHandler.kt
│   │   │   └── DepartmentCommandHandler.kt
│   │   └── query/
│   │       ├── DocumentQueryHandler.kt
│   │       ├── BookmarkQueryHandler.kt
│   │       ├── PurchaseQueryHandler.kt
│   │       ├── SchoolQueryHandler.kt
│   │       └── DepartmentQueryHandler.kt
│   │
│   ├── infrastructure/                   # Adapter Layer
│   │   ├── minio/MinioStorageService.kt
│   │   ├── producer/DocEventProducer.kt
│   │   └── repository/
│   │       ├── DocumentRepositoryImpl.kt
│   │       ├── BookmarkRepositoryImpl.kt
│   │       ├── PurchaseRepositoryImpl.kt
│   │       ├── SchoolRepositoryImpl.kt
│   │       ├── DepartmentRepositoryImpl.kt
│   │       └── postgres/               # Spring Data JPA repositories
│   │           ├── DocumentPostgresRepository.kt
│   │           ├── BookmarkPostgresRepository.kt
│   │           ├── PurchasePostgresRepository.kt
│   │           ├── SchoolPostgresRepository.kt
│   │           └── DepartmentPostgresRepository.kt
│   │
│   ├── shared/                           # Shared Utilities
│   │   ├── constant/SecurityConstants.kt
│   │   ├── dto/response/ApiResponse.kt
│   │   ├── enums/
│   │   │   ├── DocumentStatus.kt         # ACTIVE, DELETED
│   │   │   ├── FileType.kt               # PDF, WORD, TEXT
│   │   │   └── PurchaseStatus.kt         # PENDING, COMPLETED, FAILED
│   │   ├── exception/
│   │   │   ├── AppError.kt
│   │   │   ├── AppException.kt
│   │   │   └── web/GlobalExceptionHandler.kt
│   │   ├── kafka/
│   │   │   ├── event/DocumentBlockchainEvent.kt
│   │   │   └── topic/DocTopic.kt
│   │   └── web/
│   │       ├── config/SharedWebConfig.kt
│   │       └── interceptor/UserPermissionContextFilter.kt
│   │
│   └── util/
│       ├── FileHashUtil.kt               # SHA256 streaming hash
│       └── PaymentCodeGenerator.kt       # DOCUBE payment code generator
│
└── src/main/resources/
    └── application.yaml
```

---

## Domain Models

### Document

Thực thể trung tâm của hệ thống, đại diện cho một tài liệu được upload.

```
tbl_documents
├── id              UUID (PK, auto-generated)
├── title           VARCHAR (max 500)
├── description     TEXT (max 2000)
├── file_type       ENUM (PDF, WORD, TEXT)
├── minio_key       VARCHAR (path trong object storage)
├── minio_bucket    VARCHAR (tên bucket)
├── original_file_name VARCHAR
├── file_size       BIGINT (bytes)
├── doc_hash        VARCHAR (SHA256 của file)
├── hash_algo       VARCHAR (default: SHA256)
├── blockchain_doc_id VARCHAR? (ID trên Hyperledger Fabric)
├── status          ENUM (ACTIVE, DELETED)
├── price           DECIMAL (0 = miễn phí)
├── owner_id        UUID (FK → Auth-Service user)
├── school_id       UUID? (FK → tbl_schools)
├── department_id   UUID? (FK → tbl_departments)
├── created_at      TIMESTAMP
├── updated_at      TIMESTAMP
├── created_by      UUID
└── updated_by      UUID

Indexes: idx_doc_owner, idx_doc_status, idx_doc_school, idx_doc_department
```

### Bookmark

Lưu danh sách tài liệu yêu thích của user.

```
tbl_bookmarks
├── id              UUID (PK)
├── user_id         UUID (FK → Auth-Service user)
├── document_id     UUID (FK → tbl_documents)
├── created_at      TIMESTAMP
└── updated_at      TIMESTAMP

Unique: (user_id, document_id)
```

### Purchase

Lưu lịch sử giao dịch mua tài liệu.

```
tbl_purchases
├── id              UUID (PK)
├── user_id         UUID (FK → Auth-Service user)
├── document_id     UUID (FK → tbl_documents)
├── amount          DECIMAL (giá tại thời điểm mua)
├── status          ENUM (PENDING, COMPLETED, FAILED)
├── payment_code    VARCHAR (DOCUBE + 8 ký tự, unique)
├── transaction_id  VARCHAR? (SePay transaction ID)
├── created_at      TIMESTAMP
└── updated_at      TIMESTAMP

Indexes: idx_purchase_user, idx_purchase_status,
         idx_purchase_user_doc, idx_purchase_payment_code (unique)
```

### School & Department

Phân loại tài liệu theo trường và khoa.

```
tbl_schools                    tbl_departments
├── id          UUID (PK)      ├── id          UUID (PK)
├── name        VARCHAR (uniq) ├── name        VARCHAR
├── description TEXT           ├── description TEXT
├── address     TEXT           ├── school_id   UUID (FK → tbl_schools)
├── created_at  TIMESTAMP      ├── created_at  TIMESTAMP
└── updated_at  TIMESTAMP      └── updated_at  TIMESTAMP
                               Unique: (name, school_id)
```

---

## API Endpoints

> **Xác thực:** Service sử dụng header `X-User-Id` (UUID) được inject bởi API Gateway sau khi xác thực JWT. Không dùng Bearer token trực tiếp.

### Documents

| Method | Path | Auth | Mô tả |
|--------|------|------|--------|
| `POST` | `/api/v1/documents` | ✅ Bắt buộc | Upload tài liệu mới |
| `PUT` | `/api/v1/documents/{id}` | ✅ Bắt buộc | Cập nhật thông tin tài liệu |
| `DELETE` | `/api/v1/documents/{id}` | ✅ Bắt buộc | Xóa mềm tài liệu |
| `GET` | `/api/v1/documents` | ❌ Public | Danh sách tài liệu (phân trang) |
| `GET` | `/api/v1/documents/{id}` | ❌ Public | Chi tiết tài liệu |
| `GET` | `/api/v1/documents/my` | ✅ Bắt buộc | Tài liệu của tôi |
| `GET` | `/api/v1/documents/{id}/download` | ✅ Bắt buộc | Lấy URL tải xuống |

#### POST /api/v1/documents — Upload tài liệu

Request: `multipart/form-data`
```
file    : MultipartFile  (PDF / DOCX / TXT, tối đa 50MB)
title   : String         (bắt buộc, tối đa 500 ký tự)
description : String     (tùy chọn, tối đa 2000 ký tự)
price   : BigDecimal     (tùy chọn, default 0 = miễn phí)
schoolId    : UUID       (tùy chọn)
departmentId: UUID       (tùy chọn)
```

Response `201 Created`:
```json
{
  "status": "CREATED",
  "message": "Document uploaded successfully",
  "data": {
    "id": "44b1c599-de61-4de4-be31-40d472a5a162",
    "title": "Giáo trình Toán cao cấp",
    "fileType": "PDF",
    "originalFileName": "toan_cao_cap.pdf",
    "fileSize": 1048576,
    "docHash": "a44f69bc...",
    "hashAlgo": "SHA256",
    "status": "ACTIVE",
    "price": 10000.00,
    "ownerId": "c911130e-...",
    "schoolId": null,
    "departmentId": null,
    "createdAt": "2026-03-26T12:00:00Z"
  }
}
```

Quy trình nội bộ:
```
Upload file → Validate MIME type → Stream file qua SHA256 hasher
     → Upload lên MinIO (key: documents/{ownerId}/{uuid}_{filename})
     → Lưu metadata vào PostgreSQL
     → Publish DocumentCreateEvent → Kafka (docube.document.create)
     → Blockchain-service đăng ký lên Hyperledger Fabric
```

**File types được chấp nhận:**
- `application/pdf` → PDF
- `application/vnd.openxmlformats-officedocument.wordprocessingml.document` → WORD
- `application/msword` → WORD
- `text/plain` → TEXT

#### GET /api/v1/documents — Danh sách (phân trang)

Query params:
```
page  : Int  (default 0)
size  : Int  (default 20)
sort  : String (default createdAt,desc)
```

Response `200 OK`:
```json
{
  "status": "OK",
  "message": "Get documents successfully",
  "data": {
    "content": [
      {
        "id": "...",
        "title": "...",
        "fileType": "PDF",
        "fileSize": 1048576,
        "price": 10000.00,
        "ownerId": "...",
        "createdAt": "..."
      }
    ],
    "totalElements": 150,
    "totalPages": 8,
    "size": 20,
    "number": 0
  }
}
```

#### GET /api/v1/documents/{id}/download — Lấy URL tải xuống

Logic kiểm tra quyền truy cập:
```
1. User là owner → ✅ Cho phép
2. Tài liệu miễn phí (price = 0) → ✅ Cho phép
3. User đã mua (purchase status = COMPLETED) → ✅ Cho phép
4. Không thỏa điều kiện nào → ❌ 403 Forbidden (DOC_0006)
```

Response `200 OK`:
```json
{
  "status": "OK",
  "message": "Get download URL successfully",
  "data": {
    "downloadUrl": "http://minio-host:9090/docube-documents/...?X-Amz-Expires=900&..."
  }
}
```

> URL có hiệu lực **15 phút** (900 giây), sau đó cần gọi lại endpoint để lấy URL mới.

---

### Bookmarks

| Method | Path | Auth | Mô tả |
|--------|------|------|--------|
| `POST` | `/api/v1/bookmarks/{documentId}` | ✅ | Thêm bookmark |
| `DELETE` | `/api/v1/bookmarks/{documentId}` | ✅ | Xóa bookmark |
| `GET` | `/api/v1/bookmarks` | ✅ | Danh sách bookmark của tôi |

---

### Purchases & Payment

| Method | Path | Auth | Mô tả |
|--------|------|------|--------|
| `POST` | `/api/v1/purchases` | ✅ | Tạo đơn mua (sinh QR) |
| `GET` | `/api/v1/purchases` | ✅ | Lịch sử mua của tôi |
| `POST` | `/api/transactions/payment-confirmation` | ❌* | Webhook xác nhận thanh toán |

> *Webhook được xác thực bằng API Key trong header, không cần JWT.

#### POST /api/v1/purchases — Tạo đơn mua

Request:
```json
{ "documentId": "44b1c599-de61-4de4-be31-40d472a5a162" }
```

Validation:
- Tài liệu tồn tại và không bị xóa
- Không phải chủ sở hữu tài liệu (→ `PURCHASE_0004`)
- Tài liệu phải có giá > 0 (→ `PURCHASE_0002`)
- Chưa có đơn COMPLETED trước đó (→ `PURCHASE_0001`)
- Nếu đã có đơn PENDING → trả về đơn cũ kèm QR (idempotent)

Response `201 Created`:
```json
{
  "status": "CREATED",
  "message": "Purchase created successfully",
  "data": {
    "id": "a530df08-...",
    "documentId": "44b1c599-...",
    "amount": 10000.00,
    "status": "PENDING",
    "paymentCode": "DOCUBE3AF0193E",
    "qrUrl": "https://qr.sepay.vn/img?acc=VQRQAEYKB9868&bank=MBBank&amount=10000&des=DOCUBE3AF0193E",
    "transactionId": null,
    "createdAt": "2026-03-26T00:00:00Z"
  }
}
```

#### POST /api/transactions/payment-confirmation — SePay Webhook

Header yêu cầu: `Authorization: Apikey {SEPAY_API_KEY}`

Request body (từ SePay):
```json
{
  "id": 46872927,
  "gateway": "MBBank",
  "transactionDate": "2026-03-26 00:12:30",
  "accountNumber": "VQRQAEYKB9868",
  "subAccount": null,
  "code": "DOCUBE3AF0193E",
  "content": "Chuyen tien DOCUBE3AF0193E",
  "transferType": "in",
  "description": "Payment for document",
  "transferAmount": 10000,
  "accumulated": 10000,
  "referenceCode": "FT26085...",
  "body": ""
}
```

Quy trình xử lý:
```
1. Xác thực API Key header
2. Chỉ xử lý transferType = "in" (bỏ qua "out")
3. Trích xuất payment code từ content (regex: DOCUBE[A-Z0-9]{8})
4. Kiểm tra idempotency bằng transactionId
5. Tìm Purchase theo paymentCode
6. Kiểm tra amount khớp với purchase.amount
7. Cập nhật status → COMPLETED, lưu transactionId
8. Publish AccessGrantEvent → Kafka (docube.access.grant)
   → Blockchain-service cấp quyền truy cập lên Hyperledger Fabric
```

---

### Schools

| Method | Path | Auth | Mô tả |
|--------|------|------|--------|
| `POST` | `/api/v1/schools` | ❌ | Tạo trường |
| `PUT` | `/api/v1/schools/{id}` | ❌ | Cập nhật trường |
| `DELETE` | `/api/v1/schools/{id}` | ❌ | Xóa trường |
| `GET` | `/api/v1/schools` | ❌ | Danh sách tất cả trường |
| `GET` | `/api/v1/schools/{id}` | ❌ | Chi tiết trường |
| `GET` | `/api/v1/schools/{id}/departments` | ❌ | Danh sách khoa của trường |

### Departments

| Method | Path | Auth | Mô tả |
|--------|------|------|--------|
| `POST` | `/api/v1/departments` | ❌ | Tạo khoa |
| `PUT` | `/api/v1/departments/{id}` | ❌ | Cập nhật khoa |
| `DELETE` | `/api/v1/departments/{id}` | ❌ | Xóa khoa |
| `GET` | `/api/v1/departments` | ❌ | Danh sách tất cả khoa |
| `GET` | `/api/v1/departments/{id}` | ❌ | Chi tiết khoa |

---

## Luồng nghiệp vụ

### Luồng 1: Upload tài liệu

```
Client → [Gateway] → Doc-Service
                        │
                        ├─ Validate: loại file, kích thước
                        ├─ Stream file → SHA256 hash + Upload MinIO
                        │   Key: documents/{ownerId}/{uuid}_{filename}
                        ├─ Lưu Document vào PostgreSQL
                        └─ Publish: docube.document.create
                                          │
                                   [Kafka] → Blockchain-Service
                                              └─ CreateDocument
                                                 trên Hyperledger Fabric
```

### Luồng 2: Thanh toán mua tài liệu

```
Buyer → POST /api/v1/purchases
             │
             ├─ Validate điều kiện mua
             ├─ Tạo Purchase (PENDING) + sinh paymentCode
             └─ Trả về QR URL (SePay)

Buyer → Quét QR → Chuyển khoản (nội dung: DOCUBE3AF0193E)
                        │
                   [SePay WebHook] → POST /api/transactions/payment-confirmation
                                              │
                                              ├─ Xác thực API Key
                                              ├─ Parse payment code từ nội dung
                                              ├─ Idempotency check (transactionId)
                                              ├─ Validate amount
                                              ├─ Update Purchase → COMPLETED
                                              └─ Publish: docube.access.grant
                                                               │
                                                        [Kafka] → Blockchain-Service
                                                                   └─ GrantAccess
                                                                      trên Fabric
```

### Luồng 3: Tải xuống tài liệu

```
Buyer → GET /api/v1/documents/{id}/download
[Gateway inject X-User-Id] → Doc-Service
                                  │
                    ┌─── Kiểm tra quyền ───┐
                    │                       │
               Is Owner?              Has COMPLETED
               OR price=0?            Purchase?
                    │                       │
                   YES                     YES
                    └──────── ✅ ──────────┘
                                  │
                        Generate presigned MinIO URL
                        (hiệu lực 15 phút)
                                  │
                        Trả về { downloadUrl: "..." }
```

### Luồng 4: Bookmark

```
User → POST /api/v1/bookmarks/{documentId}
             │
             ├─ Kiểm tra document tồn tại
             ├─ Kiểm tra chưa bookmark (unique constraint)
             └─ Tạo Bookmark record

User → GET /api/v1/bookmarks
             └─ Trả về danh sách DocumentSummaryResponse
```

---

## Tích hợp hệ thống

### MinIO — Object Storage

```yaml
minio:
  endpoint: http://localhost:9090
  access-key: ${MINIO_ACCESS_KEY:minioadmin}
  secret-key: ${MINIO_SECRET_KEY:minioadmin}
  bucket: docube-documents
```

**Cấu trúc key trong bucket:**
```
docube-documents/
└── documents/
    └── {ownerId}/
        └── {uuid}_{originalFileName}
```

**MinioStorageService:**
- `ensureBucketExists()` — Tự động tạo bucket khi khởi động
- `uploadFile()` — Stream upload với content-type
- `getPresignedUrl()` — URL có thời hạn (GET, 15 phút)
- `deleteFile()` — Xóa file khỏi bucket

### Kafka — Event Streaming

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:7092
    security.protocol: SASL_PLAINTEXT
    sasl.mechanism: PLAIN
```

**Topics và sự kiện:**

| Topic | Event | Payload |
|-------|-------|---------|
| `docube.document.create` | DocumentCreateEvent | documentId, docHash, hashAlgo, systemUserId |
| `docube.document.delete` | DocumentDeleteEvent | documentId |
| `docube.access.grant` | AccessGrantEvent | documentId, granteeUserId, granteeUserMsp, systemUserId |
| `docube.access.revoke` | AccessRevokeEvent | documentId, userId |

**Event flow → Blockchain:**
```kotlin
// Khi upload tài liệu
data class DocumentCreateEvent(
    val documentId: String,
    val docHash: String,     // SHA256 hash của file
    val hashAlgo: String,    // "SHA256"
    val systemUserId: String // UUID của owner
)

// Khi mua thành công
data class AccessGrantEvent(
    val documentId: String,
    val granteeUserId: String,
    val granteeUserMsp: String = "AdminOrgMSP",
    val systemUserId: String   // UUID của owner
)
```

### Redis — Cache

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 3
      timeout: 5000ms
```

### SePay — Cổng thanh toán

```yaml
sepay:
  api-key: ${SEPAY_API_KEY}
  bank-account: ${SEPAY_BANK_ACCOUNT}   # Số tài khoản ngân hàng
  bank-code: ${SEPAY_BANK_CODE}         # Ví dụ: MBBank
  qr-base-url: https://qr.sepay.vn/img
```

**QR URL Format:**
```
https://qr.sepay.vn/img?acc={bankAccount}&bank={bankCode}&amount={price}&des={paymentCode}
```

**Payment Code Format:** `DOCUBE` + 8 ký tự ngẫu nhiên (uppercase chữ và số)
> Ví dụ: `DOCUBE3AF0193E`

**Webhook Security:** Header `Authorization: Apikey {API_KEY}` phải khớp chính xác.

### Eureka — Service Discovery

```yaml
eureka:
  instance:
    appname: DOC-SERVICE
  client:
    service-url:
      defaultZone: http://localhost:9000/eureka
```

Service đăng ký với tên `DOC-SERVICE`, Gateway forward request qua `lb://DOC-SERVICE`.

### Resilience4J — Circuit Breaker

```yaml
resilience4j:
  circuitbreaker:
    instances:
      docService:
        slidingWindowSize: 5
        minimumNumberOfCalls: 3
        failureRateThreshold: 50    # 50% lỗi → mở circuit
        waitDurationInOpenState: 5s
```

---

## Xử lý lỗi

Tất cả lỗi được trả về theo cấu trúc `ApiResponse`:

```json
{
  "status": "BAD_REQUEST",
  "message": "Document not found",
  "code": "DOC_0001",
  "data": null,
  "timestamp": "2026-03-26T12:00:00Z"
}
```

### Error Codes

| Code | Mô tả |
|------|--------|
| `SERVER_0001` | Internal server error |
| `SERVER_0002` | Not found (route không tồn tại) |
| `SERVER_0003` | Method not allowed |
| `AUTH_0001` | Forbidden (không có quyền) |
| `AUTH_0002` | Authentication failed |
| `VALIDATION_0001` | Lỗi validate input |
| `DOC_0001` | Document not found |
| `DOC_0002` | Document already deleted |
| `DOC_0003` | Invalid file type (không phải PDF/DOCX/TXT) |
| `DOC_0004` | File quá lớn (> 50MB) |
| `DOC_0005` | Not owner (không phải chủ sở hữu) |
| `DOC_0006` | Access denied (chưa mua, không phải owner) |
| `DOC_0007` | Upload failed (lỗi MinIO) |
| `BOOKMARK_0001` | Bookmark already exists |
| `BOOKMARK_0002` | Bookmark not found |
| `PURCHASE_0001` | Already purchased (đã mua) |
| `PURCHASE_0002` | Document is free (không cần mua) |
| `PURCHASE_0003` | Purchase not found |
| `PURCHASE_0004` | Cannot purchase own document |
| `SCHOOL_0001` | School not found |
| `SCHOOL_0002` | School already exists |
| `DEPT_0001` | Department not found |
| `DEPT_0002` | Department already exists in school |

---

## Cấu hình

### application.yaml

```yaml
spring:
  application:
    name: Doc-Service
  datasource:
    url: jdbc:postgresql://localhost:5432/horob1_docub_doc_service
    username: postgres
    password: ${DB_PASSWORD:2410}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update       # Tự tạo/cập nhật schema
    show-sql: true
  cache:
    type: redis
  data:
    redis:
      host: localhost
      port: 6379
      password: ${REDIS_PASSWORD:2410}
      database: 3
      timeout: 5000ms
  kafka:
    bootstrap-servers: localhost:7092
    consumer:
      group-id: doc-service-group
      auto-offset-reset: earliest
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 55MB

server:
  port: 9200

minio:
  endpoint: http://localhost:9090
  access-key: ${MINIO_ACCESS_KEY:minioadmin}
  secret-key: ${MINIO_SECRET_KEY:minioadmin}
  bucket: docube-documents

sepay:
  api-key: ${SEPAY_API_KEY}
  bank-account: ${SEPAY_BANK_ACCOUNT}
  bank-code: ${SEPAY_BANK_CODE}
  qr-base-url: https://qr.sepay.vn/img
```

### Environment Variables

| Biến | Mô tả | Default |
|------|--------|---------|
| `MINIO_ACCESS_KEY` | MinIO access key | `minioadmin` |
| `MINIO_SECRET_KEY` | MinIO secret key | `minioadmin` |
| `SEPAY_API_KEY` | SePay API key để xác thực webhook | `helo` |
| `SEPAY_BANK_ACCOUNT` | Số tài khoản ngân hàng | `VQRQAEYKB9868` |
| `SEPAY_BANK_CODE` | Mã ngân hàng | `MBBank` |

---

## Chạy ứng dụng

### Yêu cầu

| Service | Port | Ghi chú |
|---------|------|---------|
| PostgreSQL | 5432 | Database: `horob1_docub_doc_service` |
| MinIO | 9090 | Bucket sẽ tự tạo khi startup |
| Redis | 6379 | Database 3 |
| Kafka | 7092 | SASL/PLAIN: horob1/2410 |
| Eureka (Discovery) | 9000 | Phải chạy trước |

### Chạy với Gradle

```bash
# Tạo database PostgreSQL trước (nếu chưa có)
createdb horob1_docub_doc_service

# Chạy ứng dụng
./gradlew bootRun

# Hoặc build JAR và chạy
./gradlew build
java -jar build/libs/Doc-Service-0.0.1-SNAPSHOT.jar
```

### Chạy trong hệ thống Docube đầy đủ

1. Khởi động infrastructure: `docker compose up -d` (Kafka, Redis, MinIO, PostgreSQL)
2. Khởi động Eureka Discovery Server (port 9000)
3. Khởi động Auth-Service (port 8080)
4. Khởi động **Doc-Service** (port 9200)
5. Khởi động API Gateway (port 8080/external)

### Kiểm tra health

```bash
curl http://localhost:9200/actuator/health
# → {"status":"UP"}
```

### Verify Eureka registration

```bash
curl http://localhost:9000/eureka/apps/DOC-SERVICE
```

---

## Kiến trúc bảo mật

Doc-Service **không tự xác thực JWT**. Thay vào đó:

1. API Gateway xác thực JWT từ client
2. Gateway inject header `X-User-Id: {uuid}` vào request forwarded
3. `UserPermissionContextFilter` đọc header và tạo `SecurityContext`
4. Service layer đọc user ID qua `SecurityContextHolder`

```kotlin
// UserPermissionContextFilter.kt
val userId = request.getHeader("X-User-Id") ?: return chain.doFilter(...)
val auth = UsernamePasswordAuthenticationToken(UUID.fromString(userId), null, authorities)
SecurityContextHolder.getContext().authentication = auth
```

**Truy cập trực tiếp (bypass Gateway):**
```bash
curl http://localhost:9200/api/v1/documents/my \
  -H "X-User-Id: c911130e-0827-4fc5-aa2a-51f3e56a70c1"
```

---

*Doc-Service là một phần trong hệ thống Docube microservices. Xem thêm tại [Docube monorepo](https://github.com/Horob1/docube).*
