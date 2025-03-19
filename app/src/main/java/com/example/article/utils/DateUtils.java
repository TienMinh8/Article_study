package com.example.article.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Utility class to format dates consistently across the app
 */
public class DateUtils {

    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    private static final SimpleDateFormat OUTPUT_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    
    static {
        ISO_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        OUTPUT_FORMAT.setTimeZone(TimeZone.getDefault());
    }
    
    /**
     * Formats a published date string from ISO 8601 format to a user-friendly relative time string
     * @param publishedAt ISO 8601 formatted date string (e.g. "2023-05-20T15:30:00Z")
     * @return A string like "2 hours ago", "Yesterday", or "May 20, 2023"
     */
    public static String formatPublishedDate(String publishedAt) {
        if (publishedAt == null || publishedAt.isEmpty()) {
            return "Unknown date";
        }
        
        try {
            // Parse the ISO date
            Date date = ISO_FORMAT.parse(publishedAt);
            if (date == null) {
                return "Invalid date";
            }
            
            // Calculate time difference in milliseconds
            long diffInMillis = System.currentTimeMillis() - date.getTime();
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
            
            // Return a relative string based on the difference
            if (diffInMinutes < 60) {
                return diffInMinutes + " phút trước";
            } else if (diffInHours < 24) {
                return diffInHours + " giờ trước";
            } else if (diffInDays < 2) {
                return "Hôm qua";
            } else if (diffInDays < 7) {
                return diffInDays + " ngày trước";
            } else {
                return OUTPUT_FORMAT.format(date);
            }
            
        } catch (ParseException e) {
            // Backup parsing logic for different format
            try {
                // Try alternate format (some APIs might use different format)
                SimpleDateFormat alternateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = alternateFormat.parse(publishedAt.substring(0, Math.min(10, publishedAt.length())));
                if (date != null) {
                    return OUTPUT_FORMAT.format(date);
                }
            } catch (Exception ignored) {
                // Just fall through to default return
            }
            
            return "Unknown date";
        }
    }
} 