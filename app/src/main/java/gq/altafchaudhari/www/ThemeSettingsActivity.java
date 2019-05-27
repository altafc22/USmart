package gq.altafchaudhari.www;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import es.dmoral.toasty.Toasty;

public class ThemeSettingsActivity extends AppCompatActivity {

    CircularImageView iv_app_logo,iv_app_bg;
    private static final int LOGO_GALLERY_INTENT = 1;
    private static final int BG_GALLERY_INTENT = 2;
    private static final int STORAGE_PERMISSION_CODE = 1000;
    Bitmap logo_bitmap = null;
    Bitmap bg_bitmap = null;

    String logo_path="",bg_path="";

    boolean isLogo = false;
    boolean isBg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_settings);
        iv_app_logo = findViewById(R.id.iv_app_logo);
        iv_app_bg = findViewById(R.id.iv_app_bg);

        getImagePath();
        if(!logo_path.equals(""))
            loadLogo(logo_path);
        if(!bg_path.equals(""))
            loadBackground(bg_path);

        iv_app_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                isLogo = true;
                isBg =false;
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,LOGO_GALLERY_INTENT);
            }
        });

        iv_app_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                isLogo= false;
                isBg = true;
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,BG_GALLERY_INTENT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LOGO_GALLERY_INTENT && resultCode==RESULT_OK && data!=null){
            Uri logoUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            if (isLogo) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            }
        }
        if(requestCode==BG_GALLERY_INTENT && resultCode==RESULT_OK && data!=null){
            Uri logoUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            if (isBg) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(3, 4)
                        .start(this);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    if(isLogo)
                    {
                       logo_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        iv_app_logo.setImageURI(null);
                        iv_app_logo.setImageURI(resultUri);
                    }
                    if (isBg)
                    {
                        bg_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        iv_app_bg.setImageURI(null);
                        iv_app_bg.setImageURI(resultUri);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Exception----------"+e);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                System.out.println("Error-------"+error);
            }
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
            ActivityCompat. requestPermissions(ThemeSettingsActivity.this,permissions,STORAGE_PERMISSION_CODE);
        }
        else if(!checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            ActivityCompat. requestPermissions(ThemeSettingsActivity.this,permissions,STORAGE_PERMISSION_CODE);
        }
    }

    private boolean checkPermission(String permission)
    {
        //String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = ThemeSettingsActivity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void changeLogo(View v){
        if(logo_bitmap!=null)
        {
            saveLogo(logo_bitmap);
        }
        else
        {
            Toasty.info(ThemeSettingsActivity.this,"Please Select Logo",Toasty.LENGTH_SHORT,true).show();
        }
    }

    public void changeBackground(View v){
        if(bg_bitmap!=null)
        {
            saveBackground(bg_bitmap);
        }
        else
        {
            Toasty.info(ThemeSettingsActivity.this,"Please Select Background",Toasty.LENGTH_SHORT,true).show();
        }
    }

    public void gotoPreviousActivity(View v)
    {
        finish();
    }

    private void saveLogo(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("theme_data", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"usmart_logo.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Toasty.success(ThemeSettingsActivity.this,"Logo Changed Successfully.",Toasty.LENGTH_SHORT,true).show();
            saveImageInSp("usmart_logo",mypath.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //return directory.getAbsolutePath();
    }

    private void saveBackground(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("theme_data", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"usmart_bg.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Toasty.success(ThemeSettingsActivity.this,"Background Changed Successfully.",Toasty.LENGTH_SHORT,true).show();
            saveImageInSp("usmart_background",mypath.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //return directory.getAbsolutePath();
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
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            iv_app_bg.setImageBitmap(myBitmap);
        };
    }

    private void saveImageInSp(String key,String path) {
        SharedPreferences.Editor editor;
        String sp_name = "usmart_sp";

        SharedPreferences sp = getApplicationContext().getSharedPreferences(sp_name, 0);
        editor = sp.edit();

        editor.putString(key,path);
        editor.commit();
        Log.d("ThemeActivity",key+" Saved");
    }

    private void getImagePath()
    {
        SharedPreferences sp = getApplicationContext().getSharedPreferences("usmart_sp", 0);
        String one,two,three,four,five,six,seven,eight;

        logo_path = sp.getString("usmart_logo","");
        bg_path = sp.getString("usmart_background","");
    }

}
