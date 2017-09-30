package com.shuhart.materialcalendarview.format

/**
 * Use an array to supply week day labels
 */
class ArrayWeekDayFormatter
/**
 * @param weekDayLabels an array of 7 labels, starting with Sunday
 */
(private val weekDayLabels: Array<CharSequence>?) : WeekDayFormatter {

    init {
        if (weekDayLabels == null) {
            throw IllegalArgumentException("Cannot be null")
        }
        if (weekDayLabels.size != 7) {
            throw IllegalArgumentException("Array must contain exactly 7 elements")
        }
    }

    override fun format(dayOfWeek: Int): CharSequence =
            weekDayLabels?.getOrNull(dayOfWeek - 1) ?: ""
}
