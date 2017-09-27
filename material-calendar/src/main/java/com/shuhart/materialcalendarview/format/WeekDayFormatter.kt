package com.shuhart.materialcalendarview.format

import com.shuhart.materialcalendarview.utils.CalendarUtils

/**
 * Supply labels for a given day of the week
 */
interface WeekDayFormatter {
    /**
     * Convert a given day of the week into a label
     *
     * @param dayOfWeek the day of the week as returned by [java.util.Calendar.get] for [java.util.Calendar.DAY_OF_YEAR]
     * @return a label for the day of week
     */
    fun format(dayOfWeek: Int): CharSequence

    companion object {

        /**
         * Default implementation used by [com.shuhart.materialcalendarview.MaterialCalendarView]
         */
        val DEFAULT: WeekDayFormatter = CalendarWeekDayFormatter(CalendarUtils.instance)
    }
}
