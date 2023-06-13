package si.uni_lj.fe.tnuv.bt_simple;

import android.Manifest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    public AppViewModel appViewModel;
    private boolean isFilesAndMediaPermissionGranted = false;
    private boolean isLocationPermissionGranted = false;
    private boolean isBluetoothPermissionGranted = false;
    private boolean isBluetoothConnectPermissionGranted = false;

    private List<String> deniedPermissionList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if (result.get(Manifest.permission.BLUETOOTH_SCAN) != null){
                    isBluetoothPermissionGranted = result.get(Manifest.permission.BLUETOOTH_SCAN);
                    if (!isBluetoothPermissionGranted) {
                        deniedPermissionList.add("Bluetooth Scan");
                    }
                }
                if (result.get(Manifest.permission.BLUETOOTH_CONNECT) != null) {
                    isBluetoothConnectPermissionGranted = result.get(Manifest.permission.BLUETOOTH_CONNECT);
                    if (!isBluetoothConnectPermissionGranted) {
                        deniedPermissionList.add("Bluetooth Connect");
                    }
                }
                if (result.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null){
                    isFilesAndMediaPermissionGranted = result.get(Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (!isFilesAndMediaPermissionGranted) {
                        deniedPermissionList.add("Read External Storage");
                    }
                }
                if (result.get(Manifest.permission.ACCESS_FINE_LOCATION) != null){
                    isLocationPermissionGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                    if (!isLocationPermissionGranted) {
                        deniedPermissionList.add("Access Fine Location");
                    }
                }

                if (!deniedPermissionList.isEmpty()) {
                    showRationaleDialog(deniedPermissionList);
                }

            }
        });

        requestPermission();

        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        appViewModel.initializeData();
        appViewModel.readDataFromFile(this, "bluetoothData.txt");
        //Log.d("workoutViewModel", Double.toString(workoutViewModel.vmaxData().getValue().get("Poteg na Roke").get("80%RM")));


        FragmentManager fragmentManager = getSupportFragmentManager();

        // button for switching to workout fragment
        Button btnWorkout = findViewById(R.id.nav_workout);
        btnWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, Workout.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // Name can be null
                        .commit();

            }
        });

        // button for switching to analysis fragment
        Button btnAnalysis = findViewById(R.id.nav_analysis);
        btnAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, Analysis.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // Name can be null
                        .commit();

            }
        });

        // button for switching to workout fragment
        Button btnSettings = findViewById(R.id.nav_settings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, Settings.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // Name can be null
                        .commit();

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        requestPermission();
    }


    private void requestPermission(){

        List<String> permissionRequest = new ArrayList<String>();
        List<String> permissionRationaleList = new ArrayList<String>();

        isFilesAndMediaPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;

        isLocationPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        isBluetoothPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED;

        isBluetoothConnectPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED;



        if (!isBluetoothPermissionGranted){
            permissionRequest.add(Manifest.permission.BLUETOOTH_SCAN);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_SCAN)) {
                permissionRationaleList.add("Bluetooth Scan");
            }
        }
        if (!isBluetoothConnectPermissionGranted){
            permissionRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_CONNECT)) {
                permissionRationaleList.add("Bluetooth Connect");
            }
        }
        if (!isFilesAndMediaPermissionGranted){
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissionRationaleList.add("Read External Storage");
            }
        }
        if (!isLocationPermissionGranted){
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                permissionRationaleList.add("Access Fine Location");
            }
        }


        if(!permissionRequest.isEmpty()){
            if(!permissionRationaleList.isEmpty()){
                showRationaleDialog(permissionRationaleList);
            }
            else{
                mPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
            }
        }
    }
    private void showRationaleDialog(List<String> permissionList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions Required")
                .setMessage("You have declined the " + permissionList.toString() + " permissions. Please allow them for the app to function properly.")
                .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the user to the settings page and bring them back to your app after they're done
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // You can handle negative button click here
                        finish();
                    }
                });
        builder.create().show();
    }



    /*



    @Override
    protected void onPause() {
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
        int lighterBlue = ContextCompat.getColor(this, R.color.lighter_blue);
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
        DateMarkerView markerView = new DateMarkerView(this, R.layout.marker_view);
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
                if (parts[0].equals("Poteg na Roke") && parts[3].equals("40") && parts[4].equals("daily_readiness")) {
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
                viewHolder.loadView.setText("Load [kg]");
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

    */


}