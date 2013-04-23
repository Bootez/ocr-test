package com.example.test_ocr1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

public class MainActivity extends Activity {

	protected Button _button;
	protected ImageView _image;
	protected TextView _field;
	protected String _path;
	protected boolean _taken;
	protected String lang = "nld";

	protected static final String PHOTO_TAKEN = "photo_taken";

	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory() + "/com.example.test_ocr1/";
	private static final String TAG = "--";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		copyToExtern();

		setContentView(R.layout.activity_main);

		_image = (ImageView) findViewById(R.id.image);
		_field = (TextView) findViewById(R.id.field);
		_button = (Button) findViewById(R.id.button);
		_button.setOnClickListener(new ButtonClickHandler());

		_path = DATA_PATH + "/make_machine_example.jpg";
	}

	public class ButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			Log.i("MakeMachine", "ButtonClickHandler.onClick()");
			startCameraActivity();
		}
	}

	protected void startCameraActivity() {
		Log.i("MakeMachine", "startCameraActivity()");
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);

		Intent intent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("MakeMachine", "resultCode: " + resultCode);
		switch (resultCode) {
		case 0:
			Log.i("MakeMachine", "User cancelled");
			break;

		case -1:
			onPhotoTaken();
			break;
		}
	}

	protected void onPhotoTaken() {
		Log.i("MakeMachine", "onPhotoTaken");

		_taken = true;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

		_image.setImageBitmap(bitmap);

		_field.setVisibility(View.GONE);

		try {
			rotateImage(bitmap);
			String text = recognize(bitmap);
			text = text.replaceAll("\\D+", "");

			Toast.makeText(this, text, Toast.LENGTH_LONG).show();

		} catch (IOException e) {
			Toast.makeText(this, "Rotate is mislukt.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i("MakeMachine", "onRestoreInstanceState()");
		if (savedInstanceState.getBoolean(MainActivity.PHOTO_TAKEN)) {
			onPhotoTaken();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(MainActivity.PHOTO_TAKEN, _taken);
	}

	protected void rotateImage(Bitmap bitmap) throws IOException {
		// _path = path to the image to be OCRed
		ExifInterface exif = new ExifInterface(_path);
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

	protected String recognize(Bitmap bitmap) {
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setVariable("classify_bln_numeric_mode", "1");
		baseApi.setVariable("tessedit_char_whitelist", "0123456789");
		baseApi.init(DATA_PATH, lang);
		baseApi.setImage(bitmap);
		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();

		return recognizedText;
	}

	protected void copyToExtern() {

		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path
							+ " on sdcard failed");
					return;
				}

				Log.v(TAG, "Created directory " + path + " on sdcard");
			}

		}

		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata"))
				.exists()) {
			try {
				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang
						+ ".traineddata");
				OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/"
						+ lang + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();

				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG,
						"Was unable to copy " + lang + " traineddata "
								+ e.toString());
			}
		}
	}

}
