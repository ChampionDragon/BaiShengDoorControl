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
import com.bs.bean.ErrorManagerBean;
import com.bs.constant.Constant;
import com.bs.util.TimeUtil;
import com.bs.util.ViewHolderUtil;
import com.bs.view.MyListView;

public class ErrorAdapter extends BaseAdapter {
	private Activity activity;
	private List<ErrorManagerBean> list;
	private ErrorItemAdapter adapter;

	public ErrorAdapter(Activity activity, List<ErrorManagerBean> list) {
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
					R.layout.item_errorlv, null);
		}
		MyListView myListView = ViewHolderUtil
				.get(convertView, R.id.errorlv_lv);
		TextView tv = ViewHolderUtil.get(convertView, R.id.errorlv_tv);

		ErrorManagerBean errorManagerBean = list.get(position);

		String today = TimeUtil.long2time(System.currentTimeMillis(),
				Constant.cformatD);
		String date = errorManagerBean.getData();
		if (today.equals(date)) {
			tv.setText("今天");
		} else {
			tv.setText(date);
		}

		List<ErrorBean> beans = errorManagerBean.getList();
		initLv(myListView, beans);

		return convertView;
	}

	private void initLv(MyListView myListView, List<ErrorBean> beans) {
		adapter=(ErrorItemAdapter) myListView.getAdapter();
		if(adapter==null){
			adapter=new ErrorItemAdapter(activity, beans);
			myListView.setAdapter(adapter);
		}else {
			adapter.setData(beans);
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 添加刷新的数据
	 */
	public void setData(List<ErrorManagerBean> ErrorManagerBean) {
		list = ErrorManagerBean;
	}

}
