package com.thunderunited.meterstand;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.thunderunited.meterstand.R;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ScanActivity extends Activity {

	private static final String TAG = ScanActivity.class.getName();

	private Button _button;
	private ImageView _image;
	private TextView _field;
	private String _path;
	private boolean _taken;
	private static final String LANG = "nld";
	private static final String PHOTO_TAKEN = "photo_taken";
	private static final String DATA_PATH = Environment
			.getExternalStorageDirectory()
			+ "/"
			+ ScanActivity.class.getPackage().getName() + "/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		copyToExtern();

		setContentView(R.layout.activity_scannen);

		_image = (ImageView) findViewById(R.id.image);
		_field = (TextView) findViewById(R.id.field);
		_button = (Button) findViewById(R.id.button);
		_button.setOnClickListener(new ButtonClickHandler());

		_path = DATA_PATH + "/make_machine_example.jpg";
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

		Intent intent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "resultCode: " + resultCode);
		switch (resultCode) {
		case 0:
			Log.i(TAG, "User cancelled");
			break;

		case -1:
			onPhotoTaken();
			break;
		}
	}

	protected void onPhotoTaken() {
		Log.i(TAG, "onPhotoTaken");

		_taken = true;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

		_image.setImageBitmap(bitmap);

		_field.setVisibility(View.GONE);

		try {
			ImageUtil.autoRotate(bitmap, _path);
			String text = ImageUtil.recognizeDigits(bitmap);
			// text = text.replaceAll("\\D+", "");

			Toast.makeText(this, text, Toast.LENGTH_LONG).show();

		} catch (IOException e) {
			Toast.makeText(this, "Rotate is mislukt.", Toast.LENGTH_SHORT)
					.show();
		}
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
