package gq.altafchaudhari.www;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import es.dmoral.toasty.Toasty;
import vn.luongvo.widget.iosswitchview.SwitchView;

public class MainActivity extends AppCompatActivity {

    SwitchView switchView;
    String btDeviceAddress = null;
    String btDeviceName = null;

    String message ="";
    TextView tv_connetivity_status;

    private boolean isBtConnected = false;
    static UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    MyApplication myApplication;

    RadioGroup radioGroup1, radioGroup2, radioGroup3, radioGroup4;
    BluetoothAdapter bluetoothadapter = null;
    private boolean isChecking = true;

    RadioButton toggle_button_one, toggle_button_two, toggle_button_three, toggle_button_four,
            toggle_button_five, toggle_button_six, toggle_button_seven, toggle_button_eight;
    int mCheckedId;

    @Override
    protected void onDestroy() {
        Log.d("onDestroy", "called");
        super.onDestroy();
        unregisterReceiver(btStateReceiver);
        //unregisterReceiver(btDiscoverReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*SharedPreferences sp = getApplicationContext().getSharedPreferences("usmart_sp", 0);
        btDeviceAddress = sp.getString("device_address", null);
        btDeviceName = sp.getString("device_name", null);*/

        System.out.println(btDeviceName+" : "+btDeviceAddress);

        initializeaAll();

        turnOnBt();
        changeStatusBarColor();

        switchView.setOnCheckedChangeListener(new SwitchView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchView switchView, boolean isChecked) {

                    if(isChecked) {
                        sendDataToDevice("y");
                        checkAllButtons();
                    }
                    else{
                        sendDataToDevice("x");
                        clearCheck(1234);
                    }
            }
        });

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    clearCheck(234);
                    radioGroup1.check(checkedId);
                    mCheckedId = checkedId;
                    if(toggle_button_one.isChecked())
                    {
                        System.out.println("---A");
                        sendDataToDevice("a");
                    }
                    if(toggle_button_two.isChecked()) {
                        System.out.println("---B");
                        sendDataToDevice("b");
                    }
                }
                isChecking = true;
            }
        });

        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    clearCheck(134);
                    radioGroup2.check(checkedId);
                    mCheckedId = checkedId;
                    if(toggle_button_three.isChecked())
                    {
                        System.out.println("---C");
                        sendDataToDevice("c");
                    }
                    if(toggle_button_four.isChecked())
                    {
                        System.out.println("---D");
                        sendDataToDevice("d");
                    }
                }
                isChecking = true;
            }
        });

        radioGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    clearCheck(124);
                    radioGroup3.check(checkedId);
                    mCheckedId = checkedId;
                    if(toggle_button_five.isChecked()){
                        System.out.println("---E");
                        sendDataToDevice("e");
                    }
                    if(toggle_button_six.isChecked()){
                        System.out.println("---F");
                        sendDataToDevice("f");
                    }
                }
                isChecking = true;

            }
        });

        radioGroup4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                        isChecking = false;
                        clearCheck(123);
                        radioGroup4.check(checkedId);
                        mCheckedId = checkedId;
                        if (toggle_button_seven.isChecked()) {
                            System.out.println("---G");
                            sendDataToDevice("g");
                        }
                        if (toggle_button_eight.isChecked()) {
                            System.out.println("---H");
                            sendDataToDevice("h");
                        }
                    }
                isChecking = true;
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getApplicationContext().getSharedPreferences("usmart_sp", 0);
        //btDeviceAddress = sp.getString("device_address",null);
        //btDeviceName = sp.getString("device_name",null);
        btDeviceName = myApplication.deviceName;
        btDeviceAddress = myApplication.deviceAddress;
        System.out.println(btDeviceName+" : "+btDeviceAddress);
        if(!btDeviceName.equals(""))
            tv_connetivity_status.setText(btDeviceName+" Connected");
    }

    private void initializeaAll(){

        myApplication = (MyApplication)getApplication();

        bluetoothadapter = BluetoothAdapter.getDefaultAdapter();

        radioGroup1 = findViewById(R.id.radioGroup1);
        radioGroup2 = findViewById(R.id.radioGroup2);
        radioGroup3 = findViewById(R.id.radioGroup3);
        radioGroup4 = findViewById(R.id.radioGroup4);

        toggle_button_one = findViewById(R.id.toggle_button_one);
        toggle_button_two = findViewById(R.id.toggle_button_two);
        toggle_button_three = findViewById(R.id.toggle_button_three);
        toggle_button_four = findViewById(R.id.toggle_button_four);
        toggle_button_five  = findViewById(R.id.toggle_button_five);
        toggle_button_six = findViewById(R.id.toggle_button_six);
        toggle_button_seven = findViewById(R.id.toggle_button_seven);
        toggle_button_eight = findViewById(R.id.toggle_button_eight);

        tv_connetivity_status = findViewById(R.id.tv_connetivity_status);

        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(btStateReceiver, filter3);

        //makeDeviceDiscoverable();

        switchView = findViewById(R.id.switchView);
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

    private void showDilogueBox() {
        new iOSDialogBuilder(MainActivity.this)
                .setTitle("Connect Device")
                .setSubtitle("Please connect to device.")
                .setBoldPositiveLabel(true)
                .setCancelable(false)
                .setPositiveListener("OK",new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        startActivity(new Intent(MainActivity.this,DevicesActivity.class));
                        dialog.dismiss();
                    }
                })
                .setNegativeListener("Cancel", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build().show();
    }

    private void showToast(String s) {
        Toasty.info(getApplicationContext(),s,Toasty.LENGTH_SHORT,true).show();
    }

    public void makeDeviceDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,10);
        startActivity(discoverableIntent);

        IntentFilter filter1 = new IntentFilter(bluetoothadapter.ACTION_STATE_CHANGED);
        registerReceiver(btDiscoverReceiver, filter1);

    }

    public void goToSettings(View v){
        startActivity(new Intent(MainActivity.this,Settings.class));
    }

    /*public void goToPairedDevices(View v){
        startActivity(new Intent(MainActivity.this,DevicesActivity.class));
    }
*/
    private void clearCheck(int num) {
        if(radioGroup1.getCheckedRadioButtonId() != -1)
            radioGroup1.clearCheck();
        if(num==234)
        {
            radioGroup2.clearCheck();
            radioGroup3.clearCheck();
            radioGroup4.clearCheck();
        }
        if(num==134)
        {
            radioGroup1.clearCheck();
            radioGroup3.clearCheck();
            radioGroup4.clearCheck();
        }
        if(num==124)
        {
            radioGroup1.clearCheck();
            radioGroup2.clearCheck();
            radioGroup4.clearCheck();
        }
        if(num==123)
        {
            radioGroup1.clearCheck();
            radioGroup2.clearCheck();
            radioGroup3.clearCheck();
        }

    }

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

    private final BroadcastReceiver btDiscoverReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode =intent.getIntExtra(bluetoothadapter.EXTRA_SCAN_MODE,bluetoothadapter.ERROR);
                switch(mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d("Bluetooth","discoverability enabled");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d("Bluetooth","discoverability enabled, able to receive connection");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d("Bluetooth","discoverability disbled");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d("Bluetooth","Connecting");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d("Bluetooth","Connected");
                        break;
                }
            }
        }
    };

    private void sendDataToDevice(String data)
    {
        if(myApplication.deviceAddress.equals(""))
        {
            showToast("Please Connect to Device");
            startActivity(new Intent(MainActivity.this,DevicesActivity.class));
            switchView.setChecked(false);
        }
        else
        {

            if (myApplication.bluetoothSocket!=null)
            {
                try
                {
                    System.out.println("Trying to send data");
                    myApplication.bluetoothSocket.getOutputStream().write(data.getBytes());
                }
                catch (IOException e)
                {
                    new ConnectBT().execute();
                    System.out.println("Trying to send data");
                    try {
                        myApplication.bluetoothSocket.getOutputStream().write(data.getBytes());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    //showToast("Error while sending data: "+e);
                }
            }
        }
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
                if(!btDeviceName.equals(""))
                    tv_connetivity_status.setText(btDeviceName+" Connected");
                //showToast(myApplication+" Connected.");
                isBtConnected = true;
            }
            //progress.dismiss();
        }
    }

    private void checkAllButtons()
    {

    }

    private void uncheckAllButtons()
    {

    }

}
