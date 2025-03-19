package com.example.article.api;

import com.example.article.api.model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {
    @GET("top-headlines")
    Call<ApiResponse> getTopHeadlines(
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );

    @GET("everything")
    Call<ApiResponse> getEverything(
            @Query("q") String query,
            @Query("apiKey") String apiKey
    );
} 