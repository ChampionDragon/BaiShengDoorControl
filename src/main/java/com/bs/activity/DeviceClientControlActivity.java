package com.bs.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.constant.Constant;
import com.bs.socket.Protocol;
import com.bs.socket.TCPClient;
import com.bs.socket.TCPThread;
import com.bs.util.DialogNotileUtil;
import com.bs.util.SmallUtil;

/**
 * 通过连接服务器控制设备 作者 lcb created at 2017/5/13
 **/
public class DeviceClientControlActivity extends BaseActivity {

    private EditText edtSvrIpAddress, edtSvrPort;
    private Button btnConnect;
    private TextView txtSSID, ip;
    private TextView txtResult, wifi_ip;
    private TextView txtDeviceState;
    private Button btnGetDeviceState, btnOpenDevice, btnCloseDevice;
    private Button btnTestRes;
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private String IP_one, IP_two, IP_wifi;

    private String strSSID = null;
    TCPThread tcpThread = null;
    boolean bConnecting = false;
    private DhcpInfo mDhcpInfo;
    String tag = "lcb";
    long first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_client_control);
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        tcpThread = new TCPThread();
        setupComponent();
        IntentFilter filter = new IntentFilter(
                WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(controlReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(controlReceiver);
        if (tcpThread != null) {
            tcpThread.stopReceive();
            tcpThread.disCOnnect();
        }
    }

    private void setupComponent() {
        ip = (TextView) findViewById(R.id.ip);
        wifi_ip = (TextView) findViewById(R.id.wifi_ip);
        edtSvrIpAddress = (EditText) findViewById(R.id.edtSvrIpAddress);
        edtSvrPort = (EditText) findViewById(R.id.edtSvrPort);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        txtSSID = (TextView) findViewById(R.id.txtSSID);
        txtResult = (TextView) findViewById(R.id.txtResult);
        txtDeviceState = (TextView) findViewById(R.id.txtDeviceState);
        btnGetDeviceState = (Button) findViewById(R.id.btnGetDeviceState);
        btnOpenDevice = (Button) findViewById(R.id.btnOpenDevice);
        btnCloseDevice = (Button) findViewById(R.id.btnCloseDevice);
        btnTestRes = (Button) findViewById(R.id.btnTestRes);
        findViewById(R.id.back_clientcontrol).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        btnConnect.setOnClickListener(btnConnectClkLis);
        btnGetDeviceState.setOnClickListener(btnGetDeviceStateClkLis);
        btnOpenDevice.setOnClickListener(btnOpenDeviceClkLis);
        btnCloseDevice.setOnClickListener(btnCloseDeviceClkLis);
        btnTestRes.setOnClickListener(btnTestResClkLis);
        InitWifi();

    }

    /**
     * 初始化WIFI的信息
     */
    private void InitWifi() {
        wifiInfo = wifiManager.getConnectionInfo();
        strSSID = wifiInfo.getSSID();
        mDhcpInfo = wifiManager.getDhcpInfo();
        IP_one = SmallUtil.intToString(wifiInfo.getIpAddress());
        IP_two = SmallUtil.intToString(mDhcpInfo.ipAddress);
        IP_wifi = SmallUtil.intToString(mDhcpInfo.serverAddress);
        strSSID = strSSID.replace("\"", "");
        ip.setText(IP_two);
        // edtSvrIpAddress.setText(IP_one);
        wifi_ip.setText(IP_wifi);
        txtSSID.setText(strSSID);
    }

    // 连接TCP服务端
    private Button.OnClickListener btnConnectClkLis = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            String strIpAddress = edtSvrIpAddress.getText().toString();
            String strPort = edtSvrPort.getText().toString();
            if (strIpAddress.isEmpty()) {
                Toast.makeText(DeviceClientControlActivity.this, "请输入服务端IP地址",
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (strPort.isEmpty()) {
                Toast.makeText(DeviceClientControlActivity.this, "请输入服务端端口号",
                        Toast.LENGTH_LONG).show();
                return;
            }

			/*
             * if (tcpThread != null) { tcpThread.stopReceive();
			 * tcpThread.disCOnnect(); tcpThread.setServer(strIpAddress,
			 * Integer.parseInt(strPort)); } else { tcpThread = new
			 * TCPThread(strIpAddress, Integer.parseInt(strPort)); }
			 * tcpThread.setHandler(handler); tcpThread.connect();
			 * tcpThread.startReceive(); btnConnect.setEnabled(false);
			 */

            if (tcpThread == null) {
                connect(strIpAddress, Integer.parseInt(strPort));
            } else if (tcpThread.isConnected()) {
                tcpThread.stopReceive();
                tcpThread.disCOnnect();
                bConnecting = true;
            } else {
                connect(strIpAddress, Integer.parseInt(strPort));
            }
            btnConnect.setEnabled(false);
        }
    };

    private void connect(String strSvrIpAddress, int nSvrPort) {
        if (tcpThread == null) {
            tcpThread = new TCPThread();
        }

        tcpThread.setServer(strSvrIpAddress, nSvrPort);
        tcpThread.setHandler(handler);
        tcpThread.connect();
        tcpThread.startReceive();
    }

    // 获取设备状态
    private Button.OnClickListener btnGetDeviceStateClkLis = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!tcpThread.isConnected()) {
                return;
            }

            byte[] packet = Protocol.getDeviceReqPacket("LIGHT:?");
            tcpThread.send(packet, packet.length);
        }
    };

    // 打开设备
    private Button.OnClickListener btnOpenDeviceClkLis = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!tcpThread.isConnected()) {
                return;
            }

            byte[] packet = Protocol.getDeviceReqPacket("LIGHT:1");
            tcpThread.send(packet, packet.length);
        }
    };

    // 关闭设备
    private Button.OnClickListener btnCloseDeviceClkLis = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!tcpThread.isConnected()) {
                return;
            }

            byte[] packet = Protocol.getDeviceReqPacket("LIGHT:0");
            tcpThread.send(packet, packet.length);
        }
    };

    // 发送响应数据包作测试（LIGHT:0）
    private Button.OnClickListener btnTestResClkLis = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!tcpThread.isConnected()) {
                return;
            }

            byte[] packet = Protocol.getDeviceResPacket("LIGHT:0");
            tcpThread.send(packet, packet.length);
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case TCPThread.MSG_TCPCONNECT_END:
                    if (tcpThread.isConnected()) {
                        txtResult.setText(txtResult.getText().toString()
                                + "连接服务器成功！\n");
                        txtDeviceState.setText("已连接");
                    } else {
                        txtResult.setText(txtResult.getText().toString()
                                + "连接服务器失败！\n");
                    }
                    btnConnect.setEnabled(true);
                    break;
                case TCPThread.MSG_TCPDISCONNECT_END:
                    txtResult
                            .setText(txtResult.getText().toString() + "断开服务器连接！\n");
                    txtDeviceState.setText("未连接");

                    if (bConnecting) {
                        connect(edtSvrIpAddress.getText().toString(),
                                Integer.parseInt(edtSvrPort.getText().toString()));
                        bConnecting = false;
                    }
                    btnConnect.setEnabled(true);
                    break;
                case TCPClient.MSG_TCPREVEIVE: {
                    txtResult
                            .setText(txtResult.getText().toString() + "接收到响应数据！\n");
                    byte[] packet = msg.getData().getByteArray(
                            "KEY_BYTEARRAY_TCPRECEIVER");

                    String strPacket = new String(packet) + "|| len = "
                            + new String(Integer.toString(packet.length));
                    txtResult.setText(txtResult.getText().toString() + strPacket
                            + "\n");

                    byte[] data = Protocol.getDeviceResData(packet, packet.length);
                    if (data == null) {
                        break;
                    }

                    String strRes = new String(data);
                    txtResult.setText(txtResult.getText().toString() + strRes
                            + "\n");
                    if (strRes.equals("LIGHT:1")) {
                        txtDeviceState.setText("打开");
                    } else if (strRes.equals("LIGHT:0")) {
                        txtDeviceState.setText("关闭");
                    } else {
                        txtDeviceState.setText("未知");
                    }
                }
                break;
            }
        }
    };

    // 监听wifi状态变化
    private BroadcastReceiver controlReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SystemClock.sleep(777);// 让系统睡眠一段时间
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            // .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            int wifiState = wifiManager.getWifiState();
            long second = System.currentTimeMillis();
            Log.d(tag, "state:      " + wifiState);
            if (wifiState == 2 || wifiState == 3) {
                if (networkInfo != null
                        && networkInfo.isConnectedOrConnecting()) {
                    InitWifi();
                    if (second - first > Constant.BroadcastReceiverTime) {
                        DialogNotileUtil.show(DeviceClientControlActivity.this,
                                strSSID + "连接成功");
                        first = second;
                    }
                } else {
                    ip.setText("设备断开");
                    edtSvrIpAddress.setText("设备断开");
                    wifi_ip.setText("设备断开");
                    txtSSID.setText("设备断开");
                    if (second - first > Constant.BroadcastReceiverTime) {
                        DialogNotileUtil.show(DeviceClientControlActivity.this,
                                "设备断开");
                        first = second;
                    }
                }
            } else {
                ip.setText("WIFI关闭");
                edtSvrIpAddress.setText("WIFI关闭");
                wifi_ip.setText("WIFI关闭");
                txtSSID.setText("WIFI关闭");
                if (second - first > Constant.BroadcastReceiverTime) {
                    DialogNotileUtil.show(DeviceClientControlActivity.this,
                            "WIFI关闭");
                    first = second;
                }
            }

        }

    };

}
