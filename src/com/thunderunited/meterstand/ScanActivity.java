package com.thunderunited.meterstand;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
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

	private static final String DATA_PATH = Environment
			.getExternalStorageDirectory()
			+ "/"
			+ ScanActivity.class.getPackage().getName() + "/";

	private String TABLE_NAME = "mytable";

	private static final String TAG = ScanActivity.class.getName();

	private Button _button;
	private ImageView _image;
	private TextView _field;
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

		_image = (ImageView) findViewById(R.id.image);
		_field = (TextView) findViewById(R.id.field);
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

		_image.setImageBitmap(bitmap);
		_field.setVisibility(View.GONE);

		byte[] imageBytes = ImageUtil.bitmapToByteArray(bitmap);

		try {
			ImageUtil.autoRotate(bitmap, _path);

			createTable();
			saveInDB(imageBytes);

			String text = ImageUtil.recognizeDigits(bitmap);
			// text = text.replaceAll("\\D+", "");

			//Toast.makeText(this, text, Toast.LENGTH_LONG).show();

			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			String title = getString(R.string.recognition_done_message);
			String closeButtonStr = getString(R.string.close);
			alertDialog.setTitle(title);
			alertDialog.setMessage(text);
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, closeButtonStr, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			alertDialog.show();

		} catch (IOException e) {
			Toast.makeText(this, "Rotate is mislukt.", Toast.LENGTH_SHORT)
					.show();
		}
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

	void createTable() {
		String databasePath = DatabaseUtil.getDatabasePath();
		
		SQLiteDatabase myDb = openOrCreateDatabase(databasePath,
				Context.MODE_PRIVATE, null);
		String MySQL = "create table if not exists "
				+ TABLE_NAME
				+ " (_id INTEGER primary key autoincrement, name TEXT not null, image BLOB);";
		myDb.execSQL(MySQL);
		myDb.close();
	}

	void saveInDB(byte[] image) {
		
		String databasePath = DatabaseUtil.getDatabasePath();

		SQLiteDatabase myDb = openOrCreateDatabase(databasePath,
				Context.MODE_PRIVATE, null);

		String s = myDb.getPath();

		myDb.execSQL("delete from " + TABLE_NAME); // clearing the table
		ContentValues newValues = new ContentValues();
		String name = "Foto's";
		newValues.put("name", name);
		try {
			newValues.put("image", image);
			long ret = myDb.insert(TABLE_NAME, null, newValues);
			if (ret < 0) {
				Toast.makeText(this, "Het opslaan is mislukt",
						Toast.LENGTH_LONG);
			}
		} catch (Exception e) {
			Toast.makeText(this, "Error Exception : " + e.getMessage(),
					Toast.LENGTH_LONG);
			Log.e(TAG, "Error", e);
		}
		myDb.close();
		Log.i(TAG, "Saving Details \n Name : " + name);
		Log.i(TAG, "Image Size : " + image.length + " KB");
		Log.i(TAG, "Saved in DB : " + s);

		Toast.makeText(this.getBaseContext(),
				"Image Saved in DB successfully.", Toast.LENGTH_SHORT).show();
	}

}
