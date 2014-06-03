package com.kaancelen.vehiclecam.ftpupload;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import android.os.AsyncTask;
import android.util.Log;

public class FTPUpload extends AsyncTask<String, Void, Void>{

	private static final String SERVER_NAME = FTPConstants.DEFAULT_FTP_URL;
	private static final String USER_NAME = FTPConstants.DEFAULT_FTP_USERNAME;
	private static final String PASSWORD = FTPConstants.DEFAULT_FTP_PASSWORD;
	
	/**
	 * Should give 2 params
	 * params[0] => file path wants to be uploaded
	 * params[1] => file path wants to be saved 
	 */
	public FTPUpload() {
		//no implementation need
	}
	
	@Override
	protected Void doInBackground(String... params) {
		FTPClient ftpclient = null;
		
		try {
			if(params.length != 2)//if params is not 5 then i cannot use that method
				throw new IOException("params.length should be 5");
			
			ftpclient = new FTPClient();
			ftpclient.connect(InetAddress.getByName(SERVER_NAME));//connect
			if(!ftpclient.login(USER_NAME, PASSWORD))//login
				throw new IOException("Cannot Login");
			
			ftpclient.enterLocalPassiveMode();//for 0 bytes issues
			ftpclient.setFileType(FTP.BINARY_FILE_TYPE);//because we upload videos and videos are binary files
			
			FileInputStream in = new FileInputStream(params[0]);//input stream for upload
			boolean result = ftpclient.storeFile(params[1], in);//upload file
			in.close();//close fileInputStream
			if(!result)//if cannot upload
				throw new IOException("Cannot upload file");
			
		} catch (SocketException e) {
			Log.e("USER:SocketException", e.getMessage());
		} catch (UnknownHostException e) {
			Log.e("USER:UnknownHostException", e.getMessage());
		} catch (IOException e) {
			Log.e("USER:IOException", e.getMessage());
		} finally{
			try {
				ftpclient.logout();
				ftpclient.disconnect();
			} catch (IOException e) {
				Log.e("USER:IOException", e.getMessage());
			}
		}
		return null;
	}

}
