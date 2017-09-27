package com.shuhart.materialcalendarview

/**
 * The callback used to indicate a range has been selected
 */
interface OnRangeSelectedListener {

    /**
     * Called when a user selects a range of days.
     * There is no logic to prevent multiple calls for the same date and state.
     *
     * @param widget   the view associated with this listener
     * @param dates     the dates in the range, in ascending order
     */
    fun onRangeSelected(widget: MaterialCalendarView, dates: List<CalendarDay>)
}
