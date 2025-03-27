package com.example.article.ui.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.article.R;
import com.example.article.utils.LanguageUtils;
import com.example.article.utils.NotificationUtils;

public class MenuSetting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_setting);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up click listeners for menu items
        findViewById(R.id.cardLanguage).setOnClickListener(v -> {
            // Mở màn hình chọn ngôn ngữ
            Intent intent = new Intent(this, LanguageActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardNotifications).setOnClickListener(v -> {
            // Mở màn hình cài đặt thông báo
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardRateApp).setOnClickListener(v -> {
            // Mở trang đánh giá trên Play Store
            String packageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
        });

        findViewById(R.id.cardFeedback).setOnClickListener(v -> {
            // Mở màn hình feedback
            Intent intent = new Intent(this, FeedbackActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardPrivacy).setOnClickListener(v -> {
            // Mở trang chính sách bảo mật
            String url = "https://sites.google.com/view/miastudiopolicy";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}