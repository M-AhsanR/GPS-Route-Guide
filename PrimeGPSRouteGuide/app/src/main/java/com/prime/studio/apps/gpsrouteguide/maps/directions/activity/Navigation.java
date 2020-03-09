package com.prime.studio.apps.gpsrouteguide.maps.directions.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.prime.studio.apps.gpsrouteguide.maps.directions.R;
import com.prime.studio.apps.gpsrouteguide.maps.directions.Utils.ConnectionDetector;
import com.prime.studio.apps.gpsrouteguide.maps.directions.Utils.DirectionsJSONParser;
import com.prime.studio.apps.gpsrouteguide.maps.directions.Utils.PermissionUtils;
import com.prime.studio.apps.gpsrouteguide.maps.directions.adapter.PlaceAutocompleteAdapter;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin20 on 10/18/2016.
 */
public class Navigation extends AppCompatActivity implements LocationListener,
        OnMapReadyCallback,GoogleMap.OnMapClickListener, GoogleApiClient.OnConnectionFailedListener,
        SensorEventListener{
Button mCurrentLocation, mGetPlace,up;
    android.support.v7.app.AlertDialog alertdialog;
    LinearLayout first,second;
    private PlaceAutocompleteAdapter mAdapterFrom;
    private PlaceAutocompleteAdapter mAdapterTo;
    private PlaceAutocompleteAdapter mAdapterSearch;
    protected GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    public static String SharedPrefName= "GPSRouteFinderZEE";
    private AutoCompleteTextView mAutocompleteViewFrom;
    private AutoCompleteTextView mAutocompleteViewTo;
    private AutoCompleteTextView mAutocompleteViewSearch;
    public static final String TAG = "Gps";
    private GoogleMap mMap;
    int mMode = 0;
    final int MODE_DRIVING = 0;
    final int MODE_BICYCLING = 1;
    final int MODE_WALKING = 2;
    LatLng mFromLoc,mToLoc;
    LatLng mTempFromLoc=null,mTempToLoc=null;
    String mToLocName;
   // private LogLocationListener logLocationListener;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    FloatingActionButton mNavigation;
    boolean mRouteDraw=false;
    LocationManager mLocationManager;
    private float[] mRotationMatrix = new float[16];
    float mDeclination;
    double angle;
    LinearLayout mRouteLayout,mSearchLayout,mFakeGps,mShare,mTopLocation,mDiscover,mShareRecLoc,mTrackingHistory;
    TextView mRouteTime,mRouteDistance;
    private Location location;
    boolean networkCheck = false;
    private boolean gpsok, networkok,checking = false;
    InterstitialAd mInterstitialAd;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
Context mContext;
    SQLiteHandler db;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
     //   MultiDex.install(this);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


       key= getIntent().getStringExtra("key");


       initialize();
        action();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
      //  logLocationListener = new LogLocationListener();
        if (mMap!=null)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission to access the location is missing.
                PermissionUtils.requestPermission(this, 1,
                        Manifest.permission.ACCESS_FINE_LOCATION, true);
            } else if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                boolean gpsIsEnabled = mLocationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean networkIsEnabled = mLocationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (networkIsEnabled) {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, 5000L, 100, (LocationListener) this);
                }
                if (gpsIsEnabled) {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 5000L, 100, (LocationListener) this);
                }
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                   mFromLoc= new LatLng(location.getLatitude(),location.getLongitude());


                }

              /*  mMap.animateCamera(CameraUpdateFactory.newLatLng(mFromLoc));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        mFromLoc.latitude, mFromLoc.longitude), 18));*/
            }
        }

    }

    @SuppressLint("RestrictedApi")
    private void initialize()
    {
        mContext= this;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        pref = getSharedPreferences(SharedPrefName, MODE_PRIVATE);
        editor = pref.edit();
        mNavigation= (FloatingActionButton) findViewById(R.id.fab);
        cd = new ConnectionDetector(getApplicationContext());

        db = new SQLiteHandler(getBaseContext());
//        up= (Button) findViewById(R.id.up);
//        up.setBackgroundResource(R.drawable.up_icon);
       // first= (LinearLayout) findViewById(R.id.layoutfirst);
      //  second= (LinearLayout) findViewById(R.id.layoutsecond);
        mRouteLayout= (LinearLayout) findViewById(R.id.route);
        mSearchLayout= (LinearLayout) findViewById(R.id.search);
      //  mFakeGps= (LinearLayout) findViewById(R.id.mFakeGps);
      //  mShare= (LinearLayout) findViewById(R.id.mShare);
       // mDiscover= (LinearLayout) findViewById(R.id.mDiscover);
      //  mTrackingHistory= (LinearLayout) findViewById(R.id.mTrackingHistory);
      //  mTopLocation= (LinearLayout) findViewById(R.id.mTopLocation);
      //  mShareRecLoc= (LinearLayout) findViewById(R.id.mShareRecLoc);
        mAutocompleteViewFrom = (AutoCompleteTextView) findViewById(R.id.loc_from);
        mAutocompleteViewTo = (AutoCompleteTextView) findViewById(R.id.loc_to);
        mAutocompleteViewSearch = (AutoCompleteTextView) findViewById(R.id.mSearch);
        mRouteTime = (TextView) findViewById(R.id.mRouteTime);
        mRouteDistance = (TextView) findViewById(R.id.mRouteDistane);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
       // logLocationListener = new LogLocationListener();

        if (key.equals("driving"))
        {
            if (mRouteLayout.getVisibility()==View.GONE)
            {
                mRouteLayout.setVisibility(View.VISIBLE);
            }
            if (mSearchLayout.getVisibility()==View.VISIBLE)
            {
                mSearchLayout.setVisibility(View.GONE);
            }
        }else if (key.equals("earth"))
        {
            if (mRouteLayout.getVisibility()==View.VISIBLE)
            {
                mRouteLayout.setVisibility(View.GONE);
            }
            if (mSearchLayout.getVisibility()==View.GONE)
            {
                mSearchLayout.setVisibility(View.VISIBLE);
            }
            if (mNavigation.getVisibility()==View.VISIBLE)
            {
                mNavigation.setVisibility(View.GONE);
            }
        }


        InitiateGPS();
        startLogging(true);
        if (isNetworkConnected()) {
            startLogging(true);
        } else {
            GetOldGPS();
        }


        ///////////////////Ads/////////////////////////
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
        requestNewInterstitial();
        //////////////////////////////
    }

    private void action()
    {
        mAdapterFrom = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAdapterTo = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAdapterSearch = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAutocompleteViewFrom.setAdapter(mAdapterFrom);
        mAutocompleteViewTo.setAdapter(mAdapterTo);
        mAutocompleteViewSearch.setAdapter(mAdapterSearch);
        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteViewFrom.setOnItemClickListener(mAutocompleteClickListenerFrom);
        mAutocompleteViewTo.setOnItemClickListener(mAutocompleteClickListenerTo);
        mAutocompleteViewSearch.setOnItemClickListener(mAutocompleteClickListenerSearch);
        mNavigation.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#16B7DD")));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                startActivity(new Intent(getBaseContext(), FirstActivity.class));
               finish();

            }
        });
        /*mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Download this Application from https://play.google.com/store/apps/details?id=com.zee.techno.apps.gps.route.finder.map");
                startActivity(Intent.createChooser(intent, "Share"));
            }
        });
*/

        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRouteDraw) {
                    if (mFromLoc != null && mToLoc != null) {
                   /* String url = "http://maps.google.com/maps?saddr=" + mFromLoc.latitude + ","
                            + mFromLoc.longitude + "&daddr=" + mToLoc.latitude + ","
                            + mToLoc.longitude;

                    Intent navigation = new Intent(Intent.ACTION_VIEW);
                    navigation.setData(Uri.parse(url));

                    startActivity(navigation);*/

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(mFromLoc));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                                mFromLoc.latitude, mFromLoc.longitude), 18));


                      /*  int trackinghistorysize=  pref.getInt("tracksize",0);
                        trackinghistorysize++;
                        String history= String.valueOf(mFromLoc.latitude)+","+String.valueOf(mFromLoc.longitude)+","+String.valueOf(mToLoc.latitude)+","+String.valueOf(mToLoc.longitude);
                        editor.putString("thistory"+trackinghistorysize,history);
                        editor.putInt("tracksize",trackinghistorysize);
                        editor.commit();


                        Log.d("historysize",String.valueOf(pref.getInt("tracksize",0)));*/

                        try {
                           /* address = geocoder.getFromLocation(myLatitude, myLongitude, 1);

                            final String address1 = address.get(0).getAddressLine(0);
                            final String city = address.get(0).getLocality();
                            final String Location = "" + address1 + " " + city;
                            */

                            if (mTempFromLoc==mFromLoc && mTempFromLoc==mFromLoc && mTempToLoc==mToLoc && mTempToLoc==mToLoc) {

                                Toast.makeText(getBaseContext(),
                                        "Location already saved",
                                        Toast.LENGTH_LONG).show();


                            }else {

                                mTempFromLoc = mFromLoc;
                                mTempToLoc = mToLoc;
                                String Final = "Location Saved on\n" + String.valueOf("Date:" + ListLocation.getDate(System.currentTimeMillis(), "dd/MM/yyyy") + " | Time:" + ListLocation.getDate(System.currentTimeMillis(), "hh:mm:ss"));

                              //  db.addTHistory(Final, String.valueOf(mFromLoc.latitude), String.valueOf(mFromLoc.longitude), String.valueOf(mToLoc.latitude), String.valueOf(mToLoc.longitude));


                                Toast.makeText(getBaseContext(),
                                        "Location saved to Tracking History",
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(),
                                    "Can't add to Favourites\nCheck you Internet",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }




                    }
                } else {
                    if (mToLoc != null && mFromLoc != null) {
                        if (mRouteLayout.getVisibility() == View.GONE) {
                            mAutocompleteViewTo.setText(mToLocName);
                            mRouteLayout.setVisibility(View.VISIBLE);
                            mSearchLayout.setVisibility(View.GONE);
                        }
                        LatLng origin = mFromLoc;
                        LatLng dest = mToLoc;

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                        mRouteDraw = true;
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(mFromLoc));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                                mFromLoc.latitude, mFromLoc.longitude), 18));

                      /*  int trackinghistorysize=  pref.getInt("trackinghistorysize",0);
                        trackinghistorysize++;
                        String history= String.valueOf(mFromLoc.latitude)+","+String.valueOf(mFromLoc.longitude)+","+String.valueOf(mToLoc.latitude)+","+String.valueOf(mToLoc.longitude);
                        editor.putString("thistory" + trackinghistorysize, history);
                        editor.putInt("tracksize", trackinghistorysize);
                        editor.commit();

                        Log.d("historysize", String.valueOf(pref.getInt("trackinghistorysize",0)));*/
                       /* Toast.makeText(MainActivity.this, String.valueOf(mRouteTime.getText()), Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, String.valueOf(mRouteDistance.getText()), Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this,"dfdfyy", Toast.LENGTH_SHORT).show();
                   */




                        try {
                           /* address = geocoder.getFromLocation(myLatitude, myLongitude, 1);

                            final String address1 = address.get(0).getAddressLine(0);
                            final String city = address.get(0).getLocality();
                            final String Location = "" + address1 + " " + city;
                            */
                            if (mTempFromLoc==mFromLoc && mTempFromLoc==mFromLoc && mTempToLoc==mToLoc && mTempToLoc==mToLoc) {

                                Toast.makeText(getBaseContext(),
                                        "Location already saved",
                                        Toast.LENGTH_LONG).show();


                            }else {

                                mTempFromLoc =mFromLoc;
                                mTempToLoc= mToLoc;
                                String Final = "Location Saved on\n" + String.valueOf("Date:" + ListLocation.getDate(System.currentTimeMillis(), "dd/MM/yyyy") + " | Time:" + ListLocation.getDate(System.currentTimeMillis(), "hh:mm:ss"));

                             //   db.addTHistory(Final, String.valueOf(mFromLoc.latitude), String.valueOf(mFromLoc.longitude), String.valueOf(mToLoc.latitude), String.valueOf(mToLoc.longitude));


                                Toast.makeText(getBaseContext(),
                                        "Location saved to Tracking History",
                                        Toast.LENGTH_LONG).show();
                            }
                                                    } catch (Exception e) {
                            Toast.makeText(getBaseContext(),
                                    "Can't add to Favourites\nCheck you Internet",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(Navigation.this, "Pleae enter Destination!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }



    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
        // mInterstitialAd2.loadAd(adRequest);
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
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 50, (LocationListener) mContext);
                return;
            }

        } catch (Exception e) {
            Toast.makeText(mContext, "GPS not Found", Toast.LENGTH_LONG).show();
        }

    }
    private void startLogging(boolean check) {
        if(check) {
            //checking if gps and network is ON.
            gpsok = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
           networkok = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "LOCATION PROVIDER PERMISSIONS NOT GRANTED", Toast.LENGTH_LONG).show();
                return;
            }
            checking = true;
            //checking every 100 miliSec and minDistance change 0
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Navigation.this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Navigation.this);
            if (gpsok && networkok) {
//                Toast.makeText(this, "GPS and NETWORK PROVIDER Found!!", Toast.LENGTH_SHORT).show();
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
             location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    mFromLoc= new LatLng(location.getLatitude(),location.getLongitude());


                }

            } else {
                Toast.makeText(this, "LOCATION PROVIDER NOT AVAILABLE", Toast.LENGTH_LONG).show();
            }
        }else {
            mLocationManager.removeUpdates(Navigation.this);
            checking = false;
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void InitiateGPS(){
        int hasCallPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasCallPermission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);


            return;
        }else{

        }
    }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            if (mRouteLayout.getVisibility()==View.VISIBLE)
            {
                mRouteLayout.setVisibility(View.GONE);
            }
            if (mSearchLayout.getVisibility()==View.GONE)
            {
                mSearchLayout.setVisibility(View.VISIBLE);
            }
            return true;
        }
        if (id == R.id.navigation) {
            if (mRouteLayout.getVisibility()==View.GONE)
            {
                mRouteLayout.setVisibility(View.VISIBLE);
            }
            if (mSearchLayout.getVisibility()==View.VISIBLE)
            {
                mSearchLayout.setVisibility(View.GONE);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


    private AdapterView.OnItemClickListener mAutocompleteClickListenerFrom
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapterFrom.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackFrom);

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);



        }
    };
    private AdapterView.OnItemClickListener mAutocompleteClickListenerTo
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapterTo.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackTo);

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };
    private AdapterView.OnItemClickListener mAutocompleteClickListenerSearch
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapterSearch.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackSearch);

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackSearch
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            mMap.clear();
            mRouteDraw=false;
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            mToLoc= place.getLatLng();
            mToLocName= place.getAddress().toString();
            MarkerOptions optionsTo = new MarkerOptions();
            optionsTo.position(place.getLatLng());
            optionsTo.icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.pin_start));

            optionsTo.title("To");

            mMap.addMarker(optionsTo);

            mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    place.getLatLng().latitude,  place.getLatLng().longitude), 15));
            places.release();
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackFrom
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
          /*  mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()));
*/
          //  mAutocompleteView.setText(place.getName());
            // Display the third party attributions if set.
          /*  final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }
*/


            mFromLoc= place.getLatLng();
            // Checks, whether start and end locations are captured
            if (mToLoc!=null&& mFromLoc!=null) {
                LatLng origin = mFromLoc;
                LatLng dest = mToLoc;

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
                mRouteDraw=true;
            }


            mMap.clear();
            // Creating MarkerOptions
            MarkerOptions optionsTo = new MarkerOptions();
            // Marker makre;

            // Setting the position of the marker
            optionsTo.position(mToLoc);
            optionsTo.icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.pin_start));
            // Marker hamburg = map.addMarker(new
            // MarkerOptions().title("Hamburg"));
            optionsTo.title("To");
            // Add new marker to the Google Map Android API V2
            mMap.addMarker(optionsTo);
            // Creating MarkerOptions
            MarkerOptions optionsFrom = new MarkerOptions();
            // Marker makre;

            // Setting the position of the marker
            optionsFrom.position(mFromLoc);
            optionsFrom.icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.pin_start));
            // Marker hamburg = map.addMarker(new
            // MarkerOptions().title("Hamburg"));
            optionsFrom.title("From");
            // Add new marker to the Google Map Android API V2
            mMap.addMarker(optionsFrom);
            Log.i(TAG, "Place details received: " + place.getName());
            try {
                LatLngBounds.Builder bc = new LatLngBounds.Builder();

                bc.include(mToLoc);
                bc.include(mFromLoc);
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 200));
            } catch (Exception e) {
                e.printStackTrace();
            }
            places.release();
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackTo
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
          /*  mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()));
*/
            //  mAutocompleteView.setText(place.getName());
            // Display the third party attributions if set.
          /*  final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }
*/
            mToLoc= place.getLatLng();




            // Checks, whether start and end locations are captured
            if (mToLoc!=null&& mFromLoc!=null) {



                LatLng origin = mFromLoc;
                LatLng dest = mToLoc;

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
                mRouteDraw = true;


                mMap.clear();
                // Creating MarkerOptions
                MarkerOptions optionsTo = new MarkerOptions();
                // Marker makre;

                // Setting the position of the marker
                optionsTo.position(place.getLatLng());
                optionsTo.icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.pin_start));
                // Marker hamburg = map.addMarker(new
                // MarkerOptions().title("Hamburg"));
                optionsTo.title("To");
                // Add new marker to the Google Map Android API V2
                mMap.addMarker(optionsTo);
                // Creating MarkerOptions
                MarkerOptions optionsFrom = new MarkerOptions();
                // Marker makre;

                // Setting the position of the marker
                optionsFrom.position(mFromLoc);
                optionsFrom.icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.pin_start));
                // Marker hamburg = map.addMarker(new
                // MarkerOptions().title("Hamburg"));
                optionsFrom.title("To");
                // Add new marker to the Google Map Android API V2
                mMap.addMarker(optionsFrom);
                Log.i(TAG, "Place details received: " + place.getName());
                try {
                    LatLngBounds.Builder bc = new LatLngBounds.Builder();

                    bc.include(mToLoc);
                    bc.include(mFromLoc);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 200));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            places.release();
        }
    };
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
            }

        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            boolean gpsIsEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkIsEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (networkIsEnabled) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 5000L, 100, (LocationListener) this);
            }
            if (gpsIsEnabled) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 5000L, 100, (LocationListener) this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (mMap!=null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                    location.getLatitude(), location.getLongitude())));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    location.getLatitude(), location.getLongitude()), 15));
            //  mFromLoc= new LatLng(location.getLatitude(), location.getLongitude());

            GeomagneticField field = new GeomagneticField(
                    (float) location.getLatitude(),
                    (float) location.getLongitude(),
                    (float) location.getAltitude(),
                    System.currentTimeMillis()
            );

            // getDeclination returns degrees
            mDeclination = field.getDeclination();
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

        try {


            if (networkCheck == false) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Navigation.this);
                final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                final String message = "GPS is not Enable\nEnable GPS for Proper Functioning";

                networkCheck = true;

                builder.setMessage(message)
                        .setPositiveButton("Enable GPS in Settings",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {


                                        startActivity(new Intent(action).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                        d.dismiss();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {
                                        d.cancel();
                                    }
                                });
                builder.create().show();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix, event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);
            if (Math.abs(Math.toDegrees(orientation[0]) - angle) > 0.8) {
                float bearing = (float) Math.toDegrees(orientation[0]) + mDeclination;
                updateCamera(bearing);
            }
            angle = Math.toDegrees(orientation[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private void updateCamera(float bearing) {
        CameraPosition oldPos = mMap.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            try {
                super.onPostExecute(result);

                ParserTask parserTask = new ParserTask();

                // Invokes the thread for parsing the JSON data
                parserTask.execute(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception download url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private void showAlertDialog(Context context, String title, String message,
                                 boolean status) {
        // TODO Auto-generated method stub
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        // alertDialog.setIcon((status);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        isInternetPresent = cd.isConnectingToInternet();
        String url = null;

        String str_origin = "origin=" + origin.latitude + ","
                + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Travelling Mode
        String mode = "mode=driving";
        if (isInternetPresent) {
            mMode = 0;
           /* if (rbDriving.isChecked()) {
                mode = "mode=driving";
                mMode = 0;
            } else if (rbWalking.isChecked()) {
                mode = "mode=walking";
                mMode = 2;
            }*/
        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(Navigation.this, "No Internet Connection",
                    "You don't have internet connection.", false);
        }

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
                + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        url = "https://maps.googleapis.com/maps/api/directions/" + output + "?"
                + parameters;
        return url;

        // Origin of route

    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            try {
                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;
                MarkerOptions markerOptions = new MarkerOptions();
                String distance = "";
                String duration = "";

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        if (j == 0) { // Get distance from the list
                            distance = (String) point.get("distance");
                            continue;
                        } else if (j == 1) { // Get duration from the list
                            duration = (String) point.get("duration");
                            continue;
                        }

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(2);


                    // Changing the color polyline according to the mode
                    if (mMode == MODE_DRIVING) {
                        lineOptions.color(Color.BLUE);
                        // lineOptions.getWidth();
                        lineOptions.width((float) 20);

                    } else if (mMode == MODE_WALKING) {
                        lineOptions.color(Color.GREEN);
                        lineOptions.width((float) 5.5);
                    }
                }// loop end

                if (result.size() < 1) {
                    Toast.makeText(getBaseContext(), "No Points",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);
              /*  mRouteTime.setText("Time= " + duration);
                mRouteDistance.setText("Distance= " + distance);
*/

               /* tvDistanceDuration.setText("Distance= " + distance + "   Time="
                        + duration);
*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }// end function
    }

    @Override
    public void onBackPressed() {
        if (mRouteDraw)
        {
            mRouteDraw=false;
            mMap.clear();
            mAutocompleteViewSearch.setText("");
            mAutocompleteViewTo.setText("");
            mAutocompleteViewFrom.setText("");
            mRouteDistance.setText("");
            mRouteTime.setText("");
            mToLoc=null;

        }else if (mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }else {
        startActivity(new Intent(getBaseContext(),FirstActivity.class));
            finish();
    }
    }


    private class LogLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location p1) {
            if (p1 != null) {
                mFromLoc= new LatLng(p1.getLatitude(),p1.getLongitude());


            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(Navigation.this, provider.toUpperCase() + " is ENABLED!", Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onProviderDisabled(String provider) {
            if (!((Activity) mContext).isFinishing()) {
                if (networkCheck == false) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                    final String message = "GPS is not Enable\nEnable GPS for Proper Functioning";

                    networkCheck = true;

                    builder.setMessage(message)
                            .setPositiveButton("Enable GPS in Settings",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int id) {


                                            startActivity(new Intent(action).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                            d.dismiss();
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int id) {
                                            d.cancel();
                                        }
                                    });
                    builder.create().show();
                }

            }

        }

    }
}
