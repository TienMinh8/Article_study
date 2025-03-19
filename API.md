# Tài Liệu API Cho Ứng Dụng Tin Tức

## Các Endpoint API

### 1. Tạo Nội Dung AI
**URL Cơ sở**: `https://ai.dreamapi.net/v1/workflows/run`  
**Xác thực**: Bearer Token (`app-KXgXIc3FMFJZzN46kukP7Avs`)

#### Trường Hợp Sử Dụng:
- Tạo tóm tắt tin tức
- Phân tích nội dung tin tức
- Tạo đề xuất cá nhân hóa

#### Định Dạng Yêu Cầu:
```json
{
  "inputs": {
    "prompt": "Tóm tắt bài báo này",
    "data": "{dữ_liệu_bài_viết_json}",
    "template": "{cấu_trúc_đầu_ra_json}"
  },
  "response_mode": "blocking",
  "user": "{id_người_dùng_duy_nhất}"
}
```

### 2. Chức Năng Trò Chuyện
**URL Cơ sở**: `https://ai.dreamapi.net/v1/chat-messages`  
**Xác thực**: Bearer Token (`app-mrEpeQsGxaEXzpTaUFJOhi4Q`)

#### Trường Hợp Sử Dụng:
- Hỗ trợ người dùng
- Câu hỏi về nội dung
- Hướng dẫn tính năng

#### Định Dạng Yêu Cầu:
```json
{
  "query": "Câu hỏi của người dùng",
  "user": "{id_người_dùng_duy_nhất}",
  "conversation_id": "{id_cuộc_hội_thoại_tùy_chọn}",
  "response_mode": "blocking",
  "inputs": {
    "data": "{ngữ_cảnh_json}"
  }
}
```

### 3. Phân Tích Media
**URL Cơ sở**: `https://ai.dreamapi.net/v1/workflows/run`  
**Xác thực**: Bearer Token (`app-KXgXIc3FMFJZzN46kukP7Avs`)

#### Trường Hợp Sử Dụng:
- Phân tích hình ảnh tin tức
- Trích xuất thông tin chính từ nội dung hình ảnh
- Tăng cường khả năng tiếp cận với mô tả hình ảnh

#### Định Dạng Yêu Cầu:
```json
{
  "inputs": {
    "prompt": "Phân tích hình ảnh tin tức này",
    "data": "{}",
    "template": "{cấu_trúc_đầu_ra_json}"
  },
  "files": [{
    "type": "image",
    "transfer_method": "remote_url",
    "url": "{url_hình_ảnh}"
  }],
  "response_mode": "blocking",
  "user": "{id_người_dùng_duy_nhất}"
}
```

## Hướng Dẫn Triển Khai

### Quản Lý ID Người Dùng
- Tạo và lưu trữ ID người dùng duy nhất khi lần đầu khởi chạy ứng dụng
- Sử dụng ID người dùng nhất quán cho tất cả các yêu cầu API
- Định dạng: Chuỗi UUID v4

### Xử Lý Phản Hồi API
- Luôn ghi nhật ký các yêu cầu và phản hồi
- Triển khai logic thử lại (3 lần với thời gian chờ tăng dần)
- Lưu trữ tạm các phản hồi thành công (TTL: 15 phút)
- Quay lại nội dung đã lưu tạm khi API không khả dụng

### Các Cân Nhắc Bảo Mật
- Không bao giờ để lộ token API trong mã phía máy khách
- Triển khai hạn chế yêu cầu hợp lý
- Xác thực tất cả đầu vào người dùng trước khi gửi đến API

### Tối Ưu Hóa Hiệu Suất
- Tải trước nội dung trong thời gian rảnh
- Xử lý phản hồi API trong các luồng nền
- Gộp các yêu cầu tương tự khi có thể
- Triển khai kết nối tổng hợp 