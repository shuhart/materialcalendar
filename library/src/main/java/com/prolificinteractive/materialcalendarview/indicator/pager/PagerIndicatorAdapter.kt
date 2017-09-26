package com.prolificinteractive.materialcalendarview.indicator.pager

import android.database.DataSetObserver
import android.graphics.Color
import android.support.v4.view.PagerAdapter
import android.text.format.DateUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarPagerAdapter
import com.prolificinteractive.materialcalendarview.utils.DpUtils
import java.util.*

/**
 * Created by Bogdan Kornev
 * on 9/23/2017, 10:58 AM.
 */
class PagerIndicatorAdapter(private val pagerAdapter: CalendarPagerAdapter<*>) : PagerAdapter() {
    private val currentViews: ArrayDeque<FrameLayout> = ArrayDeque()
    var defaultButtonBackgroundColor = Color.parseColor("#f8f9f9")
    var defaultButtonTextColor = Color.parseColor("#1a1a1a")
    var selectedButtonBackgroundColor = Color.parseColor("#1398f5")
    var selectedButtonTextColor = Color.WHITE

    init {
        pagerAdapter.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                notifyDataSetChanged()
                invalidateSelectedMonths()
            }
        })
    }

    private fun invalidateSelectedMonths() {

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = SmartButton(container.context)
        val month = pagerAdapter.getItem(position)
        view.text = DateUtils.formatDateTime(container.context, month.date.time,
                DateUtils.FORMAT_NO_MONTH_DAY or DateUtils.FORMAT_SHOW_YEAR)
        view.tag = month
        initView(view, month)
        val parent = FrameLayout(container.context)
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        parent.addView(view, lp)
        currentViews.add(parent)
        container.addView(parent)
        return parent
    }

    private fun initView(view: SmartButton, month: CalendarDay) {
        val mainColor = if (pagerAdapter.getSelectedDates().any { it.month == month.month })
            selectedButtonBackgroundColor else defaultButtonBackgroundColor
        val textColor = if (mainColor == selectedButtonBackgroundColor)
            selectedButtonTextColor else defaultButtonTextColor
        view.init(mainColor = mainColor, mainTextColor = textColor,
                cornerRadius = 50)
        val dp184 = DpUtils.dpToPx(view.context, 184)
        view.minWidth = dp184
        view.height = DpUtils.dpToPx(view.context, 40)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getItemPosition(`object`: Any?): Int {
        if (`object` !is FrameLayout) {
            return PagerAdapter.POSITION_NONE
        }
        val index = indexOf(`object`.getChildAt(0).tag as CalendarDay)
        return if (index < 0) {
            PagerAdapter.POSITION_NONE
        } else index
    }

    fun indexOf(month: CalendarDay): Int = pagerAdapter.getIndexForDay(month)

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean = view == `object`

    override fun getCount(): Int = pagerAdapter.count

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as FrameLayout
        currentViews.remove(view)
        container.removeView(view)
    }
}