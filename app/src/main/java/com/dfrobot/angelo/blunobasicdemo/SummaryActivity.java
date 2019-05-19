package com.dfrobot.angelo.blunobasicdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.dfrobot.angelo.blunobasicdemo.cBaseApplication.mBluetoothLeService;

public class SummaryActivity extends AppCompatActivity {
    private LineGraphSeries<DataPoint> s1;
    private LineGraphSeries<DataPoint> s2;
    private LineGraphSeries<DataPoint> s3;
    private GraphView tempGraph;
    private GraphView phGraph;
    private GraphView turbGraph;
    public ArrayList<Double> tempArray;
    public ArrayList<Double> phArray;
    public ArrayList<Double> turbArray;
    private TextView avgTemp;
    private TextView avgpH;
    private TextView avgTurb;
    private final DecimalFormat df = new DecimalFormat("0.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        tempGraph = (GraphView) findViewById(R.id.graph);
        phGraph = (GraphView) findViewById(R.id.graph2);
        turbGraph = (GraphView) findViewById(R.id.graph3);
        s1 = MainActivity.getS1();
        s2 = MainActivity.getS2();
        s3 = MainActivity.getS3();
        tempGraph.addSeries(s1);
        phGraph.addSeries(s2);
        turbGraph.addSeries(s3);
        tempGraph.getViewport().setScalable(true);
        tempGraph.getViewport().setScalableY(true);
        phGraph.getViewport().setScalable(true);
        phGraph.getViewport().setScalableY(true);
        turbGraph.getViewport().setScalable(true);
        turbGraph.getViewport().setScalableY(true);
        tempArray = MainActivity.getTempArray();
        phArray = MainActivity.getPhArray();
        turbArray = MainActivity.getTurbArray();
        avgTemp = (TextView) findViewById(R.id.avgTemp);
        avgpH = (TextView) findViewById(R.id.avgPh);
        avgTurb = (TextView) findViewById(R.id.avgturb);

        System.out.println(mBluetoothLeService.mConnectionState);

    }

    public double average(ArrayList<Double> array){
        double sum = 0;
        for (int i = 0; i < array.size(); i++)
            sum += array.get(i);
        return sum/array.size();
    }

    protected void onResume(){
        super.onResume();
        avgTemp.setText(df.format(average(tempArray)) + " unit");
        avgpH.setText(df.format(average(phArray)));
        avgTurb.setText(df.format(average(turbArray)) + "\u00B0" + "F");
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void startNew(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
