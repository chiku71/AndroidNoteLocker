package com.apps71.notelocker;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LayoutUtils
{
    public static View addViewToLayout(Context appContext, RelativeLayout layout, int upperElementId, int width, int height, int backgroundColor)
    {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,height); // Height in sp Example: 5sp
        layoutParams.addRule(RelativeLayout.BELOW, upperElementId);

        View view = new View(appContext);
        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(backgroundColor); //Color.parseColor("#00000")
        // This will not work if for Systems below Jelly Beans
        int thisElementId = View.generateViewId();
        view.setId(thisElementId);//
        layout.addView(view);

        return view;
    }

    public static TextView addTextViewToLayout(Context appContext, LinearLayout layout, LinearLayout.LayoutParams layoutParams, String text, int textColor, int textSize)
    {
        TextView textView = new TextView(appContext);
        textView.setText(text);
        textView.setTextColor(textColor);
        textView.setTextSize(textSize);
        textView.setLayoutParams(layoutParams);
        textView.setId(TextView.generateViewId());
        layout.addView(textView);

        return textView;
    }
}
