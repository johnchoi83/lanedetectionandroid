package org.siprop.opencv;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
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
		       
		       if(preview.camera != null) {
		    	   preview.camera.takePicture(shutterCallback, rawCallback,jpegCallback);
		     
		    	   mHandler.postDelayed(this, 20 * 1000);
		       }
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

			if(data == null)
				Log.e(TAG, "onPictureTaken - raw is null");
			else
				Log.e(TAG, "onPictureTaken - raw is not null");

			/*if(data != null){
				Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
				 
	        	OpenCV opencv = new OpenCV();
	        	if(opencv.testString(bmToByteArray(bmp), preview.camera.getParameters().getPreviewSize().width, preview.camera.getParameters().getPreviewSize().height))
	        		//opencv.saveImageCv( "/sdcard/odataut.jpg" , preview.camera.getParameters().getPreviewSize().width, preview.camera.getParameters().getPreviewSize().height))
	        		Log.e(TAG, "TRUE");
	        	else
	        		Log.e(TAG, "FALSE");
				
				preview.camera.startPreview();
				
				Log.e(TAG, "onPictureTaken - wrote bytes: " + data.length);
			}*/
		}
	};
	
	
	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.e(TAG, "onPictureTaken - jpeg");
            OpenCV opencv = new OpenCV();        
            if(data == null)
				Log.e(TAG, "onPictureTaken - jpeg is null");
			else
				Log.e(TAG, "onPictureTaken - jpeg is not null");
            /*
            Bitmap faceDetectBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            int width = faceDetectBitmap.getWidth(),
            height = faceDetectBitmap.getHeight();
            */
            
            //Load Static Image file from Android Camera
            Bitmap faceDetectBitmap = BitmapFactory.decodeFile("/sdcard/roadTest.jpg");
            int width = faceDetectBitmap.getWidth(),
            height = faceDetectBitmap.getHeight(); 
            
			if(data == null){
				Log.e(TAG, "testFile - null bitmap");
			}
			
            //Scale the bitmap down by 1/5 because the camera kept running out of memory when we allocated the int array below
			faceDetectBitmap = Bitmap.createScaledBitmap(faceDetectBitmap, width/2, height/2, false);
			if(faceDetectBitmap == null){
				Log.e(TAG, "onPictureTaken - null bitmap");
	            
			}
            width = faceDetectBitmap.getWidth();
            height = faceDetectBitmap.getHeight();
            
            //FOR TESTING ONLY (write the file to the sdcard on the Java side
            FileOutputStream outStream = null;
			try {
				outStream = new FileOutputStream(String.format("/sdcard/out.jpg", System.currentTimeMillis()));
				faceDetectBitmap.compress(CompressFormat.JPEG, 100, outStream);
				//outStream.write(data);
				outStream.close();
				Log.e(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			
            Log.e(TAG, ""+width + " "+height);  
            //Allocate the int array for the OpenCV side
            int pixels[] = new int[width * height];
            faceDetectBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            
            //Call OpenCV passing int the int array of pixels
            if (opencv.findLines(pixels, width, height))            		
            	Log.e(TAG, "TRUE");
        	else
        		Log.e(TAG, "FALSE");
			
			Log.e(TAG, "onPictureTaken - jpeg");
			preview.camera.startPreview();
		}
	
	};	 
}