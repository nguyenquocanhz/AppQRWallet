# Ví QR - Ứng dụng Quản lý & Thanh toán QR

**Ví QR** là một ứng dụng Android hiện đại, được thiết kế để đơn giản hóa việc quét và tạo mã QR, được bảo mật bằng tài khoản Google và xác thực sinh trắc học (vân tay/khuôn mặt).

## 🚀 Tính Năng Chính

*   **Bảo Mật Hiện Đại:**
    *   Đăng nhập an toàn bằng tài khoản Google (Google Sign-In).
    *   Khóa ứng dụng bằng **xác thực sinh trắc học** (Vân tay/Khuôn mặt), thay thế hoàn toàn mã PIN.
    *   Hiển thị thông tin người dùng (tên và ảnh đại diện) trực tiếp từ tài khoản Google.
    *   Cải thiện tính năng ghim mã QR, cho phép người dùng chọn mã QR mặc định từ danh sách.
*   **Quét & Tạo QR Code:**
    *   Quét mã QR nhanh chóng và chính xác với CameraX.
    *   Dễ dàng tạo mã QR thanh toán theo chuẩn VietQR.
*   **Tiện Ích Mở Rộng:**
    *   **App Widget:** Truy cập nhanh tính năng quét QR từ màn hình chính.
    *   **Quick Settings Tile:** Mở máy quét QR tức thì từ thanh cài đặt nhanh.

## 🔒 Bảo Mật

Bảo vệ dữ liệu người dùng là ưu tiên hàng đầu. **Ví QR** áp dụng nhiều lớp bảo mật để đảm bảo an toàn cho thông tin của bạn:

*   **Xác thực Sinh trắc học:** Sau khi đăng nhập bằng Google, ứng dụng được bảo vệ bởi lớp xác thực sinh trắc học của thiết bị, đảm bảo chỉ bạn mới có thể truy cập.
*   **Mã hóa Dữ liệu:** Tất cả các thông tin nhạy cảm được lưu trữ bằng `EncryptedSharedPreferences` của AndroidX Security.
*   **Bảo vệ Chống Chụp Màn Hình:** Các màn hình chứa thông tin nhạy cảm được bảo vệ, không cho phép chụp ảnh hoặc quay video màn hình.
*   **Làm rối Mã nguồn (Obfuscation):** Trong các phiên bản phát hành (release), chúng tôi sử dụng ProGuard/R8 để làm rối mã nguồn, gây khó khăn cho việc dịch ngược ứng dụng.

## 🛠 Công Nghệ Sử Dụng

*   **Ngôn ngữ:** Java
*   **Kiến trúc:** MVVM (đang chuyển đổi) / Activity-based
*   **Bảo mật:** Google Sign-In, **AndroidX Biometric**, AndroidX Security
*   **Giao diện:** XML Layouts, Material Design 3
*   **Cơ sở dữ liệu:** Room Database (SQLite)
*   **Camera & QR:** Android CameraX, ZXing Library

## 📦 Cài Đặt

1.  Clone repository về máy.
2.  Mở dự án bằng **Android Studio**.
3.  Đồng bộ hóa Gradle (Sync Project with Gradle Files).
4.  Kết nối thiết bị và nhấn **Run**.

---
*Dự án được phát triển bởi NQATech.*
