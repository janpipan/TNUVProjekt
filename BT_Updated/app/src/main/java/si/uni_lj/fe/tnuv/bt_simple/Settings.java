package si.uni_lj.fe.tnuv.bt_simple;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;


public class Settings extends Fragment implements ConnectionStatusListener{

    
    private BluetoothAdapter bluetoothAdapter;

    private ArrayAdapter<String> deviceListAdapter;

    public Settings() {
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ListView devicesListView = view.findViewById(R.id.devices_list_view);
        deviceListAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        devicesListView.setAdapter(deviceListAdapter);

        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String deviceInfo = deviceListAdapter.getItem(position);
                String address = deviceInfo.split(" - ")[1];
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                ConnectThread connectThread = new ConnectThread(device, Settings.this);
                connectThread.start();
            }
        });
        displayPairedDevices();
    }

    private void displayPairedDevices() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceListAdapter.add(device.getName() + " - " + device.getAddress());
            }
        } else {
            Toast.makeText(requireContext(), "No paired devices found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuccess() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(requireContext(), "Connected successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailed() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(requireContext(), "Connection failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNewReceivedData(String message) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}