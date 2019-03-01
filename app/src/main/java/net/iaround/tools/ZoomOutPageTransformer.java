
package net.iaround.tools;


import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.Log;
import android.view.View;

import net.iaround.R;


public class ZoomOutPageTransformer implements PageTransformer {
    private final float MIN_ALPHA = 0.5f;
    private final float MIN_POSITION = 0.2f;

    public static final int MASK_LAYER_ID = R.id.mask_layer;
    public static final int TITLE_ID = R.id.transTitle;

    @SuppressLint("NewApi")
    public void transformPage(View view, float position) {

        Log.e("TAG", view + " , " + position + "");
        View maskView = view.findViewById(MASK_LAYER_ID);
        View title = view.findViewById(TITLE_ID);
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            if (maskView != null) {
                maskView.setAlpha(0);
            }
            if (title != null) {
                title.setAlpha(1.0f);
            }
        } else if (position <= 1) // a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
        { // [-1,1]
            // Modify the default slide transition to shrink the page as well

            float absValue = Math.abs(position);
            if (absValue < MIN_POSITION && absValue != 0) {
                CommonFunction.log("fan", view + " , " + "setAlpha***" + MIN_ALPHA);
                if (title != null) {
                    float alpha = absValue / MIN_POSITION;
                    CommonFunction.log("fan", "alpha***" + alpha);
                    title.setAlpha(1 - alpha);
                }
            } else if (absValue > MIN_POSITION && absValue < 1.0f) {
                CommonFunction.log("fan", view + " , " + "setAlpha***" + 0);
                if (title != null) {
                    title.setAlpha(0);
                }
            }

            // Fade the page relative to its size.
            if (maskView != null) {
                if (absValue == 0) {
                    maskView.setAlpha(0);
                } else {
                    if (absValue != 1.0f) {
                        maskView.setAlpha(absValue);
                    }
                }
            }
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            if (maskView != null) {
                maskView.setAlpha(0);
            }
            if (title != null) {
                title.setAlpha(1.0f);
            }
        }
    }

}
