package com.freshplanet.ane.AirVideo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.AsyncTask;
import android.util.Log;

public class CreateFileTask extends AsyncTask<byte[], Integer, String> {

	private static String TAG = "CreateFileTask";
	
	private static String mFileName = null;
	private String mFilePath = null;
	private int mTimePosition = 0;
	private File mOutputDir = null;
	
	public void setParams(int position, int timePosition, File outputDir)
	{
		this.mTimePosition = timePosition;
		mOutputDir = outputDir;
	}
	
	public String getPreviousFileName()
	{
		return mFileName;
	}
	
	
	@Override
	protected String doInBackground(byte[]... inputs) {

		if (inputs == null || inputs.length == 0)
		{
			Log.d(TAG, "param is null");
			return null;
		}
		
		byte[] bInput = inputs[0];
		
		if (bInput == null)
		{
			Log.d(TAG, "stream is null");
			return null;
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(bInput);
		
		File tempFile;
		try {
			tempFile = File.createTempFile("video", ".mp4", mOutputDir);
		} catch (IOException e1) {
			Log.e(TAG, "couldn't create tmp file");
			e1.printStackTrace();
			return null;
		}
		
		mFilePath = tempFile.getAbsolutePath();
		mFileName = tempFile.getName();

	    FileOutputStream out = null;
		try {
			out = new FileOutputStream(tempFile);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "file out not found");
			e.printStackTrace();
		}   
		
	    byte buf[] = new byte[16384];
	    do {
	        int numread = -1;
			try {
				numread = input.read(buf);
			} catch (IOException e) {
				Log.e(TAG, "couldn't read");
				e.printStackTrace();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
	        if (numread <= 0) break;   
	        try {
				out.write(buf, 0, numread);
			} catch (IOException e) {
				Log.e(TAG, "couldn't write");
				e.printStackTrace();
			}
	    } while (input != null);

		if (out != null)
		{
			try {
				out.close();
			} catch (IOException e) {
				Log.e(TAG, "couldn't close");
				e.printStackTrace();
			}
		}
		
		return null;
	}

	@Override
    protected void onPostExecute(String result) {
		Log.d(TAG, "setting the video param");
		try
		{
			Extension.context.setVideoPath(mFilePath);
			if (mTimePosition > 0)
			{
				Extension.context.startVideo(mTimePosition);
				Extension.context.makeContainerVisible();
			}
			Extension.context.createPlayer();
			if (android.os.Build.VERSION.SDK_INT < 14)
			{
				Log.d(TAG, "froyo detected");
				Extension.context.makeContainerInvisible();
				Extension.context.dispatchStatusEventAsync("READY_TO_DISPLAY", "OK");
			}
		} catch (Exception e)
		{
			Log.e(TAG, "exception occured");
			e.printStackTrace();
		}
	}

	
}
