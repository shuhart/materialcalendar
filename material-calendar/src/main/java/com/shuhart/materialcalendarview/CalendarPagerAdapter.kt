package com.shuhart.materialcalendarview

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.shuhart.materialcalendarview.MaterialCalendarView.ShowOtherDates
import com.shuhart.materialcalendarview.draw.DayDrawDataProvider
import com.shuhart.materialcalendarview.draw.DayDrawDelegate
import com.shuhart.materialcalendarview.format.DayFormatter
import com.shuhart.materialcalendarview.format.TitleFormatter
import com.shuhart.materialcalendarview.format.WeekDayFormatter
import java.util.*

/**
 * Pager adapter backing the calendar view
 */
abstract class CalendarPagerAdapter<V : CalendarPagerView>(val mcv: MaterialCalendarView) : PagerAdapter() {

    private val currentViews: ArrayDeque<V> = ArrayDeque()
    private val today: CalendarDay = CalendarDay.today()

    private var titleFormatter: TitleFormatter? = null
    private var bottomTopDayPadding: Int? = null
    private var dayDrawDelegate: DayDrawDelegate? = null
    private var dayDrawDataProvider: DayDrawDataProvider? = null
    var dateTextAppearance: Int = 0
        set(taId) {
            if (taId == 0) {
                return
            }
            field = taId
            for (pagerView in currentViews) {
                pagerView.setDateTextAppearance(taId)
            }
        }
    var weekDayTextAppearance: Int = 0
        set(taId) {
            if (taId == 0) {
                return
            }
            field = taId
            for (pagerView in currentViews) {
                pagerView.setWeekDayTextAppearance(taId)
            }
        }
    @ShowOtherDates
    private var showOtherDates = MaterialCalendarView.SHOW_DEFAULTS
    private var minDate: CalendarDay? = null
    private var maxDate: CalendarDay? = null
    lateinit var rangeIndex: DateRangeIndex
        private set
    private var selectedDates: MutableList<CalendarDay> = ArrayList()
    private var weekDayFormatter = WeekDayFormatter.DEFAULT
    private var dayFormatter = DayFormatter.DEFAULT
    private var selectionEnabled = true

    init {
        currentViews.iterator()
        setRangeDates(null, null)
    }

    override fun getCount(): Int = rangeIndex.getCount()

    override fun getPageTitle(position: Int): CharSequence =
            if (titleFormatter == null) "" else titleFormatter!!.format(getItem(position))

    fun migrateStateAndReturn(newAdapter: CalendarPagerAdapter<*>): CalendarPagerAdapter<*> {
        newAdapter.titleFormatter = titleFormatter
        newAdapter.dateTextAppearance = dateTextAppearance
        newAdapter.weekDayTextAppearance = weekDayTextAppearance
        newAdapter.showOtherDates = showOtherDates
        newAdapter.minDate = minDate
        newAdapter.maxDate = maxDate
        newAdapter.selectedDates = selectedDates
        newAdapter.weekDayFormatter = weekDayFormatter
        newAdapter.dayFormatter = dayFormatter
        newAdapter.selectionEnabled = selectionEnabled
        return newAdapter
    }

    fun getIndexForDay(day: CalendarDay?): Int {
        if (day == null) {
            return count / 2
        }
        if (minDate != null && day.isBefore(minDate!!)) {
            return 0
        }
        return if (maxDate != null && day.isAfter(maxDate!!)) {
            count - 1
        } else rangeIndex.indexOf(day)
    }

    protected abstract fun createView(position: Int): V

    protected abstract fun indexOf(view: V): Int

    protected abstract fun isInstanceOfView(`object`: Any?): Boolean

    protected abstract fun createRangeIndex(min: CalendarDay, max: CalendarDay): DateRangeIndex

    @Suppress("UNCHECKED_CAST")
    override fun getItemPosition(`object`: Any?): Int {
        if (!isInstanceOfView(`object`)) {
            return PagerAdapter.POSITION_NONE
        }
//        val pagerView = `object` as CalendarPagerView?
//        pagerView!!.firstViewDay ?: return PagerAdapter.POSITION_NONE
        val index = indexOf(`object` as V)
        return if (index < 0) {
            PagerAdapter.POSITION_NONE
        } else index
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pagerView = createView(position)
        pagerView.contentDescription = mcv.calendarContentDescription
        pagerView.alpha = 0f
        pagerView.setSelectionEnabled(selectionEnabled)

        pagerView.setWeekDayFormatter(weekDayFormatter)
        pagerView.setDayFormatter(dayFormatter)
        if (bottomTopDayPadding != null) {
            pagerView.setBottomTopDayPadding(bottomTopDayPadding!!)
        }
        if (dateTextAppearance != 0) {
            pagerView.setDateTextAppearance(dateTextAppearance)
        }
        if (weekDayTextAppearance != 0) {
            pagerView.setWeekDayTextAppearance(weekDayTextAppearance)
        }
        pagerView.showOtherDates = showOtherDates
        pagerView.setMinimumDate(minDate)
        pagerView.setMaximumDate(maxDate)
        pagerView.setSelectedDates(selectedDates)

        container.addView(pagerView)
        currentViews.add(pagerView)
        return pagerView
    }

    fun setSelectionEnabled(enabled: Boolean) {
        selectionEnabled = enabled
        for (pagerView in currentViews) {
            pagerView.setSelectionEnabled(selectionEnabled)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val pagerView = `object` as CalendarPagerView
        currentViews.remove(pagerView)
        container.removeView(pagerView)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    fun setTitleFormatter(titleFormatter: TitleFormatter) {
        this.titleFormatter = titleFormatter
    }

    fun setShowOtherDates(@ShowOtherDates showFlags: Int) {
        this.showOtherDates = showFlags
        for (pagerView in currentViews) {
            pagerView.showOtherDates = showFlags
        }
    }

    fun setWeekDayFormatter(formatter: WeekDayFormatter) {
        this.weekDayFormatter = formatter
        for (pagerView in currentViews) {
            pagerView.setWeekDayFormatter(formatter)
        }
    }

    fun setDayFormatter(formatter: DayFormatter) {
        this.dayFormatter = formatter
        for (pagerView in currentViews) {
            pagerView.setDayFormatter(formatter)
        }
    }

    @ShowOtherDates
    fun getShowOtherDates(): Int = showOtherDates

    @Suppress("NAME_SHADOWING")
    fun setRangeDates(min: CalendarDay?, max: CalendarDay?) {
        var min = min
        var max = max
        this.minDate = min
        this.maxDate = max
        for (pagerView in currentViews) {
            pagerView.setMinimumDate(min)
            pagerView.setMaximumDate(max)
        }

        if (min == null) {
            min = CalendarDay.from(today.year - 200, today.month, today.day)
        }

        if (max == null) {
            max = CalendarDay.from(today.year + 200, today.month, today.day)
        }

        rangeIndex = createRangeIndex(min, max)

        notifyDataSetChanged()
        invalidateSelectedDates()
    }

    fun clearSelections() {
        selectedDates.clear()
        invalidateSelectedDates()
    }

    fun setDateSelected(day: CalendarDay, selected: Boolean) {
        if (selected) {
            if (!selectedDates.contains(day)) {
                selectedDates.add(day)
                invalidateSelectedDates()
            }
        } else {
            if (selectedDates.contains(day)) {
                selectedDates.remove(day)
                invalidateSelectedDates()
            }
        }
    }

    private fun invalidateSelectedDates() {
        validateSelectedDates()
        for (pagerView in currentViews) {
            pagerView.setSelectedDates(selectedDates)
        }
    }

    private fun validateSelectedDates() {
        var i = 0
        while (i < selectedDates.size) {
            val date = selectedDates[i]

            if (minDate != null && minDate!!.isAfter(date) || maxDate != null && maxDate!!.isBefore(date)) {
                selectedDates.removeAt(i)
                mcv.onDateUnselected(date)
                i -= 1
            }
            i++
        }
    }

    fun getItem(position: Int): CalendarDay = rangeIndex.getItem(position)

    fun getSelectedDates(): List<CalendarDay> = Collections.unmodifiableList(selectedDates.sortedBy { it.date.time })

    fun setBottomTopDayPadding(padding: Int) {
        bottomTopDayPadding = padding
        for (pagerView in currentViews) {
            pagerView.setBottomTopDayPadding(padding)
        }
    }

    fun setDayDrawDelegate(dayDrawDelegate: DayDrawDelegate) {
        this.dayDrawDelegate = dayDrawDelegate
        for (pagerView in currentViews) {
            pagerView.setDayDrawDelegate(dayDrawDelegate)
        }
    }

    fun setDayDrawDataProvider(dayDrawDataProvider: DayDrawDataProvider) {
        this.dayDrawDataProvider = dayDrawDataProvider
        for (pagerView in currentViews) {
            pagerView.setDayDrawDataProvider(dayDrawDataProvider)
        }
    }
}
