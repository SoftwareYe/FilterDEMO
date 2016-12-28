package com.flamingo.filterdemo.tools;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created by matrix on 16/7/4.
 */
public class MyPhoneContacts {
    private Context mContext;

    public MyPhoneContacts(Context mContext){
        this.mContext = mContext;

    }

    /*
     * 读取联系人的信息
     */
    public void testReadAllContacts() {
        Uri uri = Uri.parse("content://icc/adn");

        Cursor cursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        int contactIdIndex = 0;
        int nameIndex = 0;
        String contactId = "";
        String name;
        int count = cursor.getCount();
        if(cursor.getCount() > 0) {
            contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            Log.i("TAG",String.valueOf(nameIndex));
            while(cursor.moveToNext()) {
                contactId = cursor.getString(contactIdIndex);
                name = cursor.getString(nameIndex);
                Log.i("TAG", contactId);
                Log.i("TAG", name);
        }

             /*
             * 查找该联系人的phone信息
             */
            Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                    null, null);
            int phoneIndex = 0;
            if(phones.getCount() > 0) {
                phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            }
            while(phones.moveToNext()) {
                String phoneNumber = phones.getString(phoneIndex);
                Log.i("TAG", phoneNumber);
            }





        }
    }



}
