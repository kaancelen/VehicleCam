package samples.jawsware.interactiveoverlay;

/*
Copyright 2011 jawsware international

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import java.util.Timer;
import java.util.TimerTask;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.jawsware.core.share.OverlayService;
import com.jawsware.core.share.OverlayView;
import com.kaancelen.vehiclecam.camera.CameraPreview;
import com.kaancelen.vehiclecam.camera.VideoRecorder;
import com.kaancelen.vehiclecam.constants.Constants;
import com.kaancelen.vehiclecam.errors.LogErrors;
import com.kaancelen.vehiclecam.errors.ToastMessages;
import com.kaancelen.vehiclecam.helpers.CameraHelper;

public class SampleOverlayView extends OverlayView {

	private static final String TAG = "SampleOverlayView";
	private Camera camera;
	private CameraPreview cameraPreview;
	private VideoRecorder videoRecorder;
	private Timer timer;
	private RecordingTask recordingTask;
	
	public SampleOverlayView(OverlayService service) {
		super(service, R.layout.overlay, 1);
		Log.d(TAG, "public SampleOverlayView(OverlayService service)");
		//kamera varm� kontrol et
		if(!CameraHelper.checkCameraHardware(getContext())){
			Log.e(TAG ,LogErrors.Messages.NO_CAMERA);
			Toast.makeText(getContext(), ToastMessages.NO_CAMERA, Toast.LENGTH_LONG).show();
			detachAllViewsFromParent();//view'u kapat
		}
		//kamera objesini al ve kontrol et
		camera = CameraHelper.getCameraInstance(Constants.FRONT_CAMERA);
		if(camera == null){
			Log.e(TAG, "Camera instance al�nam�yor!\n");
			Toast.makeText(getContext(), ToastMessages.CANNOT_OPEN_CAMERA, Toast.LENGTH_SHORT).show();
			detachAllViewsFromParent();//view'u kapat
		}
		//camera preview'unu ayarla
		cameraPreview = new CameraPreview(getContext(), camera);
		FrameLayout frameLayout = (FrameLayout)findViewById(R.id.camera_preview);
		frameLayout.addView(cameraPreview);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		Log.d(TAG, "protected void onAttachedToWindow()");
		//video kaydetmek i�in kay�t objesini olu�tur
		videoRecorder = new VideoRecorder(camera);
		//timer'� ayarla
		timer = new Timer();
		recordingTask = new RecordingTask();
		timer.schedule(recordingTask, Constants.PREPARE_DURATION, Constants.SECOND_30);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Log.d(TAG, "protected void onDetachedFromWindow()");
		if(camera != null){//release camera
			camera.release();
			camera = null;
		}
		if(timer!=null){ //release Timer
			timer.cancel();
			timer = null;
		}
		if(videoRecorder != null){ //release video recorder
			videoRecorder.releaseMediaRecorder();
			videoRecorder = null;
		}
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
			videoRecorder.releaseMediaRecorder();
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
		videoRecorder.releaseMediaRecorder();
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
