# Hướng dẫn sử dụng ứng dụng tin tức

## Tổng quan

Đây là một ứng dụng đọc tin tức hiện đại cho phép người dùng xem các tin tức mới nhất từ khắp nơi trên thế giới. Ứng dụng sử dụng NewsAPI để lấy dữ liệu và hiển thị thông tin một cách trực quan.

## Điều hướng ứng dụng

Ứng dụng có thanh điều hướng ở dưới cùng với 4 mục chính:
- **Trang chủ**: Hiển thị tin nóng và tin tức đề xuất
- **Khám phá**: Tìm kiếm và lọc tin tức theo từ khóa hoặc danh mục
- **Đã lưu**: Xem các bài viết đã được lưu lại để đọc sau
- **Hồ sơ**: Quản lý thông tin cá nhân và tùy chọn cài đặt

### Thông báo
Thay vì có một tab riêng cho thông báo, ứng dụng hiển thị biểu tượng thông báo ở góc trên bên phải của mỗi màn hình. Khi nhấn vào biểu tượng này, người dùng sẽ được điều hướng đến màn hình thông báo, nơi hiển thị:
- Tin tức mới từ các chủ đề đang theo dõi
- Cập nhật từ ứng dụng
- Thông báo tương tác

Cách sử dụng:
1. Nhấn vào biểu tượng thông báo (hình chuông) ở góc trên bên phải của bất kỳ màn hình chính nào
2. Ứng dụng sẽ điều hướng bạn đến màn hình thông báo
3. Xem danh sách thông báo và nhấn vào thông báo để xem chi tiết
4. Sử dụng nút quay lại để trở về màn hình trước đó

## Các tính năng chính

### Breaking News Slider
- Hiển thị tối đa 5 bài viết quan trọng nhất từ chủ đề "Tesla"
- Tự động chuyển trang mỗi 3 giây
- Vuốt trái/phải để xem các bài viết khác
- Nhấn vào để xem chi tiết bài viết

### Đề xuất bài viết
- Hiển thị danh sách tối đa 10 bài viết được đề xuất dựa trên từ khóa "technology"
- Nhấn "Xem tất cả" để hiển thị tất cả các bài viết có sẵn ngay lập tức
- Nhấn "Xem thêm" ở dưới danh sách để tải thêm 10 bài viết mới
- Hệ thống thông minh lọc bỏ các bài viết trùng lặp khi tải thêm
- Nhấn vào bài viết để xem chi tiết

### Tìm kiếm
- Tìm kiếm bài viết theo từ khóa
- Hỗ trợ tìm kiếm theo cả tiếng Anh và tiếng Việt

### Tính năng tải thêm
- Nút "Xem thêm" ở cuối danh sách tin tức trên cả màn hình Trang chủ và Khám phá
- Hiển thị ban đầu được giới hạn 10 bài viết để tăng tốc độ tải
- Tải thêm 10 bài viết mới khi nhấn nút
- Khi có nhiều hơn 10 kết quả, nút "Xem thêm" sẽ tự động hiển thị và hoạt động
- Tự động lọc các bài viết trùng lặp để tránh hiển thị nội dung đã có
- Hiển thị trạng thái nút phù hợp:
  - "Đang tải thêm..." khi đang tải dữ liệu mới
  - "Không còn kết quả" khi đã tải hết bài viết có sẵn
- Tối ưu hiệu suất bằng cách ưu tiên sử dụng dữ liệu đã tải về trước khi gọi API
- Hỗ trợ tải theo trang (pagination) để xem nhiều nội dung hơn
- Lưu ý: Chức năng tải thêm sẽ hoạt động khác nhau tùy theo loại tìm kiếm:
  - Tìm kiếm theo từ khóa: Tải thêm từ danh sách đã có, sau đó tiếp tục tải từ API nếu cần
  - Tìm kiếm theo danh mục: Chỉ tải từ danh sách đã có do giới hạn của API

### Xem tất cả tin tức
- Nút "Xem tất cả" ở phần Đề xuất trên màn hình Trang chủ:
  - Hiển thị ngay lập tức tất cả các bài viết có sẵn trong bộ nhớ đệm
  - Ẩn nút "Xem thêm" khi đã hiển thị tất cả bài viết
  - Thông báo số lượng bài viết đang hiển thị
- Cơ chế lưu trữ thông minh:
  - Lưu trữ danh sách đầy đủ các bài viết đề xuất trong bộ nhớ
  - Tối ưu hóa tải thêm bằng cách ưu tiên sử dụng dữ liệu đã có
  - Tự động gọi API chỉ khi cần thiết để tiết kiệm dữ liệu và tăng tốc độ

### Tải thêm
- Tính năng tải thêm 10 bài viết khi kéo xuống cuối danh sách
- Hiển thị hiệu ứng loading khi đang tải

### Hiển thị chi tiết
- Xem bài viết đầy đủ với trình duyệt tích hợp
- Hỗ trợ zoom và điều hướng trong trang

## Tính năng kéo để làm mới (Pull-to-refresh)

Ứng dụng hỗ trợ tính năng "Kéo để làm mới" trên các màn hình chính, cho phép bạn cập nhật nội dung tin tức mới nhất mà không cần đóng và mở lại ứng dụng.

### Cách sử dụng:
1. Đặt ngón tay ở phần trên của danh sách tin tức
2. Kéo xuống dưới cho đến khi xuất hiện biểu tượng làm mới
3. Thả tay để kích hoạt quá trình làm mới
4. Chờ trong giây lát khi ứng dụng tải dữ liệu mới

### Tính năng:
- **Hiệu ứng tải:** Hiển thị hiệu ứng tải hình tròn với màu sắc phù hợp với giao diện ứng dụng
- **Thông báo trạng thái:** Hiển thị thông báo khi bắt đầu làm mới và khi hoàn thành
- **Tải thông minh:** Tự động làm mới theo danh mục hoặc từ khóa tìm kiếm hiện tại

### Làm mới theo danh mục:
- Trên màn hình Khám phá, khi bạn đã chọn một danh mục cụ thể (như Kinh doanh, Công nghệ, v.v.), tính năng làm mới sẽ tải dữ liệu mới nhất cho danh mục đó
- Trên màn hình Trang chủ, làm mới sẽ cập nhật cả tin nổi bật và đề xuất

### Làm mới khi không có mạng:
- Nếu không có kết nối internet, ứng dụng sẽ thông báo và hiển thị dữ liệu đã được lưu trong bộ nhớ cache

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

## Bố cục tin tức dạng thẻ

Ứng dụng sử dụng bố cục dạng thẻ (card) cho tất cả các tin tức, với khoảng cách 12dp giữa các mục để tăng khả năng đọc và tạo không gian trắng hợp lý. Mỗi thẻ tin tức có:

- Hình ảnh thu nhỏ
- Tiêu đề bài viết
- Nhãn nguồn (với màu nền khác biệt)
- Thời gian đăng
- Mô tả ngắn
- Nút lưu bài viết và chia sẻ

Các thành phần UI liên quan:
- **CardView**: Mỗi thẻ tin tức sử dụng CardView với bo góc 10dp và độ nâng 4dp, tạo hiệu ứng nổi phù hợp với Material Design.
- **ItemSpacingDecoration**: Đảm bảo khoảng cách 12dp giữa các thẻ tin tức trong RecyclerView.
- **Bộ lọc danh mục (ChipGroup)**: Trong màn hình Khám phá, người dùng có thể lọc tin tức theo danh mục thông qua các chip có thể click vào.

Người dùng có thể:
- Cuộn để xem các tin tức khác
- Nhấn vào thẻ để xem chi tiết bài viết
- Lọc tin tức theo danh mục trong màn hình Khám phá
- Lưu hoặc chia sẻ bài viết trực tiếp từ thẻ 

## Tùy chỉnh giao diện
Ứng dụng hỗ trợ nhiều tùy chọn để điều chỉnh giao diện theo nhu cầu cá nhân của bạn.

### Chế độ tối/sáng
Ứng dụng hỗ trợ cả chế độ sáng và chế độ tối, tự động theo cài đặt hệ thống hoặc có thể tùy chỉnh thủ công.

**Cách sử dụng:**
1. Đi tới tab **Hồ sơ** từ thanh điều hướng dưới cùng
2. Tìm mục "Chế độ tối" trong phần Cài đặt giao diện
3. Bật công tắc để kích hoạt chế độ tối hoặc tắt để sử dụng chế độ sáng
4. Thay đổi sẽ được áp dụng ngay lập tức

Chế độ tối giúp giảm mỏi mắt khi đọc tin trong điều kiện ánh sáng yếu và tiết kiệm pin trên các thiết bị có màn hình OLED/AMOLED.

### Điều chỉnh kích thước chữ
Bạn có thể điều chỉnh kích thước chữ hiển thị trong ứng dụng để phù hợp với nhu cầu cá nhân.

**Có 4 tùy chọn kích thước chữ:**
- **Nhỏ:** Phù hợp khi bạn muốn hiển thị nhiều nội dung hơn trên màn hình
- **Vừa (Mặc định):** Kích thước cân bằng phù hợp với hầu hết người dùng
- **Lớn:** Dễ đọc hơn, phù hợp cho người có thị lực trung bình
- **Rất lớn:** Kích thước tối đa, phù hợp cho người lớn tuổi hoặc khó khăn về thị lực

**Cách sử dụng:**
1. Đi tới tab **Hồ sơ** từ thanh điều hướng dưới cùng
2. Tìm mục "Kích thước chữ" trong phần Cài đặt giao diện
3. Chọn kích thước phù hợp với nhu cầu của bạn
4. Khởi động lại ứng dụng để áp dụng thay đổi

### Chế độ tương phản cao
Ứng dụng cung cấp chế độ tương phản cao cho người dùng có thị lực kém hoặc khó phân biệt màu sắc.

**Cách sử dụng:**
1. Đi tới tab **Hồ sơ** từ thanh điều hướng dưới cùng
2. Tìm mục "Độ tương phản cao" trong phần Cài đặt giao diện
3. Bật công tắc để kích hoạt
4. Khởi động lại ứng dụng để áp dụng thay đổi

Khi bật chế độ này, ứng dụng sẽ sử dụng màu sắc tương phản cao hơn và loại bỏ các hiệu ứng phức tạp, giúp tăng khả năng đọc cho người khiếm thị. 