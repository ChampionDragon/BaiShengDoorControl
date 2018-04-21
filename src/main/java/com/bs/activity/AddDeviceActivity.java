package com.bs.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.bean.DeviceBean;
import com.bs.constant.Constant;
import com.bs.db.DbManager;
import com.bs.http.HttpByGet;
import com.bs.util.DialogCustomUtil;
import com.bs.util.DialogNotileUtil;
import com.bs.util.Logs;
import com.bs.util.SmallUtil;
import com.bs.util.SoundPoolUtil;
import com.bs.util.ToastUtil;
import com.bs.zxing.MipcaActivityCapture;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author lcb
 * @date 2017-5-5
 */
public class AddDeviceActivity extends BaseActivity implements OnClickListener {
    private final static int SCANNIN_GREQUEST_CODE =5;
    private ImageView ivTwocode, ivManual, ivAuto, back;
    private LinearLayout twocode, manual, auto, id;
    private EditText etId;
    private int choose;
    private Dialog dialog;
    private String idStr;
    private static final int CHECKID = 0;
    private static final int CHECKID_FAIL = 1;
    private SoundPoolUtil mSoundPoolUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddevice);
        initview();
        initchoose();
    }


    private void initchoose() {
        choose = 1;
        // manual.setPressed(true);
        // auto.setPressed(false);
        // twocode.setPressed(false);
        manual.setBackgroundColor(getResources().getColor(R.color.gray_shallow));
        ivAuto.setVisibility(View.INVISIBLE);
        ivTwocode.setVisibility(View.INVISIBLE);
        mSoundPoolUtil = SoundPoolUtil.getInstance(this);
    }

    private void initview() {
        ivAuto = (ImageView) findViewById(R.id.adddevice_auto_iv);
        ivManual = (ImageView) findViewById(R.id.adddevice_manual_iv);
        ivTwocode = (ImageView) findViewById(R.id.adddevice_twocode_iv);
        twocode = (LinearLayout) findViewById(R.id.adddevice_twocode_ll);
        manual = (LinearLayout) findViewById(R.id.adddevice_manual_ll);
        auto = (LinearLayout) findViewById(R.id.adddevice_auto_ll);
        twocode.setOnClickListener(this);
        manual.setOnClickListener(this);
        auto.setOnClickListener(this);
        etId = (EditText) findViewById(R.id.adddevice_id_et);
        id = (LinearLayout) findViewById(R.id.adddevice_id_ll);
        back = (ImageView) findViewById(R.id.back_adddevice);
        back.setOnClickListener(this);
        findViewById(R.id.adddevice_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.adddevice_auto_ll:
                choose = 2;
                manual.setBackgroundColor(getResources().getColor(R.color.white));
                twocode.setBackgroundColor(getResources().getColor(R.color.white));
                auto.setBackgroundColor(getResources().getColor(
                        R.color.gray_shallow));
                ivAuto.setVisibility(View.VISIBLE);
                ivManual.setVisibility(View.INVISIBLE);
                ivTwocode.setVisibility(View.INVISIBLE);
                id.setVisibility(View.INVISIBLE);

                break;
            case R.id.adddevice_manual_ll:
                choose = 1;
                manual.setBackgroundColor(getResources().getColor(
                        R.color.gray_shallow));
                auto.setBackgroundColor(getResources().getColor(R.color.white));
                twocode.setBackgroundColor(getResources().getColor(R.color.white));
                ivAuto.setVisibility(View.INVISIBLE);
                ivTwocode.setVisibility(View.INVISIBLE);
                ivManual.setVisibility(View.VISIBLE);
                id.setVisibility(View.VISIBLE);

                break;
            case R.id.adddevice_twocode_ll:
                choose = 0;
                twocode.setBackgroundColor(getResources().getColor(R.color.gray_shallow));
                auto.setBackgroundColor(getResources().getColor(R.color.white));
                manual.setBackgroundColor(getResources().getColor(R.color.white));
                ivAuto.setVisibility(View.INVISIBLE);
                ivManual.setVisibility(View.INVISIBLE);
                ivTwocode.setVisibility(View.VISIBLE);
                id.setVisibility(View.INVISIBLE);
                break;
            case R.id.back_adddevice:
                finish();
                break;

            case R.id.adddevice_next:
                switch (choose) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setClass(AddDeviceActivity.this, MipcaActivityCapture.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                        break;
                    case 1:

                        Manual();

                        break;
                    case 2:
//                        SmallUtil.getActivity(AddDeviceActivity.this,
//                                AutoScanActivity.class);
                        SmallUtil.getActivity(AddDeviceActivity.this,
                                WifiSetActivity.class);
                        finish();
                        break;
                }
                break;

        }
    }

    /**
     * 手动添加
     */
    private void Manual() {
        idStr = etId.getText().toString();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                String url = Constant.checkId + HttpByGet.get("id", idStr);
                Logs.v("发送的内容："+url);
                String result = HttpByGet.executeHttpGet(url);
                Logs.e("设备是否存在的返回结果:" + result);
                result = result.replace("(", "").replace(")", "");
                Response(result);
            }
        });

    }


    /**
     * 解析返回的参数
     */
    private void Response(String response) {
        Logs.d("adddevice179  " + response);
        String ret = "";
        try {
            JSONObject jo = new JSONObject(response);
            ret = jo.getString("ret");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (ret.equals("fail")) {
            mHandler.sendEmptyMessage(CHECKID_FAIL);
        }
        if (ret.equals("ok")) {
            mHandler.sendEmptyMessage(CHECKID);
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHECKID:
                    DbManager dbManager = DbManager.getmInstance(AddDeviceActivity.this, Constant.dbDiveceBsmk, Constant.dbVersion);
                    DeviceBean device = dbManager.getDevice(idStr);
                    Logs.e(device.getName() + device.getAddress() + device.getCreateTime());
                    boolean b = dbManager.addOrUpdateDevice(idStr, device.getName(), device.getAddress(), device.getCreateTime());
                    if (b) {
                        ToastUtil.showLong("添加或更新设备成功");
                    }

                    Intent intent = new Intent(AddDeviceActivity.this, WifiSetActivity.class);
                    intent = intent.putExtra(Constant.deviceId, idStr);
                    startActivity(intent);
                    finish();

                    break;
                case CHECKID_FAIL:
                    mSoundPoolUtil.play(Constant.DOERROR);
                    SpannableString spannableString = new SpannableString("配置失败\n\n设备号" + idStr + "不存在");
                    RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(1.5f);//设置字体大小
                    RelativeSizeSpan relativeSizeSpan1 = new RelativeSizeSpan(1.3f);//设置字体大小
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.red));//设置颜色
                    ForegroundColorSpan blackSpan = new ForegroundColorSpan(getResources().getColor(R.color.black));//设置颜色
                    spannableString.setSpan(relativeSizeSpan1, 9, idStr.length() + 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(colorSpan, 9, idStr.length() + 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(relativeSizeSpan, 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(blackSpan, 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    DialogNotileUtil.show(AddDeviceActivity.this, spannableString);
                    break;

            }

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    // 扫描到的内容
                    String str = bundle.getString("result");
                    dialog = DialogCustomUtil.create("二维码返回的结果", str,
                            AddDeviceActivity.this, new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                }
                break;
        }

    }

}
