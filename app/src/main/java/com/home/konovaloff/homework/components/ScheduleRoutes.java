package com.home.konovaloff.homework.components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.home.konovaloff.homework.R;

public class ScheduleRoutes extends LinearLayout {

//	private String mRoute = null;

	private TextView tvDescription;

	private int checked;
	private int unchecked;
	private boolean mShowText = false;

	public ScheduleRoutes(Context context) {
		super(context);
		ctor();
	}

	public ScheduleRoutes(Context context, AttributeSet attrs) {
		super(context, attrs);
		ctor();
	}

	public void setRoute(String route) {
		if (route == null)
			return;

		String[] days = getResources().getStringArray(R.array.days_of_weeks);

		LinearLayout ll = (LinearLayout) getChildAt(0);
		TableRow row = (TableRow) ll.getChildAt(1);
		if (row == null)
			return;

		for (int i = 0; i < row.getChildCount(); i++) {

			if (i >= route.length())
				break;

			Button button = (Button) row.getChildAt(i);
			if (route.charAt(i) == '1') {
				button.setBackgroundResource(checked);
			} else {
				button.setBackgroundResource(unchecked);
				if (mShowText)
					button.setText(days[i]);
			}
            button.invalidate();
			button.requestLayout();
		}

        invalidate();
        requestLayout();
	}

	private void ctor() {
		LayoutInflater li = (LayoutInflater)(getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		li.inflate(R.layout.schedule_routes, this, true);

		checked = android.R.drawable.ic_menu_compass;
		unchecked = R.drawable.button_shape_normal;

		tvDescription = findViewById(R.id.sr_tvDescription);
	}
	
	public void setShowText(boolean showText) {
		mShowText = showText;
	}

	public void setDescriptionText(String text) {
		if (tvDescription != null) {
			tvDescription.setText(text);
            invalidate();
            requestLayout();
		}
	}

	public void setDescriptionWidth(int pixels) {
		if (tvDescription != null) {
			tvDescription.setWidth(pixels);
		}
	}

}
