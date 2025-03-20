# Nhật Ký Thay Đổi

## [Unreleased]

### Sửa lỗi (Fix)
- Sửa lỗi hiển thị icon trong dark mode bằng cách thay đổi thuộc tính tint để sử dụng màu theo theme thay vì màu đen cứng
- Sửa lỗi trùng lặp chuỗi `high_contrast` trong tệp strings.xml
- Tạo file `rounded_button_background.xml` để làm nền cho các nút chia sẻ
- Cập nhật ProfileFragment và ThemeUtils để sử dụng RadioGroup cho cài đặt theme (light/dark/system)

### Cải thiện (Enhancement)
- Cải thiện chế độ tối với các icon thích ứng với màu textColorPrimary theo theme
- Tối ưu hóa tệp ngôn ngữ để tránh trùng lặp

## [1.0.0] - 2023-12-15

## [2025-03-24] - Cải thiện xử lý API không có danh mục
### Changed
- Điều chỉnh ApiClient để sử dụng API "everything" thay vì "top-headlines" cho tìm kiếm danh mục
- Cải thiện xử lý lỗi trong HomeFragment để hiển thị dữ liệu mẫu khi API không trả về kết quả
- Thêm thông báo "Đang sử dụng dữ liệu mẫu" khi không thể tải dữ liệu thực từ API

### Fixed
- Sửa lỗi "không tải được tin tức danh mục" bằng cách luôn sử dụng dữ liệu mẫu khi API không hỗ trợ danh mục
- Thay đổi logic retry để giảm số lần gọi API không cần thiết khi biết API không hỗ trợ tính năng đó

## [2025-03-23] - Thêm giới hạn tin tức và tính năng Xem thêm
### Added
- Thêm tính năng giới hạn hiển thị 10 bài viết trên danh sách đề xuất
- Thêm nút "Xem thêm" ở cuối danh sách để tải thêm 10 bài tiếp theo
- Thêm tệp layout mới item_load_more.xml để hiển thị nút Xem thêm
- Thêm drawable rounded_button.xml để tạo nút có viền bo tròn

### Changed
- Cập nhật NewsAdapter để hỗ trợ hiển thị có giới hạn và chức năng tải thêm
- Điều chỉnh HomeFragment để xử lý sự kiện nút "Xem tất cả" trong phần đề xuất
- Tăng số lượng dữ liệu mẫu từ 10 lên 25 mục để kiểm thử tính năng tải thêm

## [2025-03-22] - Giới hạn số lượng slideshow trong Breaking News
### Changed
- Giới hạn số lượng bài viết trong slideshow Breaking News còn tối đa 5 bài mới nhất
- Cập nhật logic trong HomeFragment và BreakingNewsAdapter để đảm bảo giới hạn được áp dụng nhất quán
- Cải thiện dữ liệu mẫu để hiển thị thông tin thời gian dựa trên giờ thực

## [2025-03-21] - Cải thiện độ tin cậy API và xử lý lỗi
### Added
- Thêm lớp NetworkUtils để kiểm tra kết nối mạng
- Thêm cơ chế thử lại (retry) cho các API calls bị lỗi
- Thêm kiểm tra kết nối internet trước khi gọi API
- Cải thiện xử lý lỗi và hiển thị thông báo lỗi chi tiết

### Changed
- Điều chỉnh khoảng thời gian tìm kiếm tin tức từ 30 ngày xuống 7 ngày để tránh giới hạn API
- Cập nhật cách xử lý lỗi trong ApiClient và HomeFragment
- Sử dụng thời gian thực hiện tại thay vì thời gian cố định năm 2025

### Fixed
- Sửa lỗi "Error fetching Tesla articles: 426" khi gọi NewsAPI
- Sửa lỗi hiển thị "Không thể tải tin tức" bằng cách kiểm tra kết nối và retry

## [2025-03-20] - Tích hợp NewsAPI và sửa lỗi chuỗi trùng lặp
### Added
- Tích hợp NewsAPI để lấy dữ liệu tin tức thực thay vì dữ liệu giả
- Tạo cấu trúc API client với Retrofit để gọi API
- Tạo lớp ApiService, ApiClient và các model liên quan
- Cấu hình ứng dụng đọc API key từ tệp config.json
- Thêm màn hình chi tiết bài viết với WebView để xem nội dung đầy đủ
- Thêm phương thức tính toán ngày để sử dụng ngày hiện tại - 30 ngày cho API

### Changed
- Cập nhật HomeFragment để hiển thị dữ liệu từ API thay vì dữ liệu giả
- Điều chỉnh adapter để xử lý dữ liệu từ API
- Thêm xử lý lỗi khi không có kết nối internet

### Fixed
- Sửa lỗi trùng lặp chuỗi view_all trong tệp strings.xml
- Thêm cấu hình usesCleartextTraffic cho phép gọi HTTP API

## [2025-03-19] - Cải thiện hiển thị tiêu đề ứng dụng
### Changed
- Điều chỉnh vị trí và text của tiêu đề trong top bar để hiển thị rõ ràng (từ "Breaking News" sang "News App")
- Thay đổi layout topBar để cải thiện khoảng cách giữa các phần tử
- Tăng kích thước font cho tiêu đề Breaking News từ 18sp lên 20sp
- Giảm padding chung của layout từ 16dp xuống còn 12dp để có thêm không gian hiển thị
- Thêm file strings.xml cho ngôn ngữ tiếng Việt (values-vi)

## [2025-03-19] - Cải thiện hiển thị text nguồn và thời gian
### Added
- Thêm shadow cho text nguồn và thời gian để nổi bật trên nền hình ảnh
- Tăng kích thước font cho tvTime từ 12sp lên 14sp

### Changed
- Điều chỉnh cách hiển thị tvSource để ưu tiên hiển thị tên tác giả
- Tăng margin-bottom của tvSource từ 4dp lên 8dp
- Thêm font chữ đậm (bold) cho nguồn để dễ đọc hơn

## [2025-03-19] - Cải thiện hiển thị category tag
### Added
- Thêm elevation cho category tag để nổi bật hơn
- Điều chỉnh padding của category tag để hiển thị đầy đủ text

### Changed
- Thay đổi màu nền của category tag sang màu xanh đậm hơn (#0D47A1)
- Tăng kích thước padding của category tag (từ 8dp lên 12dp)
- Thêm paddingStart và paddingEnd cho ViewPager2 (16dp) để tránh cắt nội dung
- Điều chỉnh MarginPageTransformer từ 40dp xuống 20dp
- Điều chỉnh hiệu ứng scale cho card trong ViewPager2

## [2025-03-19] - Bổ sung tính năng tự động chuyển trang
### Added
- Thêm tính năng tự động chuyển trang (auto slide) cho ViewPager2 tin nổi bật
- Tự động chuyển trang mỗi 3 giây
- Dừng tự động khi fragment không hiển thị và tiếp tục khi fragment trở lại

## [2025-03-19] - Cải thiện giao diện Page Indicator
### Added
- Cập nhật chỉ báo trang (page indicator) trong ViewPager2 Breaking News
- Thay đổi kích thước và màu sắc của các chấm tròn cho trực quan hơn
- Tùy chỉnh TabLayout để hiển thị các chấm điều hướng tốt hơn

### Changed
- Thay đổi màu chấm tròn được chọn sang màu xanh dương nhạt (#1E88E5)
- Giảm kích thước chấm không được chọn xuống 6dp x 6dp
- Tinh chỉnh kích thước và khoảng cách giữa các chấm

## [2025-03-19] - Sửa lỗi package model không tồn tại
### Fixed
- Sửa lỗi "package com.example.article.model does not exist" bằng cách thay thế import từ model sang api.model
- Cập nhật SavedFragment để sử dụng cấu trúc NewsArticle từ api.model thay vì model
- Cập nhật cách gọi NewsAdapter constructor phù hợp với triển khai hiện tại

## [2025-03-19] - Sửa lỗi RecyclerView không có adapter
### Added
- Cập nhật fragment_saved.xml với RecyclerView để hiển thị bài viết đã lưu
- Thêm adapter cho SavedFragment để hiển thị danh sách bài viết đã lưu
- Thêm chức năng hiển thị thông báo khi không có bài viết đã lưu

### Fixed
- Sửa lỗi "No adapter attached; skipping layout" bằng cách thêm adapter cho tất cả RecyclerView trong ứng dụng
- Cải thiện layout cho ViewPager2 và RecyclerView

## Chú Thích
- Thay đổi mới nhất luôn ở trên cùng
- Nhóm các thay đổi liên quan trong cùng một danh mục
- Sử dụng dấu gạch đầu dòng cho mỗi mục
