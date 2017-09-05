package com.bs.socket;

/**
 * 通讯协议
 * 例如：手机查询模块设置指令
 手机发送查询命令： A5 00  0L  01  00   L1 L2 L3…LX  0H  0L  AA
 模块接收后应答：   A5 00  0L  01  01   L1 L2 L3…LX  0H  0L  AA
 2.	控制设备动作：
 ③	 LIGHT:?  设备状态查询
 ②  LIGHT:1  打开设备
 ③  LIGHT:0  关闭设备
 例如：控制设备动作指令
 手机发送打开设备命令： A5  07  00  B4  00  LIGHT:1  0H  0L  AA
 设备接收后执行并应答： A5  07  00  B4  01  LIGHT:1  0H  0L  AA
 */

public class Protocol {
	private static final byte byStart = (byte) 0xA5;
	private static final byte byEnd = (byte) 0xAA;

	// 命令字
	private static final byte[] byCommands = { (byte) 0xB1, (byte) 0xB2,
			(byte) 0xB3, (byte) 0xB4 };

	// 应答码
	private static final byte byReqCode = 0;
	private static final byte byResCodeSuccess = 1;
	private static final byte byResCodeError = 2;

	public Protocol() {
		super();
	}

	// 设备控制请求数据包
	public static byte[] getDeviceReqPacket(String strReqData) {
		int nDataLen = strReqData.length();
		if (nDataLen <= 0 || nDataLen > 200) {
			return null;
		}

		int nPacketLen = nDataLen + 8;
		byte[] packet = new byte[nPacketLen];
		packet[0] = byStart;
		packet[1] = 0x00;// 设备的地址标识符（预留 0x00）
		packet[2] = (byte) (nPacketLen - 4);// 数据长度
		packet[3] = byCommands[3];
		packet[4] = byReqCode;
		// packet = strReqData.getBytes();
		System.arraycopy(strReqData.getBytes(), 0, packet, 5, nDataLen);
		// short checkCode = getCheckCode(strReqData.getBytes(), nDataLen);
		byte[] byCheck = new byte[nPacketLen - 4];
		System.arraycopy(packet, 1, byCheck, 0, nPacketLen - 4);
		short checkCode = getCheckCode(byCheck, nPacketLen - 4);
		packet[nPacketLen - 3] = (byte) (checkCode / 256);
		packet[nPacketLen - 2] = (byte) (checkCode & 0xFF);
		packet[nPacketLen - 1] = byEnd;
		return packet;
	}

	// 设备响应数据包，作调试用
	public static byte[] getDeviceResPacket(String strResData) {
		int nDataLen = strResData.length();
		if (nDataLen <= 0 || nDataLen > 200) {
			return null;
		}

		int nPacketLen = nDataLen + 8;
		byte[] packet = new byte[nPacketLen];

		packet[0] = byStart;
		packet[1] = 0x00;
		packet[2] = (byte) (nPacketLen - 4);
		packet[3] = byCommands[3];
		packet[4] = byResCodeSuccess;

		System.arraycopy(strResData.getBytes(), 0, packet, 5, nDataLen);

		byte[] byCheck = new byte[nPacketLen - 4];
		System.arraycopy(packet, 1, byCheck, 0, nPacketLen - 4);
		short checkCode = getCheckCode(byCheck, nPacketLen - 4);
		packet[nPacketLen - 3] = (byte) (checkCode / 256);
		packet[nPacketLen - 2] = (byte) (checkCode & 0xFF);
		packet[nPacketLen - 1] = byEnd;

		return packet;
	}

	// 解析出设备响应中的数据部分
	public static byte[] getDeviceResData(byte[] packet, int packetlen) {
		if (packetlen <= 8) {
			return null;
		}
		if (packet[0] != byStart && packet[packetlen - 1] != byEnd) {
			return null;
		}
		if (packet[4] != byResCodeSuccess && packet[4] != byResCodeError) {
			return null;
		}
		byte[] byCheck = new byte[packetlen - 4];
		System.arraycopy(packet, 1, byCheck, 0, packetlen - 4);
		getCheckCode(byCheck, packetlen - 4);

		// 检查校验码
		/*
		 * if (packetCheckCode != checkCode) { String str = "packetCheckCode = "
		 * + new String(Integer.toString(packetCheckCode)) +
		 * "     || checkCode = " + new String(Integer.toString(checkCode));
		 * return str.getBytes(); }
		 */
		int datalen = packet[2] - 4;
		byte[] data = new byte[datalen];
		System.arraycopy(packet, 5, data, 0, datalen);

		return data;
	}

	private static short getCheckCode(byte[] data, int len) {
		short code = 0;
		for (int i = 0; i < len; i++) {
			code += (short) (data[i] & 0xFF);
		}

		return code;
	}
}
