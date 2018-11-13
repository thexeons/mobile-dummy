package com.example.hazelnut.dummy;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler {
/* extends SQLiteOpenHelper
    /*
    // static variable
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "LocalBlock";

    // table name
    private static final String TABLE_BLOCK = "block";


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
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_BLOCK + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FIRSTNAME + " TEXT," + KEY_LASTNAME + " TEXT," + KEY_KTP + " TEXT," + KEY_EMAIL + " TEXT," + KEY_DOB + " TEXT," + KEY_EMAIL + " TEXT," + KEY_ADDRESS + " TEXT," + KEY_NATIONALITY + " TEXT," + KEY_ACCOUNTNUM + " TEXT,"
                + KEY_PHOTO + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOCK);
        onCreate(db);
    }

    public void addRecord(Block block){
        SQLiteDatabase db  = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FIRSTNAME, block.getFirstname());
        values.put(KEY_LASTNAME, block.getLastname());
        values.put(KEY_KTP, block.getKtp());
        values.put(KEY_EMAIL, block.getEmail());
        values.put(KEY_DOB, block.getDob());
        values.put(KEY_ADDRESS, block.getAddress());
        values.put(KEY_NATIONALITY, block.getNationality());
        values.put(KEY_ACCOUNTNUM, block.getAccountnum());
        values.put(KEY_PHOTO, block.getPhoto());
        db.insert(TABLE_BLOCK, null, values);
        db.close();
    }

    public Block getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BLOCK, new String[] { KEY_ID,
                        KEY_NAME, KEY_TALL }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Block contact = new Block(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return contact
        return contact;
    }

    // get All Record
    public List<UserModels> getAllRecord() {
        List<UserModels> contactList = new ArrayList<UserModels>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TALL;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                UserModels userModels = new UserModels();
                userModels.setId(Integer.parseInt(cursor.getString(0)));
                userModels.setName(cursor.getString(1));
                userModels.setTall(cursor.getString(2));

                contactList.add(userModels);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }
*/
}
