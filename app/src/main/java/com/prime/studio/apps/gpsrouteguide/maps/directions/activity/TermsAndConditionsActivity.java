package com.prime.studio.apps.gpsrouteguide.maps.directions.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;


import com.prime.studio.apps.gpsrouteguide.maps.directions.R;


public class TermsAndConditionsActivity extends Activity {

    boolean isChecked = false;
    prime_SharedPrefs sharedPrefs_obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.term_and_conditions);

        sharedPrefs_obj=new prime_SharedPrefs(this);
        if (sharedPrefs_obj.getFirstTerm())
        {
            startActivity(new Intent(TermsAndConditionsActivity.this,Splash.class));
            finish();
        }
        else
        {
        }
    final Button btnStart=findViewById(R.id.btn_start);
        CheckBox checkbox = findViewById(R.id.checkbox);
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
    }

    public void getStartedClicked(View view) {
        if (isChecked)
        {
            sharedPrefs_obj.setFirstTerm(true);
            startActivity(new Intent(TermsAndConditionsActivity.this, Splash.class));
            finish();
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

