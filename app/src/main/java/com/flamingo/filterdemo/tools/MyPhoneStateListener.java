package com.flamingo.filterdemo.tools;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.flamingo.filterdemo.core.AbsTrigger;
import com.flamingo.filterdemo.impl.InCallingTrigger;

/**
 * Created by matrix on 16/6/23.
 */
public class MyPhoneStateListener extends PhoneStateListener {

    private AbsTrigger mTrigger;

    public MyPhoneStateListener(AbsTrigger absTrigger){
        this.mTrigger = absTrigger;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch(state) {
            case TelephonyManager.CALL_STATE_IDLE: //空闲
                break;
            case TelephonyManager.CALL_STATE_RINGING: //来电
                ((InCallingTrigger)mTrigger).InComingCall(incomingNumber);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK: //摘机（正在通话中）
                break;
        }
        super.onCallStateChanged(state, incomingNumber);
    }

}
