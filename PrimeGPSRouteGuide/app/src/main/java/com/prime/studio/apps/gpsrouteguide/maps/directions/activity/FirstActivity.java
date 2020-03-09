package com.prime.studio.apps.gpsrouteguide.maps.directions.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.jzz.the.it.solution.market.library.activity.PromotionalApps;
import com.prime.studio.apps.gpsrouteguide.maps.directions.R;
import com.prime.studio.apps.gpsrouteguide.maps.directions.Service.RatingHelpService;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Ahmed on 9/15/2016.
 */
public class FirstActivity extends AppCompatActivity implements LocationListener {
    //  public ListView listView;
    ImageButton mRouteFinder, mMyLocation, mYourFavouritePlaces, mViewFriendLocation, mShareMyLocation, mFeedback, mDiscover, mSpeedometer, mGPSRouteFinder2, mDrivingRoute, mEarthMap, mPrivacyPolicy;
    //  ImageButton mApp1,mApp2,mApp3,mApp4,mApp5,mApp6,mApp7,mApp8;
    TextView mRamUsage;
    Context mContext;
    String MarketLink = "market://details?id=";
    String browserLink = "https://play.google.com/store/apps/details?id=";
    static final int PICK_CONTACT = 1;
    Double myLongitude, myLatitude;
    boolean networkCheck = false;
    private LogLocationListener logLocationListener;
    private LocationManager lm;
    private boolean gpsok, networkok, checking = false;
    private Location location;
    private Uri uriContact;
    private String contactID;
    String contactNumber;
    final static int REQUEST_CHECK_SETTINGS = 121;
    View convertView;
    TextView add_from_contacts, add_from_callLog, add_from_messages, manually_add;
    android.support.v7.app.AlertDialog alertdialog;
    InterstitialAd mInterstitialAd;
    String NearBy_FindRoute = "nothing clicked";
    LinearLayout mAdLayout;
    AdView adView;
    String app1, app2, app3, app4, app5, app6, app7, app8;
    private RatingBar ratingBar;
    Animation zoomout, animZoomIn;
    ImageButton mTracker;
    Animation appzoomout, appanimZoomIn;
    String number;
    public static boolean showlib = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstlayout);
        onNewIntent(getIntent());
        initialize();
        action();
    }

    @Override
    public void onResume() {
        super.onResume();
        action();
    }

    private void initialize() {

        mContext = FirstActivity.this;

        mRouteFinder = (ImageButton) findViewById(R.id.Imgbtnroutefinder);
        mMyLocation = (ImageButton) findViewById(R.id.Imgbtnmylocation);
        mYourFavouritePlaces = (ImageButton) findViewById(R.id.Imgbtnyourfavplaces);
        mViewFriendLocation = (ImageButton) findViewById(R.id.Imgbtnviewfriendlocation);
        mShareMyLocation = (ImageButton) findViewById(R.id.Imgbtnsharemylocation);
        mFeedback = (ImageButton) findViewById(R.id.Imgbtnfeedback);
        mDiscover = (ImageButton) findViewById(R.id.Imgbtndiscover);
        mSpeedometer = (ImageButton) findViewById(R.id.Imgbtnspeedometer);
        mGPSRouteFinder2 = (ImageButton) findViewById(R.id.Imgbtngps2);
        mDrivingRoute = (ImageButton) findViewById(R.id.Imgbtndrivingroute);
        mEarthMap = (ImageButton) findViewById(R.id.Imgbtnearthmap);
        mTracker = (ImageButton) findViewById(R.id.Imgbtntracker);
        mPrivacyPolicy = (ImageButton) findViewById(R.id.Imgbtnprivacypolicy);
       /* mApp1 = (ImageButton) findViewById(R.id.Imgbtnapp1);
        mApp2 = (ImageButton) findViewById(R.id.Imgbtnapp2);
        mApp3 = (ImageButton) findViewById(R.id.Imgbtnapp3);
        mApp4 = (ImageButton) findViewById(R.id.Imgbtnapp4);
        mApp5 = (ImageButton) findViewById(R.id.Imgbtnapp5);
        mApp6 = (ImageButton) findViewById(R.id.Imgbtnapp6);
        mApp7 = (ImageButton) findViewById(R.id.Imgbtnapp7);
        mApp8 = (ImageButton) findViewById(R.id.Imgbtnapp8);
*/


        lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        logLocationListener = new LogLocationListener();


        //////////////////ADs/////////////
        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
        requestNewInterstitial();

/////////////////////////////////Ads

        mAdLayout = (LinearLayout) findViewById(R.id.adlayout);
        adView = findViewById(R.id.adView);

        AdRequest request = new AdRequest.Builder()
                .build();
        adView.loadAd(request);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdLayout.setVisibility(View.VISIBLE);
            }
        });


        //////////////////////////////////////
        startLogging(true);
       /* InitiateGPS();
        if (isNetworkConnected()) {
            startLogging(true);
        } else {
            GetOldGPS();
        }*/


       /* app1="com.zee.techno.apps.offline.gps.route.finder";
        app2="com.zee.techno.apps.gps.route.finder.map";
        app3="com.sink.apps.mobile.location.tracker";
        app4="com.zee.techno.apps.videodownloaderforfacebook";
        app5="com.sink.apps.girl.voice.changer";
        app6="com.zee.techno.apps.smart.manager.memory.ram.cleaner";
        app7="com.jzz.the.it.solutions.supervpn.proxy.vpn.free";
        app8="com.jzz.the.it.solutions.always.on.display.amoled";*/

//////////////////////////////Animation/////////////////
        zoomout = AnimationUtils.loadAnimation(mContext,
                R.anim.zoom_out);
        animZoomIn = AnimationUtils.loadAnimation(
                mContext, R.anim.zoom_in);

        appzoomout = AnimationUtils.loadAnimation(mContext,
                R.anim.zoom_out_app);
        appanimZoomIn = AnimationUtils.loadAnimation(
                mContext, R.anim.zoom_in_app);


        animZoomIn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) { // TODO

            }

            @Override
            public void onAnimationEnd(Animation arg0) {

                mRamUsage.startAnimation(zoomout);
            }
        });
        zoomout.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) { // TODO

            }

            @Override
            public void onAnimationEnd(Animation arg0) {

                mRamUsage.startAnimation(animZoomIn);
            }
        });

        appanimZoomIn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) { // TODO

            }

            @Override
            public void onAnimationEnd(Animation arg0) {

               /* mApp1.startAnimation(appzoomout);
                mApp2.startAnimation(appzoomout);
                mApp3.startAnimation(appzoomout);
                mApp4.startAnimation(appzoomout);
                mApp5.startAnimation(appzoomout);
                mApp6.startAnimation(appzoomout);
                mApp7.startAnimation(appzoomout);
                mApp8.startAnimation(appzoomout);*/


            }
        });
        appzoomout.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) { // TODO

            }

            @Override
            public void onAnimationEnd(Animation arg0) {

               /* mApp1.startAnimation(appanimZoomIn);
                mApp2.startAnimation(appanimZoomIn);
                mApp3.startAnimation(appanimZoomIn);
                mApp4.startAnimation(appanimZoomIn);
                mApp5.startAnimation(appanimZoomIn);
                mApp6.startAnimation(appanimZoomIn);
                mApp7.startAnimation(appanimZoomIn);
                mApp8.startAnimation(appanimZoomIn);*/


            }
        });
        ///////////////////////////////////////
    }

    private void PrivacyDialogue(){

        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.policy_dialogue);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView policy_text = dialog.findViewById(R.id.policy_dialogue_text);
        TextView d_text1 = dialog.findViewById(R.id.d_text1);
        TextView d_text2 = dialog.findViewById(R.id.d_text2);
        TextView d_text3 = dialog.findViewById(R.id.d_text3);
        Button btn_ok = dialog.findViewById(R.id.btn_privacy_dialogue_ok);

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
                policy_text.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
            } else {
                policy_text.setText(Html.fromHtml(text));
            }
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    private void action() {

      /*  mApp1.startAnimation(appanimZoomIn);
        mApp2.startAnimation(appanimZoomIn);
        mApp3.startAnimation(appanimZoomIn);
        mApp4.startAnimation(appanimZoomIn);
        mApp5.startAnimation(appanimZoomIn);
        mApp6.startAnimation(appanimZoomIn);
        mApp7.startAnimation(appanimZoomIn);
        mApp8.startAnimation(appanimZoomIn);*/


        mTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.prime.studio.apps.gps.personal.tracker.Activity.MainActivity");
                intent.setPackage("com.prime.studio.apps.gps.personal.tracker");
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                try {
                    startActivityForResult(intent, 0);

                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    if (isAppInstalled(mContext, "com.prime.studio.apps.gps.personal.tracker")) {
                        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage("com.prime.studio.apps.gps.personal.tracker");
                        if (launchIntent != null) {
                            startActivity(launchIntent);//null pointer check in case package name was not found
                        }

                    } else {
                        // startActivity(new Intent(mContext, LocationTracker.class));
                        gpstracker_dialog();
                    }
                }
            }
        });
        mRouteFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkGps()) {
                    return;
                }
                NearBy_FindRoute = "RouteFinder Clicked";

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        int hasCallPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
                        if (hasCallPermission != PackageManager.PERMISSION_GRANTED) {

                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    0);
                        } else {
                            startActivity(new Intent(getBaseContext(), RouteFinder.class));
                            finish();
                        }

                    } else {
                        startActivity(new Intent(getBaseContext(), RouteFinder.class));
                        finish();
                    }

//                    try {
//
//                        Uri gmmIntentUri = Uri.parse("google.navigation:q=");
//                        // Taronga+Zoo,+Sydney+Australia
//                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                        mapIntent.setPackage("com.google.android.apps.maps");
//                        startActivity(mapIntent);
//                    } catch (Exception e) {
//
//                        Toast.makeText(mContext, "Install this First", Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(
//                                Intent.ACTION_VIEW,
//                                Uri.parse(browserLink + "com.google.android.apps.maps")));
//
//                    }

                }
            }
        });


        mMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkGps()) {
                    return;
                }
                NearBy_FindRoute = "MyLocation Clicked";
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            int hasCallPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
                            if (hasCallPermission != PackageManager.PERMISSION_GRANTED) {

                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        0);


                                return;
                            } else {
                                String urlAddress = "http://maps.google.com/maps?q="
                                        + myLatitude + "," + myLongitude + "("
                                        + "your Current Location" + ")&iwloc=A&hl=es";

                                Intent maps = new Intent(Intent.ACTION_VIEW, Uri
                                        .parse(urlAddress));
                                startActivity(maps);
                            }
                        } else {
                            String urlAddress = "http://maps.google.com/maps?q="
                                    + myLatitude + "," + myLongitude + "("
                                    + "your Current Location" + ")&iwloc=A&hl=es";

                            Intent maps = new Intent(Intent.ACTION_VIEW, Uri
                                    .parse(urlAddress));
                            startActivity(maps);
                        }
                    } catch (Exception e) {

                        Toast.makeText(mContext, "Install this First", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(browserLink + "com.google.android.apps.maps")));

                    }
                }
            }
        });

        mFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               /* final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(FirstActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = (View) inflater.inflate(R.layout.rating, null);
                alertDialog.setView(view);
                alertdialog= alertDialog.show();
                ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    public void onRatingChanged(RatingBar ratingBar, float rating,
                                                boolean fromUser) {


                        if (rating < 2) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jzz.user.experience@gmail.com"});
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback: GPS Route Guide ");
                            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                                startActivity(intent);
                            }
                            alertdialog.dismiss();

                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    final Intent mainIntent = new Intent(mContext, RatingHelpService.class);
                                    mContext.startService(mainIntent);

                                }
                            }, 2000);
                            try {

                                startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                        .parse(MarketLink + mContext.getPackageName())));

                            } catch (ActivityNotFoundException anfe) {

                                startActivity(new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(browserLink + mContext.getPackageName())));
                            }

                            alertdialog.dismiss();

                        }

                    }
                });
*/

                feedback_dialog();

            }
        });

        mDrivingRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkGps()) {
                    return;
                }
                startActivity(new Intent(mContext, StartDrivingRoute.class));

            }
        });

        mEarthMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkGps()) {
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int hasCallPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
                    if (hasCallPermission != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                1);
                    } else {

                        startActivity(new Intent(mContext, Navigation.class).putExtra("key", "earth"));
                        finish();
                    }
                } else {
                    startActivity(new Intent(mContext, Navigation.class).putExtra("key", "earth"));
                    finish();
                }
            }
        });

        mPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PrivacyDialogue();

//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://jzz.com.pk/PrimeStudioApps.html"));
//                startActivity(browserIntent);
            }
        });
        mYourFavouritePlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkGps()) {
                    return;
                }
                startActivity(new Intent(mContext, Fav.class));
                finish();
            }
        });


        mSpeedometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isAppInstalled(mContext, "com.prime.studio.apps.gps.speedometer.odometer")) {
                    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage("com.prime.studio.apps.gps.speedometer.odometer");
                    if (launchIntent != null) {
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }

                } else {
                    speedometer_dialog();
                }
            }
        });


        mGPSRouteFinder2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isAppInstalled(mContext, "com.prime.studio.apps.gps.route.finder.gps.tracker")) {
                    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage("com.prime.studio.apps.gps.route.finder.gps.tracker");
                    if (launchIntent != null) {
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }

                } else {
                    gpsroutefinder2_dialog();
                }

            }
        });
        mDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //   MainActivity.showlib = true;
                startActivity(new Intent(mContext, PromotionalApps.class));
                //getActivity().finish();
            }
        });

        mViewFriendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkGps()) {
                    return;
                }

                VisitedPlaces(convertView);

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    int hasCallPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
//                    if (hasCallPermission != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
//                            || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//
//                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS},
//                                3);
//                    } else {
//                        startActivity(new Intent(mContext, ViewFriendLocation.class));
//                        finish();
//                    }
//                } else {
//                    startActivity(new Intent(mContext, ViewFriendLocation.class));
//                    finish();
//                }

            }
        });

        mShareMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkGps()) {
                    return;
                }

                ShareLocation(convertView);

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    int hasCallPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
//                    if (hasCallPermission != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
//                            || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//
//                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS},
//                                4);
//                    } else {
//
//                        shareloc_dialog();
                       /* AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                        // set title
                        alertDialogBuilder.setTitle("share Your Location");

                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Location will be Shared through SMS\nCharges may Apply, share ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(FirstActivity.this);
                                        LayoutInflater inflater = getLayoutInflater();
                                        convertView = (View) inflater.inflate(R.layout.dialog, null);
                                        add_from_contacts = (Button) convertView.findViewById(R.id.add_from_contacts);
                                        add_from_callLog = (Button) convertView.findViewById(R.id.add_from_callLog);
                                        add_from_messages = (Button) convertView.findViewById(R.id.add_from_messages);
                                        manually_add = (Button) convertView.findViewById(R.id.manually_add);
                                        alertDialog.setView(convertView);
                                        alertdialog = alertDialog.show();

                                        add_from_contacts.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                alertdialog.dismiss();
                                                Intent intent = new Intent(getBaseContext(), Select_Contact.class);
                                                intent.putExtra("key", "contact");
                                                intent.putExtra("location", "share");
                                                intent.putExtra("myLatitude", myLatitude);
                                                intent.putExtra("myLongitude", myLongitude);

                                                startActivity(intent);


                                            }
                                        });

                                        add_from_callLog.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                alertdialog.dismiss();

                                                Intent intent = new Intent(getBaseContext(), Select_Contact.class);
                                                intent.putExtra("key", "callLog");
                                                intent.putExtra("location", "share");
                                                intent.putExtra("myLatitude", myLatitude);
                                                intent.putExtra("myLongitude", myLongitude);
                                                startActivity(intent);
                                            }
                                        });
                                        add_from_messages.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                alertdialog.dismiss();
                                                Intent intent = new Intent(getBaseContext(), Select_Contact.class);
                                                intent.putExtra("key", "messages");
                                                intent.putExtra("location", "share");
                                                intent.putExtra("myLatitude", myLatitude);
                                                intent.putExtra("myLongitude", myLongitude);
                                                startActivity(intent);
                                            }
                                        });
                                        manually_add.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                alertdialog.dismiss();
                                                manually_add();
                                            }
                                        });


                                    }
                                })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();*/

//                    }
//                } else {
//
//                    shareloc_dialog();
                  /*  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    // set title
                    alertDialogBuilder.setTitle("share Your Location");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Location will be Shared through SMS\nCharges may Apply, share ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(FirstActivity.this);
                                    LayoutInflater inflater = getLayoutInflater();
                                    convertView = (View) inflater.inflate(R.layout.dialog, null);
                                    add_from_contacts = (Button) convertView.findViewById(R.id.add_from_contacts);
                                    add_from_callLog = (Button) convertView.findViewById(R.id.add_from_callLog);
                                    add_from_messages = (Button) convertView.findViewById(R.id.add_from_messages);
                                    manually_add = (Button) convertView.findViewById(R.id.manually_add);
                                    alertDialog.setView(convertView);
                                    alertdialog = alertDialog.show();

                                    add_from_contacts.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            alertdialog.dismiss();
                                            Intent intent = new Intent(getBaseContext(), Select_Contact.class);
                                            intent.putExtra("key", "contact");
                                            intent.putExtra("location", "share");
                                            intent.putExtra("myLatitude", myLatitude);
                                            intent.putExtra("myLongitude", myLongitude);

                                            startActivity(intent);


                                        }
                                    });

                                    add_from_callLog.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            alertdialog.dismiss();

                                            Intent intent = new Intent(getBaseContext(), Select_Contact.class);
                                            intent.putExtra("key", "callLog");
                                            intent.putExtra("location", "share");
                                            intent.putExtra("myLatitude", myLatitude);
                                            intent.putExtra("myLongitude", myLongitude);
                                            startActivity(intent);
                                        }
                                    });
                                    add_from_messages.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            alertdialog.dismiss();
                                            Intent intent = new Intent(getBaseContext(), Select_Contact.class);
                                            intent.putExtra("key", "messages");
                                            intent.putExtra("location", "share");
                                            intent.putExtra("myLatitude", myLatitude);
                                            intent.putExtra("myLongitude", myLongitude);
                                            startActivity(intent);
                                        }
                                    });
                                    manually_add.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertdialog.dismiss();
                                            manually_add();
                                        }
                                    });


                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();*/
//                }
            }

        });

       /* mApp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isAppInstalled(mContext,app1)) {
                    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(app1);
                    if (launchIntent != null) {
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }

                } else {
                    try {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(MarketLink + app1)));

                    } catch (ActivityNotFoundException anfe) {

                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(browserLink + app1)));
                    }
                }
            }
        });

        mApp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAppInstalled(mContext,app2)) {
                    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(app2);
                    if (launchIntent != null) {
                        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);


                        startActivity(new Intent(launchIntent));//null pointer check in case package name was not found
                    }

                } else {
                    try {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(MarketLink + app2)));

                    } catch (ActivityNotFoundException anfe) {

                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(browserLink + app2)));
                    }
                }
            }
        });

        mApp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAppInstalled(mContext,app3)) {
                    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(app3);
                    if (launchIntent != null) {

                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }

                } else {
                    try {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(MarketLink + app3)));

                    } catch (ActivityNotFoundException anfe) {

                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(browserLink + app3)));
                    }
                }
            }
        });

        mApp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAppInstalled(mContext,app4)) {
                    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(app4);
                    if (launchIntent != null) {

                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }

                } else {
                    try {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(MarketLink + app4)));

                    } catch (ActivityNotFoundException anfe) {

                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(browserLink + app4)));
                    }
                }
            }
        });

        mApp5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAppInstalled(mContext,app5)) {
                    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(app5);
                    if (launchIntent != null) {

                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }

                } else {
                    try {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(MarketLink + app5)));

                    } catch (ActivityNotFoundException anfe) {

                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(browserLink + app5)));
                    }
                }
            }
        });

        mApp6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAppInstalled(mContext,app6)) {
                    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(app6);
                    if (launchIntent != null) {

                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }

                } else {
                    try {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(MarketLink + app6)));

                    } catch (ActivityNotFoundException anfe) {

                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(browserLink + app6)));
                    }
                }
            }
        });

        mApp7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAppInstalled(mContext,app7)) {
                    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(app7);
                    if (launchIntent != null) {

                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }

                } else {
                    try {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(MarketLink + app7)));

                    } catch (ActivityNotFoundException anfe) {

                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(browserLink + app7)));
                    }
                }
            }
        });

        mApp8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAppInstalled(mContext,app8)) {
                    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(app8);
                    if (launchIntent != null) {

                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }

                } else {
                    try {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(MarketLink + app8)));

                    } catch (ActivityNotFoundException anfe) {

                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(browserLink + app8)));
                    }
                }
            }
        });
*/


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
//                startActivity(new Intent(Menu.this, MainActivity.class));
                if (NearBy_FindRoute.equalsIgnoreCase("RouteFinder Clicked")) {

                    NearBy_FindRoute = "nothing clicked";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        int hasCallPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
                        if (hasCallPermission != PackageManager.PERMISSION_GRANTED) {

                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    0);
                        } else {
                            startActivity(new Intent(getBaseContext(), RouteFinder.class));
                            finish();
                        }

                    } else {
                        startActivity(new Intent(getBaseContext(), RouteFinder.class));
                        finish();
                    }
                } else if (NearBy_FindRoute.equalsIgnoreCase("MyLocation Clicked")) {
                    NearBy_FindRoute = "nothing clicked";
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            int hasCallPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
                            if (hasCallPermission != PackageManager.PERMISSION_GRANTED) {

                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        0);


                                return;
                            } else {
                                String urlAddress = "http://maps.google.com/maps?q="
                                        + myLatitude + "," + myLongitude + "("
                                        + "your Current Location" + ")&iwloc=A&hl=es";

                                Intent maps = new Intent(Intent.ACTION_VIEW, Uri
                                        .parse(urlAddress));
                                startActivity(maps);
                            }
                        } else {
                            String urlAddress = "http://maps.google.com/maps?q="
                                    + myLatitude + "," + myLongitude + "("
                                    + "your Current Location" + ")&iwloc=A&hl=es";

                            Intent maps = new Intent(Intent.ACTION_VIEW, Uri
                                    .parse(urlAddress));
                            startActivity(maps);
                        }
                    } catch (Exception e) {
                        Toast.makeText(mContext, "Install this First", Toast.LENGTH_LONG).show();
                        try {
                            startActivity(new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(MarketLink + "com.google.android.apps.maps")));
                        } catch (Exception e1) {
                            e1.printStackTrace();

                            startActivity(new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(browserLink + "com.google.android.apps.maps")));
                        }
                    }
                }

            }
        });


    }

    public void ShareLocation(View view) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/@0,0,0z/data=!4m2!7m1!2e1"));
            startActivity(intent);
        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/@0,0,0z/data=!4m2!7m1!2e1"));
                startActivity(intent);
            }
        });

    }
    public void VisitedPlaces(View view) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/@33.494309,73.0936509,15z/data=!4m2!10m1!1e2?hl=en&authuser=0"));
            startActivity(intent);
        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/@33.494309,73.0936509,15z/data=!4m2!10m1!1e2?hl=en&authuser=0"));
                startActivity(intent);
            }
        });

    }


    void gpsroutefinder2_dialog() {
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(FirstActivity.this, R.style.CustomDialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = (View) inflater.inflate(R.layout.gpsroutefinder2_dialog, null);
        alertDialog.setView(view);
        //   alertDialog.setCancelable(false);

        Button btnok = (Button) view.findViewById(R.id.mOK);
        alertdialog = alertDialog.show();

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog.dismiss();
                try {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse(MarketLink + "com.prime.studio.apps.gps.route.finder.gps.tracker")));

                } catch (android.content.ActivityNotFoundException anfe) {

                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(browserLink + "com.prime.studio.apps.gps.route.finder.gps.tracker")));
                }

            }
        });


    }

    void speedometer_dialog() {
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(FirstActivity.this, R.style.CustomDialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = (View) inflater.inflate(R.layout.speedometer_dialog, null);
        alertDialog.setView(view);
        //   alertDialog.setCancelable(false);

        TextView btnok = (TextView) view.findViewById(R.id.mOK);
        alertdialog = alertDialog.show();

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog.dismiss();
                try {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse(MarketLink + "com.prime.studio.apps.gps.speedometer.odometer")));

                } catch (android.content.ActivityNotFoundException anfe) {

                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(browserLink + "com.prime.studio.apps.gps.speedometer.odometer")));
                }

            }
        });


    }

    void shareloc_dialog() {
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(FirstActivity.this, R.style.CustomDialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = (View) inflater.inflate(R.layout.sharelocation_dialog, null);
        alertDialog.setView(view);
        alertDialog.setCancelable(false);

        TextView btnok = (TextView) view.findViewById(R.id.mOK);
        TextView btnn = (TextView) view.findViewById(R.id.mNO);
        alertdialog = alertDialog.show();
        btnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog.dismiss();
            }
        });
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog.dismiss();
                final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(FirstActivity.this, R.style.CustomDialog);
                LayoutInflater inflater = getLayoutInflater();
                convertView = (View) inflater.inflate(R.layout.selectcontact_dialog, null);
                add_from_contacts = (TextView) convertView.findViewById(R.id.add_from_contacts);
                add_from_callLog = (TextView) convertView.findViewById(R.id.add_from_callLog);
                add_from_messages = (TextView) convertView.findViewById(R.id.add_from_messages);
                manually_add = (TextView) convertView.findViewById(R.id.manually_add);
                alertDialog.setView(convertView);
                alertdialog = alertDialog.show();

                add_from_contacts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertdialog.dismiss();
                        Intent intent = new Intent(getBaseContext(), Select_Contact.class);
                        intent.putExtra("key", "contact");
                        intent.putExtra("location", "share");
                        intent.putExtra("myLatitude", myLatitude);
                        intent.putExtra("myLongitude", myLongitude);

                        startActivity(intent);


                    }
                });

                add_from_callLog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertdialog.dismiss();

                        Intent intent = new Intent(getBaseContext(), Select_Contact.class);
                        intent.putExtra("key", "callLog");
                        intent.putExtra("location", "share");
                        intent.putExtra("myLatitude", myLatitude);
                        intent.putExtra("myLongitude", myLongitude);
                        startActivity(intent);
                    }
                });
                add_from_messages.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertdialog.dismiss();
                        Intent intent = new Intent(getBaseContext(), Select_Contact.class);
                        intent.putExtra("key", "messages");
                        intent.putExtra("location", "share");
                        intent.putExtra("myLatitude", myLatitude);
                        intent.putExtra("myLongitude", myLongitude);
                        startActivity(intent);
                    }
                });
                manually_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertdialog.dismiss();
                        manually_add();
                    }
                });
            }
        });


    }

    void gpstracker_dialog() {
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(FirstActivity.this, R.style.CustomDialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = (View) inflater.inflate(R.layout.gpstracker_dialog, null);
        alertDialog.setView(view);
        //  alertDialog.setCancelable(false);

        TextView btnok = (TextView) view.findViewById(R.id.mOK);


        alertdialog = alertDialog.show();


        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog.dismiss();
                try {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse(MarketLink + "com.prime.studio.apps.gps.personal.tracker")));

                } catch (android.content.ActivityNotFoundException anfe) {

                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(browserLink + "com.prime.studio.apps.gps.personal.tracker")));
                }

            }
        });


    }

    void feedback_dialog() {
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(FirstActivity.this, R.style.CustomDialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = (View) inflater.inflate(R.layout.feedback_dialog, null);
        alertDialog.setView(view);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {


                if (rating < 2) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jzz.user.experience@gmail.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback: GPS Route Guide ");
                    if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    alertdialog.dismiss();

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final Intent mainIntent = new Intent(mContext, RatingHelpService.class);
                            mContext.startService(mainIntent);

                        }
                    }, 2000);
                    try {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(MarketLink + mContext.getPackageName())));

                    } catch (ActivityNotFoundException anfe) {

                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(browserLink + mContext.getPackageName())));
                    }

                    alertdialog.dismiss();

                }

            }
        });
        alertdialog = alertDialog.show();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras != null) {

            number = extras.getString("number");
            //  number = getIntent().getStringExtra("number");
            Log.d("number", String.valueOf(number));
            if (number != null) {
                if (ActivityCompat.checkSelfPermission(FirstActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FirstActivity.this);
                    // set title
                    alertDialogBuilder.setTitle("Location Request");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Location will be Shared through SMS\nCharges may Apply, share ?")
                            .setCancelable(false)
                            .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (ActivityCompat.checkSelfPermission(FirstActivity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FirstActivity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                                        InitiateSMS();


                                    }
                                    if (ActivityCompat.checkSelfPermission(FirstActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                                        InitiateContact();


                                    } else {

                                        try {
                                            if (myLatitude == null) {

                                                final ProgressDialog progress = new ProgressDialog(FirstActivity.this);
                                                progress.setTitle("Connecting to GPS\nStay outside for better Results");
                                                progress.setMessage("Getting Your Location...");
                                                progress.setCancelable(false);
                                                progress.show();
                                                progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        progress.cancel();
                                                        dialog.dismiss();
                                                    }
                                                });


                                                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, (LocationListener) logLocationListener);

                                                final Handler handler = new Handler();
                                                Runnable task = new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        if (ActivityCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                            // TODO: Consider calling
                                                            //    ActivityCompat#requestPermissions
                                                            // here to request the missing permissions, and then overriding
                                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                            //                                          int[] grantResults)
                                                            // to handle the case where the user grants the permission. See the documentation
                                                            // for ActivityCompat#requestPermissions for more details.
                                                            return;
                                                        }
                                                        if (lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                                                            myLatitude = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                                                            myLongitude = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
                                                            String Msg = "Download this Application to view Your Friend's Location http://tinyurl.com/y8l2g394\nLatitude=" + myLatitude + ";Longitude=" + myLongitude;

                                                            //  sendSmsMessage(contactNumber, Msg);
                                                            sendSms(number, Msg);
                                                            progress.cancel();
                                                            handler.removeCallbacks(this);
                                                        } else {

                                                            handler.postDelayed(this, 3000);
                                                        }


                                                    }

                                                };
                                                handler.post(task);


                                            } else {


                                                String Msg = "Download this Application to view Your Friend's Location http://tinyurl.com/y8l2g394\nLatitude=" + myLatitude + ";Longitude=" + myLongitude;

                                                // sendSmsMessage(contactNumber, Msg);
                                                sendSms(number, Msg);

                                            }

                                        } catch (Exception e) {
                                        }


                                    }


                                }
                            })
                            .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();


                } else {
                    InitiateGPS();
                }
            }

        }
    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    void manually_add() {
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(FirstActivity.this, R.style.CustomDialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = (View) inflater.inflate(R.layout.manually_sharelocation_dialog, null);
        alertDialog.setView(view);
        final EditText edtnumber = (EditText) view.findViewById(R.id.edtnumber);
        TextView btncancel = (TextView) view.findViewById(R.id.btncancel);
        TextView btnsend = (TextView) view.findViewById(R.id.btnsend);

        alertdialog = alertDialog.show();

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog.dismiss();
            }
        });

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                    InitiateSMS();

                    return;
                } else {

                    try {
                        if (myLatitude == null) {

                            final ProgressDialog progress = new ProgressDialog(mContext);
                            progress.setTitle("Connecting to GPS\nStay outside for better Results");
                            progress.setMessage("Getting Your Location...");
                            progress.setCancelable(false);
                            progress.show();
                            progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    progress.cancel();
                                    dialog.dismiss();
                                }
                            });


                            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, (LocationListener) mContext);

                            final Handler handler = new Handler();
                            Runnable task = new Runnable() {
                                @Override
                                public void run() {

                                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    if (lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                                        myLatitude = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                                        myLongitude = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
                                        String Msg = "Download this Application to view Your Friend's Location http://tinyurl.com/y8l2g394\nLatitude=" + myLatitude + ";Longitude=" + myLongitude;

                                        //  sendSmsMessage(contactNumber, Msg);
                                        sendSms(edtnumber.getText().toString(), Msg);
                                        progress.cancel();
                                        handler.removeCallbacks(this);
                                    } else {

                                        handler.postDelayed(this, 3000);
                                    }


                                }

                            };
                            handler.post(task);


                        } else {


                            String Msg = "Download this Application to view Your Friend's Location http://tinyurl.com/y8l2g394\nLatitude=" + myLatitude + ";Longitude=" + myLongitude;

                           // sendSmsMessage(contactNumber, Msg);
                            sendSms(edtnumber.getText().toString(), Msg);

                        }

                    } catch (Exception e) {
                    }

                }


                alertdialog.dismiss();
            }
        });
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void GetOldGPS() {
        try {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                return;
            }
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 50, (LocationListener) mContext);
        } catch (Exception e) {
            Toast.makeText(mContext, "GPS not Found", Toast.LENGTH_LONG).show();
        }

    }



    public  boolean isSimExists()
    {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        int SIM_STATE = telephonyManager.getSimState();

        if(SIM_STATE == TelephonyManager.SIM_STATE_READY)
            return true;
        else
        {
            switch(SIM_STATE)
            {
                case TelephonyManager.SIM_STATE_ABSENT: //SimState = "No Sim Found!";
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED: //SimState = "Network Locked!";
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED: //SimState = "PIN Required to access SIM!";
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED: //SimState = "PUK Required to access SIM!"; // Personal Unblocking Code
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN: //SimState = "Unknown SIM State!";
                    break;
            }
            return false;
        }
    }
    private void sendSms(String straddress, String strmsg) {
        if (isSimExists()) {
            try {
                String SENT = "SMS_SENT";

                PendingIntent sentPI = PendingIntent.getBroadcast(mContext, 0, new Intent(SENT), 0);



                SmsManager smsMgr = SmsManager.getDefault();
                smsMgr.sendTextMessage(straddress, null, strmsg, sentPI, null);
            } catch (Exception e) {
                Toast.makeText(mContext, e.getMessage() + "!\n" + "Failed to send SMS", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mContext, "" + " " + "Cannot send SMS", Toast.LENGTH_LONG).show();
        }
    }
    private void startLogging(boolean check) {
        if(check) {
            //checking if gps and network is ON.
            gpsok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkok = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             //   Toast.makeText(mContext, "LOCATION PROVIDER PERMISSIONS NOT GRANTED", Toast.LENGTH_LONG).show();
                return;
            }
            checking = true;
            //checking every 100 miliSec and minDistance change
            try {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, logLocationListener);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, logLocationListener);
            }catch (Exception e ){
                e.printStackTrace();
            }
                if (gpsok && networkok) {
//                Toast.makeText(mContext, "GPS and NETWORK PROVIDER Found!!", Toast.LENGTH_SHORT).show();
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    myLatitude = location.getLatitude();
                    myLongitude = location.getLongitude();

                }

            } else {
                Toast.makeText(mContext, "LOCATION PROVIDER NOT AVAILABLE", Toast.LENGTH_LONG).show();
            }
        }else {
            lm.removeUpdates(logLocationListener);
            checking = false;
        }
    }
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        if(networkCheck==false) {
//            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//            final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
//            final String message = "GPS is not Enable\nEnable GPS for Proper Functioning";
//
//            networkCheck=true;
//
//            builder.setMessage(message)
//                    .setPositiveButton("Enable GPS in Settings",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface d, int id) {
//
//
//                                    mContext.startActivity(new Intent(action).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                                    d.dismiss();
//                                }
//                            })
//                    .setNegativeButton("Cancel",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface d, int id) {
//                                    d.cancel();
//                                }
//                            });
//            builder.create().show();
        }
    }

    @Override
    public void onBackPressed() {
//        if (!showlib)
//        {
//            showlib= true;
//            startActivity(new Intent(getBaseContext(), PromotionalApps.class));
//        }else
            {

            finish();
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void InitiateSMS(){
        int hasCallPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.SEND_SMS);
        if (hasCallPermission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                    2);


            return;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void InitiateContacts(){
        int hasCallPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_SMS);
        if (hasCallPermission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_SMS},
                   5);


            return;
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void InitiateContact(){
        int hasCallPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS);
        if (hasCallPermission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    3);


            return;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void InitiateGPS(){
        int hasCallPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasCallPermission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);


            return;
        }else{

        }
    }
    private class LogLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location p1) {
            if (p1 != null) {
                myLatitude = p1.getLatitude();
                myLongitude = p1.getLongitude();

            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(mContext, provider.toUpperCase() + " is ENABLED!", Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onProviderDisabled(String provider) {
            if (!((Activity) mContext).isFinishing()) {
                if (networkCheck == false) {
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                    final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
//                    final String message = "GPS is not Enable\nEnable GPS for Proper Functioning";
//
//                    networkCheck = true;
//
//                    builder.setMessage(message)
//                            .setPositiveButton("Enable GPS in Settings",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface d, int id) {
//
//
//                                            mContext.startActivity(new Intent(action).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                                            d.dismiss();
//                                        }
//                                    })
//                            .setNegativeButton("Cancel",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface d, int id) {
//                                            d.cancel();
//                                        }
//                                    });
//                    builder.create().show();
                }

            }

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLogging(true);
                    try {

                            String urlAddress = "http://maps.google.com/maps?q="
                                    + myLatitude + "," + myLongitude + "("
                                    + "your Current Location" + ")&iwloc=A&hl=es";

                            Intent maps = new Intent(Intent.ACTION_VIEW, Uri
                                    .parse(urlAddress));
                            startActivity(maps);

                    } catch (Exception e) {

                        Toast.makeText(mContext, "Install this First", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(browserLink + "com.google.android.apps.maps")));

                    }
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    Toast.makeText(mContext,"My Location Feature may not work properly!!", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }

            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivity(new Intent(mContext, Navigation.class).putExtra("key", "earth"));
                    finish();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    Toast.makeText(mContext,"Earth Map Features may not work properly!", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
            case 3: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED&&
                        grantResults.length > 0
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    startActivity(new Intent(mContext, ViewFriendLocation.class));
                    finish();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    Toast.makeText(mContext,"View Friend Location Features may not work properly", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }

            case 4: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED&&
                        grantResults.length > 0
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    // set title
                    alertDialogBuilder.setTitle("share Your Location");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Location will be Shared through SMS\nCharges may Apply, share ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(FirstActivity.this);
                                    LayoutInflater inflater = getLayoutInflater();
                                    convertView = (View) inflater.inflate(R.layout.dialog, null);
                                    add_from_contacts = (Button) convertView.findViewById(R.id.add_from_contacts);
                                    add_from_callLog = (Button) convertView.findViewById(R.id.add_from_callLog);
                                    add_from_messages = (Button) convertView.findViewById(R.id.add_from_messages);
                                    manually_add = (Button) convertView.findViewById(R.id.manually_add);
                                    alertDialog.setView(convertView);
                                    alertdialog = alertDialog.show();

                                    add_from_contacts.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            alertdialog.dismiss();
                                            Intent intent = new Intent(getBaseContext(), Select_Contact.class);
                                            intent.putExtra("key", "contact");
                                            intent.putExtra("location", "share");
                                            intent.putExtra("myLatitude", myLatitude);
                                            intent.putExtra("myLongitude", myLongitude);

                                            startActivity(intent);


                                        }
                                    });

                                    add_from_callLog.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            alertdialog.dismiss();

                                            Intent intent = new Intent(getBaseContext(), Select_Contact.class);
                                            intent.putExtra("key", "callLog");
                                            intent.putExtra("location", "share");
                                            intent.putExtra("myLatitude", myLatitude);
                                            intent.putExtra("myLongitude", myLongitude);
                                            startActivity(intent);
                                        }
                                    });
                                    add_from_messages.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            alertdialog.dismiss();
                                            Intent intent = new Intent(getBaseContext(), Select_Contact.class);
                                            intent.putExtra("key", "messages");
                                            intent.putExtra("location", "share");
                                            intent.putExtra("myLatitude", myLatitude);
                                            intent.putExtra("myLongitude", myLongitude);
                                            startActivity(intent);
                                        }
                                    });
                                    manually_add.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertdialog.dismiss();
                                            manually_add();
                                        }
                                    });


                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                } else {

                    Toast.makeText(mContext,"Share Location Features may not work properly", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    boolean checkGps()
    {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            displayLocationSettingsRequest(FirstActivity.this);

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
                            status.startResolutionForResult(FirstActivity.this, REQUEST_CHECK_SETTINGS);
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_CHECK_SETTINGS:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        Log.i("iamindf", "RESULT_OK execute request.");
//
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Log.i("iamindf", "RESULT_CANCELED execute request.");
//
//
//                        break;
//                }
//                break;
//        }
//    }




}
