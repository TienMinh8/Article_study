package com.example.article.utils;

import android.os.Handler;
import android.os.Looper;
import android.text.Html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HtmlParser {
    private final ExecutorService executor;
    private final Handler mainHandler;
    private boolean isCancelled;

    public interface OnContentLoadedListener {
        void onContentLoaded(String title, String imageUrl, String content);
        void onError(String error);
    }

    public HtmlParser() {
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        isCancelled = false;
    }

    public void parseUrl(String url, OnContentLoadedListener listener) {
        executor.execute(() -> {
            try {
                if (isCancelled) return;

                // Connect to URL with timeout
                Document doc = Jsoup.connect(url)
                        .timeout(10000)
                        .get();

                if (isCancelled) return;

                // Get title
                String title = doc.title();

                // Get featured image
                String imageUrl = null;
                Element ogImage = doc.select("meta[property=og:image]").first();
                if (ogImage != null) {
                    imageUrl = ogImage.attr("content");
                } else {
                    // Try to find first image in article
                    Element firstImage = doc.select("article img, .article img, .content img").first();
                    if (firstImage != null) {
                        imageUrl = firstImage.absUrl("src");
                    }
                }

                // Get main content
                Element contentElement = doc.select("article, .article, .content, .post-content").first();
                if (contentElement == null) {
                    contentElement = doc.body();
                }

                // Clean up content
                if (contentElement != null) {
                    // Remove unwanted elements
                    contentElement.select("script, style, iframe, .advertisement, .social-share, .related-posts").remove();

                    // Clean up remaining content
                    String content = contentElement.text();
                    content = Html.fromHtml(content).toString().trim();

                    // Format paragraphs
                    content = content.replaceAll("\\n{3,}", "\n\n");

                    final String finalImageUrl = imageUrl;
                    final String finalContent = content;

                    if (!isCancelled) {
                        mainHandler.post(() -> listener.onContentLoaded(title, finalImageUrl, finalContent));
                    }
                } else {
                    if (!isCancelled) {
                        mainHandler.post(() -> listener.onError("Could not find article content"));
                    }
                }
            } catch (IOException e) {
                if (!isCancelled) {
                    mainHandler.post(() -> listener.onError(e.getMessage()));
                }
            }
        });
    }

    public void cancel() {
        isCancelled = true;
        executor.shutdownNow();
    }
} 