package gq.altafchaudhari.www;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import es.dmoral.toasty.Toasty;

public class Settings extends AppCompatActivity {
    BluetoothAdapter bluetoothadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        changeStatusBarColor();

        bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void gotoPreviousActivity(View v){
        finish();
    }

    public void connectBluetooth(View v)
    {
        turnOnBt();
        startActivity(new Intent(Settings.this,DevicesActivity.class));
    }

    public void buttonConfiguration(View v)
    {
        startActivity(new Intent(Settings.this,ButtonConfiguration.class));
    }

    public void gotoThemes(View v)
    {
        startActivity(new Intent(Settings.this,ThemeSettingsActivity.class));
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
            else{
                showToast("Bluetooth is already on");
            }
        }
    }

    private void showToast(String s) {
        Toasty.info(getApplicationContext(),s,Toasty.LENGTH_SHORT,true).show();
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

