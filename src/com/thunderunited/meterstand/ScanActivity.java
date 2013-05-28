package com.thunderunited.meterstand;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ScanActivity extends Activity {

	private static final String DATA_PATH = Environment
			.getExternalStorageDirectory()
			+ "/"
			+ ScanActivity.class.getPackage().getName() + "/";

	private static final String TAG = ScanActivity.class.getName();

	private Button _button;
	private String _path = DATA_PATH + "/ocr-image.jpg";
	private boolean _taken;
	private static final String LANG = "nld";
	private static final String PHOTO_TAKEN = "photo_taken";

	private static final int PHOTO_CAPTURE_INTENT = 1;
	private static final int PHOTO_CROP_INTENT = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		copyToExtern();

		setContentView(R.layout.activity_scannen);
		
		_button = (Button) findViewById(R.id.button);
		_button.setOnClickListener(new ButtonClickHandler());
	}

	public class ButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			Log.i(TAG, "ButtonClickHandler.onClick()");
			startCameraActivity();
		}
	}

	protected void startCameraActivity() {
		Log.i(TAG, "startCameraActivity()");
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, PHOTO_CAPTURE_INTENT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "resultCode: " + resultCode);

		if (resultCode == RESULT_CANCELED) {

			Log.w(TAG, "User cancelled");

		} else if (resultCode == RESULT_OK) {

			switch (requestCode) {
			case PHOTO_CAPTURE_INTENT:
				onPhotoTaken();
				break;

			case PHOTO_CROP_INTENT:
				onPhotoCroped();
				break;
			}
		}
	}

	private void onPhotoCroped() {
		Log.i(TAG, "onPhotoCroped");

		_taken = true;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

		try {
			ImageUtil.autoRotate(bitmap, _path);
		} catch (IOException e) {
			Toast.makeText(this, "Roteren is mislukt.", Toast.LENGTH_SHORT)
					.show();
		}
		
		final String text = ImageUtil.recognizeDigits(bitmap);

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		String title = getString(R.string.recognition_done_message);
		String closeButtonStr = getString(R.string.go_to_control);
		alertDialog.setTitle(title);
		alertDialog.setMessage(text);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, closeButtonStr, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				
				Intent intent = new Intent(getBaseContext(), Handmatig.class);
				intent.putExtra(Handmatig.METER_LEVEL_KEY, text);
				intent.putExtra(Handmatig.IMAGE_PATH_KEY, _path);
				startActivity(intent);
				
				ScanActivity.this.finish();
			}
		});
		alertDialog.show();

	}
	
	protected void onPhotoTaken() {
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		intent.setDataAndType(outputFileUri, "image/*");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, PHOTO_CROP_INTENT);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "onRestoreInstanceState()");
		if (savedInstanceState.getBoolean(ScanActivity.PHOTO_TAKEN)) {
			onPhotoTaken();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(ScanActivity.PHOTO_TAKEN, _taken);
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

		if (!(new File(DATA_PATH + "tessdata/" + LANG + ".traineddata"))
				.exists()) {
			try {
				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + LANG
						+ ".traineddata");
				OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/"
						+ LANG + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();

				Log.v(TAG, "Copied " + LANG + " traineddata");
			} catch (IOException e) {
				Log.e(TAG,
						"Was unable to copy " + LANG + " traineddata "
								+ e.toString());
			}
		}
	}
}
