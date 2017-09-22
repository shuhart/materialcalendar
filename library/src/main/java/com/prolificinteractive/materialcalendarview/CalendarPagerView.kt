package com.prolificinteractive.materialcalendarview

import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.prolificinteractive.materialcalendarview.draw.DayDrawDataProvider
import com.prolificinteractive.materialcalendarview.draw.DayDrawDelegate
import com.prolificinteractive.materialcalendarview.draw.DefaultDayDrawDataProvider
import com.prolificinteractive.materialcalendarview.format.DayFormatter
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter
import com.prolificinteractive.materialcalendarview.utils.CalendarUtils
import java.util.*
import java.util.Calendar.DATE

abstract class CalendarPagerView(private val mcv: MaterialCalendarView,
                                 val firstViewDay: CalendarDay,
                                 protected val firstDayOfWeek: Int) : ViewGroup(mcv.context), View.OnClickListener {
    var showOtherDates = MaterialCalendarView.SHOW_DEFAULTS
        set(value) {
            field = value
            updateUi()
        }
    private val weekDayViews = ArrayList<WeekDayView>()
    private var minDate: CalendarDay? = null
    private var maxDate: CalendarDay? = null

    private val dayViews = ArrayList<DayView>()

    init {
        clipChildren = false
        clipToPadding = false

        buildWeekDays(resetAndGetWorkingCalendar())
        buildDayViews(dayViews, resetAndGetWorkingCalendar())
    }

    private fun buildWeekDays(calendar: Calendar) {
        for (i in 0 until DEFAULT_DAYS_IN_WEEK) {
            val weekDayView = WeekDayView(context, CalendarUtils.getDayOfWeek(calendar))
            weekDayViews.add(weekDayView)
            addView(weekDayView)
            calendar.add(DATE, 1)
        }
    }

    protected fun addDayView(dayViews: MutableCollection<DayView>, calendar: Calendar) {
        val day = CalendarDay.from(calendar) ?: return
        val dayView = DayView(context, day,
                mcv.dayDrawDelegate,
                DefaultDayDrawDataProvider())
        dayView.setOnClickListener(this)
        dayViews.add(dayView)
        addView(dayView, LayoutParams())

        calendar.add(DATE, 1)
    }

    protected fun resetAndGetWorkingCalendar(): Calendar {
        firstViewDay.copyTo(tempWorkingCalendar)
        tempWorkingCalendar.firstDayOfWeek = firstDayOfWeek
        val dow = CalendarUtils.getDayOfWeek(tempWorkingCalendar)
        var delta = firstDayOfWeek - dow
        //If the delta is positive, we want to remove a week
        val removeRow = if (MaterialCalendarView.showOtherMonths(showOtherDates)) delta >= 0 else delta > 0
        if (removeRow) {
            delta -= DEFAULT_DAYS_IN_WEEK
        }
        tempWorkingCalendar.add(DATE, delta)
        return tempWorkingCalendar
    }

    protected abstract fun buildDayViews(dayViews: MutableCollection<DayView>, calendar: Calendar)

    protected abstract fun isDayEnabled(day: CalendarDay): Boolean

    fun setWeekDayTextAppearance(taId: Int) {
        for (weekDayView in weekDayViews) {
            weekDayView.setTextAppearance(context, taId)
        }
    }

    fun setDateTextAppearance(taId: Int) {
        for (dayView in dayViews) {
            dayView.setTextAppearance(context, taId)
        }
    }

    fun setSelectionEnabled(selectionEnabled: Boolean) {
        for (dayView in dayViews) {
            dayView.setOnClickListener(if (selectionEnabled) this else null)
            dayView.isClickable = selectionEnabled
        }
    }

    fun setWeekDayFormatter(formatter: WeekDayFormatter) {
        for (dayView in weekDayViews) {
            dayView.setWeekDayFormatter(formatter)
        }
    }

    fun setDayFormatter(formatter: DayFormatter) {
        for (dayView in dayViews) {
            dayView.setDayFormatter(formatter)
        }
    }

    fun setMinimumDate(minDate: CalendarDay?) {
        this.minDate = minDate
        updateUi()
    }

    fun setMaximumDate(maxDate: CalendarDay?) {
        this.maxDate = maxDate
        updateUi()
    }

    fun setSelectedDates(dates: Collection<CalendarDay>?) {
        for (dayView in dayViews) {
            dayView.invalidate()
        }
        postInvalidate()
    }

    protected fun updateUi() {
        for (dayView in dayViews) {
            val day = dayView.date ?: continue
            dayView.setupSelection(
                    showOtherDates, day.isInRange(minDate, maxDate), isDayEnabled(day))
        }
        postInvalidate()
    }

    override fun onClick(v: View) {
        if (v is DayView) {
            mcv.onDateClicked(v)
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams = LayoutParams()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val specWidthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val specWidthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val specHeightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val specHeightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        //We expect to be somewhere inside a MaterialCalendarView, which should measure EXACTLY
        if (specHeightMode == View.MeasureSpec.UNSPECIFIED || specWidthMode == View.MeasureSpec.UNSPECIFIED) {
            throw IllegalStateException("CalendarPagerView should never be left to decide it's size")
        }

        //The spec width should be a correct multiple
        val measureTileWidth = specWidthSize / DEFAULT_DAYS_IN_WEEK
        val measureTileHeight = specHeightSize / rows

        //Just use the spec sizes
        setMeasuredDimension(specWidthSize, specHeightSize)

        val count = childCount

        for (i in 0 until count) {
            val child = getChildAt(i)

            val childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    measureTileWidth,
                    View.MeasureSpec.EXACTLY
            )

            val childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    measureTileHeight,
                    View.MeasureSpec.EXACTLY
            )

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }
    }

    /**
     * @return the number of rows to display per page
     */
    protected abstract val rows: Int

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val count = childCount

        val parentLeft = 0

        var childTop = 0
        var childLeft = parentLeft

        for (i in 0 until count) {
            val child = getChildAt(i)

            val width = child.measuredWidth
            val height = child.measuredHeight

            child.layout(childLeft, childTop, childLeft + width, childTop + height)

            childLeft += width

            //We should warp every so many children
            if (i % DEFAULT_DAYS_IN_WEEK == DEFAULT_DAYS_IN_WEEK - 1) {
                childLeft = parentLeft
                childTop += height
            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams = LayoutParams()

    override fun shouldDelayChildPressedState(): Boolean = false

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean = p is LayoutParams

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams =
            LayoutParams()


    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        event.className = CalendarPagerView::class.java.name
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.className = CalendarPagerView::class.java.name
    }

    fun setBottomTopDayPadding(padding: Int) {
        for (dayView in dayViews) {
            dayView.setBottomTopDayPadding(padding)
        }
    }

    fun setDayDrawDelegate(dayDrawDelegate: DayDrawDelegate) {
        for (dayView in dayViews) {
            dayView.drawDelegate = dayDrawDelegate
        }
    }

    fun setDayDrawDataProvider(dayDrawDataProvider: DayDrawDataProvider) {
        for (dayView in dayViews) {
            dayView.drawDataProvider = dayDrawDataProvider.copy()
        }
    }

    /**
     * Simple layout params class for MonthView, since every child is the same size
     */
    class LayoutParams : ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    companion object {
        @JvmStatic
        protected val DEFAULT_DAYS_IN_WEEK = 7
        @JvmStatic
        protected val DEFAULT_MAX_WEEKS = 6
        @JvmStatic
        protected val DAY_NAMES_ROW = 1
        private val tempWorkingCalendar = CalendarUtils.instance

    }
}
