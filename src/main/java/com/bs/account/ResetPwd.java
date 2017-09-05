package com.bs.account;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.constant.Constant;
import com.bs.http.HttpByGet;
import com.bs.util.DialogNotileUtil;
import com.bs.util.SmallUtil;
import com.bs.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class ResetPwd extends BaseActivity implements OnClickListener {
    private EditText phone, code, pwd, pwdConfirm;
    private Button resetpwd, codeGet;
    private static final int RESETPWD = 0;
    private static final int RESETPWD_ERROR = 1;
    private static final int RESETPWD_FAIL = 2;
    private static final int CODE = 3;
    private static final int CODE_ERROR = 4;
    private static final int CODE_FAIL = 5;
    private boolean codeCheck = true;
    private TimeCount timeCount;
    private String codeError;
    private String ResetPwdError;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RESETPWD:
                    SmallUtil.getActivity(ResetPwd.this, Login.class);
                    ToastUtil.showLong("修改密码成功");
                    finish();
                    break;
                case RESETPWD_ERROR:
                    DialogNotileUtil.show(ResetPwd.this, ResetPwdError);
                    break;
                case RESETPWD_FAIL:
                    DialogNotileUtil.show(ResetPwd.this, "修改密码失败");
                    break;
                case CODE:
                    ToastUtil.showLong("发送验证码成功");
                    break;
                case CODE_ERROR:
                    codeCheck = true;
                    timeCount.cancel();
                    codeGet.setText("获取验证码");
                    DialogNotileUtil.show(ResetPwd.this, codeError);
                    break;
                case CODE_FAIL:
                    codeCheck = true;
                    timeCount.cancel();
                    codeGet.setText("获取验证码");
                    DialogNotileUtil.show(ResetPwd.this, "发送验证码失败");
                    break;

            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpwd);
        initView();
        timeCount = new TimeCount(60000, 1000);

    }

    private void initView() {
        phone = (EditText) findViewById(R.id.resetpwd_phone);
        code = (EditText) findViewById(R.id.resetpwd_code);
        pwd = (EditText) findViewById(R.id.resetpwd_pwd);
        pwdConfirm = (EditText) findViewById(R.id.resetpwd_pwd_confirm);
        resetpwd = (Button) findViewById(R.id.resetpwd_btn);
        resetpwd.setOnClickListener(this);
        codeGet = (Button) findViewById(R.id.resetpwd_codeget);
        codeGet.setOnClickListener(this);
        findViewById(R.id.back_resetpwd).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetpwd_btn:
                registerCheck();
                break;
            case R.id.resetpwd_codeget:
                codeCheck();
                break;
            case R.id.back_resetpwd:
                finish();
                break;

        }
    }

    private void registerCheck() {
        String phoneStr = phone.getText().toString();
        String codeString = code.getText().toString();
        String pwdString = pwd.getText().toString();
        String pwdconfirString = pwdConfirm.getText().toString();
        boolean code = TextUtils.isEmpty(codeString);
        boolean pwd = TextUtils.isEmpty(pwdString);
        boolean pwdconfirm = TextUtils.isEmpty(pwdconfirString);
        boolean checkphone = TextUtils.isEmpty(phoneStr);
        if (checkphone) {
            ToastUtil.showLong("手机号码不能为空");
            return;
        } else if (phoneStr.length() != 11) {
            ToastUtil.showLong("手机不是11位");
            return;
        }
        if (code) {
            ToastUtil.showLong("验证码不能为空");
            return;
        } else if (codeString.length() != 6) {
            ToastUtil.showLong("验证码不是6位");
            return;
        }
        if (pwd) {
            ToastUtil.showLong("密码不能为空");
            return;
        } else if (pwdString.length() != 6) {
            ToastUtil.showLong("密码不是6位");
            return;
        }
        if (pwdconfirm) {
            ToastUtil.showLong("确认密码不能为空");
            return;
        } else if (pwdconfirString.length() != 6) {
            ToastUtil.showLong("确认密码不是6位");
            return;
        } else if (!pwdconfirString.equals(pwdString)) {
            ToastUtil.showLong("密码和确认密码不相同");
            return;
        }

        executor.submit(registerRunnable);

    }

    Runnable registerRunnable = new Runnable() {
        @Override
        public void run() {
            Random random = new Random();
            int nextInt = random.nextInt(100);
            SystemClock.sleep(3333);
            if (nextInt % 2 == 1) {
                handler.sendEmptyMessage(RESETPWD);
            } else {
                handler.sendEmptyMessage(RESETPWD_FAIL);
            }
        }
    };

    private void codeCheck() {
        String phoneStr = phone.getText().toString();
        boolean checkphone = TextUtils.isEmpty(phoneStr);
        if (checkphone) {
            ToastUtil.showLong("手机号码不能为空");
            return;
        } else if (phoneStr.length() != 11) {
            ToastUtil.showLong("手机格式不对");
            return;
        }
        if (codeCheck) {
            executor.submit(codeRunnable);
            timeCount.start();
        }
    }

    Runnable codeRunnable = new Runnable() {

        @Override
        public void run() {
            String url = Constant.mobile + HttpByGet.get("mobile", phone.getText().toString());
            String executeHttpGet = HttpByGet.executeHttpGet(url);
            try {
                JSONObject jsonObject = new JSONObject(executeHttpGet);
//                Logs.d("ResetPwd 198   "+executeHttpGet);
                int errocode = jsonObject.getInt("errocode");
//                Logs.d("ResetPwd 201   "+errocode);
                if (errocode == 0) {
                    handler.sendEmptyMessage(CODE);
                } else {
                    handler.sendEmptyMessage(CODE_FAIL);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            codeCheck = false;
            codeGet.setText(millisUntilFinished / 1000 + "s后重发");
        }

        @Override
        public void onFinish() {
            codeCheck = true;
            codeGet.setText("获取验证码");
        }

    }


}
