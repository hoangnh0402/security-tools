## 1. 🔍 Asset Discovery (Quét hệ thống)

### Chức năng:
- Scan domain / IP / endpoint
- Detect:
  - Port open
  - Service running
  - API endpoints (REST / GraphQL)

### Crawl website để lấy:
- URL
- Form
- API hidden

👉 Giống: Nmap + crawler

---

## 2. 🌐 Web Vulnerability Scanner

👉 Đây là core quan trọng nhất

### Detect các lỗi phổ biến:
- SQL Injection
- XSS (Stored / Reflected / DOM)
- CSRF
- SSRF
- Open Redirect
- File Upload vulnerability
- IDOR (Broken Access Control)
- CORS misconfiguration

### Cách làm:
- Inject payload tự động
- Phân tích response:
  - Status code
  - Content
  - Timing

---

## 3. 🔐 Authentication & Authorization Testing

### Chức năng:
- Test login brute-force (rate limit)

### Kiểm tra:
- JWT bị sai config:
  - Không có expiry
  - Secret yếu
- Role bypass (user → admin)
- IDOR (truy cập tài nguyên người khác)

👉 Ghi chú:
> 80% bug thực tế nằm ở auth + business logic

---

## 4. 📦 Dependency Vulnerability Scan

### Scan thư viện:
- Maven (Java)
- npm (JavaScript)
- pip (Python)

### Kiểm tra:
- CVE
- Version lỗi thời

👉 Tương tự:
- Snyk
- OWASP Dependency Check

---

## 5. 📡 API Security Testing

### Input:
- Swagger / OpenAPI

### Auto test:
- Missing authentication
- Rate limit
- Data exposure
- Mass assignment

---

## 6. ⚡ Dynamic Scan (DAST)

👉 Scan khi application đang chạy

### Flow:
- Crawl → Test → Detect vulnerability
- Replay request với payload

---

## 7. 🧾 Static Scan (SAST) *(khuyến nghị có)*

👉 Scan source code

### Detect:
- Hardcoded password
- SQL query không an toàn
- Unsafe deserialization
- Command injection

---

## 8. 📊 Reporting & Dashboard

👉 Rất quan trọng cho công ty outsource

### Dashboard:
- Tổng số bug theo project
- Phân loại severity:
  - Critical
  - High
  - Medium
  - Low

### Report export:
- PDF / Excel

### Nội dung report:
- Description
- Steps to reproduce
- Impact
- Fix suggestion

---

## 9. 🔄 CI/CD Integration

👉 MUST HAVE (rất quan trọng)

### Tích hợp:
- Jenkins
- GitLab CI
- GitHub Actions

### Auto scan khi:
- Pull request
- Deploy staging

### Output:
- Fail build nếu có bug nghiêm trọng

---

## 10. 🧠 Payload Engine (Core kỹ thuật)

### Payload library:
- XSS payloads
- SQL Injection payloads

### Tính năng:
- Random hóa payload
- Bypass filter
- Context-aware:
  - HTML
  - JSON
  - Header