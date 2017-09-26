package com.prolificinteractive.materialcalendarview.indicator

import android.content.res.TypedArray
import android.view.View
import android.view.ViewGroup
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.TitleFormatter

/**
 * Created by Bogdan Kornev
 * on 9/22/2017, 5:43 PM.
 */
interface MonthIndicator {

    fun onMonthChanged(previous: CalendarDay, current: CalendarDay)

    fun updateUi(currentMonth: CalendarDay)

    fun getView(mcv: MaterialCalendarView,
                pager: CalendarPager, adapter: CalendarPagerAdapter<*>): View

    /**
     * @param typedArray do not call recycle() on it.
     */
    fun applyStyles(typedArray: TypedArray)

    fun setTitleFormatter(titleFormatter: TitleFormatter)

    fun setOnTitleClickListener(listener: View.OnClickListener)

    fun desiredHeightTileNumber(): Int = 1

    fun getDesiredLayoutParamsWidth(): Int = ViewGroup.LayoutParams.WRAP_CONTENT
}