package com.flamingo.filterdemo.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.flamingo.filterdemo.R;
import com.flamingo.filterdemo.core.AbsHandler;
import com.flamingo.filterdemo.core.AbsTrigger;
import com.flamingo.filterdemo.core.BlockerBuilder;
import com.flamingo.filterdemo.core.IBlocker;
import com.flamingo.filterdemo.data.MyItemListener;
import com.flamingo.filterdemo.data.PhoneListAdapter;
import com.flamingo.filterdemo.db.impl.IBlockTime;
import com.flamingo.filterdemo.impl.InCallingHandler;
import com.flamingo.filterdemo.impl.InCallingTrigger;
import com.flamingo.filterdemo.impl.TimeRangFilter;
import com.flamingo.filterdemo.tools.MyPhoneStateListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by matrix on 16/7/1.
 */
public class TimePhoneFragment extends Fragment implements MyItemListener{

    private CompoundButton mCompoundButton;
    private Button mButton;
    private PickerView mHourView;
    private PickerView mMinuteView;
    private PickerView mHourViewEnd;
    private PickerView mMinuteViewEnd;

    private String start_hour;
    private String start_minute;
    private String end_hour;
    private String end_minute;
    private IBlockTime mIBlockTime;
    private String[] time;

    private MaterialDialog mItemDialog;

    //recycleview
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private PhoneListAdapter mAdapter;
    private List<String> mDatas;
    private ContentValues contentValues;

    //拦截器
    private IBlocker mBlocker;
    private AbsTrigger mTrigger = new InCallingTrigger();
    private Handler mUIHandler;
    private AbsHandler mHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.time_phone, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initView();
        initDatas();
        initListView();
        initOnclick();
    }

    private void initDatas() {
        mDatas = mIBlockTime.findByTimeAsString();
    }

    private void initListView() {
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        mAdapter = new PhoneListAdapter(mDatas);
        mAdapter.setMyItemListener(this);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerViewHeader header = RecyclerViewHeader.fromXml(getActivity(), R.layout.header);
        TextView headerTextView = (TextView) header.findViewById(R.id.recycle_view_title);
        headerTextView.setText("黑名单列表");
        header.attachTo(mRecyclerView,false);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void initView() {
        mIBlockTime = new IBlockTime();
        mItemDialog = new MaterialDialog(getActivity());
        mCompoundButton = (CompoundButton) getView().findViewById(R.id.open_or_not);
        mButton = (Button) getView().findViewById(R.id.add_phone);
        mHourView = (PickerView) getView().findViewById(R.id.hour_pv);
        //mMinuteView = (PickerView) getView().findViewById(R.id.minute_pv);
        mHourViewEnd = (PickerView) getView().findViewById(R.id.hour_pv_end);
        //mMinuteViewEnd = (PickerView) getView().findViewById(R.id.minute_pv_end);
        List<String> data = new ArrayList<String>();
        List<String> seconds = new ArrayList<String>();
        for (int i = 0; i < 24; i++)
        {
            data.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i++)
        {
            seconds.add(i < 10 ? "0" + i : "" + i);
        }
        mHourView.setData(data);
       // mMinuteView.setData(seconds);
        mHourViewEnd.setData(data);
        //mMinuteViewEnd.setData(seconds);

        Time time=new Time();
        time.setToNow();
        mHourView.setSelected(time.hour);
        //mMinuteView.setSelected(time.minute);
        mHourViewEnd.setSelected(time.hour+1);
        //mMinuteViewEnd.setSelected(time.minute);

        start_hour = String.valueOf(time.hour);
        //start_minute = String.valueOf(time.minute);
        end_hour = String.valueOf(time.hour+1);
        //end_minute =  String.valueOf(time.minute);


        mUIHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                String phonestr = msg.getData().getString("phone");
                String datestr = msg.getData().getString("date");
                // 通知栏提示
                Context context = getActivity();
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                Notification notification = builder
                        .setContentTitle(phonestr)
                        .setContentText(datestr)
                        .setSmallIcon(R.drawable.icon_phone)
                        .build();
                //notification.icon = R.drawable.icon_phone;
                notificationManager.notify(11, notification);
            }
        };
        mHandler = new InCallingHandler(mUIHandler,getActivity());



    }

    private void initOnclick(){

        mCompoundButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mCompoundButton.isChecked()==true){
                    mCompoundButton.setText("点击关闭");
                    setupBlocker();

                }else{
                    mCompoundButton.setText("点击开启");
                    setupBlocker();
                }
            }
        });

        mHourView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                start_hour = text;
            }
        });
        /*mMinuteView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                start_minute = text;
            }
        });*/
        mHourViewEnd.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                end_hour = text;
            }
        });
       /* mMinuteViewEnd.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                end_minute = text;
            }
        });*/

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* String startTime = start_hour+start_minute;
                String endTime  = end_hour+end_minute;*/
                String startTime = start_hour;
                String endTime  = end_hour;
                Toast.makeText(getActivity(),startTime,Toast.LENGTH_LONG).show();
                ContentValues mCcontentValues = new ContentValues();
                mCcontentValues.put("starttime",Integer.valueOf(startTime));
                mCcontentValues.put("endtime",Integer.valueOf(endTime));
                mCcontentValues.put("addtime",String.valueOf(new Date()));
                mIBlockTime.insert(mCcontentValues);
                initDatas();
                mAdapter.setDatas(mDatas);
                mAdapter.notifyDataSetChanged();
                setupBlocker();

                Calendar now = Calendar.getInstance();
                int current_hour = now.get(Calendar.HOUR_OF_DAY);
                Toast.makeText(getActivity(),String.valueOf(current_hour),Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setupBlocker(){
        BlockerBuilder builder = new BlockerBuilder();

        List<Map<String,Integer>> blockTime = mIBlockTime.findByTime();

       for (int i=0;i<blockTime.size();i++){
           builder.addFilters(new TimeRangFilter(blockTime.get(i).get("starttime"),blockTime.get(i).get("endtime")));
       }
        mBlocker = builder
                .setTrigger(mTrigger)
                .setHandler(mHandler)
                .create();
        if(mCompoundButton.isChecked()==true){
            mBlocker.enable();//开启黑名单拦截才开始拦截
        }

    }

    public void init(){

        //获取电话通讯服务
        TelephonyManager tpm = (TelephonyManager) getActivity()
                .getSystemService(Context.TELEPHONY_SERVICE);
        //创建一个监听对象，监听电话状态改变事件
        tpm.listen(new MyPhoneStateListener((AbsTrigger)mTrigger), PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onItemClick(View view, int postion) {
        String phone = mDatas.get(postion);
        //Toast.makeText(getActivity(),phone,Toast.LENGTH_SHORT).show();

        if(phone!=null){
            time = mDatas.get(postion).split("-");
            mItemDialog.setTitle("移除"+phone+"?");
            mItemDialog.setPositiveButton("确认", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemDialog.dismiss();
                    mIBlockTime.deleteByTime(Integer.valueOf(time[0]), Integer.parseInt(time[1]));
                    initDatas();
                    mAdapter.setDatas(mDatas);
                    mAdapter.notifyDataSetChanged();
                    setupBlocker();
                }
            });
            mItemDialog.setNegativeButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemDialog.dismiss();

                }
            });
            mItemDialog.show();


        }
    }
}
