package com.example.fuerthwiki;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

@SuppressWarnings("javadoc")
public class GetPhotoNameActivity extends ListActivity {

	private static final String LOG_TAG = GetPhotoNameActivity.class
			.getSimpleName();
	private final ArrayAdapter<String> excelItemArrayAdapter = new ArrayAdapter<String>(
			this, android.R.layout.simple_list_item_1);

	private final List<String> excelItems = new ArrayList<>();

	String excelFile;

	@Override
	public void onCreate(final Bundle savedInstanceState) {

		try {
			super.onCreate(savedInstanceState);
			this.setContentView(R.layout.activity_getphotoname);

			this.setListAdapter(this.excelItemArrayAdapter);

			final Intent intentInput = this.getIntent();
			this.excelFile = intentInput.getStringExtra(Constants.EXCELFILE);

			final List<String> worksheets = this.getWorksheets();

			this.excelItems.addAll(worksheets);

			this.excelItemArrayAdapter.addAll(worksheets);
			this.excelItemArrayAdapter.notifyDataSetChanged();

			final ListView lv = this.getListView();
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(final AdapterView<?> arg0,
						final View arg1, final int arg2, final long arg3) {
					if (!GetPhotoNameActivity.this.excelItems
							.contains("Data not found..!")
							&& !GetPhotoNameActivity.this.excelItems
									.contains("File not found..!")) {
						final Intent intentOutput = new Intent(
								GetPhotoNameActivity.this,
								ReadWorksheetContentActivity.class);
						intentOutput.putExtra(Constants.EXCELFILE,
								GetPhotoNameActivity.this.excelFile);
						intentOutput.putExtra(Constants.WORKSHEET,
								GetPhotoNameActivity.this.excelItemArrayAdapter
										.getItem(arg2));
						GetPhotoNameActivity.this.startActivityForResult(
								intentOutput,
								Constants.CODE_FOR_WORKSHEETCONTENT);
					} else {
						GetPhotoNameActivity.this.setResult(RESULT_CANCELED);
						GetPhotoNameActivity.this.finish();
					}
				}
			});

		} catch (final Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			this.finish();
		}

	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {

		switch (requestCode) {
		case Constants.CODE_FOR_WORKSHEETCONTENT:
			if (resultCode == RESULT_OK) {
				final String photoName = data
						.getStringExtra(Constants.PHOTONAME);
				final Intent intent = new Intent();
				intent.putExtra(Constants.PHOTONAME, photoName);
				this.setResult(RESULT_OK, intent);
				this.finish();
			}
			break;
		default:
			break;
		}
	}

	private List<String> getWorksheets() {
		final List<String> resultSet = new ArrayList<>();
		final File inputWorkbook = new File(this.excelFile);
		if (inputWorkbook.exists()) {
			try {
				final FileInputStream FSInputWorkbook = new FileInputStream(
						inputWorkbook);

				final HSSFWorkbook workbook = new HSSFWorkbook(FSInputWorkbook);
				for (int j = 0; j < workbook.getNumberOfSheets(); j++) {
					final HSSFSheet sheet = workbook.getSheetAt(j);
					if (!sheet.getSheetName().equals("")) {
						resultSet.add(sheet.getSheetName());
					}
				}
				workbook.close();
				FSInputWorkbook.close();
			} catch (final Exception e) {
				Log.e(LOG_TAG, e.getMessage(), e);
			}
		} else {
			resultSet.add("File not found..!");
		}
		if (resultSet.isEmpty()) {
			resultSet.add("Data not found..!");
			resultSet.add("Evtl konnte das File nicht gelesen werden");
			resultSet.add("Dokument als .xls speichern");
		}
		return resultSet;

	}

}
