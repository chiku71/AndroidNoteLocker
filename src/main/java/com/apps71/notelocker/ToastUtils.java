package com.apps71.notelocker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils
{
    public static void showSuccessMessage(Context appContext, String message)
    {
        Toast toast = Toast.makeText(appContext, message, Toast.LENGTH_LONG);
        showToastMessageWithColors(toast, "#11DE06", "#0A0F77");
    }

    public static void showFailureMessage(Context appContext, String message)
    {
        Toast toast = Toast.makeText(appContext, message, Toast.LENGTH_LONG);
        showToastMessageWithColors(toast, "#F65D48", "#0D0200");
    }

    public static void showWarningMessage(Context appContext, String message)
    {
        Toast toast = Toast.makeText(appContext, message, Toast.LENGTH_LONG);
        showToastMessageWithColors(toast, "#BE7474", "#0D0200");
    }

    private static void showToastMessageWithColors(Toast toast, String backgroundColorHexCode, String textColorHexCode)
    {
        View view = toast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(Color.parseColor(backgroundColorHexCode), PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be editted
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.parseColor(textColorHexCode));

        toast.show();
    }

    public static  void showInfoForShortTime(Context appContext, String message)
    {
        Toast toast = Toast.makeText(appContext, message, Toast.LENGTH_SHORT);
        showToastMessageWithColors(toast, "#BDBCB8", "#080808");
    }

    public static  void showInfoForLongTime(Context appContext, String message)
    {
        Toast toast = Toast.makeText(appContext, message, Toast.LENGTH_LONG);
        showToastMessageWithColors(toast, "#BDBCB8", "#080808");
    }

    private static void testShow(Context appContext, String message)
    {
        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show();
    }
}
