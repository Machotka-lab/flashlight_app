package com.example.flashlight;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    Switch sw;
    CheckBox cb;
    boolean isFlashAvailable;
    private CameraManager mCameraManager;
    private String mCameraId;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.toggle);

        checkCamera();

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        cb = (CheckBox) findViewById(R.id.flashlightAvailable);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    isFlashAvailable = true;
                else
                    isFlashAvailable = false;
            }
        });
        cb.setChecked(isFlashAvailable);
        sw = (Switch) findViewById(R.id.switchFlashlight);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!isFlashAvailable) {
                        showNoFlashError();
                        sw.setChecked(false);
                    }
                    else try {
                        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) !=
                                PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA},
                                    100); }
                        mCameraManager.setTorchMode(mCameraId, true);
                        if (mp.isPlaying())
                        {
                            mp.pause();
                            mp.seekTo(0);
                        }
                        mp.start();
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        mCameraManager.setTorchMode(mCameraId, false);
                        if (mp.isPlaying())
                        {
                            mp.pause();
                            mp.seekTo(0);
                        }
                        mp.start();
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    void checkCamera()
    {
        isFlashAvailable = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!isFlashAvailable) {
            showNoFlashError();
        }
    }

    void showNoFlashError()
    {
        AlertDialog alert = new AlertDialog.Builder(this)
                .create();
        alert.setTitle("Oops!");
        alert.setMessage("Flash not available in this device...");
        alert.show();
    }


}