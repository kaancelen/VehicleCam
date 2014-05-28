package com.kaancelen.vehiclecam.helpers;

import com.kaancelen.vehiclecam.errors.LogErrors;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

public class CameraHelper {

	/** Check if this device has a camera */
	public static boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	    	return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	public static Camera getCameraInstance(int CameraID){
		Camera c = null;
		try {
			c = Camera.open(CameraID);
		} catch (Exception e) {
			Log.e(LogErrors.Tags.CAMERA_EXCEPTION, e.getMessage());
		}
		
		return c;
	}
	
}
