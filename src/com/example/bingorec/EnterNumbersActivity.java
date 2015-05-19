package com.example.bingorec;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EnterNumbersActivity extends Activity
{
	
	TextView numbers;
	Button next;
	EditText numInput;
	
	ArrayList<String> allNumbers = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_number_activity);
		
		numbers = (TextView)findViewById(R.id.enter_numbers_view);
		next = (Button)findViewById(R.id.enter_numbers_button);
		numInput = (EditText)findViewById(R.id.enter_numbers_input);
		
		next.setOnClickListener(new ButtonClick());
		numbers.setText("");
		allNumbers = new ArrayList<String>();
		
		
		
	}
	
	private class ButtonClick implements View.OnClickListener
    {

    	public void onClick(View v)
    	{
    		if(v == next)
    		{
    			String theNum = numInput.getText().toString();
    			numInput.setText("");
    			if(theNum.isEmpty())
    			{
    				return;
    			}
    			
    			allNumbers.add(theNum);
    			
    			String [] nums = new String[allNumbers.size()];
    			nums = allNumbers.toArray(nums);
    			String strNums = implode(",",nums);
    			numbers.setText(strNums);
    			
    			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(EnterNumbersActivity.this);
    			
    			Editor edit = settings.edit();
    			
    			edit.putString("bingo_numbers", strNums);
    			edit.commit();
    			
    			
    		}
    	}
    }
	
	
	
	// http://stackoverflow.com/questions/11248119/java-equivalent-of-phps-implode-array-filter-array
	public  String implode(String glue, String[] strArray)
	{
	    String ret = "";
	    for(int i=0;i<strArray.length;i++)
	    {
	        ret += (i == strArray.length - 1) ? strArray[i] : strArray[i] + glue;
	    }
	    return ret;
	}

}
