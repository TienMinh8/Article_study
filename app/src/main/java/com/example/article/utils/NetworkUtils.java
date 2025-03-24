package com.example.article.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000; // 1 second
    private static final long CACHE_SIZE = 50 * 1024 * 1024; // 50MB cache
    private static final int CONNECT_TIMEOUT = 10; // 10 giây cho kết nối
    private static final int READ_TIMEOUT = 15; // 15 giây cho đọc dữ liệu
    private static final int WRITE_TIMEOUT = 15; // 15 giây cho ghi dữ liệu
    private static final int MAX_STALE = 7; // 7 ngày cho cache cũ
    private static final int MAX_AGE = 5; // 5 phút cho cache mới
    
    private static OkHttpClient client;
    private static Context appContext;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
        setupOkHttpClient();
    }

    private static void setupOkHttpClient() {
        File cacheDir = new File(appContext.getCacheDir(), "http-cache");
        Cache cache = new Cache(cacheDir, CACHE_SIZE);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(provideCacheInterceptor())
            .addNetworkInterceptor(provideCacheInterceptor())
            .addInterceptor(provideOfflineCacheInterceptor())
            .addInterceptor(provideRetryInterceptor());

        client = builder.build();
    }

    private static Interceptor provideCacheInterceptor() {
        return chain -> {
            Response response = chain.proceed(chain.request());

            CacheControl cacheControl = new CacheControl.Builder()
                .maxAge(MAX_AGE, TimeUnit.MINUTES)
                .build();

            return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", cacheControl.toString())
                .build();
        };
    }

    private static Interceptor provideOfflineCacheInterceptor() {
        return chain -> {
            Request request = chain.request();

            if (!isNetworkAvailable()) {
                CacheControl cacheControl = new CacheControl.Builder()
                    .maxStale(MAX_STALE, TimeUnit.DAYS)
                    .build();

                request = request.newBuilder()
                    .cacheControl(cacheControl)
                    .build();
                
                Log.d(TAG, "No network, using cache with max stale " + MAX_STALE + " days");
            }

            return chain.proceed(request);
        };
    }

    private static Interceptor provideRetryInterceptor() {
        return chain -> {
            Request request = chain.request();
            Response response = null;
            IOException exception = null;
            int tryCount = 0;

            while (tryCount < MAX_RETRIES) {
                try {
                    if (tryCount > 0) {
                        // Tăng thời gian delay theo số lần retry
                        Thread.sleep(RETRY_DELAY_MS * tryCount);
                    }
                    response = chain.proceed(request);
                    if (response.isSuccessful()) {
                        return response;
                    }
                } catch (IOException e) {
                    exception = e;
                    Log.w(TAG, "Attempt " + (tryCount + 1) + " failed: " + e.getMessage());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Retry interrupted", e);
                } finally {
                    if (response != null && !response.isSuccessful()) {
                        response.close();
                    }
                }
                tryCount++;
            }

            // Nếu vẫn thất bại sau khi retry, thử lấy từ cache
            if (!isNetworkAvailable()) {
                Log.d(TAG, "Network unavailable, trying to get from cache");
                Request cacheRequest = request.newBuilder()
                    .cacheControl(new CacheControl.Builder()
                        .maxStale(MAX_STALE, TimeUnit.DAYS)
                        .build())
                    .build();
                try {
                    response = chain.proceed(cacheRequest);
                    if (response.isSuccessful()) {
                        return response;
                    }
                } catch (IOException e) {
                    exception = e;
                }
            }

            // Nếu không lấy được từ cache, throw exception
            throw exception != null ? exception : new IOException("Request failed after " + MAX_RETRIES + " retries");
        };
    }

    /**
     * Kiểm tra xem thiết bị có kết nối internet không
     * @param context Context của ứng dụng
     * @return true nếu có kết nối internet, false nếu không
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities == null) {
                return false;
            }
            
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || 
                   capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || 
                   capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    public static OkHttpClient getClient() {
        if (client == null) {
            setupOkHttpClient();
        }
        return client;
    }

    public static void showNetworkError(Context context, String message) {
        Toast.makeText(context, 
            message != null ? message : "Không thể kết nối đến máy chủ. Vui lòng thử lại sau.", 
            Toast.LENGTH_SHORT).show();
    }

    public static <T> T executeWithRetry(NetworkCall<T> call) throws IOException {
        IOException lastException = null;
        
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                retrofit2.Response<T> response = call.execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                // Nếu response không thành công, thử lại
                lastException = new IOException("API error: " + response.code());
            } catch (IOException e) {
                lastException = e;
            }

            // Chờ trước khi thử lại
            if (i < MAX_RETRIES - 1) {
                try {
                    Thread.sleep(RETRY_DELAY_MS * (i + 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Retry interrupted", ie);
                }
            }
        }

        throw lastException != null ? lastException : 
            new IOException("Unknown error after " + MAX_RETRIES + " retries");
    }

    public interface NetworkCall<T> {
        retrofit2.Response<T> execute() throws IOException;
    }

    public static boolean shouldRetry(Throwable throwable) {
        return throwable instanceof IOException ||
               throwable.getMessage().contains("Connection reset") ||
               throwable.getMessage().contains("timeout");
    }

    public static boolean isNetworkAvailable() {
        if (appContext == null) return false;
        
        ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void clearCache() {
        if (client != null && client.cache() != null) {
            try {
                client.cache().evictAll();
                Log.d(TAG, "Cache cleared successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing cache: " + e.getMessage());
            }
        }
    }
} 