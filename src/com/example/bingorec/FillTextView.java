package com.example.bingorec;


/*
 * This is a class copied from https://github.com/rzsombor/fill-textview
 * 
 * changing text seemed not work so i changed onTextChanged event to be original super classes function
 * 
 * */


import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class FillTextView extends TextView {

	// Scaling factor
	private static final float VERTICAL_FONT_SCALING_FACTOR = 0.9f;

	// Attributes
	private Paint mTestPaint;

	public FillTextView(Context context) {
		super(context);
		initialise();
	}

	public FillTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialise();
	}

	private void initialise() {
		mTestPaint = new Paint();
		mTestPaint.set(this.getPaint());
	}

	/*
	 * Resize the font so the specified text fits in the text box assuming the
	 * text box is the specified width.
	 */
	public int calcTextSize(String text, int textWidth, int textHeight) {
		if (textHeight <= 0 || textWidth <= 0) {
			return 0;
		}

		// Find target height
		float targetTextSizeVertical = (textHeight - this.getPaddingTop() - this.getPaddingBottom()) * VERTICAL_FONT_SCALING_FACTOR;

		// Find target width
		float targetWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();

		float hi = 800;
		float lo = 2;
		final float threshold = 0.5f; // How close we have to be

		mTestPaint.set(this.getPaint());

		while ((hi - lo) > threshold) {
			float size = (hi + lo) / 2;
			mTestPaint.setTextSize(size);
			if (mTestPaint.measureText(text) >= targetWidth)
				hi = size; // too big
			else
				lo = size; // too small
		}
		float targetTextSizeHorizontal = lo;

		// Set the text size
		float targetTextSize = Math.min(targetTextSizeVertical, targetTextSizeHorizontal);

		
		return (int)targetTextSize;
	}

	

}