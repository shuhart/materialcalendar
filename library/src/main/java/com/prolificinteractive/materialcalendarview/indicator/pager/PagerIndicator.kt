package com.prolificinteractive.materialcalendarview.indicator.pager

import android.content.Context
import android.content.res.TypedArray
import android.view.View
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import com.prolificinteractive.materialcalendarview.indicator.MonthIndicator

/**
 * Created by Bogdan Kornev
 * on 9/23/2017, 10:40 AM.
 */
class PagerIndicator(context: Context) : MonthIndicator {
    private val view = CustomPager(context)

    override fun setOnTitleClickListener(listener: View.OnClickListener) {}

    override fun setTitleFormatter(titleFormatter: TitleFormatter) {}

    override fun onMonthChanged(previous: CalendarDay, current: CalendarDay) {
        view.onMonthChanged(previous, current)
    }

    override fun updateUi(currentMonth: CalendarDay) {
        view.updateUi(currentMonth)
    }

    override fun getView(mcv: MaterialCalendarView, pager: CalendarPager, adapter: CalendarPagerAdapter<*>): View {
        view.init(pager, mcv, adapter)
        return view
    }

    override fun applyStyles(typedArray: TypedArray) {
        view.applyStyles(typedArray)
    }

    override fun desiredHeightTileNumber(): Int = 2
}