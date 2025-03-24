package com.example.article.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.article.R;
import com.example.article.api.model.NewsArticle;
import com.example.article.utils.ArticleUtils;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    private List<NewsArticle> articles = new ArrayList<>();
    private OnSliderClickListener listener;

    public interface OnSliderClickListener {
        void onSliderClick(NewsArticle article);
    }

    public SliderAdapter(OnSliderClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        NewsArticle article = articles.get(position);
        holder.bind(article, listener);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void setArticles(List<NewsArticle> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView titleView;
        private ImageButton shareButton;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleView = itemView.findViewById(R.id.titleView);
            shareButton = itemView.findViewById(R.id.shareButton);
        }

        public void bind(final NewsArticle article, final OnSliderClickListener listener) {
            titleView.setText(article.getTitle());
            
            if (article.getUrlToImage() != null) {
                Glide.with(itemView.getContext())
                    .load(article.getUrlToImage())
                    .into(imageView);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSliderClick(article);
                }
            });

            shareButton.setOnClickListener(v -> {
                ArticleUtils.shareArticle(itemView.getContext(), article);
            });
        }
    }
} 