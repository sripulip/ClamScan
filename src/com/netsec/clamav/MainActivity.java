package com.netsec.clamav;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	final int ACTIVITY_CHOOSE_FILE = 1;
	static int uploadResponseCode = 0;
	String selectedFilePath = null;

	Button fileSelectorButton;
	TextView selectedFileTextView;
	Button uploadButton;
	ProgressDialog uploadScanDialog;
	ProgressDialog downloadScanDialog;
	TextView scanCompleteTextView;
	Button downloadButton;
	TextView downloadedTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize all the UI controls

		fileSelectorButton = (Button) findViewById(R.id.fileSelectorButton);
		selectedFileTextView = (TextView) findViewById(R.id.selectedFileTextView);
		uploadButton = (Button) findViewById(R.id.uploadButton);
		scanCompleteTextView = (TextView) findViewById(R.id.scanCompleteTextView);
		downloadButton = (Button) findViewById(R.id.downloadButton);
		downloadedTextView = (TextView) findViewById(R.id.downloadedSizeTextView);

		// Set the Event Listeners for all the UI controls

		fileSelectorButton.setOnClickListener(this);
		uploadButton.setOnClickListener(this);
		downloadButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// File selector
		case R.id.fileSelectorButton:
			Intent chooseFile;
			Intent intent;
			chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
			chooseFile.setType("file/*");
			intent = Intent.createChooser(chooseFile, "Choose a file");
			startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
			break;

		case R.id.uploadButton:
			// UploadtoServer Activity
			if (selectedFilePath != null) {
				uploadScanDialog = ProgressDialog.show(MainActivity.this, "",
						"Upload and Scan Process Started...", true);
				new Thread(new Runnable() {
					public void run() {
						//new thread to start the activity
						uploadResponseCode = UploadFileToServer
								.uploadFile((String) selectedFilePath);
						uploadScanDialog.dismiss();
						if (uploadResponseCode == 200) {
							runOnUiThread(new Runnable() {
								public void run() {
									scanCompleteTextView
											.setText("File Scan Complete!!");
									uploadResponseCode = 0;
									downloadButton.setVisibility(View.VISIBLE);
								}
							});
						} else {
							runOnUiThread(new Runnable() {
								public void run() {
									scanCompleteTextView
											.setText("Oops!! Error uploading file.");
									uploadResponseCode = 0;
								}
							});
						}
					}
				}).start();
			} else {
				Toast.makeText(MainActivity.this,
						"Please select a file to upload!!", Toast.LENGTH_LONG)
						.show();
			}
			break;

		case R.id.downloadButton:
			// Download form server activity
			downloadScanDialog = ProgressDialog.show(MainActivity.this, "",
					"Downloading Scan Results...", true);
			new Thread(new Runnable() {
				//new thread to start the activity
				public void run() {
					DownLoadFileFromServer.downloadFile();
					downloadScanDialog.dismiss();
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					File downloadedFile = new File(
							Environment.getExternalStorageDirectory()
									+ "/scan_result.txt");
					intent.setData(Uri.fromFile(downloadedFile));
					startActivity(intent);
					reset();
				}
			}).start();
			break;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ACTIVITY_CHOOSE_FILE: {
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				selectedFilePath = getRealPathFromURI(uri);
				selectedFileTextView.setText("Selected File: "
						+ selectedFilePath);
			}
		}
		}
	}

	// Get the absolute path for all the files on the device
	@SuppressLint("InlinedApi")
	public String getRealPathFromURI(Uri contentUri) {
		try {
			String[] proj = { MediaStore.Files.FileColumns.DATA };
			Cursor cursor = getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} catch (Exception e) {
			return contentUri.getPath();
		}
	}

	// reset all the controls to null
	public void reset() {
		runOnUiThread(new Runnable() {
			public void run() {
				downloadButton.setVisibility(View.GONE);
				selectedFilePath = null;
				selectedFileTextView.setText(null);
				scanCompleteTextView.setText(null);
			}
		});
	}
}
