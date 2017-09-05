package com.bs.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.constant.Constant;
import com.bs.http.HttpByGet;
import com.bs.util.DialogNotileUtil;
import com.bs.util.Logs;

import org.json.JSONException;
import org.json.JSONObject;

public class CloudSaveActivity extends BaseActivity implements OnClickListener {
    private static final int SAVE = 0;
    private static final int GET = 1;
    private static final int SAVE_ERROR = 2;
    private static final int GET_ERROR = 3;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SAVE:
                    DialogNotileUtil.show(CloudSaveActivity.this, "数据备份到云端成功");
                    break;
                case GET:
                    String msgStr = (String) msg.obj;
                    DialogNotileUtil.show(CloudSaveActivity.this, "接收到的数据为:\n" + msgStr);
                    break;
                case GET_ERROR:
                    DialogNotileUtil.show(CloudSaveActivity.this, "获取云端的备份失败");
                    break;
                case SAVE_ERROR:
                    DialogNotileUtil.show(CloudSaveActivity.this, "数据备份到云端失败");
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloudsave);
        initview();
        findViewById(R.id.back_cloudsave).setOnClickListener(this);
        findViewById(R.id.cloudsave_get).setOnClickListener(this);
        findViewById(R.id.cloudsave_save).setOnClickListener(this);
        findViewById(R.id.cloudsave_set).setOnClickListener(this);
    }


    private void initview() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_cloudsave:
                finish();
                break;
            case R.id.cloudsave_get:
                get();
                break;
            case R.id.cloudsave_save:
                save();
                break;
            case R.id.cloudsave_set:
                break;
        }
    }

    /**
     * 备份到云端数据
     */
    private void save() {
        final String url = Constant.data + HttpByGet.get("user", "test", "act", "save", "title", "联胜无轨");
        Logs.d(url);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                String executeHttpGet = HttpByGet.executeHttpGet(url);
                Logs.d(executeHttpGet);
                try {
                    JSONObject jo = new JSONObject(executeHttpGet);
                    String ret = jo.getString("ret");
                    if (ret.equals("ok")) {
                        handler.sendEmptyMessage(SAVE);
                    } else {
                        handler.sendEmptyMessage(SAVE_ERROR);
                        ;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 得到备份数据
     */
    private void get() {
        final String url = Constant.data + HttpByGet.get("user", "test", "act", "get");
        Logs.d(url);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                String executeHttpGet = HttpByGet.executeHttpGet(url);
                Logs.d(executeHttpGet);
                try {
                    JSONObject jo = new JSONObject(executeHttpGet);
                    String ret = jo.getString("ret");
                    String control = jo.getString("control");
                    if (ret.equals("ok")) {
                        Message msg = handler.obtainMessage(GET);
                        msg.obj = control;
                        handler.sendMessage(msg);
                    } else {
                        handler.sendEmptyMessage(GET_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
