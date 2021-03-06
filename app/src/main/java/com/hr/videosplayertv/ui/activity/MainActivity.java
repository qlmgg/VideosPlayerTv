package com.hr.videosplayertv.ui.activity;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hr.videosplayertv.R;
import com.hr.videosplayertv.base.BaseActivity;
import com.hr.videosplayertv.base.BaseFragment;
import com.hr.videosplayertv.common.ImmobilizationData;
import com.hr.videosplayertv.db.RealmDBManger;
import com.hr.videosplayertv.event.Event;
import com.hr.videosplayertv.net.entry.ListData;
import com.hr.videosplayertv.ui.adapter.ListDataMenuAdapter;
import com.hr.videosplayertv.ui.adapter.MainFragmentAdapter;
import com.hr.videosplayertv.ui.adapter.viewStub.HomeLayout;
import com.hr.videosplayertv.ui.fragment.MultipleFragment;
import com.hr.videosplayertv.utils.DisplayUtils;
import com.hr.videosplayertv.utils.FocusUtil;
import com.hr.videosplayertv.utils.Formatter;
import com.hr.videosplayertv.utils.NLog;
import com.hr.videosplayertv.widget.focus.FocusBorder;
import com.hr.videosplayertv.widget.single.WhatView;
import com.hr.videosplayertv.widget.view.TvViewPager;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static com.hr.videosplayertv.ui.adapter.viewStub.HomeLayout.isUpOrDown;
/*
*
* */

public class MainActivity extends BaseActivity implements BaseFragment.FocusBorderHelper {

    private MainFragmentAdapter mainFragmentAdapter;
    private List<MultipleFragment> multipleFragments;

    private ListDataMenuAdapter listDataMenuAdapter;
    private Disposable mDisposable;//脉搏

    private long firstTime=0;

    @BindView(R.id.tv_view_pager)
    TvViewPager tvViewPager;
    @BindView(R.id.main_menu)
    TvRecyclerView mainMenu;
    @BindView(R.id.image_log)
    ImageView imageLog;
    @BindView(R.id.tv_notification)
    TextView tvNotification;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.btn_search)
    TextView btn_search;
    @BindView(R.id.btn_personal_center)
    TextView btn_personal_center;

    private View selectView; //被选中的 View;


    @OnClick({R.id.btn_search,R.id.btn_personal_center})
    public void Onclick(View v){
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_search:
                intent.setClass(this, SearchActivity.class);
                startActivity(intent);

                break;
            case R.id.btn_personal_center:
                intent.setClass(this, UserCenterActivity.class);
                startActivity(intent);

                break;
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
       // super.onSaveInstanceState(outState);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void init() {
        super.init();
        setListener();
        listDataMenuAdapter = new ListDataMenuAdapter(this,ListDataMenuAdapter.ONE);
        mainMenu.setSpacingWithMargins(DisplayUtils.getDimen(R.dimen.x10), DisplayUtils.getDimen(R.dimen.x30));
        mainMenu.setAdapter(listDataMenuAdapter);

        mainFragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager());
        tvViewPager.setOffscreenPageLimit(2);
        tvViewPager.setScrollerDuration(200);
        tvViewPager.setAdapter(mainFragmentAdapter);
        multipleFragments = new ArrayList<>();

        initData();

        getTimesPosable();
    }
    private void setListener() {

        mFocusBorder.boundGlobalFocusListener(new FocusBorder.OnFocusCallback() {
            @Override
            public FocusBorder.Options onFocus(View oldFocus, View newFocus) {

                setShowOrDiss(false);

                if(null != newFocus){

                    switch (newFocus.getId()){
                            case R.id.main_layout:
                                return FocusBorder.OptionsFactory.get(1.0f, 1.0f, 0);
                        case R.id.btn_search:
                        case R.id.btn_personal_center:
                            return FocusBorder.OptionsFactory.get(1.2f, 1.2f, 0);
                          default:
                              return FocusBorder.OptionsFactory.get(Formatter.getScale(newFocus.getWidth(),newFocus.getHeight(),DisplayUtils.getDimen(R.dimen.x15)),
                                      Formatter.getScale(newFocus.getWidth(),newFocus.getHeight(),DisplayUtils.getDimen(R.dimen.x15)), Formatter.getRoundRadius());
                        }
                }
                return FocusBorder.OptionsFactory.get(1.1f, 1.1f, 0); //返回null表示不使用焦点框框架
            }
        });

        mainMenu.setOnItemListener(new SimpleOnItemListener() {

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
               // onMoveFocusBorder(itemView, 1.0f, DisplayUtils.dip2px(3));


                if(position == tvViewPager.getCurrentItem()){

                }else {
                    tvViewPager.setCurrentItem(position);
                    if(selectView != null)
                        selectView.setActivated(false);
                }
                selectView = itemView;
                itemView.setActivated(true);


            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {

            }
        });

        mainMenu.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

              //  mFocusBorder.setVisible(hasFocus);

            }
        });

    }

    private void initData(){
        List<ListData> listData = new ArrayList<>();

        for (int i =0 ;i< 8; i++){
            ListData listData1 = new ListData(
                    i,
                    ImmobilizationData.Tags.getNameByIndex(i),
                    ImmobilizationData.Tags.getColorByIndex(i)
            );
            listData.add(listData1);
            multipleFragments.add(MultipleFragment.getmultipleFragment().setType(listData1));
        }
        listDataMenuAdapter.repaceDatas(listData);
        mainFragmentAdapter.upData(multipleFragments);
        mainMenu.setSelection(0);
        tvViewPager.setCurrentItem(0,false);
    }

    @Override
    public FocusBorder getFocusBorder() {
        return mFocusBorder;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

            if(HomeLayout.isUp){
                if(event.getAction() == KeyEvent.ACTION_DOWN  && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP){
                HomeLayout.isUp = false;
                FocusUtil.setFocus(mainMenu);
                return true;
            }
           }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        View rootview = MainActivity.this.getWindow().getDecorView();

        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_LEFT: //向左键
            case KeyEvent.KEYCODE_DPAD_RIGHT:

                WhatView.getInstance().whatOperation(rootview.findFocus(),true);

                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:

                WhatView.getInstance().whatOperation(rootview.findFocus(),false);

                break;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode){

            case KeyEvent.KEYCODE_DPAD_UP:
                isUpOrDown = false;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                isUpOrDown = true;
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT: //向左键

                if(btn_search.isFocused()){
                    return true;
                }

                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:

                break;

            case KeyEvent.KEYCODE_BACK:

                if(HomeLayout.isCanBack){

                    HomeLayout.isCanBack = false;
                    EventBus.getDefault().post(new Event.HomeLayoutEvent());

                    return true;
                }

                if (System.currentTimeMillis()-firstTime>2000){
                    Toast.makeText(MainActivity.this,"再按一次退出应用",Toast.LENGTH_SHORT).show();
                    firstTime=System.currentTimeMillis();
                }else{
                    finish();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dispose();
        RealmDBManger.closed();
    }

    protected void getTimesPosable() {

        dispose();

        mDisposable = Flowable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        tvTime.setText(getTime());
                    }
                });

    }
    private void dispose(){
        if (mDisposable != null){
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    //获得当前年月日时分秒星期 
    public String getTime(){
            final Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份 
             String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份 
            String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码 
            String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
            String mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));//时 
            String mMinute = String.valueOf(c.get(Calendar.MINUTE));//分 
            String mSecond = String.valueOf(c.get(Calendar.SECOND));//秒 

            if(mSecond.length() == 1){
                mSecond = "0"+mSecond;
            }
            if(mMinute.length() == 1){
                mMinute = "0"+mMinute;
            }
            if(mHour.length() == 1){
                mHour = "0"+mHour;
            }
            if("1".equals(mWay)){
            mWay ="日";
            }else if("2".equals(mWay)){
            mWay ="一";
            }else if("3".equals(mWay)){
            mWay ="二";
            }else if("4".equals(mWay)){
            mWay ="三";
            }else if("5".equals(mWay)){
            mWay ="四";
            }else if("6".equals(mWay)){
            mWay ="五";
            }else if("7".equals(mWay)) {
                mWay = "六";
            }
     return mYear + "年" + mMonth + "月" + mDay+"日"+" "+"星期"+mWay+"\n"+mHour+":"+mMinute+":"+mSecond;
   }



}
