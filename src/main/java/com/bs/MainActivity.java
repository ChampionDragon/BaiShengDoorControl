package com.bs;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bs.activity.AddDeviceActivity;
import com.bs.activity.ControlOneActivity;
import com.bs.activity.SearchDeviceActivity;
import com.bs.activity.UsermanagerActivity;
import com.bs.adapter.DeviceAdapter;
import com.bs.adapter.ImageViewAdapter;
import com.bs.base.BaseApplication;
import com.bs.bean.DeviceBean;
import com.bs.constant.Constant;
import com.bs.constant.SpKey;
import com.bs.db.DbManager;
import com.bs.fragment.MenuleftFragment;
import com.bs.slidelibrary.SlidingFragmentActivity;
import com.bs.slidelibrary.SlidingMenu;
import com.bs.util.DialogCustomUtil;
import com.bs.util.DialogNotileUtil;
import com.bs.util.Logs;
import com.bs.util.SmallUtil;
import com.bs.util.SpUtil;
import com.bs.util.ToastUtil;
import com.bs.wifi.WifiAdmin;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends SlidingFragmentActivity implements
        OnClickListener {
    String tag = "lcb";
    private WifiInfo info;
    private ViewPager vp;
    private WifiAdmin mWifiAdmin;
    private int[] pics = {R.drawable.ad1, R.drawable.ad2, R.drawable.ad3};
    private ImageView[] dots;
    private List<ImageView> list;
    private int imgsize = 0;// 整个适配器的总长
    private ScheduledExecutorService scheduledExecutorService;// 定时周期执行指定任务
    private int currentIndex;// （自动播放时）定时周期要显示的图片的索引（viewpager中的图片位置）
    private int dotIndex = 1;// 设置当前点的索引
    private ListView lv;
    private List<ScanResult> mWifiList;
    private List<DeviceBean> deviceBeanList;
    private WifiManager mangerWifi;
    private DbManager managerDb;
    private SpUtil spUtil;
    String[] name = {"超级用户lcb", "超级用户cyp", "普通用户lxp", "遥控"};
    String[] error = {"关门遇阻", "开门遇阻", "开启异常", "关闭异常"};
    boolean resume;
    private SwipeRefreshLayout refresh;// 下拉刷新列表
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         /*设置成透明导航栏和透明状态栏*/
        SmallUtil.setScreen(this);
        initView();
        initRefresh();
        initDb();
        BaseApplication.getInstance().addActivity(this);

        mWifiAdmin = new WifiAdmin(MainActivity.this);
        mangerWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        info = mangerWifi.getConnectionInfo();
        lv.setOnScrollListener(onScrollListener);
        connectDevice();
        lv.setOnItemClickListener(itemlistener);
    }


    /**
     * 解决listview和SwipeRefreshLayout下滑冲突问题
     */
    OnScrollListener onScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            boolean enable = true;
            if (lv != null && lv.getChildCount() > 0) {
                // 检查列表的第一个项目是否可见
                boolean firstItemVisible = lv.getFirstVisiblePosition() == 0;
                // 检查第一个项目的顶部是否可见
                boolean topOfFirstItemVisible = lv.getChildAt(0).getTop() == 0;
                // 启用或禁用刷新布局
                enable = firstItemVisible && topOfFirstItemVisible;
            }
            refresh.setEnabled(enable);
        }
    };

    @Override
    public void finish() {
        super.finish();
        int a = spUtil.getInt(SpKey.deviceControl);
        managerDb.addOrUpdateControl(a, "关闭APP成功", name[new Random().nextInt(4)],
                System.currentTimeMillis());
        spUtil.putInt(SpKey.deviceControl, a + 1);
    }

    /**
     * 初始化数据库
     */
    private void initDb() {
        managerDb = DbManager.getmInstance(this, Constant.dbDiveceBsmk,
                Constant.dbVersion);
        spUtil = SpUtil.getInstance(SpKey.SP_device, MODE_PRIVATE);
        // 日志更新
//		 int a = spUtil.getInt(SpKey.deviceControl);
//		 managerDb.addOrUpdateControl(a, "开启APP成功", name[new
//		 Random().nextInt(4)],
//		 System.currentTimeMillis());
//		 spUtil.putInt(SpKey.deviceControl, a + 1);

        // 安全更新
//		int b = spUtil.getInt(SpKey.deviceError);
//		managerDb.addOrUpdateError(b, error[new Random().nextInt(4)],
//				System.currentTimeMillis());
//		spUtil.putInt(SpKey.deviceError, b + 1);
    }

    /**
     * 初始化viewpager下面的点
     */
    private void initdot() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.main_vp_ll);
        dots = new ImageView[pics.length];
        for (int i = 0; i < pics.length; i++) {
            dots[i] = (ImageView) ll.getChildAt(i);
        }
        dots[0].setPressed(true);
    }

    /**
     * 设置当前点的颜色
     */
    private void setCurrentDot(int currentIndex) {
        dots[currentIndex - 1].setPressed(true);
        dots[dotIndex].setPressed(false);
        dotIndex = currentIndex - 1;
    }

    /**
     * 程序打开自动绑定设备
     */
    private void connectDevice() {

        if (mangerWifi.getConfiguredNetworks() == null) {
            DialogNotileUtil.show(this, "WIFI未开启");
            lv.setVisibility(View.GONE);
        } else {
//            mWifiAdmin.startScan(MainActivity.this);
//            mWifiList = mWifiAdmin.getWifiList();
//            mWifiList = getfilterList(mWifiList);
//            if (mWifiList.size() > 0) {
//                lv.setAdapter(new DeviceAddAdapter(this, mWifiList));
//            }
            deviceBeanList = managerDb.getDeviceList();
            if (deviceBeanList.size() > 0) {
                lv.setAdapter(new DeviceAdapter(this, deviceBeanList));
            } else {
                dialog = DialogCustomUtil.create("提示", "您还未添加设备,快去添加个呗！", this, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SmallUtil.getActivity(MainActivity.this,
                                AddDeviceActivity.class);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

        }
    }


    /**
     * 扫描含有BSMK字符串的ssid
     */
    private List<ScanResult> getfilterList(List<ScanResult> wifiList) {
        List<ScanResult> list = new ArrayList<ScanResult>();
        for (int i = 0; i < wifiList.size(); i++) {
            ScanResult scanResult = wifiList.get(i);
            if ((scanResult.SSID).indexOf("BSMK") != -1) {
                list.add(scanResult);
            }
        }
        if (list.size() == 0) {
            DialogNotileUtil.show(this, "该范围内没有\"BSMK\"设备");
        }
        return list;
    }

    private void initView() {
        findViewById(R.id.main_menu).setOnClickListener(this);
        findViewById(R.id.main_add).setOnClickListener(this);
        findViewById(R.id.main_find).setOnClickListener(this);
        findViewById(R.id.main_user).setOnClickListener(this);
        vp = (ViewPager) findViewById(R.id.main_vp);
        lv = (ListView) findViewById(R.id.main_lv);
        initRightMenu();
        initdot();
        initViewpage();

    }

    /**
     * 初始化SwipeRefreshLayout控件
     */
    private void initRefresh() {
        refresh = (SwipeRefreshLayout) findViewById(R.id.main_srl);
//        boolean refreshing = refresh.isRefreshing();// 判断现在是否在刷新
        // 设置下拉进度条的颜色主题，参数为可变参数，并且是资源id
        refresh.setColorSchemeResources(R.color.blue_deep, R.color.gold,
                R.color.black, R.color.green_deep, R.color.red);
        // 设置下拉进度条的背景颜色，默认白色。
        refresh.setProgressBackgroundColorSchemeColor(getResources().getColor(
                R.color.white));
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                        ToastUtil.showLong("刷新列表成功");
                        refresh.setRefreshing(false);
                    }
                }, 1200);
            }
        });
    }

    /**
     * 初始化SlideMenu
     */
    private void initRightMenu() {
        Fragment leftMenuFragment = new MenuleftFragment();
        setBehindContentView(R.layout.menu_left_frame);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.id_menu_left, leftMenuFragment).commit();

        SlidingMenu menu = getSlidingMenu();
        // menu出现的位置
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置旁边阴影的宽度
        menu.setShadowWidthRes(R.dimen.shadow_width);
        // 设置旁边阴影效果(不是整个menu的效果，就是setShadowWidthRes的宽度那点距离的效果)
        menu.setShadowDrawable(R.drawable.shadow_menu);
        /*设置主菜单的阴影*/
        menu.setOffsetFadeDegree(0.4f);
        // 设置滑动菜单视图的宽度
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.88f);

    }

    /**
     * viewpager初始化
     */
    private void initViewpage() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 6,
                6, TimeUnit.SECONDS);// 自动播放
        vp.addOnPageChangeListener(pageChangeListener);
        list = new ArrayList<>();
        int length = pics.length + 2;
        for (int i = 0; i < length; i++) {
            // 情况一通过layout添加组件
            // ImageView imageView=(ImageView)
            // getLayoutInflater().inflate(R.layout.vpmain_item, null);
            // imageView.setImageResource(pics[i]);
            // 直接new一个出来
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            list.add(imageView);
        }
        setImgRes(length);
    }

    /**
     * 除去第一个和最后一个imageview，其他的imageview依次设置相应的图片，顺序与imgs里的一样
     * 最后再把第一个imageview的图片设置为imgs中的最后一张图片
     * ，把最后一个imageview的图片设置为imgs中的第一张图片（因为向做滑动到第一张时
     * ，再向左滑动就到了最后一张；向右滑动一样，到了最后一张，再向右滑动就到了第一张） 比如：要显示的图片为： A B C
     * D四张图片，此时我们要把它们构造成： D {A B C D}A
     * 中间大括号里的就是要显示的图片，第一个D和最后一个A就是滑动到头时继续再滑动时逻辑上要展示的图片
     */
    private void setImgRes(int length) {
        Picasso.with(this).load(Constant.Image_3).placeholder(R.drawable.image_load).error(R.drawable.image_error).into(list.get(0));
        Picasso.with(this).load(Constant.Image_1).placeholder(R.drawable.image_load).error(R.drawable.image_error).into(list.get(1));
        Picasso.with(this).load(Constant.Image_2).placeholder(R.drawable.image_load).error(R.drawable.image_error).into(list.get(2));
        Picasso.with(this).load(Constant.Image_3).placeholder(R.drawable.image_load).error(R.drawable.image_error).into(list.get(3));
        Picasso.with(this).load(Constant.Image_1).placeholder(R.drawable.image_load).error(R.drawable.image_error).into(list.get(4));


        imgsize = length;
        for (int i = 0; i < length - 2; i++) {
            final int index = i;
//			list.get(i + 1).setImageResource(pics[i]);
            list.get(i + 1).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 为每一张图片添加点击事件
                    if (index + 1 == 1) {
                        Bundle bundle = new Bundle();
                        bundle.putString("url", Constant.urlBaiShengtianmao);
                        SmallUtil.getActivity(MainActivity.this,
                                WebviewActivity.class, bundle);
                    } else if (index + 1 == 2) {
                        Bundle bundle = new Bundle();
                        bundle.putString("url", Constant.urlBaiShengtaobao);
                        SmallUtil.getActivity(MainActivity.this,
                                WebviewActivity.class, bundle);
                    } else if (index + 1 == 3) {
                        Bundle bundle = new Bundle();
                        bundle.putString("url", Constant.urlBaiSheng);
                        SmallUtil.getActivity(MainActivity.this,
                                WebviewActivity.class, bundle);
                    }
                }
            });
        }
//		list.get(0).setImageResource(pics[pics.length - 1]);
//		list.get(list.size() - 1).setImageResource(pics[0]);
        setadapter();
    }

    private void setadapter() {
        vp.setAdapter(new ImageViewAdapter(list));
        vp.setCurrentItem(1);
    }

    OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            currentIndex = position;// 把当前页的索引记住，方便跳转到下一页（这是必须的）
            if (currentIndex != 4) {
                setCurrentDot(currentIndex);
            }
            // Log.d(tag, "main278   " + currentIndex);
        }

        /**
         * 监听viewpager的滑动过程，可获取滑动的百分比（arg1）参数。
         * 这里判断的方法是：当滑动到第索引为0的那一页时（即：在逻辑上是到了第一张图片，此时viewpager会显示索引为0的那张图片
         * （即在视觉效果上是最后一张的图片，因为，第一张过了再向左滑动，就是最后一张
         * ）。如果再这里不做相应的处理，再向左滑动就滑不动了，因为已经到了viewpager的第一张
         * （索引为0），此时我们就要依靠参数arg1的值来判断是否已经完成了滑动到第一张
         * ，当arg1的值为0.0时，即已经滑动完成，此时我们就把viewpager的页面跳转到viewpager的倒数第二张页面上
         * ，使用setcurrentItem
         * （Int,boolean）方法,当Boolean取值为FALSE时，就没有滑动效果，直接跳转过去，由于当前页的图片和要跳转到的页面一样
         * ，所以在视觉效果上看不出闪烁 ，这样就很自然的跳转到了倒数第二张，然后继续向左滑动
         */
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            if (arg1 == 0.0) {
                if (arg0 == 0) {
                    vp.setCurrentItem(imgsize - 2, false);
                } else if (arg0 == imgsize - 1) {
                    vp.setCurrentItem(1, false);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * 来定时播放图片的线程
     */
    private class ViewPagerTask implements Runnable {

        @Override
        public void run() {
            currentIndex++;
            handler.obtainMessage().sendToTarget();
        }
    }

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            // 使viewpager跳转到指定页（true:带有滑动效果）
            vp.setCurrentItem(currentIndex, true);
        }

        ;
    };

    @Override
    public void onBackPressed() {
        BaseApplication.getInstance().exitApp();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (resume) {
//            refresh();
//        }
//        resume = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_add:
                SmallUtil.getActivity(MainActivity.this, AddDeviceActivity.class);
                break;
            case R.id.main_find:
                SmallUtil.getActivity(MainActivity.this, SearchDeviceActivity.class);
                break;
            case R.id.main_menu:
                getSlidingMenu().showMenu();
                break;
            case R.id.main_user:
                SmallUtil.getActivity(MainActivity.this, UsermanagerActivity.class);
                break;

        }

    }

    /**
     * 刷新数据
     */
    public void refresh() {
//        mWifiAdmin.startScan(MainActivity.this);
//        mWifiList = mWifiAdmin.getWifiList();
//        mWifiList = getfilterList(mWifiList);
//        if (mWifiList.size() > 0) {
//            DeviceAddAdapter adapter = (DeviceAddAdapter) lv.getAdapter();
//            if (adapter == null) {
//                lv.setAdapter(new DeviceAddAdapter(this, mWifiList));
//            } else {
//                adapter.setData(mWifiList);
//                adapter.notifyDataSetChanged();
//            }
//        }

        deviceBeanList = managerDb.getDeviceList();
        if (deviceBeanList.size() > 0) {
            DeviceAdapter adapter = (DeviceAdapter) lv.getAdapter();
            if (adapter == null) {
                lv.setAdapter(new DeviceAdapter(this, deviceBeanList));
            } else {
                adapter.setData(deviceBeanList);
                adapter.cleanCache();
                adapter.notifyDataSetChanged();
            }
        } else {
            dialog = DialogCustomUtil.create("提示", "您还未添加设备，是否去添加", this, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SmallUtil.getActivity(MainActivity.this,
                            AddDeviceActivity.class);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    OnItemClickListener itemlistener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
//            ScanResult scanResult = mWifiList.get(position);
//            String ssid = scanResult.SSID;
//            info = mangerWifi.getConnectionInfo();
//            String ssidInfo = info.getSSID().replace("\"", "");
//            Log.e(tag, ssidInfo + "    " + ssid);
//            if (ssid.equals(ssidInfo)) {
//                SmallUtil.getActivity(MainActivity.this,
//                        DeviceControlActivity.class);
//            } else {
//                dialog = DialogCustomUtil.create("提示", "未连接到设备,请添加个吧!",
//                        MainActivity.this, new OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                SmallUtil.getActivity(MainActivity.this,
//                                        AddDeviceActivity.class);
//                                dialog.dismiss();
//                            }
//                        });
//                dialog.show();
//            }

            DeviceBean device = (DeviceBean) parent.getItemAtPosition(position);
            String deviceId = device.getDeviceId();
            //device.isOnline() deviceId.equals(Constant.idFOUR)

            Logs.e(tag + " 536  " + deviceId);

            if (device.isOnline()) {
                Bundle bundle = new Bundle();
                bundle.putString("id", deviceId);
                SmallUtil.getActivity(MainActivity.this, ControlOneActivity.class, bundle);
            } else {
                DialogNotileUtil.show(MainActivity.this, "设备不在线");
            }

        }
    };


}
