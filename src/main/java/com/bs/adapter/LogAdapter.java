package com.bs.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bs.R;
import com.bs.bean.ControlBean;
import com.bs.bean.DeviceManagerBean;
import com.bs.constant.Constant;
import com.bs.db.DbHelper;
import com.bs.db.DbManager;
import com.bs.util.DialogCustomUtil;
import com.bs.util.TimeUtil;
import com.bs.util.ViewHolderUtil;
import com.bs.view.MyListView;

import java.util.List;

public class LogAdapter extends BaseAdapter {
    private Context context;
    private List<DeviceManagerBean> list;
    private LogItemAdapter adapter;
    private Dialog dialog;
    String tag = "lcb";

    public LogAdapter(Context context, List<DeviceManagerBean> list) {
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
        listView.setOnItemLongClickListener(itemLongClickListener);

        Log.d(tag, devicebean.size() + "    logadapter79");
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

    /**
     * 设置长安监听
     */
    OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
            final int location = position;
            // final List<ControlBean> devicebean =LogAdapter.this.devicebean;
            final AdapterView<?> adapterView = parent;

            dialog = DialogCustomUtil.create("警告", "你确定要从列表里删除这条信息", context,
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Log.d(tag, location + "    logadapter119  "
                            // + devicebean.size() + "   "
                            // + devicebean.get(location).getId());
                            // devicebean.remove(location);
                            // adapter.setData(devicebean);
                            // adapter.notifyDataSetChanged();

                            // 删除数据要放在前面，因为如果dvbeans.remove(location)再调用dvBean.getId()肯定越界。
                            ControlBean dvBean = (ControlBean) adapterView
                                    .getItemAtPosition(location);// 得到listView的item值。
                            int id = dvBean.getId();
                            // 删除数据库里数据
                            DbManager manager = DbManager.getmInstance(context,
                                    Constant.dbDiveceBsmk, Constant.dbVersion);
                            manager.cleanControl(DbHelper.DEVICE_CONTROL_ID
                                    + "=" + id);

                            LogItemAdapter adapter = (LogItemAdapter) adapterView
                                    .getAdapter();
                            List<ControlBean> dvbeans = adapter.list;
                            dvbeans.remove(location);
                            adapter.setData(dvbeans);
                            adapter.notifyDataSetChanged();

                            dialog.dismiss();
                        }
                    }, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
            return false;
        }
    };

}
