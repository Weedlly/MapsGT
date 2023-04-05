package com.example.mapsgt.friends;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    public UserDatabaseHelper(Context context) {
        super(context, "FriendData.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB){
        DB.execSQL("create Table Frienddetails(name TEXT primary key, contact TEXT, dob TEXT)");
    }

    @Override
    public void onUpgrade (SQLiteDatabase DB, int i, int i1){
        DB.execSQL("drop Table if exists Frienddetails");
    }

    public Boolean insertFriendData(String name, String contact, String dob)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("contact", contact);
        contentValues.put("dob", dob);

        long result = DB.insert("Frienddetails", null, contentValues);

        if(result == -1)
        {
            return false;
        } else {
            return true;
        }
    }

    public Boolean updateFriendData(String name, String contact, String dob)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("contact", contact);
        contentValues.put("dob", dob);

        Cursor cursor = DB.rawQuery("Select * from Frienddetails where name = ?", new String[] {name});
        if (cursor.getCount() > 0) {


            long result = DB.update("Frienddetails", contentValues, "name=?", new String[]{name});

            if (result == -1) {
                return false;
            } else {
                return true;
            }

        } else {
            return false;
        }
    }

    public Boolean deleteFriendData(String name)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Frienddetails where name = ?", new String[] {name});
        if (cursor.getCount() > 0) {

            long result = DB.delete("Frienddetails", "name=?", new String[]{name});

            if (result == -1) {
                return false;
            } else {
                return true;
            }

        } else {
            return false;
        }
    }

    public Cursor getData()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Frienddetails", null);
        return cursor;
    }

}
