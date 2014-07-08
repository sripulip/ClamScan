package com.netsec.clamav;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class UploadFileToServer {

	/* The address to server where the PHP file is stored */
	static String upLoadServerUri = "http://192.168.1.115/test/upload_file.php";
	String test = null;

	/* Function which is used to create connection and file upload */
	public static int uploadFile(String sourceFileUri) {
		/* File path of the file which is going to be scanned */
		String filePath = sourceFileUri;

		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(sourceFileUri);

		try {
			FileInputStream fileInputStream = new FileInputStream(sourceFile);
			URL url = new URL(upLoadServerUri);
			/* Using HttpURLConnection to create a connection to the server */
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST"); // POST Method used for file upload
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("uploaded_file", filePath);
			/* Uses the connection to send data using DataOutputStream object */
			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
					+ filePath + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize]; //Buffer to store the outgoing contents

			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {     
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			MainActivity.uploadResponseCode = conn.getResponseCode();  //Get the respnse code form the server
			String serverResponseMessage = conn.getResponseMessage();
			Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage
					+ ": " + MainActivity.uploadResponseCode);
			fileInputStream.close();
			dos.flush();
			dos.close();
			/*
			 * Catch blocks to handle exceptions if there is error in file
			 * upload
			 */
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			Log.e("Upload file to server", "error: " + ex.getMessage(), ex);

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Upload file to server Exception",
					"Exception : " + e.getMessage(), e);
		}
		/* Returns control back to Main activity of the application */
		return MainActivity.uploadResponseCode;
	}

}
