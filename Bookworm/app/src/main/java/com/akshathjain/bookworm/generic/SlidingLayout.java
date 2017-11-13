package com.akshathjain.bookworm.generic;

import android.content.Context;
import android.util.AttributeSet;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.Serializable;

/**
 * Created by Akshath on 11/12/2017.
 * This class extends SlidingUpPanelLayout so it can implement Serializable
 */

public class SlidingLayout extends SlidingUpPanelLayout implements Serializable {
    public SlidingLayout(Context context) {
        super(context);
    }

    public SlidingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
