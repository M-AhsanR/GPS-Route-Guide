package com.prime.studio.apps.gpsrouteguide.maps.directions.Receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by Asad on 9/21/2016.
 */
public class RequestSentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("sms",intent.getAction());

        int resultCode = getResultCode();
        switch (resultCode) {
            case Activity.RESULT_OK:
                Toast.makeText(context, "Location request sent!", Toast.LENGTH_LONG).show();
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(context, "Sms sending failed! Please check your network!", Toast.LENGTH_LONG).show();
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(context, " Sms sending failed! No service", Toast.LENGTH_LONG).show();
                break;

        }
    }


}
