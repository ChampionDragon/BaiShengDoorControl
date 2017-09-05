package com.bs.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.bs.R;

/**
 * SoundPool工具类
 * 作者 Champion Dragon
 * created at 2017/7/21
 **/

public class SoundPoolUtil {
    private static SoundPool soundpool;
    private static SoundPoolUtil instance;
    private static Context context;

    public static SoundPoolUtil getInstance(Context mcontext) {
        if (instance == null) {
            instance = new SoundPoolUtil();
        }
        context = mcontext;
        if (Build.VERSION.SDK_INT > 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频数量
            builder.setMaxStreams(20);
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build());
            soundpool = builder.build();
        } else {
//        第一个参数为soundPool可以支持的声音数量，这决定了Android为其开设多大的缓冲区，第二个参数为声音类型，
//        在这里标识为系统声音，除此之外还有AudioManager.STREAM_RING以及AudioManager.STREAM_MUSIC等
//        系统会根据不同的声音为其标志不同的优先级和缓冲区，最后参数为声音品质，品质越高，声音效果越好，但耗费更多的系统资源
            soundpool = new SoundPool(20, AudioManager.STREAM_SYSTEM, 5);
        }
        //第三个参数为声音的优先级，当多个声音冲突而无法同时播放时，系统会优先播放优先级高的。
        soundpool.load(context, R.raw.closed, 1);
        soundpool.load(context, R.raw.closing, 1);
        soundpool.load(context, R.raw.opened, 1);
        soundpool.load(context, R.raw.opening, 1);
        soundpool.load(context, R.raw.opening2, 1);
        soundpool.load(context, R.raw.stop, 1);
        soundpool.load(context, R.raw.doerror, 1);
        soundpool.load(context, R.raw.beep, 1);
        soundpool.load(context, R.raw.bindcamera, 1);
        soundpool.load(context, R.raw.id, 1);
        soundpool.load(context, R.raw.login, 1);
        soundpool.load(context, R.raw.logined, 1);
        soundpool.load(context, R.raw.loginfail, 1);
        soundpool.load(context, R.raw.offline, 1);
        soundpool.load(context, R.raw.paizhao, 1);
        soundpool.load(context, R.raw.timeout, 1);
        soundpool.load(context, R.raw.wifi, 1);


        return instance;
    }


    /*
     播放,你想要播放第几个就传入几
      */
    public void play(int pid) {
        soundpool.play(pid, 1, 1, 0, 0, 1);
    }
}
