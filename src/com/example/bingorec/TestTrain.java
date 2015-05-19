package com.example.bingorec;

import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TestTrain extends Activity
{
	
	TextView results;
	Vector<Instance> data;
	Network net;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_test_train);
		results = (TextView) findViewById(R.id.results_field);
		data = TrainData.loadTrainData(getImageFileDir());
		
		net = Network.loadNetwork(getImageFileDir());
		
		MultiNetwork mnet = new MultiNetwork(getImageFileDir());
		mnet.loadNetworks();
		
		String result = "";
		String multiResult = "";
		
		ImageParser parser = new ImageParser(getImageFileDir());
		
		
		
		for(int i=1;i<=25;i++)
		{
			String imageData1 = parser.readAsBinaryString(getImageFileDir(), i, true);
			String imageData2 = parser.readAsBinaryString(getImageFileDir(), i, false);
			
			
		
			
			Instance inst1 = Instance.createSingleInstance(-1, imageData1);
			Instance inst2 = Instance.createSingleInstance(-1, imageData2);
			
			Instance mInst1 = Instance.createMultiInstance(false, imageData1);
			Instance mInst2 = Instance.createMultiInstance(false, imageData2);
			
			int mInt1 = mnet.feedMultiNetwork(mInst1);
			int mInt2 = mnet.feedMultiNetwork(mInst2);
			
			
			double [] res1 = net.feedInstance(inst1);
			int inum1 = Network.readANNOutput(res1);
			
			double [] res2 = net.feedInstance(inst2);
			int inum2 = Network.readANNOutput(res2);
			
			result += (""+inum1+""+inum2+",");
			
			
			multiResult += (mInt1+""+mInt2+",");
			
			if(i % 5 == 0)
			{
				result += "\n";
				multiResult += "\n";
			}
			
			
		}
		
		
		results.setText(result + "\n\n"+multiResult);
		
	}
	
	private String getImageFileDir()
	{
		return this.getExternalFilesDir(null).getAbsolutePath();
	}

}
