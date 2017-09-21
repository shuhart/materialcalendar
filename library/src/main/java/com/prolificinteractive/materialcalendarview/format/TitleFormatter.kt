package com.prolificinteractive.materialcalendarview.format

import com.prolificinteractive.materialcalendarview.CalendarDay

/**
 * Used to format a [com.prolificinteractive.materialcalendarview.CalendarDay] to a string for the month/year title
 */
interface TitleFormatter {

    /**
     * Converts the supplied day to a suitable month/year title
     *
     * @param day the day containing relevant month and year information
     * @return a label to display for the given month/year
     */
    fun format(day: CalendarDay): CharSequence
}
