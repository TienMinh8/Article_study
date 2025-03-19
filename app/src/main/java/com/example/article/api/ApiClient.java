package com.example.article.api;

import android.content.Context;
import android.util.Log;

import com.example.article.R;
import com.example.article.api.model.NewsArticle;
import com.example.article.api.model.NewsResponse;
import com.example.article.utils.ConfigUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static ApiClient instance;
    private final ApiService apiService;
    private final String apiKey;
    private final String baseUrl;
    private final Context context;
    
    private static final int CONNECTION_TIMEOUT = 15; // Seconds
    private static final int READ_TIMEOUT = 15; // Seconds

    private ApiClient(Context context) {
        this.context = context.getApplicationContext();
        
        // Đọc config từ file
        JSONObject config = ConfigUtils.getConfigJson(context);
        try {
            JSONObject apiConfig = config.getJSONObject("api");
            baseUrl = apiConfig.getString("baseUrl");
            apiKey = apiConfig.getString("apiKey");
            
            // Tùy chỉnh OkHttpClient với timeout
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .build();
            
            // Khởi tạo Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing ApiClient: " + e.getMessage());
            throw new RuntimeException("Failed to initialize ApiClient", e);
        }
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context);
        }
        return instance;
    }
    
    public Context getContext() {
        return context;
    }

    public void getTeslaNews(final ApiCallback<List<NewsArticle>> callback) {
        try {
            // Lấy tin tức về Tesla sử dụng ngày hiện tại
            // Lấy thời gian hiện tại
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            
            // Tính toán ngày trong phạm vi cho phép (gói miễn phí thường cho phép 1 tháng)
            // Để đảm bảo không vượt quá giới hạn, chỉ lấy tin trong 7 ngày gần đây
            calendar.add(Calendar.DAY_OF_MONTH, -7);
            String fromDate = dateFormat.format(calendar.getTime());
            
            // Ngày hiện tại
            calendar = Calendar.getInstance();
            String toDate = dateFormat.format(calendar.getTime());
            
            Log.d(TAG, "Fetching Tesla news from API: " + fromDate + " to: " + toDate);
            
            // Gọi API với tham số phù hợp với thời gian hiện tại
            Call<NewsResponse> call = apiService.getEverything("tesla", fromDate, toDate, apiKey);
            
            call.enqueue(new Callback<NewsResponse>() {
                @Override
                public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<NewsArticle> articles = response.body().getArticles();
                        Log.d(TAG, "Tesla articles fetched from API: " + articles.size());
                        callback.onSuccess(articles);
                    } else {
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (IOException e) {
                            // Ignore
                        }
                        
                        String errorMessage = parseErrorResponse(response.code(), errorBody);
                        Log.e(TAG, "Error fetching Tesla articles: " + response.code() + " - " + errorBody);
                        callback.onError(errorMessage);
                    }
                }

                @Override
                public void onFailure(Call<NewsResponse> call, Throwable t) {
                    String errorMessage = "Network Error: " + t.getMessage();
                    Log.e(TAG, "Network error: " + t.getMessage());
                    callback.onError(errorMessage);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in getTeslaNews: " + e.getMessage());
            callback.onError("Error: " + e.getMessage());
        }
    }
    
    // Phương thức tìm kiếm bài viết với từ khóa
    public void getEverything(String query, final ApiCallback<List<NewsArticle>> callback) {
        try {
            // Lấy tin tức theo từ khóa
            Log.d(TAG, "Fetching news from API for query: " + query);
            
            // Lấy tin tức trong khoảng 7 ngày gần đây
            Call<NewsResponse> call = apiService.getEverything(query, getDateBefore(7), getDateBefore(0), apiKey);
            
            call.enqueue(new Callback<NewsResponse>() {
                @Override
                public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<NewsArticle> articles = response.body().getArticles();
                        
                        if (articles != null && !articles.isEmpty()) {
                            Log.d(TAG, "Articles fetched from API: " + articles.size() + " for query: " + query);
                            callback.onSuccess(articles);
                        } else {
                            Log.d(TAG, "No articles found for query: " + query);
                            // Gọi onSuccess với danh sách trống để tầng trên có thể quyết định xử lý
                            callback.onSuccess(new ArrayList<>());
                        }
                    } else {
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (IOException e) {
                            // Ignore
                        }
                        
                        String errorMessage = parseErrorResponse(response.code(), errorBody);
                        Log.e(TAG, "Error fetching articles: " + response.code() + " - " + errorBody);
                        callback.onError(errorMessage);
                    }
                }

                @Override
                public void onFailure(Call<NewsResponse> call, Throwable t) {
                    String errorMessage = "Network Error: " + t.getMessage();
                    Log.e(TAG, "Network error: " + t.getMessage());
                    callback.onError(errorMessage);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in getEverything: " + e.getMessage());
            callback.onError("Error: " + e.getMessage());
        }
    }
    
    // Phương thức tìm kiếm bài viết với từ khóa và phân trang
    public void getEverything(String query, int page, final ApiCallback<List<NewsArticle>> callback) {
        try {
            // Lấy tin tức theo từ khóa và phân trang
            Log.d(TAG, "Fetching news from API for query: " + query + ", page: " + page);
            
            // Lấy tin tức trong khoảng 7 ngày gần đây, với phân trang
            Call<NewsResponse> call = apiService.getEverythingPaged(
                    query, getDateBefore(7), getDateBefore(0), page, 10, apiKey);
            
            call.enqueue(new Callback<NewsResponse>() {
                @Override
                public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<NewsArticle> articles = response.body().getArticles();
                        
                        if (articles != null && !articles.isEmpty()) {
                            Log.d(TAG, "Articles fetched from API: " + articles.size() + " for query: " + query + ", page: " + page);
                            callback.onSuccess(articles);
                        } else {
                            Log.d(TAG, "No articles found for query: " + query + ", page: " + page);
                            // Gọi onSuccess với danh sách trống để tầng trên có thể quyết định xử lý
                            callback.onSuccess(new ArrayList<>());
                        }
                    } else {
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (IOException e) {
                            // Ignore
                        }
                        
                        String errorMessage = parseErrorResponse(response.code(), errorBody);
                        Log.e(TAG, "Error fetching articles: " + response.code() + " - " + errorBody);
                        callback.onError(errorMessage);
                    }
                }

                @Override
                public void onFailure(Call<NewsResponse> call, Throwable t) {
                    String errorMessage = "Network Error: " + t.getMessage();
                    Log.e(TAG, "Network error: " + t.getMessage());
                    callback.onError(errorMessage);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in getEverything with paging: " + e.getMessage());
            callback.onError("Error: " + e.getMessage());
        }
    }
    
    // Phân tích lỗi từ Error Response
    private String parseErrorResponse(int statusCode, String errorBody) {
        try {
            // Nếu không có JSON hoặc không phân tích được, sử dụng status code
            switch (statusCode) {
                case 400:
                    return "Bad Request: The request was invalid";
                case 401:
                    return "Unauthorized: API key is invalid or missing";
                case 429:
                    return "Too Many Requests: Rate limit exceeded";
                case 500:
                    return "Server Error: Something went wrong on the server";
                default:
                    return "Error " + statusCode + (errorBody != null && !errorBody.isEmpty() ? ": " + errorBody : "");
            }
        } catch (Exception e) {
            return "Error " + statusCode;
        }
    }
    
    // Lấy ngày X ngày trước đây
    private String getDateBefore(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return dateFormat.format(calendar.getTime());
    }

    // Interface để trả về kết quả
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}