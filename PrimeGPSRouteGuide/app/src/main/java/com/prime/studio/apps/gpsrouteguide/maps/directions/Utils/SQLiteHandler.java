package com.prime.studio.apps.gpsrouteguide.maps.directions.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_HISTORY = "history";
    private static final String TABLE_TRACKING_HISTORY = "trackinghistory";

    // Login Table Columns names
    private static final String KEY_ID = "id";

    private static final String NAME = "sourcedestination";
    private static final String ADDRESS = "address";
    private static final String SAVED_ON = "savedon";
    private static final String LAT1 = "lat1";
    private static final String LNG1 = "lng1";
    private static final String LAT2 = "lat2";
    private static final String LNG2 = "lng2";
    private static final String DATE = "date";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + LAT1 + " TEXT,"
                + NAME + " TEXT UNIQUE," + DATE + " TEXT," + LNG1 + " TEXT, " + ADDRESS + " TEXT, " + SAVED_ON + " TEXT " + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_TRACKING_TABLE = "CREATE TABLE " + TABLE_TRACKING_HISTORY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + LAT1 + " TEXT,"
                + NAME + " TEXT UNIQUE,"   + DATE + " TEXT UNIQUE,"+ LNG1 + " TEXT," + LAT2 + " TEXT," + LNG2 + " TEXT" + ")";
        db.execSQL(CREATE_TRACKING_TABLE);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKING_HISTORY);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String name, String date, String Lat1, String Lng1) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME, name); // Name
        values.put(DATE, date); // Name
        values.put(LAT1, Lat1); // LAT
        values.put(LNG1, Lng1); // LNG


        // Inserting Row
        long id = db.insert(TABLE_HISTORY, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    public void addTHistory(String name,String date, String Lat1, String Lng1, String Lat2, String Lng2) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME, name); // Name
        values.put(DATE, date); // Name
        values.put(LAT1, Lat1); // Email
        values.put(LNG1, Lng1); // Email
        values.put(LAT2, Lat2); // Email
        values.put(LNG2, Lng2); // Email

        // Inserting Row
        long id = db.insert(TABLE_TRACKING_HISTORY, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     */


    public ArrayList<HashMap<String, String>> GetData() {

        //ArrayList<HashMap<String, String>> productsList  = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> productsList = new ArrayList<HashMap<String, String>>();


        // String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;
        String[] columns = new String[]{KEY_ID, LAT1, NAME,DATE, LNG1, ADDRESS, SAVED_ON};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_HISTORY, columns, null, null, null, null, KEY_ID + " DESC");

        int i = c.getColumnIndex(NAME);
        int j = c.getColumnIndex(LAT1);
        int k = c.getColumnIndex(LNG1);
        int f = c.getColumnIndex(DATE);
        int n = c.getColumnIndex(KEY_ID);
        int o = c.getColumnIndex(ADDRESS);
        int p = c.getColumnIndex(SAVED_ON);


        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            HashMap<String, String> user = new HashMap<String, String>();
            user.put("Name", c.getString(i));
            user.put("Lat1", c.getString(j));
            user.put("Lng1", c.getString(k));
            user.put("Date", c.getString(f));
            user.put("ID", c.getString(n));
            user.put("address", c.getString(o));
            user.put("savedon", c.getString(p));
            productsList.add(user);
        }


        c.close();
        db.close();
        return productsList;

    }

    public ArrayList<HashMap<String, String>> GetTrackingHistory() {

        //ArrayList<HashMap<String, String>> productsList  = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> productsList = new ArrayList<HashMap<String, String>>();


        // String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;
        String[] columns = new String[]{KEY_ID, LAT1, NAME,DATE, LNG1, LAT2, LNG2};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_TRACKING_HISTORY, columns, null, null, null, null, KEY_ID + " DESC");

        int i = c.getColumnIndex(NAME);
        int f = c.getColumnIndex(DATE);
        int j = c.getColumnIndex(LAT1);
        int k = c.getColumnIndex(LNG1);
        int l = c.getColumnIndex(LAT2);
        int m = c.getColumnIndex(LNG2);
        int n = c.getColumnIndex(KEY_ID);


        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            HashMap<String, String> user = new HashMap<String, String>();
            user.put("Name", c.getString(i));
            user.put("Date", c.getString(f));
            user.put("Lat1", c.getString(j));
            user.put("Lng1", c.getString(k));
            user.put("Lat2", c.getString(l));
            user.put("Lng2", c.getString(m));
            user.put("ID", c.getString(n));
            productsList.add(user);
        }


        c.close();
        db.close();
        return productsList;

    }

    /**
     * Getting user login status return true if rows are there in table
     */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_HISTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    public int getRowCountHistory() {
        String countQuery = "SELECT  * FROM " + TABLE_TRACKING_HISTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_HISTORY, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    /**
     * Re crate database Delete single row
     */
    public void deleteSingleRow(int rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_HISTORY, KEY_ID + "=?", new String[]{String.valueOf(rowId)});
        db.close();

        Log.d(TAG, "Deleted single info from sqlite");
    }

    public void deleteSingleRowHistory(int rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_TRACKING_HISTORY, KEY_ID + "=?", new String[]{String.valueOf(rowId)});
        db.close();

        Log.d(TAG, "Deleted single info from sqlite");
    }
}

