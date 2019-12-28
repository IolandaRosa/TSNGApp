package com.example.tsngapp.network;

import android.os.AsyncTask;

import com.example.tsngapp.network.AsyncTaskResult;
import com.example.tsngapp.network.OnResultListener;
import com.example.tsngapp.network.OnFailureListener;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;

public abstract class AsyncTaskRequest<T> extends AsyncTask<Void, Void, AsyncTaskResult<T>> {
    protected String url, token;
    private OnResultListener<T> resultListener;
    private OnFailureListener failureListener;

    public AsyncTaskRequest(String token, String url, OnResultListener<T> resultListener,
                            OnFailureListener failureListener) {
        this.token = token;
        this.url = url;
        this.resultListener = resultListener;
        this.failureListener = failureListener;
    }

    @Override
    protected AsyncTaskResult<T> doInBackground(Void... voids) {
        try {
            return request();
        } catch (Exception e) {
            return new AsyncTaskResult<>(e);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<T> result) {
        if (result.hasError()) {
            failureListener.onFailure(result.getError());
        } else {
            resultListener.onResult(result.getResult());
        }
    }

    protected abstract AsyncTaskResult<T> request();
}
