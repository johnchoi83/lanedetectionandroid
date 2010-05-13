package org.siprop.opencv;

import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class Preview extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "Preview";

	SurfaceHolder mHolder;
	public Camera camera;

	Preview(Context context) {
		super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    //mHolder.setFixedSize(320, 240);
	}

	static public void toRGB565(byte[] yuvs, int width, int height, byte[] rgbs) {
	    //the end of the luminance data
	    final int lumEnd = width * height;
	    //points to the next luminance value pair
	    int lumPtr = 0;
	    //points to the next chromiance value pair
	    int chrPtr = lumEnd;
	    //points to the next byte output pair of RGB565 value
	    int outPtr = 0;
	    //the end of the current luminance scanline
	    int lineEnd = width;

	    while (true) {

	        //skip back to the start of the chromiance values when necessary
	        if (lumPtr == lineEnd) {
	            if (lumPtr == lumEnd) break; //we've reached the end
	            //division here is a bit expensive, but's only done once per scanline
	            chrPtr = lumEnd + ((lumPtr  >> 1) / width) * width;
	            lineEnd += width;
	        }

	        //read the luminance and chromiance values
	        final int Y1 = yuvs[lumPtr++] & 0xff; 
	        final int Y2 = yuvs[lumPtr++] & 0xff; 
	        final int Cr = (yuvs[chrPtr++] & 0xff) - 128; 
	        final int Cb = (yuvs[chrPtr++] & 0xff) - 128;
	        int R, G, B;

	        //generate first RGB components
	        B = Y1 + ((454 * Cb) >> 8);
	        if(B < 0) B = 0; else if(B > 255) B = 255; 
	        G = Y1 - ((88 * Cb + 183 * Cr) >> 8); 
	        if(G < 0) G = 0; else if(G > 255) G = 255; 
	        R = Y1 + ((359 * Cr) >> 8); 
	        if(R < 0) R = 0; else if(R > 255) R = 255; 
	        //NOTE: this assume little-endian encoding
	        rgbs[outPtr++]  = (byte) (((G & 0x3c) << 3) | (B >> 3));
	        rgbs[outPtr++]  = (byte) ((R & 0xf8) | (G >> 5));

	        //generate second RGB components
	        B = Y2 + ((454 * Cb) >> 8);
	        if(B < 0) B = 0; else if(B > 255) B = 255; 
	        G = Y2 - ((88 * Cb + 183 * Cr) >> 8); 
	        if(G < 0) G = 0; else if(G > 255) G = 255; 
	        R = Y2 + ((359 * Cr) >> 8); 
	        if(R < 0) R = 0; else if(R > 255) R = 255; 
	        //NOTE: this assume little-endian encoding
	        rgbs[outPtr++]  = (byte) (((G & 0x3c) << 3) | (B >> 3));
	        rgbs[outPtr++]  = (byte) ((R & 0xf8) | (G >> 5));
	    }
	}
	static int v = 0;
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
		camera = Camera.open();
		try {
			camera.setPreviewDisplay(holder);

			camera.setPreviewCallback(new PreviewCallback() 
			{ 
	            public void onPreviewFrame(byte[] _data, Camera _camera) {   // TODO Do something with the preview image.  
	           /* 	 Log.i("bitmap1",_data.length+"");
	            	int abc[]=decodeYUV420SP(_data,_camera.getParameters().getPreviewSize().width,_camera.getParameters().getPreviewSize().height);
	            	OpenCV opencv = new OpenCV();
	            	if(opencv.testString(abc,_camera.getParameters().getPreviewSize().width,_camera.getParameters().getPreviewSize().height))
	            		Log.e(TAG, "TRUE");
	            	else
	            		Log.e(TAG, "FALSAE");*/
	            }
	            
	            public int[] decodeYUV420SP( byte[] yuv420sp, int width, int height) {
	            	final int frameSize = width * height;

	            	int rgb[]=new int[width*height];
	            	for (int j = 0, yp = 0; j < height; j++) {
	            		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
	            		for (int i = 0; i < width; i++, yp++) {
	            			int y = (0xff & ((int) yuv420sp[yp])) - 16;
	            			if (y < 0) y = 0;
	            			if ((i & 1) == 0) {
	            				v = (0xff & yuv420sp[uvp++]) - 128;
	            				u = (0xff & yuv420sp[uvp++]) - 128;
	            			}

	            			int y1192 = 1192 * y;
	            			int r = (y1192 + 1634 * v);
	            			int g = (y1192 - 833 * v - 400 * u);
	            			int b = (y1192 + 2066 * u);

	            			if (r < 0) r = 0; else if (r > 262143) r = 262143;
	            			if (g < 0) g = 0; else if (g > 262143) g = 262143;
	            			if (b < 0) b = 0; else if (b > 262143) b = 262143;

	            			rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 
	            					0xff00) | ((b >> 10) & 0xff);


	            		}
	            	}
	            	return rgb;
	            }
	      });
			    

				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				/*
				public void onPreviewFrame(byte[] data, Camera arg1) {
					Size size = arg1.getParameters().getPreviewSize();
					byte [] data2 = new byte[size.width * size.height * 3];
					
					//Preview.toRGB565(data, size.width , size.height ,data2 );
					
					FileOutputStream outStream = null;
					try {
						Log.e("Im here" , "msg");
						String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
						filepath+=String.format("/%d.jpg", System.currentTimeMillis());
						Log.e(TAG,filepath);
						//File f = new File(filepath);
						outStream = new FileOutputStream(filepath);
						Bitmap map = BitmapFactory.decodeByteArray(data, 0, data2.length);
						if(map == null)Log.e(TAG,"BMP NULL" );
						map.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
						//outStream.write(data2);

						Log.e("Im here" , "msg after");
						outStream.flush();
						outStream.close();
					
						Log.e(TAG, "onPreviewFrame - wrote bytes: "
								+ data.length);
					} catch (FileNotFoundException e) {
						Log.e(TAG, "FNF"+ e.getMessage());
					} catch (IOException e) {
						Log.e(TAG, "IOE"+e.getMessage());
					} finally {
					}
					Preview.this.invalidate();
				}
			});*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		camera.stopPreview();
		camera = null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		Camera.Parameters parameters = camera.getParameters();
		//parameters.setPreviewSize(320, 240);
        //parameters.setPictureSize(320, 240);
        parameters.setPictureFormat(PixelFormat.RGB_565);
		camera.setParameters(parameters);
		
		camera.startPreview();
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		Paint p = new Paint(Color.RED);
		Log.e(TAG, "draw");
		canvas.drawText("PREVIEW", canvas.getWidth() / 2,
				canvas.getHeight() / 2, p);
	}
}
