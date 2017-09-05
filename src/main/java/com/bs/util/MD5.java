package com.bs.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * MD5加密工具
 * 
 * @author lcb
 * @date 2017-6-5
 */
public class MD5 {
	public static String MD5Low32(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			try {
				md.update(str.getBytes("UTF8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer(200);
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset] & 0xff;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			Logs.e(e.toString());
			return null;
		}
	}

	public static String MD5Up32(String str) {
		return MD5Low32(str).toUpperCase(Locale.getDefault());
	}

	public static String MD5Up16(String str) {
		return MD5Up32(str).substring(8, 24);
	}

	public static String Md5Low16(String str) {
		return MD5Low32(str).substring(8, 24);
	}
}
