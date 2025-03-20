package com.example.article.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Lớp tùy chỉnh để thiết lập khoảng cách giữa các item trong RecyclerView
 */
public class ItemSpacingDecoration extends RecyclerView.ItemDecoration {
    private final int spacing;

    /**
     * Khởi tạo với khoảng cách được chỉ định (đơn vị pixels)
     * @param spacing khoảng cách giữa các item tính theo pixels
     */
    public ItemSpacingDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        
        // Áp dụng khoảng cách cho tất cả các cạnh ngoại trừ cạnh trên của item đầu tiên
        if (position != 0) {
            outRect.top = spacing;
        }
        
        outRect.left = spacing;
        outRect.right = spacing;
        outRect.bottom = spacing;
    }
} 