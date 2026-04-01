---
name: HQC Spring Boot Coding Standards
description: Bộ quy tắc (Rules) về cách viết API, Error Handling, Naming Conventions và DI cho framework Spring Boot 3 & Java 17. Đọc kỹ khi viết Code Controller hoặc Layer Application.
---

# HQC Spring Boot & Java Coding Standards

Khi sinh mã (generate code) cho dự án Security Tools, AI và Developer cần tuân thủ cấu trúc code theo hướng dẫn dưới đây:

## 1. Java 17 / 21
Sử dụng triệt để những tính năng hiện đại:
- Bắt buộc sử dụng `record` (thay thế POJO class thường) để định nghĩa các Data Transfer Object (DTO) hoặc Value Object phi trạng thái.
- Tận dụng `switch` expressions, text blocks, type inference (`var`) ở trong block statements cho code ngắn gọn.

## 2. RESTful APIs
Tuân theo chuẩn API Maturity (Level 2+) và OpenAPI 3.0:
- **Paths:** URL viết bằng chữ thường + kebab-case, ưu tiên danh từ số nhiều. Ví dụ: `GET /api/v1/scan-jobs/{id}`.
- **Methods:** Dùng chuẩn `GET` (đọc), `POST` (tạo), `PUT` (cập nhật toàn phần), `PATCH` (Cập nhật 1 phần), `DELETE` (xoá).
- **Status codes:** Trả về HTTP status đúng ngữ cảnh (201 Created, 204 No Content, 400 Bad Request, 404 Not Found, 403 Forbidden).

## 3. Quản lý Lỗi (Exception Handling)
Tất cả ngoại lệ (Exception) phải được bọc trong một form JSON duy nhất và không bị crash app, nhờ `@RestControllerAdvice`:
```json
{
  "timestamp": "2026-04-01T...Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Nội dung lỗi chi tiết",
  "path": "/api/v1/scan-jobs"
}
```

## 4. Constructor Injection (DI)
Không sử dụng Injection bằng Field (ví dụ: `@Autowired private ScanService scanService`).
- **Standard:** Bắt buộc dùng Constructor Injection.
- Khuyến nghị sử dụng Annotation `@RequiredArgsConstructor` từ thư viện Lombok và khai báo thuộc tính dạng `private final`.

## 5. Naming Conventions (Quy tắc Đặt tên)
- **Interface:** Không dùng tiền tố `I` (Dùng `ScanService`, không dùng `IScanService`).
- **Interface Implementations:** Đặt hậu tố `Impl` (VD: `ScanServiceImpl`, `UserRepositoryImpl`).
- **Packages:** Được viết chữ thường, và không chứa dấu gạch dưới `_`.
- **Classes/Records:** dùng `PascalCase` (VD: `CreateScanJobRequest`).
- **Variables/Methods:** dùng `camelCase` (VD: `startNewScan()`).
- **Constants:** dùng `UPPER_SNAKE_CASE` (VD: `MAX_TIMEOUT_SECONDS`).
