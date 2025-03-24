package com.example.article.ui.detail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.article.R;
import com.example.article.api.model.NewsArticle;
import com.example.article.utils.ArticleUtils;
import com.example.article.utils.FontSizeUtils;
import com.example.article.utils.HtmlParser;
import com.example.article.utils.NetworkUtils;
import com.google.gson.Gson;

public class DetailFragment extends Fragment implements HtmlParser.OnContentLoadedListener {

    private SwipeRefreshLayout swipeRefresh;
    private ImageView featuredImage;
    private TextView toolbarTitle;
    private TextView articleTitle;
    private TextView articleContent;
    private TextView noInternetText;
    private ProgressBar progressBar;
    private ImageButton btnBookmark;
    private ImageButton btnShare;
    private String articleUrl;
    private NewsArticle currentArticle;
    private HtmlParser htmlParser;
    private BroadcastReceiver fontSizeReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            articleUrl = getArguments().getString("url", "");
            // Tạo NewsArticle object từ URL
            currentArticle = new NewsArticle();
            currentArticle.setUrl(articleUrl);
        }
        htmlParser = new HtmlParser();
        
        // Đăng ký nhận broadcast khi font size thay đổi
        fontSizeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateFontSizes();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        // Đăng ký receiver khi fragment start
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(fontSizeReceiver, new IntentFilter("FONT_SIZE_CHANGED"));
        // Cập nhật font size ngay khi start
        updateFontSizes();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Hủy đăng ký receiver khi fragment stop
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(fontSizeReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadArticle();
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        featuredImage = view.findViewById(R.id.featuredImage);
        toolbarTitle = view.findViewById(R.id.toolbarTitle);
        articleTitle = view.findViewById(R.id.articleTitle);
        articleContent = view.findViewById(R.id.articleContent);
        noInternetText = view.findViewById(R.id.noInternetText);
        progressBar = view.findViewById(R.id.progressBar);
        btnBookmark = view.findViewById(R.id.btnBookmark);
        btnShare = view.findViewById(R.id.btnShare);

        swipeRefresh.setOnRefreshListener(this::loadArticle);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Setup bookmark button
        btnBookmark.setOnClickListener(v -> toggleBookmark());
        updateBookmarkIcon();

        // Setup share button
        btnShare.setOnClickListener(v -> {
            if (currentArticle != null) {
                ArticleUtils.shareArticle(requireContext(), currentArticle);
            }
        });

        // Cập nhật font size ban đầu
        updateFontSizes();
    }

    private void updateFontSizes() {
        if (getContext() == null) return;

        float titleSize = FontSizeUtils.getTitleTextSize(requireContext());
        float contentSize = FontSizeUtils.getContentTextSize(requireContext());

        // Cập nhật kích thước chữ cho title
        toolbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize - 2); // Toolbar title nhỏ hơn 2sp
        articleTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize);

        // Cập nhật kích thước chữ cho content
        articleContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentSize);
        noInternetText.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentSize);
    }

    private void loadArticle() {
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            showNoInternet(true);
            swipeRefresh.setRefreshing(false);
            return;
        }

        if (TextUtils.isEmpty(articleUrl)) {
            Toast.makeText(requireContext(), 
                getString(R.string.error_loading_content, "Invalid URL"),
                Toast.LENGTH_LONG).show();
            showLoading(false);
            swipeRefresh.setRefreshing(false);
            return;
        }

        showLoading(true);
        showNoInternet(false);
        htmlParser.parseUrl(articleUrl, this);
    }

    @Override
    public void onContentLoaded(String title, String imageUrl, String content) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            toolbarTitle.setText(title);
            articleTitle.setText(title);
            articleContent.setText(content);

            // Update current article with parsed data
            if (currentArticle != null) {
                currentArticle.setTitle(title);
                currentArticle.setUrlToImage(imageUrl);
                currentArticle.setDescription(content.length() > 200 ? 
                    content.substring(0, 200) + "..." : content);
                updateBookmarkIcon();
            }

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .into(featuredImage);
                featuredImage.setVisibility(View.VISIBLE);
            } else {
                featuredImage.setVisibility(View.GONE);
            }

            showLoading(false);
            swipeRefresh.setRefreshing(false);
        });
    }

    @Override
    public void onError(String error) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), 
                getString(R.string.error_loading_content, error),
                Toast.LENGTH_LONG).show();
            showLoading(false);
            swipeRefresh.setRefreshing(false);
        });
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

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        swipeRefresh.setEnabled(!show);
    }

    private void showNoInternet(boolean show) {
        noInternetText.setVisibility(show ? View.VISIBLE : View.GONE);
        featuredImage.setVisibility(show ? View.GONE : View.VISIBLE);
        articleTitle.setVisibility(show ? View.GONE : View.VISIBLE);
        articleContent.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (htmlParser != null) {
            htmlParser.cancel();
        }
    }
} 