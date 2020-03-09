package com.prime.studio.apps.gpsrouteguide.maps.directions.adapter;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.prime.studio.apps.gpsrouteguide.maps.directions.R;
import com.prime.studio.apps.gpsrouteguide.maps.directions.activity.ListLocation;
import com.prime.studio.apps.gpsrouteguide.maps.directions.activity.SQLiteHandler;
import com.prime.studio.apps.gpsrouteguide.maps.directions.activity.Select_Contact;
import com.prime.studio.apps.gpsrouteguide.maps.directions.model.Child;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Asad on 9/8/2016.
 */
public class MyExpandableAdapter extends BaseExpandableListAdapter
{

    private Activity activity;
    private ArrayList<Object> childtems;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String, String>> parentItems;
    private ArrayList<Child> child;
    TextView abc;
    double myLatitude,myLongitude;
    String TempLat,TempLng;
    private LocationManager lm;
    double TrLat,TrLng;
    Geocoder geocoder;
    SQLiteHandler db;
    String Final;
    List<Address> address;
     View convertView1 = null;
    Button add_from_contacts, add_from_callLog, add_from_messages, manually_add;
    android.support.v7.app.AlertDialog alertdialog;

    // constructor
    public MyExpandableAdapter(ArrayList<HashMap<String, String>> parents, ArrayList<Object> childern)
    {
        this.parentItems = parents;
        this.childtems = childern;


    }

    public void setInflater(LayoutInflater inflater, Activity activity)
    {
        this.inflater = inflater;
        this.activity = activity;
        geocoder = new Geocoder(activity, Locale.getDefault());
        lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        db = new SQLiteHandler(activity);

    }

    // method getChildView is called automatically for each child view.
    //  Implement this method as per your requirement
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {

        child = (ArrayList<Child>) childtems.get(groupPosition);

        TextView textView = null;
        ImageView childImage = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_view, null);
        }

        // get the textView reference and set the value
        textView = (TextView) convertView.findViewById(R.id.textViewChild);
        childImage = (ImageView) convertView.findViewById(R.id.childImage);
        textView.setText(child.get(childPosition).getChildname());
        childImage.setBackgroundResource(child.get(childPosition).getChildimg());

        // set the ClickListener to handle the click event on child item
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               // Toast.makeText(activity,parentItems.get(groupPosition)+child.get(childPosition), Toast.LENGTH_SHORT).show();

                switch (childPosition) {
                    case 0:
                        String info = parentItems.get(groupPosition).get("Location");
                        if (info.contains("http://tinyurl.com/y8l2g394")) {
                            info = info.replace("Download this Application to view Your Friend's Location http://tinyurl.com/y8l2g394\n", "");


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
                            activity.startActivity(maps);

                        }
                        break;
                    case 1:
                        String info2 = parentItems.get(groupPosition).get("Location");

                        if (info2.contains("http://tinyurl.com/y8l2g394")) {
                            info2 = info2.replace("Download this Application to view Your Friend's Location http://tinyurl.com/y8l2g394\n", "");

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
                                    Uri.parse("http://maps.google.com/maps?saddr=" + ListLocation.TrLat + "," + ListLocation.TrLng + "&daddr=" + myLatitude + "," + myLongitude));
                            activity.startActivity(intent);
                        }
                        break;
                    case 2:

                      /*  if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                            InitiateSMS();

                            return;
                        }if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                        InitiateContact();

                        return;*/

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        // set title
                        alertDialogBuilder.setTitle("share Your Location");

                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Location will be Shared through SMS\nCharges may Apply, share ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                                            InitiateSMS();

                                            return;
                                        }
                                        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                                            InitiateContact();


                                            return;
                                        } else {

                                            final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(activity);
                                            LayoutInflater inflater = activity.getLayoutInflater();
                                            convertView1 = (View) inflater.inflate(R.layout.dialog, null);
                                            add_from_contacts = (Button) convertView1.findViewById(R.id.add_from_contacts);
                                            add_from_callLog = (Button) convertView1.findViewById(R.id.add_from_callLog);
                                            add_from_messages = (Button) convertView1.findViewById(R.id.add_from_messages);
                                            manually_add = (Button) convertView1.findViewById(R.id.manually_add);
                                            alertDialog.setView(convertView1);
                                            alertdialog = alertDialog.show();

                                            add_from_contacts.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    alertdialog.dismiss();
                                                    Intent intent = new Intent(activity, Select_Contact.class);
                                                    intent.putExtra("key", "contact");
                                                    intent.putExtra("myLatitude", myLatitude);
                                                    intent.putExtra("myLongitude", myLongitude);

                                                    activity.startActivity(intent);


                                                }
                                            });

                                            add_from_callLog.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    alertdialog.dismiss();

                                                    Intent intent = new Intent(activity, Select_Contact.class);
                                                    intent.putExtra("key", "callLog");
                                                    intent.putExtra("myLatitude", myLatitude);
                                                    intent.putExtra("myLongitude", myLongitude);
                                                    activity.startActivity(intent);
                                                }
                                            });
                                            add_from_messages.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    alertdialog.dismiss();
                                                    Intent intent = new Intent(activity, Select_Contact.class);
                                                    intent.putExtra("key", "messages");
                                                    intent.putExtra("myLatitude", myLatitude);
                                                    intent.putExtra("myLongitude", myLongitude);
                                                    activity.startActivity(intent);
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





                   /* else {

                        Bundle basket = new Bundle();
                        basket.putDouble("Lat", myLatitude);
                        basket.putDouble("Lng", myLongitude);
                        Intent intent1 = new Intent(activity, ShareToFNF.class);
                        intent1.putExtras(basket);
                        activity.startActivity(intent1);
                    }*/
                        break;
                    case 3:

                            String info3 = parentItems.get(groupPosition).get("Location");
                        if (info3.contains("http://tinyurl.com/y8l2g394")) {
                            info3 = info3.replace("Download this Application to view Your Friend's Location http://tinyurl.com/y8l2g394\n", "");

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
                            try {


                                BigDecimal hh3 = new BigDecimal(TempLat);
                                BigDecimal mm3 = new BigDecimal(TempLng);


                                myLatitude = hh3.doubleValue();
                                myLongitude = mm3.doubleValue();
                            } catch (Exception e) {
                                myLatitude = Double.parseDouble(TempLat);
                                myLongitude = Double.parseDouble(TempLng);
                            }

                            try {
                                address = geocoder.getFromLocation(myLatitude, myLongitude, 1);

                                final String address1 = address.get(0).getAddressLine(0);
                                final String city = address.get(0).getLocality();
                                final String Location = "" + address1 + " " + city;
                                Final = Location + "\n" + activity.getString(R.string.locationreceivedon) + "\n" + parentItems.get(groupPosition).get("Display");
                                db.addUser(Final, String.valueOf(myLatitude), String.valueOf(myLongitude));


                                Toast.makeText(activity,
                                        "Location saved to Favourite",
                                        Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                Toast.makeText(activity,
                                        "Can't add to Favourites\nCheck you Internet",
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }

                        }

                        break;
                    case 4:


                        break;
                }








            }
        });
        return convertView;
    }

    // method getGroupView is called automatically for each parent item
    // Implement this method as per your requirement
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_locations, null);
            abc= (TextView) convertView.findViewById(R.id.textView);
        }

        abc.setText(parentItems.get(groupPosition).get("Display"));
       // abc.setChecked(isExpanded);

        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
      return ((ArrayList<String>) childtems.get(groupPosition)).size();

    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return null;
    }

    @Override
    public int getGroupCount()
    {
        return parentItems.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition)
    {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition)
    {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return 0;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }


    @TargetApi(Build.VERSION_CODES.M)
    public  void InitiateSMS(){
        int hasCallPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS);
        if (hasCallPermission != PackageManager.PERMISSION_GRANTED){
            activity.requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                    1);


            return;
        }
    }
    void manually_add() {
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = (View) inflater.inflate(R.layout.manually_select_contacts, null);
        alertDialog.setView(view);
        final EditText edtnumber= (EditText) view.findViewById(R.id.edtnumber);
        Button btncancel= (Button) view.findViewById(R.id.btncancel);
        Button btnsend= (Button) view.findViewById(R.id.btnsend);

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


                if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                    InitiateSMS();

                    return;
                }

                else {

                    try {
                        if (myLatitude == 0) {

                            final ProgressDialog progress = new ProgressDialog(activity);
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

                            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, (LocationListener) activity);

                            final Handler handler = new Handler();
                            Runnable task = new Runnable() {
                                @Override
                                public void run() {

                                    if (lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) !=null) {
                                        myLatitude = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                                        myLongitude = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
                                        String Msg = "Download this Application to view Your Friend's Location http://tinyurl.com/y8l2g394\nLatitude=" + myLatitude + ";Longitude=" + myLongitude;

                                        sendSmsMessage(edtnumber.getText().toString(), Msg);
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

                            sendSmsMessage(edtnumber.getText().toString(), Msg);

                        }

                    }catch (Exception e){}

                }




                alertdialog.dismiss();
            }
        });
    }

    public void sendSmsMessage(String straddress, String strmsg) {


        try {
            Log.i("sms", "before sending");
            SmsManager smsMgr = SmsManager.getDefault();
            smsMgr.sendTextMessage(straddress, null, strmsg, null, null);
            Toast.makeText(activity, "The Location is Shared", Toast.LENGTH_LONG).show();

            Log.i("sms", "sms sent");

        }catch (Exception e){


            Toast.makeText(activity,"Invalid Number, Please Enter a valid number", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }
    @TargetApi(Build.VERSION_CODES.M)
    private void InitiateContact(){
        int hasCallPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);
        if (hasCallPermission != PackageManager.PERMISSION_GRANTED){
            activity.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    1);


            return;
        }
    }
}

