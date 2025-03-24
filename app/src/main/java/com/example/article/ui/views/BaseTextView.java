package com.example.article.ui.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.article.R;
import com.example.article.utils.FontSizeUtils;

/**
 * TextView tùy chỉnh tự động điều chỉnh kích thước chữ theo cài đặt người dùng
 */
public class BaseTextView extends AppCompatTextView {
    public static final int TYPE_TITLE = 0;
    public static final int TYPE_SUBTITLE = 1;
    public static final int TYPE_BODY = 2;
    public static final int TYPE_CAPTION = 3;

    private int textType;
    private float baseTextSize;
    private BroadcastReceiver fontSizeReceiver;
    private boolean isReceiverRegistered = false;

    public BaseTextView(Context context) {
        super(context);
        init(null);
    }

    public BaseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BaseTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BaseTextView);
            try {
                textType = a.getInt(R.styleable.BaseTextView_textType, TYPE_BODY);
            } finally {
                a.recycle();
            }
        } else {
            textType = TYPE_BODY;
        }

        // Lấy kích thước chữ cơ bản dựa trên loại
        switch (textType) {
            case TYPE_TITLE:
                baseTextSize = FontSizeUtils.BASE_TITLE_SIZE;
                break;
            case TYPE_SUBTITLE:
                baseTextSize = FontSizeUtils.BASE_SUBTITLE_SIZE;
                break;
            case TYPE_CAPTION:
                baseTextSize = FontSizeUtils.BASE_CAPTION_SIZE;
                break;
            default:
                baseTextSize = FontSizeUtils.BASE_BODY_SIZE;
        }

        // Khởi tạo receiver để lắng nghe thay đổi font size
        fontSizeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (FontSizeUtils.ACTION_FONT_SIZE_CHANGED.equals(intent.getAction())) {
                    updateTextSize();
                }
            }
        };

        // Áp dụng kích thước chữ ban đầu
        updateTextSize();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Đăng ký receiver khi view được gắn vào window
        if (fontSizeReceiver != null && !isReceiverRegistered) {
            try {
                getContext().registerReceiver(fontSizeReceiver, 
                    new IntentFilter(FontSizeUtils.ACTION_FONT_SIZE_CHANGED));
                isReceiverRegistered = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Hủy đăng ký receiver khi view bị gỡ khỏi window
        if (fontSizeReceiver != null && isReceiverRegistered) {
            try {
                getContext().unregisterReceiver(fontSizeReceiver);
                isReceiverRegistered = false;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cập nhật kích thước chữ dựa trên cài đặt người dùng
     */
    public void updateTextSize() {
        try {
            float adjustedSize = FontSizeUtils.getScaledSize(getContext(), baseTextSize);
            setTextSize(TypedValue.COMPLEX_UNIT_SP, adjustedSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Thiết lập loại văn bản và cập nhật kích thước
     */
    public void setTextType(int type) {
        this.textType = type;
        switch (type) {
            case TYPE_TITLE:
                baseTextSize = FontSizeUtils.BASE_TITLE_SIZE;
                break;
            case TYPE_SUBTITLE:
                baseTextSize = FontSizeUtils.BASE_SUBTITLE_SIZE;
                break;
            case TYPE_CAPTION:
                baseTextSize = FontSizeUtils.BASE_CAPTION_SIZE;
                break;
            default:
                baseTextSize = FontSizeUtils.BASE_BODY_SIZE;
        }
        updateTextSize();
    }

    /**
     * Lấy kích thước chữ cơ bản hiện tại
     */
    public float getBaseTextSize() {
        return baseTextSize;
    }
} 