package com.example.article.ui.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.article.R;
import com.google.android.material.textfield.TextInputEditText;

public class FeedbackActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etSubject, etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etSubject = findViewById(R.id.etSubject);
        etMessage = findViewById(R.id.etMessage);

        // Set up submit button
        findViewById(R.id.btnSubmit).setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        // Validate input
        if (TextUtils.isEmpty(etName.getText()) ||
            TextUtils.isEmpty(etEmail.getText()) ||
            TextUtils.isEmpty(etSubject.getText()) ||
            TextUtils.isEmpty(etMessage.getText())) {
            Toast.makeText(this, R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        // Create email intent
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:phamnguyetnt89@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, etSubject.getText().toString());
        
        // Create email body
        String body = "Name: " + etName.getText().toString() + "\n\n" +
                     "Email: " + etEmail.getText().toString() + "\n\n" +
                     "Message:\n" + etMessage.getText().toString();
        intent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            startActivity(Intent.createChooser(intent, getString(R.string.feedback)));
            Toast.makeText(this, R.string.feedback_sent, Toast.LENGTH_SHORT).show();
            finish();
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, R.string.error_sending_feedback, Toast.LENGTH_SHORT).show();
        }
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