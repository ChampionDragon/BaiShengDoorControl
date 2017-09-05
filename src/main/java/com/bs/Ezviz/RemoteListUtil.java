package com.bs.Ezviz;

import android.text.TextUtils;

import com.bs.R;
import com.videogo.device.DeviceInfoEx;
import com.videogo.util.LocalInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 回放列表相关的工具函数
 * Created by Administrator on 2017/6/23.
 */

public class RemoteListUtil {
    private static LocalInfo mLocalInfo = LocalInfo.getInstance();
    private static final String PM = mLocalInfo.getContext().getResources().getString(R.string.pm);
    private static final String AM = mLocalInfo.getContext().getResources().getString(R.string.am);
    private static final String MON = mLocalInfo.getContext().getResources().getString(R.string.month);
    private static final String DAY = mLocalInfo.getContext().getResources().getString(R.string.day);

    /**
     * <p>
     * 将开始时间转化为列表显示模式 ，比如 "早上09:50"
     * </p>
     *
     * @author hanlifeng 2014-6-16 下午4:00:24
     * @return
     */
    public static String convToUIBeginTime(Calendar beginCalender) {
        int i = beginCalender.get(Calendar.HOUR_OF_DAY);
        int m = beginCalender.get(Calendar.MINUTE);

        String uiStr = "";
        if (i > 12) {
            uiStr = PM + (i - 12) + ":" + (m < 10 ? "0" + m : "" + m);
        } else if (i == 12) {
            uiStr = AM + "12:" + (m < 10 ? "0" + m : "" + m);
        } else {
            uiStr = AM + (i < 10 ? "0" + i : "" + i) + ":" + (m < 10 ? "0" + m : "" + m);
        }
        return uiStr;
    }

    /**
     * <p>
     * 将录像时长转化为列表显示模式，比如"01:01:30"
     * </p>
     *
     * @author hanlifeng 2014-6-16 下午4:01:04
     * @param diffSeconds
     * @return
     */
    public static String convToUIDuration(int diffSeconds) {
        int min = diffSeconds / 60;
        String minStr = "";
        int sec = diffSeconds % 60;
        String secStr = "";
        String hStr = "";

        if (min >= 59) {
            int hour = min / 60;
            int temp = min % 60;
            if (hour < 10) {
                if (hour > 0) {
                    hStr = "0" + hour;
                } else {
                    hStr = "00";
                }
            } else {
                hStr = "" + hour;
            }
            if (temp < 10) {
                if (temp > 0) {
                    minStr = "0" + temp;
                } else {
                    minStr = "00";
                }
            } else {
                minStr = "" + temp;
            }
            if (sec < 10) {
                if (sec > 0) {
                    secStr = "0" + sec;
                } else {
                    secStr = "00";
                }
            } else {
                secStr = "" + sec;
            }
            return hStr + ":" + minStr + ":" + secStr;
        } else {
            hStr = "00";
            if (min < 10) {
                if (min > 0) {
                    minStr = "0" + min;
                } else {
                    minStr = "00";
                }
            } else {
                minStr = "" + min;
            }
            if (sec < 10) {
                if (sec > 0) {
                    secStr = "0" + sec;
                } else {
                    secStr = "00";
                }
            } else {
                secStr = "" + sec;
            }
            return hStr + ":" + minStr + ":" + secStr;
        }
    }

    /**
     * <p>
     * 将时间转化为 cas播放时间格式
     * </p>
     *
     * @author hanlifeng 2014-6-16 下午4:01:52
     * @param calenderTime
     * @return
     */
    public static String converTime(Calendar calenderTime) {
        // libCASClient 内部统一进行时间转换
        // 20130605T001020Z->2013-06-25T00:10:20
        // 调用libCASClient接口时必须使用时间格式：20130605T001020Z
        // 请和各位同步更新
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd,HHmmss.");
        String now = sdf.format(calenderTime.getTime());
        String aString = now.replace(',', 'T');
        String bString = aString.replace('.', 'Z');
        return bString;
    }

    /**
     * <p>
     * 将时间转化为月和日格式，比如 6月17号
     * </p>
     *
     * @author hanlifeng 2014-6-17 下午1:34:23
     * @param queryDate
     * @return
     */
    public static String converToMonthAndDay(Date queryDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(queryDate);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        return month + MON + day + DAY;
    }

    /**
     * <p>
     * 获取云存储封面url
     * 云存储加密封面
     * </p>
     *
     * @author hanlieng 2014-8-2 下午1:42:44
     * @param srcPicUrl
     * @return
     */
    public static String getCloudListItemPicUrl(String srcPicUrl, String keyCheckSum, String passwd) {
        if (!TextUtils.isEmpty(keyCheckSum)) {
            return srcPicUrl + "&x=200" + "&decodekey=" + passwd;
        } else {
            return srcPicUrl + "&x=200";
        }
    }

    /**
     * <p>
     * 获取回放列表封面密码
     * </p>
     *
     * @author hanlieng 2014-8-12 上午11:34:12
     * @param deviceInfoEx
     * @param cloudKeyChecksum
     * @return
     */
    public static String getEncryptRemoteListPicPasswd(DeviceInfoEx deviceInfoEx, String cloudKeyChecksum) {

        if (deviceInfoEx == null || TextUtils.isEmpty(cloudKeyChecksum)) {
            return null;
        }
        return null;
    }
}