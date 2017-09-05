package com.bs.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.constant.Constant;
import com.bs.constant.SpKey;
import com.bs.socket.TestProtocol;
import com.bs.socket.UDPThread;
import com.bs.util.DialogNotileUtil;
import com.bs.util.Logs;
import com.bs.util.SpUtil;
import com.bs.util.ToastUtil;

import java.util.Arrays;
import java.util.Random;

public class UdpControlActivity extends BaseActivity implements OnClickListener {
    private WifiManager wifiManager;
    private UDPThread udp;
    private long first;
    private String strSSID;
    private WifiInfo wifiInfo;
    private TextView txtResult;
    private String test = "1";
    private static int delaySend = 444;
    private int seq;// 随机数
    private int cmd;// 指令1：OPEN ,CLOSE:0，STOP:2
    private String cmdStr;
    private String IdDevice;
    private String IP;
    private int Port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udpcontrol);
        initView();
        initUDP();
        wifiInfo = wifiManager.getConnectionInfo();
        strSSID = wifiInfo.getSSID();
        IntentFilter filter = new IntentFilter(
                WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(controlReceiver, filter);
//        IdDevice = Constant.idTWO;
        IP = Constant.ipIP;
        Port = Constant.ipPort;
        initAlertDialog();
    }


    private void initAlertDialog() {
        AlertDialog.Builder alert = new Builder(this);
        alert.setTitle("请输入设备的ID");
        final EditText et_password = new EditText(this);

        et_password.setBackgroundResource(R.drawable.wifi_bg);
        final SpUtil sp = SpUtil.getInstance(SpKey.SP_device,
                Context.MODE_PRIVATE);
        et_password.setText(sp.getString(SpKey.deviceID));
        alert.setView(et_password);

        alert.setPositiveButton("连接", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pw = et_password.getText().toString();
                if (TextUtils.isEmpty(pw)) {
                    Toast.makeText(UdpControlActivity.this, "密码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                IdDevice = pw;
                sp.putString(SpKey.deviceID, pw);
                Toast.makeText(UdpControlActivity.this, "设备ID保存成功",
                        Toast.LENGTH_SHORT).show();

                DynamicIP();//输入完ID就去查询


            }
        });
        alert.create();
        alert.show();
    }

    /**
     * 向后台传输测试用的心跳包
     */
    private void initHeart() {
        byte[] heartpacket = TestProtocol.getHeartpacket(test);
        txtResult.setText(txtResult.getText().toString() + "心跳的数据包：\n"
                + Arrays.toString(heartpacket) + "\n");
        udp.send(heartpacket);

    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UDPThread.MSG_UDP_RECEIVE:
                    StringBuffer sb = new StringBuffer();
                    Bundle data = msg.getData();
                    String ip = (String) data.get(UDPThread.KEYUDPRECIP);
                    Integer port = (Integer) data.get(UDPThread.KEYUDPRECPORT);

                    IP = ip;
                    Port = port;

                    String receiver = data.getString(UDPThread.KEYUDPRECEIVE);
                    sb.append(ip).append("\n").append(port).append("\n")
                            .append("返回的数据结果:").append("\n")
                            .append(receiver).append("\n");
                    Logs.d(sb.toString());//后台返回的字符串
                    judgement(receiver);
                    txtResult.setText(txtResult.getText().toString() + sb);
                    break;
            }

        }

    };

    /**
     * 判断指令发送是否成功
     */
    private void judgement(String receiver) {
        if (receiver.indexOf(Constant.cmdClose) >= 0) {
//            Logs.d("关门操作成功");
            ToastUtil.showLong("关门操作成功");
        } else if (receiver.indexOf(Constant.cmdOpen) >= 0) {
            ToastUtil.showLong("开门操作成功");
        } else if (receiver.indexOf(Constant.cmdStop) >= 0) {
            ToastUtil.showLong("暂停操作成功");
        } else {
//            ToastUtil.showLong("无法与后台连接");
        }
    }


    private void initUDP() {
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        udp = new UDPThread(handler, wifiManager);
        udp.setServerPara(Constant.serverIP, Constant.serverPort);
        String connect = udp.connect();
        txtResult.setText(connect);
    }

    private void initView() {
        findViewById(R.id.back_udp).setOnClickListener(this);
        findViewById(R.id.udpclose).setOnClickListener(this);
        findViewById(R.id.udpopen).setOnClickListener(this);
        findViewById(R.id.udpstop).setOnClickListener(this);
        txtResult = (TextView) findViewById(R.id.udp_txtResult);
        findViewById(R.id.udpask).setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String disConnect = udp.disConnect();
        unregisterReceiver(controlReceiver);
        ToastUtil.showLong(disConnect);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.udpclose:
                if (IP == null) {
                    ToastUtil.showShort("设备不在线，正在查询");
                    DynamicIP();
                } else {
                    closePacket();
                }

                break;
            case R.id.udpopen:
                if (IP == null) {
                    ToastUtil.showShort("设备不在线，正在查询");
                    DynamicIP();
                } else {
                    openPacket();
                }


                break;
            case R.id.udpstop:
                if (IP == null) {
                    ToastUtil.showShort("设备不在线，正在查询");
                    DynamicIP();
                } else {
                    stopPacket();
                }
                break;
            case R.id.back_udp:
                finish();
                break;
            case R.id.udpask:
//                initHeart();
//                DynamicIP();
                break;
        }

    }

    /**
     * 查询查询动态IP地址
     */
    private void DynamicIP() {
        udp.setServerPara(Constant.ipIP, Constant.ipPort);
        byte[] heartpacket = TestProtocol.getByCmd(test, IdDevice,TestProtocol.cmdIP);
        txtResult.setText(txtResult.getText().toString() + "动态的数据包：\n"
                + Arrays.toString(heartpacket) + "\n");
        Logs.d(Arrays.toString(heartpacket));
        udp.send(heartpacket);
    }


    /**
     * 通过字节数组让设备停止
     */
    private void stopPacket() {
        udp.setServerPara(IP, Port);
        byte[] heartpacket = TestProtocol.getReqpacket("gate=2", IdDevice);
        txtResult.setText(txtResult.getText().toString() + "动态的数据包：\n"
                + Arrays.toString(heartpacket) + "\n");
        Logs.d(Arrays.toString(heartpacket));
        udp.send(heartpacket);
    }

    /**
     * 通过字节数组让设备打开
     */
    private void openPacket() {
        udp.setServerPara(IP, Port);
        byte[] heartpacket = TestProtocol.getReqpacket("gate=1", IdDevice);
        txtResult.setText(txtResult.getText().toString() + "动态的数据包：\n"
                + Arrays.toString(heartpacket) + "\n");
        Logs.d(Arrays.toString(heartpacket));
        udp.send(heartpacket);
    }

    /**
     * 通过字节数组让设备关闭
     */
    private void closePacket() {
        udp.setServerPara(IP, Port);
        byte[] heartpacket = TestProtocol.getReqpacket("gate=0", IdDevice);
        txtResult.setText(txtResult.getText().toString() + "动态的数据包：\n"
                + Arrays.toString(heartpacket) + "\n");
        Logs.d(Arrays.toString(heartpacket));
        udp.send(heartpacket);
    }

    /**
     * 通过字符串让设备停止
     */
    private void stopStr() {
        // initHeart();
        // sendRunnable(Constant.cmdStop, "停止");
        seq = new Random().nextInt(1000);
        cmd = 2;
        cmdStr = CmdStr();
        txtResult.setText(txtResult.getText().toString() + "停止的字符串：\n"
                + cmdStr + "\n");
        byte[] stop = cmdStr.getBytes();
        // TestProtocol.getReqpacket(Constant.cmdStop);
        txtResult.setText(txtResult.getText().toString() + "停止的数据包：\n"
                + Arrays.toString(stop) + "\n");
        udp.send(stop);
    }

    /**
     * 通过字符串让设备关闭
     */
    private void closeStr() {
        //  byte[] close = UDPProtocol.getDeviceReqPacket(Constant.cmdClose);
        // initHeart();
        // sendRunnable(Constant.cmdClose, "关闭");

        seq = new Random().nextInt(1000);
        cmd = 0;
        IdDevice =
                cmdStr = CmdStr();
        txtResult.setText(txtResult.getText().toString() + "关闭的字符串：\n"
                + cmdStr + "\n");
        byte[] close = cmdStr.getBytes();
        txtResult.setText(txtResult.getText().toString() + "关闭的数据包：\n"
                + Arrays.toString(close) + "\n");
        udp.send(close);
    }

    /**
     * 通过字符串让设备打开
     */
    private void openStr() {
        // initHeart();
        // sendRunnable(Constant.cmdOpen, "打开");
        seq = new Random().nextInt(1000);
        cmd = 1;
        cmdStr = CmdStr();
        txtResult.setText(txtResult.getText().toString() + "打开的字符串：\n"
                + cmdStr + "\n");
        byte[] open = cmdStr.getBytes();
        txtResult.setText(txtResult.getText().toString() + "打开的数据包：\n"
                + Arrays.toString(open) + "\n");
        udp.send(open);
    }


    /**
     * 发送的指令字符串  默认ID：BE0067BE2B
     */
    private String CmdStr() {
        String str = "cmd:103,type:1,termID:" + IdDevice + ",seq:" + seq
                + ",flag:2,param:gate=" + cmd;
        return str;
    }

    /**
     * 延迟发送
     */
    private void sendRunnable(final String cmd, final String string) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = TestProtocol.getReqpacket(cmd);
                txtResult.setText(txtResult.getText().toString() + string
                        + "的数据包：\n" + Arrays.toString(bytes) + "\n");
                udp.send(bytes);
            }
        }, delaySend);
    }

    // 监听wifi状态变化
    private BroadcastReceiver controlReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SystemClock.sleep(999);// 让系统睡眠一段时间
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            // .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            int wifiState = wifiManager.getWifiState();
            long second = System.currentTimeMillis();
            Logs.d("state:      " + wifiState);
            if (wifiState == 2 || wifiState == 3) {
                if (networkInfo != null
                        && networkInfo.isConnectedOrConnecting()) {
                    if (second - first > Constant.BroadcastReceiverTime) {
//         DialogNotileUtil.show(UdpControlActivity.this, strSSID+ "连接成功");
                        ToastUtil.showLong(strSSID + "连接成功");
                        first = second;
                    }
                } else {
                    if (second - first > Constant.BroadcastReceiverTime) {
                        DialogNotileUtil.show(UdpControlActivity.this, "设备断开");
                        first = second;
                    }
                }
            } else {
                if (second - first > Constant.BroadcastReceiverTime) {
                    DialogNotileUtil.show(UdpControlActivity.this, "WIFI关闭");
                    first = second;
                }
            }
        }

    };


}
