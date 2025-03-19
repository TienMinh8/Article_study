# Hướng Dẫn Thiết Kế UI/UX

## 🎯 Mục Tiêu
Tạo giao diện người dùng hiện đại, tối giản và trực quan theo phong cách của các ứng dụng tin tức hàng đầu. tham khảo màu sắc và bố cục trong [ui_thamkhao.jpg]

## 📱 Bottom Navigation

### 1. Cấu Trúc Tab
- Home Tab
  - Icon: Biểu tượng ngôi nhà đơn giản, stroke style
  - Label: "Home"
  - Hiển thị Breaking News và Recommendation
- Discover Tab
  - Icon: Biểu tượng kính lúp
  - Label: "Discover"
  - Hiển thị các chủ đề và bài viết được phân loại
- Saved Tab
  - Icon: Biểu tượng bookmark tối giản
  - Label: "Saved"
  - Hiển thị các bài viết đã lưu
- Profile Tab
  - Icon: Biểu tượng người dùng tối giản
  - Label: "Profile"
  - Hiển thị cài đặt và thông tin cá nhân

### 2. Thiết Kế Tab
- Kích thước: 48dp chiều cao
- Icon size: 20dp
- Label size: 12sp
- Padding: 6dp
- Active color: #007AFF
- Inactive color: #8E8E93

## 🎠 Breaking News Section

### 1. Thiết Kế
- Layout: Card style với hình ảnh full width
- Chiều cao: 180dp
- Border radius: 12dp
- Margin: 16dp horizontal, 12dp vertical
- Overlay gradient: Linear từ transparent đến semi-black (rgba(0,0,0,0.6))

### 2. Thông Tin Hiển Thị
- Tag "Sports" hoặc category khác
  - Màu nền: #0D47A1 (xanh dương đậm)
  - Padding: 12dp horizontal, 4dp vertical
  - Text: Màu trắng, 12sp, bold
  - Elevation: 4dp để tạo shadow và nổi bật trên background
  - Vị trí: Góc trên bên trái, cách lề 16dp
- Thông tin nguồn và thời gian
  - Vị trí: Phía dưới nội dung, cách lề dưới 8dp
  - Text nguồn: 14sp, bold, màu trắng, shadow 2dp
  - Text thời gian: 14sp, màu trắng, shadow 2dp
  - Shadow: Màu #80000000, Dx=1, Dy=1, Radius=2
- Tiêu đề: 20sp, SF Pro Display Bold
- Time stamp: 12sp, màu xám nhạt
- Shadow text để đảm bảo độ tương phản

### 3. ViewPager2 & Page Indicator ✅
- ViewPager2 hiển thị các tin nổi bật
- Slide effect: Khoảng cách 40dp giữa các trang
- Scale effect: Trang không được chọn có tỉ lệ nhỏ hơn (0.85)
- Page Indicator:
  - Dạng chấm tròn (dot indicator)
  - Màu chấm được chọn: #1E88E5 (xanh dương nhạt)
  - Màu chấm không được chọn: #DDDDDD (xám nhạt)
  - Kích thước chấm được chọn: 8dp x 8dp
  - Kích thước chấm không được chọn: 6dp x 6dp
  - Khoảng cách giữa các chấm: 4dp
- Tab Layout:
  - Chiều cao cố định: 16dp
  - Canh giữa với ViewPager2
  - Không hiển thị tab indicator dưới
  - Vị trí: Canh giữa dưới ViewPager
- Auto Slide: ✅
  - Tự động chuyển trang mỗi 3 giây
  - Bắt đầu sau 0.5 giây khi màn hình hiển thị
  - Dừng khi người dùng tương tác hoặc khi fragment bị tạm dừng
  - Tiếp tục khi fragment được resume

##  News Card Design

### 1. Layout
- Padding: 16dp
- Margin: 12dp
- Corner radius: 12dp
- Background: White
- Box shadow: nhẹ (0 2px 8px rgba(0,0,0,0.05))

### 2. Thành Phần
- Hình ảnh (1:1 ratio cho thumbnail)
- Tiêu đề (16sp, SF Pro Text Medium)
- Author/Source với avatar nhỏ
- Time stamp
- Category tag (khi cần thiết)

## 🎨 Theme và Màu Sắc

### 1. Light Theme
- Background: #FFFFFF
- Surface: #F2F2F7
- Primary: #007AFF
- Secondary: #5856D6
- Text primary: #000000 (87%)
- Text secondary: #8E8E93

### 2. Dark Theme
- Background: #000000
- Surface: #1C1C1E
- Primary: #0A84FF
- Secondary: #5E5CE6
- Text primary: #FFFFFF
- Text secondary: #98989F

## 📝 Typography

### 1. Font Family
- Primary: SF Pro Text
- Headers: SF Pro Display
- Fallback: System default sans-serif

### 2. Font Sizes
- Breaking News Title: 20sp
- Card Title: 16sp
- Body text: 14sp
- Meta info: 12sp
- Category tags: 12sp

### 3. Font Weights
- Bold: 700 (Headers)
- Medium: 500 (Titles)
- Regular: 400 (Body)

## 🔍 Search & Filter

### 1. Search Bar Design
- Height: 40dp
- Border radius: 10dp
- Background: #F2F2F7
- Icon: Kính lúp tối giản
- Placeholder text: "Search"

### 2. Filter Categories
- Horizontal scrolling
- Pill-shaped buttons
- Active state: Filled
- Inactive state: Outlined
- Padding: 8dp 16dp
- Font: 14sp Medium

## ⚡ Animations & Transitions

### 1. Card Interactions
- Smooth scale on press (0.98)
- Haptic feedback
- Transition duration: 200ms
- Easing: ease-in-out

### 2. Navigation Transitions
- Slide transitions between screens
- Fade effect for bottom tabs
- Smooth scroll behaviors
- Pull-to-refresh với animation tối giản

## ✅ Kiểm Tra Hoàn Thành

- [ ] Modern bottom navigation
- [ ] Breaking news section
- [ ] News card layout
- [ ] Search & filter implementation
- [ ] Typography system
- [ ] Smooth animations
- [ ] Dark mode support

## 📌 Lưu Ý

- Đảm bảo spacing nhất quán (sử dụng bội số của 4dp)
- Tối ưu contrast cho text trên hình ảnh
- Áp dụng haptic feedback cho các interaction chính
- Đảm bảo layout responsive trên các kích thước màn hình khác nhau

## 🔄 Bước Tiếp Theo

Sau khi hoàn thành UI cơ bản, chuyển sang [API_Integration.md](API_Integration.md) để tích hợp dữ liệu thực. 