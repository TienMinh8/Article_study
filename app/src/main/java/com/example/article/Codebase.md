## API Module

### ApiClient
- **Chức năng chính**: Quản lý kết nối API, gửi yêu cầu và xử lý phản hồi từ News API
- `getInstance(Context)`: Tạo hoặc trả về instance hiện có (singleton pattern)
- `getTeslaNews(ApiCallback)`: Lấy tin tức về Tesla
- `getEverything(String, ApiCallback)`: Tìm kiếm tin tức theo từ khóa
- `getEverything(String, int, ApiCallback)`: Tìm kiếm tin tức theo từ khóa với phân trang
- `getTopHeadlines(String, ApiCallback)`: Lấy tin tức hàng đầu theo danh mục

### ApiService
- **Chức năng chính**: Định nghĩa các endpoint API sử dụng Retrofit
- `getEverything(...)`: Endpoint tìm kiếm tin tức
- `getEverythingPaged(...)`: Endpoint tìm kiếm tin tức có phân trang
- `getTopHeadlines(...)`: Endpoint lấy tin tức hàng đầu
- `getTopHeadlinesByCategory(...)`: Endpoint lấy tin tức hàng đầu theo danh mục cụ thể
- `getCustomUrl(...)`: Endpoint tùy chỉnh URL 

## UI Module

### DiscoverFragment
- **Chức năng chính**: Hiển thị và tìm kiếm tin tức theo từ khóa hoặc danh mục
- `setupCategoryChips()`: Tạo và cấu hình chip cho các danh mục tin tức
- `loadNewsWithQuery()`: Tải tin tức theo từ khóa tìm kiếm (hiển thị giới hạn 10 item)
- `loadNewsByCategory()`: Tải tin tức theo danh mục đã chọn (hiển thị giới hạn 10 item)
- `setupSwipeRefresh()`: Thiết lập tính năng kéo để làm mới
- `refreshData()`: Thực hiện làm mới dữ liệu dựa trên danh mục hoặc từ khóa hiện tại
- `loadMoreNews()`: Ưu tiên tải thêm từ bộ nhớ đệm trước khi gọi API
- `loadMoreFromApi()`: Tải thêm tin tức từ API khi đã hết dữ liệu trong bộ nhớ đệm
- `filterDuplicateItems()`: Lọc các bài viết trùng lặp khi tải thêm
- `setupLoadMoreButton()`: Khởi tạo và cấu hình nút tải thêm

### HomeFragment
- **Chức năng chính**: Hiển thị trang chính với tin nóng và tin đề xuất
- `setupAutoSlide()`: Thiết lập tự động chuyển slide cho tin nóng
- `loadTeslaNews()`: Tải tin nóng từ API với chủ đề Tesla
- `loadRecommendationNews()`: Tải tin đề xuất từ API (giới hạn hiển thị 10 bài đầu tiên)
- `setupSwipeRefresh()`: Thiết lập tính năng kéo để làm mới
- `loadMoreRecommendations()`: Tải thêm tin tức đề xuất từ bộ nhớ đệm hoặc API
- `loadMoreFromApi()`: Tải thêm tin tức đề xuất trực tiếp từ API
- `filterDuplicateItems()`: Lọc các bài viết trùng lặp khi tải thêm
- `initViews()`: Khởi tạo các thành phần giao diện người dùng và thiết lập sự kiện

## Adapter Module

### NewsAdapter
- **Chức năng chính**: Hiển thị danh sách bài viết tin tức trong RecyclerView
- `setNewsList()`: Cập nhật danh sách tin tức mới
- `setDummyData()`: Tạo và hiển thị dữ liệu mẫu khi không có dữ liệu thật
- `getCurrentNewsList()`: Trả về danh sách tin tức hiện tại
- `addMoreItems()`: Thêm các bài viết mới vào danh sách hiện tại
- `formatPublishedDate()`: Định dạng ngày đăng bài phù hợp
- `interface OnNewsClickListener`: Xử lý sự kiện click vào tin tức

### BreakingNewsAdapter
- **Chức năng chính**: Quản lý hiển thị các bài viết trong ViewPager2 của phần tin nóng
- `setBreakingNewsList()`: Cập nhật danh sách tin nóng (giới hạn tối đa 5 bài)
- `getBreakingNewsAt()`: Lấy tin tức tại vị trí cụ thể
- `interface OnBreakingNewsClickListener`: Xử lý sự kiện click vào tin nóng 