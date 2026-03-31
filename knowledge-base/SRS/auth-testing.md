# 🔐 Authentication & Authorization Testing

## 1. Mục tiêu
Kiểm tra cơ chế xác thực và phân quyền

---

## 2. Chức năng

### 2.1 Brute-force Protection
- Thử nhiều lần login
- Kiểm tra rate limit

---

### 2.2 JWT Security
- Kiểm tra:
  - Expiration
  - Weak secret
  - None algorithm

---

### 2.3 Role-based Access Control
- Test:
  - User truy cập admin API
  - Bypass role

---

### 2.4 IDOR
- Thay đổi resource ID
- Kiểm tra quyền truy cập

---

## 3. Output
- Danh sách lỗi auth:
  - Loại lỗi
  - Endpoint