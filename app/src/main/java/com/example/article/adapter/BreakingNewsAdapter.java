package com.example.article.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.article.R;
import com.example.article.api.model.NewsArticle;
import com.example.article.utils.DateUtils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class BreakingNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_SHIMMER = 1;
    
    private List<NewsArticle> breakingNewsList;
    private final Context context;
    private final OnBreakingNewsClickListener listener;
    private boolean isLoading = false;

    public BreakingNewsAdapter(OnBreakingNewsClickListener listener) {
        this.breakingNewsList = new ArrayList<>();
        this.context = null;
        this.listener = listener;
    }

    public BreakingNewsAdapter(Context context) {
        this.breakingNewsList = new ArrayList<>();
        this.context = context;
        this.listener = null;
    }

    public interface OnBreakingNewsClickListener {
        void onBreakingNewsClick(NewsArticle article);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SHIMMER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_shimmer_breaking_news, parent, false);
            return new ShimmerViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_breaking_news, parent, false);
            return new BreakingNewsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BreakingNewsViewHolder && position < breakingNewsList.size()) {
            NewsArticle article = breakingNewsList.get(position);
            BreakingNewsViewHolder viewHolder = (BreakingNewsViewHolder) holder;
            
            // Set title
            viewHolder.titleTextView.setText(article.getTitle());
            
            // Set source and time
            String sourceText = article.getSource() != null ? article.getSource().getName() : "Unknown";
            viewHolder.sourceTextView.setText(sourceText);
            
            // Format published date
            String formattedDate = DateUtils.formatPublishedDate(article.getPublishedAt());
            viewHolder.dateTextView.setText(formattedDate);
            
            // Set background image
            if (article.getUrlToImage() != null && !article.getUrlToImage().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(article.getUrlToImage())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .centerCrop()
                        .into(viewHolder.imageView);
            } else {
                viewHolder.imageView.setImageResource(R.drawable.placeholder_image);
            }
            
            // Set click listener
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBreakingNewsClick(article);
                }
            });
        } else if (holder instanceof ShimmerViewHolder) {
            ShimmerViewHolder shimmerViewHolder = (ShimmerViewHolder) holder;
            shimmerViewHolder.shimmerFrameLayout.startShimmer();
        }
    }

    @Override
    public int getItemCount() {
        return isLoading ? 3 : breakingNewsList.size(); // Show 3 shimmer items when loading
    }
    
    @Override
    public int getItemViewType(int position) {
        if (isLoading) {
            return VIEW_TYPE_SHIMMER;
        }
        return VIEW_TYPE_ITEM;
    }

    public void setBreakingNewsList(List<NewsArticle> articles) {
        isLoading = false;
        
        // Limit to maximum 5 articles
        if (articles.size() > 5) {
            this.breakingNewsList = new ArrayList<>(articles.subList(0, 5));
        } else {
            this.breakingNewsList = new ArrayList<>(articles);
        }
        
        notifyDataSetChanged();
    }
    
    public void showLoading() {
        isLoading = true;
        notifyDataSetChanged();
    }
    
    public void hideLoading() {
        isLoading = false;
        notifyDataSetChanged();
    }

    public void setDummyData() {
        isLoading = false;
        this.breakingNewsList = new ArrayList<>();
        
        // Categories for dummy data
        String[] categories = {"Politics", "Technology", "Health", "Sports", "Entertainment"};
        
        // Create exact 5 dummy articles
        for (int i = 0; i < 5; i++) {
            NewsArticle article = new NewsArticle();
            article.setTitle("Breaking News Title " + (i + 1) + " - Important update about current events");
            
            NewsArticle.Source source = new NewsArticle.Source();
            source.setId("source-" + i);
            source.setName("News Source " + (i + 1));
            article.setSource(source);
            
            article.setUrlToImage("");  // Empty for placeholder
            article.setPublishedAt("2023-06-" + (10 + i) + "T10:30:00Z");
            article.setCategory(categories[i]);
            
            breakingNewsList.add(article);
        }
        
        notifyDataSetChanged();
    }

    static class BreakingNewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView sourceTextView, titleTextView, dateTextView;

        BreakingNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            sourceTextView = itemView.findViewById(R.id.sourceTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
    
    static class ShimmerViewHolder extends RecyclerView.ViewHolder {
        ShimmerFrameLayout shimmerFrameLayout;
        
        ShimmerViewHolder(@NonNull View itemView) {
            super(itemView);
            shimmerFrameLayout = (ShimmerFrameLayout) itemView;
        }
    }
} 