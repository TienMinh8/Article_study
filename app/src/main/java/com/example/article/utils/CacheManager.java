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

public class CacheManager {
    private static final String TAG = "CacheManager";
    private static final String PREF_NAME = "api_cache";
    private static final String PREF_TIMESTAMP = "api_cache_timestamp";
    private static final long CACHE_EXPIRY = 10 * 60 * 1000; // 10 phút
    
    private static CacheManager instance;
    private final SharedPreferences cache;
    private final SharedPreferences cacheTimestamp;
    private final Gson gson;
    
    private CacheManager(Context context) {
        cache = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        cacheTimestamp = context.getSharedPreferences(PREF_TIMESTAMP, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    public static synchronized CacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new CacheManager(context.getApplicationContext());
        }
        return instance;
    }
    
    public <T> void put(String key, T data) {
        try {
            String json = gson.toJson(data);
            cache.edit().putString(key, json).apply();
            cacheTimestamp.edit().putLong(key, new Date().getTime()).apply();
            Log.d(TAG, "Cached data for key: " + key);
        } catch (Exception e) {
            Log.e(TAG, "Error caching data: " + e.getMessage());
        }
    }
    
    public <T> T get(String key, Class<T> type) {
        try {
            String json = cache.getString(key, null);
            if (json == null) return null;
            
            // Kiểm tra thời gian cache
            long timestamp = cacheTimestamp.getLong(key, 0);
            if (isExpired(timestamp)) {
                remove(key);
                return null;
            }
            
            return gson.fromJson(json, type);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving cached data: " + e.getMessage());
            return null;
        }
    }
    
    public <T> T get(String key, TypeToken<T> typeToken) {
        try {
            String json = cache.getString(key, null);
            if (json == null) return null;
            
            // Kiểm tra thời gian cache
            long timestamp = cacheTimestamp.getLong(key, 0);
            if (isExpired(timestamp)) {
                remove(key);
                return null;
            }
            
            Type type = typeToken.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving cached data: " + e.getMessage());
            return null;
        }
    }
    
    public void remove(String key) {
        cache.edit().remove(key).apply();
        cacheTimestamp.edit().remove(key).apply();
        Log.d(TAG, "Removed cache for key: " + key);
    }
    
    public void clear() {
        cache.edit().clear().apply();
        cacheTimestamp.edit().clear().apply();
        Log.d(TAG, "Cleared all cache");
    }
    
    private boolean isExpired(long timestamp) {
        return new Date().getTime() - timestamp > CACHE_EXPIRY;
    }
    
    public boolean hasValidCache(String key) {
        return cache.contains(key) && 
               !isExpired(cacheTimestamp.getLong(key, 0));
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
} 