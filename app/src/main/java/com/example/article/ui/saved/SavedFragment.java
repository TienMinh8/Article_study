package com.example.article.ui.saved;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.article.R;
import com.example.article.adapter.NewsAdapter;
import com.example.article.api.model.NewsArticle;
import com.example.article.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment implements NewsAdapter.OnNewsClickListener {

    private RecyclerView recyclerViewSaved;
    private TextView tvEmptySaved;
    private NewsAdapter savedArticlesAdapter;
    private List<NewsArticle> savedArticles = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Khởi tạo các thành phần UI và sự kiện
        initViews(view);
        setupRecyclerView();
        loadSavedArticles();
        
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
    
    private void initViews(View view) {
        recyclerViewSaved = view.findViewById(R.id.recyclerViewSaved);
        tvEmptySaved = view.findViewById(R.id.tvEmptySaved);
    }
    
    private void setupRecyclerView() {
        savedArticlesAdapter = new NewsAdapter(this);
        recyclerViewSaved.setAdapter(savedArticlesAdapter);
    }
    
    private void loadSavedArticles() {
        // Tải dữ liệu mẫu
        loadSampleData();
        
        // Cập nhật UI dựa trên dữ liệu
        updateEmptyView();
        savedArticlesAdapter.setNewsList(savedArticles);
    }
    
    private void loadSampleData() {
        // Xóa dữ liệu cũ
        savedArticles.clear();
        
        // Thêm một số bài viết mẫu đã lưu
        NewsArticle article1 = new NewsArticle();
        article1.setTitle("Saved Article 1");
        article1.setDescription("This is a saved article about technology");
        article1.setUrl("https://example.com/tech");
        article1.setPublishedAt("2023-06-15");
        article1.setUrlToImage("https://via.placeholder.com/150");
        NewsArticle.Source source1 = new NewsArticle.Source();
        source1.setName("Tech News");
        article1.setSource(source1);
        savedArticles.add(article1);
        
        NewsArticle article2 = new NewsArticle();
        article2.setTitle("Saved Article 2");
        article2.setDescription("This is a saved article about health");
        article2.setUrl("https://example.com/health");
        article2.setPublishedAt("2023-06-14");
        article2.setUrlToImage("https://via.placeholder.com/150");
        NewsArticle.Source source2 = new NewsArticle.Source();
        source2.setName("Health News");
        article2.setSource(source2);
        savedArticles.add(article2);
    }
    
    private void updateEmptyView() {
        if (savedArticles.isEmpty()) {
            tvEmptySaved.setVisibility(View.VISIBLE);
            recyclerViewSaved.setVisibility(View.GONE);
        } else {
            tvEmptySaved.setVisibility(View.GONE);
            recyclerViewSaved.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNewsClick(NewsArticle article) {
        // Xử lý sự kiện khi người dùng nhấn vào bài viết đã lưu
        Toast.makeText(getContext(), "Đã chọn: " + article.getTitle(), Toast.LENGTH_SHORT).show();
    }
} 