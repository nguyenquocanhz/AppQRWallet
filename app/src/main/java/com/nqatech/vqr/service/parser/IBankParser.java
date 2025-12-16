package com.nqatech.vqr.service.parser;

public interface IBankParser {
    /**
     * Phân tích nội dung thông báo để lấy số tiền
     * @param title Tiêu đề thông báo
     * @param content Nội dung thông báo
     * @return Số tiền (double), trả về 0 nếu không tìm thấy hoặc lỗi
     */
    double parseAmount(String title, String content);
}