# Hướng Dẫn Cho Ứng Dụng Tin Tức

## 🎯 Mục Tiêu Chính
- Tạo ứng dụng tin tức tối ưu cho điện thoại dành cho người dùng trung niên và cao tuổi
- Giao diện dễ sử dụng với kiểu chữ dễ đọc
- Nguồn cấp tin tức cá nhân hóa với phân loại thông minh
- Triển khai tính năng theo dõi thời gian sử dụng (sức khỏe kỹ thuật số)
- Kiếm tiền thông qua quảng cáo không gây phiền hà
- Xây dựng trên Java

## Api 
- ✅ Api key và endpoint sẽ được lưu trữ trong file cấu hình trong assets để dễ thay đổi

## Các thư viện tham khảo 
- Sử dụng tối đa các thư viện bên thứ 3 để tránh phát sinh các resource 
Ví dụ: ionicons: ^0.2.2

## 📱 Yêu Cầu UI/UX
- ✅ Thanh điều hướng dưới với 5 tab: Trang chủ, Khám phá, Đã lưu, Thông báo, Cá nhân
- ✅ Trình chiếu cho tin nổi bật (40% chiều cao màn hình)
  - ✅ Cài đặt ViewPager2 cho hiển thị tin nổi bật
  - ✅ Thêm chỉ báo trang (page indicator) dạng chấm tròn
  - ✅ Tùy chỉnh hiệu ứng chuyển trang với margin và thu nhỏ
  - ✅ Tự động chuyển trang
- ✅ Bố cục tin tức dạng thẻ với khoảng cách phù hợp (12dp)
- ✅ Kéo để làm mới với hiệu ứng tải
- ✅ Chế độ tối/sáng dựa trên cài đặt hệ thống
- ✅ Kiểu chữ: Sans-serif (cỡ mặc định 16-20pt)
- ❌ Cử chỉ vuốt để điều hướng và hành động

*Chi tiết tại: [UI_Instructions.md](UI_Instructions.md)*

## 🧠 Tính Năng Thông Minh
- ❌ Tóm tắt tin tức bằng AI
- ❌ Đề xuất nội dung cá nhân hóa
- ❌ Phân loại tin tức thông minh
- ❌ Chức năng đọc sau
- ❌ Theo dõi tiến độ đọc
- ❌ Chế độ ngoại tuyến với nội dung tải trước

*Chi tiết tại: [SmartFeatures_Instructions.md](SmartFeatures_Instructions.md)*

## 🔔 Thông Báo
- ❌ Cảnh báo tin nóng
- ❌ Thông báo chủ đề tùy chỉnh
- ❌ Cảnh báo sức khỏe kỹ thuật số (sử dụng trên 180 phút)
- ❌ Tổng hợp đọc hàng tuần

*Chi tiết tại: [Notifications_Instructions.md](Notifications_Instructions.md)*

## 🎮 Gamification
- ❌ Huy hiệu và thành tích đọc
- ❌ Phần thưởng theo dõi chủ đề
- ❌ Khuyến khích chia sẻ
- ❌ Ghi nhận tham gia cộng đồng

*Chi tiết tại: [Gamification_Instructions.md](Gamification_Instructions.md)*

## 💰 Kiếm Tiền
- ❌ Quảng cáo native trong nguồn tin
- ❌ Vị trí quảng cáo theo ngữ cảnh
- ❌ Tùy chọn đăng ký premium

*Chi tiết tại: [Monetization_Instructions.md](Monetization_Instructions.md)*

## 🌐 Tích Hợp API
- ❌ Tích hợp API nội dung tin tức
- ❌ Tạo tóm tắt bằng AI (qua ai.dreamapi.net)
- ❌ Chức năng trò chuyện để hỗ trợ người dùng
- ❌ Phân tích hình ảnh tin tức
- ❌ Triển khai ghi nhật ký và xử lý lỗi đúng cách

*Chi tiết tại: [API_Integration_Instructions.md](API_Integration_Instructions.md)*

## 📊 Dữ Liệu Người Dùng
- ❌ Theo dõi sở thích đọc
- ❌ Giám sát thời gian sử dụng
- ❌ Lưu trữ bài đọc/đã lưu
- ❌ Quản lý tùy chọn thông báo

*Chi tiết tại: [UserData_Instructions.md](UserData_Instructions.md)*

## 👴 Khả Năng Tiếp Cận
- ❌ Vùng chạm lớn (tối thiểu 48dp)
- ✅ Kích thước văn bản điều chỉnh được
- ❌ Tùy chọn độ tương phản cao
- ❌ Tương thích với trình đọc màn hình
- ❌ Tùy chọn bố cục đơn giản hóa

*Chi tiết tại: [Accessibility_Instructions.md](Accessibility_Instructions.md)*

## 🔋 Hiệu Suất
- ✅ Tải lười biếng cho hình ảnh
- ✅ Lưu trữ tạm phản hồi API
  - ✅ Cache size: 50MB
  - ✅ Cache mới: 5 phút
  - ✅ Cache cũ: 7 ngày (offline mode)
  - ✅ Tự động retry khi lỗi mạng (3 lần)
  - ✅ Timeout tối ưu (10s connect, 15s read/write)
- ❌ Tải trước nội dung có khả năng cần đến
- ❌ Đồng bộ hóa nền cho đọc ngoại tuyến

*Chi tiết tại: [Performance_Instructions.md](Performance_Instructions.md)*

## 🌐 Hỗ Trợ Ngôn Ngữ
- ⏳ Cơ sở hạ tầng đa ngôn ngữ
  - ✅ Hỗ trợ tiếng Việt
  - ✅ Hỗ trợ tiếng Anh (mặc định)
  - ❌ Hỗ trợ cho các ngôn ngữ bổ sung
- ✅ Mặc định: Tiếng Anh
- ❌ Hỗ trợ cho các ngôn ngữ bổ sung

*Chi tiết tại: [Localization_Instructions.md](Localization_Instructions.md)*

## 🔧 Thiết Lập Dự Án
- ❌ Cấu trúc dự án
- ❌ Quản lý phụ thuộc
- ❌ Cài đặt môi trường phát triển

*Chi tiết tại: [Project_Setup_Instructions.md](Project_Setup_Instructions.md)*

## 🧪 Kiểm Thử
- ❌ Kiểm thử đơn vị
- ❌ Kiểm thử UI
- ❌ Kiểm thử hiệu suất

*Chi tiết tại: [Testing_Instructions.md](Testing_Instructions.md)*

## 📲 Triển Khai
- ❌ Quy trình phát hành
- ❌ Cấu hình CI/CD
- ❌ Theo dõi lỗi và phân tích

*Chi tiết tại: [Deployment_Instructions.md](Deployment_Instructions.md)*

## 📋 Quy Trình Phát Triển
1. Bắt đầu với [Project_Setup_Instructions.md](Project_Setup_Instructions.md) để thiết lập dự án cơ bản
2. Triển khai giao diện người dùng cơ bản theo [UI_Instructions.md](UI_Instructions.md)
3. Tích hợp API theo [API_Integration_Instructions.md](API_Integration_Instructions.md)
4. Tiếp tục với các tính năng còn lại theo thứ tự ưu tiên
5. Triển khai kiểm thử theo [Testing_Instructions.md](Testing_Instructions.md)
6. Chuẩn bị triển khai theo [Deployment_Instructions.md](Deployment_Instructions.md)

## Chú Thích
- ✅ Đã hoàn thành
- ⏳ Đang thực hiện
- ❌ Chưa bắt đầu
