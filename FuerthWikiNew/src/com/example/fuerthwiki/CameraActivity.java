package com.example.fuerthwiki;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

@SuppressWarnings("javadoc")
public class CameraActivity extends Activity {
	private static final String LOG_TAG = CameraActivity.class.getSimpleName();
	private static String photoName = "";

	private boolean done = true;
	File sdDir;

	protected static final String PHOTO_TAKEN = "photo_taken";

	@Override
	public void onCreate(final Bundle savedInstanceState) {

		try {
			super.onCreate(savedInstanceState);
			final Intent intentInput = this.getIntent();
			photoName = intentInput.getStringExtra(Constants.PHOTONAME);
			final File root = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
							+ File.separator + "FuerthWiki" + File.separator);
			root.mkdirs();
			this.sdDir = new File(root, photoName + ".jpg");
			Log.d(LOG_TAG,
					"Creating image storage file: " + this.sdDir.getPath());
			this.startCameraActivity();
		} catch (final Exception e) {
			this.finish();
		}

	}

	protected void startCameraActivity() {
		final Uri outputFileUri = Uri.fromFile(this.sdDir);
		final Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		this.startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		final Intent intent = new Intent();
		intent.putExtra("uri", this.sdDir.getPath());
		this.setResult(Constants.CODE_FOR_RESULTFILE, intent);
		this.finish();
	}

	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
		if (savedInstanceState.getBoolean(CameraActivity.PHOTO_TAKEN)) {
			this.done = true;
		}
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		outState.putBoolean(CameraActivity.PHOTO_TAKEN, this.done);
	}

}
