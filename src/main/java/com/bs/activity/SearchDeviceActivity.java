package com.bs.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.bean.DeviceBean;
import com.bs.constant.Constant;
import com.bs.db.DbManager;
import com.bs.http.HttpByGet;
import com.bs.util.DialogNotileUtil;
import com.bs.util.Logs;
import com.bs.util.SmallUtil;
import com.bs.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 通过设备ID搜索设备
 * AUTHOR: Champion Dragon
 * created at 2017/8/8
 **/
public class SearchDeviceActivity extends BaseActivity implements View.OnClickListener {
    private SearchView searchView;
    private String[] mStrs;
    private ListView mListView;
    private ArrayAdapter adapter;
    private DbManager managerDb;
    private List<DeviceBean> deviceBeanList;
    private List<String> data;
    String tag = "searchDevice";
    public static final int ONLINE = 0;
    public static final int OFFLINE = 1;
    public static final int ERROR = 2;
    String deviceId;
    boolean ischeck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);
        initDb();
        initView();
    }

    private void initDb() {
        managerDb = DbManager.getmInstance(this, Constant.dbDiveceBsmk,
                Constant.dbVersion);
        deviceBeanList = managerDb.getDeviceList();
        data = new ArrayList<>();
        int size = deviceBeanList.size();
        for (int i = 0; i < size; i++) {
            String deviceId = deviceBeanList.get(i).getDeviceId();
            data.add(deviceId);
        }
        mStrs = data.toArray(new String[size]);

    }

    private void initView() {
        searchView = (SearchView) findViewById(R.id.searchdevice_searchView);
      /*  设置搜索栏的默认状态或者静止状态。如果是true，当被按下时，一个单一的搜索图标就会被默认显示，
        同时显示文本字段和其他按钮。如果默认的状态是图标，在按下关闭按钮时它就会收缩成那个状态。这个属性的改变会立即生效。
        参数
        iconified 搜索栏是否默认被图标化*/
        searchView.setIconifiedByDefault(true);
      /* 图标化或者展开SearchView。当图标化时任何查询条件都被清除。这是一个临时的状态，
        不会重写被setIconifiedByDefault(boolean)设置的默认图标状态。如果默认的是图标化状态，则在用户关闭这个区域前都是false。
        如果默认的是展开状态，这里就是true，同时清除文本区域，但不关闭它。
        参数
        iconify true值会把SearchView收缩成一个图标，false值会展开它*/
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(listener);

        mListView = (ListView) findViewById(R.id.searchdevice_lv);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mStrs);
        mListView.setAdapter(adapter);
        //必须开启，否则不会过滤
        mListView.setTextFilterEnabled(true);
        mListView.setOnItemClickListener(itemClickListener);

        findViewById(R.id.back_searchdevice).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_searchdevice:
                finish();
                break;
        }
    }


    /**
     * 设置searchView监听
     */
    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        // 当点击搜索按钮时触发该方法
        @Override
        public boolean onQueryTextSubmit(String query) {
            ToastUtil.showLong(query);
            return false;
        }

        // 当搜索内容改变时触发该方法
        @Override
        public boolean onQueryTextChange(String newText) {
            if (!TextUtils.isEmpty(newText)) {
                /*这样写会有黑色的弹框出现*/
//                mListView.setFilterText(newText);
                adapter.getFilter().filter(newText);
            } else {
                adapter.getFilter().filter(null);
//                adapter.getFilter().filter("龙成斌");
//                mListView.clearTextFilter();

            }
            return false;
        }
    };


    /**
     * 获取点击的数据
     */
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String s = (String) parent.getItemAtPosition(position);
            if (ischeck) {
                ToastUtil.showLong("正在查询设备是否在线不可再次查询");
            } else {
                ischeck = true;
                deviceId = s;
                executor.submit(checkRunnable);
            }
        }
    };


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ONLINE:
                    Bundle bundle = new Bundle();
                    bundle.putString("id", deviceId);
                    SmallUtil.getActivity(SearchDeviceActivity.this, ControlOneActivity.class, bundle);
                    finish();
                    break;
                case OFFLINE:
                    SpannableString spannableString = new SpannableString("控制失败\n\n设备号" + deviceId + "不在线");
                    RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(1.5f);//设置字体大小
                    RelativeSizeSpan relativeSizeSpan1 = new RelativeSizeSpan(1.3f);//设置字体大小
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.red));//设置颜色
                    spannableString.setSpan(relativeSizeSpan1, 9, deviceId.length() + 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(colorSpan, 9, deviceId.length() + 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(relativeSizeSpan, 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                    DialogNotileUtil.show(SearchDeviceActivity.this, spannableString);
                    ischeck = false;
                    break;
                case ERROR:
                    DialogNotileUtil.show(SearchDeviceActivity.this, "网络问题或服务器异常");
                    ischeck = false;
                    break;
            }
        }
    };


    Runnable checkRunnable = new Runnable() {
        @Override
        public void run() {
                  /*通过查询设备是否在线*/
            String url = Constant.isOnline + HttpByGet.get("deviceid", deviceId + ".");
            String s = HttpByGet.executeHttpGet(url);
            Logs.v(tag + " 175  " + url + "\n" + s + "   length" + s.length());
            if (s.equals(" 1")) {  //返回的数据里有空格,所以1前面我空了一个格
                handler.sendEmptyMessage(ONLINE);
            } else if (s.equals(" 0")) {
                handler.sendEmptyMessage(OFFLINE);
            } else {
                handler.sendEmptyMessage(ERROR);
            }
        }
    };


}
