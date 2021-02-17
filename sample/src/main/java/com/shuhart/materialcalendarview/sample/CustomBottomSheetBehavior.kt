package com.shuhart.materialcalendarview.sample

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * Created by Bogdan Kornev
 * on 9/25/2017, 6:24 PM.
 */
class CustomBottomSheetBehavior<V : View>(private var startOffset: Int) : BottomSheetBehavior<V>() {

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        super.onLayoutChild(parent, child, layoutDirection)
        if (state == STATE_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, -(parent.height - child.height))
        } else if (state == STATE_COLLAPSED) {
            // value from parent's method to compensate offset
            ViewCompat.offsetTopAndBottom(child, -parent.width * 9 / 16)
        }
        return true
    }

    init {
        peekHeight = PEEK_HEIGHT_AUTO
    }
}