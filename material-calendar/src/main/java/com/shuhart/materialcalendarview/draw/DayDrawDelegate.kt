package com.shuhart.materialcalendarview.draw

import android.graphics.Canvas
import com.shuhart.materialcalendarview.DayView

/**
 * Created by Bogdan Kornev
 * on 9/21/2017, 7:50 PM.
 */
interface DayDrawDelegate {
    fun onDraw(canvas: Canvas, dayDrawData: DayDrawData, dayView: DayView)
    fun setSelectionColor(color: Int)
    fun setSelectionRangeColor(color: Int)
}