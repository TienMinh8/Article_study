package com.example.article.ui.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.article.MainActivity;
import com.example.article.R;
import com.example.article.utils.LanguageUtils;
import com.example.article.utils.CacheManager;

public class LanguageActivity extends AppCompatActivity {

    private RadioGroup languageGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        languageGroup = findViewById(R.id.languageGroup);

        // Set current language
        String currentLanguage = LanguageUtils.getCurrentLanguage(this);
        if (currentLanguage.equals("en")) {
            languageGroup.check(R.id.radioEnglish);
        } else if (currentLanguage.equals("vi")) {
            languageGroup.check(R.id.radioVietnamese);
        }

        // Set up language change listener
        languageGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String newLanguage;
            if (checkedId == R.id.radioEnglish) {
                newLanguage = "en";
            } else if (checkedId == R.id.radioVietnamese) {
                newLanguage = "vi";
            } else {
                return;
            }

            // Change language
            LanguageUtils.setLocale(this, newLanguage);

            // Clear cache to force reload data with new language
            CacheManager.getInstance(this).clearAll();

            // Restart app to apply changes
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            Toast.makeText(this, R.string.language_changed, Toast.LENGTH_SHORT).show();
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