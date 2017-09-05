package com.bs.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bs.R;
import com.bs.bean.ErrorBean;
import com.bs.constant.Constant;
import com.bs.util.TimeUtil;
import com.bs.util.ViewHolderUtil;

public class ErrorItemAdapter extends BaseAdapter {
	private Activity activity;
	private List<ErrorBean> list;

	public ErrorItemAdapter(Activity activity, List<ErrorBean> list) {
		super();
		this.activity = activity;
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
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.item_errorlv_item, null);
		}
		TextView time = ViewHolderUtil.get(convertView, R.id.error_time);
		TextView name = ViewHolderUtil.get(convertView, R.id.error_name);

		ErrorBean errorBean = list.get(position);
		String timeStr = TimeUtil.long2time(errorBean.getCreattime(),
				Constant.formatminute);
		time.setText(timeStr);
		name.setText(errorBean.getDeviceError());

		return convertView;
	}

	/**
	 * 添加刷新的数据
	 */
	public void setData(List<ErrorBean> ErrorBean) {
		list = ErrorBean;
	}

}
