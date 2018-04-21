# BaiShengDoorControl
通过app控制门控设备

大致功能如下：
包括了登录模块，对设备本地的增删改查，通过udp和tcp与服务器连接在发送控制命令给服务器。服务器通过wiFI模块在传输通讯协议个硬件从而控制硬件。
这个项目主要是控制我公司的多款门控设备（平开门，道闸门，伸缩门），物联网应用。购买我公司的设备的用户首先注册我公司的APP,进入主界面
就可以添加设备（二维码扫描、手动输入ID号），添加后我公司会判断这个设备是否在我公司的数据库能否查询，查询不到提示用户无法添加，
查询到就可以添加到本APP的数据库内，也是该应用的主界面。上面会显示设备是否在线，如果在线点击它就可以进入控制界面，然后就可以对设备进行
开、关、停的操作。工作原理：由于这个项目是和硬件打交道，所以首先由公司的电子工程师编写接收命令的文档如：
手机发送查询命令： A5 00  0L  01  00   L1 L2 L3…LX  0H  0L  AA
设备接收后执行并应答： A5  07  00  B4  01  LIGHT:1  0H  0L  AA
我们再根据硬件协议编写字节包如这样：packet[1] = 0x00;// 设备的地址标识符（预留 0x00）。最后通过tcp、scoket的方式向设备
发送信息（当然这还要一些网络协议）做相应的操作。这个app还可以查询操作的历史记录、设备故障原因................

##  ++++++++++程序运行的效果图+++++++++++

##  主界面

![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/1.png)

##  登录界面

![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/2.png)

##  控制界面

![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/3.png)


##  编辑资料

![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/5.png)
![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/4.png)

##  配置WIFI

![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/6.png)

##  添加设备

![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/7.png)

##  二维码扫描

![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/8.png)

##  设备设置

![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/9.png)

##  侧滑的界面

![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/10.png)

##  用户管理

![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/11.png)

##  使用帮助

![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/12.png)
![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/13.png)

##  设备管理

![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/14.png)

##  查找设备

![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/15.png)

##  关于我们

![程序的演示图片](https://github.com/ChampionDragon/BaiShengDoorControl/blob/master/UI/16.png)




























