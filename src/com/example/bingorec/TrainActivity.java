package com.example.bingorec;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class TrainActivity extends Activity 
{
	
	Button nextButton;
	Button trainButton;
	Button skipButton;
	Button saveButton;
	
	ImageParser parser;
	
	private boolean first;
	private int imageId;
	String dir;
	
	
	private Vector<Instance> trainData = new Vector<Instance>();
	
	private String getImageFileDir()
	{
		return this.getExternalFilesDir(null).getAbsolutePath();
	}
	
	private void nextImage()
	{
		if(first == true)
		{
			first = false;
			return;
		}
		
		if(imageId == 25)
		{
			return;
		}
		
		first = true;
		
		imageId++;
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train);
		
		parser = new ImageParser(dir);
		parser.initialize();
		
		nextButton = (Button)findViewById(R.id.next_button);
		nextButton.setOnClickListener(new ButtonClick());
		
		trainButton = (Button) findViewById(R.id.start_train_button);
		trainButton.setOnClickListener(new ButtonClick());
		
		skipButton = (Button) findViewById(R.id.skip_button);
		skipButton.setOnClickListener(new ButtonClick());
		
		saveButton = (Button) findViewById(R.id.save_button_train_activity);
		saveButton.setOnClickListener(new ButtonClick());
		
		first = true;
		imageId = 1;
		dir = getImageFileDir();
		
		showImage(imageId,first);
		
		trainData = TrainData.loadTrainData(dir);
		
		
	}
	
	public void showImage(int id,boolean first)
	{
		String dir = getImageFileDir();
		
		ImageView myImage = (ImageView) findViewById(R.id.train_image_view);

		
		Bitmap myBitmap = parser.readAsBitmap(dir, id, first);
		
	    myImage.setImageBitmap(myBitmap);
		
	}
	
	public void resetHumanGivenNumber()
	{
		EditText hgn = (EditText) findViewById(R.id.human_given_number);
		
		hgn.setText("");
		
	}
	
	public int readHumanGivenNumber()
	{
		EditText hgn = (EditText) findViewById(R.id.human_given_number);
		
		String number = hgn.getText().toString();
		
		if(number.isEmpty())
		{
			return -1;
		}
		
		try
		{
			int intNumber = Integer.parseInt(number);
			
			if(intNumber >= 0 && intNumber <= 9)
			{
				return intNumber;
			}
			return -1;
			
		}
		catch(NumberFormatException nfe)
		{
			return -1;
		}
		
		
	}
	
	private class DataSaver extends AsyncTask<String, Integer, String>
    {
    	protected String doInBackground(String ... imgName)
    	{
    		TrainData.saveTrainData(dir, trainData);
    		return "";
    	}

    	protected void onProgressUpdate(Integer ... progress)
    	{

    	}

    	protected void onPostExecute(String result) 
    	{
    		
    		Toast.makeText(TrainActivity.this, "Saving data complete!", Toast.LENGTH_LONG).show();
    		System.out.println("Saving data complete!");
    	}
    	
    	
    	 
    	
    }
	
	private class NNTrainer extends AsyncTask<String, Integer, String>
    {
    	protected String doInBackground(String ... imgName)
    	{
    		
    		MultiNetwork mnet = new MultiNetwork(dir);
    		
    		mnet.trainNetworks(trainData);
    		
    		
    		try
    		{
    			Network net = new Network(324, 50, 10);
    	        
    	        
    	        double accuracy = 0;
    	        double targetAccuracy = 98.0;
    	        int iterations = 1;
    	        while (accuracy < targetAccuracy)
    	        {
    	            net.train(trainData);
    	            
    	            accuracy = net.evaluateAccuracy(trainData);
    	            System.out.println("accuracy: " + accuracy);
    	            iterations++;
    	            
    	            if (iterations > 10000)
    	            {
    	            	System.out.println("Out of iterations");
    	            	break;
    	            }
    	        }
    	        
    	        Network.saveNetwork(dir, net);
    	        
    	        
	    		return "";
    		}
    		catch(Exception e)
    		{
    			System.out.println("exception: " + e.getMessage());
    			throw new RuntimeException(e);
    			
    		}
    		
    		
    	}

    	protected void onProgressUpdate(Integer ... progress)
    	{

    	}

    	protected void onPostExecute(String result) 
    	{
    		
    		Toast.makeText(TrainActivity.this, "Training complete!", Toast.LENGTH_LONG).show();
    		System.out.println("Training complete!");
    	}
    	
    	
    	 
    	
    }
	
	private class ButtonClick implements View.OnClickListener
    {

    	public void onClick(View v)
    	{

    		if(v == nextButton)
    		{
    			int num = readHumanGivenNumber();
    			resetHumanGivenNumber();
    			
    			
    			String imageData = parser.readAsBinaryString(dir, imageId, first);
    			
    			
    			Instance inst = Instance.createSingleInstance(num, imageData);
    			trainData.add(inst);
    			
    			nextImage();
    			showImage(imageId,first);
    		}
    		else if(v == skipButton)
    		{
    			nextImage();
    			showImage(imageId,first);
    		}
    		else if(v == trainButton)
			{
				try
		    	{
		    		AsyncTask task = new NNTrainer().execute("");
		    	}
		    	catch(Exception e)
		    	{
		    		throw new RuntimeException(e);
		    	}
		    	
			}
    		else if(v == saveButton)
    		{
    			try
		    	{
		    		AsyncTask task = new DataSaver().execute("");
		    	}
		    	catch(Exception e)
		    	{
		    		throw new RuntimeException(e);
		    	}
    		}
    		
    	}
    	
    }
	
	 
	

}
