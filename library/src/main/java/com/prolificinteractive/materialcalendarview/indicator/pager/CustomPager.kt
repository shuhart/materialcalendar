package com.prolificinteractive.materialcalendarview.indicator.pager

import android.content.Context
import android.content.res.TypedArray
import android.support.v4.view.ViewPager
import android.view.MotionEvent
import android.view.View
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarPager
import com.prolificinteractive.materialcalendarview.CalendarPagerAdapter
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.utils.DpUtils

/**
 * Created by Bogdan Kornev
 * on 9/23/2017, 10:41 AM.
 */
class CustomPager(context: Context) : ViewPager(context) {
    fun updateUi(currentMonth: CalendarDay) {
        currentItem = (adapter as PagerIndicatorAdapter).indexOf(currentMonth)
    }

    fun init(pager: CalendarPager, mcv: MaterialCalendarView, calendarPagerAdapter: CalendarPagerAdapter<*>) {
        adapter = PagerIndicatorAdapter(calendarPagerAdapter)
    }

    fun applyStyles(typedArray: TypedArray) {

    }

    fun onMonthChanged(previous: CalendarDay, current: CalendarDay) {
        if (adapter !is PagerIndicatorAdapter) return
        val adapter = adapter as PagerIndicatorAdapter
        val previousIndex = adapter.indexOf(previous)
        val currentIndex = adapter.indexOf(current)
        setCurrentItem(currentIndex, Math.abs(currentIndex - previousIndex) == 1)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val specWidthSize = View.MeasureSpec.getSize(widthMeasureSpec)
//        pageMargin = (0.2 * specWidthSize).toInt()
//        pageMargin = DpUtils.dpToPx(context, -140)
    }
}