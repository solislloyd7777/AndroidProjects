package com.mh.mytransaction;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class Performance extends AppCompatActivity {
    DatabaseHelper dh;
    int number_sales;
    private BarChart mChart;
    ArrayList<BarEntry> barEntries;
    ArrayList<String> label;
    List<Performance_list> performance_lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);
        mChart=(BarChart)findViewById(R.id.chart1);
        mChart.getDescription().setEnabled(false);

        dh=new DatabaseHelper(this);
        performance_lists=dh.getTrans_sales();
        number_sales=dh.getTransaction_sales();
        setData(number_sales);
        mChart.setFitBars(true);
        barEntries=new ArrayList<>();
        label=new ArrayList<>();

        for(int i=0;i<performance_lists.size();i++){
            float mysales=performance_lists.get(i).getyValues();
            String labelname=performance_lists.get(i).getxValues();
            barEntries.add(new BarEntry(i,mysales));
            label.add(labelname);
        }

        BarDataSet barDataSet=new BarDataSet(barEntries,"Daily Gross Sales");
        barDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        Description description=new Description();
        description.setText("Days");
        mChart.setDescription(description);
        BarData barData=new BarData(barDataSet);
        mChart.setData(barData);
        XAxis xAxis=mChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(label));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(label.size());
        xAxis.setLabelRotationAngle(270);
        mChart.animateY(2000);
        mChart.invalidate();



    }

    private void setData(int num_sales){
        dh=new DatabaseHelper(this);
        ArrayList<BarEntry> yVals=dh.getYvalue();
    }
}
