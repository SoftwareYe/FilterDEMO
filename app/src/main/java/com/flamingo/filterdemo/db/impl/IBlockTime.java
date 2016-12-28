package com.flamingo.filterdemo.db.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matrix on 16/7/6.
 */
public class IBlockTime {

    private final static String TABLE_NAME = "blockTime";
    private static SQLiteDatabase sqLiteDatabase;
    public static final String RMPTY_RECORED = "NO RECORED";

    public IBlockTime(){
        IDatabase iDatabase = IDatabase.getInstance();
        sqLiteDatabase =  iDatabase.getSQLiteDatabase();
    }

    public void insert(ContentValues contentValues){
        String phone = findByTime(Integer.valueOf(contentValues.get("starttime").toString()),Integer.valueOf(contentValues.get("endtime").toString()));
        Log.i("TAG",contentValues.get("starttime").toString());
        if(phone.equals("NO RECORED")&&contentValues.get("starttime")!=null&&contentValues.get("endtime")!=null){
            sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        }

    }

    public void delete(String[] whereArgs){
        sqLiteDatabase.delete(TABLE_NAME,"id=?",whereArgs);
    }

    public void deleteByTime(int starttime,int endtime){
        sqLiteDatabase.execSQL("delete from "+TABLE_NAME+" where starttime="+starttime+" and endtime="+endtime);
    }

    public void update(String[] whereArgs,ContentValues contentValues){
        sqLiteDatabase.update(TABLE_NAME,contentValues,"id=?",whereArgs);
    }

    public String findByTime(int starttime,int endtime){
        Cursor cr = sqLiteDatabase.rawQuery("select * from "+TABLE_NAME+" where starttime="+starttime+" and endtime="+endtime, null);
        if (cr.moveToFirst()) {
            return cr.getString(2);
        }
        return this.RMPTY_RECORED;
    }

    public List<String> findByTimeAsString(){
        Cursor cr = sqLiteDatabase.rawQuery("select * from "+TABLE_NAME, null);
        List<String> phoneList = new ArrayList<>();
        if (cr.moveToFirst()) {
            for (int i=0;i<cr.getCount();i++){
                phoneList.add(cr.getInt(2)+"-"+cr.getInt(3));
                cr.moveToNext();
            }

        }
        return phoneList;
    }
    public List<Map<String,Integer>> findByTime(){
        Cursor cr = sqLiteDatabase.rawQuery("select * from "+TABLE_NAME, null);
        List<Map<String,Integer>> phoneList = new ArrayList<Map<String, Integer>>();
        if (cr.moveToFirst()) {
            for (int i=0;i<cr.getCount();i++){
                Map<String,Integer> map = new HashMap<>();
                map.put("starttime",cr.getInt(2));
                map.put("endtime",cr.getInt(3));
                phoneList.add(map);
                cr.moveToNext();
            }

        }
        return phoneList;
    }


}
