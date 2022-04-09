package com.example.fotoframe;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AllowPermission();

        int time = 2000;
        SharedPreferences sharedPreferences = getSharedPreferences(
                "cun", SplashActivity.MODE_PRIVATE);
        final String expirationDate = sharedPreferences.getString("expirationDate", "");
        final String token = sharedPreferences.getString("token","");
        final String phoneNumber = sharedPreferences.getString("phoneNumber", "");
        final String masterDevice = sharedPreferences.getString("masterDevice","");
        Log.d("dddd启动页","masterDevice = "+masterDevice);
        final long current = System.currentTimeMillis();
        Log.d("dddd","current - "+current);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!expirationDate.isEmpty()){
                    final long comparetime = Long.parseLong(expirationDate);
                    Log.d("dddd", "current = " + current + " comparetime = " + comparetime);
                    if (current > comparetime) {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        SplashActivity.this.finish();
                    } else {
                        Intent it = new Intent(SplashActivity.this, MainActivity.class);
                        it.putExtra("token", token);
                        it.putExtra("userSign", phoneNumber);
                        it.putExtra("masterDevice",masterDevice);
                        startActivity(it);
                        SplashActivity.this.finish();
                    }
                }else{
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    SplashActivity.this.finish();
                }
            }
        }, time);
    }

    private void AllowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int writePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }

            int readPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return;
            }

            int cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                return;
            }
        }
    }
}
