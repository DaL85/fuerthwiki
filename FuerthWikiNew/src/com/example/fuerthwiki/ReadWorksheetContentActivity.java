package com.example.fuerthwiki;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
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
public class ReadWorksheetContentActivity extends ListActivity {

	private static final String LOG_TAG = ReadWorksheetContentActivity.class
			.getSimpleName();
	private final ArrayAdapter<String> excelItemArrayAdapter = new ArrayAdapter<String>(
			this, android.R.layout.simple_list_item_1);

	private List<String> excelItems = new ArrayList<>();

	@Override
	public void onCreate(final Bundle savedInstanceState) {

		try {
			super.onCreate(savedInstanceState);
			this.setContentView(R.layout.activity_getphotoname);
			// hier ListView mit allen Elementen anzeigen

			this.setListAdapter(this.excelItemArrayAdapter);

			final Intent intentInput = this.getIntent();
			final String excelFile = intentInput
					.getStringExtra(Constants.EXCELFILE);
			final String worksheet = intentInput
					.getStringExtra(Constants.WORKSHEET);

			this.excelItems = this.getWorksheetContent(excelFile, worksheet);
			this.excelItemArrayAdapter.addAll(this.excelItems);

			this.excelItemArrayAdapter.notifyDataSetChanged();
			final ListView lv = this.getListView();
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(final AdapterView<?> arg0,
						final View arg1, final int arg2, final long arg3) {
					final Intent intent = new Intent();
					intent.putExtra(
							Constants.PHOTONAME,
							ReadWorksheetContentActivity.this.excelItemArrayAdapter
									.getItem(arg2));
					ReadWorksheetContentActivity.this.setResult(RESULT_OK,
							intent);
					ReadWorksheetContentActivity.this.finish();
				}
			});

		} catch (final Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			this.finish();
		}

	}

	public List<String> getWorksheetContent(final String excelFile,
			final String worksheet) throws IOException {
		final List<String> resultSet = new ArrayList<String>();
		final File inFile = new File(excelFile);
		final FileInputStream FSInputWorkbook = new FileInputStream(inFile);
		if (inFile.exists()) {
			try {
				final HSSFWorkbook workbook = new HSSFWorkbook(FSInputWorkbook);
				// Get the first sheet
				final HSSFSheet sheet = workbook.getSheet(worksheet);
				// Loop over column and lines
				for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
					final HSSFRow row = sheet.getRow(j);
					for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
						final HSSFCell cell = row.getCell(i);
						if (!cell.getStringCellValue().equals("")) {
							resultSet.add(cell.getStringCellValue());
						}
					}
					continue;
				}
				workbook.close();
			} catch (final Exception e) {
				Log.e(LOG_TAG, e.getMessage(), e);
			}
		} else {
			resultSet.add("File not found..!");
		}
		FSInputWorkbook.close();

		if (resultSet.isEmpty()) {
			resultSet.add("Data not found..!");
			resultSet.add("Evtl konnte das File nicht gelesen werden");
			resultSet.add("Dokument als .xls speichern");
		}
		return resultSet;
	}

}
