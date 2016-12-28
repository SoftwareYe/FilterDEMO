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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.flamingo.filterdemo.R;
import com.flamingo.filterdemo.core.AbsHandler;
import com.flamingo.filterdemo.core.AbsTrigger;
import com.flamingo.filterdemo.core.BlockerBuilder;
import com.flamingo.filterdemo.core.IBlocker;
import com.flamingo.filterdemo.core.IFilter;
import com.flamingo.filterdemo.data.MyItemListener;
import com.flamingo.filterdemo.data.PhoneListAdapter;
import com.flamingo.filterdemo.db.impl.IBlockumber;
import com.flamingo.filterdemo.impl.InCallingHandler;
import com.flamingo.filterdemo.impl.InCallingTrigger;
import com.flamingo.filterdemo.impl.NumeralFilter;
import com.flamingo.filterdemo.tools.MyPhoneStateListener;

import java.util.Date;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by matrix on 16/7/1.
 */
public class BlackPhoneFragment extends Fragment implements MyItemListener{

    private CompoundButton mCompoundButton;
    private Button mButton;
    private MaterialDialog mMaterialDialog;
    private MaterialDialog mItemDialog;
    private EditText mEditText;


    //recycleview
    private RecyclerView mRecyclerView;
    private  LinearLayoutManager layoutManager;
    private PhoneListAdapter mAdapter;
    private List<String> mDatas;
    private ContentValues contentValues;

    //拦截器
    private IBlockumber iBlockumber;
    private IBlocker mBlocker;
    private AbsTrigger mTrigger = new InCallingTrigger();
    private IFilter mIFilter;
    private Handler mUIHandler;
    private AbsHandler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.black_phone, container, false);
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

    //获取recycleView的数据
    private void intDatas() {
        mDatas = iBlockumber.findByPhone();
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
        headerTextView.setText("黑名单列表");
        header.attachTo(mRecyclerView,false);
        mRecyclerView.setAdapter(mAdapter);
    }
    //初始化view
    private void initView() {
        iBlockumber = new IBlockumber();

        List<String> blockNumber = iBlockumber.findByPhone();
        String[] block= {""};
        if (blockNumber!=null){
            blockNumber.toArray(block);
        }
        contentValues = new ContentValues();
        mCompoundButton = (CompoundButton) getView().findViewById(R.id.open_or_not);
        mButton = (Button) getView().findViewById(R.id.add_phone);
        mEditText = new EditText(getActivity());
        mMaterialDialog = new MaterialDialog(getActivity());
        mMaterialDialog.setTitle("添加黑名单");
        mMaterialDialog.setMessage("输入号码");
        mMaterialDialog.setView(mEditText);
        mMaterialDialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentValues.put("phone",String.valueOf(mEditText.getText()));
                contentValues.put("addtime", String.valueOf(new Date()));
                iBlockumber.insert(contentValues);
                mMaterialDialog.dismiss();
                intDatas();
                mAdapter.setDatas(mDatas);
                mAdapter.notifyDataSetChanged();
                mEditText.setText(null);
                setupBlocker();
            }
        });

        mMaterialDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();

            }
        });

        mItemDialog = new MaterialDialog(getActivity());



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


            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mMaterialDialog.show();


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


    @Override
    public void onItemClick(View view, final int postion) {
        String phone = mDatas.get(postion);
        Toast.makeText(getActivity(),phone,Toast.LENGTH_SHORT).show();
        if(phone!=null){
            mItemDialog.setTitle("移除"+phone+"?");
            mItemDialog.setPositiveButton("确认", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemDialog.dismiss();
                    iBlockumber.deleteByPhone(mDatas.get(postion));
                    intDatas();
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



    private void setupBlocker(){
        BlockerBuilder builder = new BlockerBuilder();
        iBlockumber = new IBlockumber();
        List<String> blockNumber = iBlockumber.findByPhone();
        String[] block= new String[blockNumber.size()];
        if (blockNumber!=null){
            blockNumber.toArray(block);
        }
        mIFilter = new NumeralFilter(IFilter.OP_BLOCKED,block);
        mBlocker = builder
                .setTrigger(mTrigger)
                .setHandler(mHandler)
                .addFilters(mIFilter)   //实现黑名单放行
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

}
