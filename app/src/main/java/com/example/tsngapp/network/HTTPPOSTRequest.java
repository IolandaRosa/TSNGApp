package com.example.tsngapp.network;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPPOSTRequest {
    private final String LOG_TAG = "HTTPPOSTRequest";
    private String token, targetUrl;
    private JSONObject dataToPost;

    public HTTPPOSTRequest(String token, String targetUrl, JSONObject dataToPost) {
        this.token = token;
        this.targetUrl = targetUrl;
        if (dataToPost!=null){
            this.dataToPost = dataToPost;
        }
    }

    public String execute() {
        String result = "";
        HttpURLConnection conn = null;
        InputStream stream = null;
        BufferedReader reader = null;

        try {

            conn = (HttpURLConnection) new URL(targetUrl).openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(dataToPost.toString());
            writer.flush();

            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode > 199 && responseCode < 300) {
                stream = conn.getInputStream();

                //Traduz para string
                if(stream != null){
                    reader = new BufferedReader(new InputStreamReader(stream));

                    result = this.convertToString(result, reader);
                    return result;
                }

            } else {
                Log.d(LOG_TAG, String.format("%s returned code %d", targetUrl, responseCode));
                return result;
            }

        } catch(Exception e){
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

    private String convertToString(String result, BufferedReader reader) {
        String tempString = "";

        try{
            while (true){
                tempString = reader.readLine();
                if(tempString == null){

                    return result;
                }
                result += tempString;
            }
        }
        catch (Exception ex){
            Log.d(LOG_TAG,"Error in convertToString " + ex.getMessage());
        }

        return "";
    }
}
