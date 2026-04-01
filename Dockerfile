# Stage 1: Build Application sử dụng Maven image gốc
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# Copy toàn bộ Source code vào bên trong Docker
COPY . .

# Build dự án ra thư mục target (bỏ qua chạy Unit Test để rút ngắn thời gian)
RUN mvn clean package -DskipTests

# Stage 2: Môi trường chạy JRE thu gọn để giảm Size Image
FROM eclipse-temurin:17-jre
WORKDIR /app

# Coppy file Fat JAR của module đích là app-full ra ngoài
# Lưu ý đường dẫn này sẽ khớp với build output của Spring Boot
COPY --from=builder /build/app-full/target/*-SNAPSHOT.jar /app/security-tools.jar

# Mở cổng 8080 ra khỏi Container
EXPOSE 8080

# Tiến trình chính
ENTRYPOINT ["java", "-jar", "/app/security-tools.jar"]
