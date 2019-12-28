package com.example.tsngapp.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.example.tsngapp.BuildConfig;
import com.example.tsngapp.R;
import com.example.tsngapp.helpers.AuthManager;
import com.example.tsngapp.api.SMARTAAL;
import com.example.tsngapp.api.model.SimpleValueSensor;
import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.helpers.DateUtil;
import com.example.tsngapp.model.DashboardData;
import com.example.tsngapp.model.User;
import com.example.tsngapp.ui.chart.DateAxisFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BaseEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DashboardFragment extends BaseFragment {
    private final Integer CURRENT_CHART_MAX_VALUES = 4;
    private final Integer TEMPERATURE_CHART_MAX_VALUES = 4;
    private final Integer TOTAL_REQUEST_COUNT = 5;

    private LineChart chartElectricalCurrent, chartTemperature;
    private View currentChartView, temperatureChartView,
            bedStateView, doorStateView, weatherStateView, temperatureStateView;
    private TextView tvStatusAwake, tvStatusInside, tvStatusWeather, tvStatusTemperature;
    private ImageView bedStateIcon, doorStateIcon, weatherStateIcon, temperatureStateIcon;
    private SwipeRefreshLayout refreshLayout;

    private List<Entry> currentChartEntries, temperatureChartEntries;
    private Toast currentTouchToast;
    private Integer requestCount;

    private Pusher pusher;

    // Auxiliary properties
    private DashboardData data;
//    private Boolean bedState, doorState;
//    private String weatherState;
//    private Integer temperatureState;
//    private List<SMARTAAL.CurrentLastValues.Data> currentValues;
//    private List<SMARTAAL.InternalTempLastValues.Data> internalTempValues;

    public DashboardFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.currentChartEntries = new LinkedList<>();
        this.temperatureChartEntries = new LinkedList<>();
    }

    @Override
    protected void onCreateViewActions(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        this.data = new DashboardData();

        bindViews();
        setupStaticResources();
        initializeCharts();
        restoreSavedData();
        loadStatusCardsAndCharts();
        bindSockets();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_dashboard;
    }

    @Override
    public void onResume() {
        super.onResume();
        pusher.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        pusher.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pusher.unsubscribe(Constants.Pusher.CHANNEL_CURRENT);
        pusher.unsubscribe(Constants.Pusher.CHANNEL_DOOR_VALUE);
        pusher.unsubscribe(Constants.Pusher.CHANNEL_BED_VALUE);
        pusher.unsubscribe(Constants.Pusher.CHANNEL_INTERNAL_TEMP);
    }

    private void initializeCharts() {
        initializeCurrentChart();
        initializeTemperatureChart();
    }

    private void bindSockets() {
        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        pusher = new Pusher(BuildConfig.PUSHER_KEY, options);

        pusher
            .subscribe(Constants.Pusher.CHANNEL_CURRENT)
            .bind(Constants.Pusher.EVENT_NEW_CURRENT_VALUE, event -> {
                try {
                    final String data = event.getData();
                    final JSONArray arr = new JSONObject(data).getJSONArray("values");

                    if (arr.get(0).equals(AuthManager.getInstance().getUser().getElder_id())) {
                        final SMARTAAL.CurrentLastValues.Data sensorData =
                                new SMARTAAL.CurrentLastValues.Data(
                                    arr.getInt(1),
                                    arr.getString(2)
                                );
                        addLineChartEntry(chartElectricalCurrent, currentChartEntries,
                                CURRENT_CHART_MAX_VALUES, sensorData);
                    }
                } catch (JSONException | ParseException e) {
                    Log.d(Constants.DEBUG_TAG, String.format(
                            "Failed to parse %s EVENT from Pusher %s CHANNEL",
                            Constants.Pusher.CHANNEL_CURRENT,
                            Constants.Pusher.EVENT_NEW_CURRENT_VALUE));
                }
            });

        final SubscriptionEventListener subscriptionEventListener = event -> {
            Log.d(Constants.DEBUG_TAG, String.format(
                    "New %s EVENT from Pusher %s CHANNEL (No action)",
                    event.getChannelName(), event.getEventName()));
        };
        pusher
            .subscribe(Constants.Pusher.CHANNEL_INTERNAL_TEMP)
            .bind(Constants.Pusher.EVENT_NEW_INTERNAL_TEMP_VALUE, subscriptionEventListener);
        pusher
            .subscribe(Constants.Pusher.CHANNEL_BED_VALUE)
            .bind(Constants.Pusher.EVENT_NEW_BED_VALUE, subscriptionEventListener);
        pusher
            .subscribe(Constants.Pusher.CHANNEL_DOOR_VALUE)
            .bind(Constants.Pusher.EVENT_NEW_DOOR_VALUE, subscriptionEventListener);

        pusher.connect();
    }

    private void updateBedState(boolean isAwake) {
        final ImageView iconView = bedStateView.findViewById(R.id.iv_card_icon_home_status);
        final MaterialCardView iconContainer = bedStateView.findViewById(R.id.cv_icon_home_status);
        updateBooleanStatusCard(tvStatusAwake, iconContainer, iconView, isAwake,
                R.color.md_green_500, R.color.md_red_500, R.string.label_yes, R.string.label_no,
                R.drawable.ic_mdi_bed_empty_black_24dp, R.drawable.ic_hotel_black_24dp);
        this.data.setAwake(isAwake);
    }

    private void updateDoorState(boolean isInside) {
        final ImageView iconView = doorStateView.findViewById(R.id.iv_card_icon_home_status);
        final MaterialCardView iconContainer = doorStateView.findViewById(R.id.cv_icon_home_status);
        updateBooleanStatusCard(tvStatusInside, iconContainer, iconView, isInside,
                R.color.md_green_500, R.color.md_red_500, R.string.label_inside, R.string.label_outside,
                R.drawable.ic_mdi_door_closed_black_24dp, R.drawable.ic_mdi_door_open_black_24dp);
        this.data.setInside(isInside);
    }

    private void updateWeatherState(String weatherCondition) {
        @DrawableRes int weatherIcon;
        @ColorRes int weatherColor;

        switch(weatherCondition) {
            case "Clouds":
                weatherIcon = R.drawable.ic_mdi_weather_cloudy_black_24dp;
                weatherColor = R.color.md_blue_500;
                break;
            case "Rain":
                weatherIcon = R.drawable.ic_mdi_weather_rainy_black_24dp;
                weatherColor = R.color.md_yellow_A700;
                break;
            case "Drizzle":
                weatherIcon = R.drawable.ic_mdi_weather_grain_black_24dp;
                weatherColor = R.color.md_yellow_A700;
                break;
            case "Clear":
                weatherIcon = R.drawable.ic_mdi_weather_sunny_black_24dp;
                weatherColor = R.color.md_green_500;
                break;
            case "Squall":
                weatherIcon = R.drawable.ic_mdi_weather_windy_black_24dp;
                weatherColor = R.color.md_red_500;
                break;
            case "Thunderstorm":
                weatherIcon = R.drawable.ic_mdi_weather_lightning_rainy_black_24dp;
                weatherColor = R.color.md_red_500;
                break;
            default:
                weatherIcon = R.drawable.ic_mdi_weather_fog_black_24dp;
                weatherColor = R.color.md_yellow_A700;
        }

        final MaterialCardView weatherIconView =
                weatherStateView.findViewById(R.id.cv_icon_home_status);
        tvStatusWeather.setText(weatherCondition);
        weatherStateIcon.setImageResource(weatherIcon);
        weatherIconView.setCardBackgroundColor(
                ContextCompat.getColor(rootView.getContext(), weatherColor));

        this.data.setWeatherCondition(weatherCondition);
    }

    private void updateTemperatureState(int temperature) {
        @DrawableRes int temperatureIcon = R.drawable.ic_mdi_thermometer_black_24dp;
        @ColorRes int temperatureColor;

        if (temperature <= 10) {
            temperatureIcon = R.drawable.ic_mdi_thermometer_minus_black_24dp;
            temperatureColor = R.color.md_red_500;
        } else if (temperature <= 20) {
            temperatureColor = R.color.md_yellow_A700;
        } else if (temperature <= 25) {
            temperatureColor = R.color.md_green_500;
        } else if (temperature <= 30) {
            temperatureColor = R.color.md_yellow_A700;
        } else {
            temperatureColor = R.color.md_red_500;
        }

        final MaterialCardView temperatureIconView =
                temperatureStateView.findViewById(R.id.cv_icon_home_status);
        tvStatusTemperature.setText(String.valueOf(temperature));
        temperatureStateIcon.setImageResource(temperatureIcon);
        temperatureIconView.setCardBackgroundColor(
                ContextCompat.getColor(rootView.getContext(), temperatureColor));

        this.data.setTemperature(temperature);
    }

    private void loadStatusCardsAndCharts() {
        this.requestCount = 0;
        setRefreshLayoutState(true);
        loadStatusCards();
        loadLineChartLastValues();
    }

    private void finishDataLoadingProcess() {
        setRefreshLayoutState(false);
        saveCurrentData();
    }

    //region Data persisting
    private String getStoragePath() {
        return rootView.getContext().getFilesDir().getPath() + "/" +
                Constants.STORAGE_DASHBOARD_FILENAME;
    }

    private void restoreSavedData() {
        try (Reader reader = new FileReader(getStoragePath())) {
            this.data = new Gson().fromJson(reader, DashboardData.class);
            if (this.data != null) {
                loadRestoredData(this.data);
            }
        } catch (IOException | IllegalStateException e) {
            Log.d(Constants.DEBUG_TAG, "Failed to load dashboard data: " + e.getLocalizedMessage());
        }
    }

    private void saveCurrentData() {
        try (Writer writer = new FileWriter(getStoragePath())) {
            new Gson().toJson(this.data, writer);
        } catch (IOException | IllegalStateException e) {
            Log.d(Constants.DEBUG_TAG, "Failed to write dashboard data: " + e.getLocalizedMessage());
        }
    }

    private void loadRestoredData(DashboardData data) {
        final Boolean isAwake = data.getAwake();
        final Boolean isInside = data.getInside();
        final String weatherCondition = data.getWeatherCondition();
        final Integer temperature = data.getTemperature();
        final List<SMARTAAL.CurrentLastValues.Data> currentValues = data.getCurrentValues();
        final List<SMARTAAL.InternalTempLastValues.Data> internalTempValues = data.getInternalTempValues();

        if (isAwake != null) updateBedState(isAwake);
        if (isInside != null) updateDoorState(isInside);
        if (weatherCondition != null) updateWeatherState(weatherCondition);
        if (temperature != null) updateTemperatureState(temperature);
        if (currentValues != null) {
            loadChartData(chartElectricalCurrent, currentValues,
                    currentChartEntries, CURRENT_CHART_MAX_VALUES);
        }
        if (internalTempValues != null) {
            loadChartData(chartTemperature, internalTempValues,
                    temperatureChartEntries, TEMPERATURE_CHART_MAX_VALUES);
        }
    }
    //endregion

    private void handleStatusCardDataLoadingSuccess() {
        // TODO: Concurrency problems?
        if (++requestCount >= TOTAL_REQUEST_COUNT) {
            finishDataLoadingProcess();
        }
    }

    private void handleStatusCardDataLoadingFailure(Exception e, String actionName) {
        Log.d(Constants.DEBUG_TAG, String.format(
                "Couldn't initialize dashboard status card (%s), there may be no data available: %s",
                actionName, e.getMessage()));
        if (requestCount >= TOTAL_REQUEST_COUNT) {
            setRefreshLayoutState(false);
        }
    }

    private void loadStatusCards() {
        final User user = AuthManager.getInstance().getUser();
        final SMARTAAL.BedState getBedState = new SMARTAAL.BedState(
                user.getElder_id(),
                user.getAcessToken(),
                state -> {
                    updateBedState(state.isAwake());
                    handleStatusCardDataLoadingSuccess();
                },
                e -> handleStatusCardDataLoadingFailure(e, "getBedState"));
        final SMARTAAL.DoorState getDoorState = new SMARTAAL.DoorState(
                user.getElder_id(),
                user.getAcessToken(),
                state -> {
                    updateDoorState(state.isInside());
                    handleStatusCardDataLoadingSuccess();
                },
                e -> handleStatusCardDataLoadingFailure(e, "getDoorState"));
        final SMARTAAL.TemperatureValue getWeatherAndTemperature = new SMARTAAL.TemperatureValue(
                user.getElder_id(),
                user.getAcessToken(),
                state -> {
                    updateWeatherState(state.getWeather());
                    updateTemperatureState(state.getTemperature());
                    handleStatusCardDataLoadingSuccess();
                },
                e -> handleStatusCardDataLoadingFailure(e, "getWeatherAndTemperature"));

        getBedState.execute();
        getDoorState.execute();
        getWeatherAndTemperature.execute();
    }

    private <V extends SimpleValueSensor> void loadChartData(
            LineChart chart, List<V> values, List<Entry> currentChartEntries, Integer maxValues) {
        addLineChartEntries(chart, currentChartEntries, maxValues, values);

        if (requestCount != null) {
            // TODO: Concurrency problems?
            if (++requestCount >= TOTAL_REQUEST_COUNT) {
                finishDataLoadingProcess();
            }
        }
    }

    private void handleChartDataLoadingFailed(Exception e) {
        Toast.makeText(rootView.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        if (requestCount >= TOTAL_REQUEST_COUNT) {
            setRefreshLayoutState(false);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void loadLineChartLastValues() {
        final User user = AuthManager.getInstance().getUser();

        final SMARTAAL.CurrentLastValues getCurrentLastValues = new SMARTAAL.CurrentLastValues(
                user.getElder_id(), CURRENT_CHART_MAX_VALUES, user.getAcessToken(),
                values -> {
                    loadChartData(chartElectricalCurrent, values,
                            currentChartEntries, CURRENT_CHART_MAX_VALUES);
                    this.data.setCurrentValues(values);
                },
                this::handleChartDataLoadingFailed
        );
        final SMARTAAL.InternalTempLastValues getTemperatureLastValues = new SMARTAAL.InternalTempLastValues(
                user.getElder_id(), TEMPERATURE_CHART_MAX_VALUES, user.getAcessToken(),
                values -> {
                    loadChartData(chartTemperature, values,
                            temperatureChartEntries, TEMPERATURE_CHART_MAX_VALUES);
                    this.data.setInternalTempValues(values);
                },
                this::handleChartDataLoadingFailed
        );

        getCurrentLastValues.execute();
        getTemperatureLastValues.execute();
    }

    private <C extends CardView, I extends ImageView> void updateBooleanStatusCard(
            TextView textView, C iconContainer, I iconView, boolean condition,
            @ColorRes int colorPositive, @ColorRes int colorNegative, @StringRes int stringPositive,
            @StringRes int stringNegative, @DrawableRes int iconPositive, @DrawableRes int iconNegative) {
        if (condition) {
            iconContainer.setCardBackgroundColor(
                    ContextCompat.getColor(rootView.getContext(), colorPositive));
            iconView.setImageResource(iconPositive);
            textView.setText(stringPositive);
        } else {
            iconContainer.setCardBackgroundColor(
                    ContextCompat.getColor(rootView.getContext(), colorNegative));
            iconView.setImageResource(iconNegative);
            textView.setText(stringNegative);
        }
    }

    private <C extends LineChart, V extends SimpleValueSensor> void addLineChartEntry(
            C chart, List<Entry> entryList, Integer maxValues, V value) {
        final Entry entry = getChartEntry(value);
        if (entryList.size() == maxValues) {
            /* removes the first entry to maintain a fixed amount of values */
            entryList.remove(0);
        }
        entryList.add(entry);
        reloadLineChartData(chart, entryList);
    }

    private <C extends LineChart, V extends SimpleValueSensor> void addLineChartEntries(
            C chart, List<Entry> entryList, Integer maxValues, List<V> values) {
        if (values.size() == 0) return;

        for (V value : values) {
            final Entry entry = getChartEntry(value);
            if (entryList.size() == maxValues) {
                /* removes the first entry to maintain a fixed amount of values */
                entryList.remove(0);
            }
            entryList.add(entry);
        }
        reloadLineChartData(chart, entryList);
    }

    @SuppressLint("SimpleDateFormat")
    private Entry getChartEntry(SimpleValueSensor value) {
        String strDate = DateUtil.getStringFromDate(value.getDate(), "HHmmss");
        float date = Float.valueOf("0." + strDate);
        float val = value.getValue();
        return new Entry(date, val);
    }

    private int currentCircleColor;

    private <C extends LineChart> void reloadLineChartData(C chart, List<Entry> entryList) {
        float max = Stream.of(entryList)
                .map(BaseEntry::getY)
                .max(Float::compare)
                .get();

        LineDataSet dataSet = new LineDataSet(entryList, null);
        LineData lineData = new LineData(dataSet);
        dataSet.setLineWidth(2);
        dataSet.setColor(currentCircleColor);
        dataSet.setValueTextColor(currentCircleColor);
        dataSet.setCircleRadius(5);
        dataSet.setCircleColor(currentCircleColor);
        dataSet.setDrawValues(false);
        chart.setData(lineData);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(max + (max / 2));

        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private void initializeTemperatureChart() {
        initializeLineChart(chartTemperature, R.string.chart_temperature_no_data);
    }

    private void initializeCurrentChart() {
        initializeLineChart(chartElectricalCurrent, R.string.chart_current_no_data);
        chartElectricalCurrent.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            @SuppressLint("SimpleDateFormat")
            public void onChartSingleTapped(MotionEvent me) {
                Entry entry = chartElectricalCurrent.getEntryByTouchPoint(me.getX(), me.getY());
                if (currentTouchToast != null) currentTouchToast.cancel();

                try {
                    final String entryPointDate = String.valueOf(entry.getX());
                    final Date date = DateUtil.getDateFromString(entryPointDate.substring(2), "HHmmss");
                    final String strDate = DateUtil.getStringFromDate(date, Constants.TIME_FORMAT);

                    currentTouchToast = Toast.makeText(rootView.getContext(),
                            "Electrical current value: " + entry.getY() + "W\nTime: " + strDate,
                            Toast.LENGTH_SHORT);
                    currentTouchToast.show();
                } catch (ParseException e) {
                    currentTouchToast = Toast.makeText(rootView.getContext(),
                            "Failed to get entry point", Toast.LENGTH_SHORT);
                    currentTouchToast.show();
                }
            }

            @Override public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}
            @Override public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}
            @Override public void onChartLongPressed(MotionEvent me) {}
            @Override public void onChartDoubleTapped(MotionEvent me) {}
            @Override public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {}
            @Override public void onChartScale(MotionEvent me, float scaleX, float scaleY) {}
            @Override public void onChartTranslate(MotionEvent me, float dX, float dY) {}
        });
    }


    private <C extends LineChart> void initializeLineChart(C chart, @StringRes int noDataText) {
        initializeLineChart(chart, getString(noDataText));
    }

    private <C extends LineChart> void initializeLineChart(C chart, String noDataText) {
        currentCircleColor = ContextCompat.getColor(rootView.getContext(), android.R.color.white);
        final int currentGridColor = ContextCompat.getColor(rootView.getContext(), R.color.md_grey_400);

        chart.setNoDataText(noDataText);
        chart.setNoDataTextColor(ContextCompat.getColor(rootView.getContext(), R.color.md_blue_grey_100));
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setLabelCount(10);
        yAxis.setGridColor(currentGridColor);
        yAxis.setTextColor(currentCircleColor);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DateAxisFormatter());
        xAxis.setTextColor(currentCircleColor);
        xAxis.setGridColor(currentGridColor);
    }

    private void setupStaticResources() {
        TextView tvCurrentChartTitle = currentChartView.findViewById(R.id.tv_dashboard_chart_title);
        TextView tvTemperatureChartTitle = temperatureChartView.findViewById(R.id.tv_dashboard_chart_title);
        TextView tvBedStateLabel = bedStateView.findViewById(R.id.tv_card_description_home_status);
        TextView tvDoorStateLabel = doorStateView.findViewById(R.id.tv_card_description_home_status);
        TextView tvWeatherStateLabel = weatherStateView.findViewById(R.id.tv_card_description_home_status);
        TextView tvTemperatureStateLabel = temperatureStateView.findViewById(R.id.tv_card_description_home_status);

        tvCurrentChartTitle.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_power_white_24dp, 0, 0, 0);
        tvTemperatureChartTitle.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_mdi_thermometer_white_24dp, 0, 0, 0);

        tvCurrentChartTitle.setText(R.string.home_last_current_values);
        tvTemperatureChartTitle.setText(R.string.home_last_temperature_values);
        tvBedStateLabel.setText(R.string.label_awake);
        tvDoorStateLabel.setText(R.string.label_house_state);
        tvWeatherStateLabel.setText(R.string.label_weather);
        tvTemperatureStateLabel.setText(R.string.label_temperature);

        bedStateIcon.setImageResource(R.drawable.ic_close_black_24dp);
        doorStateIcon.setImageResource(R.drawable.ic_close_black_24dp);
        weatherStateIcon.setImageResource(R.drawable.ic_close_black_24dp);
        temperatureStateIcon.setImageResource(R.drawable.ic_close_black_24dp);
    }

    private void bindViews() {
        currentChartView = rootView.findViewById(R.id.chart_home_current_values);
        temperatureChartView = rootView.findViewById(R.id.chart_home_temperature_values);
        bedStateView = rootView.findViewById(R.id.v_home_bed_state);
        doorStateView = rootView.findViewById(R.id.v_home_house_state);
        weatherStateView = rootView.findViewById(R.id.v_home_weather_state);
        temperatureStateView = rootView.findViewById(R.id.v_home_temperature_value);

        chartElectricalCurrent = currentChartView.findViewById(R.id.chart_dashboard);
        chartTemperature = temperatureChartView.findViewById(R.id.chart_dashboard);

        tvStatusAwake = bedStateView.findViewById(R.id.tv_card_values_home_status);
        tvStatusInside = doorStateView.findViewById(R.id.tv_card_values_home_status);
        tvStatusWeather = weatherStateView.findViewById(R.id.tv_card_values_home_status);
        tvStatusTemperature = temperatureStateView.findViewById(R.id.tv_card_values_home_status);

        bedStateIcon = bedStateView.findViewById(R.id.iv_card_icon_home_status);
        doorStateIcon = doorStateView.findViewById(R.id.iv_card_icon_home_status);
        weatherStateIcon = weatherStateView.findViewById(R.id.iv_card_icon_home_status);
        temperatureStateIcon = temperatureStateView.findViewById(R.id.iv_card_icon_home_status);

        refreshLayout = rootView.findViewById(R.id.srl);
        refreshLayout.setOnRefreshListener(this::loadStatusCardsAndCharts);
    }

    private void setRefreshLayoutState(boolean state) {
        refreshLayout.setRefreshing(state);
        if (!state) {
            requestCount = 0;
        }
    }
}
