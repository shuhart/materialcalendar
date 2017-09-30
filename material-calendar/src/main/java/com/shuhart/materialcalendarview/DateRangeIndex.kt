package com.shuhart.materialcalendarview

/**
 * Use math to calculate first days of months by postion from a minium date
 */
interface DateRangeIndex {

    fun getCount(): Int

    fun indexOf(day: CalendarDay): Int

    fun getItem(position: Int): CalendarDay
}
