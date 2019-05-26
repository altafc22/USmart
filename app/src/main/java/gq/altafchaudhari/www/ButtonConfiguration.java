package gq.altafchaudhari.www;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import es.dmoral.toasty.Toasty;

public class ButtonConfiguration extends AppCompatActivity {

    EditText et_one,et_two,et_three,et_four,et_five,et_six,et_seven,et_eight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_configuration);

        et_one = findViewById(R.id.et_one);
        et_two = findViewById(R.id.et_two);
        et_three = findViewById(R.id.et_three);
        et_four = findViewById(R.id.et_four);
        et_five = findViewById(R.id.et_five);
        et_six = findViewById(R.id.et_six);
        et_seven = findViewById(R.id.et_seven);
        et_eight = findViewById(R.id.et_eight);

        changeStatusBarColor();
        loadButtonText();

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

    public void goToPreviousActivity(View v){
        finish();
    }

    public void saveButtonConfig(View v){

        if(et_one.length()<1)
            et_one.setError("Enter Text");
        else if(et_two.length()<1)
            et_two.setError("Enter Text");
        else if(et_three.length()<1)
            et_three.setError("Enter Text");
        else if(et_four.length()<1)
            et_four.setError("Enter Text");
        else if(et_five.length()<1)
            et_five.setError("Enter Text");
        else if(et_six.length()<1)
            et_six.setError("Enter Text");
        else if(et_seven.length()<1)
            et_seven.setError("Enter Text");
        else  if(et_eight.length()<1)
            et_eight.setError("Enter Text");
        else {
            SharedPreferences.Editor editor;
            String sp_name = "usmart_sp";

            SharedPreferences sp = getApplicationContext().getSharedPreferences(sp_name, 0);
            editor = sp.edit();

            editor.putString("button_one", et_one.getText().toString());
            editor.putString("button_two", et_two.getText().toString());
            editor.putString("button_three", et_three.getText().toString());
            editor.putString("button_four", et_four.getText().toString());
            editor.putString("button_six", et_six.getText().toString());
            editor.putString("button_seven", et_seven.getText().toString());
            editor.putString("button_eight", et_eight.getText().toString());
            editor.commit();
            Log.d("DeviceActivity","SP Saved");
            Toasty.success(ButtonConfiguration.this,"Setting Saved Succesfully.",Toasty.LENGTH_SHORT,true).show();
            finish();
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

        System.out.println(one+"-"+two+"-"+three+"-"+four);

        et_one.setText(one);
        et_two.setText(two);
        et_three.setText(three);
        et_four.setText(four);
        et_five.setText(five);
        et_six.setText(six);
        et_seven.setText(seven);
        et_eight.setText(eight);
    }

    public void clearAllText(View v){
        et_one.setText("");
        et_two.setText("");
        et_three.setText("");
        et_four.setText("");
        et_five.setText("");
        et_six.setText("");
        et_seven.setText("");
        et_eight.setText("");
        et_one.requestFocus();
    }
}
