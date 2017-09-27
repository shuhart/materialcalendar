package com.shuhart.materialcalendarview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.TextView

import com.shuhart.materialcalendarview.format.WeekDayFormatter
import com.shuhart.materialcalendarview.utils.CalendarUtils

import java.util.Calendar

/**
 * Display a day of the week
 */
@SuppressLint("ViewConstructor")
class WeekDayView(context: Context, dayOfWeek: Int) : TextView(context) {

    private var formatter = WeekDayFormatter.DEFAULT
    private var dayOfWeek: Int = 0

    init {

        gravity = Gravity.CENTER

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        setDayOfWeek(dayOfWeek)
    }

    fun setWeekDayFormatter(formatter: WeekDayFormatter?) {
        this.formatter = formatter ?: WeekDayFormatter.DEFAULT
        setDayOfWeek(dayOfWeek)
    }

    fun setDayOfWeek(dayOfWeek: Int) {
        this.dayOfWeek = dayOfWeek
        text = formatter.format(dayOfWeek)
    }

    fun setDayOfWeek(calendar: Calendar) {
        setDayOfWeek(CalendarUtils.getDayOfWeek(calendar))
    }
}
