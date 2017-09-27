package com.shuhart.materialcalendarview.format

import com.shuhart.materialcalendarview.CalendarDay

import java.text.SimpleDateFormat

/**
 * Supply labels for a given day. Default implementation is to format using a [SimpleDateFormat]
 */
interface DayFormatter {

    /**
     * Format a given day into a string
     *
     * @param day the day
     * @return a label for the day
     */
    fun format(day: CalendarDay): String

    companion object {

        /**
         * Default implementation used by [com.shuhart.materialcalendarview.MaterialCalendarView]
         */
        val DEFAULT: DayFormatter = DateFormatDayFormatter()
    }
}
