@file:Suppress("NAME_SHADOWING")

package com.shuhart.materialcalendarview

import android.support.v4.util.SparseArrayCompat

/**
 * Pager adapter backing the calendar view
 */
class MonthPagerAdapter(mcv: MaterialCalendarView) : CalendarPagerAdapter<MonthView>(mcv) {

    override fun createView(position: Int): MonthView =
            MonthView(mcv, getItem(position), mcv.firstDayOfWeek)

    override fun indexOf(view: MonthView): Int {
        val month = view.month
        return rangeIndex.indexOf(month)
    }

    override fun isInstanceOfView(`object`: Any?): Boolean = `object` is MonthView

    override fun createRangeIndex(min: CalendarDay, max: CalendarDay): DateRangeIndex =
            Monthly(min, max)

    class Monthly(min: CalendarDay, max: CalendarDay) : DateRangeIndex {

        private val min: CalendarDay
        private val count: Int

        private val dayCache = SparseArrayCompat<CalendarDay>()

        init {
            var max = max
            this.min = CalendarDay.from(min.year, min.month, 1)
            max = CalendarDay.from(max.year, max.month, 1)
            this.count = indexOf(max) + 1
        }

        override fun getCount(): Int = count

        override fun indexOf(day: CalendarDay): Int {
            val yDiff = day.year - min.year
            val mDiff = day.month - min.month

            return yDiff * 12 + mDiff
        }

        override fun getItem(position: Int): CalendarDay {

            var re: CalendarDay? = dayCache.get(position)
            if (re != null) {
                return re
            }

            val numY = position / 12
            val numM = position % 12

            var year = min.year + numY
            var month = min.month + numM
            if (month >= 12) {
                year += 1
                month -= 12
            }

            re = CalendarDay.from(year, month, 1)
            dayCache.put(position, re)
            return re
        }
    }
}
