# Ví QR - Ứng dụng Quản lý & Thanh toán QR

**Ví QR** là ứng dụng Android native giúp người dùng tạo, lưu trữ và quét mã QR ngân hàng (VietQR) một cách nhanh chóng và tiện lợi. Dự án được thiết kế theo phong cách hiện đại (Material Design 3), tích hợp bảo mật sinh trắc học và đồng bộ thông báo thời gian thực.

## 🛠 Tech Stack (Công nghệ sử dụng)

*   **Ngôn ngữ:** Java
*   **Minimum SDK:** 24 (Android 7.0)
*   **Target SDK:** 36
*   **UI Framework:** XML Layouts với Material Design 3 (M3).
*   **Kiến trúc:** Mô hình hướng Activity (Activity-driven), quản lý luồng dữ liệu đơn giản.
*   **Cơ sở dữ liệu:** [Room Database](https://developer.android.com/training/data-storage/room) (SQLite abstraction).
*   **Networking:** [Retrofit 2](https://square.github.io/retrofit/) + Gson (Gọi API VietQR).
*   **Camera & Xử lý ảnh:**
    *   [CameraX](https://developer.android.com/training/camerax): Quét mã QR.
    *   [ZXing](https://github.com/zxing/zxing): Giải mã QR Code.
    *   Custom ImageLoader: Tải ảnh bất đồng bộ.
*   **Bảo mật:** [Biometric API](https://developer.android.com/training/sign-in/biometric-auth) (Vân tay/FaceID).
*   **Cloud & Tiện ích:**
    *   [Firebase Cloud Messaging (FCM)](https://firebase.google.com/docs/cloud-messaging): Nhận thông báo đẩy.
    *   [Google Services Plugin](): Tích hợp dịch vụ Google.

## 📂 Cấu trúc Dự án

Dự án được tổ chức theo các gói (packages) dựa trên chức năng:

```
com.nqatech.vqr
├── adapter/            # RecyclerView Adapters (VietQRAdapter, BankAdapter)
├── api/                # Retrofit Interfaces & Models (VietQRService, ApiClient)
│   └── model/          # Data Models cho API (Bank, GenQRResponse)
├── database/           # Room Database setup
│   ├── dao/            # Data Access Objects (RecipientDao)
│   └── entity/         # Database Tables (Recipient, User)
├── qr/                 # Logic xử lý QR (Camera, Analyzer)
├── theme/              # Quản lý giao diện Sáng/Tối (ThemeManager)
├── util/               # Các lớp tiện ích (BiometricUtil, VietQRParser, ImageLoader)
└── [Activities]        # Các màn hình chính (Home, Scan, Create, Detail...)
```

## ✨ Tính năng Chính

1.  **Trang chủ (Dashboard):**
    *   Hiển thị lời chào theo thời gian (Sáng/Tối).
    *   Lối tắt nhanh: Quét mã, Lịch sử, Mã thanh toán chính.
    *   Danh sách mã QR đã tạo gần đây.

2.  **Tạo mã QR (VietQR Generator):**
    *   Hỗ trợ chọn ngân hàng từ danh sách (có Logo).
    *   Nhập thông tin tài khoản, số tiền, nội dung.
    *   Tự động gọi API VietQR để tạo ảnh QR chính xác.
    *   Lưu trữ lịch sử vào Database cục bộ.

3.  **Quét mã QR (Scanner):**
    *   Sử dụng CameraX để quét mã VietQR.
    *   Tự động nhận diện chuỗi EMVCo và phân tích thông tin (Số tài khoản, BIN, Số tiền...).
    *   Tự động lưu mã quét được vào lịch sử.

4.  **Chi tiết & Quản lý:**
    *   Xem chi tiết mã QR với ảnh QR sắc nét.
    *   Lưu ảnh QR vào thư viện ảnh.
    *   Chia sẻ ảnh QR qua ứng dụng khác.
    *   Xóa mã QR khỏi lịch sử.

5.  **Bảo mật & Cài đặt:**
    *   Đăng nhập bằng Vân tay/FaceID.
    *   Tùy chỉnh giao diện Sáng/Tối (Dark Mode).

## 🚀 Hướng dẫn Cài đặt (Setup)

1.  **Clone dự án:**
    ```bash
    git clone https://github.com/your-username/vqr-android.git
    ```
2.  **Cấu hình Firebase:**
    *   Tải file `google-services.json` từ Firebase Console của bạn.
    *   Đặt file vào thư mục `app/`.
3.  **Build dự án:**
    *   Mở bằng Android Studio.
    *   Sync Gradle để tải các thư viện dependencies.
4.  **Chạy ứng dụng:**
    *   Kết nối thiết bị thật (Khuyến nghị để test Camera và Vân tay).
    *   Run (Shift + F10).

## 📝 Dependencies Quan trọng (libs.versions.toml)

*   `androidx.room:room-runtime`
*   `com.squareup.retrofit2:retrofit`
*   `androidx.camera:camera-core`
*   `com.google.firebase:firebase-messaging`
*   `androidx.biometric:biometric`

---
*Dự án được phát triển bởi NQATech.*
