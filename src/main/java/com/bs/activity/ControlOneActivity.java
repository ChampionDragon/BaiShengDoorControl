package com.bs.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bs.Ezviz.act.EZCameraListActivity;
import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.base.BaseApplication;
import com.bs.util.Logs;
import com.bs.util.SmallUtil;
import com.bs.util.ToastUtil;
import com.bs.util.UdpUtil;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 直接通过udpUtil发送数据
 * 作者 Champion Dragon
 * created at 2017/7/13
 **/
public class ControlOneActivity extends BaseActivity implements View.OnClickListener {
    private String DeviceID;
    private Animation mAnimation,antiAnimation;
    private SoundPool mSoundPool;//声音控件
    private TextView malfunction, unusual, state;
    private Button open, pause, close;
    private ImageView log, safe;
    private ImageView iv;
    // 指令1：OPEN ,CLOSE:0，STOP:2 3:OPENNING 4:CLOSING
    //cmd代表       MSG_DEV_SET_POWER = 101,MSG_GET_GATESTATE	102  MSG_SET_GATESTATE=103
    private final int OPEN = 1;
    private final int CLOSE = 0;
    private final int stop = 2;
    private int stateNum=3;
    //声音的5个选项
    private int CLOSED = 1;
    private int CLOSEING = 2;
    private int OPENED = 3;
    private int OPENING = 4;
    private int STOP = 5;
    private final int ERROR = 6;
    private final int NORESULT = 7;
    private int seq;// 随机数
    private String cmdStr;//指令
    private byte[] cmdPacket;
    private ScheduledExecutorService scheduledExecutorService;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case OPEN:
                    scheduledExecutorService.shutdown();
                    scheduledExecutorService = Executors
                            .newSingleThreadScheduledExecutor();
                    mSoundPool.play(OPENED, 1, 1, 0, 0, 1);
                    state.setText("已开门");
                    stateNum=OPEN;
                    ToastUtil.showLong("开门操作成功");
                    iv.clearAnimation();
                    break;
                case CLOSE:
                    scheduledExecutorService.shutdown();
                    scheduledExecutorService = Executors
                            .newSingleThreadScheduledExecutor();
                    mSoundPool.play(CLOSED, 1, 1, 0, 0, 1);
                    ToastUtil.showLong("关门操作成功");
                    stateNum=CLOSE;
                    state.setText("已关门");
                    iv.clearAnimation();
                    break;
                case stop:
                    scheduledExecutorService.shutdown();
                    scheduledExecutorService = Executors
                            .newSingleThreadScheduledExecutor();
                    mSoundPool.play(STOP, 1, 1, 0, 0, 1);
                    state.setText("已暂停");
                    stateNum=stop;
                    ToastUtil.showLong("暂停操作成功");
                    iv.clearAnimation();
                    break;
                case ERROR:
                    mSoundPool.play(ERROR, 1, 1, 0, 0, 1);
                    state.setText("操作失败");
                    ToastUtil.showLong("操作失败,请重试");
                    iv.clearAnimation();
                    break;
                case NORESULT:
                    state.setText("服务器异常");
                    ToastUtil.showLong("服务器异常,请重试");
                    iv.clearAnimation();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            DeviceID = extras.getString("id");
        }

        mAnimation = AnimationUtils.loadAnimation(this, R.anim.circular_ring);
        /*解决无限旋转卡顿的问题*/
        LinearInterpolator lin = new LinearInterpolator();
        mAnimation.setInterpolator(lin);

        /*逆时针旋转*/
        antiAnimation= AnimationUtils.loadAnimation(this, R.anim.circular_ring_anti);
        antiAnimation.setInterpolator(lin);

        scheduledExecutorService = Executors
                .newSingleThreadScheduledExecutor();

        initView();
        initSoundPool();
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

    private void initSoundPool() {
        mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        //第三个参数为声音的优先级，当多个声音冲突而无法同时播放时，系统会优先播放优先级高的。
        mSoundPool.load(this, R.raw.closed, 1);
        mSoundPool.load(this, R.raw.closing, 1);
        mSoundPool.load(this, R.raw.opened, 1);
        mSoundPool.load(this, R.raw.opening, 1);
        mSoundPool.load(this, R.raw.stop, 1);
        mSoundPool.load(this, R.raw.doerror, 1);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_controls:
                finish();
                break;
            case R.id.control_monitor:
//                SmallUtil.getActivity(this, MonitorActivity.class);


                String token = BaseApplication.sp.getString("萤石摄像头的秘钥");
                Logs.d("controlOneActivity168  " + token);
                BaseApplication.getOpenSDK().setAccessToken(token);
                Intent toIntent = new Intent(this, EZCameraListActivity.class);
                toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toIntent);




                /*跳转到指定app*/
//                Intent intent = getPackageManager().getLaunchIntentForPackage("com.lcb");
//                if (intent == null) {
//                    DialogNotileUtil.show(ControlOneActivity.this, "未安装此程序");
//
//                } else {
//                    startActivity(intent);
//                }

//                ComponentName componentName=new ComponentName("com.lcb","com.lcb.Ezviz.act.EZCameraListActivity");
//                Intent intent=new Intent();
//                intent.setComponent(componentName);
//                startActivity(intent);//跳转到指定app的指定类


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

    private void pause() {
        if (stateNum!=stop) {
            iv.startAnimation(mAnimation);
            state.setText("正在暂停");
            seq = new Random().nextInt(1000);
            cmdStr = CmdStr(seq, stop);
            cmdPacket = cmdStr.getBytes();
            executor.submit(runnable);
        }else {
            ToastUtil.showLong("设备已暂停，不能再做此操作");
        }
    }

    private void open() {
        if (stateNum!=OPEN) {
            iv.startAnimation(mAnimation);
            state.setText("正在开门");
            mSoundPool.play(OPENING, 1, 1, 0, 0, 1);
            seq = new Random().nextInt(1000);
            cmdStr = CmdStr(seq, OPEN);
            cmdPacket = cmdStr.getBytes();
            executor.submit(runnable);
        }else {
            ToastUtil.showLong("设备已打开，不能再做此操作");
        }
    }

    private void close() {
        if (stateNum!=CLOSE) {
            iv.startAnimation(antiAnimation);
            state.setText("正在关门");
            mSoundPool.play(CLOSEING, 1, 1, 0, 0, 1);
            seq = new Random().nextInt(1000);
            cmdStr = CmdStr(seq, CLOSE);
            cmdPacket = cmdStr.getBytes();
            executor.submit(runnable);
        }else {
            ToastUtil.showLong("设备已关闭，不能再做此操作");
        }
    }

    /**
     * 返回发送的字符串
     */
    private String CmdStr(int seq, int cmd) {
        String str = "cmd:103,type:1,termID:" + DeviceID + ",seq:" + seq
                + ",flag:2,param:gate=" + cmd;
        return str;
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Logs.d("ctrOne223    " + cmdStr + "\n");
            String serverSend = UdpUtil.ServerSend(cmdPacket);
            Logs.e("ctrOne225   " + serverSend);
            if (serverSend.indexOf("flag:5") >= 0) {
                scheduledExecutorService.scheduleWithFixedDelay(getState, 2, 2,
                        TimeUnit.SECONDS);
            } else if (serverSend.indexOf("NO RESPONSE") >= 0) {
             mHandler.sendEmptyMessage(NORESULT);
            } else if (serverSend.indexOf("result=fail") >= 0) {
                mHandler.sendEmptyMessage(ERROR);
            }
        }
    };

    Runnable getState = new Runnable() {
        @Override
        public void run() {
            String stateStr = StateStr(0);
            byte[] stateByte = stateStr.getBytes();
            Logs.v("ctrOne240 " + stateStr + "\n");
            String serverSend = UdpUtil.ServerSend(stateByte);
            Logs.w("ctrOne242   " + serverSend);
            judgement(serverSend);
        }
    };

    /**
     * 判断返回值
     */
    private void judgement(String receiver) {
        if (receiver.indexOf("param:0") >= 0) {

            mHandler.sendEmptyMessage(CLOSE);

        } else if (receiver.indexOf("param:1") >= 0) {

            mHandler.sendEmptyMessage(OPEN);

        } else if (receiver.indexOf("param:2") >= 0) {

            mHandler.sendEmptyMessage(stop);

        }
    }

    private String StateStr(int cmd) {
        seq = new Random().nextInt(1000);
        String str = "cmd:102,type:1,termID:" + DeviceID + ",seq:" + seq
                + ",flag:2,param:gate=" + cmd;
        return str;
    }

}
