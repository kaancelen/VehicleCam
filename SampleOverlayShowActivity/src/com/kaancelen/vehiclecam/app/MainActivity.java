package com.kaancelen.vehiclecam.app;

import java.util.Timer;
import java.util.TimerTask;
import samples.jawsware.interactiveoverlay.R;
import samples.jawsware.interactiveoverlay.SampleOverlayShowActivity;
import com.kaancelen.vehiclecam.camera.CameraPreview;
import com.kaancelen.vehiclecam.camera.VideoRecorder;
import com.kaancelen.vehiclecam.constants.Constants;
import com.kaancelen.vehiclecam.errors.LogErrors;
import com.kaancelen.vehiclecam.errors.ToastMessages;
import com.kaancelen.vehiclecam.ftpupload.FTPAccount;
import com.kaancelen.vehiclecam.gps.GPSModule;
import com.kaancelen.vehiclecam.helpers.CameraHelper;
import com.kaancelen.vehiclecam.helpers.SharedPreferencer;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private Camera camera;
	private CameraPreview cameraPreview;
	private VideoRecorder videoRecorder;
	private Timer timer;
	private RecordingTask recordingTask;
	private boolean ftpOption;
	private int camOption;
	private int durationOption;
	private GPSModule gpsModule;
	private FTPAccount ftpAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_main);	
		
		//check if there is a camera
		if(!CameraHelper.checkCameraHardware(getApplicationContext())){
			Log.e(LogErrors.Tags.NO_CAMERA,LogErrors.Messages.NO_CAMERA);
			Toast.makeText(getApplicationContext(), ToastMessages.NO_CAMERA, Toast.LENGTH_LONG).show();
			finish();
		}
		//uygulama ayarlarýný al
		ftpOption = SharedPreferencer.getFTPUploadOption(getApplicationContext());
		camOption = SharedPreferencer.getCameraID(getApplicationContext());
		durationOption = SharedPreferencer.getRecordDuration(getApplicationContext());
		ftpAccount = SharedPreferencer.getFTPAccount(getApplicationContext());
		//get back camera object from hardware
		camera = CameraHelper.getCameraInstance(camOption);
		if(camera == null){
			Log.e(TAG, "Camera instance alýnamýyor!\n");
			Toast.makeText(getApplicationContext(), ToastMessages.CANNOT_OPEN_CAMERA, Toast.LENGTH_SHORT).show();
			finish();
		}
		//set the preview
		cameraPreview = new CameraPreview(this, camera);
		FrameLayout frameLayout = (FrameLayout)findViewById(R.id.back_camera_preview);
		frameLayout.addView(cameraPreview);
		//new gpsModule
		gpsModule = new GPSModule(getApplicationContext());
		//create video recorder object with camera
		videoRecorder = new VideoRecorder(camera);
		//create timers
		timer = new Timer();
		recordingTask = new RecordingTask();
		timer.schedule(recordingTask, Constants.PREPARE_DURATION, durationOption);
		//set icons for information
		ImageView cameraOptionImg = (ImageView)findViewById(R.id.cameraOptionImg);
		ImageView durationOptionImg = (ImageView)findViewById(R.id.durationOptionImg);
		ImageView cloudOptionImg = (ImageView)findViewById(R.id.cloudOptionImg);
		//cam view deðiþ
		if(camOption == Constants.BACK_CAMERA)
			cameraOptionImg.setImageResource(R.drawable.back_camera_icon);
		else if(camOption == Constants.FRONT_CAMERA)
			cameraOptionImg.setImageResource(R.drawable.front_camera_icon);
		//duration view deðiþ
		switch (durationOption) {
			case Constants.SECOND_30:
				durationOptionImg.setImageResource(R.drawable.second_30_icon);
				break;
			case Constants.MINUTE_2:
				durationOptionImg.setImageResource(R.drawable.minute_2_icon);
				break;
			case Constants.MINUTE_5:
				durationOptionImg.setImageResource(R.drawable.minute_5_icon);
				break;
			case Constants.MINUTE_10:
				durationOptionImg.setImageResource(R.drawable.minute_10_icon);
				break;
		}
		//cloud view deðiþ
		if(ftpOption)
			cloudOptionImg.setImageResource(R.drawable.cloud_open_icon);
		else
			cloudOptionImg.setImageResource(R.drawable.cloud_close_icon);
		
	}
		
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
		if(videoRecorder!=null){
			videoRecorder.releaseMediaRecorder(ftpOption, ftpAccount, gpsModule.toString());
			videoRecorder = null;
		}
		if(gpsModule!=null){
			gpsModule = null;
		}
		if(camera != null){
			camera.release();
			camera = null;
		}
	}
	/**
	 * Main ekranýnda ayarlar butonuna basýlýnca çaðýrýlýr ve OptionsActivity'yi çalýþtýrýr
	 * @param v
	 */
	public void onClickOptions(View v){
		Log.d(STORAGE_SERVICE, "onClickOptions");
		startActivity(new Intent(this, OptionsActivity.class));
		finish();
	}
	/**
	 * Main ekranýnda minimize butonuna basýlýnca çaðýrýlýr ve SampleOverlayShowActivity'yi çalýþtýrýr
	 * @param v
	 */
	public void onClickMinimize(View v){
		Log.d(TAG, "onClickMinimize");
		startActivity(new Intent(this, SampleOverlayShowActivity.class));
		finish();
	}
	
	/**
	 * It prepare recording if prepare task is ok then starts recording
	 * @return
	 */
	private boolean startRecording(){
		//Camera didn't record and we want to start recording.
		if(videoRecorder.prepareRecording()){
			//we prepare camera succesfully
			new VideoRecording().execute(Constants.START_RECORDING);
			return true;
		}else{
			//somethings go bad we can't record
			videoRecorder.releaseMediaRecorder(false, null, "");
			return false;
		}
	}
	
	/**
	 * It stops recording task and save video to filesystem
	 * @return
	 */
	private boolean stopRecording(){
		//Camera in record and we want to stop recording
		new VideoRecording().execute(Constants.STOP_RECORDING);
		videoRecorder.releaseMediaRecorder(ftpOption, ftpAccount, gpsModule.toString());
		return true;
	}
	
	/**
	 * Async VideoRecording, MediaRecorder objesi 
	 * kayýt etme ve kayýt durdurma iþlemini asenkron yapmalý yoksa exception alýyorsunuz
	 * @author BeratKaan
	 */
	private class VideoRecording extends AsyncTask<Integer, Void, Void> {

		private static final String TAG = "VideoRecording";
		
		@Override
		protected Void doInBackground(Integer... params) {
			Log.d(TAG, "protected Void doInBackground(Integer... params)");
			try {
				if(params[0] == Constants.START_RECORDING){
					videoRecorder.startRecording();
				}else if(params[0] == Constants.STOP_RECORDING){
					videoRecorder.stopRecording();
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}	
	}
	
	/**
	 * Kayýt iþlemi belli periyotlar ile kendini tekrarlamalý
	 * Bu tekrarlama iþlemini RecordingTask timer task'ý gerçekleþtiriyor
	 * @author BeratKaan
	 */
	private class RecordingTask extends TimerTask{
		private static final String TAG = "RecordingTask";
		private boolean isRecording = false;
		
		@Override
		public void run() {
			Log.d(TAG, "public void run()");
			if(!isRecording){
				startRecording();//start record
				isRecording = true;//mark it
				Log.d(LogErrors.Tags.RECORDING_TASK, LogErrors.Messages.IS_RECORDING_TRUE+LogErrors.Messages.RECORD_START);
			}else{
				stopRecording();//stop and save record
				Log.d(LogErrors.Tags.RECORDING_TASK, LogErrors.Messages.IS_RECORDING_FALSE+LogErrors.Messages.RECORD_STOP);
				SystemClock.sleep(Constants.PREPARE_DURATION);//wait 500millisecond
				startRecording();//start record again
				Log.d(LogErrors.Tags.RECORDING_TASK, LogErrors.Messages.IS_RECORDING_TRUE+LogErrors.Messages.RECORD_START);
			}
		}
	}
}
