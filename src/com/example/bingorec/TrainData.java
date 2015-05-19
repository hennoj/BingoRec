package com.example.bingorec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class TrainData 
{
	
	public static String fileName = "traindata.ser";
	
	public static void saveTrainData(String dir,Vector<Instance> data)
    {
    	try
        {
           FileOutputStream fileOut =
           new FileOutputStream(dir+"/"+fileName);
           ObjectOutputStream out = new ObjectOutputStream(fileOut);
           out.writeObject(data);
           out.close();
           fileOut.close();
           
        }catch(IOException i)
        {
            throw new RuntimeException(i);
        }
    }
    
    public static Vector<Instance> loadTrainData(String dir)
    {
    	Vector<Instance> n = null;
    	
    	File fileTrainData = new File(dir+"/"+fileName);
    	if(!fileTrainData.exists())
    	{
    		return new Vector<Instance>();
    	}
    	
        try
        {
           
           FileInputStream fileIn = new FileInputStream(dir+"/"+fileName);
           ObjectInputStream in = new ObjectInputStream(fileIn);
           n = (Vector<Instance>) in.readObject();
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
