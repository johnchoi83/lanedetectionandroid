package org.siprop.opencv;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class TestJniCall extends Activity {
	private static final String TAG = "CameraDemo";
	Camera camera;
	Preview preview;
	Button buttonClick;
	
	private Handler mHandler = new Handler();
	
	private long mStartTime;
	
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
		       final long start = mStartTime;
		       long millis = SystemClock.uptimeMillis() - start;
		       int seconds = (int) (millis / 1000);
		       int minutes = seconds / 60;
		       seconds     = seconds % 60;
		       
		       preview.camera.takePicture(shutterCallback, rawCallback,jpegCallback);
		     
		       mHandler.postDelayed(this, 4 * 1000);
		   }
		};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		preview = new Preview(this);
		((FrameLayout) findViewById(R.id.preview)).addView(preview);

		buttonClick = (Button) findViewById(R.id.buttonClick);
		buttonClick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			       if (mStartTime == 0L) {
			            mStartTime = System.currentTimeMillis();
			            mHandler.removeCallbacks(mUpdateTimeTask);
			            mHandler.postDelayed(mUpdateTimeTask, 100);
			       }
			}
		});

		Log.e(TAG, "onCreate'd");
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.e(TAG, "onShutter'd");
		}
	};

	/** Handles data for raw picture */
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.e(TAG, "onPictureTaken - raw");
		}
	};
	
	
	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			try {
				// write to local sandbox file system
				// outStream =
				// CameraDemo.this.openFileOutput(String.format("%d.jpg",
				// System.currentTimeMillis()), 0);
				// Or write to sdcard
				outStream = new FileOutputStream(String.format(
						"/sdcard/%d.jpg", System.currentTimeMillis()));
				outStream.write(data);
				outStream.close();
				
				int [] integers= new int[data.length];
			    for (int k = 0; k < data.length; k++)
			    	integers[k] = (int) data[k];
			    
            	OpenCV opencv = new OpenCV();
            	if(opencv.testString(data, preview.camera.getParameters().getPreviewSize().width, preview.camera.getParameters().getPreviewSize().height))
            		Log.e(TAG, "TRUE");
            	else
            		Log.e(TAG, "FALSE");
				
				preview.camera.startPreview();
				
				Log.e(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			Log.e(TAG, "onPictureTaken - jpeg");
		}
	
	};

}