package com.example.bingorec;

import java.util.Vector;

/**
 *
 * @author Johan Hagelbäck (johan.hagelback@gmail.com)
 * modiefied by Henno Joosep (hennoj@gmail.com)
 */
public class Instance implements java.io.Serializable
{
	
    public Vector<Attribute> attributes;
    public Vector<Attribute> classAttributes;
    
    public Instance()
    {
        attributes = new Vector<Attribute>();
        classAttributes = new Vector<Attribute>();
    }
    
    public int size()
    {
        return attributes.size();
    }
    
    public void add(Attribute attribute)
    {
        attributes.add(attribute);
    }
    
    public void addAttribute(String id, double value)
    {
        attributes.add(new Attribute(id, value));
    }
    
    public void addClassAttribute(String id, double value)
    {
        classAttributes.add(new Attribute(id, value, true));
    }
    
    
    public double getAttributeValue(int index)
    {
        return attributes.elementAt(index).value;
    }
    
    public String toString()
    {
        String str = "";
        
        for (Attribute a:attributes)
        {
            str += a.toString();
        }
        
        return str;
    }
    
    public double[] getTarget()
    {
        double[] target = new double[classAttributes.size()];
        for(int i=0;i<target.length;i++)
        {
        	target[i] = classAttributes.elementAt(i).value;
        }
       
        return target;
    }
    
    public static Instance createMultiInstance(boolean isNumber,String imageData)
    {
    	
    	Instance inst = new Instance();
		
		for(int i=0;i<imageData.length();i++)
		{
			char ch = imageData.charAt(i);
			
			if(ch == '0')
			{
				inst.addAttribute(""+i, 0);
			}
			else
			{
				inst.addAttribute(""+i, 1);
			}
			
		}
		
		if(isNumber)
		{	
			inst.addClassAttribute("number", 1);
		}
		else
		{
			inst.addClassAttribute("number", 0);
		}
		return inst;
    }

	public static Instance createSingleInstance(int num,String imageData)
	{
		Instance inst = new Instance();
		
		for(int i=0;i<imageData.length();i++)
		{
			char ch = imageData.charAt(i);
			
			if(ch == '0')
			{
				inst.addAttribute(""+i, 0);
			}
			else
			{
				inst.addAttribute(""+i, 1);
			}
			
		}
		
		
		
		if(num == 0)
		{
			inst.addClassAttribute("0", 1);
		}
		else
		{
			inst.addClassAttribute("0",0);
		}
		
		if(num == 1)
		{
			inst.addClassAttribute("1", 1);
		}
		else
		{
			inst.addClassAttribute("1",0);
		}
		
		if(num == 2)
		{
			inst.addClassAttribute("2", 1);
		}
		else
		{
			inst.addClassAttribute("2",0);
		}
		
		if(num == 3)
		{
			inst.addClassAttribute("3", 1);
		}
		else
		{
			inst.addClassAttribute("3",0);
		}
		
		if(num == 4)
		{
			inst.addClassAttribute("4", 1);
		}
		else
		{
			inst.addClassAttribute("4",0);
		}
		
		if(num == 5)
		{
			inst.addClassAttribute("5", 1);
		}
		else
		{
			inst.addClassAttribute("5",0);
		}
		
		if(num == 6)
		{
			inst.addClassAttribute("6", 1);
		}
		else
		{
			inst.addClassAttribute("6",0);
		}
		
		if(num == 7)
		{
			inst.addClassAttribute("7", 1);
		}
		else
		{
			inst.addClassAttribute("7",0);
		}
		
		if(num == 8)
		{
			inst.addClassAttribute("8", 1);
		}
		else
		{
			inst.addClassAttribute("8",0);
		}
		
		if(num == 9)
		{
			inst.addClassAttribute("9", 1);
		}
		else
		{
			inst.addClassAttribute("9",0);
		}
		
		
		
		return inst;
	}
    
}