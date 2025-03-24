package com.example.article.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.example.article.api.model.NewsArticle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ArticleUtils {
    private static final String PREFS_NAME = "saved_articles";
    private static final String KEY_SAVED_ARTICLES = "articles";
    private static final Gson gson = new Gson();

    // Lưu bài viết
    public static void saveArticle(Context context, NewsArticle article) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        List<NewsArticle> savedArticles = getSavedArticles(context);
        
        // Kiểm tra xem bài viết đã tồn tại chưa
        boolean exists = false;
        for (NewsArticle savedArticle : savedArticles) {
            if (savedArticle.getUrl() != null && savedArticle.getUrl().equals(article.getUrl())) {
                exists = true;
                break;
            }
        }
        
        // Nếu chưa tồn tại thì thêm vào
        if (!exists) {
            savedArticles.add(article);
            String json = gson.toJson(savedArticles);
            prefs.edit().putString(KEY_SAVED_ARTICLES, json).apply();
        }
    }

    // Xóa bài viết đã lưu
    public static void unsaveArticle(Context context, NewsArticle article) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        List<NewsArticle> savedArticles = getSavedArticles(context);
        
        // Tìm và xóa bài viết khỏi danh sách
        savedArticles.removeIf(savedArticle -> 
            savedArticle.getUrl() != null && savedArticle.getUrl().equals(article.getUrl())
        );
        
        // Lưu lại danh sách đã cập nhật
        String json = gson.toJson(savedArticles);
        prefs.edit().putString(KEY_SAVED_ARTICLES, json).apply();
    }

    // Kiểm tra xem bài viết đã được lưu chưa
    public static boolean isArticleSaved(Context context, NewsArticle article) {
        List<NewsArticle> savedArticles = getSavedArticles(context);
        
        for (NewsArticle savedArticle : savedArticles) {
            if (savedArticle.getUrl() != null && savedArticle.getUrl().equals(article.getUrl())) {
                return true;
            }
        }
        return false;
    }

    // Lấy danh sách bài viết đã lưu
    public static List<NewsArticle> getSavedArticles(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_SAVED_ARTICLES, null);
        
        if (json != null) {
            Type type = new TypeToken<List<NewsArticle>>(){}.getType();
            try {
                List<NewsArticle> savedArticles = gson.fromJson(json, type);
                return savedArticles != null ? savedArticles : new ArrayList<>();
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        
        return new ArrayList<>();
    }

    // Chia sẻ bài viết
    public static void shareArticle(Context context, NewsArticle article) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, article.getTitle());
        
        String shareMessage = article.getTitle() + "\n\n" + 
                            (article.getDescription() != null ? article.getDescription() + "\n\n" : "") +
                            "Read more at: " + article.getUrl();
        
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public static void clearSavedArticles(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_SAVED_ARTICLES);
        editor.apply();
    }
} 