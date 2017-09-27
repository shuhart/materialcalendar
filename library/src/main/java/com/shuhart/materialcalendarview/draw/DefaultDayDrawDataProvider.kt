package com.shuhart.materialcalendarview.draw

/**
 * Created by Bogdan Kornev
 * on 9/21/2017, 8:12 PM.
 */
class DefaultDayDrawDataProvider : DayDrawDataProvider {
    private val dayDrawData = DayDrawData()

    override fun calculateBounds(width: Int, height: Int) {
        val radius = Math.min(height, width)
        dayDrawData.apply {
            this.radius = radius / 2f - bottomTopDayPadding
            this.cx = width / 2f
            this.cy = height / 2f
            rangeRect.set(0, bottomTopDayPadding, width, height - bottomTopDayPadding)
            lastRect.set(0, bottomTopDayPadding, width / 2, height - bottomTopDayPadding)
            firstRect.set(width / 2, bottomTopDayPadding, width, height - bottomTopDayPadding)
        }
    }

    override fun setBottomTopDayPadding(padding: Int) {
        dayDrawData.bottomTopDayPadding = padding
    }

    override fun getDayDrawData(): DayDrawData = dayDrawData

    override fun copy(): DayDrawDataProvider = DefaultDayDrawDataProvider()
}