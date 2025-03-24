package com.example.article;

import android.app.Application;
import com.example.article.utils.NetworkUtils;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NetworkUtils.init(this);
    }
} 