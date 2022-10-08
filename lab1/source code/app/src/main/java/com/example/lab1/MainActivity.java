package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ContentResolver mContentResolver = null;
    private Cursor cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.button1:
                    Intent startIntent = new Intent(this, MyClass2.class);
                    startService(startIntent);
                    break;
                case R.id.button2:
                    Intent stopIntent = new Intent(this, MyClass2.class);
                    stopService(stopIntent);
                    break;
                case R.id.button3:
                    Intent custIntent = new Intent();
                    custIntent.setAction("com.exmaple.CUSTOM_INTENT");
                    custIntent.setPackage("com.example.myapplication");
                    sendBroadcast(custIntent);

            }
        }
    }

}