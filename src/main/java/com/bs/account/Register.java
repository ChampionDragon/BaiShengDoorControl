package com.bs.account;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
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
import com.bs.util.Logs;
import com.bs.util.SmallUtil;
import com.bs.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends BaseActivity implements OnClickListener {
    private EditText phone, code, pwd, pwdConfirm;
    private Button register, codeGet;
    private static final int REGISTER = 0;
    private static final int REGISTER_ERROR = 1;
    private static final int REGISTER_FAIL = 2;
    private static final int CODE = 3;
    private static final int CODE_ERROR = 4;
    private static final int CODE_FAIL = 5;
    private static final int RESULT_ERROR = 6;
    private boolean codeCheck = true;
    private TimeCount timeCount;
    private String codeError;
    private String registerError;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REGISTER:
                    SmallUtil.getActivity(Register.this, Login.class);
                    ToastUtil.showLong("用户注册成功");
                    finish();
                    break;
                case REGISTER_ERROR:
                    DialogNotileUtil.show(Register.this, registerError);
                    break;
                case REGISTER_FAIL:
                    DialogNotileUtil.show(Register.this, "用户注册失败");
                    break;
                case CODE:
                    ToastUtil.showLong("发送验证码成功");
                    break;
                case CODE_ERROR:
                    codeCheck = true;
                    timeCount.cancel();
                    codeGet.setText("获取验证码");
                    DialogNotileUtil.show(Register.this, codeError);
                    break;
                case CODE_FAIL:
                    codeCheck = true;
                    timeCount.cancel();
                    codeGet.setText("获取验证码");
                    DialogNotileUtil.show(Register.this, "发送验证码失败");
                    break;
                case RESULT_ERROR:
                    codeCheck = true;
                    timeCount.cancel();
                    codeGet.setText("获取验证码");
                    DialogNotileUtil.show(Register.this, "后台接口有误");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        timeCount = new TimeCount(60000, 1000);

    }

    private void initView() {
        phone = (EditText) findViewById(R.id.register_phone);
        code = (EditText) findViewById(R.id.register_code);
        pwd = (EditText) findViewById(R.id.register_pwd);
        pwdConfirm = (EditText) findViewById(R.id.register_pwd_confirm);
        register = (Button) findViewById(R.id.register_btn);
        register.setOnClickListener(this);
        codeGet = (Button) findViewById(R.id.register_codeget);
        codeGet.setOnClickListener(this);
        findViewById(R.id.back_register).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn:
                registerCheck();
                break;
            case R.id.register_codeget:
                codeCheck();
                break;
            case R.id.back_register:
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
            String url = Constant.register + HttpByGet.get("phone", phone.getText().toString(), "passwd",
                    pwd.getText().toString(), "code", code.getText().toString());
            Logs.e(url + "    Register160");
            String result = HttpByGet.executeHttpGet(url);
//            Logs.v(result + "    Register162");
            if (result.equals(HttpByGet.error)) {
                handler.sendEmptyMessage(RESULT_ERROR);
                return;
            }
            result = result.replace("(", "").replace(")", "");
//            Logs.w("Register174   "+result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String ret = jsonObject.getString("ret");
//                Logs.i("Register178   "+ret);
                if (ret.equals("ok")) {
                    handler.sendEmptyMessage(REGISTER);
                } else {
                    handler.sendEmptyMessage(REGISTER_FAIL);
                }
            } catch (JSONException e) {
                Logs.e("Register184   " + e);
                e.printStackTrace();
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
            if (executeHttpGet.equals(HttpByGet.error)) {
                handler.sendEmptyMessage(RESULT_ERROR);
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(executeHttpGet);
                Logs.d("Register 190   " + executeHttpGet);
                int errocode = jsonObject.getInt("errcode");
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
