package com.shuhart.materialcalendarview.format

import android.text.SpannableStringBuilder

import com.shuhart.materialcalendarview.CalendarDay

/**
 * Use an array to generate a month/year label
 */
class MonthArrayTitleFormatter
/**
 * Format using an array of month labels
 *
 * @param monthLabels an array of 12 labels to use for months, starting with January
 */
(private val monthLabels: Array<CharSequence>?) : TitleFormatter {

    init {
        if (monthLabels == null) {
            throw IllegalArgumentException("Label array cannot be null")
        }
        if (monthLabels.size < 12) {
            throw IllegalArgumentException("Label array is too short")
        }
    }


    override fun format(day: CalendarDay): CharSequence {
        return SpannableStringBuilder()
                .append(monthLabels?.getOrNull(day.month))
                .append(" ")
                .append(day.year.toString())
    }
}
