package com.home.konovaloff.homework;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ScheduleRoutes extends LinearLayout {

	private String mRoute = null;

	private TextView tvDescription;

	private int checked;
	private int unchecked = R.drawable.button_shape_normal;
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

		mRoute = route;

		String[] days = getResources().getStringArray(R.array.days_of_weeks);

		LinearLayout ll = (LinearLayout) getChildAt(0);
		TableRow row = (TableRow) ll.getChildAt(1);
		if (row == null)
			return;

		for (int i = 0; i < row.getChildCount(); i++) {

			if (i >= mRoute.length())
				break;

			Button button = (Button) row.getChildAt(i);
			if (mRoute.charAt(i) == '1') {
				button.setBackgroundResource(checked);
			} else {
				button.setBackgroundResource(unchecked);
				if (mShowText)
					button.setText(days[i]);
			}
		}
	}

	private void ctor() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(
				infService);
		li.inflate(R.layout.schedule_routes, this, true);

		checked = android.R.drawable.ic_menu_compass;
		unchecked = 0;

		tvDescription = (TextView) findViewById(R.id.sr_tvDescription);
	}
	
	public void setShowText(boolean showText) {
		mShowText = showText;
	}

	public void setDescriptionText(String text) {
		if (tvDescription != null) {
			tvDescription.setText(text);
		}
	}

	public void setDescriptionWidth(int pixels) {
		if (tvDescription != null) {
			tvDescription.setWidth(pixels);
		}
	}
}
