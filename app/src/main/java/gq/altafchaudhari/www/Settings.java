package gq.altafchaudhari.www;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import es.dmoral.toasty.Toasty;

public class Settings extends AppCompatActivity {
    BluetoothAdapter bluetoothadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void gotoPreviousActivity(View v){
        finish();
    }

    public void connectBluetooth(View v)
    {
        turnOnBt();
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
}

