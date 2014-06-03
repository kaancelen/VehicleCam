package samples.jawsware.interactiveoverlay;

/*
Copyright 2011 jawsware international
*/

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class SampleOverlayHideActivity extends Activity {

	private static final String TAG = "SampleOverlayHideActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "protected void onCreate(Bundle savedInstanceState)");
		//minimize penceresini kapat
		SampleOverlayService.stop();
		//kendini kapat
		finish();
		
	}
}
