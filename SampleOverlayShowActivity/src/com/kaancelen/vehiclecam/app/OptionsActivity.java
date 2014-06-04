package com.kaancelen.vehiclecam.app;

import com.kaancelen.vehiclecam.constants.Constants;
import com.kaancelen.vehiclecam.errors.ToastMessages;
import com.kaancelen.vehiclecam.ftpupload.FTPAccount;
import com.kaancelen.vehiclecam.ftpupload.FTPConstants;
import com.kaancelen.vehiclecam.helpers.SharedPreferencer;
import samples.jawsware.interactiveoverlay.R;
import samples.jawsware.interactiveoverlay.SampleOverlayShowActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class OptionsActivity extends Activity{
	
	private static final String TAG = "OptionsActivity";
	private Switch ftpOption;
	private RadioGroup camOption;
	private RadioGroup durationOption;
	private boolean ftp;
	private int cam;
	private int duration;
	private TextView ftpUrlLabel;
	private TextView ftpUsernameLabel;
	private TextView ftpPasswordLabel;
	private EditText ftpUrl;
	private EditText ftpUsername;
	private EditText ftpPassword;
	private Button ftpDefault;
	private Button ftpOK;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "protected void onCreate(Bundle savedInstanceState)");
		setContentView(R.layout.activity_options);
		//view objelerini al
		ftpOption = (Switch)findViewById(R.id.ftpOption);
		camOption = (RadioGroup)findViewById(R.id.camOption);
		durationOption = (RadioGroup)findViewById(R.id.durationOption);
		ftpUrlLabel = (TextView)findViewById(R.id.ftpUrlLabel);
		ftpUsernameLabel = (TextView)findViewById(R.id.ftpUsernameLabel);
		ftpPasswordLabel = (TextView)findViewById(R.id.ftpPasswordLabel);
		ftpUrl = (EditText)findViewById(R.id.ftpUrl);
		ftpUsername = (EditText)findViewById(R.id.ftpUsername);
		ftpPassword = (EditText)findViewById(R.id.ftpPassword);
		ftpDefault = (Button)findViewById(R.id.ftpDefault);
		ftpOK = (Button)findViewById(R.id.ftpOK);
		//kaynaklardan ayarlar� �ek
		ftp = SharedPreferencer.getFTPUploadOption(getApplicationContext());
		cam = SharedPreferencer.getCameraID(getApplicationContext());
		duration = SharedPreferencer.getRecordDuration(getApplicationContext());
		//ayarlar� viewlara ata
		ftpOption.setChecked(ftp);
		setFTPOptionViews(ftp);//viewlar� de�i�
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
		//viewlara de�erleri ata
		FTPAccount ftpAccount = SharedPreferencer.getFTPAccount(getApplicationContext());
		ftpUrl.setText(ftpAccount.getUrl());
		ftpUsername.setText(ftpAccount.getUsername());
		ftpPassword.setText(ftpAccount.getPassword());
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
				setFTPOptionViews(ftp);//Viewlar� de�i�
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
	
	/**
	 * Ayarlar ekran�nda geri d�n butonuna bas�nca �a��r�l�r ve MainActivity'ye gidilir.
	 * @param v
	 */
	public void onClickMain(View v){
		Log.d(TAG, "onClickMain");
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}
	/**
	 * Ayarlar ekran�nda minimize butonuna bas�nca �a��r�l�r ve SampleOverlayShowActivity �a��r�l�r
	 * @param v
	 */
	public void onClickMinimize(View v){
		Log.d(TAG, "onClickMinimize");
		startActivity(new Intent(this, SampleOverlayShowActivity.class));
		finish();
	}
	
	/**
	 * Ayarlar ekran�nda buluta y�kleme se�ene�i etkinle�ince g�r�nen bir buton,
	 * bu butona bas�nca ftp ayarlar�n� editTextlerde s�f�rlar
	 * @param v
	 */
	public void onClickDefault(View v){
		Log.d(TAG, "onClickDefault");
		//edittextlere orjinal ayarlar� ata
		ftpUrl.setText(FTPConstants.DEFAULT_FTP_URL);
		ftpUsername.setText(FTPConstants.DEFAULT_FTP_USERNAME);
		ftpPassword.setText(FTPConstants.DEFAULT_FTP_PASSWORD);
		//orjinal ayarlar� kaydet
		SharedPreferencer.setFTPAccount(
					new FTPAccount(FTPConstants.DEFAULT_FTP_URL, FTPConstants.DEFAULT_FTP_USERNAME, FTPConstants.DEFAULT_FTP_PASSWORD), 
					getApplicationContext());
		//Bilgilendir
		Toast.makeText(getApplicationContext(), ToastMessages.FTP_SETTINGS_RESETTED, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Ayarlar ekran�nda buluta y�kleme se�ene�i etkinle�ince g�r�nen bir buton,
	 * bu butona bas�nca editTextlerde bulunan ftpAyarlar�n� kaydeder.
	 * @param v
	 */
	public void onClickFtpOK(View v){
		//Edittextlerdeki ayarlar� kaydet
		SharedPreferencer.setFTPAccount(
				new FTPAccount(ftpUrl.getText().toString(), ftpUsername.getText().toString(), ftpPassword.getText().toString()), 
				getApplicationContext());
		//bilgilendir
		Toast.makeText(getApplicationContext(), ToastMessages.FTP_SETTINGS_SAVED, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Buluta y�kleme se�ene�i ile alakal� viewlar� g�r�n�r ve g�r�nmez yapar.
	 * E�er Buluta y�kleme se�ene�i aktifse g�r�n�r.
	 * Buluta y�kleme se�ene�i kapal� ise g�r�nmez yapar.
	 * @param flag, buluta y�kleme se�ene�i
	 */
	private void setFTPOptionViews(boolean flag){
		//flag'e g�re g�r�n�r g�r�nmez ayarla
		int visibility = flag?View.VISIBLE:View.INVISIBLE;
		//ayarlar� viewlara ata
		ftpUrlLabel.setVisibility(visibility);
		ftpUsernameLabel.setVisibility(visibility);
		ftpPasswordLabel.setVisibility(visibility);
		ftpUrl.setVisibility(visibility);
		ftpUsername.setVisibility(visibility);
		ftpPassword.setVisibility(visibility);
		ftpDefault.setVisibility(visibility);
		ftpOK.setVisibility(visibility);
	}

}
