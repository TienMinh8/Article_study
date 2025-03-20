# Cấu trúc code ứng dụng News App

## Các activity và fragment

### MainActivity
- **Chức năng chính**: Activity chính, quản lý navigation và bottom navigation
- **Các phương thức chính**:
  - onCreate: Khởi tạo activity, thiết lập navigation
  - toggleDarkMode: Thay đổi chế độ sáng/tối

### HomeFragment
- **Chức năng chính**: Hiển thị trang chủ với tin nóng và đề xuất
- **Các phương thức chính**:
  - loadBreakingNews: Tải tin nóng từ API
  - loadRecommendationNews: Tải tin đề xuất từ API
  - setupViewPager: Thiết lập ViewPager cho tin nóng
  - loadMoreRecommendations: Tải thêm tin đề xuất

### DiscoverFragment
- **Chức năng chính**: Hiển thị trang khám phá với tìm kiếm và lọc theo danh mục
- **Các phương thức chính**:
  - setupCategoryChips: Tạo chip cho các danh mục
  - loadNewsByCategory: Tải tin theo danh mục
  - setupSearch: Thiết lập tìm kiếm

### SavedFragment
- **Chức năng chính**: Hiển thị các bài viết đã lưu
- **Các phương thức chính**:
  - loadSavedArticles: Tải bài viết đã lưu từ cơ sở dữ liệu

### ProfileFragment
- **Chức năng chính**: Hiển thị và quản lý cài đặt người dùng
- **Các phương thức chính**:
  - setupListeners: Thiết lập listener cho các tùy chọn
  - loadCurrentSettings: Tải cài đặt hiện tại từ SharedPreferences

## Models
### Article
- **Chức năng**: Mô hình dữ liệu cho bài viết
- **Trường dữ liệu**: title, description, url, urlToImage, publishedAt, source, content, author, category

### Source
- **Chức năng**: Mô hình dữ liệu cho nguồn tin
- **Trường dữ liệu**: id, name

## Adapters
### NewsAdapter
- **Chức năng**: Adapter cho RecyclerView hiển thị danh sách bài viết
- **Phương thức chính**: onBindViewHolder, setNewsList

### BreakingNewsAdapter
- **Chức năng**: Adapter cho ViewPager hiển thị tin nóng
- **Phương thức chính**: onBindViewHolder, setBreakingNewsList, getBreakingNewsAt

## Network
### ApiClient
- **Chức năng**: Quản lý kết nối API và cache
- **Phương thức chính**: 
  - getArticles: Lấy bài viết từ API
  - searchArticles: Tìm kiếm bài viết
  - getTopHeadlines: Lấy tin nóng

### ApiResponse
- **Chức năng**: Mô hình dữ liệu cho phản hồi từ API
- **Trường dữ liệu**: status, totalResults, articles

## Utils

### ThemeUtils (Quản lý theme và tùy chỉnh hiển thị)
- **Chức năng chính**: Quản lý chế độ theme (sáng/tối), kích thước chữ, và tương phản
- **Các hằng số**:
  - MODE_SYSTEM, MODE_LIGHT, MODE_DARK: Các chế độ theme
  - FONT_SIZE_SMALL, FONT_SIZE_MEDIUM, FONT_SIZE_LARGE: Các kích thước chữ
- **Các phương thức chính**:
  - applyAppTheme: Áp dụng theme dựa trên cài đặt đã lưu
  - saveThemeMode, getThemeMode: Lưu và lấy chế độ theme
  - isNightMode: Kiểm tra xem thiết bị có đang ở chế độ tối hay không
  - saveFontSize, getFontSize: Lưu và lấy kích thước chữ
  - saveHighContrastMode, getHighContrastMode: Lưu và lấy chế độ tương phản cao
  - applyAdjustedTextSize: Áp dụng kích thước chữ cho TextView

### FontSizeUtils
- **Chức năng chính**: Hỗ trợ điều chỉnh kích thước chữ
- **Phương thức chính**: 
  - getScaledFontSize: Tính toán kích thước chữ theo tỷ lệ
  - applyFontSize: Áp dụng kích thước chữ cho TextView

### DateUtils
- **Chức năng chính**: Xử lý và định dạng ngày tháng
- **Phương thức chính**: 
  - formatDate: Định dạng ngày tháng theo định dạng mong muốn
  - getTimeAgo: Tính toán thời gian trôi qua (ví dụ: "2 giờ trước")
