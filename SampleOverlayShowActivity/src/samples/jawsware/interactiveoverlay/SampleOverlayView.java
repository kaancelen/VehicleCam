package samples.jawsware.interactiveoverlay;

/*
Copyright 2011 jawsware international
*/

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.jawsware.core.share.OverlayService;
import com.jawsware.core.share.OverlayView;
import com.kaancelen.vehiclecam.app.MainActivity;
import com.kaancelen.vehiclecam.app.MinimizeToMainActivity;
import com.kaancelen.vehiclecam.camera.CameraPreview;
import com.kaancelen.vehiclecam.camera.VideoRecorder;
import com.kaancelen.vehiclecam.constants.Constants;
import com.kaancelen.vehiclecam.errors.LogErrors;
import com.kaancelen.vehiclecam.errors.ToastMessages;
import com.kaancelen.vehiclecam.ftpupload.FTPAccount;
import com.kaancelen.vehiclecam.gps.GPSModule;
import com.kaancelen.vehiclecam.helpers.CameraHelper;
import com.kaancelen.vehiclecam.helpers.SharedPreferencer;

public class SampleOverlayView extends OverlayView {

	private static final String TAG = "SampleOverlayView";
	private OverlayService service;
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
	
	public SampleOverlayView(OverlayService service) {
		super(service, R.layout.overlay, 1);
		this.service = service;
		Log.d(TAG, "public SampleOverlayView(OverlayService service)");
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		Log.d(TAG, "protected void onAttachedToWindow()");
		//kamera varm� kontrol et
		if(!CameraHelper.checkCameraHardware(getContext())){
			Log.e(TAG ,LogErrors.Messages.NO_CAMERA);
			Toast.makeText(getContext(), ToastMessages.NO_CAMERA, Toast.LENGTH_LONG).show();
			detachAllViewsFromParent();//view'u kapat
		}
		//uygulama ayarlar�n� al
		ftpOption = SharedPreferencer.getFTPUploadOption(getContext());
		camOption = SharedPreferencer.getCameraID(getContext());
		durationOption = SharedPreferencer.getRecordDuration(getContext());
		ftpAccount = SharedPreferencer.getFTPAccount(getContext());
		//kameray� al ve kontrol et
		camera = CameraHelper.getCameraInstance(camOption);
		if(camera == null){
			Log.e(TAG, "Camera instance al�nam�yor!\n");
			Toast.makeText(getContext(), ToastMessages.CANNOT_OPEN_CAMERA, Toast.LENGTH_SHORT).show();
			detachAllViewsFromParent();//view'u kapat
		}
		//camera preview'unu ayarla
//		camera.setDisplayOrientation(90);
		cameraPreview = new CameraPreview(getContext(), camera);
		FrameLayout frameLayout = (FrameLayout)findViewById(R.id.camera_preview);
		frameLayout.addView(cameraPreview);
		//video kaydetmek i�in kay�t objesini olu�tur
		videoRecorder = new VideoRecorder(camera);
		//Timer ayarla
		timer = new Timer();
		recordingTask = new RecordingTask();
		timer.schedule(recordingTask, Constants.PREPARE_DURATION, durationOption);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Log.d(TAG, "protected void onDetachedFromWindow()");
		if(timer!=null){ //release Timer
			timer.cancel();
			timer = null;
		}
		if(videoRecorder != null){ //release video recorder
			videoRecorder.releaseMediaRecorder(/*ftpOption, ftpAccount, gpsModule.toString()*/);
			videoRecorder = null;
		}
//		if(gpsModule!=null){
//			gpsModule = null;
//		}
		if(camera != null){//release camera
			camera.release();
			camera = null;
		}
	}
	
	@Override
	public boolean onTouchEvent_LongPress() {
		Log.d(TAG, "public boolean onTouchEvent_LongPress()");
		//Main activity'yi olu�tur ve ba�lat
		Intent intent = new Intent(service.getBaseContext(), MinimizeToMainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		service.getApplication().startActivity(intent);
		return true;
	}
	
	/**
	 * Yeni video kayd� ba�latmak i�in haz�rl�k yapar ve ba�latma komutu verir.
	 * @return
	 */
	private boolean startRecording(){
		//Kamera kay�tta de�il haz�rlayal�m
		if(videoRecorder.prepareRecording()){
			//kameray� ba�ar�l� �ekilde haz�rlad�k.
			new VideoRecording().execute(Constants.START_RECORDING);
			return true;
		}else{
			//bir�eyler ters gitti ba�layam�yor.
			videoRecorder.releaseMediaRecorder(/*false, null, ""*/);
			return false;
		}
	}
	
	/**
	 * Video kayd�n� durdurur ve haf�zaya kaydeder.
	 * @return
	 */
	private boolean stopRecording(){
		//kamera kayd� bitti
		new VideoRecording().execute(Constants.STOP_RECORDING);
		videoRecorder.releaseMediaRecorder(/*ftpOption, ftpAccount, gpsModule.toString()*/);
		return true;
	}
	
	/**
	 * Async VideoRecording, MediaRecorder objesi 
	 * kay�t etme ve kay�t durdurma i�lemini asenkron yapmal� yoksa exception al�yorsunuz
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
				Log.e(TAG, "Exception : " + e.getMessage());
			}
			return null;
		}	
	}
	
	/**
	 * Kay�t i�lemi belli periyotlar ile kendini tekrarlamal�
	 * Bu tekrarlama i�lemini RecordingTask timer task'� ger�ekle�tiriyor
	 * @author BeratKaan
	 */
	private class RecordingTask extends TimerTask{
		private static final String TAG = "RecordingTask";
		private boolean isRecording = false;
		
		@Override
		public void run() {
			Log.d(TAG, "public void run()");
			if(!isRecording){ // sadece ilk �al��maya ba�lad���nda 
				startRecording(); //bu ifin i�ine girilir
				isRecording = true;
				Log.d(TAG, "Start Recording");
			}else{
				stopRecording();
				Log.d(TAG, "Stop Recording");
				SystemClock.sleep(Constants.PREPARE_DURATION);//az bekle kendine gelsin
				startRecording();
				Log.d(TAG, "Start Recording");
			}
		}
	}
}
