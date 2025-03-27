package com.example.article.api;

import android.util.Log;

import com.example.article.api.model.ApiResponse;
import com.example.article.api.model.NewsArticle;
import com.example.article.api.model.NewsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {
    @GET("everything")
    Call<NewsResponse> getEverything(
            @Query("q") String query,
            @Query("from") String from,
            @Query("to") String to,
            @Query("language") String language,
            @Query("apiKey") String apiKey
    );

    @GET("everything")
    Call<NewsResponse> getEverythingPaged(
            @Query("q") String query,
            @Query("from") String from,
            @Query("to") String to,
            @Query("page") int page,
            @Query("pageSize") int pageSize,
            @Query("language") String language,
            @Query("apiKey") String apiKey
    );

    @GET("top-headlines")
    Call<NewsResponse> getTopHeadlines(
            @Query("country") String country,
            @Query("language") String language,
            @Query("apiKey") String apiKey
    );

    @GET("top-headlines")
    Call<NewsResponse> getTopHeadlinesByCategory(
            @Query("country") String country,
            @Query("category") String category,
            @Query("language") String language,
            @Query("apiKey") String apiKey
    );

    @GET
    Call<NewsResponse> getCustomUrl(@Url String url);

    void getTopHeadlines(String category, final ApiCallback<List<NewsArticle>> callback);

    // Interface để trả về kết quả
    interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
} 