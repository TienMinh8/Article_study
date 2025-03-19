package com.example.article.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.article.R;
import com.example.article.adapter.BreakingNewsAdapter;
import com.example.article.adapter.NewsAdapter;
import com.example.article.api.ApiClient;
import com.example.article.api.model.NewsArticle;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment implements NewsAdapter.OnNewsClickListener, BreakingNewsAdapter.OnBreakingNewsClickListener {

    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerViewRecommendation;
    private ViewPager2 viewPagerBreakingNews;
    private TabLayout tabLayout;
    
    private NewsAdapter newsAdapter;
    private BreakingNewsAdapter breakingNewsAdapter;
    
    // Thêm biến cho auto slide
    private int currentPage = 0;
    private final long DELAY_MS = 500; // Độ trễ trước khi bắt đầu
    private final long PERIOD_MS = 3000; // Thời gian giữa các lần chuyển trang
    private Timer timer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    
    // ApiClient
    private ApiClient apiClient;
    
    // Shimmer components
    private ShimmerFrameLayout shimmerFrameLayout;
    private View mainContent;
    private boolean isLoading = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Khởi tạo shimmer và content views
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout);
        mainContent = view.findViewById(R.id.mainContent);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Khởi tạo ApiClient
        apiClient = ApiClient.getInstance(requireContext());
        
        // Hiển thị shimmer loading
        showLoading(true);
        
        // Khởi tạo views
        initViews(view);
        
        // Thiết lập adapter
        setupAdapters();
        
        // Tải dữ liệu từ API
        loadDataFromApi();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Bắt đầu lại auto slide khi fragment trở lại foreground
        setupAutoSlide();
        
        // Bắt đầu lại animation shimmer nếu đang loading
        if (isLoading && shimmerFrameLayout != null) {
            shimmerFrameLayout.startShimmer();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // Dừng auto slide khi fragment không hiển thị
        stopAutoSlide();
        
        // Dừng animation shimmer để tiết kiệm tài nguyên
        if (shimmerFrameLayout != null) {
            shimmerFrameLayout.stopShimmer();
        }
    }
    
    private void setupAutoSlide() {
        stopAutoSlide(); // Dừng timer cũ trước khi tạo timer mới
        
        // Khởi tạo timer mới
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    if (viewPagerBreakingNews == null) return;
                    
                    int totalItems = breakingNewsAdapter.getItemCount();
                    if (totalItems > 1) {
                        currentPage = (currentPage + 1) % totalItems;
                        viewPagerBreakingNews.setCurrentItem(currentPage, true);
                    }
                });
            }
        }, DELAY_MS, PERIOD_MS);
    }
    
    private void stopAutoSlide() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    private void initViews(View view) {
        recyclerViewRecommendation = view.findViewById(R.id.recyclerViewRecommendation);
        viewPagerBreakingNews = view.findViewById(R.id.viewPagerBreakingNews);
        tabLayout = view.findViewById(R.id.tabLayout);
        
        // Cấu hình ViewPager2
        viewPagerBreakingNews.setClipToPadding(false);
        viewPagerBreakingNews.setClipChildren(false);
        viewPagerBreakingNews.setOffscreenPageLimit(3);
        
        // Thiết lập page transformer cho hiệu ứng
        CompositePageTransformer compositeTransformer = new CompositePageTransformer();
        compositeTransformer.addTransformer(new MarginPageTransformer(20));
        // Thêm custom transformer
        compositeTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.90f + r * 0.10f);
        });
        viewPagerBreakingNews.setPageTransformer(compositeTransformer);
        
        // Thiết lập các click listener cho các thành phần khác
        view.findViewById(R.id.tvViewAll).setOnClickListener(v -> 
                Toast.makeText(requireContext(), R.string.view_all, Toast.LENGTH_SHORT).show());
        
        // Cập nhật click listener cho nút Xem tất cả ở phần đề xuất để tải thêm tin
        view.findViewById(R.id.tvViewAllRecommendation).setOnClickListener(v -> {
            if (newsAdapter != null) {
                newsAdapter.loadMoreItems();
            }
        });
    }
    
    private void setupAdapters() {
        // Thiết lập adapter cho RecyclerView
        newsAdapter = new NewsAdapter(this);
        recyclerViewRecommendation.setAdapter(newsAdapter);
        
        // Thiết lập adapter cho ViewPager
        breakingNewsAdapter = new BreakingNewsAdapter(this);
        viewPagerBreakingNews.setAdapter(breakingNewsAdapter);
        
        // Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPagerBreakingNews,
                (tab, position) -> {
                    // Tab không có tiêu đề
                }).attach();
    }
    
    private void loadDataFromApi() {
        // Hiển thị loading
        showLoading(true);
        
        // Kiểm tra kết nối internet trước khi gọi API
        if (isNetworkAvailable()) {
            // Lấy tin tức tesla làm breaking news  
            loadTeslaNews(0); // Thử lần đầu với 0 lần retry
            
            // Lấy tin tức với từ khóa Technology làm Recommendation
            loadRecommendationNews("technology", 0); // Thử lần đầu với 0 lần retry
        } else {
            // Không có kết nối internet
            Log.e(TAG, "No internet connection");
            
            // Hiển thị Snackbar thông báo lỗi kết nối
            showConnectionErrorSnackbar(getString(R.string.no_internet_connection));
            
            // Ẩn loading
            showLoading(false);
            
            // Tải dữ liệu giả
            loadDummyData();
        }
    }
    
    // Kiểm tra kết nối mạng đơn giản
    private boolean isNetworkAvailable() {
        return true; // Đơn giản hóa - luôn trả về true, sẽ được thay thế bằng NetworkUtils
    }
    
    // Hiển thị Snackbar thông báo lỗi kết nối
    private void showConnectionErrorSnackbar(String message) {
        if (getView() != null) {
            Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry, v -> loadDataFromApi());
            
            // Đặt màu cho nút retry
            snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
            
            // Hiển thị snackbar
            snackbar.show();
        } else {
            // Fallback để hiển thị thông báo nếu không có view
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        }
    }
    
    // Phương thức tải tin tức Tesla với retry
    private void loadTeslaNews(final int retryCount) {
        apiClient.getTeslaNews(new ApiClient.ApiCallback<List<NewsArticle>>() {
            @Override
            public void onSuccess(List<NewsArticle> result) {
                if (isAdded()) {
                    // Giới hạn danh sách Breaking News chỉ hiển thị tối đa 5 bài mới nhất
                    List<NewsArticle> limitedResults = result;
                    if (result.size() > 5) {
                        limitedResults = result.subList(0, 5);
                        Log.d(TAG, "Breaking news limited to 5 items from " + result.size());
                    }
                    
                    // Cập nhật Breaking News
                    breakingNewsAdapter.setBreakingNewsList(limitedResults);
                    
                    // TabLayout sẽ tự động cập nhật dựa trên adapter
                    
                    // Kiểm tra nếu cả Tesla và Recommendation news đã tải xong
                    checkAndHideLoading();
                    
                    // Bắt đầu auto slide
                    setupAutoSlide();
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (isAdded()) {
                    if (retryCount < 2) { // Thử lại tối đa 2 lần
                        // Đợi 2 giây rồi thử lại
                        new Handler().postDelayed(() -> {
                            Log.d(TAG, "Retrying Tesla news: attempt " + (retryCount + 1));
                            loadTeslaNews(retryCount + 1);
                        }, 2000);
                    } else {
                        // Hiển thị thông báo lỗi chi tiết
                        Log.e(TAG, "API Error after retries: " + errorMessage);
                        
                        // Hiển thị Snackbar thông báo lỗi
                        showNetworkErrorSnackbar("Tesla News: " + errorMessage);
                        
                        // Tải dữ liệu giả cho Breaking News
                        breakingNewsAdapter.setDummyData();
                        
                        // Kiểm tra nếu cả Tesla và Recommendation news đã tải xong
                        checkAndHideLoading();
                    }
                }
            }
        });
    }

    // Hiển thị Snackbar thông báo lỗi mạng
    private void showNetworkErrorSnackbar(String errorMessage) {
        if (getView() != null) {
            // Rút gọn thông báo lỗi nếu quá dài
            String displayMessage = errorMessage;
            if (errorMessage.length() > 100) {
                displayMessage = errorMessage.substring(0, 97) + "...";
            }
            
            Snackbar.make(getView(), displayMessage, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry, v -> loadDataFromApi())
                    .setActionTextColor(getResources().getColor(R.color.colorAccent))
                    .show();
        } else {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    // Phương thức tải tin tức đề xuất dựa trên từ khóa với retry
    private void loadRecommendationNews(final String query, final int retryCount) {
        apiClient.getEverything(query, new ApiClient.ApiCallback<List<NewsArticle>>() {
            @Override
            public void onSuccess(List<NewsArticle> result) {
                if (isAdded()) {
                    if (result != null && !result.isEmpty()) {
                        // Cập nhật Recommendation
                        newsAdapter.setNewsList(result);
                        Log.d(TAG, "Loaded " + result.size() + " articles for query: " + query);
                    } else {
                        Log.w(TAG, "Received empty result for query: " + query);
                        // Tải dữ liệu mẫu nếu kết quả trống
                        newsAdapter.setDummyData();
                    }
                    
                    // Kiểm tra nếu cả Tesla và Recommendation news đã tải xong
                    checkAndHideLoading();
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (isAdded()) {
                    Log.e(TAG, "Error loading recommendation news: " + errorMessage);
                    
                    if (retryCount < 1) { // Thử lại 1 lần với từ khóa khác
                        Log.d(TAG, "Retrying with different query");
                        loadRecommendationNews("trending", retryCount + 1);
                    } else {
                        // Hiển thị Snackbar thông báo lỗi
                        showNetworkErrorSnackbar("Recommendation News: " + errorMessage);
                        
                        // Hiển thị dữ liệu mẫu
                        newsAdapter.setDummyData();
                        
                        // Kiểm tra nếu cả Tesla và Recommendation news đã tải xong
                        checkAndHideLoading();
                    }
                }
            }
        });
    }
    
    // Theo dõi nếu cả Breaking News và Recommendation news đã tải xong
    private boolean teslaLoaded = false;
    private boolean recommendationLoaded = false;
    
    private void checkAndHideLoading() {
        if (breakingNewsAdapter.getItemCount() > 0) {
            teslaLoaded = true;
        }
        
        if (newsAdapter.getItemCount() > 0) {
            recommendationLoaded = true;
        }
        
        if (teslaLoaded && recommendationLoaded) {
            // Cả hai đã tải xong, ẩn loading
            showLoading(false);
        }
    }
    
    private void showLoading(boolean isLoading) {
        this.isLoading = isLoading;
        if (shimmerFrameLayout != null && mainContent != null) {
            if (isLoading) {
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
    
    private void loadDummyData() {
        // Tải dữ liệu mẫu cho RecyclerView
        newsAdapter.setDummyData();
        
        // Tải dữ liệu mẫu cho ViewPager
        breakingNewsAdapter.setDummyData();
        
        // Đánh dấu đã tải xong
        teslaLoaded = true;
        recommendationLoaded = true;
        
        // Ẩn loading
        showLoading(false);
    }

    @Override
    public void onNewsClick(NewsArticle article) {
        // Xử lý khi người dùng click vào tin tức đề xuất
        Toast.makeText(requireContext(), "Clicked: " + article.getTitle(), Toast.LENGTH_SHORT).show();
        
        // Chuyển tới màn hình chi tiết nếu có
        if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.navigation_home) {
            Bundle args = new Bundle();
            args.putString("articleUrl", article.getUrl());
            Navigation.findNavController(requireView()).navigate(R.id.action_home_to_detail, args);
        }
    }

    @Override
    public void onBreakingNewsClick(NewsArticle article) {
        // Xử lý khi người dùng click vào tin tức nổi bật
        Toast.makeText(requireContext(), "Clicked breaking: " + article.getTitle(), Toast.LENGTH_SHORT).show();
        
        // Chuyển tới màn hình chi tiết nếu có
        if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.navigation_home) {
            Bundle args = new Bundle();
            args.putString("articleUrl", article.getUrl());
            Navigation.findNavController(requireView()).navigate(R.id.action_home_to_detail, args);
        }
    }
} 