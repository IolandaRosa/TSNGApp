package com.example.tsngapp.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.annimon.stream.Stream;
import com.example.tsngapp.BuildConfig;
import com.example.tsngapp.R;
import com.example.tsngapp.api.SMARTAAL;
import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.helpers.DateUtil;
import com.example.tsngapp.helpers.StateManager;
import com.example.tsngapp.model.User;
import com.example.tsngapp.ui.chart.DateTimeAxisFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BaseEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ElectricalCurrentStateFragment extends BaseStateMenuItemFragment {
    private final String HOUR = "hour";
    private final String DAY = "day";
    private final String MONTH = "month";

    private User user;
    // private Elder elder;
    private View currentChartView;
    private LineChart chartElectricalCurrent;
    private SwipeRefreshLayout refreshLayout;

    private String time;
    private List<Entry> currentEntries;
    private int chartColor;
    private HashMap<Float, Long> timesDiff;

    private Pusher pusher;
    private Toast currentTouchToast;

    public ElectricalCurrentStateFragment() {
        timesDiff = new HashMap<>();
    }

    @Override
    protected void onCreateViewPostActions(@NonNull LayoutInflater inflater,
                                           @Nullable ViewGroup container,
                                           @Nullable Bundle savedInstanceState) {

        this.time = HOUR;
        this.user = StateManager.getInstance().getUser();
        this.currentEntries = new LinkedList<>();

        Button btnHour = rootView.findViewById(R.id.buttonHour);
        Button btnDay = rootView.findViewById(R.id.buttonDay);
        Button btnMonth = rootView.findViewById(R.id.buttonMonth);
        this.currentChartView = rootView.findViewById(R.id.chart_electricalCurrentView);
        this.chartElectricalCurrent = currentChartView.findViewById(R.id.chart_electricalCurrent);

        refreshLayout = rootView.findViewById(R.id.srl);
        refreshLayout.setOnRefreshListener(this::initializeDataset);

        this.chartColor = ContextCompat.getColor(rootView.getContext(), R.color.md_blue_A700);

        btnHour.setOnClickListener(view -> initializeDataset(HOUR));
        btnDay.setOnClickListener(view -> initializeDataset(DAY));
        btnMonth.setOnClickListener(view -> initializeDataset(MONTH));

        rootView.findViewById(R.id.btn_go_back)
                .setOnClickListener(v -> parentListener.onBackToMenuPressed());

        initializeChart();
        initializeDataset();
        bindSockets();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_electrical_current_state;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pusher != null) {
            pusher.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (pusher != null) {
            pusher.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pusher != null) {
            pusher.unsubscribe(Constants.Pusher.CHANNEL_CURRENT_HOUR_VALUE);
            pusher.unsubscribe(Constants.Pusher.CHANNEL_CURRENT_DAY_VALUE);
            pusher.unsubscribe(Constants.Pusher.CHANNEL_CURRENT_MONT_VALUE);
        }
    }

    private void initializeDataset() {
        this.initializeDataset(null);
    }

    private void initializeDataset(String newTime) {
        if (newTime != null) {
            if (this.time.equals(newTime)) {
                return;
            }
            this.time = newTime;
        }

        refreshLayout.setRefreshing(true);

        JSONObject userObject = new JSONObject();
        JSONObject dataToSend = new JSONObject();
        currentEntries = new LinkedList<>();
        try {
            userObject.put("id", user.getId());
            userObject.put("name", user.getName());
            userObject.put("username", user.getUsername());
            userObject.put("email", user.getEmail());
            userObject.put("type", "normal");
            userObject.put("elder_id", user.getElder_id());
            dataToSend.put("time", this.time);
            dataToSend.put("user_id", userObject);

            Log.d("dataToSend", dataToSend.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            refreshLayout.setRefreshing(false);
        }

        SMARTAAL.CurrentSensorValues getCurrentSensorValues = new SMARTAAL.CurrentSensorValues(
                user.getAcessToken(),
                values -> {
                    addLineChartEntries(values);
                    refreshLayout.setRefreshing(false);
                },
                e -> {
                    Toast.makeText(getContext(), "Failed to load current sensor data: " +
                            e.getMessage(), Toast.LENGTH_LONG).show();
                    refreshLayout.setRefreshing(false);
                },
                dataToSend, this.time);

        getCurrentSensorValues.execute();
    }

    private void initializeChart() {
        final TextView title = currentChartView.findViewById(R.id.current_time_chart_title);
        title.setText(R.string.home_last_current_values);

        chartElectricalCurrent.setNoDataText("Loading results...");
        chartElectricalCurrent.setNoDataTextColor(Color.BLACK);
        chartElectricalCurrent.setDragEnabled(true);
        chartElectricalCurrent.setScaleEnabled(false);
        chartElectricalCurrent.getLegend().setEnabled(false);
        chartElectricalCurrent.getAxisRight().setEnabled(false);
        chartElectricalCurrent.getDescription().setEnabled(false);

        chartElectricalCurrent.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            @SuppressLint("SimpleDateFormat")
            public void onChartSingleTapped(MotionEvent me) {
                Entry entry = chartElectricalCurrent.getEntryByTouchPoint(me.getX(), me.getY());
                if (currentTouchToast != null) currentTouchToast.cancel();

//                currentTouchToast = Toast.makeText(rootView.getContext(),
//                        "Electrical current value: " + entry.getY() + "W", Toast.LENGTH_SHORT);
//                currentTouchToast.show();

//                final long fullTimestamp = ((long) entry.getX()) +
//                        StateManager.getInstance().getCurrentReferenceTimestamp();
//                final String strDate = DateUtil.getStringFromDate(
//                        new Date(fullTimestamp), Constants.FULL_TIME_FORMAT);
//
//                currentTouchToast = Toast.makeText(rootView.getContext(),
//                        "Electrical current value: " + entry.getY() + "W\nTime: " + strDate,
//                        Toast.LENGTH_SHORT);
//                currentTouchToast.show();
            }

            @Override public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}
            @Override public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}
            @Override public void onChartLongPressed(MotionEvent me) {}
            @Override public void onChartDoubleTapped(MotionEvent me) {}
            @Override public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {}
            @Override public void onChartScale(MotionEvent me, float scaleX, float scaleY) {}
            @Override public void onChartTranslate(MotionEvent me, float dX, float dY) {}
        });

        YAxis yAxis = chartElectricalCurrent.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setLabelCount(10);
        yAxis.setDrawGridLines(true);
        yAxis.setTextColor(Color.BLACK);

        XAxis xAxis = chartElectricalCurrent.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setValueFormatter(new DateTimeAxisFormatter(this.time, timesDiff));
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.BLACK);
    }

    private void addLineChartEntries(List<SMARTAAL.CurrentSensorValues.Data> values) {
        int index = 0;

        if (values.size() == 0) {
            return;
        }

        ArrayList<Long> times = new ArrayList<>();

        for (SMARTAAL.CurrentSensorValues.Data v : values) {
            Long time = setTimeValue(v.getDate());
            times.add(time);
        }

        for (SMARTAAL.CurrentSensorValues.Data v : values) {
            timesDiff.put(Float.parseFloat(String.valueOf(index)), times.get(index));
            index ++;

            currentEntries.add(new Entry(Float.parseFloat(String.valueOf(index)), v.getValue()));
        }

        updateChart();
    }

    private void updateChart() {
        LineDataSet dataSet = new LineDataSet(currentEntries, "Label");
        dataSet.setColor(this.chartColor);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setCircleRadius(1);
        dataSet.setCircleColor(this.chartColor);
        dataSet.setCircleHoleColor(this.chartColor);
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(2);
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(this.chartColor);

        LineData lineData = new LineData(dataSet);
        chartElectricalCurrent.setData(lineData);

        float max = Stream.of(currentEntries)
                .map(BaseEntry::getY)
                .max(Float::compare)
                .get();

        chartElectricalCurrent.getAxisLeft().setAxisMaximum(max + (max / 2));

        chartElectricalCurrent.notifyDataSetChanged();
        chartElectricalCurrent.invalidate();
    }

    private Long setTimeValue (Date date) {
        return date.getTime();
    }

    private void bindSockets() {
        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        pusher = new Pusher(BuildConfig.PUSHER_KEY, options);

        pusher
            .subscribe(Constants.Pusher.CHANNEL_CURRENT_HOUR_VALUE)
            .bind(Constants.Pusher.EVENT_NEW_CURRENT_HOUR_VALUES, event ->
                    handleSocketEvent(Constants.Pusher.CHANNEL_CURRENT_HOUR_VALUE,
                            Constants.Pusher.EVENT_NEW_CURRENT_HOUR_VALUES));
        pusher
            .subscribe(Constants.Pusher.CHANNEL_CURRENT_DAY_VALUE)
            .bind(Constants.Pusher.EVENT_NEW_CURRENT_DAY_VALUES, event ->
                    handleSocketEvent(Constants.Pusher.CHANNEL_CURRENT_DAY_VALUE,
                            Constants.Pusher.EVENT_NEW_CURRENT_DAY_VALUES));
        pusher
            .subscribe(Constants.Pusher.CHANNEL_CURRENT_MONT_VALUE)
            .bind(Constants.Pusher.EVENT_NEW_CURRENT_MONTH_VALUES, event ->
                    handleSocketEvent(Constants.Pusher.CHANNEL_CURRENT_MONT_VALUE,
                            Constants.Pusher.EVENT_NEW_CURRENT_MONTH_VALUES));
        pusher.connect();
    }

    private void handleSocketEvent(String event, String channel) {
        try {
            this.initializeDataset();
        } catch (Exception e) {
            Log.d(Constants.DEBUG_TAG, String.format(
                    "Failed to parse %s EVENT from Pusher %s CHANNEL", event, channel));
        }
    }
}

