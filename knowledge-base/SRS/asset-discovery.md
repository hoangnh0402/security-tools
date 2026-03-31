# 🔍 Asset Discovery Module

## 1. Mục tiêu
Thu thập thông tin hệ thống mục tiêu bao gồm:
- Domain
- IP
- Port
- Endpoint (Web/API)

---

## 2. Input
- URL (https://example.com)
- IP address
- Domain name

---

## 3. Chức năng chính

### 3.1 Port Scanning
- Scan các port phổ biến (80, 443, 22, 3306,...)
- Detect trạng thái:
  - Open
  - Closed
  - Filtered

---

### 3.2 Service Detection
- Xác định service đang chạy:
  - HTTP / HTTPS
  - SSH
  - FTP
- Lấy banner nếu có

---

### 3.3 Website Crawling
- Crawl toàn bộ URL trong website
- Thu thập:
  - Link nội bộ
  - Form (input, method, action)
  - Parameter

---

### 3.4 API Endpoint Discovery
- Detect API endpoint từ:
  - JS files
  - Network request
- Nhận diện:
  - REST API
  - GraphQL

---

## 4. Output
- Danh sách:
  - URL
  - Endpoint
  - Port & service

---

## 5. Ghi chú kỹ thuật
- Sử dụng BFS/DFS để crawl
- Giới hạn depth crawl
- Tránh loop