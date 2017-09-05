package com.bs.activity;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.bean.DeviceBean;
import com.bs.constant.Constant;
import com.bs.constant.SpKey;
import com.bs.db.DbManager;
import com.bs.socket.TestProtocol;
import com.bs.util.DialogLoading;
import com.bs.util.DialogNotileUtil;
import com.bs.util.Logs;
import com.bs.util.SmallUtil;
import com.bs.util.SpUtil;
import com.bs.util.ToastUtil;
import com.bs.util.UdpUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class WifiSetActivity extends BaseActivity implements View.OnClickListener {
    private Button btn;
    private TextView name;
    private EditText pwd;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private String wifiname, CheckID, deviceID, wifipsw;
    private WifiManager.MulticastLock multicastLock;
    Boolean xmitStarted = false;
    xmitterTask xmitter;
    private DialogLoading dialog;
    private int ONLINE = 1;
    private int OUTLINE = 2;
    private SpUtil sp;
    private boolean IsOutline;
    private boolean IsOnline;
    private boolean IsToast;
    String tag="WifiSetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_set);
        initView();
        initWIFI();

/*如果是通过手动输入ID就可以通过查询此ID是否在线来判断WIFI是否配置成功*/
        CheckID = getIntent().getStringExtra(Constant.deviceId);
        Logs.d(CheckID + "   wifiset 62");
        deviceID = "";
    }

    private void initWIFI() {
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        wifiname = mWifiInfo.getSSID();
        wifiname = wifiname.replace("\"", "");//去掉双引号
        name.setText(wifiname);
        /*读取上次输入的密码*/
        sp = SpUtil.getInstance(SpKey.SP_wifiname,
                Context.MODE_PRIVATE);
        wifipsw = sp.getString(wifiname);
        pwd.setText(wifipsw);


    }

    private void initView() {
        name = (TextView) findViewById(R.id.wifiset_name);
        pwd = (EditText) findViewById(R.id.wifiset_pwd);
        findViewById(R.id.back_wifiset).setOnClickListener(this);
        btn = (Button) findViewById(R.id.wifiset_btn);
        btn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_wifiset:
                finish();
                break;
            case R.id.wifiset_btn:
                wifiConnect();
                break;
        }

    }


    // android默认关闭组播功能，打开组播的功能
    private void allowMulticast() {
        multicastLock = mWifiManager.createMulticastLock("multicast.test");
        multicastLock.acquire();
    }

    private static boolean validate(String pass, String key) {

        if ((pass.length() != 0) && (pass.length() < 8 || pass.length() > 63)) {
            ToastUtil.showShort("无效的密码，密码要大于8位小于63位或为空");
            return false;
        }
        if (key.length() != 10 && key.length() != 0) {
            ToastUtil.showShort("设备号要么为空，要么为十位");
            return false;
        }
        return true;
    }


    /**
     * 一键配置WIFI
     */
    private void wifiConnect() {
        wifipsw = pwd.getText().toString();
        String pass = wifipsw;
        String key = deviceID;


        allowMulticast();

        try {
//            if (xmitStarted == false) {
            if (!validate(pass, key)) return;

            dialog = new DialogLoading(this, "正在配置WIFI");
            dialog.show();

            xmitter = new xmitterTask();
            xmitter.handler = handler;
//                xmitStarted = true;
//                btn.setText("停止");
            CRC32 crc32 = new CRC32();
            crc32.reset();
            crc32.update(pass.getBytes());
            xmitter.passCRC = (int)
                    crc32.getValue() & 0xffffffff;
            Logs.v(tag+" 147  " +
                    Integer.toHexString(xmitter.passCRC));

            if (key.length() != 0) {
                if (pass.length() % 16 == 0) {
                    xmitter.passLen = pass.length();
                } else {
                    xmitter.passLen = (16 -
                            (pass.length() % 16)) + pass.length();
                }
                byte[] plainPass = new byte[xmitter.passLen];

                for (int i = 0; i < pass.length(); i++)
                    plainPass[i] =
                            pass.getBytes()[i];

                xmitter.passphrase = myEncrypt(key.getBytes(), plainPass);
            } else {
                xmitter.passphrase = pass.getBytes();
                xmitter.passLen =
                        pass.length();
            }
            xmitter.mac = new char[6];
            String[] macParts =
                    mWifiInfo.getBSSID().split(":");

            Logs.e(tag+" 173 " + mWifiInfo.getBSSID());
            for (int i = 0; i < 6; i++)
                xmitter.mac[i] = (char) Integer.parseInt(macParts[i], 16);
            xmitter.resetStateMachine();
            xmitter.execute("");
//            }


//            else {
//                xmitStarted = false;
//                btn.setText("配置WIFI");
//                xmitter.cancel(true);
//            }


        } catch (Error err) {
            Logs.e(tag+err.toString());
        }


    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 42) {
//                if (CheckID == null) {
//                    Log.d("lcb", "142   ASync task exited");
//                    xmitStarted = false;
//                    btn.setText("配置WIFI");
//                    DialogNotileUtil.show(WifiSetActivity.this, "设备WIFI已经配置.\n 如果没有,请重试.");
//                } else {
//                }
                executor.submit(IdRunnable);
            } else if (msg.what == 43) {
//                ToastUtil.showShort("发送" + msg.arg1 + "次");
                dialog.close();
                dialog = new DialogLoading(WifiSetActivity.this, "正在查询是否配置成功");
                dialog.show();

            } else if (msg.what == 44) {
                postDelayed(delayRunnable, 4444);
            } else if (msg.what == ONLINE) {
                /*将设备添加到本地数据库*/
                if (!IsToast) {
                    sp.putString(wifiname, wifipsw);

                    String idStr = msg.getData().getString("id");
                    dialog.close();//关闭Dialog
                    DbManager dbManager = DbManager.getmInstance(WifiSetActivity.this, Constant.dbDiveceBsmk, Constant.dbVersion);

                    DeviceBean device = dbManager.getDevice(idStr);
                    boolean b = dbManager.addOrUpdateDevice(idStr, device.getName(), device.getAddress(), device.getCreateTime());

                    if (b) {
                        Logs.v(tag+" 241 添加或更新设备成功");
                    }
                    ToastUtil.showLong("设备WIFI配置成功.");
                    IsToast = true;
                    finish();
                }
            } else if (msg.what == OUTLINE && IsOutline && !IsOnline) {
                Logs.e(tag+" 248" + IsOnline + "  " + IsOutline);
                dialog.close();//关闭Dialog
                DialogNotileUtil.show(WifiSetActivity.this, "设备WIFI配置失败.");
                IsOutline = false;
            }
            super.handleMessage(msg);


        }
    };


    /**
     * 延迟三秒发送查询命令
     */
    Runnable delayRunnable = new Runnable() {
        @Override
        public void run() {
            IsOutline = true;
            executor.submit(IdRunnable);
        }
    };


    /*检测ID是否在线从而判断WIFI是否配置成功*/
    Runnable IdRunnable = new Runnable() {
        @Override
        public void run() {
          /*通过查询设备是否在线*/
//            String url = Constant.isOnline + HttpByGet.get("deviceid", CheckID + ".");
//            String s = HttpByGet.executeHttpGet(url);
//            Logs.v("wifiset235   "+s+"\n"+url);
//            if (s.equals(" 1")) {  //返回的数据里有空格,所以1前面我空了一个格
//                handler.sendEmptyMessage(ONLINE);
//            } else {
//                handler.sendEmptyMessage(OUTLINE);
//            }

            /*第二种方式通过查询设备ID是否有返回值*/
            byte[] packet = TestProtocol.getByCmd("1", "", TestProtocol.cmdID);
            String serverSend = UdpUtil.IpSend(packet);
            Logs.e(tag+" 289  " + serverSend);
            if (serverSend.indexOf("NO RESPONSE") >= 0) {
                handler.sendEmptyMessage(OUTLINE);
            } else {
                /*解析返回的参数*/
                String id = SmallUtil.getPartString(serverSend, "ID:", 10);
                IsOnline = true;
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                Message msg = handler.obtainMessage(ONLINE);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }
    };


    private class xmitterTask extends AsyncTask<String, Void, String> {
        byte[] passphrase;
        char[] mac;
        int passLen;
        int passCRC;
        Handler handler;

        private int state, substate;

        public void resetStateMachine() {
            state = 0;
            substate = 0;
        }

        private void xmitRaw(int u, int m, int l) {
            MulticastSocket ms;
            InetAddress sessAddr;
            DatagramPacket dp;

            byte[] data = new byte[2];
            data = "a".getBytes();

            u = u & 0x7f; /* multicast's uppermost byte has only 7 chr */

            try {
//                Log.d("lcb", "353         239." + u + "." + m + "." + l);
                sessAddr = InetAddress
                        .getByName("239." + u + "." + m + "." + l);
                ms = new MulticastSocket(1234);
                ms.joinGroup(sessAddr);
                ms.setTimeToLive(4);
                dp = new DatagramPacket(data, data.length, sessAddr, 5500);
                ms.send(dp);
                ms.close();
            } catch (UnknownHostException e) {
                Log.e("lcb", "326        Exiting 5");
            } catch (IOException e) {
                Log.d("lcb", 329 + "    " + e);
            }
        }

        private void xmitState0(int substate) {
            int i, j, k;

            k = mac[2 * substate];
            j = mac[2 * substate + 1];
            i = substate;

            xmitRaw(i, j, k);
        }

        private void xmitState1() {
            int u = (0x01 << 5) | (0x0);
            xmitRaw(u, passLen, passLen);
        }

        private void xmitState2(int substate, int len) {

//            Log.d("lcb", "386    xmitState2" + substate);

            int u = substate | (0x2 << 5);
            int l = (0xff & passphrase[2 * substate]);
            int m;
            if (len == 2)
                m = (0xff & passphrase[2 * substate + 1]);
            else
                m = 0;
            xmitRaw(u, m, l);
        }

        private void xmitState3(int substate) {
            int i, j, k;

            k = (int) (passCRC >> ((2 * substate + 0) * 8)) & 0xff;
            j = (int) (passCRC >> ((2 * substate + 1) * 8)) & 0xff;
            i = substate | (0x3 << 5);

            xmitRaw(i, j, k);
        }

        private void stateMachine() {
            switch (state) {
                case 0:
                    if (substate == 3) {
                        state = 1;
                        substate = 0;
                    } else {
                        xmitState0(substate);
                        substate++;
                    }
                    break;
                case 1:
                    xmitState1();
                    if (passLen != 0) {
                        state = 2;
                    } else {
                        state = 3;
                    }
                    substate = 0;
                    break;
                case 2:
                    xmitState2(substate, 2);
                    substate++;
                    if (passLen % 2 == 1) {
                        if (substate * 2 == passLen - 1) {
                            xmitState2(substate, 1);
                            state = 3;
                            substate = 0;
                        }
                    } else {
                        if (substate * 2 == passLen) {
                            state = 3;
                            substate = 0;
                        }
                    }
                    break;
                case 3:
                    xmitState3(substate);
                    substate++;
                    if (substate == 2) {
                        substate = 0;
                        state = 0;
                    }
                    break;
                default:
                    Log.e("lcb", "471   I shouldn't be here");
            }
        }

        protected String doInBackground(String... params) {
            WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiManager.MulticastLock mcastLock = wm.createMulticastLock("mcastlock");
            mcastLock.acquire();
            for (int i = 0; i < passLen; i++) {
//                Log.d("lcb", "480   " + (0xff & passphrase[i]));
            }

            int i = 0;

            while (true) {
                if (state == 0 && substate == 0)
                    i++;
                //每个十秒报告下时间  i % 10 == 0   && CheckID != null
                if (i == 70) {
                    Message msg = handler.obtainMessage();
                    msg.what = 43;
                    msg.arg1 = i;
                    handler.sendMessage(msg);
                }
               /* 从80秒开始每隔十秒发送查询是否配置成功*/
                if (i >= 80 && i % 10 == 0 && i < 131) {
                    Logs.e(tag+" 457+++++++++++++++++++++++++++++++++++++++++++++++");
                    Message msg = handler.obtainMessage();
                    msg.what = 42;
                    handler.sendMessage(msg);
                }

				/*在尝试连接110次过后让用户重新发送*/
                if (i >= 131)
                    break;

                if (isCancelled())
                    break;

                stateMachine();

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Log.d("lcb", 453 + "      " + e);
                    break;
                }
            }

            mcastLock.release();

            if (i >= 131) {
                Logs.d(tag+"484-------------------------------------------------");
                Message msg = handler.obtainMessage();
                msg.what = 44;
                handler.sendMessage(msg);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    public static byte[] myEncrypt(byte[] key, byte[] plainText) {

        byte[] iv = new byte[16];
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

        for (int i = 0; i < 16; i++)
            iv[i] = 0;

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher;
        byte[] encrypted = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
            encrypted = cipher.doFinal(plainText);
        } catch (NoSuchAlgorithmException e) {
            Log.d("lcb", 501 + "    " + e);
        } catch (NoSuchPaddingException e) {
            Log.d("lcb", 502 + "    " + e);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            Log.d("lcb", 505 + "    " + e);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            Log.d("lcb", 508 + "    " + e);
        } catch (BadPaddingException e) {
            e.printStackTrace();
            Log.d("lcb", 511 + "    " + e);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            Log.d("lcb", 514 + "    " + e);
        }
        return encrypted;
    }


    /**
     * 如果配置出现问题取消加载的dialog
     */
    private void SetError() {
        if (dialog != null) {
            dialog.close();
        }
        ToastUtil.showLong("配置出现问题");

    }


}
