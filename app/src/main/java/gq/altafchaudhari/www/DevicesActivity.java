package gq.altafchaudhari.www;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class DevicesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<BluetoothDevice> bluetoothDevices;
    public DeviceListAdapter deviceListAdapter;
    ListView lvNewDevices;
    BluetoothAdapter bluetoothadapter;
    private ProgressDialog progress;

    MyApplication myApplication;

    private boolean isBtConnected = false;
    static UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BroadcastReceiver btBondStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState()==BluetoothDevice.BOND_BONDED){
                    Log.d("Bond State Receiver","BOND_BONDED");
                    Toasty.success(DevicesActivity.this," Connected",Toasty.LENGTH_SHORT,true).show();
                    progress.hide();
                    progress.dismiss();
                }
                if(device.getBondState()==BluetoothDevice.BOND_BONDING){
                    progress=new ProgressDialog(DevicesActivity.this);
                    progress.setMessage("Connecting, Please Wait");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(true);
                    progress.show();
                    Log.d("Bond State Receiver","BOND_BONDING");
                }
                if(device.getBondState()==BluetoothDevice.BOND_NONE){
                    Log.d("Bond State Receiver","BOND_NONE");
                }
            }
        }
    };

    private final BroadcastReceiver btStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(bluetoothadapter.ACTION_STATE_CHANGED))
            {
                final int state = intent.getIntExtra(bluetoothadapter.EXTRA_STATE,bluetoothadapter.ERROR);
                switch(state)
                {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d("Bluetooth","turned OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d("Bluetooth","turning OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d("Bluetooth","turning ON");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d("Bluetooth","turned ON");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                    {

                        Log.d("Bluetooth","turned ON");
                        break;
                    }
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        bluetoothadapter = BluetoothAdapter.getDefaultAdapter();

        lvNewDevices = findViewById(R.id.lvNewDevices);
        bluetoothDevices = new ArrayList<>();

        myApplication =(MyApplication)getApplication();

        IntentFilter filter = new IntentFilter(bluetoothadapter.ACTION_STATE_CHANGED);
        registerReceiver(btBondStateReceiver, filter);

        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(btStateReceiver, filter3);

        lvNewDevices.setOnItemClickListener(this);

        loadPairedDevices();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadPairedDevices();
    }

    private void loadPairedDevices() {
        bluetoothDevices.clear();
        //turnOnBt();
        Set<BluetoothDevice> devices = bluetoothadapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            bluetoothDevices.add(device);
        }
        deviceListAdapter = new DeviceListAdapter(DevicesActivity.this,R.layout.paired_device_adapter_view,bluetoothDevices);
        deviceListAdapter.notifyDataSetChanged();
        lvNewDevices.setAdapter(deviceListAdapter);
    }

    public void gotoPreviousActivity(View v)
    {
        finish();
    }

    public void discoverDevices(View v){
        startActivity(new Intent(DevicesActivity.this,SearchDevices.class));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        bluetoothadapter.cancelDiscovery();
        Log.d("DeviceActivity","Item Clicked");
        String deviceName = bluetoothDevices.get(position).getName();
        String deviceAddress = bluetoothDevices.get(position).getAddress();
        Log.d("DeviceActivity","Name: "+deviceName);
        Log.d("DeviceActivity","Address: "+deviceAddress);

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d("DeviceActivity","Trying to pair with "+deviceName);
            bluetoothDevices.get(position).createBond();
            BluetoothDevice device = bluetoothDevices.get(position);

        }
        if (bluetoothDevices.get(position).getBondState()==BluetoothDevice.BOND_BONDED)
        {
            //Toasty.success(DevicesActivity.this,deviceName+" Connected Successfully",Toasty.LENGTH_SHORT,true).show();
            SharedPreferences.Editor editor;
            String sp_name = "usmart_sp";
            SharedPreferences sp = getApplicationContext().getSharedPreferences(sp_name, 0);
            editor = sp.edit();

            myApplication.deviceName = deviceName;
            myApplication.deviceAddress = deviceAddress;

            editor.putString("device_address", deviceAddress);
            editor.putString("device_name", deviceName);
            editor.commit();
            Log.d("DeviceActivity","SP Saved");
            new ConnectBT().execute();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(btBondStateReceiver);
        unregisterReceiver(btStateReceiver);
    }


    private void showToast(String s) {
        Toasty.info(getApplicationContext(),s,Toasty.LENGTH_SHORT,true).show();
    }


    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            // progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
            System.out.println("Conntecting");
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (myApplication.bluetoothSocket == null || !isBtConnected)
                {
                    bluetoothadapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice device = bluetoothadapter.getRemoteDevice(myApplication.deviceAddress);//connects to the device's address and checks if it's available
                    myApplication.bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    myApplication.bluetoothSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                System.out.println("MyException:"+ e);
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);
            if (!ConnectSuccess)
            {
                showToast("Connection Failed. Is it a SPP Bluetooth? Try again.");
                //finish();
            }
            else
            {
                showToast(myApplication.deviceName+" Connected.");
                isBtConnected = true;
            }
            //progress.dismiss();
        }
    }


}
