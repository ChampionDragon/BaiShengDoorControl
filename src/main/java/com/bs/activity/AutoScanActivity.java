package com.bs.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.base.BaseApplication;
import com.bs.constant.SpKey;
import com.bs.util.DialogNotileUtil;
import com.bs.util.SpUtil;
import com.bs.wifi.WifiAdmin;

import java.util.ArrayList;
import java.util.List;

public class AutoScanActivity extends BaseActivity implements OnClickListener {
    private ImageView iv;
    private ListView lv;
    private WifiAdmin mWifiAdmin;
    private List<ScanResult> mWifiList;
    private String ssid;
    private String TAG = "lcb";
    private int level = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoscan);
        initView();
        mWifiAdmin = new WifiAdmin(AutoScanActivity.this);
        IntentFilter filter = new IntentFilter(
                WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
        connectDevice();
    }

    private void initView() {
        iv = (ImageView) findViewById(R.id.back_autoscan);
        iv.setOnClickListener(this);
        lv = (ListView) findViewById(R.id.device_lv);
        lv.setOnItemClickListener(mItemListen);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_autoscan:
                finish();
                break;

        }
    }

    /**
     * 程序打开自动绑定设备
     */
    private void connectDevice() {
        mWifiAdmin.startScan(AutoScanActivity.this);
        mWifiList = mWifiAdmin.getWifiList();
        // mWifiList = getfilterList(mWifiList);
        if (mWifiList.size() > 0) {
            lv.setAdapter(new MyAdapter(this, mWifiList));
            new Utility().setListViewHeightBasedOnChildren(lv);
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
        if (list.size() < 0) {
            DialogNotileUtil.show(this, "未扫描到\"BSMK\"设备");
        }

        return list;
    }

    public class MyAdapter extends BaseAdapter {
        LayoutInflater inflater;
        List<ScanResult> list;

        public MyAdapter(Context context, List<ScanResult> list) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            view = inflater.inflate(R.layout.item_wifilisst, null);
            ScanResult scanResult = list.get(position);
            TextView wifi_ssid = (TextView) view.findViewById(R.id.ssid);
            ImageView wifi_level = (ImageView) view
                    .findViewById(R.id.wifi_level);
            // if ((scanResult.SSID).indexOf("BSMK")!=-1) {
            wifi_ssid.setText(scanResult.SSID);
//			Log.i(TAG, "scanResult.SSID=" + scanResult.SSID);// 遍历所有热点数据
            // 设置接收wifi的等级信号
            level = WifiManager.calculateSignalLevel(scanResult.level, 5);
            if (scanResult.capabilities.contains("WEP")
                    || scanResult.capabilities.contains("PSK")
                    || scanResult.capabilities.contains("EAP")) {
                wifi_level.setImageResource(R.drawable.wifi_signal_lock);
            } else {
                wifi_level.setImageResource(R.drawable.wifi_signal_open);
            }
            // 判断信号强度，显示对应的指示图标  
            wifi_level.setImageLevel(level);
            // }
            return view;
        }
    }

    /* 设置listview的高度 */
    public class Utility {
        public void setListViewHeightBasedOnChildren(ListView listView) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                return;
            }
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                // 计算子项View 的宽高
                listItem.measure(0, 0);
                // 统计所有子项的总高度
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight
                    + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            // listView.getDividerHeight()获取子项间分隔符占用的高度
            // params.height最后得到整个ListView完整显示需要的高度
            listView.setLayoutParams(params);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        unregisterReceiver(mReceiver);
    }

    /**
     * listview监听
     */
    AdapterView.OnItemClickListener mItemListen = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            AlertDialog.Builder alert = new AlertDialog.Builder(
                    AutoScanActivity.this);
            ssid = mWifiList.get(position).SSID;
            alert.setTitle(ssid);
            alert.setMessage("输入密码");
            final EditText et_password = new EditText(AutoScanActivity.this);
            et_password.setBackgroundResource(R.drawable.wifi_bg);
            // 保留上次ssid的密码
            final SpUtil sp = SpUtil.getInstance(SpKey.SP_wifiname,
                    Context.MODE_PRIVATE);
            et_password.setText(sp.getString(ssid));

            alert.setView(et_password);
            alert.setPositiveButton("连接",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String pw = et_password.getText().toString();
                            if (TextUtils.isEmpty(pw)) {
                                Toast.makeText(AutoScanActivity.this, "密码不能为空",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            sp.putString(ssid, pw);
                            mWifiAdmin.addNetwork(mWifiAdmin.CreateWifiInfo(
                                    ssid, et_password.getText().toString(), 3));
                            WifiManager wifiManager = (WifiManager) BaseApplication.context
                                    .getSystemService(Context.WIFI_SERVICE);
                            if (wifiManager.getWifiState() == 3) {
                                checkWifiConfiguration();
                            }
                        }
                    });
            alert.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            alert.create();
            alert.show();

        }
    };

    // 监听wifi状态变化
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = manager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo.State wifi = networkInfo.getState();// 得到当前WiFi的状态
            // NetworkInfo.DetailedState detailedState =
            // networkInfo.getDetailedState();// 得到网络状态的详细内容
            if (NetworkInfo.State.DISCONNECTED == wifi) {
//				ToastUtil.showLong("设备断开");
            }
            if (networkInfo.isConnected()) {
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                String wifiSSID = wifiManager.getConnectionInfo().getSSID();
                DialogNotileUtil.show(AutoScanActivity.this, wifiSSID + "连接成功");
            }
        }

    };

    /**
     * 判断连接的ssid是否连上网(原理其实就是8秒后判断网络是否可用）
     */
    private void checkWifiConfiguration() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (!networkInfo.isConnected()) {
                    DialogNotileUtil.show(AutoScanActivity.this, "密码错误，请重新再尝试");
                }
            }
        }, 8888);
    }
}
