package com.prime.studio.apps.gpsrouteguide.maps.directions.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


import com.prime.studio.apps.gpsrouteguide.maps.directions.R;
import com.prime.studio.apps.gpsrouteguide.maps.directions.adapter.SelectContactAdapter;
import com.prime.studio.apps.gpsrouteguide.maps.directions.model.Contact_Model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by Asad on 5/26/2016.
 */
public class Select_Contact extends AppCompatActivity implements LocationListener {
    ListView phone_no_list;
    LinearLayout select_all_contact, unselect_all_contact;
    Toolbar toolbar;
    String key;
    ArrayList<Contact_Model> aa = new ArrayList<Contact_Model>();
    SelectContactAdapter adapter;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    EditText search;
    private LocationManager lm;
    Double myLongitude, myLatitude;
    String Msg;
    String location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_contact);
        initialize();
        action();

    }



    private void initialize() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sp = getSharedPreferences("Call_Recording", Context.MODE_PRIVATE);
        editor = sp.edit();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        phone_no_list = (ListView) findViewById(R.id.phone_no_listt);
        select_all_contact = (LinearLayout) findViewById(R.id.select_all);
        unselect_all_contact = (LinearLayout) findViewById(R.id.unselect_all);
        search= (EditText) findViewById(R.id.search);
        key = getIntent().getStringExtra("key");
        location = getIntent().getStringExtra("location");
        myLatitude = getIntent().getDoubleExtra("myLatitude",0);
        myLongitude = getIntent().getDoubleExtra("myLongitude",0);

        if (location.equals("share")) {
            Msg = "Download this Application to view Your Friend's Location http://tinyurl.com/y8l2g394\nLatitude=" + myLatitude + ";Longitude=" + myLongitude;

        }else {
            Msg = "Location Request:\n  I am looking for you, download this Application to send Location http://tinyurl.com/y8l2g394\n";
        }
    }

    private void action() {
        if (key.equals("contact")) {
            aa.clear();
            if (isContactPermissionGranted()) {
                getNumberfromContacts(getContentResolver());
            }
            toolbar.setTitle(getString(R.string.selectfromcontact));
        } else if (key.equals("callLog")) {
            aa.clear();
            if (    isCallLogPermissionGranted()){
                getNumberfromCallLog();}

        toolbar.setTitle(getString(R.string.selectfromCallLog));
    }
        else  if (key.equals("messages"))
        {
            toolbar.setTitle(getString(R.string.selectfromMessages));
            aa.clear();
            if (isSmsPermissionGranted()){
    new AsyncCaller().execute();}

          // getCallDetails();


        }
phone_no_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

       // Toast.makeText(Select_Contact.this, "clicked", Toast.LENGTH_SHORT).show();
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        if (aa.get(position).isChecked()){
            checkBox.setChecked(false);
            aa.get(position).setChecked(false);
        }else {
            checkBox.setChecked(true);
            aa.get(position).setChecked(true);

        }
    }
});

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = search.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }
        });

        select_all_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < aa.size(); i++) {
                    aa.get(i).setChecked(true);
                }
                adapter.notifyDataSetChanged();
                select_all_contact.setVisibility(View.GONE);
                unselect_all_contact.setVisibility(View.VISIBLE);
            }

        });

        unselect_all_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < aa.size(); i++) {
                    aa.get(i).setChecked(false);
                }
                adapter.notifyDataSetChanged();
                select_all_contact.setVisibility(View.VISIBLE);
                unselect_all_contact.setVisibility(View.GONE);
            }

        });

    }

    public boolean isCallLogPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_CALL_LOG)
                    == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                ActivityCompat.requestPermissions(Select_Contact.this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
    public boolean isContactPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                ActivityCompat.requestPermissions(Select_Contact.this, new String[]{Manifest.permission.READ_CONTACTS}, 0);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }



    public boolean isSmsPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_SMS)
                    == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                ActivityCompat.requestPermissions(Select_Contact.this, new String[]{Manifest.permission.READ_SMS}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


    public void getNumberfromContacts(ContentResolver cr) {
        String smsnumberdone = "";
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
           String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (smsnumberdone.contains( phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))) {

            } else {
                smsnumberdone = smsnumberdone + "," +  phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                aa.add(new Contact_Model(name, phoneNumber));
            }
        }
        phones.close();// close cursor
         adapter = new SelectContactAdapter(this, aa);

        adapter.sort(new Comparator<Contact_Model>() {
            public int compare(Contact_Model arg0, Contact_Model arg1) {
                Long date1 = Long.valueOf(arg0.getName().substring(1,
                        15));
                Long date2 = Long.valueOf(arg1.getName().substring(1,
                        15));
                return (date1 > date2 ? -1 : (date1 == date2 ? 0 : 1));
            }
        });
        phone_no_list.setAdapter(adapter);


        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    aa.clear();
                    getNumberfromContacts(getContentResolver());
                    toolbar.setTitle(getString(R.string.selectfromcontact));

                } else {

                    Toast.makeText(getApplicationContext(),"Some Features may not work properly",Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    aa.clear();
                    getNumberfromCallLog();
                    toolbar.setTitle(getString(R.string.selectfromCallLog));

                } else {
                    Toast.makeText(getApplicationContext(),"Some Features may not work properly",Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // contacts-related task you need to do.
                    new AsyncCaller().execute();

                } else {

                    Toast.makeText(getApplicationContext(),"Some Features may not work properly",Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void getNumberfromCallLog() {
        String smsnumberdone = "";
        Uri allCalls = Uri.parse("content://call_log/calls");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {

            Cursor c = getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);

            while (c.moveToNext())
            {
                String num= c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));// for  number
                String name= c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name
                if (name==null)
                {
                    name=num;
                }
                if (smsnumberdone.contains( c.getString(c.getColumnIndex(CallLog.Calls.NUMBER)))) {

                } else {
                    smsnumberdone = smsnumberdone + "," +  c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
                    aa.add(new Contact_Model(name, num));
                }
            }
            c.close();// close cursor
             adapter = new SelectContactAdapter(this, aa);

            phone_no_list.setAdapter(adapter);

        }


    }






    private ArrayList<Contact_Model> getCallDetails() {
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor c = getContentResolver().query(uri, null, null, null,
                "date" + " DESC");
        String smsnumberdone = "";

        while (c.moveToNext())
        {

            String name = getContactName(c.getString(c.getColumnIndexOrThrow("address")), this);

            String number = c.getString(c.getColumnIndexOrThrow("address")).toString();

            if (smsnumberdone.contains(c.getString(c.getColumnIndexOrThrow("address")).toString())) {

            } else {
                smsnumberdone = smsnumberdone + "," + c.getString(c.getColumnIndexOrThrow("address")).toString();
                aa.add(new Contact_Model(name, number));
            }
        }
        c.close();
return aa;

    }

    public static String getContactName(String phoneNum, Context caller) {
        String res = phoneNum;
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER };
        String selection = null;
        String[] selectionArgs = null;
        Cursor names = caller.getContentResolver().query(uri, projection,
                null, null, null);

        int indexName = 0;
        int indexNumber = 0;
        if (names != null) {
            indexName = names
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            indexNumber = names
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            if (names.getCount() > 0) {
                names.moveToFirst();
                do {
                    String name = names.getString(indexName);
                    String number = names.getString(indexNumber).replaceAll(
                            "[\\*\\+-]", "");

                    if (number.compareTo(phoneNum) == 0) {
                        res = name;
                        break;
                    }
                } while (names.moveToNext());
            }
        }
        names.close();
        return res;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public  boolean isSimExists()
    {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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
                String SENT;
                if (location.equals("share")) {
                    SENT = "SMS_SENT";


                }else {
                    SENT = "Request_SENT";

                }
                PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);



                SmsManager smsMgr = SmsManager.getDefault();
                smsMgr.sendTextMessage(straddress, null, strmsg, sentPI, null);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage() + "!\n" + "Failed to send SMS", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "" + " " + "Cannot send SMS", Toast.LENGTH_LONG).show();
        }
    }


    public void sendSmsMessage(String straddress, String strmsg) {





        try {
            Log.i("sms", "before sending");
            SmsManager smsMgr = SmsManager.getDefault();
            smsMgr.sendTextMessage(straddress, null, strmsg, null, null);
            Toast.makeText(Select_Contact.this, "The Location is Shared", Toast.LENGTH_LONG).show();

            Log.i("sms", "sms sent");

        }catch (Exception e){


            Toast.makeText(Select_Contact.this,"Invalid Number, Please Enter a valid number",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }
    String contactnumber;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
         contactnumber="";
        if (id==R.id.txtsend) {

            ArrayList<Contact_Model> test = new ArrayList<Contact_Model>();
            ArrayList<String> temp = new ArrayList<String>();
            for (int i = 0; i < aa.size(); i++) {
                if (aa.get(i).isChecked() == true) {
                   // test.add(new Contact_Model(aa.get(i).getName(), aa.get(i).getPhone_number()));
                    contactnumber=aa.get(i).getPhone_number();



                    try {
                        if (myLatitude == 0) {

                            final ProgressDialog progress = new ProgressDialog(Select_Contact.this);
                            progress.setTitle("Connecting to GPS\nStay outside for better Results");
                            progress.setMessage("Getting Your Location...");
                            progress.setCancelable(false);
                            progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            progress.show();

                            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, Select_Contact.this);

                            final Handler handler = new Handler();
                            Runnable task = new Runnable() {
                                @Override
                                public void run() {

                                    if (lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) !=null) {
                                        myLatitude = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                                        myLongitude = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
                                       // String Msg = "Download this Application to view Your Friend's Location http://tinyurl.com/y8l2g394\nLatitude=" + myLatitude + ";Longitude=" + myLongitude;

                                      //  sendSmsMessage(contactnumber, Msg);
                                        sendSms(contactnumber, Msg);
                                        try
                                        {
                                        progress.cancel();}catch (Exception e){e.printStackTrace();}
                                        handler.removeCallbacks(this);

                                    } else {

                                        handler.postDelayed(this, 3000);
                                    }


                                }

                            };
                            handler.post(task);


                        } else {


                          //  String Msg = "Download this Application to view Your Friend's Location http://tinyurl.com/y8l2g394\nLatitude=" + myLatitude + ";Longitude=" + myLongitude;

                          //  sendSmsMessage(aa.get(i).getPhone_number(), Msg);
                            sendSms(aa.get(i).getPhone_number(), Msg);


                        }

                    }catch (Exception e){}



                }
            }

            finish();
           // Toast.makeText(Select_Contact.this, String.valueOf(test.size()), Toast.LENGTH_SHORT).show();







        }
        return super.onOptionsItemSelected(item);
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

    }


    private class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(Select_Contact.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here

          aa=  getCallDetails();


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread

            // Dismiss the progress dialog
           // if (pdLoading.isShowing()) {
                pdLoading.dismiss();
          //  }
            adapter = new SelectContactAdapter(Select_Contact.this, aa);
            phone_no_list.setAdapter(adapter);

        }

    }
}
