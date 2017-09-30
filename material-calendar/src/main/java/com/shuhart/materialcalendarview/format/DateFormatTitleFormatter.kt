package com.shuhart.materialcalendarview.format

import com.shuhart.materialcalendarview.CalendarDay

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Format using a [java.text.DateFormat] instance.
 */
class DateFormatTitleFormatter : TitleFormatter {

    private val dateFormat: DateFormat

    /**
     * Format using "LLLL yyyy" for formatting
     */
    constructor() {
        this.dateFormat = SimpleDateFormat(
                "LLLL yyyy", Locale.getDefault()
        )
    }

    /**
     * Format using a specified [DateFormat]
     *
     * @param format the format to use
     */
    constructor(format: DateFormat) {
        this.dateFormat = format
    }

    override fun format(day: CalendarDay): CharSequence = dateFormat.format(day.date)
}
