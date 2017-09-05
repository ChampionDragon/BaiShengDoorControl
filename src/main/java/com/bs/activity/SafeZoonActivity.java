package com.bs.activity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.bs.R;
import com.bs.adapter.ErrorAdapter;
import com.bs.base.BaseActivity;
import com.bs.bean.ErrorBean;
import com.bs.bean.ErrorManagerBean;
import com.bs.constant.Constant;
import com.bs.constant.SpKey;
import com.bs.db.DbManager;
import com.bs.util.SmallUtil;
import com.bs.util.SpUtil;
import com.bs.util.TimeUtil;

public class SafeZoonActivity extends BaseActivity implements OnClickListener {
	private ListView lv;
	private DbManager manager;
	private List<ErrorBean> errorBeans;
	private List<ErrorManagerBean> errorManagerBeans;
	private SpUtil spUtil;
	private ErrorAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_safezoon);
		initView();
		initData();
		initLv();
	}

	private void initLv() {
		adapter = new ErrorAdapter(this, errorManagerBeans);
		lv.setAdapter(adapter);
	}

	private void initData() {
		manager = DbManager.getmInstance(this, Constant.dbDiveceBsmk,
				Constant.dbVersion);
		spUtil = SpUtil.getInstance(SpKey.SP_device, MODE_PRIVATE);
		errorBeans = manager.getErrorList();
		errorManagerBeans = getData(errorBeans);

	}

	private List<ErrorManagerBean> getData(List<ErrorBean> errorBeans) {
		Map<String, List<ErrorBean>> map = new LinkedHashMap<>();
		String key = "";
		List<ErrorBean> beans = null;
		List<ErrorManagerBean> list = new ArrayList<>();
		for (int i = errorBeans.size() - 1; i > 0; i--) {
			ErrorBean errorBean = errorBeans.get(i);
			String date = TimeUtil.long2time(errorBean.getCreattime(),
					Constant.cformatD);
			boolean a = key.equals(date);
			if (!a) {
				beans = new ArrayList<>();
				key = date;
			}
			beans.add(errorBean);
			map.put(date, beans);
		}
		Set<Entry<String, List<ErrorBean>>> entrySet = map.entrySet();
		for (Entry<String, List<ErrorBean>> entry : entrySet) {
			list.add(new ErrorManagerBean(entry.getKey(), entry.getValue()));
		}

		return list;
	}

	private void initView() {
		lv = (ListView) findViewById(R.id.safezoon_lv);
		findViewById(R.id.back_safezoon).setOnClickListener(this);
		findViewById(R.id.refresh_safezoon).setOnClickListener(this);
		findViewById(R.id.safezoon_add).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_safezoon:
			finish();
			break;
		case R.id.refresh_safezoon:
			Refresh();
			break;
		case R.id.safezoon_add:
			SmallUtil.getActivity(SafeZoonActivity.this,
					AddDeviceActivity.class);
			break;
		}

	}

	private void Refresh() {
		int a = spUtil.getInt(SpKey.deviceError);
		manager.addOrUpdateError(a, "错误测试", System.currentTimeMillis());
		errorBeans = manager.getErrorList();
		errorManagerBeans = getData(errorBeans);
		spUtil.putInt(SpKey.deviceError, a + 1);
		refresh(errorManagerBeans);
	}

	private void refresh(List<ErrorManagerBean> errorManagerBeans) {
		adapter = (ErrorAdapter) lv.getAdapter();
		if (adapter == null) {
			adapter = new ErrorAdapter(this, errorManagerBeans);
			lv.setAdapter(adapter);
		} else {
			adapter.setData(errorManagerBeans);
			adapter.notifyDataSetChanged();
		}
	}

}
