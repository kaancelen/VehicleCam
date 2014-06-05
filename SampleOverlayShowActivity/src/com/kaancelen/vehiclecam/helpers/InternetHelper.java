package com.kaancelen.vehiclecam.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetHelper {

	public static boolean hasInternetConnection(Context context){
		ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
		if(networkInfo == null)
			return false;
		if(!networkInfo.isConnectedOrConnecting())
			return false;
		return true;
	}
	
}
