package com.wlhsfrc.vision;

import com.wlhsfrc.vision.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import com.example.test.util.SystemUiHider;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity implements CvCameraViewListener2 {
	static private final String TAG = "MainActivity";
	private MainView mCamView;
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after user
	 * interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	    @Override
	    public void onManagerConnected(int status) {
	        switch (status) {
	            case LoaderCallbackInterface.SUCCESS:
	            {
	                Log.i(TAG, "OpenCV loaded successfully");
	                mCamView.initFlash();
	                mCamView.enableView();
	            } break;
	            default:
	            {
	                super.onManagerConnected(status);
	            } break;
	        }
	    }
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    Log.i(TAG, "called onCreate");
	    super.onCreate(savedInstanceState);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    setContentView(R.layout.activity_main);
	    final View contentView = findViewById(R.id.activity_main);
	    mCamView = (MainView) findViewById(R.id.activity_main);
	    mCamView.setVisibility(SurfaceView.VISIBLE);
	    mCamView.setCvCameraViewListener(this);
	    mCamView.setMaxFrameSize(400, 300);
	    //Log.i(TAG, "Before Flash Loaded");
	    //mCamView.flashlightOn();
	    //Log.i(TAG, "After Flash Loaded");
	    
	}
	@Override
	public void onPause()
	{
	    super.onPause();
	    if (mCamView != null)
	        mCamView.disableView();
	}
	public void onDestroy() {
	    super.onDestroy();
	    if (mCamView != null)
	        mCamView.disableView();
	}
	public void onCameraViewStarted(int width, int height) {
	}
	public void onCameraViewStopped() {
	}
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Mat view = inputFrame.gray();
		//Mat newView = Mat.zeros(view.size(),  view.type());
		//Toast.makeText(getApplication(), String.valueOf(view.type()), Toast.LENGTH_SHORT).show();
		view.convertTo(view, 0, 5, -600);
		/*for(int x = 0; x < view.cols(); x++) {
			for(int y = 0; y < view.rows(); y++) {
				if(x < view.cols()*.2) {
					if(view.get(y, x)  > .8) {
						
					}
				}
			}
		}*/
		mCamView.flashlightOn();
	    return view;
	}
	
	@Override
	public void onResume()
	{
	    super.onResume();
	    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
	}
}
