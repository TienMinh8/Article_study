# Article Study App - Tài liệu người dùng

## Các tính năng chính

### Trang chủ (Home)
- **Breaking News Slider**: Hiển thị 5 tin tức hot nhất trong slider với hiệu ứng trượt đẹp mắt.
  - Giới hạn hiển thị tối đa 5 tin tức mới nhất để tối ưu trải nghiệm
  - Tự động chuyển trang mỗi 3 giây
  - Nhấn vào bài viết để đọc chi tiết
- **Danh sách tin tức đề xuất**: Hiển thị các bài viết mới nhất dưới dạng danh sách có thể cuộn.
  - Ban đầu chỉ hiển thị 10 bài viết đầu tiên để tối ưu hiệu suất
  - Nút "Xem thêm" ở cuối danh sách cho phép tải thêm 10 bài tiếp theo
  - Nhấn vào bài viết để đọc chi tiết
  - Tự động ẩn nút "Xem thêm" khi đã hiển thị tất cả bài viết
- **Page Indicator (Chỉ báo trang)**: Hiển thị vị trí trang hiện tại trong slider tin nổi bật bằng dạng chấm tròn
  - Chấm màu xanh: Trang hiện tại đang xem
  - Chấm màu xám: Các trang khác có thể vuốt để xem
  - Số lượng chấm tương ứng với số tin nổi bật có sẵn
  - Vuốt trái/phải để chuyển qua lại giữa các tin, chấm indicator sẽ tự động cập nhật
- **Auto Slide (Tự động chuyển trang)**: Các tin nổi bật sẽ tự động chuyển trang
  - Tự động chuyển trang mỗi 3 giây
  - Tính năng tạm dừng khi bạn rời khỏi màn hình và tiếp tục khi quay lại
  - Bạn vẫn có thể vuốt qua lại để xem tin thủ công

### Khám phá (Discover)
- **Bộ lọc danh mục**: Sử dụng Chip Group để lọc bài viết theo danh mục (Tất cả, Công nghệ, Sức khỏe, Thể thao, v.v).
- **Danh sách bài viết theo danh mục**: Hiển thị các bài viết liên quan đến danh mục đã chọn.

### Bài viết đã lưu (Saved)
- **Danh sách bài viết đã lưu**: Hiển thị tất cả bài viết bạn đã lưu để đọc sau.
- **Trạng thái trống**: Hiển thị thông báo khi chưa có bài viết nào được lưu.

## Hướng dẫn sử dụng

### Cách điều hướng
- Sử dụng thanh điều hướng bên dưới để chuyển giữa các màn hình chính: Home, Discover, Saved và Profile.
- Vuốt trái/phải trên slider tin nóng để xem các tin khác nhau.
- Cuộn danh sách tin tức để xem thêm bài viết.
- Nhấn "Xem thêm" ở cuối danh sách đề xuất để tải thêm bài viết.

### Cách xem thêm bài viết
1. Cuộn xuống cuối danh sách bài viết đề xuất
2. Nhấn vào nút "Xem thêm" (hoặc "Load More" trong phiên bản tiếng Anh) 
3. Thêm 10 bài viết mới sẽ được tải và hiển thị
4. Tiếp tục nhấn "Xem thêm" cho đến khi tất cả bài viết đã được hiển thị
5. Nút "Xem thêm" sẽ tự động biến mất khi đã hiển thị toàn bộ bài viết

### Cách lọc bài viết
1. Chuyển đến tab "Discover"
2. Chọn một danh mục từ các chip ở phía trên
3. Danh sách bài viết sẽ tự động cập nhật hiển thị các bài viết trong danh mục đã chọn

### Cách lưu bài viết
- Tính năng lưu bài viết sẽ được bổ sung trong phiên bản tiếp theo.

## Giải thích kỹ thuật

### RecyclerView
RecyclerView được sử dụng để hiển thị danh sách bài viết một cách hiệu quả. Nó chỉ tạo và giữ View cho các mục nhìn thấy và tái sử dụng chúng khi người dùng cuộn, giúp tiết kiệm bộ nhớ và tối ưu hiệu suất.

**Thành phần chính:**
- **Adapter**: Kết nối dữ liệu với RecyclerView và tạo ViewHolder
- **ViewHolder**: Lưu trữ tham chiếu đến các view trong mỗi item
- **LayoutManager**: Quản lý vị trí của các item trong RecyclerView

### ViewPager2
ViewPager2 được sử dụng cho slider tin nóng, cho phép người dùng vuốt qua lại giữa các trang. Nó cung cấp hiệu ứng chuyển trang mượt mà và hỗ trợ phương hướng dọc/ngang.

**Đặc điểm:**
- **MarginPageTransformer**: Tạo khoảng cách giữa các trang và hiệu ứng thu nhỏ trang không được chọn
- **OffscreenPageLimit**: Kiểm soát số lượng trang được tải trước khi hiển thị
- **ClipToPadding & ClipChildren**: Cho phép hiển thị một phần của trang kề để tạo hiệu ứng peek

### Tính năng Load More (Xem thêm)
Tính năng "Xem thêm" giúp tối ưu hiệu suất ứng dụng bằng cách chỉ tải và hiển thị một số lượng bài viết vừa đủ, thêm bài viết khi cần thiết.

**Cách hoạt động:**
- Ban đầu chỉ hiển thị 10 bài viết đầu tiên từ danh sách
- Adapter quản lý 2 danh sách: danh sách đầy đủ (fullNewsList) và danh sách đang hiển thị (newsList)
- Khi người dùng nhấn "Xem thêm", thêm 10 bài viết tiếp theo sẽ được thêm vào danh sách hiển thị
- Nếu đã hiển thị tất cả bài viết, nút "Xem thêm" sẽ tự động biến mất
- Adapter sử dụng 2 loại ViewHolder khác nhau: một cho bài viết thông thường và một cho nút "Xem thêm"

### Các adapter
- **NewsAdapter**: Hiển thị danh sách tin tức thông thường với tính năng "Xem thêm"
- **BreakingNewsAdapter**: Hiển thị tin nóng trong ViewPager2

### Mô hình dữ liệu
- **NewsArticle**: Lớp chính để lưu trữ thông tin bài viết tin tức
  - `title`: Tiêu đề bài viết
  - `description`: Mô tả ngắn gọn
  - `content`: Nội dung đầy đủ
  - `author`: Tác giả
  - `url`: Đường dẫn đến bài viết gốc
  - `urlToImage`: Đường dẫn đến hình ảnh bài viết
  - `publishedAt`: Thời gian xuất bản
  - `source`: Nguồn của bài viết (bao gồm id và name)

### Breaking News Layout - Cập nhật
- **Category Tags**: Thẻ danh mục (Technology, Sports, v.v.) đã được cải thiện hiển thị
  - Thẻ danh mục được thiết kế nổi bật với màu xanh đậm
  - Padding lớn hơn để hiển thị đầy đủ text
  - Có thêm shadow để nổi bật trên mọi hình nền
  - Font chữ đậm (bold) cho dễ đọc
- **Thông tin Nguồn và Thời gian**: Phần hiển thị nguồn và thời gian đã được cải thiện
  - Hiển thị tên tác giả (nếu có) hoặc tên nguồn
  - Text được thêm đổ bóng (shadow) để dễ đọc trên mọi nền hình ảnh
  - Kích thước text phù hợp (14sp) và font chữ đậm cho phần nguồn
  - Khoảng cách phù hợp (8dp) với tiêu đề bài viết

### Hỗ trợ đa ngôn ngữ
Ứng dụng hiện hỗ trợ các ngôn ngữ sau:
- **Tiếng Anh**: Ngôn ngữ mặc định
- **Tiếng Việt**: Bản địa hóa đầy đủ cho tất cả văn bản giao diện

Ngôn ngữ hiển thị sẽ tự động theo cài đặt hệ thống của thiết bị. Nếu thiết bị cài đặt tiếng Việt, toàn bộ giao diện sẽ hiển thị bằng tiếng Việt.

# Hướng dẫn sử dụng News App

## Tính năng API và Xem chi tiết bài viết
### Tổng quan
News App hiện kết nối với NewsAPI.org để hiển thị các tin tức thực từ Internet. Ứng dụng sẽ tự động tải tin tức mới nhất về Tesla cho phần Tin nóng và tin tức công nghệ cho phần Đề xuất.

### Cách xem chi tiết bài viết
1. Nhấn vào bất kỳ bài viết nào trong phần Tin nóng hoặc Đề xuất
2. Ứng dụng sẽ mở màn hình chi tiết với nội dung đầy đủ của bài viết
3. Bạn có thể cuộn để đọc toàn bộ nội dung và phóng to bằng cử chỉ zoom
4. Nhấn nút back trên thanh công cụ để quay lại màn hình chính

### Xem tin tức khi không có kết nối mạng
- Khi không có kết nối mạng, ứng dụng sẽ hiển thị dữ liệu mẫu
- Nếu bạn đang ở màn hình chi tiết và mất kết nối, ứng dụng sẽ hiển thị thông báo "Không có kết nối internet"
- Kết nối lại mạng và tải lại trang để tiếp tục đọc

## Ngôn ngữ
- Ứng dụng hỗ trợ hai ngôn ngữ: tiếng Anh và tiếng Việt
- Ngôn ngữ hiển thị sẽ tự động theo cài đặt của thiết bị
- Không cần thao tác gì thêm để chuyển đổi ngôn ngữ

## Trang chủ
1. **Tin nóng** - Hiển thị tin tức mới nhất về Tesla dưới dạng slideshow
   - Tin tức sẽ tự động trượt mỗi 3 giây
   - Nhấn vào để xem chi tiết
   - Có thể vuốt trái/phải để xem tin tiếp theo/trước đó

2. **Đề xuất cho bạn** - Hiển thị tin tức công nghệ mới nhất
   - Ban đầu hiển thị 10 bài viết đầu tiên để tối ưu hiệu suất
   - Nhấn "Xem thêm" để tải thêm 10 bài tiếp theo
   - Nhấn vào một bài viết để xem chi tiết
   - Nút "Xem thêm" tự động biến mất khi đã hiển thị tất cả bài viết

## Cài đặt và cấu hình
API key và cấu hình của ứng dụng được lưu trong file config.json. Không cần phải thay đổi nếu chỉ sử dụng ứng dụng bình thường.

# Tài Liệu Trợ Giúp

## Tính Năng Đa Ngôn Ngữ
Ứng dụng News App hỗ trợ đa ngôn ngữ, bao gồm:
- Tiếng Anh (mặc định)
- Tiếng Việt

Ngôn ngữ sẽ tự động hiển thị dựa trên cài đặt của thiết bị.

## Kiểm Tra Kết Nối Mạng và Cơ Chế Retry
Ứng dụng có tích hợp các tính năng để đảm bảo trải nghiệm người dùng tốt nhất, ngay cả khi có vấn đề về kết nối Internet:

### 1. Kiểm Tra Kết Nối Mạng
- **Lớp NetworkUtils**: Cung cấp phương thức `isNetworkAvailable()` để kiểm tra kết nối Internet
- **Cách sử dụng**: Trước khi gọi API, ứng dụng sẽ kiểm tra kết nối
- **Xử lý khi không có mạng**: Hiển thị thông báo và tải dữ liệu mẫu để người dùng vẫn có thể xem ứng dụng

### 2. Xử lý API không có danh mục
- **Phát hiện API hạn chế**: Tự động phát hiện khi API không hỗ trợ tìm kiếm theo danh mục
- **Tìm kiếm thay thế**: Sử dụng API "everything" với từ khóa danh mục thay vì "top-headlines"
- **Dữ liệu mẫu thông minh**: Tự động sử dụng dữ liệu mẫu khi không tìm thấy kết quả từ API
- **Thông báo tinh tế**: Hiển thị thông báo nhỏ "Đang sử dụng dữ liệu mẫu" thay vì thông báo lỗi

### 3. Cơ Chế Retry Tự Động
- **Mục đích**: Tăng tỷ lệ thành công khi gọi API
- **Số lần thử lại**: Tối đa 2 lần cho API được hỗ trợ
- **Tối ưu hóa retry**: Không thử lại với API đã biết không được hỗ trợ
- **Độ trễ giữa các lần thử**: 1 giây
- **Hoạt động**: Nếu lần gọi API đầu tiên thất bại, ứng dụng sẽ tự động thử lại sau 1 giây (cho API có hỗ trợ)

### 4. Điều Chỉnh Thời Gian Tìm Kiếm
- Thay đổi khoảng thời gian tìm kiếm tin tức từ 30 ngày xuống còn 7 ngày để phù hợp với giới hạn của API miễn phí
- Sử dụng thời gian thực hiện tại thay vì ngày cố định

### 5. Xử Lý Lỗi
- Hiển thị thông báo lỗi cụ thể
- Log chi tiết để dễ dàng gỡ lỗi
- Tải dữ liệu mẫu như là giải pháp dự phòng khi không thể tải dữ liệu từ API

## Tính Năng Tải Thêm Bài Viết (Load More)
Tính năng này giúp cải thiện hiệu suất và trải nghiệm người dùng bằng cách:

- **Hiển thị ban đầu**: Chỉ hiển thị 10 bài viết đầu tiên, giảm thời gian tải ban đầu
- **Tải theo yêu cầu**: Chỉ tải thêm bài viết khi người dùng yêu cầu (bằng cách nhấn nút "Xem thêm")
- **Hiển thị tối ưu**: Thêm 10 bài viết mỗi lần để không tạo quá nhiều gánh nặng cho bộ nhớ và hiệu suất 
- **Thiết kế thông minh**: Tự động ẩn nút "Xem thêm" khi tất cả bài viết đã được hiển thị
- **Phản hồi hiệu quả**: Thông báo dữ liệu thay đổi cho adapter một cách hiệu quả để cập nhật chỉ những phần cần thiết của RecyclerView

Các tính năng này giúp ứng dụng hoạt động ổn định và nhanh hơn, đặc biệt là khi có lượng lớn bài viết cần hiển thị.

## Chế độ Dark/Light và Khả năng tiếp cận

### Chế độ Dark Mode
Ứng dụng hỗ trợ đầy đủ chế độ Dark mode, với các tùy chọn:
- **Theo hệ thống**: Tự động thay đổi theo cài đặt của thiết bị
- **Sáng**: Luôn sử dụng giao diện sáng
- **Tối**: Luôn sử dụng giao diện tối

Các icon trong ứng dụng sẽ tự động thay đổi màu sắc dựa trên theme, đảm bảo chúng luôn hiển thị rõ ràng trên nền tối hoặc sáng. Cụ thể:
- Trong chế độ sáng: Các icon có màu tối
- Trong chế độ tối: Các icon có màu sáng để dễ nhìn thấy

### Cài đặt Cỡ chữ
Người dùng có thể điều chỉnh kích thước chữ toàn bộ ứng dụng:
- Nhỏ: Dành cho người muốn hiển thị nhiều nội dung hơn
- Vừa: Kích thước mặc định
- Lớn: Dành cho người đọc thoải mái hơn

### Chế độ Tương phản cao
Tăng độ tương phản giữa văn bản và nền, giúp người dùng cao tuổi hoặc có vấn đề về thị lực đọc dễ dàng hơn.
