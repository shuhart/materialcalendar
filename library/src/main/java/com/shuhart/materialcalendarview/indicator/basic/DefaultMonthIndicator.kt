package com.shuhart.materialcalendarview.indicator.basic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.view.View
import com.shuhart.materialcalendarview.CalendarDay
import com.shuhart.materialcalendarview.CalendarPager
import com.shuhart.materialcalendarview.CalendarPagerAdapter
import com.shuhart.materialcalendarview.MaterialCalendarView
import com.shuhart.materialcalendarview.format.TitleFormatter
import com.shuhart.materialcalendarview.indicator.MonthIndicator

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

    override fun getView(mcv: MaterialCalendarView, pager: CalendarPager, adapter: CalendarPagerAdapter<*>): View {
        view.init(pager, mcv)
        return view
    }

    override fun applyStyles(typedArray: TypedArray) {
        view.applyStyles(typedArray)
    }
}