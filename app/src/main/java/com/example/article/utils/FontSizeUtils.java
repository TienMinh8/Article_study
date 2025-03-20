package com.example.article.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Lớp tiện ích để quản lý kích thước chữ trong ứng dụng
 */
public class FontSizeUtils {

    private static final String PREF_NAME = "ArticlePrefs";
    private static final String KEY_FONT_SIZE = "font_size";
    
    public static final int FONT_SIZE_SMALL = 0;
    public static final int FONT_SIZE_MEDIUM = 1;
    public static final int FONT_SIZE_LARGE = 2;
    public static final int FONT_SIZE_EXTRA_LARGE = 3;
    
    /**
     * Lấy kích thước chữ từ SharedPreferences
     * @param context Context ứng dụng
     * @return Mã kích thước chữ (0: nhỏ, 1: vừa, 2: lớn, 3: rất lớn)
     */
    public static int getFontSize(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_FONT_SIZE, FONT_SIZE_MEDIUM);
    }
    
    /**
     * Lưu kích thước chữ vào SharedPreferences
     * @param context Context ứng dụng
     * @param fontSize Mã kích thước chữ (0: nhỏ, 1: vừa, 2: lớn, 3: rất lớn)
     */
    public static void saveFontSize(Context context, int fontSize) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_FONT_SIZE, fontSize).apply();
    }
    
    /**
     * Chuyển đổi kích thước chữ từ mã sang giá trị SP
     * @param context Context ứng dụng
     * @param baseSize Kích thước cơ bản (SP)
     * @return Kích thước chữ theo cài đặt người dùng (SP)
     */
    public static float getScaledFontSize(Context context, float baseSize) {
        int fontSize = getFontSize(context);
        float scaleFactor = 1.0f;
        
        switch (fontSize) {
            case FONT_SIZE_SMALL:
                scaleFactor = 0.85f;
                break;
            case FONT_SIZE_MEDIUM:
                scaleFactor = 1.0f;
                break;
            case FONT_SIZE_LARGE:
                scaleFactor = 1.2f;
                break;
            case FONT_SIZE_EXTRA_LARGE:
                scaleFactor = 1.4f;
                break;
        }
        
        return baseSize * scaleFactor;
    }
    
    /**
     * Áp dụng kích thước chữ đã điều chỉnh cho một giá trị
     * @param context Context ứng dụng
     * @param baseSize Kích thước cơ bản (SP)
     * @return Giá trị pixel đã điều chỉnh
     */
    public static int getAdjustedFontSizeInPixels(Context context, float baseSize) {
        float scaledSize = getScaledFontSize(context, baseSize);
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                scaledSize,
                context.getResources().getDisplayMetrics()
        );
    }
    
    /**
     * Tạo một TypedValue đã điều chỉnh theo cài đặt kích thước chữ
     * @param context Context ứng dụng
     * @param baseSize Kích thước cơ bản (SP)
     * @return TypedValue đã điều chỉnh
     */
    public static float getAdjustedTextSizeValue(Context context, float baseSize) {
        float scaledSize = getScaledFontSize(context, baseSize);
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                scaledSize,
                context.getResources().getDisplayMetrics()
        );
    }
} 