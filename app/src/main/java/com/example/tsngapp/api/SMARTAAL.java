package com.example.tsngapp.api;

import android.annotation.SuppressLint;

import com.example.tsngapp.api.model.SimpleValueSensor;
import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.model.Elder;
import com.example.tsngapp.network.AsyncTaskRequest;
import com.example.tsngapp.network.AsyncTaskResult;
import com.example.tsngapp.network.HTTPGETRequest;
import com.example.tsngapp.network.HTTPPOSTRequest;
import com.example.tsngapp.network.OnFailureListener;
import com.example.tsngapp.network.OnResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SMARTAAL {
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
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String response = request.execute();
            JSONObject jsonResponse = new JSONObject(response);
            Elder data = Elder.fromJSON(jsonResponse);
            return new AsyncTaskResult<>(data);
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
                final String value = jsonObject.getString("temperature");
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
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String response = request.execute();
            JSONObject jsonResponse = new JSONObject(response);
            Data data = Data.fromJSON(jsonResponse);
            return new AsyncTaskResult<>(data);
        }
    }

    public static class DoorLastValues extends AsyncTaskRequest<JSONObject> {
        @SuppressLint("DefaultLocale")
        public DoorLastValues(int elderId, int numOfValues, String token,
                              OnResultListener<JSONObject> resultListener,
                              OnFailureListener failureListener) {
            super(token, String.format(Constants.DOOR_LAST_VALUES_URL, elderId, numOfValues),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<JSONObject> request() throws JSONException {
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String response = request.execute();
            JSONObject jsonResponse = new JSONObject(response);
            return new AsyncTaskResult<>(jsonResponse);
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
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String response = request.execute();
            JSONObject jsonResponse = new JSONObject(response);
            Data data = Data.fromJSON(jsonResponse);
            return new AsyncTaskResult<>(data);
        }
    }

    public static class BedLastValues extends AsyncTaskRequest<String> {
        @SuppressLint("DefaultLocale")
        public BedLastValues(int elderId, int numOfValues, String token,
                             OnResultListener<String> resultListener,
                             OnFailureListener failureListener) {
            super(token, String.format(Constants.BED_LAST_VALUES_URL, elderId, numOfValues),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<String> request() {
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String result = request.execute();
            return new AsyncTaskResult<>(result);
        }
    }

    public static class InternalTempLastValues extends AsyncTaskRequest<List<InternalTempLastValues.Data>> {
        public static class Data extends SimpleValueSensor {
            public Data(String sensorId, float value, Date date) {
                super(value, date);
            }

            public Data(String sensorId, float value, String strDate) throws ParseException {
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
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String response = request.execute();
            if (response.length() > 0) {
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
            HTTPGETRequest request = new HTTPGETRequest(token, url);
            String response = request.execute();
            JSONObject jsonResponse = new JSONObject(response);
            Data data = Data.fromJSON(jsonResponse);
            return new AsyncTaskResult<>(data);
        }
    }

    public static  class CurrentSensorValues extends AsyncTaskRequest<List<CurrentSensorValues.Data>> {
        private JSONObject dataToSend;
        private static String time;

        public static class Data extends SimpleValueSensor {
            public Data(float value, Date date) {
                super(value, date);
            }

            public Data(float value, String strDate) throws ParseException {
                super(value, strDate);
            }

            @SuppressLint("SimpleDateFormat")
            public static CurrentSensorValues.Data fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
                final String dateString = jsonObject.getString("date");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

                if(time.equals("day")){
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd hh");
                }

                if(time.equals("month")){
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                }
                final Date date = dateFormat.parse(dateString);

                return new CurrentSensorValues.Data(
                        Float.valueOf(jsonObject.getString("value")), date);
            }
        }


        public CurrentSensorValues(String token, OnResultListener<List<Data>> resultListener, OnFailureListener failureListener, JSONObject dataToSend, String time) {
            super(token, Constants.CURRENT_SENSOR_VALUES_CHART, resultListener, failureListener);
            this.dataToSend = dataToSend;
            this.time = time;

        }

        @Override
        protected AsyncTaskResult<List<Data>> request() throws IOException, JSONException, ParseException {
            HTTPPOSTRequest request = new HTTPPOSTRequest(token, url, dataToSend);

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

    public static class CurrentLastValues extends AsyncTaskRequest<List<CurrentLastValues.Data>> {
        public static class Data extends SimpleValueSensor {
            public Data(float value, Date date) {
                super(value, date);
            }

            public Data(float value, String strDate) throws ParseException {
                super(value, strDate);
            }

            @SuppressLint("SimpleDateFormat")
            public static Data fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
                final String dateString = jsonObject.getString("date");
                final Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateString);

                return new Data(
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
