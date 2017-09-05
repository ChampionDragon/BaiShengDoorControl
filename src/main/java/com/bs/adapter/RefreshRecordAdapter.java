package com.bs.adapter;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bs.R;
import com.bs.bean.ControlBean;
import com.bs.bean.DeviceManagerBean;
import com.bs.constant.Constant;
import com.bs.util.TimeUtil;
import com.bs.util.ViewHolderUtil;
import com.bs.view.MyListView;

public class RefreshRecordAdapter extends BaseAdapter {
	private Context context;
	private List<DeviceManagerBean> list;
	private LogItemAdapter adapter;
	private Dialog dialog;
	String tag = "lcb";

	public RefreshRecordAdapter(Context context, List<DeviceManagerBean> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {

		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_loglv, null);
		}
		MyListView listView = ViewHolderUtil.get(convertView, R.id.loglv_lv);
		TextView tv = ViewHolderUtil.get(convertView, R.id.loglv_tv);
		DeviceManagerBean deviceManage = list.get(position);
		String today = TimeUtil.long2time(System.currentTimeMillis(),
				Constant.cformatD);
		String date = deviceManage.getData();
		if (today.equals(date)) {
			tv.setText("今天");
		} else {
			tv.setText(date);
		}

		List<ControlBean> devicebean = deviceManage.getList();
		initLv(listView, devicebean);
		return convertView;
	}

	/**
	 * 初始化子listview
	 */
	private void initLv(MyListView listView, List<ControlBean> devicebean) {
		adapter = (LogItemAdapter) listView.getAdapter();
		if (adapter == null) {
			adapter = new LogItemAdapter(context, devicebean);
			listView.setAdapter(adapter);
		} else {
			adapter.setData(devicebean);
			adapter.notifyDataSetChanged();
		}

	}

	/**
	 * 添加刷新的数据
	 */
	public void setData(List<DeviceManagerBean> DeviceManagerBean) {
		list = DeviceManagerBean;
	}
}
