package com.bs.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.base.BaseApplication;
import com.bs.util.SmallUtil;
import com.bs.util.ToastUtil;

public class UsermanagerActivity extends BaseActivity implements
		OnClickListener {
	private String tag = "lcb";
	private ImageView back;
	private TextView user, users, authorize, set;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_usermanager);
        BaseApplication.getInstance().addActivity(this);
		initview();
	}

	private void initview() {
		findViewById(R.id.userm_authorize).setOnClickListener(this);
		findViewById(R.id.userm_set).setOnClickListener(this);
		findViewById(R.id.userm_user).setOnClickListener(this);
		findViewById(R.id.userm_users).setOnClickListener(this);
		findViewById(R.id.back_usermanager).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.userm_authorize:
			ToastUtil.showShort("authorize");
			break;
		case R.id.userm_set:
            SmallUtil.getActivity(UsermanagerActivity.this, SetActivity.class);
            break;
            case R.id.userm_user:
            SmallUtil.getActivity(UsermanagerActivity.this, UserActivity.class);
			break;
		case R.id.userm_users:
			ToastUtil.showShort("users");
			break;
		case R.id.back_usermanager:
			finish();
			break;
		}

	}

}
