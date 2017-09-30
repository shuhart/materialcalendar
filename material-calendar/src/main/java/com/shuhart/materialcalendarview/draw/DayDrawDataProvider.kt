package com.shuhart.materialcalendarview.draw

/**
 * Created by Bogdan Kornev
 * on 9/21/2017, 8:10 PM.
 */
interface DayDrawDataProvider {
    fun calculateBounds(width: Int, height: Int)
    fun setBottomTopDayPadding(padding: Int)
    fun getDayDrawData(): DayDrawData
    fun copy(): DayDrawDataProvider
}