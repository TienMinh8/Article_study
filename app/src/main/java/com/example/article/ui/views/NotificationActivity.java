package com.example.article.ui.views;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.article.R;
import com.example.article.utils.NotificationUtils;

public class NotificationActivity extends AppCompatActivity {

    private Switch switchBreakingNews;
    private Switch switchDailyDigest;
    private Switch switchSavedArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        switchBreakingNews = findViewById(R.id.switchBreakingNews);
        switchDailyDigest = findViewById(R.id.switchDailyDigest);
        switchSavedArticles = findViewById(R.id.switchSavedArticles);

        // Set current settings
        switchBreakingNews.setChecked(NotificationUtils.isBreakingNewsEnabled(this));
        switchDailyDigest.setChecked(NotificationUtils.isDailyDigestEnabled(this));
        switchSavedArticles.setChecked(NotificationUtils.isSavedArticlesEnabled(this));

        // Set up listeners
        switchBreakingNews.setOnCheckedChangeListener((buttonView, isChecked) -> {
            NotificationUtils.setBreakingNewsEnabled(this, isChecked);
        });

        switchDailyDigest.setOnCheckedChangeListener((buttonView, isChecked) -> {
            NotificationUtils.setDailyDigestEnabled(this, isChecked);
        });

        switchSavedArticles.setOnCheckedChangeListener((buttonView, isChecked) -> {
            NotificationUtils.setSavedArticlesEnabled(this, isChecked);
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