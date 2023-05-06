package si.uni_lj.fe.tnuv.bt_simple;

import android.content.Context;

public interface ConnectionStatusListener {
    Context getContext();
    void onConnectionSuccess();
    void onConnectionFailed();
    void onNewReceivedData(String message);
}
