package samples.jawsware.interactiveoverlay;

/*
Copyright 2011 jawsware international
*/

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SampleOverlayShowActivity extends Activity {

	private static final String TAG = "SampleOverlayShowActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "protected void onCreate(Bundle savedInstanceState)");
		//minimize penceresini aç
		startService(new Intent(this, SampleOverlayService.class));
		
		finish();
		
	}
}
