package com.prolificinteractive.materialcalendarview.indicator

import android.content.res.TypedArray
import android.view.View
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarPager
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.format.TitleFormatter

/**
 * Created by Bogdan Kornev
 * on 9/22/2017, 5:43 PM.
 */
interface MonthIndicator {

    fun onMonthChanged(previous: CalendarDay, current: CalendarDay)

    fun updateUi(currentMonth: CalendarDay)

    fun getView(mcv: MaterialCalendarView,
                pager: CalendarPager): View

    /**
     * @param typedArray do not call recycle() on it.
     */
    fun applyStyles(typedArray: TypedArray)

    fun setTitleFormatter(titleFormatter: TitleFormatter)

    fun setOnTitleClickListener(listener: View.OnClickListener)
}