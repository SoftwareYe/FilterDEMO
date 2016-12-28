package com.flamingo.filterdemo.tools;


import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by matrix on 16/6/22.
 */
public class FindLocation {

    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    static {
        //设置网络超时时间
        asyncHttpClient.setTimeout(5000);
    }

    private static String result = "";
    private static final String HTTP_FAIL="fail";
    private static String url;



    public static String getLocation(String phone){

        url = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel="+phone;
        asyncHttpClient.get(url, new TextHttpResponseHandler("GBK") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                JSONObject jsonObject = null;
                String responseJson="aaa";
                String responseStr = response.toString();
                try {
                    responseJson = responseStr.replaceAll("\r\n","");
                    responseJson = responseJson.replaceAll("\t","");
                    responseJson = responseJson.replaceAll("__GetZoneResult_ =","");
                    jsonObject = new JSONObject(responseJson);
                    result = (String) jsonObject.getString("province");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable throwable) {
                System.out.println("fail 出错");
                result = HTTP_FAIL;
            }
        });
        return result;

    }

}
