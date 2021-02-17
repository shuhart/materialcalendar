package com.shuhart.materialcalendarview

import android.content.Context
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Custom ViewPager that allows swiping to be disabled.
 */
class CalendarPager(context: Context) : ViewPager(context) {

    var isPagingEnabled = true

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean =
            isPagingEnabled && super.onInterceptTouchEvent(ev)

    override fun onTouchEvent(ev: MotionEvent): Boolean = isPagingEnabled && super.onTouchEvent(ev)

    /**
     * disables scrolling vertically when paging disabled, fixes scrolling
     * for nested [android.support.v4.view.ViewPager]
     */
    override fun canScrollVertically(direction: Int): Boolean =
            isPagingEnabled && super.canScrollVertically(direction)

    /**
     * disables scrolling horizontally when paging disabled, fixes scrolling
     * for nested [android.support.v4.view.ViewPager]
     */
    override fun canScrollHorizontally(direction: Int): Boolean =
            isPagingEnabled && super.canScrollHorizontally(direction)

}
