package si.uni_lj.fe.tnuv.bt_simple;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class WorkoutActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;

    private ArrayAdapter<String> deviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

    }

    public void startActivityAnalysis(View view) {
        Intent intent = new Intent(WorkoutActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public void startActivitySettings(View view) {
        Intent intent = new Intent(WorkoutActivity.this,SettingsActivity.class);
        startActivity(intent);
    }
}