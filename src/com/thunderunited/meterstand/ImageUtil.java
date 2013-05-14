package com.thunderunited.meterstand;

import java.io.IOException;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

public class ImageUtil {
	
	private static final String TESSEDIT_CHAR_WHITELIST = "tessedit_char_whitelist";
	

	private static final String TAG = ImageUtil.class.getName();
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory() + "/com.example.test_ocr1/";
	protected static final String LANG = "nld";
	

	protected ImageUtil() {
		// Prevent instantiation
	}

	public static void autoRotate(Bitmap bitmap, String path) throws IOException {
		// _path = path to the image to be OCRed
		ExifInterface exif = new ExifInterface(path);
		int exifOrientation = exif
				.getAttributeInt(ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);

		int rotate = 0;

		switch (exifOrientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			rotate = 90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			rotate = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			rotate = 270;
			break;
		}

		if (rotate != 0) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();

			// Setting pre rotate
			Matrix mtx = new Matrix();
			mtx.preRotate(rotate);

			// Rotating Bitmap & convert to ARGB_8888, required by tess
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		}
	}
	
	
	public static final String recognizeDigits(Bitmap bitmap) {
		TessBaseAPI baseApi = new TessBaseAPI();
		if (!baseApi.setVariable(TESSEDIT_CHAR_WHITELIST, "0123456789")) {
			Log.e(TAG, "The tessedit_char_whitelist variable cannot be set with value: '0123456789'");
		}
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, LANG);
		baseApi.setImage(bitmap);
		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();

		return recognizedText;
	}

}
