package com.prime.studio.apps.gpsrouteguide.maps.directions.Service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.prime.studio.apps.gpsrouteguide.maps.directions.R;


/**
 * Created by Admin20 on 9/28/2016.
 */
public class RatingHelpService extends Service {

    private WindowManager windowManager;
    private Point szWindow = new Point();
    WindowManager.LayoutParams params;
    private View chatheadView;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleStart();



        return START_NOT_STICKY;
    }

    private void handleStart() {

        //activityManager.moveTaskToFront(getTaskId(), 0);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);


        chatheadView =  inflater.inflate(R.layout.ratehelp, null);




		/*ImageView imageView = (ImageView) chatheadView.findViewById(R.id.kill);
		GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
		Glide.with(this).load(R.drawable.kill).into(imageViewTarget);
*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {

            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);

        }

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;

        try {
            windowManager.addView(chatheadView, params);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (chatheadView != null) {
                        windowManager.removeView(chatheadView);
                    }
                    stopService(new Intent(getApplicationContext(), RatingHelpService.class));
                }
            }, 1000);
        }catch (Exception e)
        {
          /*  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                // Show alert dialog to the user saying a separate permission is needed
                // Launch the settings activity if the user prefers
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(myIntent);

               *//* final Intent mainIntent = new Intent(getApplicationContext(), RatingHelpService.class);
                startService(mainIntent);*//*
            }*/
        }


    }
}
