package com.example.article.ui.transform;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.viewpager2.widget.ViewPager2;

public class MarginPageTransformer implements ViewPager2.PageTransformer {
    private final int marginPx;

    public MarginPageTransformer(int marginPx) {
        this.marginPx = marginPx;
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        ViewPager2 viewPager = (ViewPager2) page.getParent().getParent();
        
        int width = viewPager.getWidth();
        float offset = marginPx / 2f;
        
        if (position < -1) {
            // Page is far off-screen to the left
            page.setTranslationX(-offset);
        } else if (position <= 1) {
            // Page is visible or becoming visible
            // Counteract the default slide transition
            float scaleFactor = Math.max(0.8f, 1 - Math.abs(position * 0.2f));
            
            // Move it up a bit
            float verticalOffset = position < 0 
                ? 0f 
                : 20 * position;
            
            // Scale the page down (between 0.8 and 1)
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            
            // Shift the page, taking into account the scale factor
            float horizontalMargin = width * (1 - scaleFactor) / 2;
            if (position < 0) {
                page.setTranslationX(horizontalMargin - offset);
            } else {
                page.setTranslationX(-horizontalMargin + offset);
            }
            
            page.setTranslationY(-verticalOffset);
        } else {
            // Page is far off-screen to the right
            page.setTranslationX(offset);
        }
    }
} 