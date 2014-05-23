package com.kaancelen.vehiclecam.app;

import java.util.Timer;
import java.util.TimerTask;
import com.kaancelen.vehiclecam.R;
import com.kaancelen.vehiclecam.constants.Constants;
import com.kaancelen.vehiclecam.errors.LogErrors;
import com.kaancelen.vehiclecam.errors.ToastMessages;
import com.kaancelen.vehiclecam.gps.GPSModule;
import com.kaancelen.vehiclecam.helpers.CameraHelper;
import com.kaancelen.vehiclecam.helpers.SharedPreferencer;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private Camera camera;
	private CameraPreview cameraPreview;
	private VideoRecorder videoRecorder;
	private Timer timer;
	private RecordingTask recordingTask;
	private GPSModule gpsModule;
	private int cameraID;
	private int recordDuration;
	private boolean ftpOption;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_main);	
		
		//check if there is a camera
		if(!CameraHelper.checkCameraHardware(getApplicationContext())){
			Log.e(LogErrors.Tags.NO_CAMERA,LogErrors.Messages.NO_CAMERA);
			Toast.makeText(getApplicationContext(), ToastMessages.NO_CAMERA, Toast.LENGTH_LONG).show();
			return;
		}
		//get back camera object from hardware
		cameraID = SharedPreferencer.getCameraID(getApplicationContext());
		Log.d(TAG, "CameraID = "+cameraID);
		camera = CameraHelper.getCameraInstance(cameraID);
		if(camera == null){
			Toast.makeText(getApplicationContext(), ToastMessages.CANNOT_OPEN_CAMERA, Toast.LENGTH_SHORT).show();
			return;
		}
		//set the preview
		cameraPreview = new CameraPreview(this, camera);
		FrameLayout frameLayout = (FrameLayout)findViewById(R.id.back_camera_preview);
		frameLayout.addView(cameraPreview);
		
		//create video recorder object with camera
		videoRecorder = new VideoRecorder(camera);
		
		//set GPSModule
		gpsModule = new GPSModule(this);

		//create timers
		timer = new Timer();
		recordingTask = new RecordingTask();
		
		//set timer for repeat
		recordDuration = SharedPreferencer.getRecordDuration(getApplicationContext());
		Log.d(TAG, "recordDuration = "+recordDuration);
		timer.schedule(recordingTask, Constants.PREPARE_DURATION, recordDuration);
		
		//settext for information
		((TextView)findViewById(R.id.cameraIdText)).setText(cameraID==0?"Arka\nKam":"Ön\nKam");
		((TextView)findViewById(R.id.recordDurationText)).setText("Periyot\n("+(recordDuration/1000)+")sn");
		
		//getFtpOption
		ftpOption = SharedPreferencer.getFTPUploadOption(getApplicationContext());
	}
	
	public void onClickOptions(View v){
		Log.d(STORAGE_SERVICE, "onClickOptions");
		Intent intent = new Intent(this, OptionsActivity.class);
		startActivity(intent);
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
			videoRecorder.releaseMediaRecorder(ftpOption, gpsModule.getLatitude()+"_"+gpsModule.getLongitude());
			videoRecorder = null;
		}
		if(gpsModule!=null){
			gpsModule = null;
		}
		releaseCamera();
	}
	
	/**
	 * 
	 */
	private void releaseCamera(){
		if(camera != null){
			camera.release();
			camera = null;
		}
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
			videoRecorder.releaseMediaRecorder(false, "");
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
		videoRecorder.releaseMediaRecorder(ftpOption, gpsModule.getLatitude()+"_"+gpsModule.getLongitude());
		return true;
	}
	
	/**
	 * Async VideoRecording, MediaRecorder objesi 
	 * kayýt etme ve kayýt durdurma iþlemini asenkron yapmalý yoksa exception alýyorsunuz
	 * @author BeratKaan
	 */
	class VideoRecording extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			try {
				if(params[0] == Constants.START_RECORDING){
					videoRecorder.startRecording();
				}else if(params[0] == Constants.STOP_RECORDING){
					videoRecorder.stopRecording();
				}
			} catch (Exception e) {
				Log.e(LogErrors.Tags.ASYN_TASK_ERROR, e.getMessage());
			}
			return null;
		}	
	}
	
	/**
	 * Kayýt iþlemi belli periyotlar ile kendini tekrarlamalý
	 * Bu tekrarlama iþlemini RecordingTask timer task'ý gerçekleþtiriyor
	 * @author BeratKaan
	 */
	class RecordingTask extends TimerTask{

		private boolean isRecording = false;
		
		@Override
		public void run() {
			if(!isRecording){
				startRecording();//start record
				isRecording = true;//mark it
				Log.d(LogErrors.Tags.RECORDING_TASK, LogErrors.Messages.IS_RECORDING_TRUE+LogErrors.Messages.RECORD_START);
			}else{
				stopRecording();//stop and save record
				isRecording = false;
				Log.d(LogErrors.Tags.RECORDING_TASK, LogErrors.Messages.IS_RECORDING_FALSE+LogErrors.Messages.RECORD_STOP);
				//SystemClock.sleep(Constants.PREPARE_DURATION);//wait 500millisecond
				startRecording();//start record again
				isRecording = true;
				Log.d(LogErrors.Tags.RECORDING_TASK, LogErrors.Messages.IS_RECORDING_TRUE+LogErrors.Messages.RECORD_START);
			}
		}
	}
}
