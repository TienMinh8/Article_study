package com.example.article.ui.detail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.article.R;
import com.example.article.api.model.NewsArticle;
import com.example.article.utils.ArticleUtils;
import com.google.gson.Gson;

public class DetailFragment extends Fragment {

    private WebView webView;
    private ProgressBar progressBar;
    private ImageButton btnBookmark;
    private ImageButton btnShare;
    private NewsArticle currentArticle;
    private TextView tvNoInternet;
    private Toolbar toolbar;
    private String articleUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Lấy URL từ arguments
        if (getArguments() != null) {
            articleUrl = getArguments().getString("articleUrl");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Khởi tạo views
        initViews(view);
        
        // Thiết lập WebView
        setupWebView();
        
        // Tải URL bài viết
        loadArticle();
    }
    
    private void initViews(View view) {
        webView = view.findViewById(R.id.webView);
        progressBar = view.findViewById(R.id.progressBar);
        btnBookmark = view.findViewById(R.id.btnBookmark);
        btnShare = view.findViewById(R.id.btnShare);
        tvNoInternet = view.findViewById(R.id.tvNoInternet);
        toolbar = view.findViewById(R.id.toolbar);
        TextView toolbarTitle = view.findViewById(R.id.toolbarTitle);

        // Setup toolbar navigation
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Share button
        btnShare.setOnClickListener(v -> {
            if (currentArticle != null) {
                ArticleUtils.shareArticle(requireContext(), currentArticle);
            }
        });

        // Bookmark button
        btnBookmark.setOnClickListener(v -> toggleBookmark());
    }
    
    private void setupWebView() {
        // Bật JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        
        // Bật zoom
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        
        // Cấu hình WebViewClient
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Tải tất cả URL trong WebView này
                return false;
            }
            
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                tvNoInternet.setVisibility(View.GONE);
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
            
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                progressBar.setVisibility(View.GONE);
                tvNoInternet.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Error: " + description, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Cấu hình WebChromeClient để theo dõi tiến độ tải
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
    }
    
    private void loadArticle() {
        if (getArguments() != null) {
            // Lấy toàn bộ article object từ arguments
            String articleJson = getArguments().getString("article");
            if (articleJson != null) {
                currentArticle = new Gson().fromJson(articleJson, NewsArticle.class);
                if (currentArticle != null && currentArticle.getUrl() != null) {
                    webView.loadUrl(currentArticle.getUrl());
                    updateBookmarkIcon();
                }
            }
        }
    }
    
    private void toggleBookmark() {
        if (currentArticle != null && getContext() != null) {
            if (ArticleUtils.isArticleSaved(getContext(), currentArticle)) {
                ArticleUtils.unsaveArticle(getContext(), currentArticle);
                Toast.makeText(getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
            } else {
                ArticleUtils.saveArticle(getContext(), currentArticle);
                Toast.makeText(getContext(), R.string.saved, Toast.LENGTH_SHORT).show();
            }
            updateBookmarkIcon();
        }
    }

    private void updateBookmarkIcon() {
        if (currentArticle != null && getContext() != null && btnBookmark != null) {
            boolean isSaved = ArticleUtils.isArticleSaved(getContext(), currentArticle);
            btnBookmark.setImageResource(isSaved ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_border);
        }
    }
    
    @Override
    public void onDestroy() {
        // Dọn dẹp WebView
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
        }
        super.onDestroy();
    }
} 