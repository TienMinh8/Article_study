package com.example.article.ui.saved;

import android.app.AlertDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.article.R;
import com.example.article.adapter.NewsAdapter;
import com.example.article.api.model.NewsArticle;
import com.example.article.utils.ArticleUtils;

import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment implements NewsAdapter.OnNewsClickListener {

    private RecyclerView recyclerViewSaved;
    private View emptyView;
    private NewsAdapter savedArticlesAdapter;
    private List<NewsArticle> savedArticles = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton btnDeleteAll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupRecyclerView();
        setupListeners();
        loadSavedArticles();
    }
    
    private void initViews(View view) {
        recyclerViewSaved = view.findViewById(R.id.recyclerViewSaved);
        emptyView = view.findViewById(R.id.emptyView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        btnDeleteAll = view.findViewById(R.id.btnDeleteAll);
        
        // Setup back button
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }
    
    private void setupRecyclerView() {
        savedArticlesAdapter = new NewsAdapter(this);
        recyclerViewSaved.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSaved.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, 
                                     @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = (int) (5 * getResources().getDisplayMetrics().density);
                outRect.bottom = (int) (5 * getResources().getDisplayMetrics().density);
            }
        });
        recyclerViewSaved.setAdapter(savedArticlesAdapter);
    }
    
    private void setupListeners() {
        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadSavedArticles);
        
        // Setup delete all button
        btnDeleteAll.setOnClickListener(v -> showDeleteAllConfirmDialog());
    }
    
    private void loadSavedArticles() {
        if (getContext() != null) {
            savedArticles = ArticleUtils.getSavedArticles(getContext());
            updateUI();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    
    private void updateUI() {
        if (savedArticles.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerViewSaved.setVisibility(View.GONE);
            btnDeleteAll.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerViewSaved.setVisibility(View.VISIBLE);
            btnDeleteAll.setVisibility(View.VISIBLE);
            savedArticlesAdapter.setNewsList(savedArticles);
        }
    }

    @Override
    public void onNewsClick(NewsArticle article) {
        Bundle bundle = new Bundle();
        bundle.putString("url", article.getUrl());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_saved_to_detail, bundle);
    }

    @Override
    public void onShareClick(NewsArticle article) {
        ArticleUtils.shareArticle(requireContext(), article);
    }

    @Override
    public void onBookmarkClick(NewsArticle article) {
        showDeleteConfirmDialog(article);
    }
    
    private void showDeleteConfirmDialog(NewsArticle article) {
        new AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_confirm)
            .setPositiveButton(R.string.yes, (dialog, which) -> {
                ArticleUtils.unsaveArticle(requireContext(), article);
                Toast.makeText(getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                loadSavedArticles();
            })
            .setNegativeButton(R.string.no, null)
            .show();
    }
    
    private void showDeleteAllConfirmDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_all_confirm)
            .setPositiveButton(R.string.yes, (dialog, which) -> {
                if (getContext() != null) {
                    ArticleUtils.clearSavedArticles(getContext());
                    Toast.makeText(getContext(), R.string.all_deleted, Toast.LENGTH_SHORT).show();
                    loadSavedArticles();
                }
            })
            .setNegativeButton(R.string.no, null)
            .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSavedArticles();
    }
} 