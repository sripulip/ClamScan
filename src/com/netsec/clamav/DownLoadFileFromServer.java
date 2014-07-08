package com.netsec.clamav;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Environment;
import android.util.Log;

public class DownLoadFileFromServer {
	/*The address to server from where the scan result is downloaded */
	static String downLoadFilePath = "http://192.168.1.115/test/scan_result";
	
	/*Function which is used to intitiate connection and complete file download*/
	public static void downloadFile() {

		try {
			/*url object which contains the file path for download of the scan result*/
			URL url = new URL(downLoadFilePath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");//GET Method used for file download
			conn.setDoOutput(true);
			conn.connect();

			File SDCardRoot = Environment.getExternalStorageDirectory();
			File file = new File(SDCardRoot, "scan_result.txt");
			FileOutputStream fileOutput = new FileOutputStream(file);
			/*Uses the connection to receive data using inputStream object*/
			InputStream inputStream = conn.getInputStream();

			byte[] buffer = new byte[1024];
			int bufferLength = 0;

			while ((bufferLength = inputStream.read(buffer)) > 0) {
				fileOutput.write(buffer, 0, bufferLength);
			}
			fileOutput.close();
		/*Catch blocks to handle exceptions if there is error in file download*/
		} catch (final MalformedURLException ex) {
			ex.printStackTrace();
			Log.e("Download File", "error: " + ex.getMessage(), ex);
		} catch (final IOException e) {
			e.printStackTrace();
			Log.e("Download File", "error: " + e.getMessage(), e);
		} catch (final Exception e) {
			e.printStackTrace();
			Log.e("Download File", "error: " + e.getMessage(), e);
		}
	}
}
