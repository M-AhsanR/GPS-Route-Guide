package com.prime.studio.apps.gpsrouteguide.maps.directions.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.prime.studio.apps.gpsrouteguide.maps.directions.R;

public class PermissionsActivity extends AppCompatActivity {

    LinearLayout one, two, three, four;
    Button allow_btn;
    private Context mContext;
    final static int REQUEST_CHECK_SETTINGS=120;

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasCallPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasCallPermission == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(mContext, Splash.class));
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        Initialization();
        callanimation();

        mContext = this;

        allow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkGps()) {
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int hasCallPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
                    if (hasCallPermission != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.MODIFY_PHONE_STATE},
                                10);
                    } else {
                        startActivity(new Intent(mContext, Splash.class));
                        finish();
                    }

                } else {
                    startActivity(new Intent(mContext, Splash.class));
                    finish();
                }
            }
        });


    }

    boolean checkGps() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            showGPSDisabledAlertToUser();
            displayLocationSettingsRequest(PermissionsActivity.this);

            return false;
        }

        return true;
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("iamindf", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("iamindf", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(PermissionsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("iamindf", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("iamindf", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }



    private void callanimation() {

        TranslateAnimation translatefirst = (TranslateAnimation) AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_first_cache);


        TranslateAnimation translate2nd = (TranslateAnimation) AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_second_cache);

        TranslateAnimation translate3nd = (TranslateAnimation) AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_third_cache);

        TranslateAnimation translate4rd = (TranslateAnimation) AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_fourth_cache);

        findViewById(R.id.one).setAnimation(translatefirst);
        findViewById(R.id.two).setAnimation(translate2nd);
        findViewById(R.id.three).setAnimation(translate3nd);
        findViewById(R.id.four).setAnimation(translate4rd);
    }

    private void Initialization(){

        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);

        allow_btn = findViewById(R.id.btn_allow);

    }
}
