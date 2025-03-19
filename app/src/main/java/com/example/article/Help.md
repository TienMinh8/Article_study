# Hướng dẫn sử dụng ứng dụng tin tức

## Tổng quan

Đây là một ứng dụng đọc tin tức hiện đại cho phép người dùng xem các tin tức mới nhất từ khắp nơi trên thế giới. Ứng dụng sử dụng NewsAPI để lấy dữ liệu và hiển thị thông tin một cách trực quan.

## Các tính năng chính

### Breaking News Slider
- Hiển thị tối đa 5 bài viết quan trọng nhất từ chủ đề "Tesla"
- Tự động chuyển trang mỗi 3 giây
- Vuốt trái/phải để xem các bài viết khác
- Nhấn vào để xem chi tiết bài viết

### Đề xuất bài viết
- Hiển thị danh sách các bài viết được đề xuất dựa trên từ khóa "technology"
- Nhấn "Xem tất cả" để tải thêm bài viết
- Nhấn vào bài viết để xem chi tiết

### Tìm kiếm
- Tìm kiếm bài viết theo từ khóa
- Hỗ trợ tìm kiếm theo cả tiếng Anh và tiếng Việt

### Tải thêm
- Tính năng tải thêm 10 bài viết khi kéo xuống cuối danh sách
- Hiển thị hiệu ứng loading khi đang tải

### Hiển thị chi tiết
- Xem bài viết đầy đủ với trình duyệt tích hợp
- Hỗ trợ zoom và điều hướng trong trang

## Xử lý khi không có kết nối mạng

Ứng dụng xử lý sự cố mạng bằng các cách sau:

1. **Thông báo thân thiện**: Hiển thị thông báo cụ thể về vấn đề kết nối
2. **Nút Thử lại**: Thêm nút "Retry" cho phép người dùng thử kết nối lại ngay lập tức
3. **Dữ liệu mẫu**: Hiển thị dữ liệu mẫu khi không thể tải từ API

## Hiệu ứng Shimmer khi đang tải

Ứng dụng sử dụng hiệu ứng shimmer để hiển thị trạng thái đang tải:
1. Hiệu ứng loading cho các bài viết chính (Breaking News)
2. Hiệu ứng loading cho danh sách bài viết đề xuất
3. Hiệu ứng loading khi tìm kiếm hoặc tải thêm bài viết

## Đa ngôn ngữ

Ứng dụng hỗ trợ cả tiếng Anh và tiếng Việt:
1. Tự động hiển thị ngôn ngữ theo cài đặt thiết bị
2. Hỗ trợ tìm kiếm bằng cả hai ngôn ngữ
3. Định dạng thời gian theo ngôn ngữ hiện tại (ví dụ: "2 giờ trước" hoặc "2 hours ago")

## Xử lý sự cố phổ biến

### Lỗi kết nối
Nguyên nhân có thể là:
1. Mạng Internet không ổn định
2. NewsAPI tạm thời không khả dụng

Cách khắc phục:
1. Kiểm tra kết nối Internet của bạn
2. Nhấn nút "Retry" trong thông báo lỗi
3. Nếu vẫn không thành công, ứng dụng sẽ hiển thị dữ liệu mẫu

### Lỗi "Too Many Requests"
Nguyên nhân: API key đã đạt đến giới hạn số lượng yêu cầu cho phép.

Cách khắc phục:
1. Đợi một lúc trước khi thử lại
2. Ứng dụng sẽ hiển thị dữ liệu mẫu

## Phiên bản và cập nhật

Ứng dụng được cập nhật thường xuyên với các tính năng mới và cải tiến hiệu suất. Kiểm tra phiên bản mới nhất để có trải nghiệm tốt nhất. 