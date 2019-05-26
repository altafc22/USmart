package gq.altafchaudhari.www;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class MyApplication extends Application {

    public BluetoothSocket bluetoothSocket = null;
    public BluetoothDevice bluetoothDevice = null;
    public String deviceName = "";
    public String deviceAddress = "";

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

}