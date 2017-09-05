package com.bs.Ezviz;

import com.bs.Ezviz.PullToRefresh.CloudPartInfoFileEx;
import com.videogo.openapi.bean.resp.CloudPartInfoFile;

import java.util.List;

/**
 * 逻辑或功能性重大变更记录
 * Created by Administrator on 2017/6/23.
 */

public interface QueryPlayBackListTaskCallback {

    /**
     * <p>
     * 云+设备本地 无录像数据
     * </p>
     *
     * @author hanlifeng 2014-6-9 上午10:44:11
     */
    void queryHasNoData();

    /**
     * <p>
     * 无云 + 有设备本地
     * </p>
     *
     * @author hanlifeng 2014-6-9 上午10:48:58
     */
    void queryOnlyHasLocalFile();

    /**
     * <p>
     * 按月查只有本地，具体查询本地确无数据
     * </p>
     *
     * @author hanlifeng 2014-6-17 上午9:21:09
     */
    void queryOnlyLocalNoData();

    /**
     * <p>
     * 查本地异常
     * </p>
     *
     * @author hanlifeng 2014-6-17 上午9:22:05
     */
    void queryLocalException();

    /**
     * <p>
     * 云 数据获取成功
     * </p>
     *
     * @param cloudPartInfoFileEx
     * @param queryMLocalStatus 设备本地是否有录像
     * @param cloudPartInfoFile    云录像数据
     * @author hanlifeng 2014-6-9 上午10:44:34
     */
    void queryCloudSucess(List<CloudPartInfoFileEx> cloudPartInfoFileEx, int queryMLocalStatus, List<CloudPartInfoFile> cloudPartInfoFile);

    /**
     * <p>
     * 本地 数据获取成功
     * </p>
     * 本地数据
     *
     * @author hanlifeng 2014-6-9 上午10:52:12
     */
    void queryLocalSucess(List<CloudPartInfoFileEx> cloudPartInfoFileEx, int position, List<CloudPartInfoFile> cloudPartInfoFile);

    /**
     * <p>
     * 本地无数据
     * </p>
     *
     * @author hanlifeng 2014-6-19 下午2:03:38
     */
    void queryLocalNoData();

    /**
     * <p>
     * 异常处理
     * </p>
     *
     * @author hanlifeng 2014-6-9 上午10:54:05
     */
    void queryException();

    /**
     * <p>
     * 查询录像数据结束
     * </p>
     *
     * @param type
     * @param queryMode      查询结果
     * @param queryErrorCode 查询errorCode
     * @param detail
     * @author hanlieng 2014-8-6 下午3:02:14
     */
    void queryTaskOver(int type, int queryMode, int queryErrorCode, String detail);

}
