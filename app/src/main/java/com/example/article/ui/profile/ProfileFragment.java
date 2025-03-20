package com.example.article.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.article.R;
import com.example.article.utils.ThemeUtils;

public class ProfileFragment extends Fragment {

    private RadioGroup themeRadioGroup;
    private RadioGroup fontSizeRadioGroup;
    private Switch highContrastSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        
        // Khởi tạo các control
        initializeViews(root);
        
        // Thiết lập event listener
        setupListeners();
        
        // Áp dụng cài đặt hiện tại
        loadCurrentSettings();
        
        return root;
    }
    
    private void initializeViews(View root) {
        // Thiết lập nút thông báo
        ImageButton btnNotification = root.findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(v -> {
            Toast.makeText(getContext(), R.string.tab_notifications, Toast.LENGTH_SHORT).show();
        });
        
        // RadioGroup cho theme
        themeRadioGroup = root.findViewById(R.id.themeRadioGroup);
        
        // RadioGroup cho font size
        fontSizeRadioGroup = root.findViewById(R.id.fontSizeRadioGroup);
        
        // Switch cho high contrast mode
        highContrastSwitch = root.findViewById(R.id.highContrastSwitch);
    }
    
    private void setupListeners() {
        // Listener cho thay đổi theme
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int themeMode;
            if (checkedId == R.id.radioSystem) {
                themeMode = ThemeUtils.MODE_SYSTEM;
            } else if (checkedId == R.id.radioLight) {
                themeMode = ThemeUtils.MODE_LIGHT;
            } else {
                themeMode = ThemeUtils.MODE_DARK;
            }
            ThemeUtils.saveThemeMode(requireContext(), themeMode);
        });
        
        // Listener cho thay đổi font size
        fontSizeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int fontSize;
            if (checkedId == R.id.radioSmall) {
                fontSize = ThemeUtils.FONT_SIZE_SMALL;
            } else if (checkedId == R.id.radioMedium) {
                fontSize = ThemeUtils.FONT_SIZE_MEDIUM;
            } else {
                fontSize = ThemeUtils.FONT_SIZE_LARGE;
            }
            ThemeUtils.saveFontSize(requireContext(), fontSize);
            Toast.makeText(getContext(), R.string.restart_to_apply_font_size, Toast.LENGTH_SHORT).show();
        });
        
        // Listener cho high contrast mode
        highContrastSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeUtils.saveHighContrastMode(requireContext(), isChecked);
            Toast.makeText(getContext(), R.string.restart_to_apply_high_contrast, Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadCurrentSettings() {
        // Áp dụng cài đặt theme hiện tại
        int themeMode = ThemeUtils.getThemeMode(requireContext());
        switch (themeMode) {
            case ThemeUtils.MODE_LIGHT:
                themeRadioGroup.check(R.id.radioLight);
                break;
            case ThemeUtils.MODE_DARK:
                themeRadioGroup.check(R.id.radioDark);
                break;
            default:
                themeRadioGroup.check(R.id.radioSystem);
                break;
        }
        
        // Áp dụng cài đặt font size hiện tại
        int fontSize = ThemeUtils.getFontSize(requireContext());
        switch (fontSize) {
            case ThemeUtils.FONT_SIZE_SMALL:
                fontSizeRadioGroup.check(R.id.radioSmall);
                break;
            case ThemeUtils.FONT_SIZE_LARGE:
                fontSizeRadioGroup.check(R.id.radioLarge);
                break;
            default:
                fontSizeRadioGroup.check(R.id.radioMedium);
                break;
        }
        
        // Áp dụng cài đặt high contrast hiện tại
        boolean highContrast = ThemeUtils.getHighContrastMode(requireContext());
        highContrastSwitch.setChecked(highContrast);
    }
} 