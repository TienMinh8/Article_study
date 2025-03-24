package com.example.article.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

public class AccessibilityUtils {
    private static final String PREFS_NAME = "accessibility_preferences";
    private static final String KEY_SCREEN_READER = "screen_reader_enabled";
    private static final String KEY_SIMPLIFIED_LAYOUT = "simplified_layout_enabled";
    private static final String KEY_REDUCED_MOTION = "reduced_motion_enabled";
    private static final String KEY_REDUCED_TRANSPARENCY = "reduced_transparency_enabled";
    private static final String KEY_LARGE_TOUCH_TARGETS = "large_touch_targets_enabled";

    private final Context context;
    private final SharedPreferences prefs;

    public AccessibilityUtils(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void applyAccessibilityFeatures(ViewGroup rootView) {
        if (rootView == null) return;

        // Áp dụng mô tả cho trình đọc màn hình
        applyScreenReaderSupport(rootView);

        // Áp dụng bố cục đơn giản hóa
        if (isSimplifiedLayoutEnabled()) {
            applySimplifiedLayout(rootView);
        }

        // Áp dụng giảm chuyển động
        if (isReducedMotionEnabled()) {
            applyReducedMotion(rootView);
        }

        // Áp dụng giảm độ trong suốt
        if (isReducedTransparencyEnabled()) {
            applyReducedTransparency(rootView);
        }

        // Áp dụng vùng chạm lớn
        if (isLargeTouchTargetsEnabled()) {
            applyLargeTouchTargets(rootView);
        }

        // Áp dụng tối ưu điều hướng
        optimizeNavigation(rootView);

        // Áp dụng hỗ trợ màn hình khác nhau
        applyScreenSizeSupport(rootView);
    }

    private void applyScreenReaderSupport(View view) {
        if (view == null) return;

        ViewCompat.setAccessibilityDelegate(view, new AccessibilityDelegateCompat() {
            @Override
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                
                // Thêm mô tả cho các phần tử
                if (host instanceof ViewGroup) {
                    info.setClassName(ViewGroup.class.getName());
                }
                
                // Đảm bảo thứ tự đọc logic
                info.setTraversalBefore(null);
                info.setTraversalAfter(null);
                
                // Thêm phản hồi âm thanh
                info.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
            }
        });

        // Áp dụng cho các view con
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyScreenReaderSupport(group.getChildAt(i));
            }
        }
    }

    private void applySimplifiedLayout(ViewGroup rootView) {
        if (rootView == null) return;

        // Tăng khoảng cách giữa các phần tử
        ViewGroup.LayoutParams params = rootView.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
            marginParams.setMargins(24, 24, 24, 24);
        }

        // Áp dụng cho các view con
        for (int i = 0; i < rootView.getChildCount(); i++) {
            View child = rootView.getChildAt(i);
            if (child instanceof ViewGroup) {
                applySimplifiedLayout((ViewGroup) child);
            }
        }
    }

    private void applyReducedMotion(ViewGroup rootView) {
        if (rootView == null) return;

        // Tắt các hiệu ứng chuyển động
        rootView.setAnimation(null);

        // Áp dụng cho các view con
        for (int i = 0; i < rootView.getChildCount(); i++) {
            View child = rootView.getChildAt(i);
            if (child instanceof ViewGroup) {
                applyReducedMotion((ViewGroup) child);
            }
        }
    }

    private void applyReducedTransparency(ViewGroup rootView) {
        if (rootView == null) return;

        // Tăng độ đục của các view
        rootView.setAlpha(1.0f);

        // Áp dụng cho các view con
        for (int i = 0; i < rootView.getChildCount(); i++) {
            View child = rootView.getChildAt(i);
            if (child instanceof ViewGroup) {
                applyReducedTransparency((ViewGroup) child);
            }
        }
    }

    private void applyLargeTouchTargets(View view) {
        if (view == null) return;

        // Đảm bảo kích thước tối thiểu 48dp x 48dp
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            int minSize = (int) (48 * context.getResources().getDisplayMetrics().density);
            params.width = Math.max(params.width, minSize);
            params.height = Math.max(params.height, minSize);
        }

        // Áp dụng cho các view con
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyLargeTouchTargets(group.getChildAt(i));
            }
        }
    }

    private void optimizeNavigation(ViewGroup rootView) {
        if (rootView == null) return;

        // Thêm nút quay lại dễ thấy
        View backButton = findBackButton(rootView);
        if (backButton != null) {
            ViewCompat.setAccessibilityDelegate(backButton, new AccessibilityDelegateCompat() {
                @Override
                public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
                    super.onInitializeAccessibilityNodeInfo(host, info);
                    info.setContentDescription("Quay lại");
                    info.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                }
            });
        }

        // Tối ưu thứ tự điều hướng
        for (int i = 0; i < rootView.getChildCount(); i++) {
            View child = rootView.getChildAt(i);
            if (child instanceof ViewGroup) {
                optimizeNavigation((ViewGroup) child);
            }
        }
    }

    private View findBackButton(ViewGroup rootView) {
        for (int i = 0; i < rootView.getChildCount(); i++) {
            View child = rootView.getChildAt(i);
            if (child.getId() == android.R.id.button1 || 
                "back_button".equals(child.getTag())) {
                return child;
            }
            if (child instanceof ViewGroup) {
                View found = findBackButton((ViewGroup) child);
                if (found != null) return found;
            }
        }
        return null;
    }

    private void applyScreenSizeSupport(ViewGroup rootView) {
        if (rootView == null) return;

        Configuration config = context.getResources().getConfiguration();
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        // Điều chỉnh layout dựa trên kích thước màn hình
        ViewGroup.LayoutParams params = rootView.getLayoutParams();
        if (params != null) {
            // Đảm bảo không bị cuộn ngang
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            
            // Tối ưu cho màn hình nhỏ
            if (screenWidth < 600) {
                applySmallScreenOptimizations(rootView);
            }
        }

        // Áp dụng cho các view con
        for (int i = 0; i < rootView.getChildCount(); i++) {
            View child = rootView.getChildAt(i);
            if (child instanceof ViewGroup) {
                applyScreenSizeSupport((ViewGroup) child);
            }
        }
    }

    private void applySmallScreenOptimizations(ViewGroup view) {
        // Tăng padding cho màn hình nhỏ
        int padding = (int) (16 * context.getResources().getDisplayMetrics().density);
        view.setPadding(padding, padding, padding, padding);

        // Điều chỉnh margin
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
            marginParams.setMargins(padding, padding, padding, padding);
        }
    }

    public boolean isScreenReaderEnabled() {
        return prefs.getBoolean(KEY_SCREEN_READER, false);
    }

    public void setScreenReaderEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SCREEN_READER, enabled).apply();
    }

    public boolean isSimplifiedLayoutEnabled() {
        return prefs.getBoolean(KEY_SIMPLIFIED_LAYOUT, false);
    }

    public void setSimplifiedLayoutEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SIMPLIFIED_LAYOUT, enabled).apply();
    }

    public boolean isReducedMotionEnabled() {
        return prefs.getBoolean(KEY_REDUCED_MOTION, false);
    }

    public void setReducedMotionEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_REDUCED_MOTION, enabled).apply();
    }

    public boolean isReducedTransparencyEnabled() {
        return prefs.getBoolean(KEY_REDUCED_TRANSPARENCY, false);
    }

    public void setReducedTransparencyEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_REDUCED_TRANSPARENCY, enabled).apply();
    }

    public boolean isLargeTouchTargetsEnabled() {
        return prefs.getBoolean(KEY_LARGE_TOUCH_TARGETS, true);
    }

    public void setLargeTouchTargetsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_LARGE_TOUCH_TARGETS, enabled).apply();
    }
} 