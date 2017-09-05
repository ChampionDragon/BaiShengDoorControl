package com.bs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bs.R;
import com.bs.account.Login;
import com.bs.account.ResetPwd;
import com.bs.base.BaseActivity;
import com.bs.base.BaseApplication;
import com.bs.constant.SpKey;
import com.bs.util.Logs;
import com.bs.util.SmallUtil;
import com.bs.util.SpUtil;

public class SetActivity extends BaseActivity implements View.OnClickListener {
    private SpUtil sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        findViewById(R.id.back_set).setOnClickListener(this);
        findViewById(R.id.set_resetpwd).setOnClickListener(this);
        findViewById(R.id.set_out).setOnClickListener(this);
        sp=SpUtil.getInstance(SpKey.SP_device,MODE_PRIVATE);
        BaseApplication.getInstance().addActivity(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_set:
                finish();
                break;
            case R.id.set_resetpwd:
                SmallUtil.getActivity(SetActivity.this, ResetPwd.class);
                break;
            case R.id.set_out:
                sp.putBoolean(SpKey.isLogin, false);
                Logs.d("set 38  " + sp.getBoolean(SpKey.isLogin));
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                finish();
        }

    }
}
