package com.example.fuerthwiki;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;

public class GetPhotoNameActivity extends ListActivity{


	private static final String LOG_TAG = GetPhotoNameActivity.class.getSimpleName();
	private ArrayAdapter<String> ExcelItemArrayAdapter;
	private List<String> excelItems;
	String excelFile;
	String[] item_array=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		try {
			super.onCreate(savedInstanceState);   
			setContentView(R.layout.activity_getphotoname);
			//hier ListView mit allen Elementen anzeigen
			ExcelItemArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);//new CustomAdapter(this,R.id.textview_b_item);
	        setListAdapter(ExcelItemArrayAdapter);
	        
	        Intent i = getIntent();
	        excelFile = i.getStringExtra(Constants.EXCELFILE);
	        excelItems=getWorksheets(excelFile);
	        for	(String item : excelItems)
	        {
	        	ExcelItemArrayAdapter.add(item);
	        }	        
	        ExcelItemArrayAdapter.notifyDataSetChanged();
			ListView lv = getListView();
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Intent i = new Intent(GetPhotoNameActivity.this, ReadWorksheetContentActivity.class);
					i.putExtra(Constants.EXCELFILE, excelFile);
					i.putExtra(Constants.WORKSHEET, ExcelItemArrayAdapter.getItem(arg2));
					startActivityForResult(i, Constants.CODE_FOR_WORKSHEETCONTENT);	
				}
			});
	      
			//Element w�hlen lassen dann(aber noch ab�ndern):
//			
			
		} catch (Exception e) {
			finish();
		}

	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    	switch (requestCode) {
        case Constants.CODE_FOR_WORKSHEETCONTENT:
        	String photoName = data.getStringExtra(Constants.PHOTONAME);
        	Intent intent = new Intent();
        	intent.putExtra(Constants.PHOTONAME, photoName);
        	setResult(Constants.CODE_FOR_PHOTONAME, intent);
        	finish();
        	break;
    	}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
	}
	public List<String> getWorksheets(String excelFile) throws IOException  {
		List<String> resultSet = new ArrayList<String>();
	    File inputWorkbook = new File(excelFile);
	    if(inputWorkbook.exists()){
	        try {
	        	Workbook w = Workbook.getWorkbook(inputWorkbook);
	        	for(int j = 0; j < w.getSheets().length; j++) {
	        		Sheet sheet = w.getSheet(j);
	        		if(!sheet.getName().equals(""))
	        			resultSet.add(sheet.getName());
	        	}
	        }catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    else
	    {
	        resultSet.add("File not found..!");
	    }
	    if(resultSet.size()==0){
	        resultSet.add("Data not found..!");
	        resultSet.add("Evtl konnte das File nicht gelesen werden");
	        resultSet.add("Dokument als .xls speichern");
	    }
	    return resultSet;

    }



}