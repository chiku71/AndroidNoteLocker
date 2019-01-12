package com.apps71.notelocker;

import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

class UiEditNoteUtils
{
    public static ShapeDrawable getPasswordFieldShape()
    {
        ShapeDrawable shapePw = new ShapeDrawable(new RectShape());
        shapePw.getPaint().setStyle(Paint.Style.STROKE);
        shapePw.getPaint().setStrokeWidth(6);
        return shapePw;
    }
}
