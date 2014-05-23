package com.kaancelen.vehiclecam.camera;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.kaancelen.vehiclecam.constants.Constants;
import com.kaancelen.vehiclecam.errors.LogErrors;
import com.kaancelen.vehiclecam.helpers.SaveFileHelper;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;

public class VideoRecorder {

	private MediaRecorder mediaRecorder;
	private Camera camera;
	private String filepath;
	private static final String PATH = "VehicleCam/34TK231_";/*Ana klasör/Plaka*/
	
	/**
	 * @param camera, Camera objesi
	 * @param cameraID, Camera objesinin numarasý, ön kamera ise 1, arka kamera ise 0
	 */
	public VideoRecorder(Camera camera){
		this.camera = camera;
	}
	
	public boolean prepareRecording(){
		mediaRecorder = new MediaRecorder();
		
		// Step 1: Unlock and set camera to MediaRecorder
		camera.unlock();
		mediaRecorder.setCamera(camera);
		
		// Step 2: Set sources
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	    
	    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	    mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
	    
	    // Step 4: Set output file
	    filepath = SaveFileHelper.getOutputMediaFile(Constants.MEDIA_TYPE_VIDEO).toString();
	    mediaRecorder.setOutputFile(filepath);
	    
	    // Step 5: Set the preview output
//	    mediaRecorder.setPreviewDisplay(surface);
	    
	    //Step 6: Prepare configured MediaRecorder
	    try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			Log.e(LogErrors.Tags.IllegalStateException, e.getMessage());
			releaseMediaRecorder(false, "");
			return false;
		} catch (IOException e) {
			Log.e(LogErrors.Tags.IOException, e.getMessage());
			releaseMediaRecorder(false, "");
			return false;
		}
	    
	    return true;
	}
	
	public void releaseMediaRecorder(boolean isUpload, String gpsString){
		if(mediaRecorder!=null){
			mediaRecorder.reset();
			mediaRecorder.release();
			mediaRecorder = null;
			camera.lock();
		}
//		//burada FTP Upload yapýlmasý lazým
//		if(isUpload){
//			new FTPUpload().execute(filepath, PATH + 
//										"GPS_" + gpsString +
//										"TIME_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) +
//										".mp4");
//		}
	}
	
	public void startRecording(){
		mediaRecorder.start();
	}
	
	public void stopRecording(){
		mediaRecorder.stop();
	}
	
	public String getFilepath(){
		return filepath;
	}
}
