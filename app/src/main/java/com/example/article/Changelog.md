# Changelog

## [Unreleased]

### Removed
- Loại bỏ cơ chế caching trong ApiClient bao gồm memory cache và disk cache
- Loại bỏ HTTP caching với cache-control headers
- Loại bỏ cơ chế prefetching dữ liệu và hình ảnh
- Loại bỏ ExecutorService trong HomeFragment
- Loại bỏ tính năng refresh dữ liệu trong background
- Loại bỏ phương thức getBreakingNewsAt trong BreakingNewsAdapter
- Đơn giản hóa cách xử lý lỗi mạng

### Changed
- Chuyển đổi từ tìm kiếm theo danh mục sang tìm kiếm theo từ khóa
- Cập nhật giao diện người dùng để ẩn bớt các thành phần Category không còn sử dụng
- Cải thiện trải nghiệm tìm kiếm trên màn hình Discover
- Cải thiện cách xử lý và hiển thị lỗi kết nối mạng với thông báo chi tiết hơn
- Tăng tốc độ tải dữ liệu bằng hệ thống cache đa tầng
- Giảm lượng yêu cầu API bằng cách tận dụng cache đã có

### Added
- Thêm phương thức `getEverything` và `getEverythingPaged` trong ApiClient để hỗ trợ tìm kiếm theo từ khóa
- Thêm trường `category` vào model NewsArticle để duy trì tương thích với mã nguồn hiện tại
- Cập nhật tài liệu Help.md với thông tin về cách xử lý khi API không hỗ trợ danh mục
- Thêm shimmer effect cho tất cả các màn hình khi đang tải dữ liệu
- Thêm Snackbar thông báo lỗi với nút "Retry" khi gặp vấn đề về kết nối mạng
- Thêm xử lý timeout và các lỗi mạng phổ biến với thông báo thân thiện
- Tăng thời gian timeout cho các request API để cải thiện trải nghiệm người dùng
- Thêm memory cache và disk cache cho dữ liệu API, giúp truy xuất nhanh hơn
- Thêm cơ chế prefetch dữ liệu trong background cho các từ khóa phổ biến
- Thêm cơ chế prefetch hình ảnh để cải thiện tốc độ hiển thị
- Thêm cơ chế refresh dữ liệu ngầm khi người dùng trở lại ứng dụng
- Thêm ExecutorService để quản lý các tác vụ background hiệu quả

### Fixed
- Sửa lỗi ứng dụng crash khi API không trả về kết quả theo danh mục
- Cải thiện thông báo lỗi khi tìm kiếm không có kết quả
- Cải thiện cách xử lý lỗi "failed to connect" với retry và fallback tự động
- Tăng độ ổn định khi xử lý các exception từ API service
- Giải quyết vấn đề mất kết nối bằng cache dự phòng
- Giảm số lượng request API không cần thiết với cache-control headers

### Optimized
- Tối ưu việc tải dữ liệu với cache đa tầng: memory cache, disk cache và HTTP cache
- Tối ưu hiển thị hình ảnh với prefetch và cache
- Tối ưu hóa trải nghiệm người dùng với giải pháp tải nhanh từ cache
- Giảm thiểu thời gian chờ khi khởi động ứng dụng lại
- Cải thiện hiệu suất khi không có kết nối internet

## [1.0.0] - 2025-07-01

### Added
- Tích hợp Facebook Shimmer để tạo hiệu ứng loading
- Tạo layout shimmer cho breaking news
- Tạo layout shimmer cho news items
- Tạo layout shimmer cho home fragment
- Tạo hiệu ứng shimmer cho dot indicators
- Implement shimmer effect trong HomeFragment
- Cập nhật các adapter để hỗ trợ hiển thị shimmer khi loading

### Changed
- Cập nhật giao diện Fragment Home với shimmer layout
- Cập nhật NewsAdapter để phân biệt giữa item thông thường và shimmer
- Giới hạn Breaking News Slider chỉ hiển thị tối đa 5 bài viết
- Thêm chức năng tải thêm 10 item khi nhấn nút "Xem tất cả"

### Fixed
- Sửa lỗi khi không có kết nối internet
- Cải thiện hiệu suất loading với Shimmer effect
- Sửa lỗi hiển thị tiếng Việt không đúng
- Cải thiện định dạng thời gian với DateUtils 