---
name: HQC Database & Integrations Rules
description: Hướng dẫn quản lý CSDL PostgreSQL, quy tắc đặt tên bảng, column và công cụ Migration Flyway. Cần tham khảo khi thiết kế Entity và chạy Integration Tests.
---

# HQC Database & Flyway Rules

## 1. Cơ sở dữ liệu sử dụng
- Dự án sử dụng **PostgreSQL**.
- Cho các trường hợp tính toán Search cao cấp sau này, có thể áp dụng thêm MongoDB / Elasticsearch, nhưng Data chính vẫn nằm trọn trong SQL.

## 2. Quản lý cấu trúc Migration (Flyway)
- Bắt buộc phải sử dụng `Flyway` (hoặc Liquibase) cho mọi phiên bản Schema.
- Không bao giờ thiết lập cấu hình Hibernate `spring.jpa.hibernate.ddl-auto=update` trong production. Cấu hình này chỉ được dùng trong in-memory/test.
- Script đặt trong folder `/src/main/resources/db/migration` theo thứ tự đặt tên chuẩn: `V1__init_schema.sql`, `V2__add_project_table.sql`.

## 3. Naming Conventions (Cơ sở dữ liệu)
- Tên Database Tables: `snake_case`, danh từ số nhiều (ví dụ: `scan_jobs`, `vulnerabilities`, `projects`).
- Tên Database Columns: `snake_case` (ví dụ: `target_url`, `created_at`).
- Chú ý lập chỉ mục (Indexes) trên các biến ForeignKey (`project_id`) để truy vấn nhanh hơn.

## 4. Automated Testing
- Dùng thư viện `Testcontainers` (Container PostgreSQL) thay vì in-memory DB `H2` cho các bài Regression/Integration Tests với Repository (ví dụ: dùng anotation `@DataJpaTest` hoặc `@SpringBootTest` kèm `@Testcontainers`). Môi trường Database ở Test và Production phải đồng nhất để không bị rủi ro conflict Syntax SQL đặc thù của Postgres.
