package com.example.tsngapp.api;

import android.annotation.SuppressLint;

import com.example.tsngapp.api.model.SimpleValueSensor;
import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.model.Elder;
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
import java.util.LinkedList;
import java.util.List;

public class SMARTAAL {
    private static JSONObject performHttpGetRequest(String token, String url) throws JSONException {
        HTTPGETRequest request = new HTTPGETRequest(token, url);
        String response = request.execute();
        if (response.length() > 0) {
            return new JSONObject(response);
        }
        return null;
    }

    public static class ElderInfo extends AsyncTaskRequest<Elder> {
        @SuppressLint("DefaultLocale")
        public ElderInfo(int userId, String token,
                         OnResultListener<Elder> resultListener,
                         OnFailureListener failureListener) {
            super(token, String.format(Constants.ELDER_INFO_URL, userId),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<Elder> request() throws JSONException, ParseException {
            final JSONObject response = performHttpGetRequest(token, url);
            if (response != null) {
                Elder data = Elder.fromJSON(response);
                return new AsyncTaskResult<>(data);
            }
            return new AsyncTaskResult<>(new NullPointerException("No data returned from request"));
        }
    }

    public static class DoorState extends AsyncTaskRequest<DoorState.Data> {
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
                final String value = jsonObject.getString("value");
                return new Data(!value.equals("L"), date);
            }
        }

        @SuppressLint("DefaultLocale")
        public DoorState(int elderId, String token,
                         OnResultListener<Data> resultListener,
                         OnFailureListener failureListener) {
            super(token, String.format(Constants.DOOR_STATE_URL, elderId),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<Data> request() throws JSONException, ParseException {
            final JSONObject response = performHttpGetRequest(token, url);
            if (response != null) {
                Data data = Data.fromJSON(response);
                return new AsyncTaskResult<>(data);
            }
            return new AsyncTaskResult<>(new NullPointerException("No data returned from request"));
        }
    }

    public static class DoorLastValues extends AsyncTaskRequest<List<DoorState.Data>> {
        @SuppressLint("DefaultLocale")
        public DoorLastValues(int elderId, int numOfValues, String token,
                              OnResultListener<List<DoorState.Data>> resultListener,
                              OnFailureListener failureListener) {
            super(token, String.format(Constants.DOOR_LAST_VALUES_URL, elderId, numOfValues),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<List<DoorState.Data>> request() throws JSONException, ParseException {
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String response = request.execute();

            if (response.length() > 0) {
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray data = jsonResponse.getJSONArray("data");

                List<DoorState.Data> sensorDataList = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    DoorState.Data sensorData = DoorState.Data.fromJSON(obj);
                    sensorDataList.add(sensorData);
                }
                return new AsyncTaskResult<>(sensorDataList);
            }

            return new AsyncTaskResult<>(new LinkedList<>());
        }
    }

    public static class LightLastValues extends AsyncTaskRequest<String> {
        @SuppressLint("DefaultLocale")
        public LightLastValues(int elderId, int numOfValues, String token,
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

    public static class WindowLastValues extends AsyncTaskRequest<String> {
        @SuppressLint("DefaultLocale")
        public WindowLastValues(int elderId, int numOfValues, String token,
                                OnResultListener<String> resultListener,
                                OnFailureListener failureListener) {
            super(token, String.format(Constants.WINDOW_LAST_VALUES_URL, elderId, numOfValues),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<String> request() {
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String result = request.execute();
            return new AsyncTaskResult<>(result);
        }
    }

    public static class BedState extends AsyncTaskRequest<BedState.Data> {
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
        public BedState(int elderId, String token,
                        OnResultListener<Data> resultListener,
                        OnFailureListener failureListener) {
            super(token, String.format(Constants.BED_STATE_URL, elderId),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<Data> request() throws JSONException, ParseException {
            final JSONObject response = performHttpGetRequest(token, url);
            if (response != null) {
                Data data = Data.fromJSON(response);
                return new AsyncTaskResult<>(data);
            }
            return new AsyncTaskResult<>(new NullPointerException("No data returned from request"));
        }
    }

    public static class BedLastValues extends AsyncTaskRequest<List<BedState.Data>> {
        @SuppressLint("DefaultLocale")
        public BedLastValues(int elderId, int numOfValues, String token,
                             OnResultListener<List<BedState.Data>> resultListener,
                             OnFailureListener failureListener) {
            super(token, String.format(Constants.BED_LAST_VALUES_URL, elderId, numOfValues),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<List<BedState.Data>> request() throws JSONException, ParseException {
            final JSONObject response = performHttpGetRequest(token, url);

            if (response != null) {
                JSONArray data = response.getJSONArray("data");
                List<BedState.Data> sensorDataList = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    BedState.Data sensorData = BedState.Data.fromJSON(obj);
                    sensorDataList.add(sensorData);
                }
                return new AsyncTaskResult<>(sensorDataList);
            }

            return new AsyncTaskResult<>(new LinkedList<>());
        }
    }

    public static class InternalTempLastValues extends AsyncTaskRequest<List<InternalTempLastValues.Data>> {
        public static class Data extends SimpleValueSensor {
            public Data(String sensorId, float value, Date date) {
                super(value, date);
            }

            public Data(float value, String strDate) throws ParseException {
                super(value, strDate);
            }

            @SuppressLint("SimpleDateFormat")
            public static Data fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
                final String dateString = jsonObject.getString("updated_at");
                final Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateString);
                final String sensorId = jsonObject.has("internal_sensor_id") ?
                        jsonObject.getString("internal_sensor_id") : null;

                return new Data(sensorId, Float.valueOf(jsonObject.getString("value")), date);
            }
        }

        @SuppressLint("DefaultLocale")
        public InternalTempLastValues(int elderId, int numOfValues, String token,
                                      OnResultListener<List<Data>> resultListener,
                                      OnFailureListener failureListener) {
            super(token, String.format(Constants.INTERNAL_TEMP_LAST_VALUES_URL, elderId, numOfValues),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<List<Data>> request() throws JSONException, ParseException {
            final JSONObject response = performHttpGetRequest(token, url);

            if (response != null) {
                JSONArray data = response.getJSONArray("data");
                List<Data> sensorDataList = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    Data sensorData = Data.fromJSON(obj);
                    sensorDataList.add(sensorData);
                }
                return new AsyncTaskResult<>(sensorDataList);
            }

            return new AsyncTaskResult<>(new LinkedList<>());
        }
    }

    public static class TemperatureValue extends AsyncTaskRequest<TemperatureValue.Data> {
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
        public TemperatureValue(int elderId, String token,
                                OnResultListener<Data> resultListener,
                                OnFailureListener failureListener) {
            super(token, String.format(Constants.TEMPERATURE_VALUE_URL, elderId),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<Data> request() throws JSONException, ParseException {
            final JSONObject response = performHttpGetRequest(token, url);
            if (response != null) {
                Data data = Data.fromJSON(response);
                return new AsyncTaskResult<>(data);
            }
            return new AsyncTaskResult<>(new NullPointerException("No data returned from request"));
        }
    }

    public static class CurrentLastValues extends AsyncTaskRequest<List<CurrentLastValues.Data>> {
        public static class Data extends SimpleValueSensor {
            public Data(String sensorId, float value, Date date) {
                super(value, date);
            }

            public Data(float value, String strDate) throws ParseException {
                super(value, strDate);
            }

            @SuppressLint("SimpleDateFormat")
            public static Data fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
                final String dateString = jsonObject.getString("date");
                final Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateString);
                final Integer sensorId = jsonObject.has("corrent_sensor_id") ?
                        jsonObject.getInt("corrent_sensor_id") : null;

                return new Data(String.valueOf(sensorId),
                        Float.valueOf(jsonObject.getString("value")), date);
            }
        }

        @SuppressLint("DefaultLocale")
        public CurrentLastValues(int elderId, int numOfValues, String token,
                                 OnResultListener<List<Data>> resultListener,
                                 OnFailureListener failureListener) {
            super(token, String.format(Constants.ELECTRICAL_CURRENT_LAST_VALUES_URL, elderId, numOfValues),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<List<Data>> request() throws JSONException, ParseException {
            final JSONObject response = performHttpGetRequest(token, url);

            if (response != null) {
                JSONArray data = response.getJSONArray("data");
                List<Data> sensorDataList = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    Data sensorData = Data.fromJSON(obj);
                    sensorDataList.add(sensorData);
                }

                return new AsyncTaskResult<>(sensorDataList);
            }
           return new AsyncTaskResult<>(new LinkedList<>());
        }
    }

    public static class DivisionValues extends AsyncTaskRequest<List<DivisionValues.Data>> {
        public static class Data {
            private boolean isInside;
            private String division;
            private Date date;

            public Data(boolean isInside, String division, Date date) {
                this.isInside = isInside;
                this.division = division;
                this.date = date;
            }

            public boolean isInside() {
                return isInside;
            }

            public String getDivision() {
                return division;
            }

            public Date getDate() {
                return date;
            }

            @SuppressLint("SimpleDateFormat")
            public static Data fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
                final String dateString = jsonObject.getString("updated_at");
                final Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateString);
                final boolean isInside = jsonObject.getString("value").equals("left");
                return new Data(isInside, jsonObject.getString("division"), date);
            }
        }

        @SuppressLint("DefaultLocale")
        public DivisionValues(int elderId, String division, String token,
                             OnResultListener<List<DivisionValues.Data>> resultListener,
                             OnFailureListener failureListener) {
            super(token, String.format(Constants.DIVISION_VALUES_URL, elderId, division),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<List<DivisionValues.Data>> request() throws JSONException, ParseException {
            final JSONObject response = performHttpGetRequest(token, url);

            if (response != null) {
                JSONArray data = response.getJSONArray("data");
                List<DivisionValues.Data> sensorDataList = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    DivisionValues.Data sensorData = DivisionValues.Data.fromJSON(obj);
                    sensorDataList.add(sensorData);
                }
                return new AsyncTaskResult<>(sensorDataList);
            }

            return new AsyncTaskResult<>(new LinkedList<>());
        }
    }
}
