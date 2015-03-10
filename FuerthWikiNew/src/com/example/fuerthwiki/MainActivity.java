package com.example.fuerthwiki;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("javadoc")
public class MainActivity extends Activity {

	private static final String LOG_TAG = MainActivity.class.getSimpleName();
	private static String excelFile = "";
	TextView textviewInfotextExcelfile;
	TextView textviewResultfolder;
	Button buttonTakePhoto;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		this.textviewInfotextExcelfile = (TextView) this
				.findViewById(R.id.textView_Infotext_ExcelFile);
		this.textviewResultfolder = (TextView) this
				.findViewById(R.id.textView_Infotext_ResultFolder);
		this.textviewResultfolder
				.setText("Zielordner der Bilder: "
						+ Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
						+ File.separator + "FuerthWiki");
		this.buttonTakePhoto = (Button) this
				.findViewById(R.id.button_Take_Photo);

		this.buttonTakePhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (!excelFile.equals("")) {
					final Intent iname = new Intent(MainActivity.this,
							GetPhotoNameActivity.class);
					iname.putExtra(Constants.EXCELFILE, excelFile);
					MainActivity.this.startActivityForResult(iname,
							Constants.CODE_FOR_PHOTONAME);
				} else {
					Toast.makeText(MainActivity.this,
							"Bitte ein Excelfile w�hlen", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {

		switch (requestCode) {
		case Constants.FILE_SELECT_CODE:
			if (resultCode == RESULT_OK) {
				// Get the Uri of the selected file
				final Uri uri = data.getData();
				Log.d(LOG_TAG, "File Uri: " + uri.toString());
				final String path = getPath(this, uri);
				excelFile = path;
				this.textviewInfotextExcelfile.setText("gew�hltes File:"
						+ excelFile);
				Log.d(LOG_TAG, "File Path: " + path);
			}
			break;

		case Constants.CODE_FOR_PHOTONAME:
			if (resultCode == RESULT_OK) {
				final String photoName = data
						.getStringExtra(Constants.PHOTONAME);
				final Intent i = new Intent(MainActivity.this,
						CameraActivity.class);
				i.putExtra(Constants.PHOTONAME, photoName);
				this.startActivityForResult(i, Constants.CODE_FOR_RESULTFILE);
			} else {
				Toast.makeText(MainActivity.this,
						"Es wurde kein Fotoname gew�hlt", Toast.LENGTH_SHORT)
						.show();
			}
			break;

		case Constants.CODE_FOR_RESULTFILE:
			Log.d(LOG_TAG,
					"activity result; parsing uri: "
							+ data.getStringExtra("uri"));
			this.galleryAddPic(data.getStringExtra("uri"));
			// Bitmap snapshot =
			// BitmapFactory.decodeFile(data.getStringExtra("uri"));
			Toast.makeText(MainActivity.this,
					"Foto wurde erfolgreich aufgenommen", Toast.LENGTH_SHORT)
					.show();
			break;
		default:
			break;
		}
	}// onActivityResult

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_exit:
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Beenden");
			builder.setMessage("Wollen Sie FuerthWiki wirklich beenden?");
			builder.setPositiveButton("JA",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog,
								final int which) {
							System.exit(0);
							dialog.dismiss();
						}
					});

			builder.setNegativeButton("NEIN",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog,
								final int which) {
							dialog.dismiss();
						}

					});
			final AlertDialog alert = builder.create();
			alert.show();
			return true;

		case R.id.action_excelfile:

			final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);

			try {
				this.startActivityForResult(
						Intent.createChooser(intent, "Select a File to Upload"),
						Constants.FILE_SELECT_CODE);
			} catch (final android.content.ActivityNotFoundException e) {
				Log.e(LOG_TAG, e.getMessage(), e);
				// Potentially direct the user to the Market with a Dialog
				Toast.makeText(this, "Please install a File Manager.",
						Toast.LENGTH_SHORT).show();
			}
			return true;

		case R.id.action_info:
			final String title = this.getString(R.string.app_name)
					+ " build 1.1";
			final Builder builder_INFO = new Builder(this);
			builder_INFO.setTitle(title);
			builder_INFO.setView(View.inflate(this, R.layout.info, null));
			builder_INFO.setIcon(R.drawable.ic_launcher);
			builder_INFO.setPositiveButton("OK", null);
			final Dialog info = builder_INFO.create();
			info.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static String getPath(final Context context, final Uri uri) {
		String result = "";
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			final String[] projection = { "_data" };

			try {
				final Cursor cursor = context.getContentResolver().query(uri,
						projection, null, null, null);
				final int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					result = cursor.getString(column_index);
				}
				cursor.close();
			} catch (final Exception e) {
				Log.e(LOG_TAG, e.getMessage(), e);
				// Eat it
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			result = uri.getPath();
		}

		return result;
	}

	public String getFileName(final String Path) {
		final String[] pathArray = Path.split("/");
		return pathArray[pathArray.length - 1];
	}

	private void galleryAddPic(final String mCurrentPhotoPath) {
		final Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		final File f = new File(mCurrentPhotoPath);
		final Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

}
