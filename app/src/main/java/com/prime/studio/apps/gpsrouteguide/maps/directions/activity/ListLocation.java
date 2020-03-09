package com.prime.studio.apps.gpsrouteguide.maps.directions.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;
import android.widget.Toast;


import com.prime.studio.apps.gpsrouteguide.maps.directions.R;
import com.prime.studio.apps.gpsrouteguide.maps.directions.adapter.MyExpandableAdapter;
import com.prime.studio.apps.gpsrouteguide.maps.directions.model.Child;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by A S Computers on 01-Jun-16.
 */
public class ListLocation extends AppCompatActivity {
    Geocoder geocoder;
    List<Address> address;
    boolean networkCheck = false;
    private int state;
    private boolean test_is_set, gpsok, networkok, checking = false;
    private LocationManager lm;
    private LogLocationListener logLocationListener;
    private Handler handler;
    String Final;
    private Location location;
    SQLiteHandler db;
    public static double TrLat,TrLng;
    private ExpandableListView listview;
    ArrayList<HashMap<String, String>> list ;
    double myLatitude,myLongitude;
    String TempLat,TempLng;
    private ArrayList<Object> childItems = new ArrayList<Object>();
//    InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listlocation);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //////////////ads////////////////////
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
//        requestNewInterstitial();

//        AdView adView = (AdView) this.findViewById(R.id.adView);
//        AdRequest stlit_adRequest = new AdRequest.Builder().build();
//        adView.loadAd(stlit_adRequest);
        //////////////End Ads////////////////

        Bundle gotBasket = getIntent().getExtras();
        String number = gotBasket.getString("Number");
        db = new SQLiteHandler(getApplicationContext());
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        startLogging(true);


        list = new ArrayList<HashMap<String, String>>();

       /* ListAdapter adapter = new SimpleAdapter(
                ListLocation.this, list,
                R.layout.list_locations, new String[]{"Display", "Location"},
                new int[]{R.id.textView, R.id.textView2});*/
        // updating listview

        listview = (ExpandableListView) findViewById(R.id.List);
       // setChildData();
        // Create the Adapter
        MyExpandableAdapter adapter = new MyExpandableAdapter(list, childItems);

        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);

        // Set the Adapter to expandableList
        listview.setAdapter(adapter);
        listview.setEnabled(true);

        listview.setAdapter(adapter);


        /*listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {


                final AlertDialog.Builder builder = new AlertDialog.Builder(ListLocation.this);
                builder.setTitle("Select the Option");

                builder.setItems(new CharSequence[]
                                {"View Location", "Route to Friend", "share", "Add to Favourite", "Cancel"},

                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    case 0:
                                        String info = ((TextView) view.findViewById(R.id.textView2)).getText().toString();
                                        info = info.replace("Download this Application to view Your Friend's Location http://tinyurl.com/hvsdgz8\n", "");
                                        String[] parts = info.split(";");
                                        Map<String, String> hashMap = new HashMap<String, String>();

                                        for (String s : parts) {
                                            String[] keyVal = s.trim().split("=");
                                            hashMap.put(keyVal[0], keyVal[1]);
                                            Log.e("sms", "Check " + keyVal[0] + "=" + keyVal[1]);
                                            if (keyVal[0].equals("Latitude")) {
                                                TempLat = keyVal[1];
                                            } else if (keyVal[0].equals("Longitude")) {
                                                TempLng = keyVal[1];
                                            }
                                        }
                                        BigDecimal hh = new BigDecimal(TempLat);
                                        BigDecimal mm = new BigDecimal(TempLng);


                                        myLatitude = hh.doubleValue();
                                        myLongitude = mm.doubleValue();

                                        String urlAddress = "http://maps.google.com/maps?q="
                                                + myLatitude + "," + myLongitude + "("
                                                + "Your Friend's Location" + ")&iwloc=A&hl=es";

                                        Intent maps = new Intent(Intent.ACTION_VIEW, Uri
                                                .parse(urlAddress));
                                        startActivity(maps);


                                        break;
                                    case 1:
                                        String info2 = ((TextView) view.findViewById(R.id.textView2)).getText().toString();

                                        info2 = info2.replace("Download this Application to view Your Friend's Location http://tinyurl.com/hvsdgz8\n", "");
                                        String[] parts2 = info2.split(";");
                                        Map<String, String> hashMap2 = new HashMap<String, String>();

                                        for (String s : parts2) {
                                            String[] keyVal = s.trim().split("=");
                                            hashMap2.put(keyVal[0], keyVal[1]);
                                            Log.e("sms", "Check " + keyVal[0] + "=" + keyVal[1]);
                                            if (keyVal[0].equals("Latitude")) {
                                                TempLat = keyVal[1];
                                            } else if (keyVal[0].equals("Longitude")) {
                                                TempLng = keyVal[1];
                                            }
                                        }
                                        BigDecimal hh2 = new BigDecimal(TempLat);
                                        BigDecimal mm2 = new BigDecimal(TempLng);


                                        myLatitude = hh2.doubleValue();
                                        myLongitude = mm2.doubleValue();
                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?saddr=" + TrLat + "," + TrLng + "&daddr=" + myLatitude + "," + myLongitude));
                                        startActivity(intent);
                                        break;
                                    case 2:

                                        if (ActivityCompat.checkSelfPermission(ListLocation.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ListLocation.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                                            InitiateSMS();

                                            return;
                                        }if (ActivityCompat.checkSelfPermission(ListLocation.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ListLocation.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                                        InitiateContact();

                                        return;
                                    }


                                    else {

                                        Bundle basket = new Bundle();
                                        basket.putDouble("Lat", myLatitude);
                                        basket.putDouble("Lng", myLongitude);
                                        Intent intent1 = new Intent(getApplicationContext(), ShareToFNF.class);
                                        intent1.putExtras(basket);
                                        startActivity(intent1);
                                    }
                                        break;
                                    case 3:
                                        String info3 = ((TextView) view.findViewById(R.id.textView2)).getText().toString();

                                        info3 = info3.replace("Download this Application to view Your Friend's Location http://tinyurl.com/hvsdgz8\n", "");
                                        String[] parts3 = info3.split(";");
                                        Map<String, String> hashMap3 = new HashMap<String, String>();

                                        for (String s : parts3) {
                                            String[] keyVal = s.trim().split("=");
                                            hashMap3.put(keyVal[0], keyVal[1]);
                                            Log.e("sms", "Check " + keyVal[0] + "=" + keyVal[1]);
                                            if (keyVal[0].equals("Latitude")) {
                                                TempLat = keyVal[1];
                                            } else if (keyVal[0].equals("Longitude")) {
                                                TempLng = keyVal[1];
                                            }
                                        }
                                        BigDecimal hh3 = new BigDecimal(TempLat);
                                        BigDecimal mm3 = new BigDecimal(TempLng);


                                        myLatitude = hh3.doubleValue();
                                        myLongitude = mm3.doubleValue();


                                        try {
                                            address = geocoder.getFromLocation(myLatitude, myLongitude, 1);

                                            final String address1 = address.get(0).getAddressLine(0);
                                            final String city = address.get(0).getLocality();
                                            final String Location = "Address : " + address1 + " " + city;
                                            Final = Location + "\n" + ((TextView) view.findViewById(R.id.textView)).getText().toString();
                                            db.addUser(Final, String.valueOf(myLatitude), String.valueOf(myLongitude));


                                            Toast.makeText(getApplicationContext(),
                                                    "Location saved to Favourite",
                                                    Toast.LENGTH_LONG).show();
                                        } catch (IOException e) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Can't add to Favourites\nCheck you Internet",
                                                    Toast.LENGTH_LONG).show();
                                            e.printStackTrace();
                                        }


                                        break;
                                    case 4:


                                        break;
                                }
                            }
                        });
                builder.create().show();


            }
        });
*/

        PrintWriter lmFout_sms_inbox = null;
        ContentResolver cr = getContentResolver();
        Uri SMSinbox = Uri.parse("content://sms/inbox");
        Cursor cursor_sms_inbox = cr.query(
                //SMS_INBOX_CONTENT_URI,
                SMSinbox,
                new String[]{"_id", "thread_id", "address", "person", "date", "body"},
                null,
                null,
                null);
        //int count = 0;
        if (cursor_sms_inbox != null) {
            //count = cursor_sms.getCount();
            while (cursor_sms_inbox.moveToNext()) {
                long messageId = cursor_sms_inbox.getLong(0);
                //long threadId = cursor_sms.getLong(1);
                String address = cursor_sms_inbox.getString(2);
                long contactId = cursor_sms_inbox.getLong(3);
                //String contactId_string = String.valueOf(contactId);
                long timestamp = cursor_sms_inbox.getLong(4);
                String body = cursor_sms_inbox.getString(5);

// Anywhere in string
                HashMap<String, String> map = new HashMap<String, String>();

                boolean b = (body.indexOf("Latitude=") > 0 && body.indexOf("tinyurl.com/y8l2g394") > 0);


                if (b) {


                    if (address.equals(number)) {
                        map.put("Location", body);
                        map.put("Display", String.valueOf("Date:"+ getDate(timestamp, "dd/MM/yyyy")+" | Time:"+ getDate(timestamp, "hh:mm:ss")));
                        list.add(map);
                    }


                }
                String newRow = "msgId:" + messageId + ";contctId:" + contactId + ";body:" + body;
                if (timestamp != 0) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy_HH/mm/ss");
                    formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timestamp);
                    String formatted = formatter.format(calendar.getTime());
                    newRow += ";time:" + formatted;
                }

                if (address != null) {
                    newRow += ";address:" + address;
                }

                if (lmFout_sms_inbox != null) {
                    lmFout_sms_inbox.println(newRow);
                }

            }
        }
for (int i=0; i<list.size();i++)
{
    setChildData();
}
        adapter.notifyDataSetChanged();
    }
    public void setChildData() {

        // Add Child Items for Fruits
        ArrayList<Child> child = new ArrayList<Child>();
        child.add(new Child(R.drawable.view,getString(R.string.viewlocation)));
        child.add(new Child(R.drawable.route,getString(R.string.routetofriend)));
        child.add(new Child(R.drawable.share,getString(R.string.share)));
        child.add(new Child(R.drawable.favorite,getString(R.string.addtofavourite)));

        childItems.add(child);
    }
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    private void startLogging(boolean check) {
        if(check) {
            test_is_set = false;
            lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            logLocationListener = new LogLocationListener();

            handler = new Handler(getApplicationContext().getMainLooper());
            //noinspection deprecation
            //checking if gps and network is ON.
            gpsok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkok = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "LOCATION PROVIDER PERMISSIONS NOT GRANTED", Toast.LENGTH_LONG).show();
                return;
            }
            checking = true;
            //checking every 100 miliSec and minDistance change 0
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, logLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, logLocationListener);
            if (gpsok && networkok) {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    TrLat = location.getLatitude();
                    TrLng = location.getLongitude();

                }

            } else {
                Toast.makeText(getApplicationContext(), "LOCATION PROVIDER NOT AVAILABLE", Toast.LENGTH_LONG).show();
            }
        }else {
            lm.removeUpdates(logLocationListener);
            checking = false;
        }
    }


    private class LogLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location p1) {
            if (p1 != null) {
                TrLat = p1.getLatitude();
                TrLng = p1.getLongitude();

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
            if(networkCheck==false) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ListLocation.this);
                final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                final String message = "GPS is not Enable\nEnable GPS for Proper Functioning";
                networkCheck=true;

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


        }}

    @TargetApi(Build.VERSION_CODES.M)
    public  void InitiateSMS(){
        int hasCallPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS);
        if (hasCallPermission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                    1);


            return;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void InitiateContact(){
        int hasCallPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);
        if (hasCallPermission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    1);


            return;
        }
    }}