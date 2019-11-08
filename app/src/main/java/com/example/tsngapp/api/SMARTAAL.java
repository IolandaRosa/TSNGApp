package com.example.tsngapp.api;

import android.annotation.SuppressLint;

import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.network.AsyncTaskRequest;
import com.example.tsngapp.network.AsyncTaskResult;
import com.example.tsngapp.network.HTTPGETRequest;
import com.example.tsngapp.network.OnFailureListener;
import com.example.tsngapp.network.OnResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SMARTAAL {
    public static class GetDoorState extends AsyncTaskRequest<GetDoorState.Data> {
        public static class Data {
            private boolean inside;
            private Date updatedAt;

            public Data(boolean inside, Date updatedAt) {
                this.inside = inside;
                this.updatedAt = updatedAt;
            }

            public boolean isInside() {
                return inside;
            }

            public Date getUpdatedAt() {
                return updatedAt;
            }

            @SuppressLint("SimpleDateFormat")
            public static Data fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
                final String dateString = jsonObject.getString("date");
                final Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateString);
                final String value = jsonObject.getString("temperature");
                return new Data(!value.equals("L"), date);
            }
        }

        @SuppressLint("DefaultLocale")
        public GetDoorState(int elderId, String token,
                            OnResultListener<Data> resultListener,
                            OnFailureListener failureListener) {
            super(token, String.format(Constants.DOOR_STATE_URL, elderId),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<Data> request() throws JSONException, ParseException {
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String response = request.execute();
            JSONObject jsonResponse = new JSONObject(response);
            Data data = Data.fromJSON(jsonResponse);
            return new AsyncTaskResult<>(data);
        }
    }

    public static class GetDoorLastValues extends AsyncTaskRequest<JSONObject> {
        @SuppressLint("DefaultLocale")
        public GetDoorLastValues(int elderId, int numOfValues, String token,
                            OnResultListener<JSONObject> resultListener,
                            OnFailureListener failureListener) {
            super(String.format(Constants.DOOR_LAST_VALUES_URL, elderId, numOfValues),
                    token, resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<JSONObject> request() throws JSONException {
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String response = request.execute();
            JSONObject jsonResponse = new JSONObject(response);
            return new AsyncTaskResult<>(jsonResponse);
        }
    }

    public static class GetLightLastValues extends AsyncTaskRequest<String> {
        @SuppressLint("DefaultLocale")
        public GetLightLastValues(int elderId, int numOfValues, String token,
                            OnResultListener<String> resultListener,
                            OnFailureListener failureListener) {
            super(String.format(Constants.LIGHT_LAST_VALUES_URL, elderId, numOfValues),
                    token, resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<String> request() {
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String result = request.execute();
            return new AsyncTaskResult<>(result);
        }
    }

    public static class GetWindowLastValues extends AsyncTaskRequest<String> {
        @SuppressLint("DefaultLocale")
        public GetWindowLastValues(int elderId, int numOfValues, String token,
                            OnResultListener<String> resultListener,
                            OnFailureListener failureListener) {
            super(String.format(Constants.WINDOW_LAST_VALUES_URL, elderId, numOfValues),
                    token, resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<String> request() {
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String result = request.execute();
            return new AsyncTaskResult<>(result);
        }
    }

    public static class GetBedState extends AsyncTaskRequest<GetBedState.Data> {
        public static class Data {
            private boolean awake;
            private Date updatedAt;

            public Data(boolean awake, Date updatedAt) {
                this.awake = awake;
                this.updatedAt = updatedAt;
            }

            public boolean isAwake() {
                return awake;
            }

            public Date getUpdatedAt() {
                return updatedAt;
            }

            @SuppressLint("SimpleDateFormat")
            public static Data fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
                final String dateString = jsonObject.getString("date");
                final Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateString);
                final String value = jsonObject.getString("value");
                return new Data(value.equals("up"), date);
            }
        }

        @SuppressLint("DefaultLocale")
        public GetBedState(int elderId, String token,
                            OnResultListener<Data> resultListener,
                            OnFailureListener failureListener) {
            super(token, String.format(Constants.BED_STATE_URL, elderId),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<Data> request() throws JSONException, ParseException {
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String response = request.execute();
            JSONObject jsonResponse = new JSONObject(response);
            Data data = Data.fromJSON(jsonResponse);
            return new AsyncTaskResult<>(data);
        }
    }

    public static class GetBedLastValues extends AsyncTaskRequest<String> {
        @SuppressLint("DefaultLocale")
        public GetBedLastValues(int elderId, int numOfValues, String token,
                            OnResultListener<String> resultListener,
                            OnFailureListener failureListener) {
            super(String.format(Constants.BED_LAST_VALUES_URL, elderId, numOfValues),
                    token, resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<String> request() {
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String result = request.execute();
            return new AsyncTaskResult<>(result);
        }
    }

    public static class GetInternalTempLastValues extends AsyncTaskRequest<String> {
        @SuppressLint("DefaultLocale")
        public GetInternalTempLastValues(int elderId, int numOfValues, String token,
                            OnResultListener<String> resultListener,
                            OnFailureListener failureListener) {
            super(String.format(Constants.INTERNAL_TEMP_LAST_VALUES_URL, elderId, numOfValues),
                    token, resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<String> request() {
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String result = request.execute();
            return new AsyncTaskResult<>(result);
        }
    }

    public static class GetTemperatureValue extends AsyncTaskRequest<GetTemperatureValue.Data> {
        public static class Data {
            private int temperature;
            private String weather;
            private Date updatedAt;

            public Data(int temperature, String weather, Date updatedAt) {
                this.temperature = temperature;
                this.weather = weather;
                this.updatedAt = updatedAt;
            }

            public int getTemperature() {
                return temperature;
            }

            public String getWeather() {
                return weather;
            }

            public Date getUpdatedAt() {
                return updatedAt;
            }

            @SuppressLint("SimpleDateFormat")
            public static Data fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
                final String dateString = jsonObject.getString("date");
                final Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateString);
                return new Data(jsonObject.getInt("value"),
                        jsonObject.getString("weather_condition"), date);
            }
        }

        @SuppressLint("DefaultLocale")
        public GetTemperatureValue(int elderId, String token,
                            OnResultListener<Data> resultListener,
                            OnFailureListener failureListener) {
            super(token, String.format(Constants.TEMPERATURE_VALUE_URL, elderId),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<Data> request() throws JSONException, ParseException {
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String response = request.execute();
            JSONObject jsonResponse = new JSONObject(response);
            Data data = Data.fromJSON(jsonResponse);
            return new AsyncTaskResult<>(data);
        }
    }

    public static class GetCurrentLastValues extends AsyncTaskRequest<List<GetCurrentLastValues.Data>> {
        public static class Data {
            private int id;
            private int currentSensorId;
            private float value;
            private Date date;

            public int getId() {
                return id;
            }

            public int getCurrentSensorId() {
                return currentSensorId;
            }

            public float getValue() {
                return value;
            }

            public Date getDate() {
                return date;
            }

            public Data(int id, int currentSensorId, float value, Date date) {
                this.id = id;
                this.currentSensorId = currentSensorId;
                this.value = value;
                this.date = date;
            }

            @SuppressLint("SimpleDateFormat")
            public static Data fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
                final String dateString = jsonObject.getString("date");
                final Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateString);
                return new Data(
                        jsonObject.getInt("id"),
                        jsonObject.getInt("corrent_sensor_id"),
                        Float.valueOf(jsonObject.getString("temperature")),
                        date
                );
            }
        }

        @SuppressLint("DefaultLocale")
        public GetCurrentLastValues(int elderId, int numOfValues, String token,
                                    OnResultListener<List<Data>> resultListener,
                                    OnFailureListener failureListener) {
            super(token, String.format(Constants.ELECTRICAL_CURRENT_LAST_VALUES_URL, elderId, numOfValues),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<List<Data>> request() throws JSONException, ParseException {
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String response = request.execute();
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray data = jsonResponse.getJSONArray("data");

            List<Data> sensorDataList = new ArrayList<>();
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = data.getJSONObject(i);
                Data sensorData = Data.fromJSON(obj);
                sensorDataList.add(sensorData);
            }
            
            return new AsyncTaskResult<>(sensorDataList);
        }
    }
}
