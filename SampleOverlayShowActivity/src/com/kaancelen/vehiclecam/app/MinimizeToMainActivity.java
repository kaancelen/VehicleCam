package com.kaancelen.vehiclecam.app;

import samples.jawsware.interactiveoverlay.SampleOverlayService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MinimizeToMainActivity extends Activity{

	private static final String TAG = "MinimizeToMainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "protected void onCreate(Bundle savedInstanceState)");
		//SampleOverlayViewService'i kapat ve minimize durumu bitsin
		SampleOverlayService.stop();
		//þimdi MainActivity baþlat
		startActivity(new Intent(this, MainActivity.class));
		//kendini kapat
		finish();
	}
	
}
