package com.example.bingorec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;


// Class copied from http://stackoverflow.com/questions/6478375/how-can-i-manipulate-the-camera-preview

public class MySurfaceView extends SurfaceView implements Callback, Camera.PreviewCallback
{

private static final String TAG = "MySurfaceView";

private int width;
private int height;

private SurfaceHolder mHolder;

private SurfaceHolder realHolder;

private Camera mCamera;
private int[] rgbints;

private boolean isPreviewRunning = false; 

private int mMultiplyColor;

private ImageParser parser;

public Camera getCamera() { return mCamera; }

public MySurfaceView(Context context, AttributeSet attrs) {
    super(context, attrs);

    mHolder = getHolder();
    mHolder.addCallback(this);
    mMultiplyColor = Color.GREEN;
    parser = new ImageParser("");
}

private int findFrontFacingCamera()
{
  int cameraId = -1;
  // Search for the front facing camera
  int numberOfCameras = Camera.getNumberOfCameras();
  for (int i = 0; i < numberOfCameras; i++)
  {
    CameraInfo info = new CameraInfo();
    Camera.getCameraInfo(i, info);
    if (info.facing == CameraInfo.CAMERA_FACING_BACK)
    {
      
      cameraId = i;
      break;
    }
  }
  return cameraId;
}

// @Override
// protected void onDraw(Canvas canvas) {
// Log.w(this.getClass().getName(), "On Draw Called");
// }

@Override
public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	Camera.Parameters params = mCamera.getParameters();
    List<Camera.Size> sizes = params.getSupportedPreviewSizes();
    Camera.Size size = sizes.get(0);
    params.setPreviewSize(size.width,size.height);
    width = size.width;
    height = size.height;
  
    
    mCamera.setParameters(params);
    
}

@Override
public void surfaceCreated(SurfaceHolder holder) {
    synchronized (this) {
        if (isPreviewRunning)
            return;

        this.setWillNotDraw(false); // This allows us to make our own draw calls to this canvas


        mCamera = Camera.open(findFrontFacingCamera());
        isPreviewRunning = true;
        Camera.Parameters p = mCamera.getParameters();
        
        
        List<Camera.Size> szs =  p.getSupportedPreviewSizes();
        
        Size size = szs.get(0);
        p.setPreviewSize(size.width, size.height);
        
        width = size.width;
        height = size.height;
        p.setPreviewFormat(ImageFormat.NV21);
        
        mCamera.setParameters(p);

        rgbints = new int[width * height];

        try { mCamera.setPreviewDisplay(realHolder); } catch (IOException e)
        { Log.e("Camera", "mCamera.setPreviewDisplay(holder);"); }

        mCamera.startPreview();
        // mCamera.setPreviewCallback(this);

    }
}

public void setPreviewDisplay(SurfaceHolder holder)
{
	realHolder = holder;
	 
}

@Override
public void surfaceDestroyed(SurfaceHolder holder) {
    synchronized (this) {
        try {
            if (mCamera != null) {
                //mHolder.removeCallback(this);
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                isPreviewRunning  = false;
                mCamera.release();
            }
        } catch (Exception e) {
            Log.e("Camera", e.getMessage());
        }
    }
}

@Override
public void onPreviewFrame(byte[] data, Camera camera) {
    // Log.d("Camera", "Got a camera frame");
    if (!isPreviewRunning)
        return;

    Canvas canvas = null;

    if (mHolder == null) {
        return;
    }

    try {
        synchronized (mHolder) {
            canvas = mHolder.lockCanvas(null);
             int canvasWidth = canvas.getWidth();
             int canvasHeight = canvas.getHeight();

            // YuvImage yuv = new YuvImage(data, ImageFormat.NV21, width, height, null);
            // ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            // yuv.compressToJpeg(new Rect(0,0,width,height), 100, out);
           
            
            // byte[] bytes = out.toByteArray();
            // Bitmap bitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            
            Bitmap bitmap = parser.preview(data, width, height);
            // draw the decoded image, centered on canvas
            
            canvas.drawBitmap(bitmap, null, canvas.getClipBounds(), null);
           
            //bitmap.getPixels(rgbints, 0, 0, 0, 0, width, height);
            //canvas.drawBitmap(rgbints, 0, width, canvasWidth-((width+canvasWidth)>>1), canvasHeight-((height+canvasHeight)>>1), width, height, false, null);
            
            // use some color filter
            // canvas.drawColor(mMultiplyColor, Mode.MULTIPLY);

        }
    }  catch (Exception e){
        e.printStackTrace();
    } finally {
        // do this in a finally so that if an exception is thrown
        // during the above, we don't leave the Surface in an
        // inconsistent state
        if (canvas != null) {
            mHolder.unlockCanvasAndPost(canvas);
        }
    }
}



}