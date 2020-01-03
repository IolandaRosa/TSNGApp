package com.example.tsngapp.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.annimon.stream.Stream;
import com.example.tsngapp.BuildConfig;
import com.example.tsngapp.R;
import com.example.tsngapp.api.SMARTAAL;
import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.model.Elder;
import com.example.tsngapp.model.User;
import com.example.tsngapp.ui.chart.DateTimeAxisFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BaseEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ElectricalCurrentStateFragment extends BaseNestedFragment {

    private static final String PARAM_USER = "ElectricalCurrentStateFragment.PARAM_USER";
    private static final String PARAM_ELDER = "ElectricalCurrentStateFragment.PARAM_ELDER";
    private final String HOUR = "hour";
    private final String DAY = "day";
    private final String MONTH = "month";

    private User user;
    private Elder elder;
    private String time;
    private Button btnHour;
    private Button btnDay;
    private Button btnMonth;
    private View currentChartView;
    private LineChart chartElectricalCurrent;
    private List<Entry> currentEntries;
    private int chartColor;
    private HashMap<Float, Long> timesDiff;

    public ElectricalCurrentStateFragment() {
        timesDiff = new HashMap<>();
    }

    public static ElectricalCurrentStateFragment newInstance(User user, Elder elder) {
        ElectricalCurrentStateFragment fragment = new ElectricalCurrentStateFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_USER, user);
        args.putParcelable(PARAM_ELDER, elder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.user = getArguments().getParcelable(PARAM_USER);
            this.elder = getArguments().getParcelable(PARAM_ELDER);
        }

        this.currentEntries = new LinkedList<>();

        this.time = HOUR;

        initializeDataset();
       //bindSockets();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_electrical_current_state, container, false);
        this.btnHour = rootView.findViewById(R.id.buttonHour);
        this.btnDay = rootView.findViewById(R.id.buttonDay);
        this.btnMonth = rootView.findViewById(R.id.buttonMonth);

        this.currentChartView = rootView.findViewById(R.id.chart_electricalCurrentView);

        this.chartElectricalCurrent = currentChartView.findViewById(R.id.chart_electricalCurrent);

        this.chartColor = ContextCompat.getColor(rootView.getContext(), R.color.md_blue_A700);

        btnHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = HOUR;
                initializeDataset();
            }
        });

        btnDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = DAY;
                initializeDataset();
            }
        });

        btnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = MONTH;
                initializeDataset();
            }
        });

        return rootView;
    }

    @Override
    protected void onCreateViewPostActions(@NonNull LayoutInflater inflater,
                                           @Nullable ViewGroup container,
                                           @Nullable Bundle savedInstanceState) {
        rootView.findViewById(R.id.btn_go_back)
                .setOnClickListener(v -> {
                    parentListener.onBackToMenuPressed();
                });
    }

    private void initializeDataset() {
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
        }

        SMARTAAL.CurrentSensorValues getCurrentSensorValues = new SMARTAAL.CurrentSensorValues(
                user.getAcessToken(),
                values -> addLineChartEntries(values),
                e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show(),
                dataToSend,
                time);

        getCurrentSensorValues.execute();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_electrical_current_state;
    }

    private void addLineChartEntries(List<SMARTAAL.CurrentSensorValues.Data> values) {
        int index = 0;

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

        TextView title = currentChartView.findViewById(R.id.current_time_chart_title);
        title.setText(R.string.home_last_current_values);

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
        chartElectricalCurrent.setNoDataText("Loading results...");
        chartElectricalCurrent.setNoDataTextColor(Color.BLACK);
        chartElectricalCurrent.setDragEnabled(true);
        chartElectricalCurrent.setScaleEnabled(false);
        chartElectricalCurrent.getLegend().setEnabled(false);
        chartElectricalCurrent.getAxisRight().setEnabled(false);
        chartElectricalCurrent.getDescription().setEnabled(false);

        float max = Stream.of(currentEntries)
                .map(BaseEntry::getY)
                .max(Float::compare)
                .get();

        YAxis yAxis = chartElectricalCurrent.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setLabelCount(10);
        yAxis.setDrawGridLines(true);
        yAxis.setTextColor(Color.BLACK);

        XAxis xAxis = chartElectricalCurrent.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        yAxis.setAxisMaximum(max + (max / 2));

        xAxis.setValueFormatter(new DateTimeAxisFormatter(time, timesDiff));
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.BLACK);

        chartElectricalCurrent.notifyDataSetChanged();
        chartElectricalCurrent.invalidate();
    }

    private Long setTimeValue (Date date) {
        return date.getTime();
    }

    /*private void bindSockets() {
        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        Pusher pusher = new Pusher(BuildConfig.PUSHER_KEY, options);

        pusher
                .subscribe(Constants.Pusher.CHANNEL_CURRENT)
                .bind(Constants.Pusher.EVENT_NEW_CURRENT_VALUE, event -> {
                    try {
                       this.initializeDataset();
                    } catch (Exception e) {
                        Log.d(Constants.DEBUG_TAG, String.format(
                                "Failed to parse %s EVENT from Pusher %s CHANNEL",
                                Constants.Pusher.CHANNEL_CURRENT,
                                Constants.Pusher.EVENT_NEW_CURRENT_VALUE));
                    }
                });
        pusher.connect();
    }*/
}

