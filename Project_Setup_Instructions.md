# Hướng Dẫn Thiết Lập Dự Án

## 🔧 Mục Tiêu
Thiết lập cấu trúc dự án ban đầu cho ứng dụng tin tức với đầy đủ công cụ và phụ thuộc cần thiết để bắt đầu phát triển.

## 📁 Cấu Trúc Thư Mục

```
news-app/
├── app/                      # Mã nguồn chính
│   ├── src/                  # Mã nguồn
│   │   ├── main/             # Mã nguồn chính
│   │   │   ├── java/         # Mã Java/Kotlin
│   │   │   │   └── com/
│   │   │   │       └── newsapp/
│   │   │   │           ├── api/           # Gọi API và model phản hồi
│   │   │   │           ├── data/          # Lớp quản lý dữ liệu
│   │   │   │           ├── di/            # Dependency Injection
│   │   │   │           ├── ui/            # Giao diện người dùng
│   │   │   │           │   ├── home/      # Màn hình trang chủ
│   │   │   │           │   ├── article/   # Chi tiết bài viết
│   │   │   │           │   ├── discover/  # Khám phá
│   │   │   │           │   ├── saved/     # Đã lưu
│   │   │   │           │   ├── profile/   # Hồ sơ người dùng
│   │   │   │           │   └── common/    # Component dùng chung
│   │   │   │           ├── util/          # Tiện ích
│   │   │   │           ├── service/       # Background service
│   │   │   │           └── App.kt         # Lớp Application
│   │   │   └── res/           # Tài nguyên
│   │   │       ├── drawable/  # Hình ảnh và biểu tượng
│   │   │       ├── layout/    # Layout XML
│   │   │       ├── values/    # Strings, colors, styles
│   │   │       └── xml/       # Cấu hình khác
│   │   └── test/              # Unit tests
│   └── build.gradle          # Cấu hình build module
├── build.gradle              # Cấu hình build dự án
├── proguard-rules.pro        # ProGuard rules
└── README.md                 # Tài liệu dự án
```

## 🛠️ Các Bước Thiết Lập

### 1. Tạo Dự Án Mới
```bash
# Sử dụng Android Studio hoặc dòng lệnh
# Tạo dự án Android với:
# - Ứng dụng mobile
# - Sử dụng Kotlin
# - Hỗ trợ API level tối thiểu 21 (Android 5.0)
# - Sử dụng AndroidX
```

### 2. Cài Đặt Phụ Thuộc

Thêm các phụ thuộc sau vào file `build.gradle`:

```gradle
dependencies {
    // AndroidX và Material Design
    implementation 'androidx.core:core-ktx:1.10.1'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // Navigation Component
    implementation 'androidx.navigation:navigation-fragment-ktx:2.6.0'
    implementation 'androidx.navigation:navigation-ui-ktx:2.6.0'
    
    // ViewModel + LiveData
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.6.1'
    
    // Room Database
    implementation 'androidx.room:room-runtime:2.5.2'
    implementation 'androidx.room:room-ktx:2.5.2'
    kapt 'androidx.room:room-compiler:2.5.2'
    
    // Retrofit + GSON
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
    
    // Glide - Tải và cache hình ảnh
    implementation 'com.github.bumptech.glide:glide:4.15.1'
    kapt 'com.github.bumptech.glide:compiler:4.15.1'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1'
    
    // DataStore
    implementation 'androidx.datastore:datastore-preferences:1.0.0'
    
    // Hilt - Dependency Injection
    implementation 'com.google.dagger:hilt-android:2.44'
    kapt 'com.google.dagger:hilt-android-compiler:2.44'
    
    // WorkManager
    implementation 'androidx.work:work-runtime-ktx:2.8.1'
    
    // Firebase (nếu cần phân tích/thông báo)
    implementation platform('com.google.firebase:firebase-bom:32.2.0')
    implementation 'com.google.firebase:firebase-analytics-ktx'
    implementation 'com.google.firebase:firebase-messaging-ktx'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

### 3. Cấu Hình Gradle

Thêm các cấu hình sau vào file `build.gradle` cấp dự án:

```gradle
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.0.2'
        classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0'
        classpath 'com.google.dagger:hilt-android-gradle-plugin:2.44'
        // Firebase
        classpath 'com.google.gms:google-services:4.3.15'
    }
}
```

### 4. Cấu Hình Application

Tạo lớp `App.kt` để khởi tạo các thành phần cần thiết:

```kotlin
@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Khởi tạo các thư viện, cấu hình logging
    }
}
```

Cập nhật `AndroidManifest.xml` để sử dụng lớp Application:

```xml
<application
    android:name=".App"
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:supportsRtl="true"
    android:theme="@style/Theme.NewsApp">
    <!-- Activities -->
</application>
```

### 5. Thiết Lập Đa Ngôn Ngữ Cơ Bản

Tạo file `strings.xml` cho tiếng Anh và tiếng Việt:

`res/values/strings.xml` (Tiếng Anh mặc định):
```xml
<resources>
    <string name="app_name">News App</string>
    <string name="tab_home">Home</string>
    <string name="tab_discover">Discover</string>
    <string name="tab_saved">Saved</string>
    <string name="tab_notifications">Notifications</string>
    <string name="tab_profile">Profile</string>
</resources>
```

`res/values-vi/strings.xml` (Tiếng Việt):
```xml
<resources>
    <string name="app_name">Ứng Dụng Tin Tức</string>
    <string name="tab_home">Trang chủ</string>
    <string name="tab_discover">Khám phá</string>
    <string name="tab_saved">Đã lưu</string>
    <string name="tab_notifications">Thông báo</string>
    <string name="tab_profile">Cá nhân</string>
</resources>
```

### 6. Thiết Lập Theme

Cấu hình `themes.xml` để hỗ trợ dark mode:

```xml
<!-- Base application theme -->
<style name="Theme.NewsApp" parent="Theme.MaterialComponents.DayNight.NoActionBar">
    <!-- Màu sắc chính -->
    <item name="colorPrimary">@color/primary</item>
    <item name="colorPrimaryVariant">@color/primary_dark</item>
    <item name="colorOnPrimary">@color/white</item>
    <!-- Màu sắc phụ -->
    <item name="colorSecondary">@color/accent</item>
    <item name="colorSecondaryVariant">@color/accent_dark</item>
    <item name="colorOnSecondary">@color/black</item>
    <!-- Status bar -->
    <item name="android:statusBarColor">?attr/colorPrimaryVariant</item>
    <!-- Typography và các thuộc tính khác -->
</style>
```

### 7. Tạo Navigation Graph

Thiết lập file `res/navigation/nav_graph.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_graph"
    app:startDestination="@id/homeFragment">

    <fragment
        android:id="@+id/homeFragment"
        android:name="com.newsapp.ui.home.HomeFragment"
        android:label="@string/tab_home" />

    <fragment
        android:id="@+id/discoverFragment"
        android:name="com.newsapp.ui.discover.DiscoverFragment"
        android:label="@string/tab_discover" />

    <fragment
        android:id="@+id/savedFragment"
        android:name="com.newsapp.ui.saved.SavedFragment"
        android:label="@string/tab_saved" />

    <fragment
        android:id="@+id/notificationsFragment"
        android:name="com.newsapp.ui.notifications.NotificationsFragment"
        android:label="@string/tab_notifications" />

    <fragment
        android:id="@+id/profileFragment"
        android:name="com.newsapp.ui.profile.ProfileFragment"
        android:label="@string/tab_profile" />

</navigation>
```

### 8. Thiết Lập Git

Tạo file `.gitignore` để loại trừ các file không cần theo dõi:

```
*.iml
.gradle
/local.properties
/.idea
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
local.properties
```

Khởi tạo repository:

```bash
git init
git add .
git commit -m "Initial project setup"
```

## ✅ Kiểm Tra Hoàn Thành

- [ ] Dự án đã được tạo với cấu trúc thư mục phù hợp
- [ ] Các phụ thuộc (dependencies) đã được cài đặt
- [ ] Cấu hình Gradle đã được thiết lập chính xác
- [ ] Lớp Application đã được tạo và cấu hình
- [ ] Đa ngôn ngữ cơ bản đã được thiết lập
- [ ] Theme và styles đã được cấu hình
- [ ] Navigation graph đã được tạo
- [ ] Git đã được khởi tạo

## 📌 Bước Tiếp Theo

Sau khi hoàn thành thiết lập dự án, tiếp tục với [UI_Instructions.md](UI_Instructions.md) để triển khai giao diện người dùng cơ bản. 