package com.bs.constant;

import android.os.Environment;

import com.bs.util.SystemUtil;

import java.io.File;

/**
 * 常量类
 */
public class Constant {
    /*Intent的key*/
    public final static String deviceId = "deviceId";


    /*更新APK的网址*/
    public final static String apkUpdate="http://releases.b0.upaiyun.com/hoolay.apk";



    /* 网络 */
    public final static String urlBaiSheng = "http://www.bisensa.com/";
    public final static String urlBaiShengtianmao =
//            "http://www.cnblogs.com/olartan/p/5713013.html";
            "https://baishengjj.tmall.com/";
    public final static String urlBaiShengtaobao =
//        "http://blog.csdn.net/dodod2012/article/details/51425369";
"https://shop128145782.taobao.com/category-1128776305.htm?spm=a1z10.1-c.w5002-11821265594.17.1d548095HK59uM&search=y&catName=%BF%AA%C3%C5%B5%E7%BB%FA";


    /* 设备连接 */
    public final static String serverIP =
            "120.203.0.218";
    //      "116.62.180.134";
    public final static int serverPort
          = 30066;
//        = 30000;

    public final static int ipPort = 30099;
    public final static String ipIP = "224.1.1.1";
    public final static String oneIp = "192.168.0.132";

    public final static int tcpServerPort = 8080;


    /* 设备命令 */
    public final static String cmdOpen = "gate=1";
    public final static String cmdClose = "gate=0";
    public final static String cmdStop = "gate=2";
    public final static String idONE = "AE0067BD11";
    public final static String idTWO = "BE0067BE2B";
    public final static String idTHREE = "BE0067BE5E";
    public final static String idFOUR = "BE0067BE49";


    /*向设备发送指令   gate=?  1：OPEN ,CLOSE:0，STOP:2   */
    public final static String cmdSet = "cmd:103,type:1,termID:AE0067BD11,seq:298,flag:2,param:gate=1";
    /*查询设备状态*/
    public final static String cmdState = "cmd:103,type:1,termID:AE0067BD11,seq:298,flag:2,param:XX";
    /*查看设备是否在线*/
//    public final static String isOnline = "http://116.62.180.134/wifi/getonline_bydid.php";
    public final static String isOnline = "http://120.203.0.218:30000/wifi/getonline_bydid.php";

    /* 登录模块 */
    public final static String urlLogin = //登录
            "http://www.bsznyun.com/wifi/ios/get_user_ios.php";
//            "http://www.bsznyun.com/wifi/get_user_ios.php";
    public final static String key = "5tZwmk3TIaJ4ELVHzN";//加密钥匙
    public final static String mobile = "http://116.62.180.134/simplewind/Core/Library/Vendor/alidayu/sendmsg-ios.php";//短信
    public final static String register = "http://www.bsznyun.com/wifi/register_user_mobi.php";//注册
    public final static String changePwd = "http://www.bsznyun.com/wifi/setuser.php";//修改密码

    /*信息存取*/
    public final static String urlGetRecord = "http://120.203.0.218:30000/wifi/get_records_ios.php";//开关门日志
    public final static String urlGetProblem = "http://www.bsznyun.com/wifi/get_problems_ios.php";//错误日志
    public final static String checkId = "http://www.bsznyun.com/wifi/check_deviceid.php";//检查序列号是否存在
    public final static String data = "http://www.bsznyun.com/wifi/dobackup_ios.php";//数据

    /* 数据库 */
    public final static String dbDiveceBsmk = "BSMK";
    public final static int dbVersion = 1;

    /* 时间格式 */
    public final static String cformatDay = "yyyy年MM月dd日";
    public final static String cformatD = "M月d日";
    public final static String cformatsecond = "yyyy年MM月dd日HH时mm分ss秒";
    public final static String formatminute = "HH:mm";
    public final static String formatsecond = "yyyy-MM-dd HH:mm:ss";

    /* 文件夹 */
    // 整个项目的目录
    public final static File fileDir = new File(
            Environment.getExternalStorageDirectory(), "百胜门控");
    public final static String fileRoot = SystemUtil.AppName();
    public final static String filehead = "头像";
    public final static String filehead_temp = "头像缓存";
    public final static String fileScreenShot = "监控截图";
    public final static String fileWebViewCache = "网页缓存";


    // 时间差
    public final static int BroadcastReceiverTime = 2777;
    public final static int bgTimeout = 32777;


    /*获取导航图片*/
    public final static String Image_1 = "http://120.203.0.218:30000/img/1.png";
    public final static String Image_2 = "http://120.203.0.218:30000/img/2.png";
    public final static String Image_3 = "http://120.203.0.218:30000/img/3.png";


    /* 视频 */
    public final static String viedoUrl =
//     "http://yxfile.idealsee.com/9f6f64aca98f90b91d260555d3b41b97_mp4.mp4";
            "rtmp://live.hkstv.hk.lxdns.com/live/hks";
//"http://219.238.2.164/apk.r1.market.hiapk.com/data/upload/apkres/2016/11_10/11/com.ss.android.article.news_115902.apk?wsiphost=local";


    /*某个声音所代表的值*/
    public final static int CLOSED = 1;//关门完成
    public final static int CLOSING = 2;//正在关门
    public final static int OPENED = 3;//开门完成
    public final static int OPENING = 4;//正在开门
    public final static int OPENING2 = 5;//正在开门,先按停止才能开门
    public final static int STOP = 6;//停止
    public final static int DOERROR = 7;//操作失败
    public final static int BEEP = 8;//铃声
    public final static int BINDCAMERA = 9;//请绑定摄像头到门控设备
    public final static int ID = 10;//请先绑定门控设备ID
    public final static int LOGIN = 11;//请登录
    public final static int LOGINED = 12;//登录成功
    public final static int LOGINFAIL = 13;//登录失败
    public final static int OFFLINE = 14;//设备离线
    public final static int PAIZHAO = 15;//拍照的声音
    public final static int TIMEOUT = 16;//接收超时
    public final static int WIFI = 17;//请添加设备按一键添加WIFI


}
