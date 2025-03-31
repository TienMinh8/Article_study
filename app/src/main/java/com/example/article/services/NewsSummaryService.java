package com.example.article.services;

import android.content.Context;
import android.util.Log;
import com.example.article.api.model.NewsArticle;
import com.example.article.utils.NetworkUtils;
import com.example.article.utils.LanguageUtils;
import com.example.article.utils.CacheManager;

import org.json.JSONObject;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NewsSummaryService {
    private static final String TAG = "NewsSummaryService";
    private static final String API_URL = "https://ai.dreamapi.net/api/v1/summarize";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private final OkHttpClient client;
    private final Context context;
    private final CacheManager cacheManager;

    public NewsSummaryService(Context context) {
        this.context = context;
        this.cacheManager = CacheManager.getInstance(context);
        
        this.client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();
    }

    public interface SummaryCallback {
        void onSuccess(String summary);
        void onError(String error);
    }

    public void generateSummary(NewsArticle article, SummaryCallback callback) {
        // Kiểm tra cache trước
        String cacheKey = "summary_" + article.getUrl();
        String cachedSummary = cacheManager.getString(cacheKey);
        
        if (cachedSummary != null) {
            Log.d(TAG, "Using cached summary for article: " + article.getUrl());
            callback.onSuccess(cachedSummary);
            return;
        }

        // Kiểm tra kết nối mạng
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("No internet connection");
            return;
        }

        try {
            // Chuẩn bị nội dung để gửi
            String content = article.getContent() != null ? article.getContent() : article.getDescription();
            if (content == null || content.isEmpty()) {
                callback.onError("No content to summarize");
                return;
            }

            JSONObject requestBody = new JSONObject();
            requestBody.put("text", content);
            requestBody.put("language", LanguageUtils.getCurrentLanguage(context));
            requestBody.put("max_length", 150);

            RequestBody body = RequestBody.create(JSON, requestBody.toString());
            Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, java.io.IOException e) {
                    Log.e(TAG, "API call failed", e);
                    callback.onError("Network error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws java.io.IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            String summary = jsonResponse.getString("summary");
                            
                            // Lưu vào cache
                            cacheManager.putString(cacheKey, summary);
                            
                            callback.onSuccess(summary);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response", e);
                            callback.onError("Error parsing response");
                        }
                    } else {
                        Log.e(TAG, "API error: " + response.code());
                        callback.onError("API error: " + response.code());
                    }
                    response.close();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error preparing request", e);
            callback.onError("Error: " + e.getMessage());
        }
    }
} 