package com.example.article;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.article.utils.ThemeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Áp dụng chế độ giao diện theo cài đặt người dùng
        ThemeUtils.applyAppTheme(this);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Sửa cách lấy NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Thiết lập các ID destinations cấp cao nhất (top-level destinations) - đã loại bỏ notifications
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, 
                    R.id.navigation_discover, 
                    R.id.navigation_saved, 
                    R.id.navigation_profile).build();

            // Kết nối bottom navigation với NavController
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
            
            // Thiết lập listener để xử lý các trường hợp đặc biệt
            setupNavListener();
        }
    }
    
    private void setupNavListener() {
        // Set up notification icon click listener trong từng fragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Xử lý logic cho các destination khác nhau nếu cần
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }
    
    // Phương thức này sẽ được gọi từ các fragment khi người dùng nhấn vào icon thông báo
    public void showNotifications(View view) {
        // Điều hướng đến màn hình thông báo
        if (navController != null) {
            navController.navigate(R.id.navigation_notifications);
        }
    }
    
    /**
     * Phương thức để chuyển đổi chế độ tối/sáng
     * Có thể được gọi từ ProfileFragment hoặc màn hình cài đặt
     */
    public void toggleDarkMode(boolean enableDarkMode) {
        // Lưu cài đặt
        if (enableDarkMode) {
            ThemeUtils.saveThemeMode(this, ThemeUtils.MODE_DARK);
        } else {
            ThemeUtils.saveThemeMode(this, ThemeUtils.MODE_LIGHT);
        }
    }
}