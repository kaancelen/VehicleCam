package com.kaancelen.vehiclecam.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class TempActivity extends Activity{

	private static final String TAG = "TempActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		//go back main activity
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}
