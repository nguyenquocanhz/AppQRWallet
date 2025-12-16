# Ví QR - Ứng dụng Quản lý & Thanh toán QR

**Ví QR** là một ứng dụng Android hiện đại, được thiết kế để đơn giản hóa việc quét mã QR, tạo mã thanh toán và quản lý thông tin tài khoản ngân hàng. Dự án tích hợp nhiều công nghệ tiên tiến như quét NFC, nhận diện sinh trắc học và đồng bộ đám mây.

## 🚀 Tính Năng Chính

*   **Quét QR Code Thông Minh:**
    *   Sử dụng CameraX và ZXing để quét mã QR nhanh chóng và chính xác.
    *   Hỗ trợ quét mã VietQR và các loại mã QR thanh toán phổ biến.
    *   Tích hợp đèn Flash và khả năng quét từ thư viện ảnh.
*   **Tạo Mã QR Cá Nhân:**
    *   Dễ dàng tạo mã QR cho tài khoản ngân hàng của bạn (chuẩn VietQR).
    *   Tùy chỉnh thông tin số tiền và nội dung chuyển khoản.
*   **Quản Lý Ví & Danh Bạ:**
    *   Lưu trữ danh sách người thụ hưởng.
    *   Xem lại lịch sử quét và các mã QR đã tạo.
*   **Đọc NFC (CCCD/Hộ Chiếu):**
    *   Tích hợp tính năng đọc thẻ Căn cước công dân gắn chip và Hộ chiếu điện tử qua giao thức NFC (sử dụng thư viện JMRTD).
*   **Tiện Ích Mở Rộng:**
    *   **App Widget:** Đưa tính năng quét QR ra ngay màn hình chính.
    *   **Quick Settings Tile:** Phím tắt trên thanh cài đặt nhanh giúp mở máy quét QR tức thì từ bất kỳ đâu.
*   **Bảo Mật & Thông Báo:**
    *   Hỗ trợ đăng nhập và xác thực sinh trắc học (Vân tay/Khuôn mặt).
    *   Nhận thông báo đẩy (Push Notification) qua Firebase Cloud Messaging.

## 🛠 Công Nghệ Sử Dụng

*   **Ngôn ngữ:** Java
*   **SDK:** Min 24 (Android 7.0), Target 36
*   **Kiến trúc:** MVVM (đang chuyển đổi) / Activity-based
*   **Giao diện:** XML Layouts, Material Design 3
*   **Cơ sở dữ liệu:** Room Database (SQLite)
*   **Kết nối mạng:** Retrofit, Gson
*   **Camera & QR:** Android CameraX, ZXing Library
*   **NFC & Identity:** JMRTD, Scuba (cho việc đọc chip Passport/CCCD)
*   **Cloud Services:** Google Firebase (Messaging, Analytics)

## 📂 Cấu Trúc Dự Án

*   `ui/`: Chứa các Activity và Fragment (Scan, Home, Settings, NFC...).
*   `database/`: Các Entity và DAO của Room Database.
*   `api/`: Cấu hình Retrofit và các Interface gọi API.
*   `qr/`: Các lớp xử lý logic quét và phân tích mã QR.
*   `services/`: Các Service chạy nền (FCM, TileService).

## 📦 Cài Đặt

1.  Clone repository về máy:
    ```bash
    git clone https://github.com/your-repo/VQR.git
    ```
2.  Mở dự án bằng **Android Studio**.
3.  Đồng bộ hóa Gradle (Sync Project with Gradle Files).
4.  Kết nối thiết bị thật hoặc máy ảo và nhấn **Run**.

---
*Dự án được phát triển bởi NQATech.*
