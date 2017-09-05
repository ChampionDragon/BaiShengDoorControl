package com.bs.activity;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bs.R;
import com.bs.base.BaseActivity;
import com.bs.bean.DeviceBean;
import com.bs.constant.Constant;
import com.bs.db.DbManager;
import com.bs.util.ToastUtil;

import java.util.Calendar;

public class DeviceSet extends BaseActivity implements OnClickListener {
    private EditText address;
    private TextView time;
    private Spinner name;
    private int position;
    private String nameStr, timeStr, addressStr, id;
    private int year, month, day;
    private DbManager managerDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_set);
        initView();
        initdata();
    }

    private void initdata() {
        Bundle bundle = getIntent().getExtras();
//        position = bundle.getInt("position");
        id = bundle.getString("id");
        managerDb = DbManager.getmInstance(this, Constant.dbDiveceBsmk, Constant.dbVersion);
        DeviceBean device = managerDb.getDevice(id);
        address.setText(device.getAddress());
        time.setText(device.getCreateTime());
    }

    private void initView() {
        findViewById(R.id.back_set).setOnClickListener(this);
        findViewById(R.id.set_confirm).setOnClickListener(this);
        name = (Spinner) findViewById(R.id.set_name);
        address = (EditText) findViewById(R.id.set_address);
        time = (TextView) findViewById(R.id.set_time);
        address.setOnClickListener(this);
        time.setOnClickListener(this);
        initSpinner();
    }

    private void initSpinner() {
        ArrayAdapter<String> adapter = getadapter();
        // = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,
        // mItems);//系统默认的布局
        // 绑定 Adapter到控件
        name.setAdapter(adapter);
        name.setOnItemSelectedListener(listener);
    }

    private ArrayAdapter<String> getadapter() {
        final String[] mItems = getResources().getStringArray(
                R.array.devicename);
        // 建立Adapter并且绑定数据源,默认系统布局
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_checked_text, mItems) {

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = LayoutInflater.from(DeviceSet.this).inflate(
                        R.layout.spinner_item_layout, null);
                TextView label = (TextView) view
                        .findViewById(R.id.spinner_item_label);
                ImageView check = (ImageView) view
                        .findViewById(R.id.spinner_item_checked_image);
                label.setText(mItems[position]);
                if (name.getSelectedItemPosition() == position) {
                    view.setBackgroundColor(getResources().getColor(
                            R.color.blue_deep));
                    check.setImageResource(R.drawable.dot_press);
                } else {
                    view.setBackgroundColor(getResources().getColor(
                            R.color.blueSky));
                    check.setImageResource(R.drawable.dot);
                }

                return view;
            }

        };

        return adapter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_set:
                finish();
                break;
            case R.id.set_address:
                break;
            case R.id.set_time:
                ChoiceTime();
                break;
            case R.id.set_confirm:
//                Bundle bundle = new Bundle();
//                bundle.putString("name", nameStr);
//                bundle.putInt("position", position);
//                Intent intent = new Intent().putExtras(bundle);
//                setResult(RESULT_OK, intent);
                confirm();
                finish();
        }

    }

    private void confirm() {
        addressStr = address.getText().toString();
        boolean b = managerDb.addOrUpdateDevice(id, nameStr, addressStr, timeStr);
        if (b) {
            ToastUtil.showLong("设备设置成功");
        }
    }

    private void ChoiceTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, dateSetListener, year, month, day).show();
    }

    OnDateSetListener dateSetListener = new OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            timeStr = year + "年" + (monthOfYear + 1) + "月" + dayOfMonth
                    + "日";
            time.setText(timeStr);
        }
    };

    OnItemSelectedListener listener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            nameStr = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

}
