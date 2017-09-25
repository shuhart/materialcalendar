package com.prolificinteractive.materialcalendarview.sample

import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.view.View

/**
 * Created by Bogdan Kornev
 * on 9/25/2017, 6:24 PM.
 */
class CustomBottomSheetBehavior<V : View>(private var startOffset: Int) : BottomSheetBehavior<V>() {

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        super.onLayoutChild(parent, child, layoutDirection)
        if (state == STATE_COLLAPSED) {
            ViewCompat.offsetTopAndBottom(child, -startOffset)
        }
        return true
    }

    init {
        peekHeight = PEEK_HEIGHT_AUTO
    }
}