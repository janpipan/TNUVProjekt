package si.uni_lj.fe.tnuv.bt_simple;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private LineChart chart;
    private List<Entry> entries = new ArrayList<>();
    private File file;
    private DateFormat format = new SimpleDateFormat("d.MM.yyyy", Locale.getDefault());
    private float minPeakVelocity = Float.MAX_VALUE;
    private float maxPeakVelocity = Float.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chart = findViewById(R.id.chart);
        chart.setBackgroundColor(Color.WHITE);  // set chart's background color to white
        file = new File(getExternalFilesDir(null), "bluetoothData.txt");

        plotGraph();
    }

    @Override
    protected void onResume() {
        super.onResume();
        plotGraph(); // Update the graph when the activity is resumed.
    }

    public void plotGraph() {
        readDataFromFile();

        // Sort the entries by date
        Collections.sort(entries, new EntryXComparator());

        // Create a data set from the entries
        LineDataSet dataSet = new LineDataSet(entries, "Poteg na Roke (40kg)");

        // Configure the data set
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setCircleColor(Color.RED);
        dataSet.setCircleHoleColor(Color.RED);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                return String.format(Locale.ENGLISH, "%.2f", entry.getY());
            }
        });

        // Create a data object with the data sets
        LineData lineData = new LineData(dataSet);

        // Set the data to the chart
        chart.setData(lineData);

        // Customize the chart
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false); // disable the right y-axis

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false); // remove the grid lines
        yAxisLeft.setAxisMinimum(minPeakVelocity - 0.2f); // start at minimum peak velocity - 0.2
        yAxisLeft.setAxisMaximum(maxPeakVelocity + 0.2f); // end at maximum peak velocity + 0.2
        yAxisLeft.setGranularity(1f); // interval 1
        yAxisLeft.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.ENGLISH, "%.2f", value);
            }
        });
        yAxisLeft.setLabelCount(6, true);
        yAxisLeft.setTextColor(Color.BLACK);
        yAxisLeft.setDrawAxisLine(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // set the XAxis to the bottom
        xAxis.setDrawGridLines(false); // remove the grid lines
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return new SimpleDateFormat("d.MM", Locale.ENGLISH).format(new Date((long) value));
            }
        });

        xAxis.setCenterAxisLabels(true); // align the date labels properly under the corresponding data points
        xAxis.setLabelCount(6, true);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(true);

        Description description = new Description();
        description.setText("Poteg na Roke (40kg)");
        description.setTextColor(Color.BLACK);
        description.setTextSize(12f);

        // Calculate the width based on the chart's dimensions and offsets
        float descriptionWidth = description.getText().length() * description.getTextSize();
        float descriptionOffsetRight = chart.getViewPortHandler().offsetRight();
        float chartWidth = chart.getViewPortHandler().contentWidth();
        float descriptionPositionX = chartWidth - descriptionOffsetRight - descriptionWidth;
        float descriptionPositionY = chart.getTop() + description.getTextSize() + 10f;
        description.setPosition(descriptionPositionX, descriptionPositionY);

        chart.setDescription(description);

        Legend legend = chart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setTextColor(Color.BLACK);

        chart.invalidate(); // Refresh the chart
    }






    private void readDataFromFile() {
        entries.clear();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts[0].equals("Poteg na Roke") && parts[3].equals("40")) {
                    float peakVelocity = Float.parseFloat(parts[2]);
                    Date date = format.parse(parts[1].split("/")[0]);
                    if (date != null) {
                        entries.add(new Entry(date.getTime(), peakVelocity));
                        minPeakVelocity = Math.min(minPeakVelocity, peakVelocity);
                        maxPeakVelocity = Math.max(maxPeakVelocity, peakVelocity); // update maximum peak velocity
                    }
                }
            }
            scanner.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void startActivityWorkout(View view) {
        Intent intent = new Intent(MainActivity.this, WorkoutActivity.class);
        startActivity(intent);
    }

    public void startActivitySettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}