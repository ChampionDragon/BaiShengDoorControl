package com.bs.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.util.SmallUtil;
import com.bs.util.SystemUtil;

public class AboutUsActivity extends BaseActivity {
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initView();
    }

    private void initView() {
        findViewById(R.id.back_aboutus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv = (TextView) findViewById(R.id.aboutus_tv);
        tv.setText("百胜智控" + SystemUtil.VersionName());
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmallUtil.getActivity(AboutUsActivity.this, CodeCreateActivity.class);
            }
        });

    }


}
