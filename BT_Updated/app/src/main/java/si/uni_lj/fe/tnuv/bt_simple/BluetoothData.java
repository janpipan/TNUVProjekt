package si.uni_lj.fe.tnuv.bt_simple;

import android.bluetooth.BluetoothSocket;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class BluetoothData extends ViewModel {
    private final MutableLiveData<BluetoothSocket> bluetoothSocket = new MutableLiveData<>();
    private final MutableLiveData<String> receivedData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> connected;


    public BluetoothData(){
        connected = new MutableLiveData<>();
        connected.setValue(false);
    }


    public void startConnection(){connected.postValue(true);}

    public void stopConnection(){connected.postValue(false);}

    public LiveData<Boolean> isConnected(){return connected;}

    public void setBluetoothSocket(BluetoothSocket socket) {
        bluetoothSocket.postValue(socket);
    }

    public LiveData<BluetoothSocket> getBluetoothSocket() {
        return bluetoothSocket;
    }

    public void setReceivedData(String data) {
        receivedData.postValue(data);
    }

    public LiveData<String> getReceivedData() {
        return receivedData;
    }

    public void clearReceivedData(){
        receivedData.setValue(null);
    }
}
