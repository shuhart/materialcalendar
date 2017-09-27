package com.prolificinteractive.materialcalendarview.utils

import android.content.Context
import android.graphics.Point
import android.util.TypedValue
import android.view.WindowManager

/**
 * Created by Bogdan Kornev
 * on 9/23/2017, 11:06 PM.
 */
object DpUtils {
    fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics
        ).toInt()
    }

    fun getDisplayWidthInPx(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }

    fun getDisplayHeightInPx(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.y
    }
}