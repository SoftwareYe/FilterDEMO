package com.flamingo.filterdemo.demo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flamingo.filterdemo.R;
import com.flamingo.filterdemo.view.BlackPhoneFragment;
import com.flamingo.filterdemo.view.TimePhoneFragment;
import com.flamingo.filterdemo.view.WhitePhoneFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

	private ViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;
	private List<Fragment> mDatas;

	public FragmentManager fragmentManager;

	private TextView mWhitePhoneTextView;
	private TextView mBlackPhoneTextView;
	private TextView mTimePhoneTextView;

	private ImageView mTabLine;
	private int mScreen1_3;
	private int mCurrentPageIndex;


	private WhitePhoneFragment whitePhoneFragment;
	private BlackPhoneFragment blackPhoneFragment;
	private TimePhoneFragment timePhoneFragment;

	public static final int FRAGMENT_ONE=0;
	public static final int FRAGMENT_TWO=1;


	public MainActivity() {
	}

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Log.i("tag","启动");
		setContentView(R.layout.activity_main);

		init();
		initTabLine();
		initView();

		/*Contacts.initialize(this);
		List<Contact> contacts = Contacts.getQuery().find();
		for (int i=0;i<contacts.size();i++){
			Log.i("contact", String.valueOf(contacts.get(i).getDisplayNames()));
			Log.i("number", String.valueOf(contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber()));
		}*/

		/*MyPhoneContacts myPhoneContacts = new MyPhoneContacts(this);
		myPhoneContacts.testReadAllContacts();*/


	}

	private void initTabLine() {
		mTabLine = (ImageView) findViewById(R.id.id_iv_tabline);
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		mScreen1_3 = outMetrics.widthPixels/3;
		ViewGroup.LayoutParams lp = mTabLine.getLayoutParams();
		lp.width = mScreen1_3;
		mTabLine.setLayoutParams(lp);
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

		mWhitePhoneTextView = (TextView) findViewById(R.id.id_tv_white);
		mBlackPhoneTextView = (TextView) findViewById(R.id.id_tv_black);
		mTimePhoneTextView = (TextView) findViewById(R.id.id_tv_time);

		mDatas = new ArrayList<Fragment>();
		whitePhoneFragment = new WhitePhoneFragment();
		blackPhoneFragment = new BlackPhoneFragment();
		timePhoneFragment = new TimePhoneFragment();
		mDatas.add(whitePhoneFragment);
		mDatas.add(blackPhoneFragment);
		mDatas.add(timePhoneFragment);

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public Fragment getItem(int position) {
				return mDatas.get(position);
			}

			@Override
			public int getCount() {
				return mDatas.size();
			}
		};
		mViewPager.setAdapter(mAdapter);

		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLine.getLayoutParams();
				if (mCurrentPageIndex==0&&position==0){//0->1
					lp.leftMargin = (int) (positionOffset*mScreen1_3+mCurrentPageIndex*mScreen1_3);
				}else if(mCurrentPageIndex==1&&position==0){//1->0
					lp.leftMargin = (int) (mCurrentPageIndex*mScreen1_3+(positionOffset-1)*mScreen1_3);
				}else if(mCurrentPageIndex==1&&position==1){//1->2
					lp.leftMargin = (int) (mCurrentPageIndex*mScreen1_3+positionOffset*mScreen1_3);
				}else if(mCurrentPageIndex==2&&position==1){//2->1
					lp.leftMargin = (int) (mCurrentPageIndex*mScreen1_3+(positionOffset-1)*mScreen1_3);
				}
				mTabLine.setLayoutParams(lp);
			}

			@Override
			public void onPageSelected(int position) {
				resetTextView();
				switch(position){
					case 0:
						mWhitePhoneTextView.setTextColor(Color.GREEN);
						break;
					case 1:
						mBlackPhoneTextView.setTextColor(Color.GREEN);
						break;
					case 2:
						mTimePhoneTextView.setTextColor(Color.GREEN);
						break;
				}
				mCurrentPageIndex = position;
			}


			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

	}

	private void resetTextView() {
		mWhitePhoneTextView.setTextColor(Color.BLACK);
		mBlackPhoneTextView.setTextColor(Color.BLACK);
		mTimePhoneTextView.setTextColor(Color.BLACK);
	}

	@Override
	protected void onStop(){
		super.onStop();
		Log.i("tag","已暂停");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("tag","销毁");
	}

private void init(){

	/*Intent intent = new Intent(Intent.ACTION_MAIN);
	intent.addCategory(Intent.CATEGORY_HOME);
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	startActivity(intent);*/

}







}
