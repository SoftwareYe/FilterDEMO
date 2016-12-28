package com.flamingo.filterdemo.impl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.flamingo.filterdemo.core.AbsHandler;
import com.flamingo.filterdemo.core.IFilter;
import com.flamingo.filterdemo.core.MessageData;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * 来电 处理器， 主要对经由各种Filter判断之后的结果进行处理，比如挂断来电，通知栏提醒等等
 * @author boyliang
 *
 */
public final class InCallingHandler extends AbsHandler {
	private Handler mUIHandler;
	private Context context;
	TelephonyManager telMgr;

	public InCallingHandler(Handler handler,Context context){
		this.mUIHandler = handler;
		this.context = context;
	}
	
	@Override
	public void handle(MessageData data){
		int opcode = data.getInt(MessageData.KEY_OP);
		String phone = data.getString(MessageData.KEY_DATA);

		// 刷新数据列表
		String phonestr = String.format("(%s)%s",
				(opcode == IFilter.OP_BLOCKED) ? "拦截" :
						((opcode == IFilter.OP_PASS) ? "放行" : "跳过"), phone);
		String datestr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
				Locale.CHINA).format(new Date());
		if (opcode==IFilter.OP_BLOCKED){
			endCall();
		}
		Bundle bundle = new Bundle();
		bundle.putString("phone", phonestr);
		bundle.putString("date", datestr);

		Message msg = mUIHandler.obtainMessage();
		msg.what = 11;
		msg.setData(bundle);

		msg.sendToTarget();
	}

	private void endCall(){
		telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		Class<TelephonyManager> c = TelephonyManager.class;
		try
		{
			Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
			getITelephonyMethod.setAccessible(true);
			ITelephony iTelephony = null;
			Log.e("TAG", "End call.");
			iTelephony = (ITelephony) getITelephonyMethod.invoke(telMgr, (Object[]) null);
			iTelephony.endCall();

		}
		catch (Exception e)
		{
			Log.e("tag", "Fail to answer ring call.", e);
		}
	}
}
