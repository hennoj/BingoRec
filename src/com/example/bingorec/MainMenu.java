package com.example.bingorec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainMenu extends Activity
{

	
	Button captureButton;
	Button trainButton;
	Button testTrainButton;
	Button enterNumbersButton;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		captureButton = (Button)findViewById(R.id.capture_button);

		captureButton.setOnClickListener(new MainMenuButtonClick());
		
		trainButton = (Button)findViewById(R.id.train_button);
		
		trainButton.setOnClickListener(new MainMenuButtonClick());
		
		testTrainButton = (Button) findViewById(R.id.test_training_button);
		testTrainButton.setOnClickListener(new MainMenuButtonClick());
		
		enterNumbersButton = (Button)findViewById(R.id.enter_numbers_menu_button);
		enterNumbersButton.setOnClickListener(new MainMenuButtonClick());
		
		
		String dir = this.getExternalFilesDir(null).getAbsolutePath();
		
		String tessDir = dir + File.separator + "tessdata";
		File theDir = new File(tessDir);
		if(!theDir.exists())
		{
			if(theDir.mkdir()==false)
			{
				Toast.makeText(this, "Could not make dir:"+dir, Toast.LENGTH_LONG).show();
				return;
			}
		}
		
		copyAssets(dir);
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings_button) 
		{
			Intent intent = new Intent(MainMenu.this,MyPreferenceActivity.class);
			MainMenu.this.startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class MainMenuButtonClick implements View.OnClickListener
    {

    	public void onClick(View v)
    	{

    		if(v == captureButton)
    		{
    			Intent intent = new Intent(MainMenu.this,TakePhotoAndEdit.class);
    			MainMenu.this.startActivity(intent);
    		}
    		else if(v == trainButton)
    		{
    			Intent intent = new Intent(MainMenu.this,TrainActivity.class);
    			MainMenu.this.startActivity(intent);
    		}
    		else if(v == testTrainButton)
    		{
    			Intent intent = new Intent(MainMenu.this,TestTrain.class);
    			MainMenu.this.startActivity(intent);
    		}
    		else if(v == enterNumbersButton)
    		{
    			Intent intent = new Intent(MainMenu.this,EnterNumbersActivity.class);
    			MainMenu.this.startActivity(intent);
    		}
    		
    		
    	}
    	
    }
	
	private void copyAssets(String toDir) {
	    AssetManager assetManager = getAssets();
	    String[] files = {"tessdata/eng.traineddata"};
	    
	    for(String filename : files) {
	    	
	        InputStream in = null;
	        OutputStream out = null;
	        try {
	          in = assetManager.open(filename);
	          File outFile = new File(toDir, filename);
	          out = new FileOutputStream(outFile);
	          copyFile(in, out);
	          in.close();
	          in = null;
	          out.flush();
	          out.close();
	          out = null;
	        } catch(IOException e) {
	        	System.out.println("asset copy "+e.getMessage());
	            return;
	        }       
	    }
	}
	private void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
}
