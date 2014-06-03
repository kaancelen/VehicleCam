package com.kaancelen.vehiclecam.ftpupload;

public class FTPAccount {

	private String url;
	private String username;
	private String password;
	
	public FTPAccount(String url, String username, String password) {
		super();
		this.url = url;
		this.username = username;
		this.password = password;
	}
	public static FTPAccount fromString(String string){
		String[] elems = string.split(";");
		return new FTPAccount(elems[0], elems[1], elems[2]);
	}
	public String getUrl() {
		return url;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	@Override
	public String toString() {
		return url+";"+username+";"+password;
	}
}
