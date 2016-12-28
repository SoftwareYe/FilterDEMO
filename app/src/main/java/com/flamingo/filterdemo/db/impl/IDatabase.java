package com.flamingo.filterdemo.db.impl;

import android.database.sqlite.SQLiteDatabase;

import com.flamingo.filterdemo.db.core.AbCreate;

import java.io.File;

/**
 * Created by matrix on 16/6/24.
 */
public final class IDatabase implements AbCreate{
    private static IDatabase iDatabase = null;
    private static SQLiteDatabase sqLiteDatabase;

    private IDatabase(){

        /*if (sqLiteDatabase!=null){
            sqLiteDatabase.close();
        }
        */
        File dir = new File("/data/data/com.flamingo.filterdemo/databases");
        if (!dir.exists())
            dir.mkdir();

        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase("/data/data/com.flamingo.filterdemo/databases/blockphone.db",null);
        sqLiteDatabase.execSQL("create table if not exists " + "blockNumber(id integer primary key AUTOINCREMENT,"
                + "addtime datetime," + "phone varchar(20))");
        sqLiteDatabase.execSQL("create table if not exists " + "blockTime(id integer primary key AUTOINCREMENT,"
                + "addtime datetime," + "starttime int,endtime int)");
    }


    public static IDatabase getInstance() {
        if (iDatabase==null){
            synchronized (IDatabase.class){
                if (iDatabase==null){
                    iDatabase = new IDatabase();
                }
            }
        }

        return iDatabase;
    }

    @Override
    public SQLiteDatabase getSQLiteDatabase(){
        return sqLiteDatabase;
    }
}
