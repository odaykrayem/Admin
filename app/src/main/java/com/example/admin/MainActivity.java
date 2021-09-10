package com.example.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.admin.Models.OperationModel;
import com.example.admin.utils.ScheduledService;
import com.example.admin.utils.SharedPrefs;
import com.example.admin.utils.SimCardutil;
import com.example.admin.utils.USSDService;

public class MainActivity extends AppCompatActivity {
    public static final String MESSAGE_STATUS = "message_status";
    Button btnStart, btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.startServiceBtn);
        btnStop = findViewById(R.id.stopServiceBtn);

        final int PERMISSION_ALL = 1;
        final String[] PERMISSIONS = {
                android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.INTERNET
        };
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// use this to start and trigger a service
                if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_ALL);
                }else{
                    SharedPrefs.save(getApplicationContext(), SharedPrefs.KEY_OPERATION_IN, false);

                    Intent i= new Intent(getApplicationContext(), USSDService.class);
                    Intent ii= new Intent(getApplicationContext(), ScheduledService.class);

                    getApplicationContext().startService(i);
                    getApplicationContext().startService(ii);

                    Toast.makeText(MainActivity.this, "Service has been activated", Toast.LENGTH_SHORT).show();

                }

            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApplicationContext().stopService(new Intent(getApplicationContext(), USSDService.class));
                getApplicationContext().stopService(new Intent(getApplicationContext(), ScheduledService.class));


            }
        });





    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i= new Intent(getApplicationContext(), USSDService.class);
                    Intent ii= new Intent(getApplicationContext(), ScheduledService.class);

                    getApplicationContext().startService(i);
                    getApplicationContext().startService(ii);

                    Toast.makeText(MainActivity.this, "Service has been activated", Toast.LENGTH_SHORT).show();

                    // permission was granted, yay! Do the
                    // contacts-related task you need todo.
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to Call permission", Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }
}