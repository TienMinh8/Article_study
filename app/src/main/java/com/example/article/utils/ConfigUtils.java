package com.example.article.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigUtils {
    private static final String TAG = "ConfigUtils";
    private static final String CONFIG_FILE = "config.json";
    private static JSONObject configJson;

    public static JSONObject getConfigJson(Context context) {
        if (configJson == null) {
            loadConfigFile(context);
        }
        return configJson;
    }

    private static void loadConfigFile(Context context) {
        try {
            String jsonString = readFileFromAssets(context, CONFIG_FILE);
            configJson = new JSONObject(jsonString);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing config JSON: " + e.getMessage());
            configJson = new JSONObject();
        } catch (IOException e) {
            Log.e(TAG, "Error reading config file: " + e.getMessage());
            configJson = new JSONObject();
        }
    }

    private static String readFileFromAssets(Context context, String fileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = context.getAssets().open(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        
        bufferedReader.close();
        return stringBuilder.toString();
    }
} 