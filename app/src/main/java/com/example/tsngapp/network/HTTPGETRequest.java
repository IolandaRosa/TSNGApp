package com.example.tsngapp.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Synchronous, only to use inside AsyncTasks
 */
public class HTTPGETRequest {
    private final String LOG_TAG = "HTTPGETRequest";
    private String token, targetURL;

    public HTTPGETRequest(String token, String url) {
        if (token != null && !token.isEmpty()) {
            this.token = token;
            this.targetURL = url;
        }
    }

    public String execute() {
        String result = "";
        HttpURLConnection conn = null;
        InputStream stream = null;
        BufferedReader reader = null;

        try {
            conn = (HttpURLConnection) new URL(targetURL).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                Log.d(LOG_TAG, String.format("%s returned code %d", targetURL, responseCode));
                return result;
            }

            stream = conn.getInputStream();

            if (stream != null) {
                reader = new BufferedReader(new InputStreamReader(stream));

                String l;
                StringBuilder sb = new StringBuilder();
                while ((l = reader.readLine()) != null) {
                    sb.append(l);
                }

                result = sb.toString();
            }

        } catch (Exception e) {
            Log.d(LOG_TAG, "Failed to execute request with " + e.getMessage());
        } finally {
            try {
                if (reader != null) reader.close();
                if (stream != null) stream.close();
                if (conn != null) conn.disconnect();
            } catch (IOException e) {
                Log.d(LOG_TAG, "Failed to clean resources. " + e.getMessage());
                e.printStackTrace();
            }
        }

        return result;
    }
}
