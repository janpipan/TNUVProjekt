package si.uni_lj.fe.tnuv.bt_simple;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = inputStream.read(buffer);
                if (bytes > 0) {
                    String newContent = new String(buffer, 0, bytes, "UTF-8");
                    Log.d("ConnectedThread", "Received new content: " + newContent);
                    writeToFile("bluetoothData.txt", newContent);
                    connectionStatusListener.onNewReceivedData(newContent);
                }
            } catch (IOException e) {
                Log.d("ConnectedThread", "Error occurred when reading input stream", e);
                break;
            }
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

    private void writeToFile(String fileName, String content) {
        // writes file to /storage/emulated/0/Android/data/si.uni_lj.fe.tnuv.bt_simple/files
        // physical phone internalStorage/Android/data/si.uni_lj.fe.tnuv.bt_simple/files
        File appSpecificExternalDir = connectionStatusListener.getContext().getExternalFilesDir(null);
        File file = new File(appSpecificExternalDir, fileName);

        /*
        File parentDir = file.getParentFile();
        String parentDirPath = parentDir.getAbsolutePath();
        Log.d("ConnectedThread", "File was written to: " + parentDirPath);
        */

        try (FileOutputStream fos = new FileOutputStream(file, true)){
            fos.write((content.replace("\n","") + "\n").getBytes());
        } catch (IOException e){
            Log.e("ConnectedThread", "Failed to write to file");
            e.printStackTrace();
        }

    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.e("ConnectedThread", "Error occurred when closing the socket", e);
        }
    }
}