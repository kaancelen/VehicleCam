package com.kaancelen.vehiclecam.helpers;

import com.kaancelen.vehiclecam.constants.Constants;
import com.kaancelen.vehiclecam.ftpupload.FTPAccount;
import com.kaancelen.vehiclecam.ftpupload.FTPConstants;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencer {
	
	private static final String CAMERA_ID_KEY = "qwer4321";
	private static final String RECORD_DURATION_KEY = "posks456";
	private static final String FTP_OPTION_KEY = "sada342d";
	private static final String FTP_ACCOUNT_KEY = "sd340n11";
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
	
	public static boolean setFTPAccount(FTPAccount ftpAccount, Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(FTP_ACCOUNT_KEY, ftpAccount.toString());
		return editor.commit();
	}
	
	public static FTPAccount getFTPAccount(Context context){
		FTPAccount defaultAccount = new FTPAccount(FTPConstants.DEFAULT_FTP_URL, FTPConstants.DEFAULT_FTP_USERNAME, FTPConstants.DEFAULT_FTP_PASSWORD);
		SharedPreferences prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		String string = prefs.getString(FTP_ACCOUNT_KEY, defaultAccount.toString());
		return FTPAccount.fromString(string);
	}
}
