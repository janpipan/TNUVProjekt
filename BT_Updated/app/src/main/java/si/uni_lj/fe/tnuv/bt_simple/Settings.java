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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Set;


public class Settings extends Fragment implements ConnectionStatusListener{

    
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothData viewModel;
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

        MainActivity mainActivity = (MainActivity) requireActivity();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ListView devicesListView = view.findViewById(R.id.devices_list_view);
        viewModel = new ViewModelProvider(requireActivity()).get(BluetoothData.class);
        deviceListAdapter = new ArrayAdapter<>(requireContext(), R.layout.list_item);
        devicesListView.setAdapter(deviceListAdapter);

        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String deviceInfo = deviceListAdapter.getItem(position);
                String address = deviceInfo.split(" - ")[1];
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                ConnectThread connectThread = new ConnectThread(device, Settings.this, viewModel);
                connectThread.start();
            }
        });
        Button btnSearchDevices = view.findViewById(R.id.search_button);
        btnSearchDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.appViewModel.setDevicesDisplayed(true);
                displayPairedDevices();
            }
        });

        if (mainActivity.appViewModel.arePairedDevicesDisplayed().getValue()){
            displayPairedDevices();
        }


        Spinner spinner = view.findViewById(R.id.spinner_units);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mainActivity.appViewModel.units().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
                spinner.setSelection(position);
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mainActivity.appViewModel.setUnits(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    private void displayPairedDevices() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceListAdapter.add(device.getName() + " - " + device.getAddress());
                //deviceListAdapter.add(device.getName());
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