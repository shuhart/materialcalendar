package com.prolificinteractive.materialcalendarview

/**
 * Decorate Day views with drawables and text manipulation
 */
interface DayViewDecorator {

    /**
     * Determine if a specific day should be decorated
     *
     * @param day [CalendarDay] to possibly decorate
     * @return true if this decorator should be applied to the provided day
     */
    fun shouldDecorate(day: CalendarDay): Boolean

    /**
     * Set decoration options onto a facade to be applied to all relevant days
     *
     * @param view View to decorate
     */
    fun decorate(view: DayViewFacade)

}
