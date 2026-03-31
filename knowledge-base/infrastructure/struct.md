security-tool/
│
├── pom.xml (parent)
│
├── common/
│   └── pom.xml
│
├── modules/
│   ├── scan/
│   │   └── pom.xml
│   ├── crawler/
│   │   └── pom.xml
│   ├── vulnerability/
│   │   └── pom.xml
│   ├── report/
│   │   └── pom.xml
│
├── infrastructure/
│   └── pom.xml
│
├── application/
│   └── pom.xml
│
├── api/
│   └── pom.xml
│
├── app-full/        # chạy toàn bộ hệ thống
│   └── pom.xml
│
├── app-scan/        # chỉ chạy scan
│   └── pom.xml
│
├── app-crawler/     # chỉ crawler
│   └── pom.xml





scan/
└── src/main/java/com/security/scan/
    ├── domain/
    │   ├── model/
    │   ├── repository/
    │   └── service/
    │
    ├── application/
    │   └── ScanUseCase.java
    │
    └── infrastructure/
        └── ScanRepositoryImpl.java