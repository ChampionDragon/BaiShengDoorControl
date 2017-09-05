package com.bs.activity;

import android.os.Bundle;
import android.view.View;

import com.bs.R;
import com.bs.base.BaseActivity;

public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        findViewById(R.id.back_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
