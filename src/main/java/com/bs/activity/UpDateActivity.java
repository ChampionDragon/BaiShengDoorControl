package com.bs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.constant.UpdateService;

public class UpDateActivity extends BaseActivity implements View.OnClickListener {
    private Intent intentService;

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
                break;
            case R.id.update_download:
                startService(intentService);
                break;
        }
    }


}
