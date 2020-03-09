package com.prime.studio.apps.gpsrouteguide.maps.directions.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.MobileAds;
import com.prime.studio.apps.gpsrouteguide.maps.directions.R;

import java.io.IOException;
import java.io.InputStream;


public class TermsAndConditionsActivity extends Activity {

    boolean isChecked = false;
    TextView tv_info;
    ScrollView text_scroll;
    prime_SharedPrefs sharedPrefs_obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.term_and_conditions);

//        MobileAds.initialize(this,getResources().getString(R.string.admob_interstitial_id));

        tv_info = findViewById(R.id.tv_info);
        text_scroll = findViewById(R.id.text_scroll);

        sharedPrefs_obj=new prime_SharedPrefs(this);
        if (sharedPrefs_obj.getFirstTerm())
        {
            if (Build.VERSION.SDK_INT >= 23 ) {
                    if (checkLocationPermission()){
                        startActivity(new Intent(TermsAndConditionsActivity.this, Splash.class));
                        finish();
                    }else {
                        startActivity(new Intent(TermsAndConditionsActivity.this, PermissionsActivity.class));
                        finish();
                    }

            } else {
                startActivity(new Intent(TermsAndConditionsActivity.this, Splash.class));
                finish();
            }
        }
    final Button btnStart=findViewById(R.id.btn_start);
        final CheckBox checkbox = findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isChecked = b;

                if (isChecked)
                {
                    btnStart.setBackgroundResource(R.drawable.unselected);
                }
                else
                {
                    btnStart.setBackgroundResource(R.drawable.prime_unselected);
                }
            }
        });

        try {
            InputStream is = getAssets().open("privacy_sync.txt");

            // We guarantee that the available method returns the total
            // size of the asset...  of course, this does mean that a single
            // asset can't be more than 2 gigs.
            int size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert the buffer into a string.
            String text = new String(buffer);

            // Finally stick the string into the text view.
            // Replace with whatever you need to have the text into.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tv_info.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
            } else {
                tv_info.setText(Html.fromHtml(text));
            }

        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }

        text_scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (text_scroll.getChildAt(0).getBottom() <= (text_scroll.getHeight() + text_scroll.getScrollY())) {
                    // Scroll view is at bottom

                    checkbox.setChecked(true);
                    btnStart.setBackgroundResource(R.drawable.unselected);

                } else {
                    // scrollview is not at bottom
                    checkbox.setChecked(false);
                    btnStart.setBackgroundResource(R.drawable.prime_unselected);

                }
            }
        });

    }

//    private boolean checkWriteExternalPermission() {
//        String permission = Manifest.permission.READ_SMS;
//        int res = checkCallingOrSelfPermission(permission);
//        return (res == PackageManager.PERMISSION_GRANTED);
//    }
    private boolean checkLocationPermission() {
        String permission = android.Manifest.permission.ACCESS_FINE_LOCATION;
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void getStartedClicked(View view) {
        if (isChecked)
        {
            sharedPrefs_obj.setFirstTerm(true);

            if (Build.VERSION.SDK_INT >= 23 ) {
                startActivity(new Intent(TermsAndConditionsActivity.this, PermissionsActivity.class));
                finish();
            }else {
                startActivity(new Intent(TermsAndConditionsActivity.this, Splash.class));
                finish();
            }


        }
        else
            {
            Toast.makeText(TermsAndConditionsActivity.this, "Please accept the terms and conditions.", Toast.LENGTH_SHORT).show();
        }
    }

    public void policyClicked(View view) {
        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://jzz.com.pk/PrimeStudioApps.html"));
        startActivity(browser);
    }
    public void termsClicked(View view) {
        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://jzz.com.pk/PrimeTermcondition.html"));
        startActivity(browser);
    }
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    private void setupWindowAnimations() {
//        Slide slide = TransitionInflater.from(this).inflateTransition(R.transi.explode);
//        getWindow().setExitTransition(slide);
//    }
public class prime_SharedPrefs {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public prime_SharedPrefs(Context context) {
        prefs = context.getSharedPreferences("GpsSpeedometerJZZ", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }



    public void setFirstTerm(boolean isTrue) {
        editor.putBoolean("isprivacyview", isTrue);
        editor.commit();
    }

    public boolean getFirstTerm() {
        return prefs.getBoolean("isprivacyview", false);

    }
}
}

