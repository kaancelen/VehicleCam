package com.kaancelen.vehiclecam.options;

import com.kaancelen.vehiclecam.constants.Constants;
import com.kaancelen.vehiclecam.helpers.SharedPreferencer;
import samples.jawsware.interactiveoverlay.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.Switch;

public class OptionsActivity extends Activity{
	
	private static final String TAG = "OptionsActivity";
	private Switch ftpOption;
	private RadioGroup camOption;
	private RadioGroup durationOption;
	private boolean ftp;
	private int cam;
	private int duration;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "protected void onCreate(Bundle savedInstanceState)");
		setContentView(R.layout.options);
		//view objelerini al
		ftpOption = (Switch)findViewById(R.id.ftpOption);
		camOption = (RadioGroup)findViewById(R.id.camOption);
		durationOption = (RadioGroup)findViewById(R.id.durationOption);
		//kaynaklardan ayarlarý çek
		ftp = SharedPreferencer.getFTPUploadOption(getApplicationContext());
		cam = SharedPreferencer.getCameraID(getApplicationContext());
		duration = SharedPreferencer.getRecordDuration(getApplicationContext());
		//ayarlarý viewlara ata
		ftpOption.setChecked(ftp);
		if(cam==Constants.BACK_CAMERA)
			camOption.check(R.id.backCam);
		else
			camOption.check(R.id.frontCam);
		switch (duration) {
			case Constants.SECOND_30: durationOption.check(R.id.second30);
				break;
			case Constants.MINUTE_2: durationOption.check(R.id.minute2);
				break;
			case Constants.MINUTE_5: durationOption.check(R.id.minute5);
				break;
			case Constants.MINUTE_10: durationOption.check(R.id.minute10);
				break;
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "protected void onStart()");
		//View Listeners
		ftpOption.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ftp = isChecked;
				SharedPreferencer.setFTPUploadOption(ftp, getApplicationContext());
			}
		});
		
		camOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.frontCam:
					if(cam != Constants.FRONT_CAMERA){
						cam = Constants.FRONT_CAMERA;
						SharedPreferencer.setCameraID(Constants.FRONT_CAMERA, getApplicationContext());
					}
					break;
				case R.id.backCam:
					if(cam != Constants.BACK_CAMERA){
						cam = Constants.BACK_CAMERA;
						SharedPreferencer.setCameraID(Constants.BACK_CAMERA, getApplicationContext());
					}
					break;
				}
			}
		});
		
		durationOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.second30:
					if(duration != Constants.SECOND_30){
						duration = Constants.SECOND_30;
						SharedPreferencer.setRecordDuration(Constants.SECOND_30, getApplicationContext());
					}
					break;
				case R.id.minute2:
					if(duration != Constants.MINUTE_2){
						duration = Constants.MINUTE_2;
						SharedPreferencer.setRecordDuration(Constants.MINUTE_2, getApplicationContext());
					}
					break;
				case R.id.minute5:
					if(duration != Constants.MINUTE_5){
						duration = Constants.MINUTE_5;
						SharedPreferencer.setRecordDuration(Constants.MINUTE_5, getApplicationContext());
					}
					break;
				case R.id.minute10:
					if(duration != Constants.MINUTE_10){
						duration = Constants.MINUTE_10;
						SharedPreferencer.setRecordDuration(Constants.MINUTE_10, getApplicationContext());
					}
					break;
				}
			}
		});
	}

}
