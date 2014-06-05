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

	private FTPAccount ftpAccount;
	
	/**
	 * Should give 2 params
	 * params[0] => file path wants to be uploaded
	 * params[1] => file path wants to be saved 
	 */
	public FTPUpload(FTPAccount ftpAccount) {
		this.ftpAccount = ftpAccount;
	}
	
	@Override
	protected Void doInBackground(String... params) {
		FTPClient ftpclient = null;
		
		try {
			if(params.length != 2)//if params is not 5 then i cannot use that method
				throw new IOException("params.length should be 5");
			
			ftpclient = new FTPClient();
			ftpclient.connect(ftpAccount.getUrl() ,21);//connect
			if(!ftpclient.login(ftpAccount.getUsername(), ftpAccount.getPassword()))//login
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
			Log.e("USER:UnknownHostException", ftpAccount.getUrl() + " " + ftpAccount.getUsername() + " " + ftpAccount.getPassword());
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
