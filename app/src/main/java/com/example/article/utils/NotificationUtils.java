package com.example.article.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NotificationUtils {
    private static final String NOTIFICATION_PREF = "notification_pref";
    private static final String BREAKING_NEWS_KEY = "breaking_news_key";
    private static final String DAILY_DIGEST_KEY = "daily_digest_key";
    private static final String SAVED_ARTICLES_KEY = "saved_articles_key";

    public static void setBreakingNewsEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(BREAKING_NEWS_KEY, enabled).apply();
    }

    public static boolean isBreakingNewsEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(BREAKING_NEWS_KEY, true); // Default to true
    }

    public static void setDailyDigestEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(DAILY_DIGEST_KEY, enabled).apply();
    }

    public static boolean isDailyDigestEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(DAILY_DIGEST_KEY, true); // Default to true
    }

    public static void setSavedArticlesEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(SAVED_ARTICLES_KEY, enabled).apply();
    }

    public static boolean isSavedArticlesEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(SAVED_ARTICLES_KEY, true); // Default to true
    }
} 