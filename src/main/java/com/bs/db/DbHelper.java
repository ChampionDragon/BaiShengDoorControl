package com.bs.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库帮助类
 *
 * @author lcb
 * @date 2017-5-8
 */
public class DbHelper extends SQLiteOpenHelper {
    public final static String TABLE_DEVICE_CONTROL = "deviceControl";
    public final static String DEVICE_CONTROL = "someThing";
    public final static String DEVICE_CONTROL_ID = "id";
    public final static String DEVICE_CONTROL_NAME = "name";
    public final static String DEVICE_CONTROL_TIME = "createTime";

    public final static String TABLE_DEVICE_ERROR = "deviceerror";
    public final static String DEVICE_ERROR = "error";
    public final static String DEVICE_ERROR_ID = "id";
    public final static String DEVICE_ERROR_TIME = "time";

    public final static String TABLE_DEVICE = "device";
    public final static String DEVICE_ID = "id";
    public final static String DEVICE_NAME = "name";
    public final static String DEVICE_ADDRESS = "address";
    public final static String DEVICE_TIME = "time";
    public final static String DEVICE_NUMBER = "number";
    public final static String DEVICE_FLAGONE = "flagone";
    public final static String DEVICE_FLAGTWO = "flagtwo";

    public final static String ID = "_id";
    public static DbHelper mInstance;
    private SQLiteDatabase db;
    private String TAG = "lcb";

    public DbHelper(Context context, String dbName, int dbVersion) {
        super(context, dbName, null, dbVersion);
    }

    public static DbHelper getInstance(Context context, String dbName,
                                       int dbVersion) {
        if (mInstance == null) {
            mInstance = new DbHelper(context, dbName, dbVersion);
        }
        return mInstance;
    }

    /**
     * 当数据库被首次创建时才会执行
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_DEVICE_CONTROL
                + "(_id integer PRIMARY KEY AUTOINCREMENT,"
                + DEVICE_CONTROL_ID + " integer," + DEVICE_CONTROL
                + " varchar," + DEVICE_CONTROL_NAME + " varchar,"
                + DEVICE_CONTROL_TIME + " long)";
        db.execSQL(sql);
        Log.i(TAG, "Table  " + TABLE_DEVICE_CONTROL + "  success");

        sql = "create table " + TABLE_DEVICE_ERROR
                + "(_id integer PRIMARY KEY AUTOINCREMENT," + DEVICE_ERROR_ID
                + " integer," + DEVICE_ERROR + " varchar," + DEVICE_ERROR_TIME
                + " long)";
        db.execSQL(sql);
        Log.d(TAG, "Table  " + TABLE_DEVICE_ERROR + "  success");

        sql = "create table " + TABLE_DEVICE
                + "(_id integer PRIMARY KEY AUTOINCREMENT," + DEVICE_NUMBER + " integer,"
                + DEVICE_ERROR + " varchar," + DEVICE_ID + " varchar,"
                + DEVICE_NAME + " varchar," + DEVICE_TIME + " varchar,"
                + DEVICE_FLAGONE + " varchar," + DEVICE_FLAGTWO + " varchar,"
                + DEVICE_ADDRESS + " varchar)";
        db.execSQL(sql);
        Log.d(TAG, "Table  " + TABLE_DEVICE + "  success");
    }

    /**
     * 当打开数据库时传入的版本号与当前的版本号不同时才会调用该方法
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == newVersion) {
            return;
        }// 版本变了重新建立数据库
        db.execSQL("drop table if exists " + TABLE_DEVICE_CONTROL);
        db.execSQL("drop table if exists " + TABLE_DEVICE_ERROR);
        db.execSQL("drop table if exists " + TABLE_DEVICE);
        onCreate(db);
    }


    /**
     * 每次调用数据库都会执行
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
//        db.execSQL("drop table if exists " + TABLE_DEVICE);
//        String sql = "create table " + TABLE_DEVICE
//                + "(_id integer PRIMARY KEY AUTOINCREMENT," + DEVICE_NUMBER + " integer,"
//                + DEVICE_ERROR + " varchar," + DEVICE_ID + " varchar,"
//                + DEVICE_NAME + " varchar," + DEVICE_TIME + " varchar,"
//                + DEVICE_FLAGONE + " varchar," + DEVICE_FLAGTWO + " varchar,"
//                + DEVICE_ADDRESS + " varchar)";
//        db.execSQL(sql);
//        Log.d(TAG, "Table  " + TABLE_DEVICE + "  success");



/*如果想添加表又不想删除掉之前的表，就这个方法里添加建表语句。
且只能添加一次,添加后立刻在这删掉填表语句不然会报错
Caused by: android.database.sqlite.SQLiteException: table device already exists*/
    }

    /**
     * 插入数据
     */
    public long insert(String tableName, ContentValues contentValues) {
        if (db == null) {
            db = getWritableDatabase();
        }
        return db.insert(tableName, null, contentValues);
    }

    /**
     * 删除数据
     */
    public int delete(String table, String whereClause, String[] whereArgs) {
        if (db == null) {
            db = getWritableDatabase();
        }
        return db.delete(table, whereClause, whereArgs);
    }

    /**
     * 更新数据
     */
    public int update(String table, ContentValues contentValues,
                      String whereClause, String[] whereArgs) {
        if (db == null) {
            db = getWritableDatabase();
        }
        return db.update(table, contentValues, whereClause, whereArgs);
    }

    /**
     * 通过四个参数数据查询
     *
     * @param whereClause where子句，除去where关键字剩下的部分，其中可带？占位符。如没有子句，则为null。
     * @param whereArgs   用于替代whereClause参数中？占位符的参数。如不需传入参数，则为null。
     */
    public Cursor query(String table, String[] columns, String whereClause,
                        String[] whereArgs) {
        if (db == null) {
            db = getWritableDatabase();
        }
        return db.query(table, columns, whereClause, whereArgs, null, null,
                null);
    }

    /**
     * 通过sql语句查询数据
     */
    public Cursor rawQuery(String sql, String[] whereArgs) {
        if (db == null) {
            db = getWritableDatabase();
        }
        return db.rawQuery(sql, whereArgs);
    }

    /**
     * 向数据库写语句
     */
    public void execSql(String sql) {
        if (db == null) {
            db = getWritableDatabase();
        }
        db.execSQL(sql);
    }

    /**
     * 关闭数据库
     */
    public void close() {
        if (db == null) {
            db = getWritableDatabase();
        }
        db.close();
        db = null;
    }

}
