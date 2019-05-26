package gq.altafchaudhari.www;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class SearchDevices extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<BluetoothDevice> bluetoothDevices;
    public DeviceListAdapter deviceListAdapter;
    ListView lvNewDevices;
    BluetoothAdapter bluetoothadapter;
    private ProgressDialog progress;

    private final BroadcastReceiver btBondStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState()==BluetoothDevice.BOND_BONDED){
                    Log.d("Bond State Receiver","BOND_BONDED");
                    showToast(device.getName()+" Paired");
                    progress.dismiss();
                    finish();
                }
                if(device.getBondState()==BluetoothDevice.BOND_BONDING){
                    progress=new ProgressDialog(SearchDevices.this);
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
    private BroadcastReceiver descoverDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action  = intent.getAction();
            Log.d("DiscoverReceiver ", "ACTION FOUND");
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                progress.hide();
                progress.dismiss();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevices.add(device);
                Log.d("DiscoverReceiver ", device.getName()+":"+device.getAddress());
                deviceListAdapter = new DeviceListAdapter(context,R.layout.device_adapter_view,bluetoothDevices);
                deviceListAdapter.notifyDataSetChanged();
                lvNewDevices.setAdapter(deviceListAdapter);
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
        setContentView(R.layout.activity_search_devices);
        changeStatusBarColor();
        bluetoothadapter = BluetoothAdapter.getDefaultAdapter();

        lvNewDevices = findViewById(R.id.lvNewDevices);
        bluetoothDevices = new ArrayList<>();

        IntentFilter filter = new IntentFilter(bluetoothadapter.ACTION_STATE_CHANGED);
        registerReceiver(btBondStateReceiver, filter);

        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(btStateReceiver, filter3);

        lvNewDevices.setOnItemClickListener(this);

        discoverNewDevices();

    }

    private void turnOnBt() {
        if(bluetoothadapter == null)
        {
            showToast("Bluetooth Not Supported");
        }
        else{
            if(!bluetoothadapter.isEnabled()){
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),1);
                //showToast("Bluetooth Turned ON");
                //switchView.setChecked(true);
            }
        }
    }

    public void gotoPreviousActivity(View v)
    {
        finish();
    }


    private void checkBtPermissions()
    {
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck =this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if(permissionCheck!=0){
                this.requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},1001);
            }
            else
            {
                Log.d("Bluetooth","No need to Check Permission < 5.0");
            }
        }
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
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(descoverDeviceReceiver);
        unregisterReceiver(btBondStateReceiver);
    }

    private void showToast(String s) {
        Toasty.info(getApplicationContext(),s,Toasty.LENGTH_SHORT,true).show();
    }


    private void discoverNewDevices()
    {
        turnOnBt();
        bluetoothDevices.clear();
        progress=new ProgressDialog(this);
        progress.setMessage("Searching for New Device");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        Log.d("Bluetooth","Looking for New Device");
        if(bluetoothadapter.isDiscovering())
        {
            bluetoothadapter.cancelDiscovery();
            Log.d("Bluetooth","Discovery Canceled");

            checkBtPermissions();

            bluetoothadapter.startDiscovery();
            IntentFilter discoverDeviceIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(descoverDeviceReceiver,discoverDeviceIntent);
        }
        if(!bluetoothadapter.isDiscovering()){
            checkBtPermissions();

            bluetoothadapter.startDiscovery();
            IntentFilter discoverDeviceIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(descoverDeviceReceiver,discoverDeviceIntent);
        }
    }

    private void changeStatusBarColor() {
        //change notification icon color...
        View yourView = findViewById(R.id.main_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (yourView != null) {
                yourView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        // making notification bar transparent

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

}
