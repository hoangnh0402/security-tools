---
name: HQC Hexagonal Architecture Guidelines
description: Hướng dẫn tổ chức mã nguồn và logic phụ thuộc theo kiến trúc Hexagonal (Clean Architecture) cho dự án Security Tools. Đọc kỹ skill này trước khi khởi tạo module, package hoặc viết Use Case mới.
---

# HQC Hexagonal Architecture (Clean Architecture) Guidelines

Khi xây dựng dự án Security Tools, AI và Developer phải tuân thủ nghiêm ngặt nguyên tắc phân tách lớp (Layer Separation) của Hexagonal Architecture.

## 1. Domain Layer (`/domain` package)
Đây là "trái tim" của ứng dụng phần mềm.
- **Quy tắc tuyệt đối:** Tầng này **KHÔNG ĐƯỢC PHÉP** chứa bất kỳ dependency nào liên quan đến Spring Boot (`org.springframework.*`), JPA, hoặc các thư viện infrastructure bên ngoài. (Chỉ ngoại lệ cho các annotation tiêm phụ thuộc JSR-330 như `@Named`, `@Inject` nếu thực sự cần thiết).
- **Thành phần:**
  - `model`: Các Business Entities, Value Objects đúc kết nghiệp vụ (Không phải JPA Entity).
  - `port/in`: Các Use Case interface (Các dịch vụ cung cấp bởi ứng dụng).
  - `port/out`: Các Port giao tiếp ra bên ngoài (ví dụ: `ScanRepository`, `ExternalApiClient`).
  - `service`: Implementation của Use Case interfaces, thuần tính toán logic.
  - `exception`: Các Exception nội bộ cho Domain (extends `RuntimeException`).

## 2. Infrastructure Layer (`/infrastructure` package)
Lớp này cung cấp công cụ tương tác ra bên ngoài (Database, Network, File system).
- **Quy tắc:** Phụ thuộc VÀO Domain Layer (`infrastructure` gọi `domain`).
- **Thành phần:**
  - `adapter/out`: Các Adapter implementation từ `port/out` (ví dụ: `ScanRepositoryImpl`).
  - `entity`: Cấu trúc POJO hoặc Record tương ứng với bảng Database (JPA Entities).
  - `repository`: Các interface `Spring Data JPA` hoặc JDBC Queries.
  - `mapper`: Class/Component dùng để chuyển đổi (map) giữa Domain Entity và JPA Entity.

## 3. Application/API Layer (`/application` hoặc `/api` package)
Tầng nhận request từ người dùng hoặc hệ thống bên ngoài.
- **Quy tắc:** Lớp này cung cấp các Controller (REST, GraphQL, gRPC) nhưng chỉ được phép gọi vào `port/in` (Use Cases) ở tầng Domain thông qua việc map Data Transfer Objects (DTO).
- **Thành phần:**
  - `controller`: Các `@RestController` cung cấp API (e.g., `ScanJobController`).
  - `dto/request`, `dto/response`: DTO nhận và trả về từ frontend (nên dùng Java `record`).
  - `GlobalExceptionHandler`: Xử lý lõi với `@ControllerAdvice` để mapping tất cả exception về một `ErrorResponse` thống nhất.
- **CẤM:** Không được truyền các DB Entity layer (JPA Entity) lên Controller và ném trả về cho Client. Bắt buộc dùng DTOs.
