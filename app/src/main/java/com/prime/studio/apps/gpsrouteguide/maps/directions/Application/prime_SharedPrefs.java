package com.prime.studio.apps.gpsrouteguide.maps.directions.Application;

import android.content.Context;
import android.content.SharedPreferences;


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
