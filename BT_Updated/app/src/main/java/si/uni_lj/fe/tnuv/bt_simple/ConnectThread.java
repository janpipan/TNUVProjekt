package si.uni_lj.fe.tnuv.bt_simple;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {
    private final BluetoothDevice device;
    private final BluetoothSocket socket;
    private final ConnectionStatusListener connectionStatusListener;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public ConnectThread(BluetoothDevice device, ConnectionStatusListener listener) {
        this.device = device;
        this.connectionStatusListener = listener;
        BluetoothSocket tmpSocket = null;

        try {
            if (ActivityCompat.checkSelfPermission(listener.getContext(), android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                tmpSocket = null;
            } else {
                tmpSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            }
        } catch (IOException e) {
            Log.e("ConnectThread", "Error creating socket with UUID " + MY_UUID.toString(), e);
            e.printStackTrace();
        }
        socket = tmpSocket;
    }

    public void run() {
        if (ActivityCompat.checkSelfPermission(connectionStatusListener.getContext(), android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        try {
            if (ActivityCompat.checkSelfPermission(connectionStatusListener.getContext(), android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            socket.connect();
            connectionStatusListener.onConnectionSuccess();
            ConnectedThread connectedThread = new ConnectedThread(socket, connectionStatusListener);
            connectedThread.start();
        } catch (IOException connectException) {
            try {
                socket.close();
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
            connectionStatusListener.onConnectionFailed();
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
