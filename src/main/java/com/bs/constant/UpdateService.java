package com.bs.constant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.bs.R;
import com.bs.base.AsyncTaskExecutor;
import com.bs.util.DialogNotileUtil;
import com.bs.util.Logs;
import com.bs.util.NetConnectUtil;
import com.bs.util.SystemUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

public class UpdateService extends Service {
    private Notification.Builder builder, msgBuilder;
    private NotificationManager manager;
    private Notification notification;
    private String mDownloadUrl;//APK的下载路径
    String tag = "UpdateService";
    int myprogress;
    int defferent;
    public final int progressNotification = 0;
    public final int msgNotification = 1;


    @Override
    public IBinder onBind(Intent intent) {
        //将通信通道返回给服务
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logs.d(tag + " 51    " + "onCreate()");
        initNotificationBuilder();
        initmsgBuilder();
    }

    /**
     * 初始化NotificationBuilder
     */
    private void initNotificationBuilder() {
        builder = new Notification.Builder(this);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setTicker("软件正在更新")
                //设置启动时间
                .setWhen(System.currentTimeMillis())
                .setVibrate(new long[]{0, 900})
                .setSmallIcon(R.drawable.ad3)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ad2))
                //自定义通知的提示音
//              .setSound(Uri.parse("android.resource://com.lcb/" + R.raw.timeout));
                //设置系统默认声音
                .setDefaults(Notification.DEFAULT_SOUND);
    }


    private void initmsgBuilder() {
        msgBuilder = new Notification.Builder(this);
        msgBuilder.setSmallIcon(R.drawable.ad3)
                .setContentTitle("文件正在下载")
                .setContentText("下载进度")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ad2));
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mDownloadUrl = Constant.apkUpdate;

        //下载APKs
        AsyncTaskExecutor.getinstance().submit(downloadRunnable);

        Logs.d(tag + " 80    " + "onStartCommand()     " + mDownloadUrl);

        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 下载文件
     */

    Runnable downloadRunnable = new Runnable() {
        @Override
        public void run() {
            OkHttpUtils.get().url(mDownloadUrl).build().execute(new FileCallBack(Constant.fileDir.getAbsolutePath(),
                    SystemUtil.AppName() + "_updata.apk") {
                @Override
                public void onError(Call call, Exception e, int i) {
                    if (NetConnectUtil.NetConnect(UpdateService.this)) {
//                    ToastUtil.showLong("服务器异常,文件下载失败");
                        DialogNotileUtil.show(UpdateService.this, "服务器异常,文件下载失败");
                    } else {
//                    ToastUtil.showLong("未连接到网络,文件下载失败");
                        DialogNotileUtil.show(UpdateService.this, "未连接到网络,文件下载失败");
                    }
                    stopSelf();
                    Logs.e(tag + " 111 " + e + "  " + i);
                }

                @Override
                public void onResponse(File file, int i) {
                    notifyMsg("温馨提醒", "文件下载已完成", 100);
                    stopSelf();
                    Logs.e(tag + " 118 " + file.getAbsolutePath() + "  " + i);
                }

                @Override
                public void inProgress(float progress, long total, int id) {
                    //progress*100为当前文件下载进度，total为文件大小
                    super.inProgress(progress, total, id);
                    myprogress = (int) (progress * 100);
//                Logs.v(tag + "120 " + progress + "  " + "  " + defferent + "  " + myprogress);


                    if (myprogress == defferent) {
                        defferent = defferent + 5;
//                    Logs.e("" + myprogress);
                        if (myprogress > 0 && myprogress < 100) {
                            notifyMsg(myprogress);
//                            mHandler.sendEmptyMessage(progressNotification);
                            Logs.d(tag + "  163 " + myprogress);
                        } else if (myprogress == 0) {
                            // 避免频繁刷新View，这里设置每下载10%提醒更新一次进度
                            notifyMsg("温馨提醒", "文件准备下载", myprogress);
//                            mHandler.sendEmptyMessage(msgNotification);
                            Logs.e(tag + "  168 " + myprogress);
                        }

                    }
                }
            });
        }
    };


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msgNotification:
                    notifyMsg("温馨提醒", "文件正在下载............", myprogress);
                    break;
                case progressNotification:
                    notifyMsg(myprogress);
                    break;

            }
        }
    };


    /**
     * 设置开始和结束的进度消息
     */
    private void notifyMsg(String title, String msg, int progress) {
        builder.setContentTitle(title);
        if (progress > 0 && progress < 100) {
//            下载进行中
            builder.setProgress(100, progress, false);
        } else {
            builder.setProgress(0, 0, false);
        }
//        builder.setContentInfo("下载进度  " + progress + "%");
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentText(msg);//左边的文字
        if (progress >= 100) {
//            下载完成
            builder.setContentIntent(getInstallIntent());
        }
        notification = builder.build();
        manager.notify(0, notification);
    }


    /**
     * 下载中的消息
     */
    private void notifyMsg(int progress) {
        if (progress > 0 && progress < 100) {
//            下载进行中
            msgBuilder.setProgress(100, progress, false);
        }
        msgBuilder.setContentInfo(progress + "%");//右边的文字
        notification = msgBuilder.build();
        manager.notify(0, notification);
    }


    /**
     * 设置安装的pendingIntent
     */
    private PendingIntent getInstallIntent() {
        File file = new File(Constant.fileDir.getAbsolutePath(), SystemUtil.AppName() + "_updata.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //系统自带安装程序
        //Uri uri = Uri.fromFile(file);和Uri.parse("file://" + file.getAbsolutePath())相同
        intent.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()), "application/vnd.android.package-archive");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }


}
