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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import net.colindodd.toggleimagebutton.ToggleImageButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import es.dmoral.toasty.Toasty;
import vn.luongvo.widget.iosswitchview.SwitchView;

public class MainActivity extends AppCompatActivity {

    SwitchView switchView;
    String btDeviceAddress = null;
    String btDeviceName = null;

    String logo_path="",bg_path="";

    CircularImageView iv_app_logo;
    ImageView iv_app_bg;

    RelativeLayout container;


    TextView tv_connetivity_status;
    boolean isMasterSwitch = false;

    private boolean isBtConnected = false;
    static UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    MyApplication myApplication;

    BluetoothAdapter bluetoothadapter = null;

    ToggleImageButton toggle_button_one, toggle_button_two, toggle_button_three, toggle_button_four,
            toggle_button_five, toggle_button_six, toggle_button_seven, toggle_button_eight;

    TextView tv_btn_one,tv_btn_two,tv_btn_three,tv_btn_four,tv_btn_five,tv_btn_six,tv_btn_seven,tv_btn_eight;

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

        //SharedPreferences sp = getApplicationContext().getSharedPreferences("usmart_sp", 0);
        System.out.println(btDeviceName+" : "+btDeviceAddress);


        initializeaAll();
        loadButtonText();

        getImagePath();
        if(!logo_path.equals(""))
            loadLogo(logo_path);
        if(!bg_path.equals(""))
            loadBackground(bg_path);


        turnOnBt();
        changeStatusBarColor();

        switchView.setOnCheckedChangeListener(new SwitchView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchView switchView, boolean isChecked) {

                    if(isChecked) {
                        isMasterSwitch = true;
                        toggle_button_one.setChecked(true);
                        toggle_button_two.setChecked(true);
                        toggle_button_three.setChecked(true);
                        toggle_button_four.setChecked(true);
                        toggle_button_five.setChecked(true);
                        toggle_button_six.setChecked(true);
                        toggle_button_seven.setChecked(true);
                        toggle_button_eight.setChecked(true);
                        sendDataToDevice("y");
                    }
                    else{
                        isMasterSwitch =false;
                        toggle_button_one.setChecked(false);
                        toggle_button_two.setChecked(false);
                        toggle_button_three.setChecked(false);
                        toggle_button_four.setChecked(false);
                        toggle_button_five.setChecked(false);
                        toggle_button_six.setChecked(false);
                        toggle_button_seven.setChecked(false);
                        toggle_button_eight.setChecked(false);
                        sendDataToDevice("x");
                    }
            }
        });


        toggle_button_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggle_button_one.isChecked() && !isMasterSwitch)
                {
                    toggle_button_two.setChecked(false);
                    toggle_button_three.setChecked(false);
                    toggle_button_four.setChecked(false);
                    toggle_button_five.setChecked(false);
                    toggle_button_six.setChecked(false);
                    toggle_button_seven.setChecked(false);
                    toggle_button_eight.setChecked(false);
                    sendDataToDevice("a1");
                }
                if(!toggle_button_one.isChecked() && !isMasterSwitch)
                {
                    sendDataToDevice("a0");
                }
            }
        });

        toggle_button_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggle_button_two.isChecked() && !isMasterSwitch)
                {
                    toggle_button_one.setChecked(false);
                    toggle_button_three.setChecked(false);
                    toggle_button_four.setChecked(false);
                    toggle_button_five.setChecked(false);
                    toggle_button_six.setChecked(false);
                    toggle_button_seven.setChecked(false);
                    toggle_button_eight.setChecked(false);
                    sendDataToDevice("b1");
                }
                if(!toggle_button_two.isChecked() && !isMasterSwitch)
                {
                    sendDataToDevice("b0");
                }
            }
        });
        toggle_button_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggle_button_three.isChecked() && !isMasterSwitch)
                {
                    toggle_button_two.setChecked(false);
                    toggle_button_one.setChecked(false);
                    toggle_button_four.setChecked(false);
                    toggle_button_five.setChecked(false);
                    toggle_button_six.setChecked(false);
                    toggle_button_seven.setChecked(false);
                    toggle_button_eight.setChecked(false);
                    sendDataToDevice("c1");

                }
                if(!toggle_button_three.isChecked() && !isMasterSwitch)
                {
                    sendDataToDevice("c0");
                }
            }
        });

        toggle_button_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggle_button_four.isChecked() && !isMasterSwitch)
                {
                    toggle_button_two.setChecked(false);
                    toggle_button_three.setChecked(false);
                    toggle_button_one.setChecked(false);
                    toggle_button_five.setChecked(false);
                    toggle_button_six.setChecked(false);
                    toggle_button_seven.setChecked(false);
                    toggle_button_eight.setChecked(false);
                    sendDataToDevice("d1");
                }
                if(!toggle_button_four.isChecked() && !isMasterSwitch)
                {
                    sendDataToDevice("d0");
                }
            }
        });

        toggle_button_five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggle_button_five.isChecked() && !isMasterSwitch)
                {
                    toggle_button_two.setChecked(false);
                    toggle_button_three.setChecked(false);
                    toggle_button_four.setChecked(false);
                    toggle_button_one.setChecked(false);
                    toggle_button_six.setChecked(false);
                    toggle_button_seven.setChecked(false);
                    toggle_button_eight.setChecked(false);
                    sendDataToDevice("e1");
                }
                if(!toggle_button_five.isChecked() && !isMasterSwitch)
                {
                    sendDataToDevice("e0");
                }
            }
        });


        toggle_button_six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggle_button_six.isChecked() && !isMasterSwitch)
                {
                    toggle_button_two.setChecked(false);
                    toggle_button_three.setChecked(false);
                    toggle_button_four.setChecked(false);
                    toggle_button_five.setChecked(false);
                    toggle_button_one.setChecked(false);
                    toggle_button_seven.setChecked(false);
                    toggle_button_eight.setChecked(false);
                    sendDataToDevice("f1");
                }
                if(!toggle_button_six.isChecked() && !isMasterSwitch)
                {
                    sendDataToDevice("f0");
                }
            }
        });

        toggle_button_seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggle_button_seven.isChecked() && !isMasterSwitch)
                {
                    toggle_button_two.setChecked(false);
                    toggle_button_three.setChecked(false);
                    toggle_button_four.setChecked(false);
                    toggle_button_five.setChecked(false);
                    toggle_button_six.setChecked(false);
                    toggle_button_one.setChecked(false);
                    toggle_button_eight.setChecked(false);
                    sendDataToDevice("g1");
                }
                if(!toggle_button_seven.isChecked() && !isMasterSwitch)
                {
                    sendDataToDevice("g0");
                }
            }
        });

        toggle_button_eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggle_button_eight.isChecked() && !isMasterSwitch)
                {
                    toggle_button_two.setChecked(false);
                    toggle_button_three.setChecked(false);
                    toggle_button_four.setChecked(false);
                    toggle_button_five.setChecked(false);
                    toggle_button_six.setChecked(false);
                    toggle_button_seven.setChecked(false);
                    toggle_button_one.setChecked(false);
                    sendDataToDevice("h1");
                }
                if(!toggle_button_eight.isChecked() && !isMasterSwitch)
                {
                    sendDataToDevice("h0");
                }
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        //SharedPreferences sp = getApplicationContext().getSharedPreferences("usmart_sp", 0);
        //btDeviceAddress = sp.getString("device_address",null);
        //btDeviceName = sp.getString("device_name",null);

        getImagePath();
        if(!logo_path.equals(""))
            loadLogo(logo_path);
        if(!bg_path.equals(""))
            loadBackground(bg_path);


        btDeviceName = myApplication.deviceName;
        btDeviceAddress = myApplication.deviceAddress;
        System.out.println(btDeviceName+" : "+btDeviceAddress);
        if(!btDeviceName.equals(""))
            tv_connetivity_status.setText(btDeviceName+" Connected");

        loadButtonText();
    }

    private void initializeaAll(){

        myApplication = (MyApplication)getApplication();

        bluetoothadapter = BluetoothAdapter.getDefaultAdapter();

        toggle_button_one = findViewById(R.id.toggle_button_one);
        toggle_button_two = findViewById(R.id.toggle_button_two);
        toggle_button_three = findViewById(R.id.toggle_button_three);
        toggle_button_four = findViewById(R.id.toggle_button_four);
        toggle_button_five  = findViewById(R.id.toggle_button_five);
        toggle_button_six = findViewById(R.id.toggle_button_six);
        toggle_button_seven = findViewById(R.id.toggle_button_seven);
        toggle_button_eight = findViewById(R.id.toggle_button_eight);


        tv_btn_one = findViewById(R.id.tv_btn_one);
        tv_btn_two = findViewById(R.id.tv_btn_two);
        tv_btn_three = findViewById(R.id.tv_btn_three);
        tv_btn_four = findViewById(R.id.tv_btn_four);
        tv_btn_five = findViewById(R.id.tv_btn_five);
        tv_btn_six = findViewById(R.id.tv_btn_six);
        tv_btn_seven = findViewById(R.id.tv_btn_seven);
        tv_btn_eight = findViewById(R.id.tv_btn_eight);

        iv_app_bg = findViewById(R.id.iv_app_bg);
        iv_app_logo = findViewById(R.id.iv_logo);

        container = findViewById(R.id.container);

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

    private void showToast(String s) {
        Toasty.info(getApplicationContext(),s,Toasty.LENGTH_SHORT,true).show();
    }

    public void goToSettings(View v){
        startActivity(new Intent(MainActivity.this,Settings.class));
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

    private void sendDataToDevice(String data)
    {
        if(myApplication.deviceAddress.equals(""))
        {
            showToast("Please Connect to Device");
            startActivity(new Intent(MainActivity.this,DevicesActivity.class));
            switchView.setChecked(false);
            toggle_button_one.setChecked(false);
            toggle_button_two.setChecked(false);
            toggle_button_three.setChecked(false);
            toggle_button_four.setChecked(false);
            toggle_button_five.setChecked(false);
            toggle_button_six.setChecked(false);
            toggle_button_seven.setChecked(false);
            toggle_button_eight.setChecked(false);
        }
        else
        {
            if (myApplication.bluetoothSocket!=null)
            {
                try
                {
                    System.out.println("Trying to send data");
                    System.out.println("-- "+data);
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

    private void loadButtonText()
    {
        SharedPreferences sp = getApplicationContext().getSharedPreferences("usmart_sp", 0);
        String one,two,three,four,five,six,seven,eight;

        one = sp.getString("button_one","Button 1");
        two = sp.getString("button_two","Button 2");
        three = sp.getString("button_three","Button 3");
        four = sp.getString("button_four","Button 4");
        five = sp.getString("button_five","Button 5");
        six = sp.getString("button_six","Button 6");
        seven = sp.getString("button_seven","Button 7");
        eight = sp.getString("button_eight","Button 8");

        tv_btn_one.setText(one);
        tv_btn_two.setText(two);
        tv_btn_three.setText(three);
        tv_btn_four.setText(four);
        tv_btn_five.setText(five);
        tv_btn_six.setText(six);
        tv_btn_seven.setText(seven);
        tv_btn_eight.setText(eight);
    }

    private void getImagePath()
    {
        SharedPreferences sp = getApplicationContext().getSharedPreferences("usmart_sp", 0);
        logo_path = sp.getString("usmart_logo","");
        bg_path = sp.getString("usmart_background","");

        System.out.println("PATH "+logo_path);
        System.out.println("PATH "+bg_path);
    }

    private void loadLogo(String path) {
        File imgFile = new  File(path);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            iv_app_logo.setImageBitmap(myBitmap);
        };
    }

    private void loadBackground(String path) {
        File imgFile = new  File(path);
        System.out.println("BGG PAAAAATHHHHH "+path);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            iv_app_bg.setImageBitmap(myBitmap);
            Drawable dr = new BitmapDrawable(myBitmap);
            container.setBackgroundDrawable(null);
            container.setBackgroundDrawable(dr);
        };
    }






}
