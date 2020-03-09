package com.prime.studio.apps.gpsrouteguide.maps.directions.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.prime.studio.apps.gpsrouteguide.maps.directions.R;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import snow.skittles.BaseSkittle;
import snow.skittles.SkittleBuilder;
import snow.skittles.SkittleLayout;
import snow.skittles.TextSkittle;


public class Fav extends AppCompatActivity implements LocationListener, SkittleBuilder.OnSkittleClickListener {
    InterstitialAd mInterstitialAd;
    SQLiteHandler db;
    private LocationManager lm;
    private boolean gpsok, networkok, checking = false;
    Double myLongitude, myLatitude;
    private Location location;
    boolean networkCheck = false;
    List<Address> address;
    Geocoder geocoder;
    String Final;
    ArrayList productsList;
    ListAdapter adapter;
    ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fav);
        lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(this, Locale.getDefault());

        InitiateGPS();

        if (isNetworkConnected()) {
            startLogging(true);
        } else {
            GetOldGPS();
        }
        final SkittleBuilder skittleBuilder =
                SkittleBuilder.newInstance((SkittleLayout) findViewById(R.id.skittleLayout), false);

       /* skittleBuilder.addSkittle(Skittle.newInstance(ContextCompat.getColor(this, R.color.barratheon),
                ContextCompat.getDrawable(this, R.drawable.barratheon_icon)));*/
        skittleBuilder.addSkittle(
                new TextSkittle.Builder("     Add from Friend's Location     ", ContextCompat.getColor(this, R.color.blue),
                        ContextCompat.getDrawable(this, R.drawable.skittle_other)).setTextBackground(
                        ContextCompat.getDrawable(this, R.drawable.rounded_corner)).build());
        skittleBuilder.addSkittle(
                new TextSkittle.Builder("     Add Current Location     ", ContextCompat.getColor(this, R.color.blue),
                        ContextCompat.getDrawable(this, R.drawable.skittle_other)).setTextBackground(
                        ContextCompat.getDrawable(this, R.drawable.rounded_corner)).build());
        // ContextCompat.getColor(this, R.color.teal_500)).build());

        skittleBuilder.setSkittleClickListener(this);


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
//        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    startActivity(new Intent(Fav.this, FirstActivity.class));
                    finish();
                }
            }
        });

        // getSupportActionBar().setIcon(R.drawable.ic_launcher);
        //    getSupportActionBar().setTitle(getString(R.string.favourite_places));
        // toolbar.setTitle(getString(R.string.favourite_places));
////////////////ads//////////////////////////

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
        requestNewInterstitial();
       /* AdView adView = (AdView) this.findViewById(R.id.adView);
        AdRequest stlit_adRequest = new AdRequest.Builder().build();
        adView.loadAd(stlit_adRequest);*/

        //////////////end ads///////////////////
        /*AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        db = new SQLiteHandler(getApplicationContext());


        if (db.getRowCount() > 0) {

        }

      /* Button bt = (Button)findViewById(R.id.button3);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteUsers();
                Toast.makeText(getApplicationContext(),"Favourites Cleared",Toast.LENGTH_LONG).show();
                startActivity(getIntent());
                finish();

            }
        });
        if(db.getRowCount()>0){
            bt.setEnabled(true);
        }*/
        productsList = new ArrayList<HashMap<String, String>>();
        productsList = db.GetData();
        lv = (ListView) findViewById(R.id.list);

        adapter = new SimpleAdapter(
                Fav.this, productsList,
                R.layout.list_item2, new String[]{"ID", "Name", "Lat1", "Lng1"
        },
                new int[]{R.id.number, R.id.name, R.id.LAT1, R.id.LNG1});
        // updating listview
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String LAT1 = ((TextView) view.findViewById(R.id.LAT1)).getText().toString();
                String LNG1 = ((TextView) view.findViewById(R.id.LNG1)).getText().toString();


                BigDecimal a = new BigDecimal(LAT1);
                BigDecimal b = new BigDecimal(LNG1);


                Double lat1, lng1;
                lat1 = a.doubleValue();
                lng1 = b.doubleValue();

                String urlAddress = "http://maps.google.com/maps?q="
                        + lat1 + "," + lng1 + "("
                        + "Your Friend's Location" + ")&iwloc=A&hl=es";

                Intent maps = new Intent(Intent.ACTION_VIEW, Uri
                        .parse(urlAddress));
                startActivity(maps);
                // Starting new intent


            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {


                new AlertDialog.Builder(Fav.this)
                        .setTitle(R.string.confirm)
                        .setMessage(R.string.confirm_delte)
                        .setPositiveButton(R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {

                                        String ID = ((TextView) view.findViewById(R.id.number)).getText().toString();
                                        db.deleteSingleRow(Integer.parseInt(ID));
                                        Toast.makeText(getApplicationContext(), "Favourites deleted", Toast.LENGTH_LONG).show();
                                        startActivity(getIntent());
                                        finish();


                                    }
                                })
                        .setNegativeButton(R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                    }
                                }).show();


                return true;
            }
        });
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                startActivity(new Intent(Fav.this, FirstActivity.class));
                finish();

            }
        });
    }

    ;

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            startActivity(new Intent(Fav.this, FirstActivity.class));
            finish();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clear_all) {
            db.deleteUsers();
            Toast.makeText(getApplicationContext(), "Favourites Cleared", Toast.LENGTH_LONG).show();
            startActivity(getIntent());
            finish();
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSkittleClick(BaseSkittle skittle, int position) {
      /*  Snackbar.make(findViewById(R.id.skittle_container), "This is a snackbar", Snackbar.LENGTH_SHORT)
                .show();*/
        //  Toast.makeText(Fav.this, "Clicked at posiion: " + position, Toast.LENGTH_SHORT).show();

        switch (position) {
            case 0:


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

                        InitiateContacts();

                        return;
                    }
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                        InitiateContact();

                        return;
                    } else {

                        startActivity(new Intent(Fav.this, ViewFriendLocation.class).putExtra("key", true));
                        finish();
                    }

                }
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {

                    startActivity(new Intent(Fav.this, ViewFriendLocation.class).putExtra("key", true));
                    finish();
                }


                break;
            case 1:


                if (myLatitude == null) {

                    final ProgressDialog progress = new ProgressDialog(Fav.this);
                    progress.setTitle("Connecting to GPS\nStay outside for better Results");
                    progress.setMessage("Getting Your Location...");
                    progress.setCancelable(false);
                    progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            progress.cancel();

                            dialog.dismiss();
                        }
                    });
                    progress.show();
                    if (ActivityCompat.checkSelfPermission(Fav.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Fav.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                       //   Toast.makeText(Fav.this, "LOCATION PROVIDER PERMISSIONS NOT GRANTED", Toast.LENGTH_LONG).show();
                        progress.cancel();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    0);
                        }

                        return;
                    }
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, (LocationListener) Fav.this);

                    final Handler handler = new Handler();
                    Runnable task = new Runnable() {
                        @Override
                        public void run() {

                            if (ActivityCompat.checkSelfPermission(Fav.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Fav.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                InitiateGPS();
                                return;
                            }else {
                                if (lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                                    myLatitude = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                                    myLongitude = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();

                                    try {
                                        address = geocoder.getFromLocation(myLatitude, myLongitude, 1);

                                        final String address1 = address.get(0).getAddressLine(0);
                                        final String city = address.get(0).getLocality();
                                        final String Location = "" + address1 + " " + city;
                                        Final = Location + "\n" + " Location Saved on\n" + String.valueOf("Date:" + ListLocation.getDate(System.currentTimeMillis(), "dd/MM/yyyy") + " | Time:" + ListLocation.getDate(System.currentTimeMillis(), "hh:mm:ss"));
                                        db.addUser(Final, String.valueOf(myLatitude), String.valueOf(myLongitude));


                                        Toast.makeText(Fav.this,
                                                "Location saved to Favourite",
                                                Toast.LENGTH_LONG).show();

                                        productsList = db.GetData();
                                        adapter = new SimpleAdapter(
                                                Fav.this, productsList,
                                                R.layout.list_item2, new String[]{"ID", "Name", "Lat1", "Lng1"
                                        },
                                                new int[]{R.id.number, R.id.name, R.id.LAT1, R.id.LNG1});
                                        lv.setAdapter(adapter);
                                    } catch (IOException e) {
                                        Toast.makeText(Fav.this, "Can't add to Favourites\nCheck you Internet",
                                                Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }


                                    progress.cancel();
                                    handler.removeCallbacks(this);
                                } else {

                                    handler.postDelayed(this, 3000);
                                }
                            }


                            }

                        };
                        handler.post(task);


                    } else {

                        try {
                            address = geocoder.getFromLocation(myLatitude, myLongitude, 1);

                            final String address1 = address.get(0).getAddressLine(0);
                            final String city = address.get(0).getLocality();
                            final String Location = "" + address1 + " " + city;
                            Final = Location + "\n"+  " Location Saved on\n" + String.valueOf("Date:" + ListLocation.getDate(System.currentTimeMillis(), "dd/MM/yyyy") + " | Time:" + ListLocation.getDate(System.currentTimeMillis(), "hh:mm:ss"));
                            db.addUser(Final, String.valueOf(myLatitude), String.valueOf(myLongitude));


                            Toast.makeText(Fav.this,
                                    "Location saved to Favourite",
                                    Toast.LENGTH_LONG).show();
                            productsList = db.GetData();
                            adapter = new SimpleAdapter(
                                    Fav.this, productsList,
                                    R.layout.list_item2, new String[] { "ID","Name","Lat1","Lng1"
                            },
                                    new int[] { R.id.number, R.id.name,R.id.LAT1,R.id.LNG1 });
                            lv.setAdapter(adapter);
                        } catch (IOException e) {
                            Toast.makeText(Fav.this, "Can't add to Favourites\nCheck you Internet",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                break;


        }
    }

    @Override
    public void onMainSkittleClick() {

    }



    @TargetApi(Build.VERSION_CODES.M)
    private void InitiateContacts(){
        int hasCallPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if (hasCallPermission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_SMS},
                    1);


            return;
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void InitiateContact(){
        int hasCallPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (hasCallPermission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    3);


            return;
        }
    }
    private void startLogging(boolean check) {
        if(check) {
            //checking if gps and network is ON.
            gpsok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkok = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (ActivityCompat.checkSelfPermission(Fav.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Fav.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
              //  Toast.makeText(Fav.this, "LOCATION PROVIDER PERMISSIONS NOT GRANTED", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            0);
                }

                return;
            }
            checking = true;
            //checking every 100 miliSec and minDistance change 0
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Fav.this);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Fav.this);
            if (gpsok) {
//                Toast.makeText(Fav.this, "GPS and NETWORK PROVIDER Found!!", Toast.LENGTH_SHORT).show();
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    myLatitude = location.getLatitude();
                    myLongitude = location.getLongitude();

                }

            } else {
//                Toast.makeText(Fav.this, "LOCATION PROVIDER NOT AVAILABLE", Toast.LENGTH_LONG).show();
            }
        }else {
            lm.removeUpdates(Fav.this);
            checking = false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            myLatitude = location.getLatitude();
            myLongitude = location.getLongitude();

        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(Fav.this, provider.toUpperCase() + " is ENABLED!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        if(!((Activity) Fav.this).isFinishing()) {
            if (networkCheck == false) {
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Fav.this);
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

    @TargetApi(Build.VERSION_CODES.M)
    private void InitiateGPS(){
        int hasCallPermission = ContextCompat.checkSelfPermission(Fav.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasCallPermission != PackageManager.PERMISSION_GRANTED &&  ContextCompat.checkSelfPermission(Fav.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED ){
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);


            return;
        }else{

        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) Fav.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void GetOldGPS() {
        try {

            if (ActivityCompat.checkSelfPermission(Fav.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Fav.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 50,  Fav.this);
                return;
            }

        } catch (Exception e) {
            Toast.makeText(Fav.this, "GPS not Found", Toast.LENGTH_LONG).show();
        }

    }



}
