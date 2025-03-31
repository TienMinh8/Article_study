package com.example.newsapp.models

data class NewsSummary(
    val articleId: String,
    val summary: String,
    val language: String,
    val timestamp: Long = System.currentTimeMillis()
) 