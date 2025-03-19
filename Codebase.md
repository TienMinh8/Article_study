# Cấu trúc Codebase Article Study App

## Cấu trúc gói (Package Structure)

### com.example.article
- **MainActivity**: Quản lý điều hướng chính và cài đặt BottomNavigationView

### com.example.article.api.model
- **NewsArticle**: Lưu trữ thông tin về một bài báo (tiêu đề, mô tả, url, nguồn, ngày, hình ảnh)
  - `getTitle()`, `setTitle()`: Quản lý tiêu đề bài viết
  - `getDescription()`, `setDescription()`: Quản lý mô tả bài viết
  - `getContent()`, `setContent()`: Quản lý nội dung bài viết
  - `getAuthor()`, `setAuthor()`: Quản lý tác giả 
  - `getUrl()`, `setUrl()`: Quản lý URL đến bài viết gốc
  - `getUrlToImage()`, `setUrlToImage()`: Quản lý URL đến hình ảnh
  - `getPublishedAt()`, `setPublishedAt()`: Quản lý thời gian xuất bản
  - `getSource()`, `setSource()`: Quản lý nguồn bài viết
  - **Source**: Lớp con chứa thông tin về nguồn bài viết (id, name)

### com.example.article.adapter
- **NewsAdapter**: Adapter cho RecyclerView hiển thị danh sách bài viết thông thường với tính năng tải thêm
  - `onCreateViewHolder`: Tạo view holder dựa trên loại view (bài viết hoặc nút "Xem thêm")
  - `onBindViewHolder`: Gắn dữ liệu vào view và thiết lập click listener
  - `getItemViewType`: Xác định loại item (TYPE_ITEM hoặc TYPE_LOAD_MORE)
  - `setNewsList`: Cập nhật danh sách đầy đủ và danh sách hiển thị
  - `updateVisibleItems`: Cập nhật danh sách hiển thị dựa trên giới hạn
  - `loadMoreItems`: Thêm 10 bài viết tiếp theo vào danh sách hiển thị
  - `NewsViewHolder`: Lưu trữ các view trong layout item bài viết
  - `LoadMoreViewHolder`: Lưu trữ nút "Xem thêm" và xử lý sự kiện click
  - `OnNewsClickListener`: Interface để xử lý sự kiện click vào bài viết
  
- **BreakingNewsAdapter**: Adapter cho ViewPager2 hiển thị tin tức nổi bật
  - `onCreateViewHolder`: Tạo view holder từ layout item_breaking_news
  - `onBindViewHolder`: Gắn dữ liệu vào view và thiết lập click listener
  - `BreakingNewsViewHolder`: Lưu trữ các view trong layout item
  - `OnBreakingNewsClickListener`: Interface để xử lý sự kiện click

### com.example.article.ui.home
- **HomeFragment**: Hiển thị trang chủ với tin nổi bật và danh sách tin mới nhất
  - `initViewPager`: Cài đặt ViewPager2 với BreakingNewsAdapter
  - `initRecyclerView`: Cài đặt RecyclerView với NewsAdapter
  - `loadBreakingNews`: Tải dữ liệu mẫu cho tin nổi bật
  - `loadLatestNews`: Tải dữ liệu mẫu cho tin mới nhất

### com.example.article.ui.discover
- **DiscoverFragment**: Hiển thị trang khám phá với bộ lọc danh mục
  - `initCategoryChips`: Cài đặt ChipGroup cho việc lọc danh mục
  - `initRecyclerView`: Cài đặt RecyclerView với NewsAdapter
  - `loadNewsByCategory`: Tải dữ liệu mẫu dựa trên danh mục đã chọn

### com.example.article.ui.saved
- **SavedFragment**: Hiển thị danh sách bài viết đã lưu
  - `initViews`: Khởi tạo các thành phần UI
  - `setupRecyclerView`: Cài đặt RecyclerView với NewsAdapter
  - `loadSavedArticles`: Tải dữ liệu mẫu bài viết đã lưu
  - `updateEmptyView`: Hiển thị trạng thái trống khi không có bài viết

### com.example.article.ui.profile
- **ProfileFragment**: Hiển thị trang thông tin người dùng (đang phát triển)

### com.example.article.ui.transform
- **MarginPageTransformer**: Tạo hiệu ứng chuyển trang với margin giữa các trang
  - `transformPage`: Xử lý biến đổi view dựa trên vị trí trang

## Thư mục Layout

### layout/
- **activity_main.xml**: Layout chính với BottomNavigationView và NavHostFragment
- **fragment_home.xml**: Layout cho HomeFragment với ViewPager2 và RecyclerView
- **fragment_discover.xml**: Layout cho DiscoverFragment với ChipGroup và RecyclerView
- **fragment_saved.xml**: Layout cho SavedFragment với RecyclerView và thông báo trống
- **fragment_profile.xml**: Layout cho ProfileFragment (đang phát triển)
- **item_news.xml**: Layout cho mỗi item trong RecyclerView tin tức thông thường
- **item_breaking_news.xml**: Layout cho mỗi item trong ViewPager2 tin nổi bật
- **item_load_more.xml**: Layout cho nút "Xem thêm" ở cuối danh sách bài viết

## Thư mục Drawable

### drawable/
- **placeholder_image.xml**: Hình ảnh placeholder được sử dụng khi ảnh bài viết chưa tải
- **error_image.xml**: Hình ảnh lỗi được hiển thị khi không thể tải ảnh bài viết
- **rounded_button.xml**: Định nghĩa hình dạng nút có viền bo tròn cho nút "Xem thêm"

## Navigation

### navigation/
- **mobile_navigation.xml**: Định nghĩa điều hướng giữa các fragment

## API Module
- **ApiService** (Interface): Định nghĩa các API endpoints sử dụng Retrofit
  - getEverything: Lấy tin tức từ tất cả nguồn với query, date và sort
  - getTopHeadlines: Lấy tin tức nổi bật theo quốc gia và danh mục
  - getCustomUrl: Cho phép gọi một URL tùy chỉnh

- **ApiClient** (Singleton): Quản lý việc tạo và sử dụng API service
  - getInstance: Lấy instance của ApiClient (singleton pattern)
  - getTeslaNews: Lấy tin tức về Tesla
  - getNewsByCategory: Lấy tin tức theo danh mục, sử dụng API "everything" thay vì "top-headlines"
  - getDateBefore: Tính toán ngày trước đây X ngày
  - Xử lý thông minh khi API không có kết quả hoặc không hỗ trợ tính năng

- **ConfigUtils**: Đọc cấu hình API từ file config.json
  - getConfigJson: Đọc và parse JSON file
  - loadConfigFile: Tải file từ assets
  - readFileFromAssets: Đọc file text từ assets

## API Model
- **NewsResponse**: Model cho response từ NewsAPI
  - status: Trạng thái response (ok/error)
  - totalResults: Tổng số kết quả
  - articles: Danh sách bài viết

- **NewsArticle**: Model cho một bài viết tin tức
  - source: Nguồn của bài viết
  - author: Tác giả
  - title: Tiêu đề
  - description: Mô tả ngắn
  - url: URL đến bài viết đầy đủ
  - urlToImage: URL đến hình ảnh
  - publishedAt: Thời gian xuất bản
  - content: Nội dung (cắt ngắn)

## UI Module
- **HomeFragment**: Hiển thị trang chủ với tin nóng và tin đề xuất
  - loadDataFromApi: Tải dữ liệu từ API
  - setupAutoSlide: Cấu hình tự động trượt cho tin nóng
  - onNewsClick/onBreakingNewsClick: Xử lý click vào bài viết

- **DetailFragment**: Hiển thị chi tiết bài viết trong WebView
  - setupWebView: Cấu hình WebView
  - loadArticle: Tải bài viết từ URL
  - Xử lý trạng thái loading và error

## Adapter
- **BreakingNewsAdapter**: Adapter cho ViewPager2 hiển thị tin nóng
  - `setBreakingNewsList`: Cập nhật danh sách tin nóng và giới hạn tối đa 5 item
  - `setDummyData`: Tạo dữ liệu mẫu với 5 item khi không có internet
  - Hiển thị thông tin thời gian dựa trên thời gian thực

- **NewsAdapter**: Adapter cho RecyclerView hiển thị tin đề xuất
  - `setNewsList`: Cập nhật danh sách đầy đủ (fullNewsList) và danh sách hiển thị (newsList)
  - `updateVisibleItems`: Tự động giới hạn hiển thị 10 bài đầu tiên nếu có nhiều hơn
  - `loadMoreItems`: Tải thêm 10 bài tiếp theo khi người dùng nhấn "Xem thêm"
  - `setDummyData`: Tạo dữ liệu mẫu với 25 mục để kiểm thử tính năng tải thêm
  - `getItemViewType`: Phân biệt giữa item bài viết và nút "Xem thêm"
  - `NewsViewHolder`: Hiển thị thông tin bài viết
  - `LoadMoreViewHolder`: Hiển thị và xử lý nút "Xem thêm"

## Utilities
- **NetworkUtils**: Lớp tiện ích kiểm tra kết nối mạng
  - `isNetworkAvailable(Context)`: Kiểm tra thiết bị có kết nối internet không

## API
- **ApiClient**: Lớp xử lý gọi API từ NewsAPI.org
  - `getTeslaNews(int retryCount)`: Lấy tin tức về Tesla với cơ chế retry
  - `getNewsByCategory(String category, int retryCount)`: Lấy tin tức theo danh mục bằng cách tìm kiếm với chuỗi danh mục
  - Điều chỉnh khoảng thời gian tìm kiếm từ 30 ngày xuống 7 ngày để phù hợp với giới hạn API
  - Cải thiện xử lý danh sách trống khi API không trả về kết quả

## Fragments
- **HomeFragment**: Fragment hiển thị tin tức nổi bật và tin tức theo danh mục
  - `loadDataFromApi()`: Kiểm tra kết nối mạng trước khi gọi API
  - `loadTeslaNews(int retryCount)`: Gọi API lấy tin Tesla với cơ chế retry
  - `loadCategoryNews(String category, int retryCount)`: Gọi API lấy tin theo danh mục, tự động sử dụng dữ liệu mẫu khi API trả về danh sách trống
  - Giới hạn hiển thị tối đa 5 bài viết trong slider breaking news
  - Xử lý sự kiện "Xem tất cả" trong phần đề xuất để tải thêm bài viết
  - Xử lý thông báo lỗi khi không có mạng hoặc API lỗi
  - Tải dữ liệu mẫu khi không thể tải dữ liệu từ API

## Adapters
- **NewsAdapter**: Adapter cho RecyclerView hiển thị danh sách bài viết
  - Quản lý hai danh sách: danh sách đầy đủ và danh sách hiển thị có giới hạn
  - Hỗ trợ hai loại ViewHolder: bài viết thông thường và nút "Xem thêm"
  - Tự động giới hạn hiển thị tối đa 10 bài đầu tiên
  - Tải thêm 10 bài viết mỗi khi người dùng nhấn "Xem thêm"
  - Tự động ẩn nút "Xem thêm" khi đã hiển thị hết tất cả bài viết

## Transformers
- **MarginPageTransformer**: Áp dụng hiệu ứng chuyển đổi cho các trang trong ViewPager2
  - `transformPage(View, float)`: Điều chỉnh tỷ lệ và vị trí của trang khi vuốt

## Cơ Chế Xử Lý Lỗi
- **Kiểm tra kết nối**: Sử dụng NetworkUtils trước khi gọi API
- **Retry mechanism**: Tự động thử lại khi API call thất bại (tối đa 2 lần)
- **Fallback data**: Sử dụng dữ liệu mẫu khi không thể lấy dữ liệu từ API
- **Thông báo người dùng**: Hiển thị Toast thông báo khi không có kết nối hoặc API lỗi

## Tính năng tải có giới hạn (Load Limited)
- **Giới hạn ban đầu**: Chỉ hiển thị 10 bài viết đầu tiên để tối ưu hiệu suất ban đầu
- **Tải theo yêu cầu**: Chỉ tải thêm bài viết khi cần thiết (khi người dùng nhấn "Xem thêm")
- **Hiệu suất tối ưu**: Giảm sử dụng bộ nhớ và tài nguyên hệ thống bằng cách không tải tất cả cùng lúc
- **Hiển thị thông minh**: Tự động quản lý nút "Xem thêm" dựa trên trạng thái dữ liệu
- **Quản lý dữ liệu hiệu quả**: Duy trì danh sách đầy đủ và danh sách hiển thị riêng biệt
