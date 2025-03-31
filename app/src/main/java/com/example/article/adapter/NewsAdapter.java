package com.example.article.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.article.R;
import com.example.article.api.ApiClient;
import com.example.article.api.model.NewsArticle;
import com.example.article.utils.DateUtils;
import com.example.article.utils.ArticleUtils;
import com.example.article.services.NewsSummaryService;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    
    private List<NewsArticle> newsList;
    private final OnNewsClickListener listener;
    private boolean isLoadingMore = false;
    private ApiClient apiClient;
    private String currentQuery = "trending"; // Từ khóa mặc định
    private int currentPage = 1;
    private static final int ITEMS_PER_PAGE = 10;
    private static final String TAG = "NewsAdapter";

    public NewsAdapter(OnNewsClickListener listener) {
        this.newsList = new ArrayList<>();
        this.listener = listener;
    }

    public interface OnNewsClickListener {
        void onNewsClick(NewsArticle article);
        void onShareClick(NewsArticle article);
        void onBookmarkClick(NewsArticle article);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (apiClient == null) {
            apiClient = ApiClient.getInstance(parent.getContext());
        }
        
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
            return new NewsViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shimmer_news, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NewsViewHolder) {
            NewsArticle article = newsList.get(position);
            NewsViewHolder viewHolder = (NewsViewHolder) holder;
            
            viewHolder.titleTextView.setText(article.getTitle());
            
            // Kiểm tra và hiển thị source nếu có
            if (article.getSource() != null && article.getSource().getName() != null) {
                viewHolder.sourceTextView.setText(article.getSource().getName());
                viewHolder.sourceTextView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.sourceTextView.setVisibility(View.GONE);
            }
            
            // Định dạng thời gian
            String formattedDate = DateUtils.formatPublishedDate(article.getPublishedAt());
            // Hiển thị ngày xuất bản trong description
            
            // Hiển thị description nếu có
            if (article.getDescription() != null && !article.getDescription().isEmpty()) {
                viewHolder.descriptionTextView.setVisibility(View.VISIBLE);
                viewHolder.descriptionTextView.setText(article.getDescription());
            } else {
                viewHolder.descriptionTextView.setVisibility(View.GONE);
            }
            
            // Hiển thị tóm tắt nếu có
            if (article.getSummary() != null && !article.getSummary().isEmpty()) {
                viewHolder.summaryTextView.setVisibility(View.VISIBLE);
                viewHolder.summaryTextView.setText(article.getSummary());
            } else {
                viewHolder.summaryTextView.setVisibility(View.GONE);
                
                // Tạo tóm tắt nếu chưa có
                NewsSummaryService summaryService = new NewsSummaryService(viewHolder.itemView.getContext());
                summaryService.generateSummary(article, new NewsSummaryService.SummaryCallback() {
                    @Override
                    public void onSuccess(String summary) {
                        article.setSummary(summary);
                        notifyItemChanged(position);
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("NewsAdapter", "Error generating summary: " + error);
                    }
                });
            }
            
            // Load ảnh với Glide
            if (article.getUrlToImage() != null && !article.getUrlToImage().isEmpty()) {
                Glide.with(viewHolder.itemView.getContext())
                        .load(article.getUrlToImage())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(viewHolder.imageView);
            } else {
                viewHolder.imageView.setImageResource(R.drawable.placeholder_image);
            }
            
            // Set click listeners
            viewHolder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNewsClick(article);
                }
            });

            // Xử lý sự kiện click nút share
            viewHolder.btnShare.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onShareClick(article);
                }
            });

            // Xử lý sự kiện click nút bookmark
            viewHolder.btnBookmark.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookmarkClick(article);
                }
            });

            // Cập nhật trạng thái bookmark
            Context context = viewHolder.itemView.getContext();
            boolean isSaved = ArticleUtils.isArticleSaved(context, article);
            viewHolder.btnBookmark.setIcon(context.getDrawable(isSaved ? 
                R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_border));
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.shimmerFrameLayout.startShimmer();
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
    
    @Override
    public int getItemViewType(int position) {
        return newsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    /**
     * Thiết lập danh sách tin tức
     * @param newsList Danh sách tin tức mới
     */
    public void setNewsList(List<NewsArticle> newsList) {
        this.newsList = newsList != null ? newsList : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void loadMoreItems() {
        if (isLoadingMore) return;
        
        isLoadingMore = true;
        
        // Thêm placeholder để hiển thị loading
        newsList.add(null);
        notifyItemInserted(newsList.size() - 1);
        
        currentPage++;
        
        // Load thêm dữ liệu từ API
        apiClient.getEverything(currentQuery, currentPage, new ApiClient.ApiCallback<List<NewsArticle>>() {
            @Override
            public void onSuccess(List<NewsArticle> result) {
                // Xóa item loading
                int loadingPosition = newsList.size() - 1;
                if (loadingPosition >= 0) {
                    newsList.remove(loadingPosition);
                    notifyItemRemoved(loadingPosition);
                }
                
                if (result != null && !result.isEmpty()) {
                    // Bắt đầu từ vị trí cuối cùng hiện tại
                    int startPosition = newsList.size();
                    
                    // Thêm dữ liệu mới
                    newsList.addAll(result);
                    
                    // Thông báo thay đổi
                    notifyItemRangeInserted(startPosition, result.size());
                    
                    // Hiển thị thông báo đã tải thêm
                    if (result.size() > 0) {
                        Toast.makeText(apiClient.getContext(), 
                                "Đã tải thêm " + result.size() + " bài viết", 
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Thông báo không có thêm bài viết
                    Toast.makeText(apiClient.getContext(), 
                            "Không còn bài viết để tải", 
                            Toast.LENGTH_SHORT).show();
                }
                
                isLoadingMore = false;
            }

            @Override
            public void onError(String errorMessage) {
                // Xóa item loading
                int loadingPosition = newsList.size() - 1;
                if (loadingPosition >= 0) {
                    newsList.remove(loadingPosition);
                    notifyItemRemoved(loadingPosition);
                }
                
                // Hiển thị thông báo lỗi
                Toast.makeText(apiClient.getContext(), 
                        "Không thể tải thêm: " + errorMessage, 
                        Toast.LENGTH_SHORT).show();
                
                isLoadingMore = false;
            }
        });
    }
    
    public void setSearchQuery(String query) {
        if (!this.currentQuery.equals(query)) {
            this.currentQuery = query;
            this.currentPage = 1;
            this.newsList.clear();
            notifyDataSetChanged();
            
            // Thêm loading placeholder
            this.newsList.add(null);
            notifyItemInserted(0);
            
            // Load dữ liệu mới
            apiClient.getEverything(query, new ApiClient.ApiCallback<List<NewsArticle>>() {
                @Override
                public void onSuccess(List<NewsArticle> result) {
                    newsList.clear();
                    
                    if (result != null && !result.isEmpty()) {
                        newsList.addAll(result);
                    } else {
                        setDummyData();
                    }
                    
                    notifyDataSetChanged();
                }

                @Override
                public void onError(String errorMessage) {
                    newsList.clear();
                    setDummyData();
                    notifyDataSetChanged();
                    
                    Toast.makeText(apiClient.getContext(), 
                            "Không thể tìm tin tức cho: " + query, 
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void setDummyData() {
        newsList.clear();
        
        // Create dummy data for testing - 10 items
        for (int i = 1; i <= 10; i++) {
            NewsArticle article = new NewsArticle();
            article.setTitle("Sample Article Title " + i + " - This is a longer title to test how wrapping works in the layout");
            article.setDescription("This is a sample description for article " + i + ". It contains more text to display how the description field will appear in the application interface.");
            article.setUrlToImage("");
            article.setPublishedAt("2023-06-15T" + (10 + i) + ":00:00Z");
            
            // Khởi tạo Source object
            NewsArticle.Source source = new NewsArticle.Source();
            source.setName("Sample News " + i);
            article.setSource(source);
            
            newsList.add(article);
        }
        
        notifyDataSetChanged();
    }

    /**
     * Lấy danh sách tin tức hiện tại
     * @return Danh sách tin tức
     */
    public List<NewsArticle> getCurrentNewsList() {
        return newsList;
    }
    
    /**
     * Thêm nhiều bài viết vào danh sách hiện tại
     * @param moreItems Danh sách bài viết cần thêm
     */
    public void addMoreItems(List<NewsArticle> moreItems) {
        if (moreItems != null && !moreItems.isEmpty()) {
            int positionStart = newsList.size();
            newsList.addAll(moreItems);
            notifyItemRangeInserted(positionStart, moreItems.size());
        }
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView sourceTextView;
        TextView descriptionTextView;
        TextView summaryTextView;
        MaterialButton btnShare;
        MaterialButton btnBookmark;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            sourceTextView = itemView.findViewById(R.id.sourceTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
        }
    }
    
    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ShimmerFrameLayout shimmerFrameLayout;
        
        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            shimmerFrameLayout = (ShimmerFrameLayout) itemView;
        }
    }
} 