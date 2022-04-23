package com.example.contacthandbook.fragment.home;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.contacthandbook.R;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.model.Mark;
import com.example.contacthandbook.model.Student;
import com.example.contacthandbook.model.User;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    private static final String PREFS_NAME = "USER_INFO";
    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    LineChart chart;
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupChart();
        FirebaseManager firebaseManager = new FirebaseManager(getContext());
        User user = getSavedInfo();
        List<Mark> markList = new ArrayList<>();

        //show progressHUD
        KProgressHUD hud = KProgressHUD.create(getContext())
                .setDetailsLabel("Loading chart...")
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        for (int year = currentYear - 2; year <= currentYear; year++) {
            Student student = new Student(user.getUsername()); //only need id here
            firebaseManager.getMark(student, year, new FirebaseCallBack.GetMarkCallback() {
                @Override
                public void onCallback(Mark mark) {
                    markList.add(mark);
                    updateChart(markList);
                    hud.dismiss();
                }
            });
        }

    }

    // update chart when loaded mark
    void updateChart(List<Mark> markList) {
        ArrayList<Entry> values = new ArrayList<>();

        //sort mark for year
        Collections.sort(markList, (s1, s2) -> {
            if (s1.getYear() > s2.getYear()) { return 0; }
            else { return -1; }
        });
        Log.e("NUMBER OF YEAR", String.valueOf(markList.size()));
        for (Mark mark: markList) {
            Log.e("YEAR", String.valueOf(mark.getYear()));
            Log.e("AVERAGE", String.valueOf(mark.getAveragePoint()));
            float year = mark.getYear();
            float averagePoint = (float) mark.getAveragePoint();
            values.add(new Entry(year, averagePoint));
        }
        LineDataSet dataSet = new LineDataSet(values, "Average Point Chart");
        dataSet.setValueTextSize(10f);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();
    }


    //set up chart in the first time
    void setupChart() {
        chart = getView().findViewById(R.id.chart);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1);
        xAxis.setTextSize(10f);
        xAxis.setLabelCount(25);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return  String.valueOf(((int) value));
            }
        });
        xAxis.setAxisMinimum(currentYear - 3);
        xAxis.setAxisMaximum(currentYear + 1);
    }


    //get saved information from shared pref, saved it in login screen
    public User getSavedInfo() {
        User user = new User();
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        user.setName(sharedPref.getString("name", "Contact Handbook"));
        user.setRole(sharedPref.getString("role", "student"));
        user.setUsername(sharedPref.getString("username", "1"));
        return  user;
    }



}

