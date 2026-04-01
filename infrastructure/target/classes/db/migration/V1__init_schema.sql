-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Users
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role VARCHAR(50) NOT NULL, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Projects
CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Scan Jobs
CREATE TABLE scan_jobs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
    created_by UUID REFERENCES users(id),
    scan_type VARCHAR(50) NOT NULL, 
    status VARCHAR(50) NOT NULL, 
    started_at TIMESTAMP,
    finished_at TIMESTAMP
);

-- 4. Endpoints
CREATE TABLE endpoints (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
    url TEXT NOT NULL,
    method VARCHAR(10) NOT NULL,
    parameters JSONB,
    discovered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Scan Targets
CREATE TABLE scan_targets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    scan_job_id UUID REFERENCES scan_jobs(id) ON DELETE CASCADE,
    target_url TEXT NOT NULL,
    method VARCHAR(10),
    headers JSONB,
    body TEXT
);

-- 6. Vulnerabilities
CREATE TABLE vulnerabilities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    scan_job_id UUID REFERENCES scan_jobs(id) ON DELETE CASCADE,
    endpoint_id UUID REFERENCES endpoints(id) ON DELETE SET NULL,
    type VARCHAR(100), 
    severity VARCHAR(20), 
    title VARCHAR(255) NOT NULL,
    description TEXT,
    impact TEXT,
    recommendation TEXT,
    proof_of_concept TEXT,
    status VARCHAR(50) DEFAULT 'OPEN', 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. Payload Logs
CREATE TABLE payload_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vulnerability_id UUID REFERENCES vulnerabilities(id) ON DELETE CASCADE,
    payload TEXT,
    request_data TEXT,
    response_data TEXT,
    response_time INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. Reports
CREATE TABLE reports (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
    scan_job_id UUID REFERENCES scan_jobs(id) ON DELETE CASCADE,
    summary JSONB,
    file_path TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9. Dependencies
CREATE TABLE dependencies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    version VARCHAR(100),
    ecosystem VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. Dependency Vulnerabilities
CREATE TABLE dependency_vulnerabilities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    dependency_id UUID REFERENCES dependencies(id) ON DELETE CASCADE,
    cve_id VARCHAR(50),
    severity VARCHAR(20),
    description TEXT,
    fixed_version VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. Custom Payloads Dictionary
CREATE TABLE payloads (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    type VARCHAR(50) NOT NULL, -- Ví dụ: SQLI, XSS, CMDI
    value TEXT NOT NULL,       -- Chuỗi tấn công (vd: ' OR 1=1 --)
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Thêm một vài dòng payload mẫu cơ bản ngay khi init DB
INSERT INTO payloads (type, value) VALUES ('SQLI', ''' OR 1=1 --');
INSERT INTO payloads (type, value) VALUES ('SQLI', ''' OR ''1''=''1');
INSERT INTO payloads (type, value) VALUES ('XSS', '<script>alert(1)</script>');
INSERT INTO payloads (type, value) VALUES ('XSS', '"><script>alert(1)</script>');

-- Indexes for performance
CREATE INDEX idx_scan_job_project ON scan_jobs(project_id);
CREATE INDEX idx_vuln_scan_job ON vulnerabilities(scan_job_id);
CREATE INDEX idx_vuln_severity ON vulnerabilities(severity);
CREATE INDEX idx_endpoint_project ON endpoints(project_id);
