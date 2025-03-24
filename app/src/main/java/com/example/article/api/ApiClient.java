package com.example.article.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.article.api.model.NewsArticle;
import com.example.article.api.model.NewsResponse;
import com.example.article.utils.ConfigUtils;
import com.example.article.utils.NetworkUtils;
import com.example.article.utils.CacheManager;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.reflect.TypeToken;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static ApiClient instance;
    private final ApiService apiService;
    private final String apiKey;
    private final String baseUrl;
    private final Context context;
    private final CacheManager cacheManager;
    
    private static final int CONNECTION_TIMEOUT = 15; // Seconds
    private static final int READ_TIMEOUT = 15; // Seconds

    private ApiClient(Context context) {
        this.context = context.getApplicationContext();
        this.cacheManager = CacheManager.getInstance(context);
        
        // Đọc config từ file
        JSONObject config = ConfigUtils.getConfigJson(context);
        try {
            JSONObject apiConfig = config.getJSONObject("api");
            baseUrl = apiConfig.getString("baseUrl");
            apiKey = apiConfig.getString("apiKey");
            
            // Khởi tạo Retrofit với client đã cấu hình
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(NetworkUtils.getClient())
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
        String cacheKey = CacheManager.generateCacheKey("search", query);

        // Kiểm tra cache trước
        if (cacheManager.hasValidCache(cacheKey)) {
            TypeToken<List<NewsArticle>> typeToken = new TypeToken<List<NewsArticle>>() {};
            List<NewsArticle> cachedArticles = cacheManager.get(cacheKey, typeToken);
            if (cachedArticles != null) {
                Log.d(TAG, "Using cached search results for query: " + query);
                callback.onSuccess(cachedArticles);
                return;
            }
        }

        // Tính toán khoảng thời gian tìm kiếm (7 ngày gần nhất)
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String toDate = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        String fromDate = dateFormat.format(calendar.getTime());

        // Gọi API nếu không có cache
        Call<NewsResponse> call = apiService.getEverything(query, fromDate, toDate, apiKey);
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsArticle> articles = response.body().getArticles();
                    if (articles != null && !articles.isEmpty()) {
                        // Lưu vào cache
                        cacheManager.put(cacheKey, articles);
                        Log.d(TAG, "Search results fetched and cached for query: " + query);
                        callback.onSuccess(articles);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                } else {
                    handleApiError(response, cacheKey, callback);
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                handleNetworkError(t, cacheKey, callback);
            }
        });
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

    /**
     * Lấy tin tức hàng đầu theo danh mục
     * @param category Danh mục tin tức (business, technology, entertainment, sports, science, health, general)
     * @param callback Callback để trả về kết quả
     */
    public void getTopHeadlines(String category, final ApiCallback<List<NewsArticle>> callback) {
        String cacheKey = CacheManager.generateCacheKey("headlines", category);

        // Kiểm tra cache trước
        if (cacheManager.hasValidCache(cacheKey)) {
            TypeToken<List<NewsArticle>> typeToken = new TypeToken<List<NewsArticle>>() {};
            List<NewsArticle> cachedArticles = cacheManager.get(cacheKey, typeToken);
            if (cachedArticles != null) {
                Log.d(TAG, "Using cached headlines for category: " + category);
                callback.onSuccess(cachedArticles);
                return;
            }
        }

        // Nếu không có cache hoặc cache đã hết hạn, gọi API
        Call<NewsResponse> call = apiService.getTopHeadlines(category, apiKey);
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsArticle> articles = response.body().getArticles();
                    if (articles != null && !articles.isEmpty()) {
                        // Lưu vào cache
                        cacheManager.put(cacheKey, articles);
                        Log.d(TAG, "Headlines fetched and cached for category: " + category);
                        callback.onSuccess(articles);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                } else {
                    handleApiError(response, cacheKey, callback);
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                handleNetworkError(t, cacheKey, callback);
            }
        });
    }

    private void handleApiError(Response<NewsResponse> response, String cacheKey, ApiCallback<List<NewsArticle>> callback) {
        String errorMessage = "Error " + response.code();
        try {
            if (response.errorBody() != null) {
                errorMessage += ": " + response.errorBody().string();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading error body", e);
        }
        Log.e(TAG, errorMessage);

        // Thử lấy từ cache khi có lỗi API
        TypeToken<List<NewsArticle>> typeToken = new TypeToken<List<NewsArticle>>() {};
        List<NewsArticle> cachedArticles = cacheManager.get(cacheKey, typeToken);
        if (cachedArticles != null) {
            Log.d(TAG, "Using expired cache due to API error");
            callback.onSuccess(cachedArticles);
        } else {
            callback.onError(errorMessage);
        }
    }

    private void handleNetworkError(Throwable t, String cacheKey, ApiCallback<List<NewsArticle>> callback) {
        String errorMessage = "Network Error: " + t.getMessage();
        Log.e(TAG, errorMessage, t);

        // Thử lấy từ cache khi có lỗi mạng
        TypeToken<List<NewsArticle>> typeToken = new TypeToken<List<NewsArticle>>() {};
        List<NewsArticle> cachedArticles = cacheManager.get(cacheKey, typeToken);
        if (cachedArticles != null) {
            Log.d(TAG, "Using expired cache due to network error");
            callback.onSuccess(cachedArticles);
        } else {
            callback.onError(errorMessage);
        }
    }

    // Interface để trả về kết quả
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}