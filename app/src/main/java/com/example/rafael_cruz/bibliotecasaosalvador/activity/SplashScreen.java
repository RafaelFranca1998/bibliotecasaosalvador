/*
 * Copyright (c) 2018. all rights are reserved to the authors of this project,
 * unauthorized use of this code in other projects may result in legal complications.
 */

package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.rafael_cruz.bibliotecasaosalvador.R;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    int cont;
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (ContextCompat.checkSelfPermission(SplashScreen.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(SplashScreen.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            checkAllPermissions();
        } else {
            loadMain();
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void checkAllPermissions() {
        cont = 0;
        for (String permission : PERMISSIONS) {
            cont++;
            int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;

            if (ActivityCompat.checkSelfPermission
                    (SplashScreen.this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SplashScreen.this,PERMISSIONS , PERMISSION_GRANTED);
            }else{
                ActivityCompat.requestPermissions(SplashScreen.this, PERMISSIONS, PERMISSION_GRANTED);
            }
            if (cont == PERMISSIONS.length) loadMain();
        }
    }
    private void loadMain(){
        boolean t = true;
        while (t) {
            if (ContextCompat.checkSelfPermission(SplashScreen.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED||
                    ContextCompat.checkSelfPermission(SplashScreen.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                t = false;
                new Handler().postDelayed(() -> {
                    Intent i = new Intent(SplashScreen.this, TransitionActivity.class);
                    startActivity(i);
                    finish();
                }, SPLASH_TIME_OUT);
            }
        }
    }
}
