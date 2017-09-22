package com.prolificinteractive.materialcalendarview.format

import com.prolificinteractive.materialcalendarview.utils.CalendarUtils

import java.util.Calendar
import java.util.Locale

/**
 * Use a [java.util.Calendar] to get week day labels.
 *
 * @see java.util.Calendar.getDisplayName
 */
class CalendarWeekDayFormatter
/**
 * Format with a specific calendar
 *
 * @param calendar Calendar to retrieve formatting information from
 */
@JvmOverloads constructor(private val calendar: Calendar = CalendarUtils.instance) : WeekDayFormatter {

    init {
        // recompute all fields of the calendar based on current date
        // See "Getting and Setting Calendar Field Values"
        // in https://developer.android.com/reference/java/util/Calendar.html
        calendar.get(Calendar.DAY_OF_WEEK)  // Any fields to get is OK to recompute all fields in the calendar.

    }


    override fun format(dayOfWeek: Int): CharSequence {
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
    }
}
