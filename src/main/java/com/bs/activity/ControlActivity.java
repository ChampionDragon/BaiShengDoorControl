package com.bs.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bs.Ezviz.act.EZCameraListActivity;
import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.base.BaseApplication;
import com.bs.constant.Constant;
import com.bs.socket.UDPThread;
import com.bs.util.DialogNotileUtil;
import com.bs.util.Logs;
import com.bs.util.SmallUtil;
import com.bs.util.ToastUtil;

import java.util.Arrays;
import java.util.Random;

/**
 * 通过UDPThread发送数据
 * 作者 Champion Dragon
 * created at 2017/7/13
 **/
public class ControlActivity extends BaseActivity implements OnClickListener {
    private WifiManager wifiManager;
    private UDPThread udp;
    private long first;
    private String strSSID;
    private WifiInfo wifiInfo;
    private int seq;// 随机数
    private int cmd;// 指令1：OPEN ,CLOSE:0，STOP:2
    private String cmdStr;
    private String DeviceID;
    private TextView malfunction, unusual, state;
    private Button open, pause, close;
    private ImageView log, safe;
    private SoundPool mSoundPool;//声音控件
    private ImageView iv;
    private int CLOSED = 1;
    private int CLOSEING = 2;
    private int OPENED = 3;
    private int OPENING = 4;
    private int STOP = 5;
    private Animation animOpen, animClose;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case UDPThread.MSG_UDP_RECEIVE:
                    StringBuffer sb = new StringBuffer();
                    Bundle data = msg.getData();
                    String ip = (String) data.get(UDPThread.KEYUDPRECIP);
                    Integer port = (Integer) data.get(UDPThread.KEYUDPRECPORT);
                    String receiver = data.getString(UDPThread.KEYUDPRECEIVE);
                    sb.append(ip).append("\n").append(port).append("\n")
                            .append("返回的数据结果:").append("\n")
                            .append(receiver).append("\n");
                    Logs.d("control 78    " + sb);
                    judgement(receiver);
                    break;
            }
        }
    };

    /**
     * 判断指令发送是否成功
     */
    private void judgement(String receiver) {
        if (receiver.indexOf(Constant.cmdClose) >= 0) {
            iv.clearAnimation();
            state.setText("已关门");
            ToastUtil.showLong("关门操作成功");
            mSoundPool.play(CLOSED, 1, 1, 0, 0, 1);
        } else if (receiver.indexOf(Constant.cmdOpen) >= 0) {
            iv.clearAnimation();
            state.setText("已开门");
            ToastUtil.showLong("开门门操作成功");
            mSoundPool.play(OPENED, 1, 1, 0, 0, 1);
        } else if (receiver.indexOf(Constant.cmdStop) >= 0) {
            iv.clearAnimation();
            state.setText("已暂停");
            ToastUtil.showLong("暂停操作成功");
            mSoundPool.play(STOP, 1, 1, 0, 0, 1);
        } else if (receiver.indexOf("termID:null") >= 0) {
            iv.clearAnimation();
            state.setText("连接失败");
            ToastUtil.showLong("缺少ID值");
            mSoundPool.play(STOP, 1, 1, 0, 0, 1);
        } else {
            if (iv.getAnimation() != null) {
                iv.clearAnimation();
            }
            long second = System.currentTimeMillis();
            if (second - first > Constant.bgTimeout) {
                ToastUtil.showLong("连接超时");
                first = second;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            DeviceID = extras.getString("id");
        } else {
            DeviceID = Constant.idFOUR;
        }
        initView();
        initAnim();
        initSoundPool();
        initUDP();
        initWifi();
    }

    private void initUDP() {
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        udp = new UDPThread(handler, wifiManager);
        udp.setServerPara(Constant.serverIP, Constant.serverPort);
        String connect = udp.connect();
        Logs.d("control85      " + connect);
    }

    private void initWifi() {
        wifiInfo = wifiManager.getConnectionInfo();
        strSSID = wifiInfo.getSSID();
        IntentFilter filter = new IntentFilter(
                WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(controlReceiver, filter);
    }

    private void initView() {
        malfunction = (TextView) findViewById(R.id.control_malfunction);
        unusual = (TextView) findViewById(R.id.control_unusual);
        state = (TextView) findViewById(R.id.control_state);
        open = (Button) findViewById(R.id.control_open);
        pause = (Button) findViewById(R.id.control_pause);
        close = (Button) findViewById(R.id.control_close);
        open.setOnClickListener(this);
        pause.setOnClickListener(this);
        close.setOnClickListener(this);
        safe = (ImageView) findViewById(R.id.control_safe);
        safe.setOnClickListener(this);
        log = (ImageView) findViewById(R.id.control_log);
        log.setOnClickListener(this);
        findViewById(R.id.back_controls).setOnClickListener(this);
        findViewById(R.id.control_monitor).setOnClickListener(this);
        iv = (ImageView) findViewById(R.id.control_iv);
    }

    /**
     * 发送的指令字符串  默认ID：BE0067BE2B
     */
    private String CmdStr() {
        String str = "cmd:103,type:1,termID:" + DeviceID + ",seq:" + seq
                + ",flag:2,param:gate=" + cmd;
        return str;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_controls:
                finish();
                break;
            case R.id.control_monitor:
                String token = BaseApplication.sp.getString("萤石摄像头的秘钥");
                Logs.d("controlActivity194  " + token);
                BaseApplication.getOpenSDK().setAccessToken(token);
                Intent toIntent = new Intent(this, EZCameraListActivity.class);
                toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toIntent);
                break;
            case R.id.control_close:
                close();
                break;
            case R.id.control_open:
                open();
                break;
            case R.id.control_pause:
                pause();
                break;
            case R.id.control_log:
                SmallUtil.getActivity(this, RefreshRecordActivity.class);
                break;
            case R.id.control_safe:
                SmallUtil.getActivity(this, SafeZoonActivity.class);
                break;
        }
    }

    /**
     * 暂停相关操作
     */
    private void pause() {
        iv.startAnimation(animOpen);
        state.setText("正在暂停");
        seq = new Random().nextInt(1000);
        cmd = 2;
        cmdStr = CmdStr();
        byte[] stop = cmdStr.getBytes();
        Logs.d("ctr216   " + cmdStr + "\n" + Arrays.toString(stop));
        udp.send(stop);
    }

    /**
     * 打开相关操作
     */
    private void open() {
        iv.startAnimation(animOpen);
        state.setText("正在开门");
        mSoundPool.play(OPENING, 1, 1, 0, 0, 1);
        seq = new Random().nextInt(1000);
        cmd = 1;
        cmdStr = CmdStr();
        final byte[] open = cmdStr.getBytes();
        Logs.d("ctr215    " + cmdStr + "\n" + Arrays.toString(open));
        udp.send(open);


//        Runnable Runnable = new Runnable() {
//            @Override
//            public void run() {
//                String s = UdpUtil.ServerSend(open);
//                Logs.d(s + "              udp231");
//            }
//        };
//        executor.submit(Runnable);


    }

    /**
     * 关闭相关操作
     */
    private void close() {
        iv.startAnimation(animClose);
        state.setText("正在关门");
        mSoundPool.play(CLOSEING, 1, 1, 0, 0, 1);
        seq = new Random().nextInt(1000);
        cmd = 0;
        cmdStr = CmdStr();
        byte[] close = cmdStr.getBytes();
        Logs.d("ctr230    " + cmdStr + "\n" + Arrays.toString(close));
        udp.send(close);
    }

    /**
     * 初始化声音
     */
    private void initSoundPool() {
//        第一个参数为soundPool可以支持的声音数量，这决定了Android为其开设多大的缓冲区，第二个参数为声音类型，
//        在这里标识为系统声音，除此之外还有AudioManager.STREAM_RING以及AudioManager.STREAM_MUSIC等
//        系统会根据不同的声音为其标志不同的优先级和缓冲区，最后参数为声音品质，品质越高，声音效果越好，但耗费更多的系统资源
        mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        //第三个参数为声音的优先级，当多个声音冲突而无法同时播放时，系统会优先播放优先级高的。
        mSoundPool.load(this, R.raw.closed, 1);
        mSoundPool.load(this, R.raw.closing, 1);
        mSoundPool.load(this, R.raw.opened, 1);
        mSoundPool.load(this, R.raw.opening, 1);
        mSoundPool.load(this, R.raw.stop, 1);
//        第一个参数为id，id即为放入到soundPool中的顺序，比如现在collide.wav是第一个，因此它的id就是1。
//        第二个和第三个参数为左右声道的音量控制。第四个参数为优先级，由于只有这一个声音，因此优先级在这里并不重要。
//        第五个参数为是否循环播放，n为n+1次即0为1次，-1为循环。最后一个参数为播放比率，从0.5到2，一般为1，表示正常播放。
//        mSoundPool.play(CLOSEING, 1, 1, 0, 0, 1);
    }

    private void initAnim() {
        animOpen = AnimationUtils.loadAnimation(this, R.anim.circular_ring);
        animClose = AnimationUtils.loadAnimation(this, R.anim.circular_ring);
//        mAnimation1 = AnimationUtils.loadAnimation(this, R.anim.translate_demo);
//        animOpen.setAnimationListener(mAnimationListener);
//        animClose.setAnimationListener(mAnimationListener);
    }


    private Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            if (animation == animClose) {
                state.setText("正在关门");
                mSoundPool.play(CLOSEING, 1, 1, 0, 0, 1);

            } else {
                state.setText("正在开门");
                mSoundPool.play(OPENING, 1, 1, 0, 0, 1);
            }

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (animation == animClose) {
                state.setText("已关门");
                mSoundPool.play(CLOSED, 1, 1, 0, 0, 1);
            } else {
                state.setText("已开门");
                mSoundPool.play(OPENED, 1, 1, 0, 0, 1);
            }

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


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
                        DialogNotileUtil.show(ControlActivity.this, "设备断开");
                        first = second;
                    }
                }
            } else {
                if (second - first > Constant.BroadcastReceiverTime) {
                    DialogNotileUtil.show(ControlActivity.this, "WIFI关闭");
                    first = second;
                }
            }
        }

    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        String disConnect = udp.disConnect();
        unregisterReceiver(controlReceiver);
        ToastUtil.showLong(disConnect);
    }


}
