# Cấu Trúc Điều Hướng Ứng Dụng

## Sơ Đồ Luồng Màn Hình

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│    Khởi động    │────▶│    Giới thiệu   │────▶│  Trang chủ      │
└─────────────────┘     └─────────────────┘     └────────┬────────┘
                                                          │
                                                          ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Đánh dấu      │◀───▶│   Danh mục      │◀───▶│  Chi tiết bài   │
└─────────────────┘     └─────────────────┘     └────────┬────────┘
                                                          │
                                                          ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Bình luận     │◀───▶│  Tin liên quan  │◀───▶│     Chia sẻ     │
└─────────────────┘     └─────────────────┘     └─────────────────┘

┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Thông báo      │     │     Cá nhân     │     │    Cài đặt      │
└────────┬────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 ▼
                        ┌─────────────────┐
                        │  Thống kê sử dụng│
                        └─────────────────┘
```

## Thành Phần Điều Hướng Chính

### Thanh Điều Hướng Dưới
- Trang chủ
- Khám phá
- Đã lưu
- Thông báo
- Cá nhân

## Mô Tả Màn Hình

### Màn Hình Trang Chủ
- Trình chiếu nổi bật
- Tab danh mục
- Nguồn cấp tin tức
- Hành động nhanh

### Chi Tiết Bài Viết
- Nội dung đầy đủ bài viết
- Bài viết liên quan
- Phần bình luận
- Tùy chọn chia sẻ
- Chức năng đánh dấu

### Màn Hình Danh Mục
- Danh sách bài viết trong danh mục
- Tùy chọn sắp xếp
- Điều khiển lọc

### Màn Hình Cá Nhân
- Tùy chọn người dùng
- Thống kê đọc
- Bài viết đã lưu
- Truy cập cài đặt ứng dụng

### Màn Hình Cài Đặt
- Tùy chọn thông báo
- Tùy chọn hiển thị
- Tùy chọn nội dung
- Cài đặt sức khỏe kỹ thuật số

### Trung Tâm Thông Báo
- Cảnh báo tin nóng
- Cập nhật chủ đề
- Thông báo hệ thống
- Cảnh báo sử dụng

## Cửa Sổ và Hộp Thoại

### Cảnh Báo Sức Khỏe
- Xuất hiện sau 180 phút sử dụng
- Tùy chọn: Tạm nghỉ, Tiếp tục đọc, Bỏ qua

### Tùy Chỉnh Nguồn Tin
- Lựa chọn chủ đề
- Tùy chọn nguồn
- Lọc nội dung

### Chia Sẻ Bài Viết
- Tùy chọn mạng xã hội
- Sao chép liên kết
- Gửi cho bạn bè
- Lưu dưới dạng hình ảnh
