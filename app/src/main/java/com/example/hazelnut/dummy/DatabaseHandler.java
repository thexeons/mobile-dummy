package com.example.hazelnut.dummy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper {

    // static variable
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "LocalBlock";

    // table name
    private static final String TABLE_BLOCK = "msBlock";


    // column tables
    private static final String KEY_ID = "id";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_KTP = "ktp";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DOB = "dob";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_NATIONALITY = "nationality";
    private static final String KEY_ACCOUNTNUM = "accountnum";
    private static final String KEY_PHOTO = "photo";
    public DatabaseHandler(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_BLOCK + "("
                + KEY_ID + " TEXT,"
                + KEY_FIRSTNAME + " TEXT,"
                + KEY_LASTNAME + " TEXT,"
                + KEY_KTP + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_DOB + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_NATIONALITY + " TEXT,"
                + KEY_ACCOUNTNUM + " TEXT,"
                + KEY_PHOTO + " TEXT"+ ")";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOCK);
        // Create tables again
        onCreate(db);
    }
    // **** CRUD (Create, Read, Update, Delete) Operations ***** //

    // Adding new User Details
    void insertUserDetails(String firstname, String lastname, String ktp, String email, String dob, String address, String nationality, String accountnum, String photo){
        //Get the Data Repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_FIRSTNAME, firstname);
        cValues.put(KEY_LASTNAME, lastname);
        cValues.put(KEY_KTP, ktp);
        cValues.put(KEY_EMAIL, email);
        cValues.put(KEY_DOB, dob);
        cValues.put(KEY_ADDRESS, address);
        cValues.put(KEY_NATIONALITY, nationality);
        cValues.put(KEY_ACCOUNTNUM, accountnum);
        cValues.put(KEY_PHOTO, photo);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_BLOCK,null, cValues);
        db.close();
    }
    // Get User Details
    public ArrayList<HashMap<String, String>> GetBlocks(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> blockList = new ArrayList<>();
        String query = "SELECT name, location, designation FROM "+ TABLE_BLOCK;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("firstname",cursor.getString(cursor.getColumnIndex(KEY_FIRSTNAME)));
            user.put("lastname",cursor.getString(cursor.getColumnIndex(KEY_LASTNAME)));
            user.put("ktp",cursor.getString(cursor.getColumnIndex(KEY_KTP)));
            user.put("email",cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            user.put("dob",cursor.getString(cursor.getColumnIndex(KEY_DOB)));
            user.put("address",cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)));
            user.put("nationality",cursor.getString(cursor.getColumnIndex(KEY_NATIONALITY)));
            user.put("accountnum",cursor.getString(cursor.getColumnIndex(KEY_ACCOUNTNUM)));
            user.put("photo",cursor.getString(cursor.getColumnIndex(KEY_PHOTO)));
            blockList.add(user);
        }
        return  blockList;
    }
    // Get User Details based on userid
    /*
    public ArrayList<HashMap<String, String>> GetUserByUserId(int userid){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT name, location, designation FROM "+ TABLE_BLOCK;
        Cursor cursor = db.query(TABLE_BLOCK, new String[]{KEY_NAME, KEY_LOC, KEY_DESG}, KEY_ID+ "=?",new String[]{String.valueOf(userid)},null, null, null, null);
        if (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("name",cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            user.put("designation",cursor.getString(cursor.getColumnIndex(KEY_DESG)));
            user.put("location",cursor.getString(cursor.getColumnIndex(KEY_LOC)));
            userList.add(user);
        }
        return  userList;
    }*/
    
    // Delete User Details
    public void DeleteUser(int userid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BLOCK, KEY_ID+" = ?",new String[]{String.valueOf(userid)});
        db.close();
    }
    // Update User Details
    public int UpdateUserDetails(String firstname, String lastname, String ktp, String email, String dob, String address, String nationality, String accountnum, String photo, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put(KEY_FIRSTNAME, firstname);
        cVals.put(KEY_LASTNAME, lastname);
        cVals.put(KEY_KTP, ktp);
        cVals.put(KEY_EMAIL, email);
        cVals.put(KEY_DOB, dob);
        cVals.put(KEY_ADDRESS, address);
        cVals.put(KEY_NATIONALITY, nationality);
        cVals.put(KEY_ACCOUNTNUM, accountnum);
        cVals.put(KEY_PHOTO, photo);
        int count = db.update(TABLE_BLOCK, cVals, KEY_ID+" = ?",new String[]{String.valueOf(id)});
        return  count;
    }
}