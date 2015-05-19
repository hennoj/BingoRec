package com.example.bingorec;

import java.io.File;
import java.io.FileOutputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/*
 * copy from http://www.vogella.com/tutorials/AndroidCamera/article.html
 * 
 * */

public class TakePhotoAndEdit extends Activity
{
	
	Button takeImageButton;
	Button doRecognizeButton;
	Button newPictureButton;
	ImageView helperImage;
	
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_TAKE_PHOTO = 1;
	
	

	private String getImageFileDir()
	{
		return this.getExternalFilesDir(null).getAbsolutePath();
	}
	
	private File getImageFile()
	{
	    String storageDir = getImageFileDir();
	    File image = new File(storageDir,"bingo.jpg");
	    return image;
	}
	
	
	
	  
	  private SurfaceView the_real_preview;
	  private MySurfaceView the_modified_preview;

	  
	  @Override
	  public void onCreate(Bundle savedInstanceState) 
	  {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.takephotolayout);
	    
	    the_real_preview = (SurfaceView)findViewById(R.id.the_real_preview);
    	the_modified_preview = (MySurfaceView)findViewById(R.id.the_modified_preview);

    	the_modified_preview.setZOrderOnTop(true);
    	the_modified_preview.setZOrderMediaOverlay(true);
    	
    	the_modified_preview.setPreviewDisplay(the_real_preview.getHolder());
    	
	    
	    
	    View decorView = getWindow().getDecorView();
	    // Hide the status bar.
	    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
	    decorView.setSystemUiVisibility(uiOptions);
	    
	    ActionBar actionBar = getActionBar();
	    actionBar.hide();
	    
	    takeImageButton = (Button)findViewById(R.id.captureFront);
	    doRecognizeButton = (Button)findViewById(R.id.do_recognize_button);
	    newPictureButton = (Button)findViewById(R.id.take_new_picture_button);
	    
	    helperImage = (ImageView)findViewById(R.id.helper_parsed_image);
	    
	    takeImageButton.setOnClickListener(new TheButtonClick());
	    doRecognizeButton.setOnClickListener(new TheButtonClick());
	    newPictureButton.setOnClickListener(new TheButtonClick());
	    
	  }

	private class TheButtonClick implements View.OnClickListener
    {

    	public void onClick(View v)
    	{

    		if(v == takeImageButton)
    		{
    			the_modified_preview.getCamera().autoFocus(new Focus());
    			the_modified_preview.getCamera().takePicture(null, null,
    		    		new PhotoHandler(getApplicationContext()) );
    		}
    		else if(v == doRecognizeButton)
    		{
    			File imgFile = getImageFile();
    	    	String imgFileName = imgFile.getAbsolutePath();
    	    	
    	    	try
    	    	{
    	    		AsyncTask task = new ImageRecognizer().execute(imgFile.getAbsolutePath(),getImageFileDir());
    	    	}
    	    	catch(Exception e)
    	    	{
    	    		throw new RuntimeException(e);
    	    	}
    		}
    		else if(v == newPictureButton)
    		{
    			helperImage.setVisibility(View.INVISIBLE);
    			the_modified_preview.getCamera().startPreview();
    			
    		}
    		
    		
    	}
    	
    }
	  

	  

	  
	
	
	
	// http://stackoverflow.com/questions/20478765/how-to-get-the-correct-orientation-of-the-image-selected-from-the-default-image
	public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) 
	{

	    
        Matrix matrix = new Matrix();
        switch (orientation)
        {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
           case ExifInterface.ORIENTATION_ROTATE_90:
               matrix.setRotate(90);
               break;
           case ExifInterface.ORIENTATION_TRANSVERSE:
               matrix.setRotate(-90);
               matrix.postScale(-1, 1);
               break;
           case ExifInterface.ORIENTATION_ROTATE_270:
               matrix.setRotate(-90);
               break;
           default:
               return bitmap;
        }
	        
        try 
        {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e)
        {
            throw new RuntimeException(e);
            
        }
	    
	}
	
	
	
	public int calculateInSampleSize(
	        BitmapFactory.Options options, int reqWidth, int reqHeight) 
	{
	   // Raw height and width of image
	   int height = options.outHeight;
	   int width = options.outWidth;
	   int inSampleSize = 1;

	   if (height > reqHeight || width > reqWidth)
	   {

	      // Calculate ratios of height and width to requested height and width
	      int heightRatio = (int) Math.floor((float) height / (float) reqHeight);
	      int widthRatio = (int) Math.floor((float) width / (float) reqWidth);

	      // Choose the smallest ratio as inSampleSize value, this will guarantee
	      // a final image with both dimensions larger than or equal to the
	      // requested height and width.
	      inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
	   }

	   return inSampleSize;
	}
	
	
	
	
	
	
	private class ImageParserTask extends AsyncTask<String,Integer,String>
	{
		
		String imageFileDir = "";
		protected String doInBackground(String ... imgName)
		{
			try
			{
				imageFileDir = imgName[1];
				ImageParser parser = new ImageParser(imgName[1]);
				parser.parse(imgName[0]);
	    		
	    		return "";
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
    			throw new RuntimeException(e);
			}
			
		}
		
		protected void onPostExecute(String result) 
    	{
			Bitmap helpBitmap = BitmapFactory.decodeFile(imageFileDir+"/bingo_final_digits.png");
			
			helperImage.setImageBitmap(helpBitmap);
			helperImage.setVisibility(View.VISIBLE);
    	}
		
		
	}
	
	
	private class ImageRecognizer extends AsyncTask<String, Integer, String>
    {
    	protected String doInBackground(String ... imgName)
    	{
    		
    		try
    		{
    			
    			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(TakePhotoAndEdit.this);
    			
    			String recognizer = settings.getString("recognizer", "Tesseract");
    			
    			recognizer = recognizer.toLowerCase();
    			
    			ImageParser parser = new ImageParser(imgName[1]);
    			
	    		
    			
	    		String res = "";
    			if(recognizer.equals("tesseract"))
    			{
    				System.out.println("Tesseract recognizer");
    	    		res =  parser.TesseractRecognize();
    	    		parser.makeText();
    			}
    			else if(recognizer.equals("ann"))
    			{
    				System.out.println("ANN recognizer");
    				res = parser.ANNrecognize();
    			}
    			else if(recognizer.equals("multinet"))
    			{
    				System.out.println("multinet recognizer");
    				res = parser.multinetRecognize();
    				
    			}
    			
    			
    			return res;
    		}
    		catch(Exception e)
    		{
    			System.out.println(e.getMessage());
    			throw new RuntimeException(e);
    			
    		}
    	}

    	protected void onProgressUpdate(Integer ... progress)
    	{

    	}

    	protected void onPostExecute(String result) 
    	{
    		
    		// TakePhotoAndEdit.this.finish();
    		
    		if(result.isEmpty())
    		{
    			result = "aaabbcc";
    		}
    		
    		Toast.makeText(TakePhotoAndEdit.this, "image parsed:"+result, Toast.LENGTH_LONG).show();
    		Intent intent = new Intent(TakePhotoAndEdit.this,RecognizeResult.class);
    		intent.putExtra("bingo", result);
    		TakePhotoAndEdit.this.startActivity(intent);
    		
    	}
    	
    	
    	 
    	
    }
	
	
	
	private class Focus implements Camera.AutoFocusCallback
	{
		public void onAutoFocus(boolean success, Camera camera)
		{
			camera.takePicture(null, null,
		    		new PhotoHandler(getApplicationContext()) );
		}
	}
	
	private class PhotoHandler implements PictureCallback
	{

		  private Context context;

		  public PhotoHandler(Context context) 
		  {
		    this.context = context;

		  }

		  @Override
		  public void onPictureTaken(byte[] data, Camera camera) 
		  {

		    File pictureFileDir = new File(getImageFileDir());

		    if (!pictureFileDir.exists() && !pictureFileDir.mkdirs())
		    {

		      // Log.d(MakePhotoActivity.DEBUG_TAG, "Can't create directory to save image.");
		      Toast.makeText(context, "Can't create directory to save image.",
		          Toast.LENGTH_LONG).show();
		      return;

		    }

		    
		    File pictureFile =  getImageFile();

		    try {
		      FileOutputStream fos = new FileOutputStream(pictureFile);
		      fos.write(data);
		      fos.close();
		      imageTaken();
		    } catch (Exception error) {
		      // Log.d(MakePhotoActivity.DEBUG_TAG, "File" + filename + "not saved:  + error.getMessage());
		      Toast.makeText(context, "Image could not be saved.",
		          Toast.LENGTH_LONG).show();
		    }
		  }

		  
	}
	
	public void imageTaken()
	{
		File imgFile = getImageFile();
    	String imgFileName = imgFile.getAbsolutePath();
    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inJustDecodeBounds = true;
    	BitmapFactory.decodeFile(imgFileName,options);
    	
    	options.inSampleSize = calculateInSampleSize(options,350,350);
    	options.inJustDecodeBounds = false;
    	Bitmap myBitmap = BitmapFactory.decodeFile(imgFileName,options);
    	try
    	{
    		ExifInterface exif = new ExifInterface(imgFileName);  
    		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED); 
    		myBitmap = rotateBitmap(myBitmap,orientation);
    		
    		FileOutputStream out = new FileOutputStream(imgFileName);
    	    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
    		
    		AsyncTask task = new ImageParserTask().execute(imgFile.getAbsolutePath(),getImageFileDir());
    	}
    	catch(Exception e)
    	{
    		throw new RuntimeException(e);
    	}
    	
    	
    	
    }

}
