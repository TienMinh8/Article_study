package com.example.article.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Utility class for handling theme-related functionality
 */
public class ThemeUtils {
    private static final String PREFS_NAME = "article_preferences";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_FONT_SIZE = "font_size";
    private static final String KEY_HIGH_CONTRAST = "high_contrast";
    
    // Theme mode constants
    public static final int MODE_SYSTEM = 0;  // Follow system
    public static final int MODE_LIGHT = 1;   // Light mode
    public static final int MODE_DARK = 2;    // Dark mode
    
    // Font size constants
    public static final int FONT_SIZE_SMALL = 0;
    public static final int FONT_SIZE_MEDIUM = 1;
    public static final int FONT_SIZE_LARGE = 2;
    
    /**
     * Apply the theme based on saved preferences
     */
    public static void applyAppTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int themeMode = prefs.getInt(KEY_THEME_MODE, MODE_SYSTEM);
        
        switch (themeMode) {
            case MODE_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case MODE_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                // Follow system
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
    
    /**
     * Save theme mode preference
     */
    public static void saveThemeMode(Context context, int themeMode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME_MODE, themeMode).apply();
        applyAppTheme(context);
    }
    
    /**
     * Get current theme mode preference
     */
    public static int getThemeMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME_MODE, MODE_SYSTEM);
    }
    
    /**
     * Check if device is currently in dark mode
     */
    public static boolean isNightMode(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & 
                Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
    
    /**
     * Save font size preference
     */
    public static void saveFontSize(Context context, int fontSize) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_FONT_SIZE, fontSize).apply();
    }
    
    /**
     * Get current font size preference
     */
    public static int getFontSize(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_FONT_SIZE, FONT_SIZE_MEDIUM);
    }
    
    /**
     * Save high contrast mode preference
     */
    public static void saveHighContrastMode(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_HIGH_CONTRAST, enabled).apply();
    }
    
    /**
     * Get high contrast mode preference
     */
    public static boolean getHighContrastMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_HIGH_CONTRAST, false);
    }
    
    /**
     * Áp dụng kích thước chữ đã điều chỉnh cho TextView
     * @param context Context ứng dụng
     * @param textView TextView cần điều chỉnh
     * @param baseSize Kích thước cơ bản (SP)
     */
    public static void applyAdjustedTextSize(Context context, TextView textView, float baseSize) {
        float scaledSize = FontSizeUtils.getScaledFontSize(context, baseSize);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);
    }
} 