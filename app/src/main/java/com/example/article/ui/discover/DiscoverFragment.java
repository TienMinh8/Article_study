package com.example.article.ui.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.article.R;
import com.example.article.adapter.NewsAdapter;
import com.example.article.api.ApiClient;
import com.example.article.api.model.NewsArticle;
import com.example.article.utils.NetworkUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class DiscoverFragment extends Fragment implements NewsAdapter.OnNewsClickListener {

    private RecyclerView recyclerViewNews;
    private NewsAdapter newsAdapter;
    private ApiClient apiClient;
    private TextInputLayout searchLayout;
    private TextInputEditText etSearch;
    
    // Shimmer components
    private ShimmerFrameLayout shimmerFrameLayout;
    private View mainContent;
    private boolean isLoading = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        
        // Khởi tạo shimmer và content views
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout);
        mainContent = view.findViewById(R.id.contentContainer);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Khởi tạo ApiClient
        apiClient = ApiClient.getInstance(requireContext());
        
        // Hiển thị shimmer loading
        showLoading(true);
        
        // Khởi tạo RecyclerView
        recyclerViewNews = view.findViewById(R.id.recyclerViewNews);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        // Khởi tạo Search
        searchLayout = view.findViewById(R.id.searchLayout);
        etSearch = view.findViewById(R.id.etSearch);
        setupSearch();
        
        // Thiết lập adapter
        setupAdapter();
        
        // Tải tin tức ban đầu (sử dụng từ khóa phổ biến)
        loadNewsWithQuery("trending");
        
        // Thiết lập Back button
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (isLoading && shimmerFrameLayout != null) {
            shimmerFrameLayout.startShimmer();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (shimmerFrameLayout != null) {
            shimmerFrameLayout.stopShimmer();
        }
    }
    
    private void showLoading(boolean show) {
        isLoading = show;
        if (shimmerFrameLayout != null && mainContent != null) {
            if (show) {
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();
                mainContent.setVisibility(View.INVISIBLE);
            } else {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                mainContent.setVisibility(View.VISIBLE);
            }
        }
    }
    
    private void setupAdapter() {
        newsAdapter = new NewsAdapter(this);
        recyclerViewNews.setAdapter(newsAdapter);
    }
    
    private void setupSearch() {
        // Thiết lập search khi người dùng nhấn nút tìm kiếm trên bàn phím
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                loadNewsWithQuery(query);
                return true;
            }
            return false;
        });
        
        // Thiết lập icon filter
        searchLayout.setEndIconOnClickListener(v -> {
            Toast.makeText(requireContext(), "Tính năng lọc đang được phát triển", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadNewsWithQuery(String query) {
        // Show loading
        showLoading(true);
        
        // Check internet connection
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            // Sử dụng API everything với query thay vì category
            apiClient.getEverything(query, new ApiClient.ApiCallback<List<NewsArticle>>() {
                @Override
                public void onSuccess(List<NewsArticle> result) {
                    if (isAdded()) {
                        // Hide loading
                        showLoading(false);
                        
                        if (result != null && !result.isEmpty()) {
                            // Update adapter with news
                            newsAdapter.setNewsList(result);
                            
                            // Hiển thị thông báo
                            Toast.makeText(requireContext(), 
                                    String.format("Tìm thấy %d kết quả cho \"%s\"", result.size(), query),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // No results, show dummy data
                            newsAdapter.setDummyData();
                            
                            // Hiển thị thông báo
                            Toast.makeText(requireContext(), 
                                    String.format("Không tìm thấy kết quả cho \"%s\"", query),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (isAdded()) {
                        // Hide loading
                        showLoading(false);
                        
                        // Show dummy data on error
                        newsAdapter.setDummyData();
                        
                        // Hiển thị thông báo lỗi
                        Toast.makeText(requireContext(), 
                                "Lỗi tìm kiếm: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // No internet connection
            showLoading(false);
            newsAdapter.setDummyData();
            
            // Hiển thị thông báo không có Internet
            Toast.makeText(requireContext(), 
                    "Không có kết nối internet. Hiển thị dữ liệu mẫu.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNewsClick(NewsArticle article) {
        // Xử lý khi người dùng click vào tin tức
        if (article != null) {
            // Chuyển tới màn hình chi tiết
            Bundle args = new Bundle();
            args.putString("articleUrl", article.getUrl());
            Navigation.findNavController(requireView()).navigate(R.id.action_discover_to_detail, args);
        }
    }
} 