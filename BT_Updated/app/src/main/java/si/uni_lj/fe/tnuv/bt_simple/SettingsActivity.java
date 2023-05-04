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

public class SettingsActivity extends AppCompatActivity implements ConnectionStatusListener{

    private BluetoothAdapter bluetoothAdapter;

    private ArrayAdapter<String> deviceListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ListView devicesListView = findViewById(R.id.devices_list_view);
        deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        devicesListView.setAdapter(deviceListAdapter);


        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String deviceInfo = deviceListAdapter.getItem(position);
                String address = deviceInfo.split(" - ")[1];
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                ConnectThread connectThread = new ConnectThread(device, SettingsActivity.this);
                connectThread.start();
            }
        });

        displayPairedDevices();
    }

    private void displayPairedDevices() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceListAdapter.add(device.getName() + " - " + device.getAddress());
            }
        } else {
            Toast.makeText(this, "No paired devices found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SettingsActivity.this, "Connected successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SettingsActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUpdateTextView(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SettingsActivity.this, "message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Context getContext() {
        return this;
    }

    public void startActivityAnalysis(View view) {
        Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public void startActivityWorkout(View view) {
        Intent intent = new Intent(SettingsActivity.this,WorkoutActivity.class);
        startActivity(intent);
    }
}