package com.bs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.bs.R;
import com.bs.adapter.DeviceSetAdapter;
import com.bs.base.BaseActivity;
import com.bs.bean.DeviceBean;
import com.bs.bean.WifiBean;
import com.bs.constant.Constant;
import com.bs.db.DbManager;
import com.bs.util.DialogNotileUtil;
import com.bs.util.SmallUtil;
import com.bs.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class DeviceManager extends BaseActivity implements OnClickListener {
    private ListView lv;
    private List<WifiBean> listWifi;
    private List<WifiBean> checks;
    private DeviceSetAdapter adapter;
    private DbManager managerDB;
    public static final int NAME = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_manage);
        initView();
    }

    private void initView() {
        findViewById(R.id.back_manage).setOnClickListener(this);
        findViewById(R.id.manage_close).setOnClickListener(this);
        findViewById(R.id.manage_open).setOnClickListener(this);
        findViewById(R.id.manage_pause).setOnClickListener(this);
        findViewById(R.id.manage_add).setOnClickListener(this);
        lv = (ListView) findViewById(R.id.manage_lv);
        managerDB = DbManager.getmInstance(this, Constant.dbDiveceBsmk, Constant.dbVersion);
//        initdata();
//        adapter = new DeviceSetAdapter(this, listWifi);
//        lv.setAdapter(adapter);
        lv.setOnItemClickListener(itemlistener);
    }

    private void initdata() {
        listWifi = new ArrayList<>();
        List<DeviceBean> deviceList = managerDB.getDeviceList();
        for (DeviceBean device : deviceList) {
            listWifi.add(new WifiBean(device.getName(), device.getDeviceId()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_manage:
                finish();
                break;
            case R.id.manage_close:
                close();
                break;
            case R.id.manage_open:
                open();
                break;
            case R.id.manage_pause:
                pause();
                break;
            case R.id.manage_add:
                SmallUtil.getActivity(DeviceManager.this, AddDeviceActivity.class);
                break;

        }
    }

    /**
     * 批量暂停的相关操作
     */
    private void pause() {
        String s = "设备:";
        checks = getchecks();
        if (checks.size() == 0) {
            DialogNotileUtil.show(this, "请至少选中一台设备,再做\"批量暂停\"操作");
        } else {
            for (WifiBean bean : checks) {
                s += bean.getKey() + "   ";
            }
            ToastUtil.showLong(s + ",批量暂停");
        }
    }

    /**
     * 批量关闭的相关操作
     */
    private void close() {
        String s = "设备:";
        checks = getchecks();
        if (checks.size() == 0) {
            DialogNotileUtil.show(this, "请至少选中一台设备,再做\"批量关闭\"操作");
        } else {
            for (WifiBean bean : checks) {
                s += bean.getKey() + " ";
            }
            ToastUtil.showLong(s + ",批量关闭");
        }
    }

    /**
     * 批量开启的相关操作
     */
    private void open() {
        String s = "设备:";
        checks = getchecks();
        if (checks.size() == 0) {
            DialogNotileUtil.show(this, "请至少选中一台设备,再做\"批量开启\"操作");
        } else {
            for (WifiBean bean : checks) {
                s += bean.getKey() + " ";
            }
            ToastUtil.showLong(s + ",批量开启");
        }
    }

    /**
     * 返回选中的设备
     */
    private List<WifiBean> getchecks() {
        List<WifiBean> checks = new ArrayList<>();
        for (WifiBean bean : listWifi) {
            if (bean.getCheck()) {
                checks.add(bean);
            }
        }
        return checks;
    }

    OnItemClickListener itemlistener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            boolean a = listWifi.get(position).getCheck();
            if (!a) {
                view.setBackgroundColor(getResources().getColor(R.color.gray));
                listWifi.get(position).setCheck(true);
            } else {
                view.setBackgroundColor(getResources()
                        .getColor(R.color.bg_main));
                listWifi.get(position).setCheck(false);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case NAME:
                    String name = data.getExtras().getString("name");
                    int position = data.getExtras().getInt("position");
                    listWifi.get(position).setKey(name);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    /**
     * 数据更新刷新列表
     */
    private void refreshData() {
        initdata();
        adapter = (DeviceSetAdapter) lv.getAdapter();
        if (adapter == null) {
            adapter = new DeviceSetAdapter(this, listWifi);
            lv.setAdapter(adapter);
        } else {
            adapter.setData(listWifi);
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }


}
