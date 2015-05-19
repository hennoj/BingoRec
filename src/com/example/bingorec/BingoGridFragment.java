package com.example.bingorec;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridLayout.Spec;
import android.widget.TextView;

public class BingoGridFragment extends Fragment
{
	
	TreeMap<String, TextView> cellGrid = new TreeMap<String,TextView>();
	
	public void setBingo(Map<String,String> squares)
	{
		for(Map.Entry<String, String> entry : squares.entrySet())
		{
			if(!entry.getValue().equals("0"))
			{
				setCell(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public void setBingo(String bingoString)
	{
		TreeMap<String,String> squares = BingoGame.fromString(bingoString);
		
		setBingo(squares);
		
	}
	
	public boolean inArray(String str,String [] array)
	{
		for(int i=0;i<array.length;i++)
		{
			if(str.equals(array[i]))
			{
				return true;
			}
		}
		return false;
	}
	
	public void confirmNumbers(String [] numbers)
	{
		for(Map.Entry<String, TextView> entry : cellGrid.entrySet())
		{
			TextView theCell = entry.getValue();
			if(inArray(theCell.getText().toString(),numbers))
			{
				theCell.setTypeface(null, Typeface.BOLD);
			}
			
		}
	}
	
	public void setCell(String cell,String value)
	{
		TextView theCell = cellGrid.get(cell);
		theCell.setText(value);
	}
	
	// http://stackoverflow.com/questions/3355367/height-of-statusbar/3356263#3356263
	public int getStatusBarHeight() 
	{ 
	      int result = 0;
	      int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
	      if (resourceId > 0) {
	          result = getResources().getDimensionPixelSize(resourceId);
	      } 
	      return result;
	} 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState)
	{

		GridLayout layout = (GridLayout)inflater.inflate(R.layout.bingogridfragment, container, false);
		
		layout.setOrientation(GridLayout.VERTICAL);
		
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		int cellWidth = 32;
		int cellHeight = 32;
		
		if(size.x > size.y)
		{
			cellWidth = (size.x - 300) / 5;
			cellHeight = ( size.y - getStatusBarHeight() )  / 5;
		}
		else
		{
			cellWidth = size.x / 5;
			cellHeight = (size.y - 300 - getStatusBarHeight()) / 5;
		}
			
		
		Vector<String> rows = BingoGame.toVector("12345");
		Vector<String> cols = BingoGame.toVector("bingo");
		
		Vector<String> grid = BingoGame.cross(rows, cols);
		
		int cellNum = 0;
		int textSize = -1;
		for(String cell : grid)
		{
			
			FillTextView textViewCell = new FillTextView(getActivity());
			
			
			
			textViewCell.setWidth(cellWidth);
			textViewCell.setHeight(cellHeight);
			textViewCell.setGravity(Gravity.CENTER);
			textViewCell.setClickable(true);
			textViewCell.setBackgroundResource(R.drawable.button_custom);
			textViewCell.setText("");
			textViewCell.setTag(cell);
			
			if(textSize == -1)
			{
				textSize = textViewCell.calcTextSize("0", cellWidth, cellHeight);
			}
			textViewCell.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
			
			
			
			cellGrid.put(cell, textViewCell);
			
			int columnIndex = cellNum % 5;
			int rowIndex = (cellNum - columnIndex) / 5;
			
			Spec row = GridLayout.spec(rowIndex, 1); 
            Spec colspan = GridLayout.spec(columnIndex, 1);
            GridLayout.LayoutParams gridLayoutParam = new GridLayout.LayoutParams(row, colspan);
			
			layout.addView(textViewCell,gridLayoutParam);
			
			cellNum++;
		}
		
		return layout;

	}
}
