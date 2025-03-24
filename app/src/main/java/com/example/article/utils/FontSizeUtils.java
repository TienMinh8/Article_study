package com.example.article.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Lớp tiện ích để quản lý kích thước chữ trong ứng dụng
 */
public class FontSizeUtils {

    private static final String PREF_NAME = "font_settings";
    private static final String KEY_FONT_SIZE = "font_size";
    private static final String KEY_TITLE_SIZE = "title_size";
    private static final String KEY_CONTENT_SIZE = "content_size";
    public static final String ACTION_FONT_SIZE_CHANGED = "com.example.article.FONT_SIZE_CHANGED";
    
    // Font size levels
    public static final int FONT_SIZE_SMALL = 0;
    public static final int FONT_SIZE_MEDIUM = 1;
    public static final int FONT_SIZE_LARGE = 2;
    public static final int FONT_SIZE_EXTRA_LARGE = 3;

    // Base sizes (in SP)
    public static final float BASE_TITLE_SIZE = 20f;
    public static final float BASE_SUBTITLE_SIZE = 18f;
    public static final float BASE_BODY_SIZE = 16f;
    public static final float BASE_CAPTION_SIZE = 14f;
    
    // Scale factors for different font size levels
    private static final float SCALE_SMALL = 0.85f;
    private static final float SCALE_MEDIUM = 1.0f;
    private static final float SCALE_LARGE = 1.5f;
    private static final float SCALE_EXTRA_LARGE = 2.0f;

    // Kích thước mặc định (SP)
    private static final float DEFAULT_TITLE_SIZE = 20f;
    private static final float DEFAULT_CONTENT_SIZE = 16f;

    // Kích thước tối thiểu và tối đa (SP)
    private static final float MIN_TITLE_SIZE = 16f;
    private static final float MAX_TITLE_SIZE = 28f;
    private static final float MIN_CONTENT_SIZE = 12f;
    private static final float MAX_CONTENT_SIZE = 24f;

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Lấy kích thước chữ từ SharedPreferences
     * @param context Context ứng dụng
     * @return Mã kích thước chữ (0: nhỏ, 1: vừa, 2: lớn, 3: rất lớn)
     */
    public static int getFontSize(Context context) {
        return getPrefs(context).getInt(KEY_FONT_SIZE, FONT_SIZE_MEDIUM);
    }
    
    /**
     * Lưu kích thước chữ vào SharedPreferences và thông báo thay đổi
     * @param context Context ứng dụng
     * @param fontSize Mã kích thước chữ (0: nhỏ, 1: vừa, 2: lớn, 3: rất lớn)
     */
    public static void saveFontSize(Context context, int fontSize) {
        int oldSize = getFontSize(context);
        
        if (oldSize != fontSize) {
            getPrefs(context).edit().putInt(KEY_FONT_SIZE, fontSize).apply();
            notifyFontSizeChanged(context);
        }
    }
    
    /**
     * Chuyển đổi kích thước chữ từ mã sang giá trị SP
     * @param context Context ứng dụng
     * @param baseSize Kích thước cơ bản (SP)
     * @return Kích thước chữ theo cài đặt người dùng (SP)
     */
    public static float getScaledSize(Context context, float baseSize) {
        int fontSize = getFontSize(context);
        float scaleFactor;
        
        switch (fontSize) {
            case FONT_SIZE_SMALL:
                scaleFactor = SCALE_SMALL;
                break;
            case FONT_SIZE_LARGE:
                scaleFactor = SCALE_LARGE;
                break;
            case FONT_SIZE_EXTRA_LARGE:
                scaleFactor = SCALE_EXTRA_LARGE;
                break;
            default: // FONT_SIZE_MEDIUM
                scaleFactor = SCALE_MEDIUM;
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
        float scaledSize = getScaledSize(context, baseSize);
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
        float scaledSize = getScaledSize(context, baseSize);
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                scaledSize,
                context.getResources().getDisplayMetrics()
        );
    }

    public static float getTitleTextSize(Context context) {
        return getScaledSize(context, BASE_TITLE_SIZE);
    }

    public static float getContentTextSize(Context context) {
        return getScaledSize(context, BASE_BODY_SIZE);
    }

    public static float getSubtitleTextSize(Context context) {
        return getScaledSize(context, BASE_SUBTITLE_SIZE);
    }

    public static float getCaptionTextSize(Context context) {
        return getScaledSize(context, BASE_CAPTION_SIZE);
    }

    public static void setTitleTextSize(Context context, float size) {
        // Đảm bảo kích thước nằm trong giới hạn
        size = Math.max(MIN_TITLE_SIZE, Math.min(size, MAX_TITLE_SIZE));
        getPrefs(context).edit().putFloat(KEY_TITLE_SIZE, size).apply();
    }

    public static void setContentTextSize(Context context, float size) {
        // Đảm bảo kích thước nằm trong giới hạn
        size = Math.max(MIN_CONTENT_SIZE, Math.min(size, MAX_CONTENT_SIZE));
        getPrefs(context).edit().putFloat(KEY_CONTENT_SIZE, size).apply();
    }

    public static void increaseTitleSize(Context context) {
        float currentSize = getTitleTextSize(context);
        setTitleTextSize(context, currentSize + 2);
    }

    public static void decreaseTitleSize(Context context) {
        float currentSize = getTitleTextSize(context);
        setTitleTextSize(context, currentSize - 2);
    }

    public static void increaseContentSize(Context context) {
        float currentSize = getContentTextSize(context);
        setContentTextSize(context, currentSize + 2);
    }

    public static void decreaseContentSize(Context context) {
        float currentSize = getContentTextSize(context);
        setContentTextSize(context, currentSize - 2);
    }

    public static boolean canIncreaseTitleSize(Context context) {
        return getTitleTextSize(context) < MAX_TITLE_SIZE;
    }

    public static boolean canDecreaseTitleSize(Context context) {
        return getTitleTextSize(context) > MIN_TITLE_SIZE;
    }

    public static boolean canIncreaseContentSize(Context context) {
        return getContentTextSize(context) < MAX_CONTENT_SIZE;
    }

    public static boolean canDecreaseContentSize(Context context) {
        return getContentTextSize(context) > MIN_CONTENT_SIZE;
    }

    private static void notifyFontSizeChanged(Context context) {
        Intent intent = new Intent(ACTION_FONT_SIZE_CHANGED);
        context.sendBroadcast(intent);
    }
} 