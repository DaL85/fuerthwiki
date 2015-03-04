package com.example.fuerthwiki;

import java.io.File;
import java.net.URISyntaxException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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


public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static String excelFile = "";
	TextView textview_infotext_excelfile;
	TextView textview_Resultfolder;
	Button button_take_photo;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview_infotext_excelfile = (TextView)findViewById(R.id.textView_Infotext_ExcelFile);
        textview_Resultfolder = (TextView)findViewById(R.id.textView_Infotext_ResultFolder);
        textview_Resultfolder.setText("Zielordner der Bilder: "+Environment.getExternalStoragePublicDirectory(
		            Environment.DIRECTORY_PICTURES)+File.separator+"FuerthWiki");
        button_take_photo=(Button)findViewById(R.id.button_Take_Photo);
        
        button_take_photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!excelFile.equals(""))
				{
					Intent iname = new Intent(MainActivity.this,GetPhotoNameActivity.class);
					iname.putExtra(Constants.EXCELFILE, excelFile);
					startActivityForResult(iname,Constants.CODE_FOR_PHOTONAME);
				}
				else
					Toast.makeText(MainActivity.this, "Bitte ein Excelfile w�hlen", Toast.LENGTH_SHORT).show();
				
			}
		});
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    	switch (requestCode) {
        case Constants.FILE_SELECT_CODE:
	        if (resultCode == RESULT_OK) {
	            // Get the Uri of the selected file 
	            Uri uri = data.getData();
	            Log.d(TAG, "File Uri: " + uri.toString());
	            try{
	            	String path = getPath(this, uri);
	            	excelFile=path;
	            	textview_infotext_excelfile.setText("gew�hltes File:" +excelFile);
		            Log.d(TAG, "File Path: " + path);
	            }catch(URISyntaxException e){
	            	Log.e(TAG,e.getMessage());
	            }
	        }
	        break;
        
        case Constants.CODE_FOR_PHOTONAME:
        	if(resultCode==RESULT_OK)
        	{
	        	String photoName = data.getStringExtra(Constants.PHOTONAME);
	        	Intent i = new Intent(MainActivity.this, CameraActivity.class);
				i.putExtra(Constants.PHOTONAME, photoName);
				startActivityForResult(i, Constants.CODE_FOR_RESULTFILE);
        	}
        	else
        		Toast.makeText(MainActivity.this, "Es wurde kein Fotoname gew�hlt", Toast.LENGTH_SHORT).show();
        	break;
        	
        case Constants.CODE_FOR_RESULTFILE:
        	Log.d(TAG, "activity result; parsing uri: " + data.getStringExtra("uri"));
        	galleryAddPic(data.getStringExtra("uri"));
			//Bitmap snapshot = BitmapFactory.decodeFile(data.getStringExtra("uri"));
			Toast.makeText(MainActivity.this, "Foto wurde erfolgreich aufgenommen", Toast.LENGTH_SHORT).show();
			break;
    }
    }//onActivityResult

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(item.getItemId()){
        case R.id.action_exit: 
        	 AlertDialog.Builder builder = new AlertDialog.Builder(this);		    
		     builder.setTitle("Beenden");
		     builder.setMessage("Wollen Sie FuerthWiki wirklich beenden?");		    
		     builder.setPositiveButton("JA", new DialogInterface.OnClickListener() {	    
			     @Override
			     public void onClick(DialogInterface dialog, int which) {	
				     System.exit(0);				    
				     dialog.dismiss();
			     }	    
		     });
		    
		     builder.setNegativeButton("NEIN", new DialogInterface.OnClickListener() {	    
			     @Override
			     public void onClick(DialogInterface dialog, int which) {
				     dialog.dismiss();
			     }
			    
		     });
		     AlertDialog alert = builder.create();
		     alert.show();
		     return true;
        
        case R.id.action_excelfile:

    	    Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
    	    intent.setType("*/*"); 
    	    intent.addCategory(Intent.CATEGORY_OPENABLE);

    	    try {
    	        startActivityForResult(
    	                Intent.createChooser(intent, "Select a File to Upload"),
    	                Constants.FILE_SELECT_CODE);
    	    } catch (android.content.ActivityNotFoundException ex) {
    	        // Potentially direct the user to the Market with a Dialog
    	        Toast.makeText(this, "Please install a File Manager.", 
    	                Toast.LENGTH_SHORT).show();
    	    }       	
        	return true;

        case R.id.action_info:
        	String title = getString(R.string.app_name) + " build 1.1";
        	Builder builder_INFO = new Builder(this);
        	builder_INFO.setTitle(title);
        	builder_INFO.setView(View.inflate(this, R.layout.info, null));
        	builder_INFO.setIcon(R.drawable.ic_launcher);
        	builder_INFO.setPositiveButton("OK", null);
        	Dialog info =builder_INFO.create();
        	info.show();
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
    
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    } 
    public String getFileName(String Path){
    	String[] pathArray = Path.split("/");
    	return pathArray[pathArray.length-1];
    }
    private void galleryAddPic(String mCurrentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    
}
