package com.bs.account;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bs.MainActivity;
import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.base.BaseApplication;
import com.bs.constant.Constant;
import com.bs.constant.SpKey;
import com.bs.http.HttpByPost;
import com.bs.util.DialogNotileUtil;
import com.bs.util.Logs;
import com.bs.util.MD5;
import com.bs.util.NetConnectUtil;
import com.bs.util.SmallUtil;
import com.bs.util.SpUtil;
import com.bs.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class Login extends BaseActivity implements OnClickListener {
    private EditText phone, psw;
    private TextView resetpsw;
    private Button login, register;
    private static final int LOGIN = 0;
    private static final int LOGIN_FAIL = 1;
    private SpUtil sp;
    private String url, urlTest = "http://192.168.10.217:8888/SSM/loginajax.do";

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGIN:
                    ToastUtil.showLong("登录成功");
                    sp.putBoolean(SpKey.isLogin, true);
                    Logs.v("login 53  " + sp.getBoolean(SpKey.isLogin));
                    SmallUtil.getActivity(Login.this, MainActivity.class);
                    finish();
                    break;
                case LOGIN_FAIL:
                    DialogNotileUtil.show(Login.this, "登陆失败\n用户名或密码错误");
                    break;

            }
        }
    };

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sp = SpUtil.getInstance(SpKey.SP_device, MODE_PRIVATE);
        initView();
        boolean netConnect = NetConnectUtil.NetConnect(this);
        if (!netConnect) {
            DialogNotileUtil.show(this, "请先将网络打开");
        }
//        Logs.d("login 77  " + sp.getBoolean(SpKey.isLogin));
    }

    private void initView() {
        findViewById(R.id.back_login).setOnClickListener(this);
        phone = (EditText) findViewById(R.id.login_phone);
        psw = (EditText) findViewById(R.id.login_psw);
        resetpsw = (TextView) findViewById(R.id.login_resetpwd);
        resetpsw.setOnClickListener(this);
        login = (Button) findViewById(R.id.login_login);
        login.setOnClickListener(this);
        register = (Button) findViewById(R.id.login_register);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_login:
                BaseApplication.getInstance().exitApp();
                break;
            case R.id.login_login:
//                loginCheck();
//                  login();
                okHttpLogin();
                break;
            case R.id.login_register:
                SmallUtil.getActivity(Login.this, Register.class);
                break;
            case R.id.login_resetpwd:
                SmallUtil.getActivity(Login.this, ResetPwd.class);
                break;
        }
    }

    /*   ++++++++++++++++++++++++++++++++  测试后台  +++++++++++++++++++++++++++++++++++++   */
    private void login() {
        String name = phone.getText().toString();
        String pwd = psw.getText().toString();
        final JSONObject jb = new JSONObject();
        try {
            jb.put("username", name);
            jb.put("password", pwd);
        } catch (JSONException e) {
            Logs.d("127  " + e);
        }
        executor.submit(new Runnable() {
            @Override
            public void run() {
                //通过post方法传输jsonobject数据
//                JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlTest, jb,
//                        new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject jsonObject) {
//                                Logs.d("127"+ jsonObject.toString());
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Logs.e("142  " + error.toString());
//                    }
//                });
//                BaseApplication.queue.add(JsonObjectRequest);
                String s = HttpByPost.executeHttpPost(jb.toString(), urlTest);
                Logs.d(s + "   149");
            }
        });
    }


    public void okHttpLogin() {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                /*数据类型*/
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            /*申明给服务端传递一个json串*/
                String name = phone.getText().toString();
                String pwd = psw.getText().toString();
                final JSONObject jb = new JSONObject();
                try {
                    jb.put("username", name);
                    jb.put("password", pwd);
                    Logs.v("168 " + jb.toString());
                } catch (JSONException e) {
                    Logs.d("127  " + e);
                }
                //创建一个OkHttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
                RequestBody requestBody = RequestBody.create(JSON, jb.toString());
                //创建一个请求对象
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(urlTest)
                        .post(requestBody)
                        .build();

                //发送请求获取响应
                try {
                    okhttp3.Response response = okHttpClient.newCall(request).execute();
                    //判断请求是否成功
                    if (response.isSuccessful()) {
                        //打印服务端返回结果
                        Logs.i("185   " + response.body().string() + "  " + jb.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Logs.e("192  " + e.toString());
                }
            }
        });
    }

    /*  ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++   */


    private void loginCheck() {
        String phoneStr = phone.getText().toString();
        boolean checkphone = TextUtils.isEmpty(phoneStr);
        String pswStr = psw.getText().toString();
//		pswStr = getMd5(pswStr);
        Logs.d(pswStr);
        boolean checkpsw = TextUtils.isEmpty(pswStr);
        if (checkphone) {
            ToastUtil.showLong("手机号码不能为空");
            return;
        }
        // else if (phoneStr.length() != 11) {
        // ToastUtil.showLong("手机格式不对");
        // return;
        // }
        else if (checkpsw) {
            ToastUtil.showLong("密码不能为空");
            return;
        }
        url = Constant.urlLogin + "?passwd=" + pswStr + "&user=" + phoneStr;
        executor.submit(loginRunnable);
    }

    /**
     * MD5加密
     */
    private String getMd5(String pswStr) {
        String key = Constant.key + pswStr;
        key = MD5.MD5Low32(key);
        key = "###" + MD5.MD5Low32(key);
        try {
            key = URLEncoder.encode(key, "utf-8");// URL编码
            Logs.d(key);
            // 它是一种编码类型。当URL地址里包含非西欧字符的字符串时，系统会将这些字符转换成application/x-www-form-urlencoded字符串。
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return key;
    }

    Runnable loginRunnable = new Runnable() {

        @Override
        public void run() {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Logs.i("volleyget success  " + response);
                    Response(response);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logs.d("volleyget fail  " + error);
                }
            });
            BaseApplication.queue.add(stringRequest);
        }
    };

    /**
     * 解析返回的参数
     */
    private void Response(String response) {
        String ret = "";
        try {
            JSONObject jo = new JSONObject(response);
            ret = jo.getString("ret");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (ret.equals("fail")) {
            handler.sendEmptyMessage(LOGIN_FAIL);
        }
        if (ret.equals("ok")) {
            handler.sendEmptyMessage(LOGIN);
        }

    }

    @Override
    public void onBackPressed() {
        BaseApplication.getInstance().exitApp();
    }


}
