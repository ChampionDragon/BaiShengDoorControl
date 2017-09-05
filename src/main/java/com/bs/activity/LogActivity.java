package com.bs.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.bs.R;
import com.bs.adapter.LogAdapter;
import com.bs.base.BaseActivity;
import com.bs.bean.ControlBean;
import com.bs.bean.DeviceManagerBean;
import com.bs.constant.Constant;
import com.bs.constant.SpKey;
import com.bs.db.DbManager;
import com.bs.util.SpUtil;
import com.bs.util.TimeUtil;
import com.bs.util.ToastUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 设备的开关记录
 * 作者
 * created at 2017/6/13
 **/
public class LogActivity extends BaseActivity implements OnClickListener {
    private ListView lv;
    String tag = "lcb";
    private DbManager manager;
    private List<ControlBean> list;
    private List<DeviceManagerBean> deviceManages;
    private LogAdapter adapter;
    private SpUtil spUtil;
    private SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        initView();
        initRefresh();
        initData();
        initLv();
    }

    /**
     * 初始化SwipeRefreshLayout控件
     */
    private void initRefresh() {
        refresh = (SwipeRefreshLayout) findViewById(R.id.log_srl);
        boolean refreshing = refresh.isRefreshing();// 判断现在是否在刷新
        Log.d(tag, refreshing + "");
        // 设置下拉进度条的颜色主题，参数为可变参数，并且是资源id
        refresh.setColorSchemeResources(R.color.blue_deep, R.color.gold,
                R.color.black, R.color.green_deep, R.color.red);
        // 设置下拉进度条的背景颜色，默认白色。
        refresh.setProgressBackgroundColorSchemeColor(getResources().getColor(
                R.color.white));
        refresh.setOnRefreshListener(onRefreshListener);
    }

    /**
     * 下拉刷新监听
     */
    OnRefreshListener onRefreshListener = new OnRefreshListener() {

        @Override
        public void onRefresh() {
            // 开始刷新，设置当前为刷新状态
            // refresh.setRefreshing(true);//经过测试,用户只要做下拉动作自动设为true.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Refresh();
                    ToastUtil.showLong("刷新了一条数据");
                    // 加载完数据设置为不刷新状态，将下拉进度收起来
                    refresh.setRefreshing(false);
                }
            }, 3200);
            // 这个不能写在外边，不然会直接收起来
            // refresh.setRefreshing(false);
        }
    };

    private void initLv() {
        adapter = new LogAdapter(this, deviceManages);
        lv.setAdapter(adapter);
        lv.setOnScrollListener(onScrollListener);
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
            boolean enable = false;
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

    /**
     * 初始化数据
     */
    private void initData() {
        spUtil = SpUtil.getInstance(SpKey.SP_device, MODE_PRIVATE);
        manager = DbManager.getmInstance(this, Constant.dbDiveceBsmk,
                Constant.dbVersion);
        list = manager.getControlList();
        deviceManages = getData(list);
    }

    /**
     * 过滤数据
     */
    private List<DeviceManagerBean> getData(List<ControlBean> list) {
        Map<String, List<ControlBean>> map = new LinkedHashMap<String, List<ControlBean>>();
        String key = "";
        List<ControlBean> deviceBeans = null;
        List<DeviceManagerBean> deviceManagerBeans = new ArrayList<>();
        for (int i = list.size() - 1; i > 0; i--) {
            ControlBean bean = list.get(i);
            String data = TimeUtil.long2time(bean.getCreattime(),
                    Constant.cformatD);
            boolean b = key.equals(data);
            if (!b) {
                deviceBeans = new ArrayList<ControlBean>();
                key = data;
            }
            deviceBeans.add(bean);
            map.put(key, deviceBeans);
        }
        Set<Entry<String, List<ControlBean>>> entrySet = map.entrySet();
        for (Entry<String, List<ControlBean>> entry : entrySet) {
            deviceManagerBeans.add(new DeviceManagerBean(entry.getKey(), entry
                    .getValue()));
        }
        return deviceManagerBeans;
    }

    private void initView() {
        lv = (ListView) findViewById(R.id.log_lv);
        findViewById(R.id.refresh_log).setOnClickListener(this);
        findViewById(R.id.back_log).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_log:
                finish();
                break;
            case R.id.refresh_log:

                break;

        }
    }

    private void Refresh() {
        int a = spUtil.getInt(SpKey.deviceControl);
        manager.addOrUpdateControl(a, "设备调试", "龙成斌", System.currentTimeMillis());
        list = manager.getControlList();
        deviceManages = getData(list);
        spUtil.putInt(SpKey.deviceControl, a + 1);
        Refresh(deviceManages);
    }

    /**
     * 刷新数据
     *
     * @param list 要更新的数据源
     */
    private void Refresh(List<DeviceManagerBean> list) {
        adapter = (LogAdapter) lv.getAdapter();
        if (adapter == null) {
            adapter = new LogAdapter(this, list);
            lv.setAdapter(adapter);
        } else {
            adapter.setData(list);
            adapter.notifyDataSetChanged();
        }
    }

}
