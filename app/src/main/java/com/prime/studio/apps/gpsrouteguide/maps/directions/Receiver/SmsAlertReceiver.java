package com.prime.studio.apps.gpsrouteguide.maps.directions.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.prime.studio.apps.gpsrouteguide.maps.directions.R;
import com.prime.studio.apps.gpsrouteguide.maps.directions.activity.FirstActivity;
import com.prime.studio.apps.gpsrouteguide.maps.directions.activity.ViewFriendLocation;


/**
 * Created by Asad on 9/21/2016.
 */
public class SmsAlertReceiver extends BroadcastReceiver {
    Notification myNoticationUpper;
    NotificationManager manager;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("sms",intent.getAction());
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);



        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);


                    if (message.contains("view Your Friend's Location http://tinyurl.com/y8l2g394"))
                    {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            if (ViewFriendLocation.getContactName(context, senderNum) != null) {

                                showNotification(context, ViewFriendLocation.getContactName(context, senderNum));



                            } else {
                                showNotification(context, senderNum);


                            }

                          // showNotification(context);
                        }else{
                            if (ViewFriendLocation.getContactName(context, senderNum) != null) {

                                showNotification01(context, ViewFriendLocation.getContactName(context, senderNum));



                            } else {
                                showNotification01(context, senderNum);


                            }
                           // showNotification01(context);
                        }
                    }
                    // Show Alert
                   /* int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,
                            "senderNum: "+ senderNum + ", message: " + message, duration);
                    toast.show();*/

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }






    }




    private void showNotification01(Context context, String title)
    {

        Intent notificationIntent = new Intent(context,  ViewFriendLocation.class);

        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        Bundle bundle = new Bundle();
        bundle.putBoolean("alert", true);
        notificationIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, notificationIntent, 0);

        Notification.Builder builder = new Notification.Builder(context);


        builder.setContentInfo("");
        builder.setAutoCancel(true);

        builder.setContentTitle("Location Received from \n" + title);
        builder.setContentText("Click to see Friend's location!!");
        builder.setSmallIcon(R.drawable.notification1);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_launcher));
        builder.setContentIntent(pendingIntent);
        //builder.setOngoing(true);

        builder.setNumber(100);
      /*  builder.build();

        myNoticationUpper = builder.build();
        manager.notify(12, myNoticationUpper);*/

        if (Build.VERSION.SDK_INT < 16) {
            manager.notify(12, builder.getNotification());
        } else {
            manager.notify(12, builder.build());
        }


    }


    private void showNotification(Context context, String title) {
        Intent notificationIntent = new Intent(context, ViewFriendLocation.class);

        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, notificationIntent, 0);

        Notification.Builder builder = new Notification.Builder(context);


        builder.setContentInfo("");
        builder.setAutoCancel(true);

        builder.setContentTitle("Location Received from \n"+ title);

        builder.setContentText("Click to see Friend's location!!");
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentIntent(pendingIntent);

        Intent push = new Intent();
        push.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        push.setClass(context, FirstActivity.class);

        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                push, PendingIntent.FLAG_CANCEL_CURRENT);
        builder
                .setContentText("")
                .setFullScreenIntent(fullScreenPendingIntent, true);

        builder.setNumber(100);
        builder.build();

        myNoticationUpper = builder.build();
        manager.notify(11, myNoticationUpper);


    }

}
