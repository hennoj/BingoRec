package com.example.bingorec;

import java.io.File;
import java.util.Vector;

public class MultiNetwork 
{
	
	public String dir;
	
	public Vector<Network> nets = new Vector<Network>();
	
	Vector < Vector <Instance > > multiTrainData = new Vector< Vector <Instance > >();
	
	public int numNetworks = 10;
	public String prefix = "multinetwork";
	public String suffix = ".ser";
	
	public MultiNetwork(String dir)
	{
		this.dir = dir;
	}
	
	public Vector < Vector <Instance> > singleToMultiData(Vector<Instance> singleTrainData)
	{
		Vector< Vector<Instance >> multiData = new Vector< Vector <Instance > >();
		for(int i=0;i<numNetworks;i++)
		{
			Vector <Instance> numData = new Vector<Instance>();
			for(Instance inst : singleTrainData)
			{
				double [] target = inst.getTarget();
				int inum = Network.readANNOutput(target);
				boolean isNumber = false;
				if(inum == i)
				{
					isNumber = true;
				}
				
				Instance numInstance = Instance.createMultiInstance(isNumber, inst.toString());
				numData.add(numInstance);
			}
			multiData.add(numData);
		}
		return multiData;
	}
	
	public int [] evaluateTotalAccuracy(Vector<Instance> singleData)
	{
		Vector< Vector <Instance > > multiEvaluateData = singleToMultiData(singleData);
		double [] accuracy = new double[nets.size()];
		int correct = 0;
		int total = 0;
		for(int i=0;i<nets.size();i++)
		{
			Network net = nets.get(i);
			Vector<Instance> data = multiEvaluateData.get(i);
			total += data.size();
			correct += net.evaluateCorrect(data);
		}
		int [] res = new int[2];
		res[0] = correct;
		res[1] = total;
		return res;
		
		
	}
	
	public double [] evaluateTrainAccuracy()
	{
		double [] accuracy = new double[nets.size()];
		for(int i=0;i<nets.size();i++)
		{
			Network net = nets.get(i);
			Vector<Instance> data = multiTrainData.get(i);
			accuracy[i] = net.evaluateAccuracy(data);
		}
		return accuracy;
	}
	
	public int [] evaluateTotalTrainAccuracy()
	{
		double [] accuracy = new double[nets.size()];
		int correct = 0;
		int total = 0;
		for(int i=0;i<nets.size();i++)
		{
			Network net = nets.get(i);
			Vector<Instance> data = multiTrainData.get(i);
			total += data.size();
			correct += net.evaluateCorrect(data);
		}
		int [] res = new int[2];
		res[0] = correct;
		res[1] = total;
		return res;
	}
	
	public double [] evaluateAccuracy(Vector<Instance> singleData)
	{
		
		Vector< Vector <Instance > > multiEvaluateData = singleToMultiData(singleData);
		double [] accuracy = new double[nets.size()];
		for(int i=0;i<nets.size();i++)
		{
			Network net = nets.get(i);
			Vector<Instance> data = multiEvaluateData.get(i);
			accuracy[i] = net.evaluateAccuracy(data);
		}
		return accuracy;
	}
	
	public void trainNetworks(Vector<Instance> singleTrainData)
	{
		
		multiTrainData = singleToMultiData(singleTrainData);
		
		
		
		newNetworks();
		
		double [] accuracy = new double[nets.size()];
	    double targetAccuracy = 99.0;
	    int iterations = 1;
	   
        while (minimum(accuracy) < targetAccuracy)
        {
        	for(int i=0;i<nets.size();i++)
        	{
        		Network net = nets.get(i);
        		Vector<Instance> trainData = multiTrainData.get(i);
        		
        		net.train(trainData);
        		
        		accuracy[i] = net.evaluateAccuracy(trainData);
        		
        		
        		nets.set(i, net);
        		
        	}
            
            for(int i=0;i<accuracy.length;i++)
            {
            	System.out.print("num " + i + "=" + accuracy[i]);
            }
            System.out.println();
            iterations++;
            
            if (iterations > 1000)
            {
            	System.out.println("Out of iterations");
            	break;
            }
        }
        
       saveNetworks();
	}
	
	public int feedMultiNetwork(Instance inst)
	{
		
		double [] outputs = new double[nets.size()];
		
		for(int i=0;i<nets.size();i++)
		{
			Network net = nets.get(i);
			double [] out = net.feedInstance(inst);
			outputs[i] = out[0];
		}
		
		return Network.readANNOutput(outputs);
		
	}
	
	public double minimum(double [] nums)
	{
		double min = nums[0];
		for(int i=0;i<nums.length;i++)
		{
			if(nums[i] < min)
			{
				min = nums[i];
			}
		}
		return min;
	}
	
	public void newNetworks()
	{
		for(int i=0;i<numNetworks;i++)
		{
			Network n = new Network(324, 11, 1);
	        nets.add(n);
		}
	}
	
	public boolean networksExist()
	{
		for(int i=0;i<numNetworks;i++)
		{
			File f = new File(dir,prefix+i+suffix);
			if(!f.exists())
			{
				return false;
			}
		}
		return true;
	}
	
	public void loadNetworks()
	{
		for(int i=0;i<numNetworks;i++)
		{
			Network n = Network.loadNetwork(dir,prefix+i+suffix);
			nets.add(n);
		}
	}
	
	public void saveNetworks()
	{
		for(int i=0;i<nets.size();i++)
		{
			Network n = nets.elementAt(i);
			Network.saveNetwork(dir, prefix+i+suffix,n);
		}
	}

}
