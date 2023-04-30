package si.uni_lj.fe.tnuv.bt_simple;

import static android.content.ContentValues.TAG;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.Timer;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    private static final int CONNECT_SUCCESS = 0;
    private static final int CONNECT_FAILED = 1;
    private static final int UPDATE_TEXT_VIEW = 2;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> deviceListAdapter;
    private Handler connectionHandler;
    private TextView receivedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receivedTextView = findViewById(R.id.received_text_view);

        connectionHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == CONNECT_SUCCESS) {
                    Toast.makeText(MainActivity.this, "Connected successfully", Toast.LENGTH_SHORT).show();
                } else if (msg.what == CONNECT_FAILED) {
                    Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                } else if (msg.what == UPDATE_TEXT_VIEW) {
                    receivedTextView.setText((String) msg.obj);
                }
                return true;
            }
        });

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
                ConnectThread connectThread = new ConnectThread(device);
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

    private class ConnectThread extends Thread {
        private final BluetoothDevice device;
        BluetoothSocket socket;

        public ConnectThread(BluetoothDevice device) {
            this.device = device;
            BluetoothSocket tmpSocket = null;
            UUID deviceUuid = MY_UUID;

            try {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                tmpSocket = device.createRfcommSocketToServiceRecord(deviceUuid);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Error creating socket with UUID " + deviceUuid.toString(), e);
                e.printStackTrace();
            }
            socket = tmpSocket;
        }

        public void run() {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothAdapter.cancelDiscovery();

            try {
                socket.connect();
                connectionHandler.obtainMessage(CONNECT_SUCCESS).sendToTarget();
                ConnectedThread connectedThread = new ConnectedThread(socket);
                connectedThread.start();
            } catch (IOException connectException) {
                try {
                    socket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
                connectionHandler.obtainMessage(CONNECT_FAILED).sendToTarget();
                return;
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input and output streams", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            try {
                while (true) {
                    String newContent = readTxtFileContent();
                    Log.d(TAG, "Received new content: " + newContent);
                    Message message = connectionHandler.obtainMessage(UPDATE_TEXT_VIEW, newContent);
                    message.sendToTarget();
                }
            } catch (IOException e) {
                Log.d(TAG, "Error occurred when reading input stream", e);
            }
        }

        private String readTxtFileContent() throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int bytesRead;
            byte[] buffer = new byte[1024];

            while (true) {
                bytesRead = mmInStream.read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                String temp = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
                if (temp.endsWith("\n\n")) {
                    break;
                }
            }
            return byteArrayOutputStream.toString("UTF-8");
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when closing the socket", e);
            }
        }
    }
}