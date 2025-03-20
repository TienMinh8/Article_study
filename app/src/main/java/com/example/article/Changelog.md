# Changelog

## [Unreleased] - yyyy-mm-dd

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
- Giới hạn số lượng tin tức đề xuất (Recommendation) hiển thị tối đa 10 item
- Cập nhật dữ liệu mẫu hiển thị đúng 10 item trong phần đề xuất
- Cải thiện giao diện item_news.xml với padding và corner radius phù hợp 
- Tăng độ rõ ràng của thông tin nguồn và thời gian đăng trong mỗi bài viết
- Thêm hiệu ứng ripple khi nhấn vào các thành phần tương tác
- Cải thiện logic hiển thị và ẩn shimmer loading
- Cập nhật phương pháp tải dữ liệu theo danh mục trên màn hình Discover
- Tối ưu hóa việc theo dõi và làm mới dữ liệu trong quá trình sử dụng
- Chỉnh sửa DiscoverFragment để giới hạn hiển thị 10 bài viết đầu tiên và thêm nút "Xem thêm" có khả năng tải 10 bài tiếp theo
- Cải thiện hiệu suất tải thêm tin tức trong DiscoverFragment bằng cách sử dụng bộ nhớ đệm trực tiếp từ kết quả trước đó
- Hiệu chỉnh nút "Xem thêm" trong DiscoverFragment để hiển thị trạng thái phù hợp (đang tải, không còn kết quả)
- Tối ưu hóa logic tải nội dung theo danh mục để cũng áp dụng giới hạn 10 item
- Cập nhật HomeFragment để cũng giới hạn hiển thị 10 bài viết đề xuất ban đầu
- Cải thiện chức năng "Xem tất cả" trong HomeFragment để hiển thị tất cả các bài viết có sẵn
- Tăng hiệu suất tải thêm bài viết trong HomeFragment bằng cách ưu tiên sử dụng bộ nhớ đệm

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
- Thêm nút "Xem thêm" dưới RecyclerView để tải thêm 10 bài viết khi bấm
- Thêm phương thức getCurrentNewsList() và addMoreItems() trong NewsAdapter
- Thêm chức năng lọc trùng lặp khi tải thêm bài viết mới
- Bố cục tin tức dạng thẻ với khoảng cách 12dp giữa các thẻ
- Lớp ItemSpacingDecoration để quản lý khoảng cách giữa các item trong RecyclerView
- ChipGroup cho bộ lọc danh mục tin tức trong màn hình Khám phá
- Thêm nút chia sẻ và lưu bài viết trong các item tin tức
- Tính năng kéo để làm mới (Pull-to-refresh) trên cả màn hình Home và Discover
- SwipeRefreshLayout được tích hợp vào giao diện người dùng
- Hỗ trợ làm mới dữ liệu theo danh mục và từ khóa tìm kiếm
- Hiệu ứng tải với màu sắc tùy chỉnh theo giao diện ứng dụng
- Thêm nút "Xem thêm" vào màn hình Khám phá để tải thêm bài viết
- Thêm tính năng phân trang khi tìm kiếm và xem bài viết trong màn hình Khám phá
- Cơ chế lọc bài viết trùng lặp khi tải thêm để tránh hiển thị nội dung đã có
- Theo dõi trạng thái tải với các thông báo phù hợp ("Đang tải thêm...", "Không còn kết quả")
- Thêm tính năng bộ nhớ đệm trong HomeFragment để lưu trữ danh sách đầy đủ và tối ưu hóa tải thêm
- Cải thiện trải nghiệm người dùng với thông báo số lượng bài viết được tải

### Fixed
- Sửa lỗi ứng dụng crash khi API không trả về kết quả theo danh mục
- Cải thiện thông báo lỗi khi tìm kiếm không có kết quả
- Cải thiện cách xử lý lỗi "failed to connect" với retry và fallback tự động
- Tăng độ ổn định khi xử lý các exception từ API service
- Giải quyết vấn đề mất kết nối bằng cache dự phòng
- Giảm số lượng request API không cần thiết với cache-control headers
- Thêm phương thức `getTopHeadlinesByCategory` vào ApiService để lấy tin tức theo danh mục
- Sửa lỗi thiếu phương thức `getTopHeadlines` trong ApiClient
- Cải thiện cách gọi API cho danh mục tin tức sử dụng Retrofit thay vì OkHttp trực tiếp

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