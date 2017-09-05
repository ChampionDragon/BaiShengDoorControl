package com.bs.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoUtil {
	/**
	 * 压缩图片
	 */
	public static Bitmap SizeImage(Bitmap image) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, os);
		while (os.toByteArray().length / 1024 > 1111) {
			os.reset();
			image.compress(CompressFormat.JPEG, 50, os);
		}
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		BitmapFactory.Options options = new Options();
		options.inJustDecodeBounds = true;
		options.inJustDecodeBounds = false;
		int h = options.outHeight;
		int w = options.outWidth;
		int hh = 666;
		int ww = 666;
		int b = 1;
		if (h > w && h > hh) {
			b = h / hh;
		} else if (w > h && w > ww) {
			b = w / ww;
		}
		options.inPreferredConfig = Config.RGB_565;
		options.inSampleSize = b;
		is = new ByteArrayInputStream(os.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
		return bitmap;
	}

	/**
	 * 存储照片并返回文件
	 */
	public static File SavePhoto(Bitmap bitmap, String path, String name) {
		String localpath = null;
		File photoFile = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File fileDir = new File(path);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			photoFile = new File(fileDir, name + ".png");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(photoFile);
				if (bitmap != null) {
					if (bitmap.compress(CompressFormat.PNG, 100, fos)) {
						localpath = photoFile.getAbsolutePath();
						try {
							fos.flush();
						} catch (IOException e) {
							Logs.d("photoutil_72     "+e.getMessage());
							photoFile.delete();
							localpath = null;
							e.printStackTrace();
						}
					}
				}
			} catch (FileNotFoundException e) {
				Logs.d("photoutil_80     "+e.getMessage());
				photoFile.delete();
				localpath = null;
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					fos = null;
				}
			}

		}

		return photoFile;
	}

}
