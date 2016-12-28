package com.flamingo.filterdemo.db.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matrix on 16/6/24.
 */
public class IBlockumber {

    private static SQLiteDatabase sqLiteDatabase;
    private static final String TABLE_NAME = "blockNumber";
    public static final String RMPTY_RECORED = "NO RECORED";

    public IBlockumber(){
        IDatabase iDatabase = IDatabase.getInstance();
        sqLiteDatabase =  iDatabase.getSQLiteDatabase();
    }

    public void insert(ContentValues contentValues){
        String phone = findByPhone(contentValues.get("phone").toString());
        Log.i("TAG",contentValues.get("phone").toString());
        if(phone.equals("NO RECORED")&&contentValues.get("phone")!=null){
            sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        }

    }

    public void delete(String[] whereArgs){
        sqLiteDatabase.delete(TABLE_NAME,"phone=?",whereArgs);
    }

    public void deleteByPhone(String phone){
        sqLiteDatabase.execSQL("delete from "+TABLE_NAME+" where phone="+phone);
    }

    public void update(String[] whereArgs,ContentValues contentValues){
        sqLiteDatabase.update(TABLE_NAME,contentValues,"id=?",whereArgs);
    }

    public String findByPhone(String phone){
        Cursor cr = sqLiteDatabase.rawQuery("select * from "+TABLE_NAME+" where phone="+phone, null);
        if (cr.moveToFirst()) {
            return cr.getString(2);
        }
        return this.RMPTY_RECORED;
    }

    public List<String> findByPhone(){
        Cursor cr = sqLiteDatabase.rawQuery("select * from "+TABLE_NAME, null);
        List<String> phoneList = new ArrayList<>();
        if (cr.moveToFirst()) {
            for (int i=0;i<cr.getCount();i++){
                phoneList.add(cr.getString(2));
                cr.moveToNext();
            }

        }
        return phoneList;
    }

}
