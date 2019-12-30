package com.example.tsngapp.api;

import android.annotation.SuppressLint;

import com.example.tsngapp.api.model.SimpleValueSensor;
import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.helpers.DateUtil;
import com.example.tsngapp.helpers.StateManager;
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
        public AsyncTaskResult<Elder> request() {
            try {
                final JSONObject response = performHttpGetRequest(token, url);
                if (response != null) {
                    Elder data = Elder.fromJSON(response);
                    return new AsyncTaskResult<>(data);
                }
            } catch (JSONException | ParseException e) {
                return new AsyncTaskResult<>(e);
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
                final Date date = DateUtil.getDateFromString(dateString);
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
        public AsyncTaskResult<Data> request() {
            try {
                final JSONObject response = performHttpGetRequest(token, url);
                if (response != null) {
                    Data data = Data.fromJSON(response);
                    return new AsyncTaskResult<>(data);
                }
            } catch (JSONException | ParseException e) {
                return new AsyncTaskResult<>(e);
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
        public AsyncTaskResult<List<DoorState.Data>> request() {
            try {
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
            } catch (JSONException | ParseException e) {
                return new AsyncTaskResult<>(e);
            }

            return new AsyncTaskResult<>(new LinkedList<>());
        }
    }

    public static class LightLastValues extends AsyncTaskRequest<List<LightLastValues.Data>> {
        public static class Data {
            private boolean turnedOn;
            private Date date;
            private String division;

            public Data(boolean turnedOn, Date date, String division) {
                this.turnedOn = turnedOn;
                this.date = date;
                this.division = division;
            }

            public boolean isTurnedOn() {
                return turnedOn;
            }

            public Date getDate() {
                return date;
            }

            public String getDivision() {
                return division;
            }

            @SuppressLint("SimpleDateFormat")
            public static Data fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
                final String dateString = jsonObject.getString("date");
                final Date date = DateUtil.getDateFromString(dateString);
                final String value = jsonObject.getString("value");
                return new Data(value.equals("on"), date, jsonObject.getString("division"));
            }
        }

        @SuppressLint("DefaultLocale")
        public LightLastValues(int elderId, String division, String token,
                               OnResultListener<List<LightLastValues.Data>> resultListener,
                               OnFailureListener failureListener) {
            super(token, String.format(Constants.LIGHT_DIVISION_LAST_VALUES_URL, elderId, division),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<List<LightLastValues.Data>> request() {
            try {
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
            } catch (JSONException | ParseException e) {
                return new AsyncTaskResult<>(e);
            }

            return new AsyncTaskResult<>(new LinkedList<>());
        }
    }

    public static class WindowLastValues extends AsyncTaskRequest<List<WindowLastValues.Data>> {
        public static class Data {
            private boolean closed;
            private Date date;
            private String division;

            public Data(boolean closed, Date date, String division) {
                this.closed = closed;
                this.date = date;
                this.division = division;
            }

            public boolean isClosed() {
                return closed;
            }

            public Date getDate() {
                return date;
            }

            public String getDivision() {
                return division;
            }

            @SuppressLint("SimpleDateFormat")
            public static Data fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
                final String dateString = jsonObject.getString("date");
                final Date date = DateUtil.getDateFromString(dateString);
                final String value = jsonObject.getString("value");
                return new Data(value.equals("closed"), date, jsonObject.getString("division"));
            }
        }

        @SuppressLint("DefaultLocale")
        public WindowLastValues(int elderId, String division, String token,
                                OnResultListener<List<WindowLastValues.Data>> resultListener,
                                OnFailureListener failureListener) {
            super(token, String.format(Constants.WINDOW_DIVISION_VALUES_URL, elderId, division),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<List<WindowLastValues.Data>> request() {
            try {
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
            } catch (JSONException | ParseException e) {
                return new AsyncTaskResult<>(e);
            }

            return new AsyncTaskResult<>(new LinkedList<>());
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
                final Date date = DateUtil.getDateFromString(dateString);
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
        public AsyncTaskResult<Data> request() {
            try {
                final JSONObject response = performHttpGetRequest(token, url);
                if (response != null) {
                    Data data = Data.fromJSON(response);
                    return new AsyncTaskResult<>(data);
                }
            } catch (JSONException | ParseException e) {
                return new AsyncTaskResult<>(e);
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
        public AsyncTaskResult<List<BedState.Data>> request() {
            try {
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
            } catch (JSONException | ParseException e) {
                return new AsyncTaskResult<>(e);
            }

            return new AsyncTaskResult<>(new LinkedList<>());
        }
    }

    public static class InternalTempLastValues extends AsyncTaskRequest<List<InternalTempLastValues.Data>> {
        public static class Data extends SimpleValueSensor {
            public Data() {}

            public Data(int id, float value, Date date) {
                super(id, value, date);
            }

            public Data(int id, float value, String strDate) throws ParseException {
                super(id, value, strDate);
            }

            @SuppressLint("SimpleDateFormat")
            public static Data fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
                final int id = StateManager.getInstance().getRng().nextInt(Constants.RNG_BOUND);
                final String dateString = jsonObject.getString("updated_at");
                final Date date = DateUtil.getDateFromString(dateString);

                return new Data(id, Float.valueOf(jsonObject.getString("value")), date);
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
        public AsyncTaskResult<List<Data>> request() {
            try {
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
            } catch (JSONException | ParseException e) {
                return new AsyncTaskResult<>(e);
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
                final Date date = DateUtil.getDateFromString(dateString);
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
        public AsyncTaskResult<Data> request() {
            try {
                final JSONObject response = performHttpGetRequest(token, url);
                if (response != null) {
                    Data data = Data.fromJSON(response);
                    return new AsyncTaskResult<>(data);
                }
            } catch (JSONException | ParseException e) {
                return new AsyncTaskResult<>(e);
            }

            return new AsyncTaskResult<>(new NullPointerException("No data returned from request"));
        }
    }

    public static class CurrentLastValues extends AsyncTaskRequest<List<CurrentLastValues.Data>> {
        public static class Data extends SimpleValueSensor {
            public Data() {}

            public Data(int id, float value, Date date) {
                super(id, value, date);
            }

            public Data(int id, float value, String strDate) throws ParseException {
                super(id, value, strDate);
            }

            @SuppressLint("SimpleDateFormat")
            public static Data fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
                final int id = StateManager.getInstance().getRng().nextInt(Constants.RNG_BOUND);
                final String dateString = jsonObject.getString("date");
                final Date date = DateUtil.getDateFromString(dateString);

                return new Data(id, Float.valueOf(jsonObject.getString("value")), date);
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
        public AsyncTaskResult<List<Data>> request() {
            try {
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
            } catch (JSONException | ParseException e) {
                return new AsyncTaskResult<>(e);
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
                final Date date = DateUtil.getDateFromString(dateString);
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
        public AsyncTaskResult<List<DivisionValues.Data>> request() {
            try {
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
            } catch (JSONException | ParseException e) {
                return new AsyncTaskResult<>(e);
            }

            return new AsyncTaskResult<>(new LinkedList<>());
        }
    }

    public static class SOSValues extends AsyncTaskRequest<List<Date>> {
        @SuppressLint("DefaultLocale")
        public SOSValues(int elderId, String token,
                              OnResultListener<List<Date>> resultListener,
                              OnFailureListener failureListener) {
            super(token, String.format(Constants.SOS_VALUE_URL, elderId),
                    resultListener, failureListener);
        }

        @Override
        @SuppressLint("SimpleDateFormat")
        public AsyncTaskResult<List<Date>> request() {
            try {
                final JSONObject response = performHttpGetRequest(token, url);

                if (response != null) {
                    JSONArray data = response.getJSONArray("data");
                    List<Date> sensorDataList = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        final String dateString = obj.getString("date");
                        final Date date = DateUtil.getDateFromString(dateString);
                        sensorDataList.add(date);
                    }
                    return new AsyncTaskResult<>(sensorDataList);
                }
            } catch (JSONException | ParseException e) {
                return new AsyncTaskResult<>(e);
            }

            return new AsyncTaskResult<>(new LinkedList<>());
        }
    }

    public static class GasEmission extends AsyncTaskRequest<GasEmission.Data> {
        public static class Data {
            private boolean isNormal;
            private Date date;

            public Data(boolean isNormal, Date date) {
                this.isNormal = isNormal;
                this.date = date;
            }

            public boolean isNormal() {
                return isNormal;
            }

            public Date getDate() {
                return date;
            }

            @SuppressLint("SimpleDateFormat")
            public static Data fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
                final String dateString = jsonObject.getString("date");
                final Date date = DateUtil.getDateFromString(dateString);
                final float value = Float.valueOf(jsonObject.getString("value"));
                return new Data(value < 1000, date);
            }
        }

        @SuppressLint("DefaultLocale")
        public GasEmission(int elderId, String token,
                           OnResultListener<Data> resultListener,
                           OnFailureListener failureListener) {
            super(token, String.format(Constants.GAS_EMISSION_VALUE_URL, elderId),
                    resultListener, failureListener);
        }

        @Override
        public AsyncTaskResult<Data> request() {
            try {
                final JSONObject response = performHttpGetRequest(token, url);
                if (response != null) {
                    Data data = Data.fromJSON(response);
                    return new AsyncTaskResult<>(data);
                }
            } catch (JSONException | ParseException e) {
                return new AsyncTaskResult<>(e);
            }
            return new AsyncTaskResult<>(new NullPointerException("No data returned from request"));
        }
    }
}
