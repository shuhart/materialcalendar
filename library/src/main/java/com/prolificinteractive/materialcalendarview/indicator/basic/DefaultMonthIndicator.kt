package com.prolificinteractive.materialcalendarview.indicator.basic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.view.View
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarPager
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import com.prolificinteractive.materialcalendarview.indicator.MonthIndicator

/**
 * Created by Bogdan Kornev
 * on 9/22/2017, 6:07 PM.
 */
@SuppressLint("ViewConstructor")
class DefaultMonthIndicator(context: Context) : MonthIndicator {
    override fun setOnTitleClickListener(listener: View.OnClickListener) {
        view.setOnTitleClickListener(listener)
    }

    override fun setTitleFormatter(titleFormatter: TitleFormatter) {
        view.setTitleFormatter(titleFormatter)
    }

    private val view: DefaultMonthIndicatorView = DefaultMonthIndicatorView(context)

    override fun onMonthChanged(previous: CalendarDay, current: CalendarDay) {
        view.setPreviousMonth(previous)
        updateUi(current)
    }

    override fun updateUi(currentMonth: CalendarDay) {
        view.updateUi(currentMonth)
    }

    override fun getView(mcv: MaterialCalendarView, pager: CalendarPager): View {
        view.init(pager, mcv)
        return view
    }

    override fun applyStyles(typedArray: TypedArray) {
        view.applyStyles(typedArray)
    }
}