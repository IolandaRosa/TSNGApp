package com.example.tsngapp.ui.auth;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.example.tsngapp.R;
import com.example.tsngapp.api.SMARTAAL;
import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.model.User;
import com.example.tsngapp.network.AsyncTaskPostLogout;
import com.example.tsngapp.network.OnFailureListener;
import com.example.tsngapp.ui.LoginActivity;
import com.example.tsngapp.ui.chart.DateAxisFormatter;
import com.example.tsngapp.view_managers.LoginManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private LineChart chartElectricalCurrent;
    private View bedStateView;
    private View doorStateView;
    private View weatherStateView;
    private View temperatureStateView;
    private TextView tvStatusAwake;
    private TextView tvStatusInside;
    private TextView tvStatusWeather;
    private TextView tvStatusTemperature;
    private ImageView weatherStateIcon;
    private ImageView temperatureStateIcon;

    private User user;
    private AsyncTaskPostLogout logoutTask;
    private Toast currentTouchToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bindViews();

        Intent intent = this.getIntent();
        Bundle extras = intent.getExtras();
        extras.getSerializable(Constants.INTENT_USER_KEY);
        user = (User)getIntent().getExtras().getSerializable(Constants.INTENT_USER_KEY);

        final TextView bedStateLabel = bedStateView.findViewById(R.id.tv_card_description_home_status);
        final TextView doorStateLabel = doorStateView.findViewById(R.id.tv_card_description_home_status);
        final TextView weatherStateLabel = weatherStateView.findViewById(R.id.tv_card_description_home_status);
        final TextView temperatureStateLabel = temperatureStateView.findViewById(R.id.tv_card_description_home_status);
        bedStateLabel.setText(R.string.label_awake);
        doorStateLabel.setText(R.string.label_house_state);
        weatherStateLabel.setText(R.string.label_weather);
        temperatureStateLabel.setText(R.string.label_temperature);

        final ImageView bedStateIcon = bedStateView.findViewById(R.id.iv_card_icon_home_status);
        final ImageView doorStateIcon = doorStateView.findViewById(R.id.iv_card_icon_home_status);
        weatherStateIcon = weatherStateView.findViewById(R.id.iv_card_icon_home_status);
        temperatureStateIcon = temperatureStateView.findViewById(R.id.iv_card_icon_home_status);
        bedStateIcon.setImageResource(R.drawable.ic_close_black_24dp);
        doorStateIcon.setImageResource(R.drawable.ic_close_black_24dp);
        weatherStateIcon.setImageResource(R.drawable.ic_close_black_24dp);
        temperatureStateIcon.setImageResource(R.drawable.ic_close_black_24dp);

        initializeCharts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.auth_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case R.id.action_logout:
                makeLogout();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SimpleDateFormat")
    private void initializeCharts() {
        initializeStatusCards();
        initializeCurrentValuesChart();
    }

    private void initializeStatusCards() {
        final OnFailureListener onFailure = e -> {
            Log.d("DEBUG_TSNGApp", "initializeStatusCards(): " + e.getMessage());
//            Toast.makeText(HomeActivity.this,
//                    this.getString(R.string.status_card_awake_failed), Toast.LENGTH_LONG).show();
        };

        final SMARTAAL.GetBedState getBedState = new SMARTAAL.GetBedState(user.getElder_id(),
            user.getAcessToken(), state -> {
                final MaterialCardView iconView = bedStateView.findViewById(R.id.cv_icon_home_status);
                updateBooleanStatusCard(tvStatusAwake, iconView, state.isAwake(), R.color.md_green_500,
                        R.color.md_red_500, R.string.label_yes, R.string.label_no);

            }, onFailure);
        final SMARTAAL.GetDoorState getDoorState = new SMARTAAL.GetDoorState(user.getElder_id(),
            user.getAcessToken(), state -> {
                final MaterialCardView iconView = doorStateView.findViewById(R.id.cv_icon_home_status);
                updateBooleanStatusCard(tvStatusInside, iconView, state.isInside(), R.color.md_green_500,
                        R.color.md_red_500, R.string.label_inside, R.string.label_outside);
            }, onFailure);
        final SMARTAAL.GetTemperatureValue getTemperature = new SMARTAAL.GetTemperatureValue(
                user.getElder_id(), user.getAcessToken(), state -> {
            final MaterialCardView weatherIconView =
                    weatherStateView.findViewById(R.id.cv_icon_home_status);
            final MaterialCardView temperatureIconView =
                    temperatureStateView.findViewById(R.id.cv_icon_home_status);

            @DrawableRes int weatherIcon;
            @ColorRes int weatherColor;
            final String weather = state.getWeather();
            switch(weather) {
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

            @DrawableRes int temperatureIcon = R.drawable.ic_tmdi_hermometer_black_24dp;
            @ColorRes int temperatureColor;
            final int temperature = state.getTemperature();
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

            weatherStateIcon.setImageResource(weatherIcon);
            temperatureStateIcon.setImageResource(temperatureIcon);
            weatherIconView.setCardBackgroundColor(
                    ContextCompat.getColor(HomeActivity.this, weatherColor));
            temperatureIconView.setCardBackgroundColor(
                    ContextCompat.getColor(HomeActivity.this, temperatureColor));

            tvStatusWeather.setText(weather);
            tvStatusTemperature.setText(String.valueOf(temperature));

        }, onFailure);

        getBedState.execute();
        getDoorState.execute();
        getTemperature.execute();
    }


    private <T extends CardView> void updateBooleanStatusCard(TextView textView,
                                                              T iconView, boolean condition,
                                                              @ColorRes int colorPositive,
                                                              @ColorRes int colorNegative,
                                                              @StringRes int stringPositive,
                                                              @StringRes int stringNegative) {
        if (condition) {
            iconView.setCardBackgroundColor(
                    ContextCompat.getColor(HomeActivity.this, colorPositive));
            textView.setText(stringPositive);
        } else {
            iconView.setCardBackgroundColor(
                    ContextCompat.getColor(HomeActivity.this, colorNegative));
            textView.setText(stringNegative);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void initializeCurrentValuesChart() {
        new SMARTAAL.GetCurrentLastValues(user.getElder_id(), 5, user.getAcessToken(),
            values -> {
                List<Entry> chartEntries = new ArrayList<>();

                for (int i = 0; i < values.size(); i++) {
                    SMARTAAL.GetCurrentLastValues.Data value = values.get(i);

                    String sDate = new SimpleDateFormat("HHmmss").format(value.getDate());
                    float fDate = Float.valueOf(sDate);
                    float val = value.getValue();
                    chartEntries.add(new Entry(fDate, val));
                }

                double max = Stream.of(values)
                        .mapToDouble(SMARTAAL.GetCurrentLastValues.Data::getValue)
                        .max().getAsDouble();

                chartElectricalCurrent.setNoDataText(this.getString(R.string.chart_current_no_data));
                chartElectricalCurrent.setDragEnabled(true);
                chartElectricalCurrent.setScaleEnabled(false);
                chartElectricalCurrent.getLegend().setEnabled(false);
                chartElectricalCurrent.getAxisRight().setEnabled(false);
                chartElectricalCurrent.getDescription().setEnabled(false);

                LineDataSet dataSet = new LineDataSet(chartEntries, null);
                LineData lineData = new LineData(dataSet);

                int circleColor = ContextCompat.getColor(HomeActivity.this, android.R.color.white);
                int gridColor = ContextCompat.getColor(HomeActivity.this, R.color.md_grey_400);
                dataSet.setLineWidth(2);
                dataSet.setColor(circleColor);
                dataSet.setValueTextColor(circleColor);
                dataSet.setCircleRadius(5);
                dataSet.setCircleColor(circleColor);
                dataSet.setDrawValues(false);
                chartElectricalCurrent.setData(lineData);

                YAxis yAxis = chartElectricalCurrent.getAxisLeft();
                yAxis.setAxisMaximum((float) max + 5);
                yAxis.setAxisMinimum(0);
                yAxis.setLabelCount(10);
                yAxis.setGridColor(gridColor);
                yAxis.setTextColor(circleColor);

                XAxis xAxis = chartElectricalCurrent.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new DateAxisFormatter());
                xAxis.setTextColor(circleColor);
                xAxis.setGridColor(gridColor);
                xAxis.setGranularity(1f);

                chartElectricalCurrent.setOnChartGestureListener(new OnChartGestureListener() {
                    @Override
                    public void onChartSingleTapped(MotionEvent me) {
                        Entry entry = chartElectricalCurrent.getEntryByTouchPoint(me.getX(), me.getY());
                        if (currentTouchToast != null) currentTouchToast.cancel();

                        try {
                            SimpleDateFormat formattedDate = new SimpleDateFormat("HHmmss");
                            Date date = formattedDate.parse(String.valueOf(entry.getX()));
                            String strDate = new SimpleDateFormat("HH:mm").format(date);

                            currentTouchToast = Toast.makeText(HomeActivity.this,
                                    "Electrical current value: " + entry.getY() + "W\nTime: " + strDate,
                                    Toast.LENGTH_SHORT);
                            currentTouchToast.show();
                        } catch (ParseException e) {
                            currentTouchToast = Toast.makeText(HomeActivity.this,
                                    "Failed to get entry point", Toast.LENGTH_SHORT);
                            currentTouchToast.show();
                        }
                    }

                    @Override
                    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}
                    @Override
                    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}
                    @Override
                    public void onChartLongPressed(MotionEvent me) {}
                    @Override
                    public void onChartDoubleTapped(MotionEvent me) {}
                    @Override
                    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {}
                    @Override
                    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {}
                    @Override
                    public void onChartTranslate(MotionEvent me, float dX, float dY) {}
                });

                chartElectricalCurrent.notifyDataSetChanged();
                chartElectricalCurrent.invalidate();
            },
            e -> {
                Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            })
            .execute();
    }

    private void makeLogout(){
        this.logoutTask = new AsyncTaskPostLogout(jsonString -> {
            if(jsonString.equals(Constants.HTTP_OK)){
                Toast.makeText(HomeActivity.this, "Logout Success", Toast.LENGTH_SHORT).show();

                //todo - retira token das shared preferences e coloca user a null e passa para a login activity
                user = null;
                LoginManager.getInstance().removeFromSharedPreference(HomeActivity.this);

                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                HomeActivity.this.finish();
            }
            else{
                Toast.makeText(HomeActivity.this, "Logout Success", Toast.LENGTH_SHORT).show();
            }
        }, user.getAcessToken());

        logoutTask.execute(Constants.LOGOUT_URL);
    }

    private void bindViews() {
        chartElectricalCurrent = findViewById(R.id.chart_current_values_home);
        bedStateView = findViewById(R.id.v_home_bed_state);
        doorStateView = findViewById(R.id.v_home_house_state);
        weatherStateView = findViewById(R.id.v_home_weather_state);
        temperatureStateView = findViewById(R.id.v_home_temperature_value);
        tvStatusAwake = bedStateView.findViewById(R.id.tv_card_values_home_status);
        tvStatusInside = doorStateView.findViewById(R.id.tv_card_values_home_status);
        tvStatusWeather = weatherStateView.findViewById(R.id.tv_card_values_home_status);
        tvStatusTemperature = temperatureStateView.findViewById(R.id.tv_card_values_home_status);
    }
}
