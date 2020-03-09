package com.prime.studio.apps.gpsrouteguide.maps.directions.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.prime.studio.apps.gpsrouteguide.maps.directions.R;
import com.prime.studio.apps.gpsrouteguide.maps.directions.adapter.MsgAdapter;


import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by A S Computers on 31-May-16.
 */
public class ViewFriendLocation extends AppCompatActivity {

    private ListView listview;
    ArrayList<HashMap<String, String>> list ;
    String[] BooleanNumbers=new String[5000];
    ArrayList<String> mCheckNumExist;
    int count = 0;
    double TrLat,TrLng;
    InterstitialAd mInterstitialAd;
    boolean key;

    View convertView;
    TextView add_from_contacts, add_from_callLog, add_from_messages, manually_add;
    Double myLongitude, myLatitude;
    Context mContext;
    android.support.v7.app.AlertDialog alertdialog;
    private LogLocationListener logLocationListener;
    private LocationManager lm;
    Button mRequest_Friend_Location;
    LinearLayout mAdLayout;
    NativeExpressAdView adView;
    ListAdapter adapter;
    MsgAdapter mMsgAdapter;
    BroadcastReceiver alertSms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_reader);
        mContext = this;
        mCheckNumExist= new ArrayList<String>();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        logLocationListener = new LogLocationListener();
        mRequest_Friend_Location = (Button) findViewById(R.id.mRequestLocation);
       /* android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
////////////////ads//////////////////////////

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
        requestNewInterstitial();
      /*  AdView adView = (AdView) this.findViewById(R.id.adView);
        AdRequest stlit_adRequest = new AdRequest.Builder().build();
        adView.loadAd(stlit_adRequest);*/


        mAdLayout = (LinearLayout) findViewById(R.id.adlayout);
        adView = (NativeExpressAdView) findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
                //   .addTestDevice("905E497EDAC664F47B3B4741C3532784")
                .build();
        adView.loadAd(request);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdLayout.setVisibility(View.VISIBLE);
            }
        });


        //////////////end ads///////////////////
      //  BooleanNumbers[0] = "kak";



        key= getIntent().getBooleanExtra("key", false);

        list = new ArrayList<HashMap<String, String>>();

         /*adapter = new SimpleAdapter(
                SmsReader.this, list,
                R.layout.list_numbers, new String[] {"Info","Number"},
                new int[] { R.id.textView, R.id.textView2 });*/

        // updating listview

             listview = (ListView) findViewById(R.id.List);
        listview.setEnabled(true);
        mMsgAdapter = new MsgAdapter(ViewFriendLocation.this,list);
          /*  adapter = new SimpleAdapter(
                    SmsReader.this, list,
                    R.layout.list_numbers, new String[] {"Info","Number"},
                    new int[] { R.id.textView, R.id.textView2 });*/
        // updating listview


        // listview.setEnabled(true);

        listview.setAdapter(mMsgAdapter);

     //   listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String numb = ((TextView) view.findViewById(R.id.textView2)).getText().toString();
                Bundle basket = new Bundle();
                basket.putString("Number", numb);
                basket.putDouble("Lat", TrLat);
                basket.putDouble("Lng", TrLng);
                Intent intent = new Intent(getApplicationContext(), ListLocation.class);
                intent.putExtras(basket);
                startActivity(intent);


            }
        });

      new AsyncGetSms().execute();
        mRequest_Friend_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestlocation_dialog();
            }
        });


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                if (key) {
                    startActivity(new Intent(ViewFriendLocation.this, Fav.class));
                    finish();
                } else {
                    startActivity(new Intent(ViewFriendLocation.this, FirstActivity.class));
                    finish();
                }

            }
        });



        String SENT = "android.provider.Telephony.SMS_RECEIVED";
        alertSms= new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
              // new AsyncGetSms().execute();

               // Toast.makeText(arg0, "Msg received", Toast.LENGTH_SHORT).show();
               // list.clear();
                try {
               new AsyncGetSms().execute();
                 //   Toast.makeText(ViewFriendLocation.this, String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
                 //  list= getAllSms1();
                  //  Toast.makeText(ViewFriendLocation.this, "try", Toast.LENGTH_SHORT).show();
                }catch (Exception e)
                {
                 //   Toast.makeText(ViewFriendLocation.this, "catch", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                mMsgAdapter.notifyDataSetChanged();
              /*  adapter = new SimpleAdapter(
                        SmsReader.this, list,
                        R.layout.list_numbers, new String[] {"Info","Number"},
                        new int[] { R.id.textView, R.id.textView2 });
                // updating listview


                listview.setAdapter(adapter);*/
            }
        };
        registerReceiver(alertSms, new IntentFilter(SENT));


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(alertSms);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        Bundle extras = intent.getExtras();

        if (extras != null && extras.getBoolean("alert")) {
            new AsyncGetSms().execute();
        }
    }

    void requestlocation_dialog() {
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(ViewFriendLocation.this,R.style.CustomDialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = (View) inflater.inflate(R.layout.requestlocation_dialog, null);
        alertDialog.setView(view);
        //  alertDialog.setCancelable(false);

        TextView btnok= (TextView) view.findViewById(R.id.mOK);
        TextView btnno= (TextView) view.findViewById(R.id.mNO);


        alertdialog= alertDialog.show();


        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog.dismiss();
                if (ActivityCompat.checkSelfPermission(ViewFriendLocation.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ViewFriendLocation.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                    InitiateSMS();


                }
                if (ActivityCompat.checkSelfPermission(ViewFriendLocation.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ViewFriendLocation.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                    InitiateContact();


                } else {

                    final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(ViewFriendLocation.this,R.style.CustomDialog);
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
                            intent.putExtra("location", "request");
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
                            intent.putExtra("location", "request");
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
                            intent.putExtra("location", "request");
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


            }
        });
        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog.dismiss();

            }
        });

    }
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
            if (key)
            {
                startActivity(new Intent(ViewFriendLocation.this, Fav.class));
                finish();
            }else {
                startActivity(new Intent(ViewFriendLocation.this, FirstActivity.class));
                finish();
            }
        }
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public ArrayList<HashMap<String, String>> getAllSms() {

        PrintWriter lmFout_sms_inbox = null;
        ContentResolver cr = getContentResolver();
        Uri SMSinbox = Uri.parse("content://sms/inbox");
        Cursor cursor_sms_inbox = cr.query(
                //SMS_INBOX_CONTENT_URI,
                SMSinbox,
                new String[] { "_id", "thread_id", "address", "person", "date", "body" },
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

                boolean  b = body.contains("view Your Friend's Location http://tinyurl.com/y8l2g394") ;
                if(b) {
                 //   if (!(Arrays.asList(BooleanNumbers).contains(address))) {
                    if (!mCheckNumExist.contains(address)) {
                       /* BooleanNumbers[count]=address;
                        count++;*/
                        mCheckNumExist.add(address);
                        map.put("Number", address);
                        Log.d("number",address);
                        if (getContactName(getApplicationContext(), address) != null) {

                            map.put("Info", getContactName(getApplicationContext(), address) + "\n" + address);
                            Log.d("number", getContactName(getApplicationContext(), address) + "\n" + address);


                        } else {
                            map.put("Info", address);
                            Log.d("number", address);


                        }

                        list.add(map);
                    }else {
                        Log.d("number", "notcontain");
                    }
                }else {
                    Log.d("number","notmatch");
                }
                String newRow = "msgId:" + messageId +";contctId:" + contactId + ";body:" + body;
                if (timestamp != 0){
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy_HH/mm/ss");
                    formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timestamp);
                    String formatted = formatter.format(calendar.getTime());
                    newRow += ";time:"+ formatted;
                }
                if (address != null){
                    newRow +=";address:" + address;
                }

                if ( lmFout_sms_inbox != null ){
                    lmFout_sms_inbox.println(newRow);
                }

            }
        }
        if (cursor_sms_inbox != null) {
            cursor_sms_inbox.close();
        }
        return list;
    }

    public ArrayList<HashMap<String, String>> getAllSms1() {

        PrintWriter lmFout_sms_inbox1 = null;
        ContentResolver cr1 = getContentResolver();
        Uri SMSinbox1 = Uri.parse("content://sms/inbox");
        Cursor cursor_sms_inbox1 = cr1.query(
                //SMS_INBOX_CONTENT_URI,
                SMSinbox1,
                new String[] { "_id", "thread_id", "address", "person", "date", "body" },
                null,
                null,
                null);
        //int count = 0;
        if (cursor_sms_inbox1 != null) {
            //count = cursor_sms.getCount();
            while (cursor_sms_inbox1.moveToNext()) {
                long messageId = cursor_sms_inbox1.getLong(0);
                //long threadId = cursor_sms.getLong(1);
                String address = cursor_sms_inbox1.getString(2);
                long contactId = cursor_sms_inbox1.getLong(3);
                //String contactId_string = String.valueOf(contactId);
                long timestamp = cursor_sms_inbox1.getLong(4);
                String body = cursor_sms_inbox1.getString(5);

// Anywhere in string
                HashMap<String, String> map = new HashMap<String, String>();

                boolean  b = body.indexOf("view Your Friend's Location http://tinyurl.com/y8l2g394") > 0;
                if(b) {
                    if (!(Arrays.asList(BooleanNumbers).contains(address))) {
                        BooleanNumbers[count]=address;
                        count++;
                        map.put("Number", address);
                        if (getContactName(getApplicationContext(), address) != null) {

                            map.put("Info", getContactName(getApplicationContext(), address) + "\n" + address);



                        } else {
                            map.put("Info", address);


                        }

                        list.add(map);
                    }
                }
                String newRow = "msgId:" + messageId +";contctId:" + contactId + ";body:" + body;
                if (timestamp != 0){
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy_HH/mm/ss");
                    formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timestamp);
                    String formatted = formatter.format(calendar.getTime());
                    newRow += ";time:"+ formatted;
                }
                if (address != null){
                    newRow +=";address:" + address;
                }

                if ( lmFout_sms_inbox1 != null ){
                    lmFout_sms_inbox1.println(newRow);
                }

            }
        }
        return list;
    }
    private class AsyncGetSms extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(ViewFriendLocation.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           list.clear();
            BooleanNumbers=new String[5000];
            mCheckNumExist.clear();
            count =0;
            //this method will be running on UI thread
        /*    pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();*/
        }
        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here

            list= getAllSms();


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread

            // Dismiss the progress dialog
            // if (pdLoading.isShowing()) {

 // mMsgAdapter = new MsgAdapter(SmsReader.this,list);
          /*  adapter = new SimpleAdapter(
                    SmsReader.this, list,
                    R.layout.list_numbers, new String[] {"Info","Number"},
                    new int[] { R.id.textView, R.id.textView2 });*/
            // updating listview


           // listview.setEnabled(true);

      //    listview.setAdapter(mMsgAdapter);
mMsgAdapter.notifyDataSetChanged();
        }

    }

    public void Request_Friend_Location()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewFriendLocation.this);
        // set title
        alertDialogBuilder.setTitle("Request Friend Location");

        // set dialog message
        alertDialogBuilder
                .setMessage("Location will be requested through SMS\nCharges may Apply, share ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (ActivityCompat.checkSelfPermission(ViewFriendLocation.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ViewFriendLocation.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                            InitiateSMS();


                        }
                        if (ActivityCompat.checkSelfPermission(ViewFriendLocation.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ViewFriendLocation.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                            InitiateContact();


                        } else {

                            final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(mContext);
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
                                    intent.putExtra("myLatitude", myLatitude);
                                    intent.putExtra("myLongitude", myLongitude);
                                    intent.putExtra("location", "request");

                                    startActivity(intent);


                                }
                            });

                            add_from_callLog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    alertdialog.dismiss();

                                    Intent intent = new Intent(getBaseContext(), Select_Contact.class);
                                    intent.putExtra("key", "callLog");
                                    intent.putExtra("myLatitude", myLatitude);
                                    intent.putExtra("myLongitude", myLongitude);
                                    intent.putExtra("location", "request");
                                    startActivity(intent);
                                }
                            });
                            add_from_messages.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    alertdialog.dismiss();
                                    Intent intent = new Intent(getBaseContext(), Select_Contact.class);
                                    intent.putExtra("key", "messages");
                                    intent.putExtra("myLatitude", myLatitude);
                                    intent.putExtra("myLongitude", myLongitude);
                                    intent.putExtra("location", "request");
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
    private void InitiateContact(){
        int hasCallPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS);
        if (hasCallPermission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    3);


            return;
        }
    }


    void manually_add() {
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(ViewFriendLocation.this,R.style.CustomDialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = (View) inflater.inflate(R.layout.manually_sharelocation_dialog, null);
        alertDialog.setView(view);
        final EditText edtnumber= (EditText) view.findViewById(R.id.edtnumber);
        TextView btncancel= (TextView) view.findViewById(R.id.btncancel);
        TextView btnsend= (TextView) view.findViewById(R.id.btnsend);

        alertdialog= alertDialog.show();

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


                            String Msg = "Location Request:\n  I am looking for you, download this Application to send Location http://tinyurl.com/y8l2g394\n";

                            // sendSmsMessage(contactNumber, Msg);
                            sendSms(edtnumber.getText().toString(), Msg);



                    } catch (Exception e) {
                    }

                }


                alertdialog.dismiss();
            }
        });
    }



    private void sendSms(String straddress, String strmsg) {
        if (isSimExists()) {
            try {
                String SENT = "Request_SENT";

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
        public void onProviderDisabled(String provider)
        {
            if (!((Activity) mContext).isFinishing()) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                final String message = "GPS is not Enable\nEnable GPS for Proper Functioning";



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