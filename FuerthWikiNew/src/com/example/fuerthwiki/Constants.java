package com.example.fuerthwiki;

import java.io.File;

import android.os.Environment;

public class Constants {

	public static final int FILE_SELECT_CODE = 0;
	public static final int CODE_FOR_RESULTFOLDER = 1;
	public static final int CODE_FOR_RESULTFILE = 5;
	public static final int CODE_FOR_PHOTONAME = 6;
	public static final int CODE_FOR_WORKSHEETCONTENT = 7;
	public static final String EXCELFILE = "excelfile";
	public static final String PHOTONAME = "photoname";
	public static final String WORKSHEET = "worksheet";
	public static final String FUERTHWIKI_FOLDER = Environment.getExternalStoragePublicDirectory(
		    Environment.DIRECTORY_PICTURES)+File.separator+ "FuerthWiki"+File.separator;
}
