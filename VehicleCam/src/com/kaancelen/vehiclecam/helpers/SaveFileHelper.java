package com.kaancelen.vehiclecam.helpers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import com.kaancelen.vehiclecam.app.MainActivity;
import com.kaancelen.vehiclecam.constants.Constants;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class SaveFileHelper {
	
	/** Create a file Uri for saving an image or video */
	public static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), Constants.APP_NAME);
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d(Constants.APP_NAME, "failed to create directory");
	            return null;
	        }
	    }
	    
	    //check how many file in directory?
	    //if more than MAX_FILE_NUMBER then delete until get it under the MAX_FILE_NUMBER
	    File[] files = mediaStorageDir.listFiles();
	    Arrays.sort(files);//sort etmezsen yeni dosyayý senin az önce sildiðinin yerine yazar.
//	    Log.d("Number of files", files.length+"");
//	    for (File file : files) {
//			Log.d("FILENAME", file.getName());
//		}
	    //hafýzayý açmak için sil
	    if(files.length > Constants.MAX_FILE_NUMBER){
	    	files[0].delete();
	    }
	    
	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == Constants.MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == Constants.MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
}
