package com.example.article.ui.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.article.R;
import com.example.article.adapter.NewsAdapter;
import com.example.article.api.ApiClient;
import com.example.article.api.model.NewsArticle;
import com.example.article.utils.ItemSpacingDecoration;
import com.example.article.utils.NetworkUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.article.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class DiscoverFragment extends Fragment implements NewsAdapter.OnNewsClickListener {

    private RecyclerView recyclerViewNews;
    private NewsAdapter newsAdapter;
    private ApiClient apiClient;
    private TextInputLayout searchLayout;
    private TextInputEditText etSearch;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String currentCategory = ""; // Theo dõi danh mục đang được chọn
    private String currentQuery = "trending"; // Theo dõi từ khóa tìm kiếm hiện tại
    private int currentPage = 1; // Theo dõi trang hiện tại cho phân trang
    private Button btnLoadMore; // Nút tải thêm
    
    // Shimmer components
    private ShimmerFrameLayout shimmerFrameLayout;
    private View mainContent;
    private boolean isLoading = true;

    // Biến để lưu trữ danh sách kết quả đầy đủ
    private List<NewsArticle> fullResultList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        
        // Khởi tạo shimmer và content views
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout);
        mainContent = view.findViewById(R.id.contentContainer);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Khởi tạo ApiClient
        apiClient = ApiClient.getInstance(requireContext());
        
        // Khởi tạo RecyclerView
        recyclerViewNews = view.findViewById(R.id.recyclerViewNews);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        // Khởi tạo Search
        searchLayout = view.findViewById(R.id.searchLayout);
        etSearch = view.findViewById(R.id.etSearch);
        setupSearch();
        
        // Thêm ItemSpacingDecoration với khoảng cách 12dp
        int spacingInPixels = (int) (12 * getResources().getDisplayMetrics().density);
        recyclerViewNews.addItemDecoration(new ItemSpacingDecoration(spacingInPixels));
        
        // Khởi tạo ChipGroup cho bộ lọc danh mục
        ChipGroup chipGroup = view.findViewById(R.id.chipGroup);
        if (chipGroup != null) {
            setupCategoryChips(chipGroup);
        }
        
        // Thiết lập adapter
        setupAdapter();
        
        // Thiết lập SwipeRefreshLayout
        setupSwipeRefresh();
        
        // Thiết lập nút Load More
        btnLoadMore = view.findViewById(R.id.btnLoadMore);
        setupLoadMoreButton();
        
        // Tải tin tức ban đầu (sử dụng từ khóa phổ biến)
        loadNewsWithQuery("trending");
        
        // Thiết lập Back button
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        
        // Thiết lập click listener cho nút thông báo
        View btnNotification = view.findViewById(R.id.btnNotification);
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                // Gọi đến MainActivity để xử lý hiển thị thông báo
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showNotifications(v);
                }
            });
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
            // Làm mới dữ liệu dựa trên danh mục hoặc từ khóa tìm kiếm hiện tại
            refreshData();
        });
    }
    
    private void refreshData() {
        // Hiển thị thông báo đang làm mới
        Toast.makeText(requireContext(), R.string.refreshing, Toast.LENGTH_SHORT).show();
        
        // Kiểm tra theo category hoặc search query
        if (!currentCategory.isEmpty() && !currentCategory.equals(getString(R.string.category_all))) {
            // Làm mới theo danh mục
            loadNewsByCategory(currentCategory);
        } else if (!currentQuery.isEmpty()) {
            // Làm mới theo từ khóa tìm kiếm
            loadNewsWithQuery(currentQuery);
        } else {
            // Mặc định: tải tin tức xu hướng
            loadNewsWithQuery("trending");
        }
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
                
                // Đảm bảo SwipeRefresh không hiển thị khi đang loading
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            } else {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                mainContent.setVisibility(View.VISIBLE);
                
                // Dừng animation SwipeRefresh nếu đang active
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                    
                    // Hiển thị thông báo đã làm mới
                    Toast.makeText(requireContext(), R.string.refresh_complete, Toast.LENGTH_SHORT).show();
                }
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
                currentQuery = query; // Lưu từ khóa tìm kiếm hiện tại
                currentCategory = ""; // Reset danh mục khi tìm kiếm
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
        // Lưu query hiện tại để có thể làm mới sau này
        currentQuery = query;
        
        // Reset page number
        currentPage = 1;
        
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
                            // Giới hạn ban đầu chỉ hiển thị 10 tin
                            List<NewsArticle> initialItems = result.size() > 10 ? 
                                    new ArrayList<>(result.subList(0, 10)) : new ArrayList<>(result);
                            
                            // Update adapter with news
                            newsAdapter.setNewsList(initialItems);
                            
                            // Hiển thị thông báo
                            Toast.makeText(requireContext(), 
                                    String.format("Tìm thấy %d kết quả cho \"%s\"", result.size(), query),
                                    Toast.LENGTH_SHORT).show();
                            
                            // Lưu lại danh sách đầy đủ để sử dụng cho "Load More"
                            if (result.size() > 10) {
                                // Có thêm tin để tải
                                resetLoadMoreButton();
                            } else {
                                // Không còn tin để tải
                                btnLoadMore.setEnabled(false);
                                btnLoadMore.setText(R.string.no_more_results);
                            }
                            
                            // Lưu lại danh sách kết quả đầy đủ (nếu cần thiết)
                            DiscoverFragment.this.fullResultList = new ArrayList<>(result);
                        } else {
                            // No results, show dummy data
                            newsAdapter.setDummyData();
                            
                            // Hiển thị thông báo
                            Toast.makeText(requireContext(), 
                                    String.format("Không tìm thấy kết quả cho \"%s\"", query),
                                    Toast.LENGTH_SHORT).show();
                            
                            // Vô hiệu hóa nút Load More
                            btnLoadMore.setEnabled(false);
                            btnLoadMore.setText(R.string.no_more_results);
                            
                            // Xóa danh sách kết quả đầy đủ
                            DiscoverFragment.this.fullResultList = null;
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
                                
                        // Vô hiệu hóa nút Load More
                        btnLoadMore.setEnabled(false);
                        btnLoadMore.setText(R.string.no_more_results);
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
                    
            // Vô hiệu hóa nút Load More
            btnLoadMore.setEnabled(false);
            btnLoadMore.setText(R.string.no_more_results);
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

    /**
     * Thiết lập chip cho các danh mục
     */
    private void setupCategoryChips(ChipGroup chipGroup) {
        // Danh sách các danh mục
        String[] categories = {
                getString(R.string.category_all),
                getString(R.string.category_business),
                getString(R.string.category_technology),
                getString(R.string.category_entertainment),
                getString(R.string.category_sports),
                getString(R.string.category_science),
                getString(R.string.category_health)
        };
        
        // Tạo chip cho mỗi danh mục
        for (int i = 0; i < categories.length; i++) {
            Chip chip = new Chip(requireContext());
            chip.setText(categories[i]);
            chip.setCheckable(true);
            chip.setClickable(true);
            
            // Thiết lập style cho chip
            chip.setChipBackgroundColorResource(R.color.chip_background_color_state_list);
            chip.setTextColor(getResources().getColorStateList(R.color.chip_text_color_state_list, null));
            chip.setChipStrokeWidth(1f);
            chip.setChipStrokeColorResource(R.color.colorPrimary);
            
            // Đặt mặc định chọn chip đầu tiên (All)
            if (i == 0) {
                chip.setChecked(true);
                currentCategory = categories[i]; // Lưu lại danh mục mặc định
            }
            
            // Thêm chip vào ChipGroup
            chipGroup.addView(chip);
            
            // Thêm listener cho chip
            final String category = categories[i]; // Tạo bản sao cuối cùng để sử dụng trong listener
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Xử lý khi chip được chọn
                    currentCategory = category; // Lưu lại danh mục hiện tại
                    currentQuery = ""; // Reset từ khóa tìm kiếm khi chọn danh mục
                    
                    if (category.equals(getString(R.string.category_all))) {
                        // Tải lại tất cả tin tức
                        loadAllNews();
                    } else {
                        // Tải tin tức theo danh mục
                        loadNewsByCategory(category);
                    }
                }
            });
        }
    }
    
    /**
     * Tải tất cả tin tức
     */
    private void loadAllNews() {
        // Hiển thị loading
        showLoading(true);
        
        // Reset search query
        currentQuery = "trending";
        
        // Tải dữ liệu trending
        loadNewsWithQuery(currentQuery);
    }
    
    /**
     * Tải tin tức theo danh mục
     */
    private void loadNewsByCategory(String category) {
        // Hiển thị loading
        showLoading(true);
        
        // Tải dữ liệu theo danh mục
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            // Gọi API với danh mục
            apiClient.getTopHeadlines(mapCategoryToApiParam(category), new ApiClient.ApiCallback<List<NewsArticle>>() {
                @Override
                public void onSuccess(List<NewsArticle> result) {
                    if (isAdded()) {
                        // Hide loading
                        showLoading(false);
                        
                        if (result != null && !result.isEmpty()) {
                            // Giới hạn hiển thị 10 tin ban đầu
                            List<NewsArticle> initialItems = result.size() > 10 ? 
                                    new ArrayList<>(result.subList(0, 10)) : new ArrayList<>(result);
                                
                            // Update adapter with news
                            newsAdapter.setNewsList(initialItems);
                            
                            // Lưu lại danh sách đầy đủ
                            fullResultList = new ArrayList<>(result);
                            
                            // Hiển thị thông báo
                            Toast.makeText(requireContext(), 
                                    String.format("Tìm thấy %d kết quả cho danh mục \"%s\"", result.size(), category),
                                    Toast.LENGTH_SHORT).show();
                                    
                            // Kiểm tra xem có nhiều hơn 10 tin không
                            if (result.size() > 10) {
                                resetLoadMoreButton();
                            } else {
                                btnLoadMore.setEnabled(false);
                                btnLoadMore.setText(R.string.no_more_results);
                            }
                        } else {
                            // No results, show dummy data
                            loadDummyData();
                            
                            // Reset danh sách đầy đủ
                            fullResultList = null;
                            
                            // Vô hiệu hóa nút tải thêm
                            btnLoadMore.setEnabled(false);
                            btnLoadMore.setText(R.string.no_more_results);
                            
                            // Hiển thị thông báo
                            Toast.makeText(requireContext(), 
                                    String.format("Không tìm thấy kết quả cho danh mục \"%s\"", category),
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
                        loadDummyData();
                        
                        // Reset danh sách đầy đủ
                        fullResultList = null;
                        
                        // Vô hiệu hóa nút tải thêm
                        btnLoadMore.setEnabled(false);
                        btnLoadMore.setText(R.string.no_more_results);
                        
                        // Hiển thị thông báo lỗi
                        Toast.makeText(requireContext(), 
                                "Lỗi tải danh mục: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // No internet connection
            loadDummyData();
            showLoading(false);
            
            // Reset danh sách đầy đủ
            fullResultList = null;
            
            // Vô hiệu hóa nút tải thêm
            btnLoadMore.setEnabled(false);
            btnLoadMore.setText(R.string.no_more_results);
            
            // Hiển thị thông báo không có Internet
            Toast.makeText(requireContext(),
                    "Không có kết nối internet. Hiển thị dữ liệu mẫu.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Map danh mục UI sang tham số API
     */
    private String mapCategoryToApiParam(String category) {
        if (category.equals(getString(R.string.category_business))) return "business";
        if (category.equals(getString(R.string.category_technology))) return "technology";
        if (category.equals(getString(R.string.category_entertainment))) return "entertainment";
        if (category.equals(getString(R.string.category_sports))) return "sports";
        if (category.equals(getString(R.string.category_science))) return "science";
        if (category.equals(getString(R.string.category_health))) return "health";
        return "general"; // Mặc định
    }
    
    /**
     * Tải dữ liệu mẫu khi không có kết nối hoặc API lỗi
     */
    private void loadDummyData() {
        // Tạo danh sách bài viết mẫu
        List<NewsArticle> dummyArticles = new ArrayList<>();
        
        for (int i = 1; i <= 10; i++) {
            NewsArticle article = new NewsArticle();
            article.setTitle("Dummy News Title " + i + " - This is a sample article");
            article.setDescription("This is a sample description for article " + i + ". It contains more text to display how the description field will appear in the application interface.");
            article.setUrlToImage("");
            article.setPublishedAt("2023-06-15T" + (10 + i % 12) + ":00:00Z");
            article.setUrl("https://example.com/article" + i);
            
            NewsArticle.Source source = new NewsArticle.Source();
            source.setName("Sample Source " + (i % 5 + 1));
            article.setSource(source);
            
            dummyArticles.add(article);
        }
        
        // Cập nhật adapter với dữ liệu mẫu
        if (newsAdapter != null) {
            newsAdapter.setNewsList(dummyArticles);
        }
        
        // Hoàn thành loading
        showLoading(false);
    }

    private void setupLoadMoreButton() {
        btnLoadMore.setOnClickListener(v -> {
            // Vô hiệu hóa nút trong khi đang tải
            btnLoadMore.setEnabled(false);
            btnLoadMore.setText(R.string.loading_more);
            
            // Tải thêm tin tức
            loadMoreNews();
        });
    }
    
    private void loadMoreNews() {
        // Kiểm tra kết nối internet
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            // Không có kết nối, thông báo và kích hoạt lại nút
            Toast.makeText(requireContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            resetLoadMoreButton();
            return;
        }
        
        // Nếu đang xem danh mục cụ thể
        if (!currentCategory.isEmpty() && !currentCategory.equals(getString(R.string.category_all))) {
            // Không thể phân trang với API getTopHeadlines, thông báo và reset
            Toast.makeText(requireContext(), 
                    "Không thể tải thêm cho danh mục này", 
                    Toast.LENGTH_SHORT).show();
            resetLoadMoreButton();
        } else {
            // Kiểm tra xem đã có danh sách kết quả đầy đủ chưa
            if (fullResultList != null && !fullResultList.isEmpty()) {
                // Lấy danh sách hiện tại
                List<NewsArticle> currentList = newsAdapter.getCurrentNewsList();
                
                // Nếu đã hiển thị hết danh sách
                if (currentList.size() >= fullResultList.size()) {
                    // Tải thêm từ API với trang tiếp theo
                    loadMoreFromApi();
                } else {
                    // Tính toán số lượng item cần thêm (tối đa 10)
                    int currentSize = currentList.size();
                    int endIndex = Math.min(currentSize + 10, fullResultList.size());
                    
                    // Lấy các item tiếp theo từ danh sách đầy đủ
                    List<NewsArticle> moreItems = new ArrayList<>(
                            fullResultList.subList(currentSize, endIndex));
                    
                    // Thêm vào danh sách hiển thị
                    newsAdapter.addMoreItems(moreItems);
                    
                    // Hiển thị thông báo
                    Toast.makeText(requireContext(), 
                            String.format("Đã tải thêm %d bài viết", moreItems.size()),
                            Toast.LENGTH_SHORT).show();
                    
                    // Kiểm tra xem đã tải hết danh sách chưa
                    if (endIndex >= fullResultList.size()) {
                        // Nếu đã hiển thị tất cả, chuyển sang tải từ API
                        currentPage++; // Tăng trang để chuẩn bị tải từ API
                        
                        // Có thể chọn một trong hai cách:
                        // 1. Vô hiệu hóa nút hoặc
                        // btnLoadMore.setEnabled(false);
                        // btnLoadMore.setText(R.string.no_more_results);
                        
                        // 2. Tải thêm từ API
                        loadMoreFromApi();
                    } else {
                        // Vẫn còn item để hiển thị
                        resetLoadMoreButton();
                    }
                }
            } else {
                // Không có danh sách đầy đủ, tải trực tiếp từ API
                loadMoreFromApi();
            }
        }
    }
    
    // Phương thức tải thêm từ API (giữ nguyên logic hiện tại)
    private void loadMoreFromApi() {
        // Tải thêm tin tức theo từ khóa với trang tiếp theo
        apiClient.getEverything(currentQuery, currentPage, new ApiClient.ApiCallback<List<NewsArticle>>() {
            @Override
            public void onSuccess(List<NewsArticle> result) {
                if (isAdded()) {
                    if (result != null && !result.isEmpty()) {
                        // Lọc tin trùng lặp và thêm vào danh sách hiện tại
                        List<NewsArticle> currentList = newsAdapter.getCurrentNewsList();
                        List<NewsArticle> filteredResults = filterDuplicateItems(currentList, result);
                        
                        // Cập nhật adapter
                        newsAdapter.addMoreItems(filteredResults);
                        
                        // Cập nhật danh sách đầy đủ
                        if (fullResultList == null) {
                            fullResultList = new ArrayList<>(result);
                        } else {
                            // Thêm các mục chưa trùng lặp vào danh sách đầy đủ
                            for (NewsArticle article : filteredResults) {
                                if (!fullResultList.contains(article)) {
                                    fullResultList.add(article);
                                }
                            }
                        }
                        
                        // Hiển thị thông báo
                        Toast.makeText(requireContext(), 
                                String.format("Đã tải thêm %d bài viết", filteredResults.size()),
                                Toast.LENGTH_SHORT).show();
                        
                        // Nếu không tải được thêm bài viết mới, vô hiệu hóa nút
                        if (filteredResults.isEmpty()) {
                            btnLoadMore.setEnabled(false);
                            btnLoadMore.setText(R.string.no_more_results);
                        } else {
                            resetLoadMoreButton();
                            currentPage++; // Tăng số trang cho lần tải tiếp theo
                        }
                    } else {
                        // Không còn bài viết để tải
                        Toast.makeText(requireContext(), 
                                "Không còn bài viết để tải",
                                Toast.LENGTH_SHORT).show();
                        btnLoadMore.setEnabled(false);
                        btnLoadMore.setText(R.string.no_more_results);
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (isAdded()) {
                    // Hiển thị thông báo lỗi
                    Toast.makeText(requireContext(), 
                            "Lỗi tải thêm: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
                    resetLoadMoreButton();
                }
            }
        });
    }
    
    private void resetLoadMoreButton() {
        // Kích hoạt lại nút và đặt lại văn bản
        if (btnLoadMore != null) {
            btnLoadMore.setEnabled(true);
            btnLoadMore.setText(R.string.load_more);
        }
    }
    
    /**
     * Lọc các bài viết trùng lặp
     */
    private List<NewsArticle> filterDuplicateItems(List<NewsArticle> currentList, List<NewsArticle> newItems) {
        List<NewsArticle> filteredList = new ArrayList<>();
        
        // Nếu danh sách hiện tại là null hoặc trống, trả về tất cả mục mới
        if (currentList == null || currentList.isEmpty()) {
            return newItems;
        }
        
        // Tạo một tập hợp các URL đã có
        Set<String> existingUrls = new HashSet<>();
        for (NewsArticle article : currentList) {
            if (article.getUrl() != null) {
                existingUrls.add(article.getUrl());
            }
        }
        
        // Thêm vào danh sách lọc chỉ các mục không trùng lặp
        for (NewsArticle article : newItems) {
            if (article.getUrl() != null && !existingUrls.contains(article.getUrl())) {
                filteredList.add(article);
            }
        }
        
        return filteredList;
    }
} 