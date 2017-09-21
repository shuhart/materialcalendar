package com.prolificinteractive.materialcalendarview

import android.annotation.SuppressLint

import java.util.Calendar

/**
 * Display a month of [DayView]s and
 * seven [WeekDayView]s.
 */
@SuppressLint("ViewConstructor")
class MonthView(view: MaterialCalendarView, month: CalendarDay, firstDayOfWeek: Int) : CalendarPagerView(view, month, firstDayOfWeek) {

    override fun buildDayViews(dayViews: MutableCollection<DayView>, calendar: Calendar) {
        for (r in 0 until CalendarPagerView.DEFAULT_MAX_WEEKS) {
            for (i in 0 until CalendarPagerView.DEFAULT_DAYS_IN_WEEK) {
                addDayView(dayViews, calendar)
            }
        }
    }

    val month: CalendarDay
        get() = firstViewDay

    override fun isDayEnabled(day: CalendarDay): Boolean = day.month == firstViewDay.month

    override val rows: Int =
            CalendarPagerView.DEFAULT_MAX_WEEKS + CalendarPagerView.DAY_NAMES_ROW
}
