package com.shuhart.materialcalendarview.indicator.pager

import android.content.Context
import android.content.res.TypedArray
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.MotionEvent
import com.shuhart.materialcalendarview.*
import com.shuhart.materialcalendarview.utils.DpUtils

/**
 * Created by Bogdan Kornev
 * on 9/23/2017, 10:41 AM.
 */
class CustomPager(context: Context) : ViewPager(context) {
    private lateinit var pagerIndicatorAdapter: PagerIndicatorAdapter
    fun updateUi(currentMonth: CalendarDay) {
        currentItem = pagerIndicatorAdapter.indexOf(currentMonth)
    }

    fun init(pager: CalendarPager, mcv: MaterialCalendarView, calendarPagerAdapter: CalendarPagerAdapter<*>) {
        pagerIndicatorAdapter = PagerIndicatorAdapter(calendarPagerAdapter)
        adapter = pagerIndicatorAdapter
        mcv.addOnRangeSelectedListener(object : OnRangeSelectedListener {
            override fun onRangeSelected(widget: MaterialCalendarView, dates: List<CalendarDay>) {
                Log.d("CustomPager", "onRangeSelected(${dates.firstOrNull()}, ${dates.lastOrNull()})")
                pagerIndicatorAdapter.selectMonths(start = dates.firstOrNull(), end = dates.lastOrNull())
            }
        })
        mcv.addOnDateChangedListener(object : OnDateSelectedListener {
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
                Log.d("CustomPager", "onDateSelected($date, selected=$selected)")
                if (selected) {
                    pagerIndicatorAdapter.selectMonths(start = date)
                } else {
                    pagerIndicatorAdapter.invalidateSelectedMonths()
                }
            }
        })
        offscreenPageLimit = 9
        pageMargin = DpUtils.dpToPx(context, 16)
    }

    fun applyStyles(typedArray: TypedArray) {

    }

    fun onMonthChanged(previous: CalendarDay, current: CalendarDay) {
        val previousIndex = pagerIndicatorAdapter.indexOf(previous)
        val currentIndex = pagerIndicatorAdapter.indexOf(current)
        setCurrentItem(currentIndex, Math.abs(currentIndex - previousIndex) == 1)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}