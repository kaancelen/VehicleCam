package com.kaancelen.vehiclecam.app;

import com.kaancelen.vehiclecam.R;
import com.kaancelen.vehiclecam.constants.Constants;
import com.kaancelen.vehiclecam.helpers.SharedPreferencer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class OptionsActivity extends Activity{

	private final static String TAG = "OptionsActivity";
	private Switch switch1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_options);
		switch1 = (Switch)findViewById(R.id.switch1);
		switch1.setChecked(SharedPreferencer.getFTPUploadOption(getApplicationContext()));
		//switch listener
		switch1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d("ftpOption", "Buluta yükleme = "+isChecked);
				SharedPreferencer.setFTPUploadOption(isChecked, getApplicationContext());
			}
		});
	}
	/**
	 * Main activity'ye geri döner
	 * @param v
	 */
	public void onClickMain(View v){
		Log.d(TAG, "onClickMain");
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	/**
	 * Radio buttonlarýn listenerlarý
	 */
	public void onClickFrontCam(View v){
		Log.d(TAG, "onClickFrontCam");
		SharedPreferencer.setCameraID(Constants.FRONT_CAMERA, getApplicationContext());
	}
	public void onClickBackCam(View v){
		Log.d(TAG, "onClickBackCam");
		SharedPreferencer.setCameraID(Constants.BACK_CAMERA, getApplicationContext());
	}
	public void onClickThirtySecond(View v){
		Log.d(TAG, "onClickThirtySecond");
		SharedPreferencer.setRecordDuration(Constants.SECOND_30, getApplicationContext());
	}
	public void onClickTwoMinute(View v){
		Log.d(TAG, "onClickTwoMinute");
		SharedPreferencer.setRecordDuration(Constants.MINUTE_2, getApplicationContext());
	}
	public void onClickFiveMinute(View v){
		Log.d(TAG, "onClickFiveMinute");
		SharedPreferencer.setRecordDuration(Constants.MINUTE_5, getApplicationContext());
	}
	public void onClickTenMinute(View v){
		Log.d(TAG, "onClickTenMinute");
		SharedPreferencer.setRecordDuration(Constants.MINUTE_10, getApplicationContext());
	}
}
