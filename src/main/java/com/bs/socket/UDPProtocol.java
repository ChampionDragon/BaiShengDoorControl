package com.bs.socket;

import java.util.Arrays;

import android.util.Log;

/*引导头：消息包引导头是固定的0xAA，占1 个字节。
 包长度：表示除去引导头数据包的长度，占1 个字节。其取值范围为[16,255]。
 设备类型：公司的各种类型产品。
 道闸ID: 道闸的ID，占12 个字节.
 消息命令:消息包的类型，占1 个字节。
 消息长度:消息体的长度。占1 个字节。
 消息体:消息的具体描述。占N 个字节。
 校验码： = 包长度+消息类别+设备类型+设备ID+消息长度+消息体，然后取反+1。消息命令: 消息包的类型，占1 个字节*/

/**
 * WIFI控制器数据传输协议
 * 
 * @author lcb
 * @date 2017-5-19
 */
public class UDPProtocol {
	// 引导头
	private static final byte byStart = (byte) 0xAA;
	// 设备类型
	// GZ_DAOZA(0x01),GZ_DAOZA2,BS_PKM,BS_PKM_INDOOR, BS_SSM,BS_YKQ,OTHER
	private static final byte[] Devices = { (byte) 0x01, (byte) 0x02,
			(byte) 0x03, (byte) 0x04, 0x05, 0x06, 0x07 };

	public static byte[] getDeviceReqPacket(String str) {
		int dataLen = str.length();// 消息长度
		int packetLen = dataLen + 18;// 整个数据包长度
		byte[] packet = new byte[packetLen];// 完整字节数组
		byte[] id = new byte[12];// 道闸ID,默认填充零

		packet[0] = byStart;// 引导头
		packet[1] = (byte) (packetLen - 1);// 包长度
		packet[2] = Devices[0];// 设备类型
		System.arraycopy(id, 0, packet, 3, 12);// 添加道闸ID
		packet[15] = 103;// 消息命令
		packet[16] = (byte) dataLen;// 消息长度
		System.arraycopy(str.getBytes(), 0, packet, 17, dataLen);// 消息体
		byte[] byCheck = new byte[packetLen - 2];
		System.arraycopy(packet, 1, byCheck, 0, packetLen - 2);
		byte checkCode = getCheckCode(byCheck, packetLen - 2);
		packet[packetLen - 1] = checkCode;// 校验
		Log.d("lcb", "udpp45" + Arrays.toString(packet));
		return packet;
	}

	/**
	 * 得到校验码
	 */
	private static byte getCheckCode(byte[] data, int len) {
		short code = 0;
		for (int i = 0; i < len; i++) {
			code += data[i] & 0xFF;
		}
		code = (short) ~code;
		code = (short) (code + 1);
		return (byte) code;
	}

}
