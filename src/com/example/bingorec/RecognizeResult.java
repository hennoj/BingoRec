package com.example.bingorec;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.Toast;

public class RecognizeResult extends FragmentActivity
{
	
	BingoGridFragment bgf;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recognizeresult);
		
		
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		
		bgf = (BingoGridFragment) getSupportFragmentManager().findFragmentById(R.id.bingo_grid_fragment_edit);
		
		String bingo = getIntent().getExtras().getString("bingo");
		if(bingo == null)
		{
			Toast.makeText(this, "Incorrect launch of activity",Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		
		bgf.setBingo(bingo);
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		
		String nums = settings.getString("bingo_numbers", "");
		
		String [] allNums = nums.split(",");
		
		if(allNums.length > 0)
		{
			bgf.confirmNumbers(allNums);
		}
		
		
		
	}
	

}
