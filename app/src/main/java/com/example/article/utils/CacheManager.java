package com.example.article.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {
    private static final String TAG = "CacheManager";
    private static final String PREF_NAME = "news_cache";
    private static final String CACHE_TIMESTAMP_PREFIX = "timestamp_";
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5 phút
    private static final long OLD_CACHE_DURATION = 7 * 24 * 60 * 60 * 1000; // 7 ngày
    
    private static CacheManager instance;
    private final SharedPreferences preferences;
    private final Map<String, Object> memoryCache;
    
    private CacheManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        memoryCache = new ConcurrentHashMap<>();
    }
    
    public static CacheManager getInstance(Context context) {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new CacheManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }
    
    public void putString(String key, String value) {
        try {
            preferences.edit().putString(key, value).apply();
            preferences.edit().putLong(CACHE_TIMESTAMP_PREFIX + key, System.currentTimeMillis()).apply();
            memoryCache.put(key, value);
        } catch (Exception e) {
            Log.e(TAG, "Error caching string: " + e.getMessage());
        }
    }
    
    public String getString(String key) {
        try {
            // Kiểm tra trong memory cache trước
            Object cachedValue = memoryCache.get(key);
            if (cachedValue instanceof String) {
                return (String) cachedValue;
            }
            
            // Nếu không có trong memory cache, lấy từ SharedPreferences
            String value = preferences.getString(key, null);
            if (value != null) {
                // Kiểm tra thời gian cache
                long timestamp = preferences.getLong(CACHE_TIMESTAMP_PREFIX + key, 0);
                if (System.currentTimeMillis() - timestamp < CACHE_DURATION) {
                    memoryCache.put(key, value);
                    return value;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting cached string: " + e.getMessage());
        }
        return null;
    }
    
    public void put(String key, Object value) {
        try {
            String jsonValue = new Gson().toJson(value);
            preferences.edit().putString(key, jsonValue).apply();
            preferences.edit().putLong(CACHE_TIMESTAMP_PREFIX + key, System.currentTimeMillis()).apply();
            memoryCache.put(key, value);
        } catch (Exception e) {
            Log.e(TAG, "Error caching object: " + e.getMessage());
        }
    }
    
    public <T> T get(String key, TypeToken<T> typeToken) {
        try {
            // Kiểm tra trong memory cache trước
            Object cachedValue = memoryCache.get(key);
            if (cachedValue != null && typeToken.getRawType().isInstance(cachedValue)) {
                return (T) cachedValue;
            }
            
            // Nếu không có trong memory cache, lấy từ SharedPreferences
            String jsonValue = preferences.getString(key, null);
            if (jsonValue != null) {
                // Kiểm tra thời gian cache
                long timestamp = preferences.getLong(CACHE_TIMESTAMP_PREFIX + key, 0);
                if (System.currentTimeMillis() - timestamp < CACHE_DURATION) {
                    T value = new Gson().fromJson(jsonValue, typeToken.getType());
                    memoryCache.put(key, value);
                    return value;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting cached object: " + e.getMessage());
        }
        return null;
    }
    
    public boolean hasValidCache(String key) {
        try {
            long timestamp = preferences.getLong(CACHE_TIMESTAMP_PREFIX + key, 0);
            return System.currentTimeMillis() - timestamp < CACHE_DURATION;
        } catch (Exception e) {
            Log.e(TAG, "Error checking cache validity: " + e.getMessage());
            return false;
        }
    }
    
    public void clearCache() {
        try {
            preferences.edit().clear().apply();
            memoryCache.clear();
        } catch (Exception e) {
            Log.e(TAG, "Error clearing cache: " + e.getMessage());
        }
    }
    
    public void remove(String key) {
        try {
            preferences.edit().remove(key).apply();
            preferences.edit().remove(CACHE_TIMESTAMP_PREFIX + key).apply();
            memoryCache.remove(key);
        } catch (Exception e) {
            Log.e(TAG, "Error removing cache: " + e.getMessage());
        }
    }
    
    public static String generateCacheKey(String... params) {
        StringBuilder key = new StringBuilder();
        for (String param : params) {
            if (param != null) {
                key.append(param).append("_");
            }
        }
        return key.toString().toLowerCase();
    }
    
    /**
     * Xóa tất cả dữ liệu đã cache
     */
    public void clearAll() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        memoryCache.clear();
        Log.d(TAG, "All cached data cleared");
    }
} 