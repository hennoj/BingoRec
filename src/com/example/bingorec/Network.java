package com.example.bingorec;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

/**
 *
 * @author Johan Hagelbäck (johan.hagelback@gmail.com)
 * 
 * modified by Henno Joosep (hennoj@gmail.com).
 */
public class Network implements java.io.Serializable
{
    private Perceptron[] h;
    private double[] outH;
    private Perceptron[] o;
    private double[] outO;
    private double[] inputs;
    
    private final double LearningRate = 0.05;
    
    public Network(int inputLayerSize, int hiddenLayerSize, int outputLayerSize)
    {
        h = new Perceptron[hiddenLayerSize];
        outH = new double[hiddenLayerSize];
        for (int i = 0; i < hiddenLayerSize; i++)
        {
            h[i] = new Perceptron(inputLayerSize);
        }
        
        o = new Perceptron[outputLayerSize];
        outO = new double[outputLayerSize];
        for (int i = 0; i < outputLayerSize; i++)
        {
            o[i] = new Perceptron(hiddenLayerSize);
        }
        
        /*h[0].w[0] = 0.1;
        h[0].w[1] = 0.8;
        h[1].w[0] = 0.4;
        h[1].w[1] = 0.6;
        
        o[0].w[0] = 0.3;
        o[0].w[1] = 0.9;*/
    }
    
    public double[] feedInstance(Instance inst)
    {
        inputs = new double[inst.attributes.size()];
        for (int i = 0; i < inst.attributes.size(); i++)
        {
            inputs[i] = inst.attributes.elementAt(i).value;
        }
        
        //Feed through hidden layer
        for (int i = 0; i < h.length; i++)
        {
            outH[i] = h[i].calculateOutput(inputs);
        }
        
        //Feed through output layer
        for (int i = 0; i < o.length; i++)
        {
            outO[i] = o[i].calculateOutput(outH);
        }
        
        return outO;
    }
    
    public void train(Vector<Instance> data)
    {
        for (Instance inst:data)
        {
        	// System.out.println("inst attributes size: " + inst.attributes.size());
            train(inst);
        }
    }
    
    public void train(Instance inst)
    {
        double[] target = inst.getTarget();
        
        
        feedInstance(inst);
        
       
        for (int i = 0; i < o.length; i++)
        {
            //Calculate error for output node i
            double E = outO[i] * (1 - outO[i]) * (target[i] - outO[i]);
            //System.out.println(E);
            
            //Update weights for output node i
            for (int ow = 0; ow < o[i].w.length; ow++)
            {
                o[i].w[ow] += E * outH[ow] * LearningRate;
                //System.out.println("O.W" + ow + " += " + E + "*" + outH[ow] + " = " + o[i].w[ow]);
            }
            
            
            //Update weights for hidden nodes
            for (int hu = 0; hu < h.length; hu++)
            {
                //Calculate error for hidden node hu
                double Eh = E * o[i].w[hu] * (1 - outH[hu]) * outH[hu];
                //System.out.println("w." + i + "." + hu + " " + o[i].w[hu]);
                //System.out.println("Eh." + hu + " = " + Eh);
                
                //Update weight hw in hidden node hu
                for (int hw = 0; hw < h[hu].w.length; hw++)
                {
                    h[hu].w[hw] += Eh * inputs[hw] * LearningRate;
                    //System.out.println("h." + hu + "." + hw + " = " + Eh + " * " + inputs[hw] + " = " + h[hu].w[hw]);
                }
            }
            
        }
        
    }
    
    public double evaluateError(Vector<Instance> data)
    {
        double SqErrSum = 0;
        
        for (Instance inst:data)
        {
            feedInstance(inst);
            double[] target = inst.getTarget();
            for (int i = 0; i < target.length; i++)
            {
                SqErrSum = Math.pow(target[i] - outO[i], 2);
            }
        }
        
        SqErrSum *= 0.5;
        
        return SqErrSum;
    }
    
    public int evaluateCorrect(Vector<Instance> data)
    {
    	int correct = 0;
        
        for (Instance inst:data)
        {
            feedInstance(inst);
            double[] target = inst.getTarget();
            boolean ok = true;
            for (int i = 0; i < target.length; i++)
            {
                if (target[i] < 0.5 && outO[i] >= 0.5) ok = false;
                if (target[i] >= 0.5 && outO[i] < 0.5) ok = false;
            }
            if (ok) correct++;
        }
        
        return correct;
        
        
    }
    
    public double evaluateAccuracy(Vector<Instance> data)
    {
        int correct = evaluateCorrect(data);
        
        
        double percent = (double)correct / (double)data.size() * 100.0;
        
        return percent;
    }
    
    public String toString()
    {
        String str = "";
        
        str += "Hidden nodes:\n";
        for (int i = 0; i < h.length; i++)
        {
            for (int j = 0; j < h[i].w.length; j++)
            {
                str += "\tIn_" + j + "-Hi_" + i + ": " + String.format("%.2f", h[i].w[j]) + "\n";
            }
        }
        
        str += "Output nodes:\n";
        for (int i = 0; i < o.length; i++)
        {
            for (int j = 0; j < o[i].w.length; j++)
            {
                str += "\tHi_" + j + "-Ou_" + i + ": " + String.format("%.2f", o[i].w[j]) + "\n";
            }
        }
        
        return str;
    }
    
    public static int readANNOutput(double [] result)
	{
		
		double largest = -1;
		int index = -1;
		// System.out.println("results:");
		for(int i=0;i<result.length;i++)
		{
			// System.out.println(i+":"+result[i]);
			if(result[i] > 0.5 && result[i] > largest )
			{
				largest = result[i];
				index = i;
				
			}
		}
		return index;
		
		
	}
    
    public static void saveNetwork(String dir,Network n)
    {
    	saveNetwork(dir,"network.ser",n);
    }
    
    
    public static void saveNetwork(String dir,String name, Network n)
    {
    	try
        {
           FileOutputStream fileOut =
           new FileOutputStream(dir+"/"+name);
           ObjectOutputStream out = new ObjectOutputStream(fileOut);
           out.writeObject(n);
           out.close();
           fileOut.close();
           
        }catch(IOException i)
        {
            throw new RuntimeException(i);
        }
    }
    
    public static Network loadNetwork(String dir)
    {
    	return loadNetwork(dir,"network.ser");
    }
    
    public static Network loadNetwork(String dir,String name)
    {
    	Network n = null;
        try
        {
           
           FileInputStream fileIn = new FileInputStream(dir+"/"+name);
           ObjectInputStream in = new ObjectInputStream(fileIn);
           n = (Network) in.readObject();
           in.close();
           fileIn.close();
           return n;
        }
        catch(Exception e)
        {
          throw new RuntimeException(e);
        }
        
    }
    
    
}

