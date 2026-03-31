# 📦 Dependency Vulnerability Scan

## 1. Mục tiêu
Phát hiện lỗ hổng trong thư viện sử dụng

---

## 2. Input
- File:
  - pom.xml
  - package.json
  - requirements.txt

---

## 3. Chức năng

### 3.1 Parse Dependency
- Extract danh sách thư viện

---

### 3.2 CVE Check
- So sánh với database CVE

---

### 3.3 Version Check
- Detect version lỗi thời

---

## 4. Output
- Library
- Version
- CVE
- Severity