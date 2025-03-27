package com.example.article.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LanguageUtils {
    private static final String LANGUAGE_PREF = "language_pref";
    private static final String LANGUAGE_KEY = "language_key";

    public static void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Save language preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(LANGUAGE_KEY, languageCode).apply();
    }

    public static String getCurrentLanguage(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(LANGUAGE_KEY, "en"); // Default to English
    }

    public static void applySavedLanguage(Context context) {
        String savedLanguage = getCurrentLanguage(context);
        setLocale(context, savedLanguage);
    }
} 