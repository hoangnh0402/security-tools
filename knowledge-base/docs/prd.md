# 📄 Product Requirement Document (PRD)
## Security Scanning Tool (Internal Use)

---

## 1. 🎯 Mục tiêu sản phẩm

Xây dựng một công cụ nội bộ giúp:
- Tự động quét và phát hiện các lỗ hổng bảo mật trong các dự án phần mềm của công ty
- Giảm thiểu rủi ro bị khách hàng phát hiện lỗi trước
- Hỗ trợ Dev và Tester phát hiện sớm lỗi security trong quá trình phát triển

---

## 2. 👥 Đối tượng sử dụng (Users)

### 2.1 Developer (Dev)
- Kiểm tra lỗi bảo mật trong code và API
- Xem report và fix bug

### 2.2 Tester (QA/QC)
- Thực hiện scan hệ thống
- Xác nhận và reproduce lỗi

### 2.3 Security Team
- Quản lý và theo dõi tình trạng bảo mật của các dự án
- Phân tích lỗ hổng và đưa ra khuyến nghị

---

## 3. 🧩 Use Cases chính

### 3.1 Scan Web Application
- Nhập URL website
- Hệ thống crawl và quét lỗ hổng
- Trả về danh sách vulnerabilities

### 3.2 Scan API
- Import Swagger / OpenAPI
- Tự động test các endpoint
- Phát hiện lỗi bảo mật API

### 3.3 Scan Source Code
- Upload source code hoặc connect repository
- Phân tích code để tìm lỗi security (SAST)

---

## 4. 📦 Phạm vi (Scope)

### 4.1 MVP (Minimum Viable Product)

#### 🔍 Asset Discovery
- Crawl website để lấy URL, form, endpoint

#### 🌐 Web Vulnerability Scan (Basic)
- Detect:
  - SQL Injection (basic)
  - XSS (basic)

#### 📡 API Scan (Basic)
- Test endpoint từ Swagger/OpenAPI
- Kiểm tra missing authentication

#### 🧾 Static Code Scan (Basic)
- Detect:
  - Hardcoded credentials
  - SQL query không an toàn

#### 📊 Reporting
- Hiển thị danh sách lỗ hổng
- Phân loại severity:
  - High / Medium / Low
- Mô tả:
  - Description
  - Reproduce steps
  - Fix suggestion

---

## 5. 🚫 Out of Scope (Không nằm trong MVP)

- AI-based vulnerability detection
- Advanced payload bypass
- CI/CD integration
- Plugin system
- Business logic testing nâng cao

---

## 6. 📌 Kỳ vọng

- Tool có thể sử dụng nội bộ ngay sau khi hoàn thành MVP
- Có khả năng mở rộng trong các phase tiếp theo
- Hỗ trợ quy trình secure development trong công ty