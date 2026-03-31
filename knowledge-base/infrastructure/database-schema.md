# 🗄️ Database Schema – Security Scanning Tool

## 📌 Database đề xuất

* PostgreSQL

---

## 🧩 I. Tổng quan các bảng

* users
* projects
* scan_jobs
* scan_targets
* endpoints
* vulnerabilities
* payload_logs
* reports
* dependencies
* dependency_vulnerabilities

---

## 👤 II. Users & Projects

### 1. users

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(100),
    email VARCHAR(255),
    password_hash TEXT,
    role VARCHAR(50), -- DEV, TESTER, SECURITY
    created_at TIMESTAMP
);
```

---

### 2. projects

```sql
CREATE TABLE projects (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMP
);
```

---

## 🚀 III. Scan System

### 3. scan_jobs

```sql
CREATE TABLE scan_jobs (
    id UUID PRIMARY KEY,
    project_id UUID REFERENCES projects(id),
    created_by UUID REFERENCES users(id),
    scan_type VARCHAR(50), -- WEB, API, SAST, DEPENDENCY
    status VARCHAR(50), -- PENDING, RUNNING, DONE, FAILED
    started_at TIMESTAMP,
    finished_at TIMESTAMP
);
```

---

### 4. scan_targets

```sql
CREATE TABLE scan_targets (
    id UUID PRIMARY KEY,
    scan_job_id UUID REFERENCES scan_jobs(id),
    target_url TEXT,
    method VARCHAR(10),
    headers JSONB,
    body TEXT
);
```

---

## 🌐 IV. Crawling & Endpoint

### 5. endpoints

```sql
CREATE TABLE endpoints (
    id UUID PRIMARY KEY,
    project_id UUID REFERENCES projects(id),
    url TEXT,
    method VARCHAR(10),
    parameters JSONB,
    discovered_at TIMESTAMP
);
```

---

## 🧨 V. Vulnerability Core

### 6. vulnerabilities

```sql
CREATE TABLE vulnerabilities (
    id UUID PRIMARY KEY,
    scan_job_id UUID REFERENCES scan_jobs(id),
    endpoint_id UUID REFERENCES endpoints(id),

    type VARCHAR(100), -- XSS, SQLI, CSRF...
    severity VARCHAR(20), -- CRITICAL, HIGH, MEDIUM, LOW

    title VARCHAR(255),
    description TEXT,
    impact TEXT,
    recommendation TEXT,

    proof_of_concept TEXT,

    status VARCHAR(50), -- OPEN, FIXED, VERIFIED
    created_at TIMESTAMP
);
```

---

## 🧠 VI. Payload & Scan Logs

### 7. payload_logs

```sql
CREATE TABLE payload_logs (
    id UUID PRIMARY KEY,
    vulnerability_id UUID REFERENCES vulnerabilities(id),

    payload TEXT,
    request_data TEXT,
    response_data TEXT,
    response_time INT,

    created_at TIMESTAMP
);
```

---

## 📊 VII. Report System

### 8. reports

```sql
CREATE TABLE reports (
    id UUID PRIMARY KEY,
    project_id UUID REFERENCES projects(id),
    scan_job_id UUID REFERENCES scan_jobs(id),

    summary JSONB,
    file_path TEXT,

    created_at TIMESTAMP
);
```

---

## 📦 VIII. Dependency Scan

### 9. dependencies

```sql
CREATE TABLE dependencies (
    id UUID PRIMARY KEY,
    project_id UUID REFERENCES projects(id),

    name VARCHAR(255),
    version VARCHAR(100),
    ecosystem VARCHAR(50), -- Maven, npm, pip

    created_at TIMESTAMP
);
```

---

### 10. dependency_vulnerabilities

```sql
CREATE TABLE dependency_vulnerabilities (
    id UUID PRIMARY KEY,
    dependency_id UUID REFERENCES dependencies(id),

    cve_id VARCHAR(50),
    severity VARCHAR(20),
    description TEXT,
    fixed_version VARCHAR(100),

    created_at TIMESTAMP
);
```

---

## 🔗 IX. Quan hệ (ERD đơn giản)

```
users → projects → scan_jobs → vulnerabilities
                         ↓
                    scan_targets
                         ↓
                     endpoints
```

---

## 🚀 X. Index & Optimization

```sql
CREATE INDEX idx_scan_job_project ON scan_jobs(project_id);
CREATE INDEX idx_vuln_scan_job ON vulnerabilities(scan_job_id);
CREATE INDEX idx_vuln_severity ON vulnerabilities(severity);
CREATE INDEX idx_endpoint_project ON endpoints(project_id);
```

---

## 💡 XI. Gợi ý mở rộng

### Multi-tenant

* Thêm `organization_id` vào bảng projects

### RBAC

* roles
* permissions

### Plugin system

* scan_rules
* plugins

---

## 🎯 XII. Mapping với Backend (Spring Boot)

### Entity đề xuất:

* User
* Project
* ScanJob
* Endpoint
* Vulnerability
* Report

### Công nghệ:

* Spring Data JPA
* Hibernate
* JSONB → sử dụng hibernate-types

---

## ✅ Kết luận

Schema này đảm bảo:

* Dễ mở rộng
* Hỗ trợ multi project
* Phù hợp CI/CD
* Tối ưu cho scan security automation
