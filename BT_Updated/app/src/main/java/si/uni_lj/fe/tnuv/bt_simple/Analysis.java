package si.uni_lj.fe.tnuv.bt_simple;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.FileObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.github.mikephil.charting.utils.MPPointF;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


public class Analysis extends Fragment {

    private LineChart chart;
    private List<Entry> entries = new ArrayList<>();
    private File file;
    private DateFormat format = new SimpleDateFormat("d.MM.yyyy/HH:mm:ss", Locale.getDefault());

    private float minPeakVelocity = Float.MAX_VALUE;
    private float maxPeakVelocity = Float.MIN_VALUE;
    private FileObserver fileObserver;
    private List<ExerciseData> exerciseDataList = new ArrayList<>();
    private ExerciseDataAdapter exerciseDataAdapter;
    private String selectedExercise;  // Declare as a class-level variable


    public Analysis() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analysis, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Start the FileObserver
        fileObserver.startWatching();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chart = view.findViewById(R.id.chart);
        chart.setBackgroundColor(Color.WHITE);  // set chart's background color to white
        chart.setExtraOffsets(0f, 10f, 0f, 10f);  // modify the offset values here
        file = new File(getContext().getExternalFilesDir(null), "bluetoothData.txt");
        MainActivity mainActivity = (MainActivity) requireActivity();


        // Initialize RecyclerView and its adapter
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        exerciseDataAdapter = new ExerciseDataAdapter(exerciseDataList);
        recyclerView.setAdapter(exerciseDataAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.exercises_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedExercise = parent.getItemAtPosition(position).toString();
                // When an exercise is selected, re-populate the table data and notify the adapter
                populateTableData(mainActivity.appViewModel.vmaxData().getValue());
                exerciseDataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        // Initialize the FileObserver
        fileObserver = new FileObserver(file.getAbsolutePath()) {
            @Override
            public void onEvent(int event, @Nullable String path) {
                // If the file is modified, update the chart
                if ((FileObserver.MODIFY & event) != 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            plotGraph();
                        }
                    });
                }
            }
        };

        plotGraph();
    }
    @Override
    public void onPause() {
        super.onPause();
        // Stop the FileObserver
        fileObserver.stopWatching();
    }

    public class DateMarkerView extends MarkerView {
        private final TextView tvContent;

        public DateMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);
            tvContent = findViewById(R.id.tvContent);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            long unixTimestamp = (long) e.getX();
            Date date = new Date(unixTimestamp);
            DateFormat dateFormat = new SimpleDateFormat("d.MM", Locale.ENGLISH);
            String dateString = dateFormat.format(date);
            Log.d("DateMarkerView", "Data point pressed: " + dateString); // log the date corresponding to the data point
            tvContent.setText(dateString);
            super.refreshContent(e, highlight);
        }


        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), -getHeight());
        }
    }




    public void plotGraph() {
        readDataFromFile();

        // Sort the entries by date
        Collections.sort(entries, new EntryXComparator());

        // Create a data set from the entries
        LineDataSet dataSet = new LineDataSet(entries, "Poteg na Roke (40kg)");

        // Configure the data set
        int lighterBlue = ContextCompat.getColor(getContext(), R.color.lighter_blue);
        dataSet.setColor(lighterBlue);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setCircleColor(lighterBlue);
        dataSet.setCircleHoleColor(lighterBlue);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                return String.format(Locale.ENGLISH, "%.2f", entry.getY());
            }
        });

        // Create a data object with the data sets
        LineData lineData = new LineData(dataSet);
        chart.setDescription(null);

        // Set the data to the chart
        chart.setData(lineData);

        // Customize the chart
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false); // disable the right y-axis

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false); // remove the grid lines
        yAxisLeft.setDrawLabels(false); // Hide labels on the Y-axis
        yAxisLeft.setAxisMinimum(minPeakVelocity - 0.1f); // start at minimum peak velocity - 0.2
        yAxisLeft.setAxisMaximum(maxPeakVelocity + 0.1f); // end at maximum peak velocity + 0.2
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
        xAxis.setDrawLabels(false); // hide labels on the X-Axis
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceMin(0.1f); // add space before the first entry



        Legend legend = chart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setTextColor(Color.BLACK);

        // Add MarkerView to show date when a data point is clicked
        DateMarkerView markerView = new DateMarkerView(getContext(), R.layout.marker_view);
        chart.setMarker(markerView);

        chart.invalidate();
    }


    private void readDataFromFile() {
        entries.clear();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                if (parts[0].equals("Poteg na Roke") && parts[3].equals("40") && parts[5].equals("daily_readiness")) {
                    float peakVelocity = Float.parseFloat(parts[2]);
                    Date date = format.parse(parts[1]);
                    if (date != null) {
                        Log.d("DateParsing", "Parsed date: " + date.toString());
                        // Set hours, minutes, and seconds to 0 to standardize the time for all entries
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);


                        entries.add(new Entry(calendar.getTimeInMillis(), peakVelocity));
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

    public class ExerciseData {
        private float peakVelocity;
        private float load;
        private String percentageRM;

        public ExerciseData(float peakVelocity, float load, String percentageRM) {
            this.peakVelocity = peakVelocity;
            this.load = load;
            this.percentageRM = percentageRM;
        }

        public float getPeakVelocity() {
            return peakVelocity;
        }

        public float getLoad() {
            return load;
        }

        public String getPercentageRM() {
            return percentageRM;
        }
    }

    public class ExerciseDataAdapter extends RecyclerView.Adapter<ExerciseDataAdapter.ViewHolder> {
        private List<ExerciseData> exerciseDataList;
        private static final int HEADER_VIEW_TYPE = 0;
        private static final int ITEM_VIEW_TYPE = 1;


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView peakVelocityView;
            private final TextView loadView;
            private final TextView percentageRMView;

            public ViewHolder(View view) {
                super(view);
                peakVelocityView = view.findViewById(R.id.peak_velocity);
                loadView = view.findViewById(R.id.load);
                percentageRMView = view.findViewById(R.id.percentage_rm);
            }
        }

        public class HeaderViewHolder extends ViewHolder {
            public HeaderViewHolder(View view) {
                super(view);
            }
        }

        public ExerciseDataAdapter(List<ExerciseData> dataSet) {
            this.exerciseDataList = dataSet;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == HEADER_VIEW_TYPE) {
                View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_header_item, parent, false);
                return new HeaderViewHolder(headerView);
            } else {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row_item, parent, false);
                return new ViewHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            if (getItemViewType(position) == HEADER_VIEW_TYPE) {
                // Bind header data
                viewHolder.peakVelocityView.setText("Vmax [m/s]");
                MainActivity mainActivity = (MainActivity) requireActivity();
                if (mainActivity.appViewModel.units().getValue() == 0){
                    viewHolder.loadView.setText("Load [Kg]");
                } else {
                    viewHolder.loadView.setText("Load [Lbs]");
                }

                viewHolder.percentageRMView.setText("%RM");
            } else {
                // Bind exercise data
                ExerciseData exerciseData = exerciseDataList.get(position - 1);
                viewHolder.peakVelocityView.setText(String.valueOf(exerciseData.getPeakVelocity()));
                viewHolder.loadView.setText(String.valueOf(exerciseData.getLoad()));

                // Modify the percentageRM value to remove "RM" from it
                String percentageRM = exerciseData.getPercentageRM();
                percentageRM = percentageRM.replace("RM", "");  // remove "RM" from the string
                viewHolder.percentageRMView.setText(percentageRM);
            }
        }

        @Override
        public int getItemCount() {
            // Add 1 to account for the header row
            return exerciseDataList.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return HEADER_VIEW_TYPE;
            } else {
                return ITEM_VIEW_TYPE;
            }
        }
    }

    void populateTableData(HashMap<String, HashMap<String, Double[]>> exercisesData) {
        exerciseDataList.clear();
        HashMap<String, Double[]> innerMap = exercisesData.get(selectedExercise);
        if (innerMap != null) {
            // Create a TreeMap with a custom comparator
            TreeMap<String, Double[]> sortedInnerMap = new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    // Extract the numeric part from the keys and compare them
                    int percentage1 = Integer.parseInt(o1.substring(0, o1.indexOf('%')));
                    int percentage2 = Integer.parseInt(o2.substring(0, o2.indexOf('%')));
                    return Integer.compare(percentage1, percentage2);
                }
            });
            sortedInnerMap.putAll(innerMap);
            for (Map.Entry<String, Double[]> entry : sortedInnerMap.entrySet()) {
                String percentageRM = entry.getKey();
                Double[] values = entry.getValue();
                // Ensure that there are two values for each percentageRM
                if (values.length == 2) {
                    Double peakVelocity = values[0];
                    Double load = values[1];
                    String tag = "load_vel_profile";
                    if (selectedExercise.equals("Poteg na Roke") && tag.equals("daily_readiness")) {
                        continue; // Skip entries with "daily_readiness" for "Poteg na Roke"
                    }
                    if (tag.equals("load_vel_profile")) {
                        exerciseDataList.add(new ExerciseData(peakVelocity.floatValue(), load.floatValue(), percentageRM));
                    }
                }
            }
        }
        /*
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts[0].equals(selectedExercise)) {
                    String tag = parts[5];
                    if (selectedExercise.equals("Poteg na Roke") && tag.equals("daily_readiness")) {
                        continue; // Skip entries with "daily_readiness" for "Poteg na Roke"
                    }
                    if (tag.equals("load_vel_profile")) {
                        float peakVelocity = Float.parseFloat(parts[2]);
                        float load = Float.parseFloat(parts[3]);
                        String percentageRM = parts[4];
                        exerciseDataList.add(new ExerciseData(peakVelocity, load, percentageRM));
                    }
                }
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

    }
}