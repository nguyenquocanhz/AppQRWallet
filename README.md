# Ví QR - Trợ lý QR & Thanh toán Cá nhân

**Ví QR** là ứng dụng Android hiện đại giúp người dùng tạo, lưu trữ và quét mã QR ngân hàng (VietQR) nhanh chóng. Được thiết kế theo phong cách Material Design 3, ứng dụng mang đến trải nghiệm mượt mà, tích hợp bảo mật sinh trắc học và các tiện ích truy cập nhanh từ hệ thống.

## ✨ Tính năng Nổi bật

*   **⚡ Quét & Tạo QR Thông minh:**
    *   **Quét QR:** Sử dụng **CameraX** kết hợp **ZXing** để nhận diện mã VietQR/EMVCo cực nhanh. Tự động phân tích thông tin (Ngân hàng, Số tài khoản, Số tiền, Nội dung).
    *   **Tạo QR:** Kết nối API VietQR để tạo mã chuyển khoản chính xác kèm logo ngân hàng.
    *   **Lưu trữ:** Tự động lưu lịch sử quét và tạo mã để tra cứu lại dễ dàng.

*   **🛡️ Bảo mật & Riêng tư:**
    *   **Sinh trắc học:** Đăng nhập an toàn bằng Vân tay hoặc FaceID (Biometric API).
    *   **Dữ liệu cục bộ:** Thông tin nhạy cảm được lưu trữ an toàn trên thiết bị người dùng.

*   **📱 Tiện ích Hệ thống:**
    *   **Widget màn hình chính:** Phím tắt giúp mở nhanh trình quét mã ngay từ màn hình chính.
    *   **Quick Settings Tile:** Tích hợp nút quét QR vào thanh cài đặt nhanh (Quick Settings) của Android.
    *   **Dark Mode:** Giao diện tự động thích ứng theo chế độ Sáng/Tối của điện thoại.

## 🛠 Tech Stack (Công nghệ)

Dự án được xây dựng trên nền tảng Java với các thư viện Android Jetpack mới nhất:

*   **Ngôn ngữ:** Java
*   **Android SDK:** Min 24 (Android 7.0) - Target 36
*   **Giao diện:** XML Layouts, Material Design 3 Components.
*   **Kiến trúc:** Mô hình hướng Activity, kết hợp Repository pattern cho xử lý dữ liệu.

### Thư viện chính:
| Thành phần | Thư viện | Mục đích |
| :--- | :--- | :--- |
| **Database** | **Room Database** | Quản lý dữ liệu cục bộ (SQLite abstraction). |
| **Networking** | **Retrofit 2 + Gson** | Gọi API và xử lý dữ liệu JSON. |
| **Camera** | **CameraX** | Xử lý xem trước và phân tích hình ảnh từ camera. |
| **QR Core** | **ZXing** | Giải mã hình ảnh QR code. |
| **Async** | **Executors** | Xử lý tác vụ nền (Background threads). |
| **Cloud** | **Firebase (FCM)** | Nhận thông báo đẩy từ máy chủ. |

## 📂 Cấu trúc Source Code

```text
com.nqatech.vqr
├── adapter/            # RecyclerView Adapters (Hiển thị danh sách)
├── api/                # Retrofit Client & API Interfaces
├── database/           # Room Database, DAOs và Entities
├── qr/                 # Logic xử lý Camera và phân tích mã QR
├── theme/              # Quản lý giao diện và Theme
├── util/               # Các lớp tiện ích (ImageLoader, Biometric, Parser...)
├── [Activities]        # Các màn hình chính (Home, Scan, Create, Detail...)
├── QRWidgetProvider.java    # Xử lý Widget
└── QRScanTileService.java   # Xử lý Quick Settings Tile
```

## 🚀 Hướng dẫn Cài đặt

1.  **Clone dự án:**
    ```bash
    git clone https://github.com/nqatech/vqr-android.git
    ```
2.  **Cấu hình Firebase:**
    *   Tải file `google-services.json` từ Firebase Console.
    *   Copy file vào thư mục `app/` của dự án.
3.  **Build & Run:**
    *   Mở dự án bằng **Android Studio**.
    *   Đợi Gradle sync hoàn tất.
    *   Nhấn **Run** (Shift + F10) để cài đặt lên thiết bị thật (Khuyến nghị để test Camera và Vân tay).

## 📝 Lưu ý Phát triển

*   Dự án sử dụng Java 11 (cấu hình trong `build.gradle.kts`).
*   Khi chỉnh sửa Database (`User`, `Recipient`), cần cập nhật version database hoặc migrate phù hợp.
*   Các key API hoặc thông tin nhạy cảm không nên commit lên git (sử dụng `local.properties` nếu cần).

---
*Dự án được phát triển bởi NQATech.*
