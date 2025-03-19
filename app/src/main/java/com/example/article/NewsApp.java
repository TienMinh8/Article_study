package com.example.article;

import android.app.Application;
import android.util.Log;

public class NewsApp extends Application {
    private static final String TAG = "NewsApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application started");
        initializeComponents();
    }

    private void initializeComponents() {
        // Khởi tạo các thành phần cần thiết khi app khởi động
        Log.d(TAG, "Initializing components");
    }
} 