package com.kaancelen.vehiclecam.errors;

public class LogErrors {

	public class Tags{
		public static final String NO_CAMERA = "NO_CAMERA";
		public static final String CAMERA_EXCEPTION = "CAMERA_EXCEPTION";
		public static final String SURFACE_COULDNT_CREATED = "SURFACE_COULDNT_CREATED";
		public static final String SURFACE_COULDNT_STARTED = "SURFACE_COULDNT_STARTED";
		public static final String IllegalStateException = "IllegalStateException";
		public static final String IOException = "IOException";
		public static final String EXCEPTION = "EXCEPTION";
		public static final String ASYN_TASK_ERROR = "ASYN_TASK_ERROR";
		public static final String RECORDING_TASK = "Recording Task";
		public static final String STATUS = "APP STATUS";
		public static final String RECORDER_SERVICE = "Recorder Service";
	}
	
	public class Messages{
		public static final String NO_CAMERA = "This device don't have any camera";
		public static final String IS_RECORDING_TRUE = "isRecording = true; ";
		public static final String IS_RECORDING_FALSE = "isRecording = false; ";
		public static final String RECORD_START = "Record starting";
		public static final String RECORD_STOP = "Record stopped";
	}
}