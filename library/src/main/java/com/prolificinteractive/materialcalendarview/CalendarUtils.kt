package com.prolificinteractive.materialcalendarview

import java.util.Calendar
import java.util.Date

import java.util.Calendar.DATE
import java.util.Calendar.DAY_OF_WEEK
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR

/**
 * Utilities for Calendar
 */
object CalendarUtils {

    /**
     * @param date [Date] to pull date information from
     * @return a new Calendar instance with the date set to the provided date. Time set to zero.
     */
    fun getInstance(date: Date?): Calendar {
        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }
        copyDateTo(calendar, calendar)
        return calendar
    }

    /**
     * @return a new Calendar instance with the date set to today. Time set to zero.
     */
    val instance: Calendar
        get() {
            val calendar = Calendar.getInstance()
            copyDateTo(calendar, calendar)
            return calendar
        }

    /**
     * Set the provided calendar to the first day of the month. Also clears all time information.
     *
     * @param calendar [Calendar] to modify to be at the first fay of the month
     */
    fun setToFirstDay(calendar: Calendar) {
        val year = getYear(calendar)
        val month = getMonth(calendar)
        calendar.clear()
        calendar.set(year, month, 1)
    }

    /**
     * Copy *only* date information to a new calendar.
     *
     * @param from calendar to copy from
     * @param to   calendar to copy to
     */
    fun copyDateTo(from: Calendar, to: Calendar) {
        val year = getYear(from)
        val month = getMonth(from)
        val day = getDay(from)
        to.clear()
        to.set(year, month, day)
    }

    fun getYear(calendar: Calendar): Int = calendar.get(YEAR)

    fun getMonth(calendar: Calendar): Int = calendar.get(MONTH)

    fun getDay(calendar: Calendar): Int = calendar.get(DATE)

    fun getDayOfWeek(calendar: Calendar): Int = calendar.get(DAY_OF_WEEK)

    fun isFirstDayOfWeek(date: CalendarDay): Boolean = getDayOfWeek(date.calendar) == date.calendar.firstDayOfWeek

    fun isLastDayOfWeek(date: CalendarDay): Boolean {
        val firstDayOfWeek = date.calendar.firstDayOfWeek
        val lastDayOfWeek =
                if (firstDayOfWeek > 1) {
                    firstDayOfWeek - 1
                } else {
                    Calendar.SATURDAY
                }
        return getDayOfWeek(date.calendar) == lastDayOfWeek
    }
}
