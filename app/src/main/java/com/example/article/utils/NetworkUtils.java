package com.example.article.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    /**
     * Kiểm tra xem thiết bị có kết nối internet không
     * @param context Context của ứng dụng
     * @return true nếu có kết nối internet, false nếu không
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities == null) {
                return false;
            }
            
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || 
                   capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || 
                   capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
} 