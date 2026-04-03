-- V2: Mở rộng bảng vulnerabilities và payloads cho ZAP-like Scanner

-- 1. Thêm các cột báo cáo chi tiết vào vulnerabilities (nếu chưa có)
-- Các cột impact, recommendation, proof_of_concept đã được tạo trong V1
-- nhưng domain model trước đó chưa map. Giờ đảm bảo tồn tại:
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='vulnerabilities' AND column_name='impact') THEN
        ALTER TABLE vulnerabilities ADD COLUMN impact TEXT;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='vulnerabilities' AND column_name='recommendation') THEN
        ALTER TABLE vulnerabilities ADD COLUMN recommendation TEXT;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='vulnerabilities' AND column_name='proof_of_concept') THEN
        ALTER TABLE vulnerabilities ADD COLUMN proof_of_concept TEXT;
    END IF;
END$$;

-- 2. Mở rộng bảng payloads: thêm context và encoding cho Payload Engine nâng cao
ALTER TABLE payloads ADD COLUMN IF NOT EXISTS context VARCHAR(20) DEFAULT 'URL';
ALTER TABLE payloads ADD COLUMN IF NOT EXISTS encoding VARCHAR(30) DEFAULT 'NONE';

-- 3. Seed thêm payloads cho các loại vulnerability mới
-- CSRF payloads
INSERT INTO payloads (type, value, context) VALUES ('CSRF', '<img src="https://target.com/transfer?amount=10000">', 'HTML');
INSERT INTO payloads (type, value, context) VALUES ('CSRF', '<form action="/api/delete" method="POST"><input type="hidden" name="id" value="1"/></form>', 'HTML');

-- SSRF payloads
INSERT INTO payloads (type, value, context) VALUES ('SSRF', 'http://169.254.169.254/latest/meta-data/', 'URL');
INSERT INTO payloads (type, value, context) VALUES ('SSRF', 'http://localhost:22', 'URL');
INSERT INTO payloads (type, value, context) VALUES ('SSRF', 'http://127.0.0.1:3306', 'URL');

-- Open Redirect payloads
INSERT INTO payloads (type, value, context) VALUES ('OPEN_REDIRECT', 'https://evil.com', 'URL');
INSERT INTO payloads (type, value, context) VALUES ('OPEN_REDIRECT', '//evil.com', 'URL');
INSERT INTO payloads (type, value, context) VALUES ('OPEN_REDIRECT', '/\evil.com', 'URL');

-- CORS payloads (dùng làm Origin header)
INSERT INTO payloads (type, value, context) VALUES ('CORS', 'https://evil.com', 'HEADER');
INSERT INTO payloads (type, value, context) VALUES ('CORS', 'null', 'HEADER');

-- Thêm SQLi nâng cao (time-based)
INSERT INTO payloads (type, value, context) VALUES ('SQLI', ''' OR SLEEP(5)--', 'URL');
INSERT INTO payloads (type, value, context) VALUES ('SQLI', ''' OR pg_sleep(5)--', 'URL');
INSERT INTO payloads (type, value, context, encoding) VALUES ('SQLI', '%27%20OR%201%3D1%20--', 'URL', 'URL_ENCODE');

-- Thêm XSS nâng cao
INSERT INTO payloads (type, value, context) VALUES ('XSS', '<img src=x onerror=alert(1)>', 'HTML');
INSERT INTO payloads (type, value, context) VALUES ('XSS', '<svg onload=alert(1)>', 'HTML');
INSERT INTO payloads (type, value, context, encoding) VALUES ('XSS', '%3Cscript%3Ealert(1)%3C/script%3E', 'URL', 'URL_ENCODE');

-- 4. Indexes cho tra cứu payload theo loại
CREATE INDEX IF NOT EXISTS idx_payload_type ON payloads(type);
CREATE INDEX IF NOT EXISTS idx_payload_log_vuln ON payload_logs(vulnerability_id);
CREATE INDEX IF NOT EXISTS idx_report_project ON reports(project_id);
CREATE INDEX IF NOT EXISTS idx_scan_target_job ON scan_targets(scan_job_id);
