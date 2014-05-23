package com.kaancelen.vehiclecam.app;

import java.util.Timer;
import java.util.TimerTask;
import com.kaancelen.vehiclecam.camera.CameraPreview;
import com.kaancelen.vehiclecam.camera.VideoRecorder;
import com.kaancelen.vehiclecam.constants.Constants;
import com.kaancelen.vehiclecam.errors.LogErrors;
import com.kaancelen.vehiclecam.errors.ToastMessages;
import com.kaancelen.vehiclecam.gps.GPSModule;
import com.kaancelen.vehiclecam.helpers.CameraHelper;
import com.kaancelen.vehiclecam.helpers.SharedPreferencer;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
	private TextView cameraText;
	private TextView durationText;
	private TextView ftpText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_main);
		//set fragment dynamically
		changeFragment(new OptionsFragment());
		initTextObjects();
		getOptionAndSetText();
		if(!getCameraAndSetPreview()){
			return;
		}
		if(!setVideoRecordOptions()){
			return;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		pauseAction();
		releaseCamera();
	}
	/**Fragment geçiþleri için listenerlar*/
	public void onClickApp1(View v){
		changeFragment(new App1Fragment());
	}
	/**Fragment geçiþleri için listenerlar*/
	public void onClickApp2(View v){
		changeFragment(new App2Fragment());
	}
	/**OptionsFragmenta geçiþi saðlar*/
	public void onOptionsClick(View v){
		changeFragment(new OptionsFragment());
	}
	/**optionsFragmentta radiobutonuna týklanýnca*/
	public void onCamClick(View v){
		String text = "";
		int id;
		if(v.getId() == R.id.front_camera){
			text = "Ön";
			id = Constants.FRONT_CAMERA;
		}else if(v.getId() == R.id.back_camera){
			text = "Arka";
			id = Constants.BACK_CAMERA;
		}else{
			Log.e(TAG, "onCamClick FAULT!!");
			return;
		}
		SharedPreferencer.setCameraID(id, getApplicationContext());
//		cameraText.setText(text);
		startActivity(new Intent(this,TempActivity.class));
	}
	/**optionsFragmentta radiobutonana týklanýnca*/
	public void onDurationClick(View v){
		String text = "";
		int duration;
		if(v.getId() == R.id.thirty){
			text = "30sn";
			duration = Constants.SECOND_30;
		}else if(v.getId() == R.id.two){
			text = "120sn";
			duration = Constants.MINUTE_2;
		}else if(v.getId() == R.id.five){
			text = "300sn";
			duration = Constants.MINUTE_5;
		}else if(v.getId() == R.id.ten){
			text = "600sn";
			duration = Constants.MINUTE_10;
		}else{
			Log.e(TAG, "onDurationClick FAULT!!");
			return;
		}
		SharedPreferencer.setRecordDuration(duration, getApplicationContext());
//		durationText.setText(text);
		startActivity(new Intent(this,TempActivity.class));
	}
	/***/
	public void onFtpClick(View v){
		ftpOption = !ftpOption;
		SharedPreferencer.setFTPUploadOption(ftpOption, getApplicationContext());
		ftpText.setText(ftpOption?"Bulut":"Yerel");
	}
	/**Text objelerini ilklendirir.*/
	private void initTextObjects() {
		//init Text Objects
		cameraText = (TextView)findViewById(R.id.cameraIdText);
		durationText = (TextView)findViewById(R.id.recordDurationText);
		ftpText = (TextView)findViewById(R.id.ftptext);
	}
	/**Android sisteminde sakladýðým opsiyonlarý al ve textlere yaz*/
	private void getOptionAndSetText() {
		//get options and set text objects
		cameraID = SharedPreferencer.getCameraID(getApplicationContext());
		recordDuration = SharedPreferencer.getRecordDuration(getApplicationContext());
		ftpOption = SharedPreferencer.getFTPUploadOption(getApplicationContext());
		//settext for information
		cameraText.setText(cameraID==0?"Arka":"Ön");
		durationText.setText("("+(recordDuration/1000)+")sn");
		ftpText.setText(ftpOption?"Bulut":"Yerel");
	}
	/**Kamerayý alýr ve preview'u set eder.
	 * @return sorun olursa false return eder.
	 * */
	private boolean getCameraAndSetPreview(){
		//check if there is a camera
		if(!CameraHelper.checkCameraHardware(getApplicationContext())){
			Log.e(LogErrors.Tags.NO_CAMERA,LogErrors.Messages.NO_CAMERA);
			Toast.makeText(getApplicationContext(), ToastMessages.NO_CAMERA, Toast.LENGTH_LONG).show();
			return false;
		}
		//get back camera object from hardware
		camera = CameraHelper.getCameraInstance(cameraID);
		if(camera == null){
			Toast.makeText(getApplicationContext(), ToastMessages.CANNOT_OPEN_CAMERA, Toast.LENGTH_SHORT).show();
			return false;
		}
		//set the preview
		cameraPreview = new CameraPreview(this, camera);
		FrameLayout frameLayout = (FrameLayout)findViewById(R.id.camera_preview);
		frameLayout.addView(cameraPreview);
		return true;
	}
	/**Video record için gerekli objeleri oluþturur ve timerlarý ayarlar.*/
	private boolean setVideoRecordOptions(){
		//create video recorder object with camera
		videoRecorder = new VideoRecorder(camera);
		
		//set GPSModule
		gpsModule = new GPSModule(this);

		//create timers
		timer = new Timer();
		recordingTask = new RecordingTask();
		
		//set timer for repeat
		timer.schedule(recordingTask, Constants.PREPARE_DURATION, recordDuration);
		return true;
	}
	/**Fragment deðiþtiren method.MainActivity'nin sol tarafýndaki fragmenti deðiþtirir.*/
	private void changeFragment(Fragment fragment){
		//set fragment dynamically
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.left_fragment ,fragment);
		ft.commit();
	}
	private void pauseAction(){
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
	}
	/**kamerayý salar*/
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
