package com.fitzguru.mfaauction;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by jtsuji on 11/14/14.
 */
public class DisplayUtils {
  static Typeface pictos;

  public static Typeface getPictosTypeface(Context context) {
    if (pictos == null)
      pictos = Typeface.createFromAsset(context.getAssets(), "fonts/HSMobilePictos.ttf");

    return pictos;
  }
  public static int toPx(int dp) {
    return (int) ((dp * Resources.getSystem().getDisplayMetrics().density) + 0.5);
  }

  public static void pictosifyTextView(TextView textView) {
    textView.setTypeface(getPictosTypeface(textView.getContext()));
  }
}
