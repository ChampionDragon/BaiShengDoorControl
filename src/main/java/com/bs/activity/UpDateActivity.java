package com.bs.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.constant.Constant;
import com.bs.constant.UpdateService;
import com.bs.util.DialogLoading;
import com.bs.util.DialogNotileUtil;
import com.bs.util.Logs;
import com.bs.util.NetConnectUtil;
import com.bs.util.SystemUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

public class UpDateActivity extends BaseActivity implements View.OnClickListener {
    private Intent intentService;
    private DialogLoading dialoading;
    String tag = "UpDateActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_date);
        intentService = new Intent(this, UpdateService.class);
        initView();
    }

    private void initView() {
        findViewById(R.id.back_update).setOnClickListener(this);
        findViewById(R.id.update_check).setOnClickListener(this);
        findViewById(R.id.update_download).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_update:
                finish();
                break;
            case R.id.update_check:
                downBydialog();
                break;
            case R.id.update_download:
                startService(intentService);
                break;
        }
    }

    private void downBydialog() {
        dialoading = new DialogLoading(this, "准备下载");
        dialoading.show();
        executor.submit(apkRunnable);
    }

    private int myprogress;
    Runnable apkRunnable = new Runnable() {
        @Override
        public void run() {
            OkHttpUtils.get().url(Constant.apkUpdate).build().execute(new FileCallBack(
                    Constant.fileDir.getAbsolutePath(), SystemUtil.AppName() + ".apk") {
                @Override
                public void onError(Call call, Exception e, int i) {
                    if (NetConnectUtil.NetConnect(UpDateActivity.this)) {
                        DialogNotileUtil.show(UpDateActivity.this, "服务器异常,文件下载失败");
                    } else {
                        DialogNotileUtil.show(UpDateActivity.this, "未连接到网络,文件下载失败");
                    }
                    Logs.e(tag + " 74 " + e + "  " + i);
                }

                @Override
                public void onResponse(File file, int i) {
                    dialoading.setTv("下载完成");
                    dialoading.close();
                    Logs.v(tag + " 82 " + file.getAbsolutePath() + "  " + i);
//                    Logs.d(tag+"86 "+Constant.fileDir.getAbsolutePath()+SystemUtil.AppName() + ".apk");
                    startActivity(getInstall());
                }

                @Override
                public void inProgress(float progress, long total, int id) {
                    super.inProgress(progress, total, id);
                    myprogress = (int) (progress * 100);
//                    Logs.v(progress + "   " + myprogress);
                    if (myprogress % 5 == 0) {
                        dialoading.setTv("下载进度：" + myprogress + "%");
                    }
                }
            });
        }
    };

    /*返回安装的意图*/
    private Intent getInstall() {
        File file = new File(Constant.fileDir.getAbsolutePath(), SystemUtil.AppName() + ".apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //系统自带安装程序
        //Uri uri = Uri.fromFile(file);和Uri.parse("file://" + file.getAbsolutePath())相同
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        return intent;
    }


}
