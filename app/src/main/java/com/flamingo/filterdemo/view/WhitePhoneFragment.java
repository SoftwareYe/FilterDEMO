package com.flamingo.filterdemo.view;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.flamingo.filterdemo.R;
import com.flamingo.filterdemo.core.AbsHandler;
import com.flamingo.filterdemo.core.AbsTrigger;
import com.flamingo.filterdemo.core.BlockerBuilder;
import com.flamingo.filterdemo.core.IBlocker;
import com.flamingo.filterdemo.core.IFilter;
import com.flamingo.filterdemo.data.MyItemListener;
import com.flamingo.filterdemo.data.PhoneListAdapter;
import com.flamingo.filterdemo.impl.InCallingHandler;
import com.flamingo.filterdemo.impl.InCallingTrigger;
import com.flamingo.filterdemo.impl.NumeralFilter;
import com.flamingo.filterdemo.tools.MyPhoneStateListener;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by matrix on 16/7/1.
 */
public class WhitePhoneFragment extends Fragment implements MyItemListener{

    private CompoundButton mCompoundButton;


    //recycleview
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private PhoneListAdapter mAdapter;
    private List<String> mDatas;
    private List<String> phoneList;

    //拦截器
    private IBlocker mBlocker;
    private AbsTrigger mTrigger = new InCallingTrigger();
    private IFilter mIFilter;
    private Handler mUIHandler;
    private AbsHandler mHandler;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.white_phone, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initView();
        intDatas();
        initListView();
        setupBlocker();
    }



    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
    }

    private void intDatas() {
        Contacts.initialize(getActivity());
        Query q = Contacts.getQuery();
        q.hasPhoneNumber();
        List<Contact> contacts = q.find();
        mDatas = new ArrayList<>();
        phoneList = new ArrayList<>();
        for (int i =0;i<contacts.size();i++){
            mDatas.add("("+contacts.get(i).getDisplayNames().get(0).toString()+")"+contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber());
            phoneList.add(contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber());
        }

    }

    private void initView() {
        mCompoundButton = (CompoundButton) getView().findViewById(R.id.open_or_not);
        mCompoundButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mCompoundButton.isChecked()==true){
                    mCompoundButton.setText("点击关闭");
                    intDatas();
                    setupBlocker();

                }else{
                    mCompoundButton.setText("点击开启");
                    intDatas();
                    setupBlocker();
                }
            }
        });

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

    //初始化recycle view
    private void initListView() {
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        mAdapter = new PhoneListAdapter(mDatas);
        mAdapter.setMyItemListener(this);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerViewHeader header = RecyclerViewHeader.fromXml(getActivity(), R.layout.header);
        TextView headerTextView = (TextView) header.findViewById(R.id.recycle_view_title);
        headerTextView.setText("白名单列表");
        header.attachTo(mRecyclerView,false);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onItemClick(View view, int postion) {

    }

    private void setupBlocker() {
        BlockerBuilder builder = new BlockerBuilder();
        String[] whiteNumber = new String[phoneList.size()];
        if (phoneList!=null){
            phoneList.toArray(whiteNumber);
            Log.i("tagee",whiteNumber.toString());
        }
        mIFilter = new NumeralFilter(IFilter.OP_PASS,whiteNumber);
        mBlocker = builder
                .setTrigger(mTrigger)
                .setHandler(mHandler)
                .addFilters(mIFilter)   //实现白名单放行
                .create();
        if(mCompoundButton.isChecked()==true){
            mBlocker.enable();//开启白名单拦截才开始拦截
        }

    }


    public void init(){
        //获取电话通讯服务
        TelephonyManager tpm = (TelephonyManager) getActivity()
                .getSystemService(Context.TELEPHONY_SERVICE);
        //创建一个监听对象，监听电话状态改变事件
        tpm.listen(new MyPhoneStateListener((AbsTrigger)mTrigger), PhoneStateListener.LISTEN_CALL_STATE);
    }



}
