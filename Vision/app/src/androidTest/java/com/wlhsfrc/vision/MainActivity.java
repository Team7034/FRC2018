package com.wlhsfrc.vision;

import android.app.Activity;

public class MainActivity extends Activity {
	
	private CameraBridgeViewBase mOpenCvCameraView;
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	    @Override
	    public void onManagerConnected(int status) {
	        switch (status) {
	            case LoaderCallbackInterface.SUCCESS:
	            {
	                Log.i(TAG, "OpenCV loaded successfully");
	                mOpenCvCameraView.enableView();
	            } break;
	            default:
	            {
	                super.onManagerConnected(status);
	            } break;
	        }
	    }
	};
	
	@Override
	public void onResume()
	{
	    super.onResume();
	    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    Log.i(TAG, "called onCreate");
	    super.onCreate(savedInstanceState);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    setContentView(R.layout.HelloOpenCvLayout);
	    mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
	    mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
	    mOpenCvCameraView.setCvCameraViewListener(this);
	}
	@Override
	public void onPause()
	{
	    super.onPause();
	    if (mOpenCvCameraView != null)
	        mOpenCvCameraView.disableView();
	}
	public void onDestroy() {
	    super.onDestroy();
	    if (mOpenCvCameraView != null)
	        mOpenCvCameraView.disableView();
	}
	public void onCameraViewStarted(int width, int height) {
	}
	public void onCameraViewStopped() {
	}
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
	    return inputFrame.rgba();
	}
}
