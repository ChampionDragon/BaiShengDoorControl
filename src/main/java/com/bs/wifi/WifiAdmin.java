package com.bs.wifi;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bs.util.DialogCustomUtil;
import com.bs.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * WiFi帮助类
 */
public class WifiAdmin {
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义一个WifiLock
    WifiManager.WifiLock mWifiLock;
    private String TAG = "lcb";
    private Context context;
    private Dialog dialog;

    public WifiAdmin(Context context) {
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
        this.context = context;

    }

    // 打开WIFI
    public void openWifi(Context context) {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        } else if (mWifiManager.getWifiState() == 2) {
            Toast.makeText(context, "亲，Wifi正在开启，不用再开了", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(context, "亲，Wifi已经开启,不用再开了", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    // 关闭wifi
    public void closeWifi(Context context) {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        } else if (mWifiManager.getWifiState() == 1) {
            Toast.makeText(context, "亲，Wifi已经关闭，不用再关了", Toast.LENGTH_SHORT)
                    .show();
        } else if (mWifiManager.getWifiState() == 0) {
            Toast.makeText(context, "亲，Wifi正在关闭，不用再关了", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(context, "请重新关闭", Toast.LENGTH_SHORT).show();
        }
    }

    // 检查当前WIFI状态
    public void checkState(Context context) {
        if (mWifiManager.getWifiState() == 0) {
            Toast.makeText(context, "Wifi正在关闭", Toast.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == 1) {
            Toast.makeText(context, "Wifi已经关闭", Toast.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == 2) {
            Toast.makeText(context, "Wifi正在开启", Toast.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == 3) {
            Toast.makeText(context, "Wifi已经开启", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "没有获取到WiFi状态", Toast.LENGTH_SHORT).show();
        }
    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建一个WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    /**
     * @param context
     */
    public void startScan(Context context) {
        mWifiManager.startScan();
        // 得到扫描结果
        List<ScanResult> results = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
        if (results == null) {
            if (mWifiManager.getWifiState() == 3) {
                Toast.makeText(context, "当前区域没有无线网络", Toast.LENGTH_SHORT)
                        .show();
            } else if (mWifiManager.getWifiState() == 2) {
                Toast.makeText(context, "wifi正在开启，请稍后扫描", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(context, "WiFi没有开启", Toast.LENGTH_SHORT).show();
            }
        } else {
            mWifiList = new ArrayList<ScanResult>();
            for (ScanResult result : results) {
                if (result.SSID == null || result.SSID.length() == 0
                        || result.capabilities.contains("[IBSS]")) {
                    continue;
                }
                boolean found = false;
                // Log.i("lcb", "result= " + result.SSID + " capabilities= "
                // + result.capabilities+" admin153");

                // 如果扫描到重复的ssid就终止添加到扫描出的网络连接列表mWifiList
                for (ScanResult item : mWifiList) {
                    if (item.SSID.equals(result.SSID)
                            && item.capabilities.equals(result.capabilities)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    mWifiList.add(result);
                }
            }
        }
    }

    // 得到网络列表
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    // 查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder
                    .append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    // 得到本机MAC地址
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 得到接入点的BSSID（接入WIFI的MAC地址）
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // 得到连接的IP
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 得到连接的ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    // 添加一个网络并连接
    public void addNetwork(final WifiConfiguration wcg) {
        // 返回值为-1证明List<WifiConfiguration>未添加,其它数字源码里面mService.addOrUpdateNetwork(config)的一个值不是WifiConfiguration.networkId值
        int wcgID = mWifiManager.addNetwork(wcg);
        // b返回true只是让WifiManager去执行连接的命令，不代表连接成功了
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        // 第二个参数true表示如果当前已经有连上一个wifi,要强制连到自己设定的wifi上，此参数必须为true否则连上的还是原来的wifi.
        ToastUtil.showLong("正在连接，请稍等");
        Log.d(TAG, "admin228      状态：" + wcg.status);
        Log.v(TAG, "admin229    " + wcgID);
    }

    // 断开指定ID的网络
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    /**
     * 删除在List<WifiConfiguration>保存的热点信息
     */
    public void removeWifi(int netId) {
        disconnectWifi(netId);
        mWifiManager.removeNetwork(netId);
//		 mWifiManager.saveConfiguration();//如果无法移除以保存的wifi热点就加上这句
    }

    // 创建wifi热点的。

    public WifiConfiguration CreateWifiInfo(String SSID, String Password,
                                            int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        // WifiConfiguration tempConfig = IsExsits(SSID);
        WifiConfiguration tempConfig;
        WifiConfiguration isExsits = IsExsits(SSID);
        if (isExsits != null) {
            Log.d(TAG, "admin263");
            tempConfig = isExsits;
            mWifiManager.removeNetwork(tempConfig.networkId);// 移除之前已连接过的热点的id,如果不移除
            // 在Android 6.0 中WiFiManager addNetwork(WifiConfiguration
            // config)，添加同一ssid时会返回-1，
            // 这个时候你再将这个-1 （NetWorkId）传进enableNetwork（-1，true），肯定连不上WiFi。
        }

        // if (tempConfig != null) {
        // mWifiManager.removeNetwork(tempConfig.networkId);//移除指定热点
        // }

        if (Type == 1) // 没有密码
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) // 用wep加密
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3)// 用wpa加密
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * 判断指定ssid是否存在
     *
     * @param SSID 接入点的名字
     * @return 返回指定ssid的WifiConfiguration:类主要提供一个wifi配置的所有信息
     */
    // 如果手机在未开启wifi的情况下，mWifiManager.getConfiguredNetworks()返回的是空。
    public WifiConfiguration IsExsits(String SSID) {
        if (mWifiManager.getConfiguredNetworks() != null) {
            List<WifiConfiguration> existingConfigs = mWifiManager
                    .getConfiguredNetworks();// 得到所有配置好的网络连接
            for (WifiConfiguration existingConfig : existingConfigs) {
                // Log.d(TAG, ++a + existingConfig.SSID + "  " + SSID
                // + " admin323   " + existingConfigs.size());
                // log是判断List<WifiConfiguration>有几个
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        } else {
            dialog = DialogCustomUtil.create("警告", "亲，你确定在不开WIFI的情况下能连到网络？",
                    context, new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        }
        return null;
    }
}
