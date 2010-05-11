package org.siprop.opencv;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TestJniCall extends Activity {
    /** Called when the activity is first created. */
    
    public OpenCV openCV = new OpenCV();
    
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        
        String testString = openCV.testString("Hello OPENCV");
        
        tv.setText(testString);
        setContentView(tv);
    }
}