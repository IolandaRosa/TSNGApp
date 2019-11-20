package com.example.tsngapp.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.tsngapp.network.AsyncTaskResult;
import com.example.tsngapp.network.OnFailureListener;
import com.example.tsngapp.network.OnResultListener;

import java.io.InputStream;
import java.net.URL;

public class DrawableUtil {
    public static class DownloadImageTask extends AsyncTask<Void, Void, AsyncTaskResult<Bitmap>> {
        private String imageUrl;
        private OnResultListener<Bitmap> resultListener;
        private OnFailureListener failureListener;

        public DownloadImageTask(String imageUrl, OnResultListener<Bitmap> resultListener) {
            this.imageUrl = imageUrl;
            this.resultListener = resultListener;
        }

        public void addOnFailureListener(OnFailureListener listener) {
            this.failureListener = listener;
        }

        protected AsyncTaskResult<Bitmap> doInBackground(Void... voids) {
            Bitmap bmp;
            try {
                InputStream in = new URL(imageUrl).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e(Constants.DEBUG_TAG, "Error downloading image: " + e.getMessage());
                return new AsyncTaskResult<>(e);
            }
            return new AsyncTaskResult<>(bmp);
        }
        protected void onPostExecute(AsyncTaskResult<Bitmap> result) {
            if (result.hasError()) {
                if (failureListener != null) {
                    failureListener.onFailure(result.getError());
                }
            } else {
                resultListener.onResult(result.getResult());
            }
        }
    }
}
