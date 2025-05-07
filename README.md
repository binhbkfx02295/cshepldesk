# CS Help Desk

CS Help Desk là một hệ thống hỗ trợ khách hàng được phát triển cho công ty Thiên An Phú – chuyên phân phối thực phẩm chức năng dành cho nam giới. Hệ thống giúp tự động hóa việc tạo ticket từ tin nhắn Facebook, quản lý vận hành chăm sóc khách hàng, và cung cấp phân tích hiệu suất làm việc.

## 🌟 Tính năng chính

- Tích hợp Webhook Facebook để nhận tin nhắn và tạo ticket tự động
- Quản lý người dùng Facebook và nhân viên nội bộ
- Quản lý ticket, trạng thái xử lý, ghi chú và hashtag
- Theo dõi cảm xúc và mức độ hài lòng của khách hàng
- Phân quyền truy cập theo nhóm và vai trò
- Giao diện Dashboard cho giám sát và điều phối

## 🧰 Công nghệ sử dụng

- **Backend:** Java Spring Boot 3.4.x
- **Frontend:** HTML, SCSS, Bootstrap 5, jQuery (UI đơn trang)
- **Database:** MySQL (Amazon RDS)
- **Triển khai:** Docker, AWS EC2, Docker Compose
- **CI/CD:** Jenkins
- **Tích hợp:** Facebook Graph API

## 🚀 Hướng dẫn chạy hệ thống

### Yêu cầu

- Docker & Docker Compose
- Java 17+ (nếu chạy thủ công)
- MySQL (hoặc sử dụng docker container có sẵn)
- Maven

### Chạy bằng Docker

```bash
docker-compose up --build
```

### Chạy thủ công

```bash
./mvnw spring-boot:run
```

### Cấu hình môi trường (ví dụ `.env` hoặc application.properties)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cshelpdesk
spring.datasource.username=root
spring.datasource.password=your_password
facebook.page.access-token=your_facebook_token
```

## 📁 Cấu trúc chính

- `ticket_management/`: quản lý ticket, hashtag, cảm xúc, trạng thái
- `facebook_user/`: thông tin người dùng Facebook
- `employee/`: quản lý nhân viên, nhóm người dùng và quyền
- `security/`: đăng nhập, kiểm soát session, bảo mật
- `webhook/`: tiếp nhận và xử lý message từ Facebook
- `masterdata/`: cache dữ liệu như trạng thái, cảm xúc, mức độ hài lòng

## 👨‍💻 Tác giả

Capstone Project – Nhóm phát triển CS Help Desk  
Giám sát: Thầy Phạm Đức Thắng

## 📄 License

This project is licensed under the MIT License.
