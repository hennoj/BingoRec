package com.example.bingorec;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

public class BingoGame
{
	
	
	TreeMap< String, Vector<String> > squares = new TreeMap<String, Vector <String>>();
	
	public static TreeMap <String, String> fromString(String bingoString)
	{
		Vector < String > rows = toVector("12345");
		Vector < String > cols = toVector("bingo");
		
		Vector<String> vsquares = cross(rows,cols);
		
		Iterator<String> it = vsquares.iterator();
		TreeMap<String, String> grid = new TreeMap<String,String>();
		String [] tokenz = bingoString.split(",");
		
		for(int i=0;i<tokenz.length && it.hasNext();i++)
		{
			
			String sq  = tokenz[i];
			String sit = it.next();
			grid.put(sit,sq);
			
			
		}
		
		return grid;
			
	}
	
	public static Vector<String> toVector(String str)
	{
		Vector < String > vec = new Vector<String>();
		for(int i = 0;i < str.length(); i++)
		{
			vec.add(str.substring(i,i+1));
		}
		return vec;
	}
	
	public static Vector<String> cross(Vector<String> a, Vector<String> b)
	{
		Vector<String> crossproduct = new Vector<String>();
		for(int i=0;i<a.size();i++)
		{
			for(int j=0;j<b.size();j++)
			{
				String p = a.get(i) + b.get(j);
				crossproduct.add(p);
			}
		}
		return crossproduct;
	}

}
