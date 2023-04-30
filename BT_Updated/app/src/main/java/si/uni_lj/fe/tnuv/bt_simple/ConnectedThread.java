package si.uni_lj.fe.tnuv.bt_simple;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private final BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final ConnectionStatusListener connectionStatusListener;

    public ConnectedThread(BluetoothSocket socket, ConnectionStatusListener listener) {
        this.socket = socket;
        this.connectionStatusListener = listener;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e("ConnectedThread", "Error occurred when creating input and output streams", e);
        }

        inputStream = tmpIn;
        outputStream = tmpOut;
    }

    public void run() {
        try {
            while (true) {
                String newContent = readTxtFileContent();
                Log.d("ConnectedThread", "Received new content: " + newContent);
                connectionStatusListener.onUpdateTextView(newContent);
            }
        } catch (IOException e) {
            Log.d("ConnectedThread", "Error occurred when reading input stream", e);
        }
    }

    private String readTxtFileContent() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bytesRead;
        byte[] buffer = new byte[1024];

        while (true) {
            bytesRead = inputStream.read(buffer);
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
            socket.close();
        } catch (IOException e) {
            Log.e("ConnectedThread", "Error occurred when closing the socket", e);
        }
    }
}