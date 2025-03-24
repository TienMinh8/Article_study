package com.example.article.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.article.MainActivity;
import com.example.article.R;
import com.example.article.adapter.BreakingNewsAdapter;
import com.example.article.adapter.NewsAdapter;
import com.example.article.api.ApiClient;
import com.example.article.api.model.NewsArticle;
import com.example.article.utils.ArticleUtils;
import com.example.article.utils.ItemSpacingDecoration;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.article.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment implements NewsAdapter.OnNewsClickListener, BreakingNewsAdapter.OnBreakingNewsClickListener {

    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerViewRecommendation;
    private ViewPager2 viewPagerBreakingNews;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    
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

    // Biến để lưu trữ danh sách kết quả đầy đủ
    private List<NewsArticle> fullResultList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Khởi tạo shimmer và content views
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout);
        mainContent = view.findViewById(R.id.mainContent);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        
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
        
        // Thiết lập SwipeRefreshLayout
        setupSwipeRefresh();
        
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
    
    private void setupSwipeRefresh() {
        // Thiết lập màu cho hiệu ứng loading
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorAccent,
                R.color.colorPrimaryDark
        );
        
        // Thiết lập sự kiện kéo để làm mới
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Reset các biến theo dõi trạng thái tải
            teslaLoaded = false;
            recommendationLoaded = false;
            
            // Tải lại dữ liệu
            refreshData();
        });
    }
    
    private void refreshData() {
        // Hiển thị thông báo đang làm mới
        Toast.makeText(requireContext(), R.string.refreshing, Toast.LENGTH_SHORT).show();
        
        // Kiểm tra kết nối internet
        if (isNetworkAvailable()) {
            // Tải lại dữ liệu Breaking News
            loadTeslaNews(0);
            
            // Tải lại dữ liệu Recommendation
            loadRecommendationNews("technology", 0);
            
            // Dừng animation làm mới sau khi cả hai dữ liệu đã tải xong
            // (sẽ được xử lý trong checkAndHideLoading())
        } else {
            // Không có kết nối internet
            showConnectionErrorSnackbar(getString(R.string.no_internet_connection));
            
            // Tải dữ liệu giả
            loadDummyData();
            
            // Dừng animation làm mới
            swipeRefreshLayout.setRefreshing(false);
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
        
        // Thêm ItemSpacingDecoration cho RecyclerView với khoảng cách 12dp
        int spacingInPixels = (int) (12 * getResources().getDisplayMetrics().density);
        recyclerViewRecommendation.addItemDecoration(new ItemSpacingDecoration(spacingInPixels));
        
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
        
        // Thiết lập click listener cho nút thông báo
        view.findViewById(R.id.btnNotification).setOnClickListener(v -> {
            // Gọi đến MainActivity để xử lý hiển thị thông báo
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showNotifications(v);
            }
        });
        
        // Cập nhật click listener cho nút Xem tất cả ở phần đề xuất
        view.findViewById(R.id.tvViewAllRecommendation).setOnClickListener(v -> {
            if (fullResultList != null && !fullResultList.isEmpty()) {
                // Hiển thị tất cả bài viết từ danh sách đầy đủ
                newsAdapter.setNewsList(new ArrayList<>(fullResultList));
                
                // Thông báo đã hiển thị tất cả
                Toast.makeText(requireContext(), 
                        String.format("Hiển thị tất cả %d bài viết", fullResultList.size()), 
                        Toast.LENGTH_SHORT).show();
                
                // Ẩn nút "Xem tất cả" vì đã hiển thị tất cả
                v.setVisibility(View.GONE);
                
                // Ẩn nút "Xem thêm" vì đã hiển thị tất cả
                View btnLoadMore = getView().findViewById(R.id.btnLoadMore);
                if (btnLoadMore != null) {
                    btnLoadMore.setEnabled(false);
                    btnLoadMore.setVisibility(View.GONE);
                }
            } else {
                // Nếu không có dữ liệu đầy đủ, thử tải thêm từ API
                Toast.makeText(requireContext(), "Đang tải tất cả bài viết...", Toast.LENGTH_SHORT).show();
                loadMoreRecommendations();
            }
        });

        // Thêm click listener cho nút "Load More"
        view.findViewById(R.id.btnLoadMore).setOnClickListener(v -> {
            if (newsAdapter != null) {
                loadMoreRecommendations();
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
                        // Lưu trữ danh sách đầy đủ để sử dụng cho "Load More"
                        fullResultList = new ArrayList<>(result);
                        
                        // Giới hạn hiển thị ban đầu là 10 item
                        List<NewsArticle> initialItems = result.size() > 10 ? 
                                new ArrayList<>(result.subList(0, 10)) : new ArrayList<>(result);
                        
                        // Cập nhật Recommendation
                        newsAdapter.setNewsList(initialItems);
                        
                        Log.d(TAG, "Loaded " + result.size() + " articles for query: " + query + 
                                ", displaying first " + initialItems.size());
                        
                        // Kích hoạt nút "Xem tất cả" và "Xem thêm" nếu có hơn 10 item
                        if (result.size() > 10) {
                            if (getView() != null) {
                                View btnLoadMore = getView().findViewById(R.id.btnLoadMore);
                                if (btnLoadMore != null) {
                                    btnLoadMore.setEnabled(true);
                                    btnLoadMore.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            // Ẩn nút "Xem thêm" nếu không có đủ item
                            if (getView() != null) {
                                View btnLoadMore = getView().findViewById(R.id.btnLoadMore);
                                if (btnLoadMore != null) {
                                    btnLoadMore.setEnabled(false);
                                    btnLoadMore.setVisibility(View.GONE);
                                }
                            }
                        }
                    } else {
                        Log.w(TAG, "Received empty result for query: " + query);
                        // Tải dữ liệu mẫu nếu kết quả trống
                        newsAdapter.setDummyData();
                        
                        // Ẩn nút "Xem thêm" vì không có dữ liệu
                        if (getView() != null) {
                            View btnLoadMore = getView().findViewById(R.id.btnLoadMore);
                            if (btnLoadMore != null) {
                                btnLoadMore.setEnabled(false);
                                btnLoadMore.setVisibility(View.GONE);
                            }
                        }
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
                        
                        // Ẩn nút "Xem thêm" vì đang hiển thị dữ liệu mẫu
                        if (getView() != null) {
                            View btnLoadMore = getView().findViewById(R.id.btnLoadMore);
                            if (btnLoadMore != null) {
                                btnLoadMore.setEnabled(false);
                                btnLoadMore.setVisibility(View.GONE);
                            }
                        }
                        
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
            
            // Dừng animation làm mới nếu đang active
            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
                
                // Hiển thị thông báo đã làm mới
                Toast.makeText(requireContext(), R.string.refresh_complete, Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void showLoading(boolean isLoading) {
        this.isLoading = isLoading;
        if (shimmerFrameLayout != null && mainContent != null && swipeRefreshLayout != null) {
            if (isLoading) {
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();
                swipeRefreshLayout.setVisibility(View.INVISIBLE);
            } else {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
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
        Bundle bundle = new Bundle();
        bundle.putString("url", article.getUrl());
        Navigation.findNavController(requireView()).navigate(R.id.action_home_to_detail, bundle);
    }

    @Override
    public void onShareClick(NewsArticle article) {
        ArticleUtils.shareArticle(requireContext(), article);
    }

    @Override
    public void onBookmarkClick(NewsArticle article) {
        Context context = requireContext();
        boolean isSaved = ArticleUtils.isArticleSaved(context, article);
        
        if (isSaved) {
            ArticleUtils.unsaveArticle(context, article);
            Toast.makeText(context, "Đã xóa khỏi danh sách đã lưu", Toast.LENGTH_SHORT).show();
        } else {
            ArticleUtils.saveArticle(context, article);
            Toast.makeText(context, "Đã thêm vào danh sách đã lưu", Toast.LENGTH_SHORT).show();
        }
        
        // Cập nhật lại adapter để thay đổi icon bookmark
        newsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBreakingNewsClick(NewsArticle article) {
        // Xử lý khi người dùng click vào tin tức nổi bật
        if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.navigation_home) {
            Bundle args = new Bundle();
            args.putString("url", article.getUrl());
            Navigation.findNavController(requireView()).navigate(R.id.action_home_to_detail, args);
        }
    }

    // Phương thức để tải thêm 10 item vào RecyclerView
    private void loadMoreRecommendations() {
        if (newsAdapter != null) {
            // Hiển thị Toast thông báo đang tải thêm
            Toast.makeText(requireContext(), "Đang tải thêm bài viết...", Toast.LENGTH_SHORT).show();
            
            // Lấy danh sách hiện tại
            List<NewsArticle> currentList = newsAdapter.getCurrentNewsList();
            
            // Kiểm tra xem đã có danh sách kết quả đầy đủ chưa
            if (fullResultList != null && !fullResultList.isEmpty()) {
                // Nếu đã hiển thị hết danh sách
                if (currentList.size() >= fullResultList.size()) {
                    // Tải thêm từ API nếu đã hiển thị tất cả các mục trong danh sách đầy đủ
                    loadMoreFromApi(currentList);
                } else {
                    // Tính toán số lượng item cần thêm (tối đa 10)
                    int currentSize = currentList.size();
                    int endIndex = Math.min(currentSize + 10, fullResultList.size());
                    
                    // Lấy các item tiếp theo từ danh sách đầy đủ
                    List<NewsArticle> moreItems = new ArrayList<>(
                            fullResultList.subList(currentSize, endIndex));
                    
                    // Kiểm tra trùng lặp và chỉ thêm các item chưa có
                    List<NewsArticle> newItems = filterDuplicateItems(currentList, moreItems);
                    
                    // Thêm các item mới vào adapter
                    if (!newItems.isEmpty()) {
                        newsAdapter.addMoreItems(newItems);
                        Log.d(TAG, "Added " + newItems.size() + " more items to recommendation list from cached results");
                        Toast.makeText(requireContext(), 
                                "Đã thêm " + newItems.size() + " bài viết mới", 
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Nếu tất cả các item mới đều trùng lặp, tải thêm từ API
                        loadMoreFromApi(currentList);
                    }
                }
            } else {
                // Nếu không có danh sách đầy đủ, tải trực tiếp từ API
                loadMoreFromApi(currentList);
            }
        }
    }

    // Phương thức tải thêm từ API
    private void loadMoreFromApi(List<NewsArticle> currentList) {
        // Gọi API để lấy thêm dữ liệu
        apiClient.getEverything("technology", new ApiClient.ApiCallback<List<NewsArticle>>() {
            @Override
            public void onSuccess(List<NewsArticle> result) {
                if (isAdded()) {
                    if (result != null && !result.isEmpty()) {
                        // Lấy tối đa 10 item từ danh sách kết quả mới
                        List<NewsArticle> additionalItems = result.size() > 10 ? 
                                result.subList(0, 10) : new ArrayList<>(result);
                        
                        // Kiểm tra trùng lặp và chỉ thêm các item chưa có
                        List<NewsArticle> newItems = filterDuplicateItems(currentList, additionalItems);
                        
                        // Thêm các item mới vào adapter
                        if (!newItems.isEmpty()) {
                            newsAdapter.addMoreItems(newItems);
                            Log.d(TAG, "Added " + newItems.size() + " more items to recommendation list from API");
                            Toast.makeText(requireContext(), 
                                    "Đã thêm " + newItems.size() + " bài viết mới", 
                                    Toast.LENGTH_SHORT).show();
                            
                            // Cập nhật danh sách đầy đủ
                            if (fullResultList == null) {
                                fullResultList = new ArrayList<>(result);
                            } else {
                                // Thêm các mục chưa trùng lặp vào danh sách đầy đủ
                                for (NewsArticle article : newItems) {
                                    if (!fullResultList.contains(article)) {
                                        fullResultList.add(article);
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), 
                                    "Không có bài viết mới để hiển thị", 
                                    Toast.LENGTH_SHORT).show();
                            
                            // Nếu không có item mới, tải dữ liệu giả
                            loadDummyRecommendations();
                        }
                    } else {
                        // Nếu API không trả về kết quả, tải dữ liệu giả
                        loadDummyRecommendations();
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (isAdded()) {
                    Log.e(TAG, "Error loading more recommendation news: " + errorMessage);
                    Toast.makeText(requireContext(), 
                            "Không thể tải thêm: " + errorMessage, 
                            Toast.LENGTH_SHORT).show();
                    
                    // Nếu có lỗi, tải dữ liệu giả
                    loadDummyRecommendations();
                }
            }
        });
    }

    // Lọc ra các item trùng lặp
    private List<NewsArticle> filterDuplicateItems(List<NewsArticle> currentList, List<NewsArticle> newItems) {
        List<NewsArticle> filteredList = new ArrayList<>();
        
        // Tạo tập hợp các URL đã tồn tại để kiểm tra nhanh
        Set<String> existingUrls = new HashSet<>();
        for (NewsArticle article : currentList) {
            if (article.getUrl() != null) {
                existingUrls.add(article.getUrl());
            }
        }
        
        // Chỉ thêm các item có URL chưa tồn tại
        for (NewsArticle article : newItems) {
            if (article.getUrl() != null && !existingUrls.contains(article.getUrl())) {
                filteredList.add(article);
                existingUrls.add(article.getUrl()); // Thêm vào tập hợp để không trùng lặp
            }
        }
        
        return filteredList;
    }

    // Tải thêm dữ liệu giả khi cần
    private void loadDummyRecommendations() {
        List<NewsArticle> dummyItems = new ArrayList<>();
        
        int startIndex = newsAdapter.getItemCount() + 1;
        int endIndex = startIndex + 10;
        
        for (int i = startIndex; i < endIndex; i++) {
            NewsArticle article = new NewsArticle();
            article.setTitle("Additional Sample Title " + i + " - This is a new article added when clicking load more");
            article.setDescription("This is a sample description for additional article " + i + ". It contains more text to display how the description field will appear in the application interface.");
            article.setUrlToImage("");
            article.setPublishedAt("2023-06-15T" + (10 + i % 12) + ":00:00Z");
            article.setUrl("https://example.com/article" + i); // URL ảo để phân biệt
            
            NewsArticle.Source source = new NewsArticle.Source();
            source.setName("Sample News " + i);
            article.setSource(source);
            
            dummyItems.add(article);
        }
        
        newsAdapter.addMoreItems(dummyItems);
        Log.d(TAG, "Added " + dummyItems.size() + " dummy items to recommendation list");
        Toast.makeText(requireContext(), 
                "Đã thêm " + dummyItems.size() + " bài viết mẫu", 
                Toast.LENGTH_SHORT).show();
    }
} 