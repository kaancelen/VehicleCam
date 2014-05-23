package com.kaancelen.vehiclecam.helpers;

import com.kaancelen.vehiclecam.constants.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencer {
	
	private static final String CAMERA_ID_KEY = "qwer4321";
	private static final String RECORD_DURATION_KEY = "posks456";
	private static final String FTP_OPTION_KEY = "sada342d";
	private static final String PREF_KEY = "74jdhsdb39";

	public static boolean setCameraID(int cameraID, Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putInt(CAMERA_ID_KEY, cameraID);
		return editor.commit();
	}
	
	public static int getCameraID(Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		return prefs.getInt(CAMERA_ID_KEY, Constants.BACK_CAMERA);//Constant.BACK_CAMERA is default
	}
	
	public static boolean setRecordDuration(int recordDuration, Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putInt(RECORD_DURATION_KEY, recordDuration);
		return editor.commit();
	}
	
	public static int getRecordDuration(Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		return prefs.getInt(RECORD_DURATION_KEY, Constants.SECOND_30);// Constants.SECOND_30 is default
	}
	
	public static boolean setFTPUploadOption(boolean ftpOption, Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putBoolean(FTP_OPTION_KEY, ftpOption);
		return editor.commit();
	}
	
	public static boolean getFTPUploadOption(Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		return prefs.getBoolean(FTP_OPTION_KEY, false);// No FTP Upload default
	}
}
