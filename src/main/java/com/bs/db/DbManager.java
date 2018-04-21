package com.bs.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bs.bean.ControlBean;
import com.bs.bean.DeviceBean;
import com.bs.bean.ErrorBean;

import java.util.ArrayList;
import java.util.List;


/**
 * 数据库管理类
 *
 * @author lcb
 * @date 2017-5-8
 */
public class DbManager {
    public static DbManager mInstance = null;
    private DbHelper mDbHelper = null;
    private Context mContext = null;
    private String tag = "lcb";

    public DbManager(Context context, String dbName, int dbVersion) {
        super();
        mContext = context;
        mDbHelper = DbHelper.getInstance(context, dbName, dbVersion);
    }

    public static DbManager getmInstance(Context context, String dbName,
                                         int dbVersion) {
        if (mInstance == null) {
            mInstance = new DbManager(context, dbName, dbVersion);
        }
        return mInstance;
    }

	/* 有关设备控制记录数据库操作 */

    /**
     * 设备控制记录的增加或更新
     */
    public boolean addOrUpdateControl(int id, String control, String name,
                                      long time) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.DEVICE_CONTROL_NAME, name);
        values.put(DbHelper.DEVICE_CONTROL, control);
        values.put(DbHelper.DEVICE_CONTROL_TIME, time);
        String sql = "select *from " + DbHelper.TABLE_DEVICE_CONTROL
                + " where " + DbHelper.DEVICE_CONTROL_ID + "=?";
        Cursor cursor = mDbHelper.rawQuery(sql, new String[]{id + ""});
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                mDbHelper.update(DbHelper.TABLE_DEVICE_CONTROL, values,
                        DbHelper.DEVICE_CONTROL_ID + "=?", new String[]{id
                                + ""});
                Log.w(tag, "dbm60  device  update");
            }

        } else {
            values.put(DbHelper.DEVICE_CONTROL_ID, id);
            mDbHelper.insert(DbHelper.TABLE_DEVICE_CONTROL, values);
            Log.w(tag, "dbm66  device  add");
        }
        cursor.close();
        return true;
    }

    /**
     * 删除控制记录
     */
    public boolean cleanControl(String whereClause, String[] whereArgs) {
        mDbHelper.delete(DbHelper.TABLE_DEVICE_CONTROL, whereClause, whereArgs);
        Log.d(tag, "dbm77  device delete");
        return true;
    }

    public boolean cleanControl(String whereClause) {
        return cleanControl(whereClause, null);
    }

    /**
     * 设备的某条控制记录
     */
    public List<ControlBean> getControlList() {
        List<ControlBean> list = new ArrayList<>();
        Cursor cursor = mDbHelper.query(DbHelper.TABLE_DEVICE_CONTROL, null,
                null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ControlBean device = new ControlBean();
                device.setDeviceName(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_CONTROL_NAME)));
                device.setCreattime(cursor.getLong(cursor
                        .getColumnIndex(DbHelper.DEVICE_CONTROL_TIME)));
                device.setDeviceControl(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_CONTROL)));

                device.set_id(cursor.getInt(cursor
                        .getColumnIndex("_id")));

                device.setId(cursor.getInt(cursor
                        .getColumnIndex(DbHelper.DEVICE_CONTROL_ID)));
                list.add(device);
            }
        }
        Log.e(tag, "dbm111  CTRlist  cursor:" + cursor.getCount());
        cursor.close();
        return list;
    }

    /**
     * 指定的设备控制记录
     */
    public ControlBean getControl(int id) {
        ControlBean device = new ControlBean();
        String sql = "select *from " + DbHelper.TABLE_DEVICE_CONTROL
                + " where " + DbHelper.DEVICE_CONTROL_ID + "=?";
        Cursor cursor = mDbHelper.rawQuery(sql, new String[]{id + ""});
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                device.setCreattime(cursor.getLong(cursor
                        .getColumnIndex(DbHelper.DEVICE_CONTROL_TIME)));
                device.setDeviceControl(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_CONTROL)));
                device.setDeviceName(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_CONTROL_NAME)));
                device.setId(cursor.getInt(cursor
                        .getColumnIndex(DbHelper.DEVICE_CONTROL_ID)));
            }
        }
        Log.i(tag, "dbm135  device");
        cursor.close();
        return device;
    }




	/* 有关设备报错数据库操作 */


    /**
     * 设备报错记录的增加或更新
     */
    public boolean addOrUpdateError(int id, String error, long time) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.DEVICE_ERROR, error);
        values.put(DbHelper.DEVICE_ERROR_TIME, time);
        String sql = "select *from " + DbHelper.TABLE_DEVICE_ERROR + " where "
                + DbHelper.DEVICE_ERROR_ID + "=?";
        Cursor cursor = mDbHelper.rawQuery(sql, new String[]{id + ""});
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                mDbHelper.update(DbHelper.TABLE_DEVICE_ERROR, values,
                        DbHelper.DEVICE_ERROR_ID + "=?",
                        new String[]{id + ""});
                Log.w(tag, "dbm158  error  update");
            }

        } else {
            values.put(DbHelper.DEVICE_ERROR_ID, id);
            mDbHelper.insert(DbHelper.TABLE_DEVICE_ERROR, values);
            Log.w(tag, "dbm164  error  add");
        }
        cursor.close();
        return true;
    }

    /**
     * 删除设备报错记录
     */
    public boolean cleanError(String whereClause, String[] whereArgs) {
        mDbHelper.delete(DbHelper.TABLE_DEVICE_ERROR, whereClause, whereArgs);
        Log.i(tag, "dbm175  error  delete");
        return true;
    }

    public boolean cleanError(String whereClause) {
        return cleanError(whereClause, null);
    }

    /**
     * 设备报错记录的数据集合
     */
    public List<ErrorBean> getErrorList() {
        List<ErrorBean> list = new ArrayList<>();
        Cursor cursor = mDbHelper.query(DbHelper.TABLE_DEVICE_ERROR, null,
                null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ErrorBean error = new ErrorBean();
                error.setCreattime(cursor.getLong(cursor
                        .getColumnIndex(DbHelper.DEVICE_ERROR_TIME)));
                error.setDeviceError(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_ERROR)));
                error.setId(cursor.getInt(cursor
                        .getColumnIndex(DbHelper.DEVICE_ERROR_ID)));
                list.add(error);
            }
        }
        Log.v(tag, "dbm198  errorlist  cursor" + cursor.getCount());
        cursor.close();
        return list;
    }

    /**
     * 指定的设备报错记录数据
     */
    public ErrorBean getError(int id) {
        ErrorBean error = new ErrorBean();
        String sql = "select *from " + DbHelper.TABLE_DEVICE_ERROR + " where "
                + DbHelper.DEVICE_ERROR_ID + "=?";
        Cursor cursor = mDbHelper.rawQuery(sql, new String[]{id + ""});
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                error.setCreattime(cursor.getLong(cursor
                        .getColumnIndex(DbHelper.DEVICE_ERROR_TIME)));
                error.setDeviceError(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_ERROR)));

                error.setId(cursor.getInt(cursor
                        .getColumnIndex(DbHelper.DEVICE_ERROR_ID)));
            }
        }
        Log.e(tag, "dbm222  error");
        cursor.close();
        return error;
    }

	/*有关设备数据库操作*/

    /**
     * 设备的增加或更新
     */
    public boolean addOrUpdateDevice(String id, String name, String address, String creattime) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.DEVICE_NAME, name);
        values.put(DbHelper.DEVICE_ADDRESS, address);
        values.put(DbHelper.DEVICE_ERROR_TIME, creattime);
        String sql = "select *from " + DbHelper.TABLE_DEVICE + " where "
                + DbHelper.DEVICE_ID + "=?";
        Cursor cursor = mDbHelper.rawQuery(sql, new String[]{id});
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                mDbHelper.update(DbHelper.TABLE_DEVICE, values,
                        DbHelper.DEVICE_ID + "=?",
                        new String[]{id + ""});
                Log.w(tag, "dbm245  device  update");
            }

        } else {
            values.put(DbHelper.DEVICE_ID, id);
            mDbHelper.insert(DbHelper.TABLE_DEVICE, values);
            Log.w(tag, "dbm251  device  add");
        }
        cursor.close();
        return true;
    }

    /**
     * 删除设备记录
     */
    public boolean cleanDevice(String whereClause, String[] whereArgs) {
        mDbHelper.delete(DbHelper.TABLE_DEVICE, whereClause, whereArgs);
        Log.i(tag, "dbm262  device  delete");
        return true;
    }

    public boolean cleanDevice(String whereClause) {
        return cleanDevice(whereClause, null);
    }

    /**
     * 设备的数据集合
     */
    public List<DeviceBean> getDeviceList() {
        List<DeviceBean> list = new ArrayList<>();
        Cursor cursor = mDbHelper.query(DbHelper.TABLE_DEVICE, null,
                null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                DeviceBean deviceBean = new DeviceBean();
                deviceBean.setId(cursor.getInt(cursor
                        .getColumnIndex(DbHelper.ID)));
                deviceBean.setNumber(cursor.getInt(cursor
                        .getColumnIndex(DbHelper.DEVICE_NUMBER)));
                deviceBean.setName(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_NAME)));
                deviceBean.setCreateTime(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_TIME)));
                deviceBean.setDeviceId(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_ID)));
                deviceBean.setFlagOne(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_FLAGONE)));
                deviceBean.setFlagTwo(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_FLAGTWO)));
                deviceBean.setAddress(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_ADDRESS)));
                list.add(deviceBean);
            }
        }
        Log.v(tag, "dbm293  devicelist  cursor" + cursor.getCount());
        cursor.close();
        return list;
    }

    /**
     * 指定的设备报错记录数据
     */
    public DeviceBean getDevice(String id) {
        DeviceBean deviceBean = new DeviceBean();
        String sql = "select *from " + DbHelper.TABLE_DEVICE + " where "
                + DbHelper.DEVICE_ID + "=?";
        Cursor cursor = mDbHelper.rawQuery(sql, new String[]{id});
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                deviceBean.setId(cursor.getInt(cursor
                        .getColumnIndex(DbHelper.ID)));
                deviceBean.setNumber(cursor.getInt(cursor
                        .getColumnIndex(DbHelper.DEVICE_NUMBER)));
                deviceBean.setName(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_NAME)));
                deviceBean.setCreateTime(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_TIME)));
                deviceBean.setDeviceId(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_ID)));
                deviceBean.setFlagOne(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_FLAGONE)));
                deviceBean.setFlagTwo(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_FLAGTWO)));
                deviceBean.setAddress(cursor.getString(cursor
                        .getColumnIndex(DbHelper.DEVICE_ADDRESS)));
            }
        }
        Log.e(tag, "dbm321  del_device");
        cursor.close();
        return deviceBean;
    }


    /**
     * 删除所有表
     */
    public void cleanAll() {
        cleanControl(null, null);
        cleanError(null, null);
        cleanDevice(null, null);
        Log.v(tag, "dbm341  deleteAll");
    }
}
