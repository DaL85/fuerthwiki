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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ReadWorksheetContentActivity extends ListActivity{

	private static final String LOG_TAG = ReadWorksheetContentActivity.class.getSimpleName();
	private ArrayAdapter<String> ExcelItemArrayAdapter;
	private List<String> excelItems;
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
	        String excelFile = i.getStringExtra(Constants.EXCELFILE);
	        String worksheet = i.getStringExtra(Constants.WORKSHEET);
	        excelItems=getWorksheetContent(excelFile,worksheet);
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
					Intent intent = new Intent();
					intent.putExtra(Constants.PHOTONAME, ExcelItemArrayAdapter.getItem(arg2));
					setResult(Constants.CODE_FOR_PHOTONAME, intent);
					finish();
				}
			});
	      
			//Element wählen lassen dann(aber noch abändern):
//			
			
		} catch (Exception e) {
			finish();
		}

	}

	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
	}
	public List<String> getWorksheetContent(String excelFile, String worksheet) throws IOException  {
		List<String> resultSet = new ArrayList<String>();
	    File inputWorkbook = new File(excelFile);
	    if(inputWorkbook.exists()){
	        try {
	        	Workbook w = Workbook.getWorkbook(inputWorkbook);
	            // Get the first sheet
	            Sheet sheet = w.getSheet(worksheet);
	            // Loop over column and lines
	            for (int j = 0; j < sheet.getRows(); j++) {
	                Cell cell = sheet.getCell(0, j);	                
                    for (int i = 0; i < sheet.getColumns(); i++) {
                        Cell cel = sheet.getCell(i, j);
                        if(!cel.getContents().equals(""))
                        	resultSet.add(cel.getContents());
                    }	                
	                continue;
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
