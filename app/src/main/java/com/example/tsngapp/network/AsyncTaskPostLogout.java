package com.example.tsngapp.network;

import android.os.AsyncTask;
import android.util.Log;

import com.example.tsngapp.helpers.Constants;

import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncTaskPostLogout extends AsyncTask<String, Integer, String> {

    private final String LOG_TAG = "AsyncTaskPostLogout";
    private AsyncResponse listener;
    private String token;

    public AsyncTaskPostLogout(AsyncResponse listener, String token) {
        this.listener = listener;
        this.token = token;
    }

    @Override
    protected String doInBackground(String... urlStrings) {
        String result = "";
        HttpURLConnection conn = null;

        try {

            //Configura a conexão
            URL url = new URL(urlStrings[0]);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.connect();

            //Ve a resposta
            int responseCode = conn.getResponseCode();

            if (responseCode > 199 && responseCode < 300) {

                result = Constants.HTTP_OK;
            } else {
                result = Constants.HTTP_ERROR;
            }

        } catch (Exception ex) {
            Log.d(LOG_TAG, "The doInBckground for login failed with " + ex.getMessage());
        } finally {

            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result != null){
            listener.onTaskDone(result);
        }
    }

}
