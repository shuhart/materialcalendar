package com.prolificinteractive.materialcalendarview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.view.Gravity
import android.view.View
import android.widget.CheckedTextView
import com.prolificinteractive.materialcalendarview.MaterialCalendarView.ShowOtherDates
import com.prolificinteractive.materialcalendarview.draw.DrawDelegate
import com.prolificinteractive.materialcalendarview.format.DayFormatter

/**
 * Display one day of a [MaterialCalendarView]
 */
@SuppressLint("ViewConstructor")
class DayView(context: Context, day: CalendarDay,
              val drawDelegate: DrawDelegate) : CheckedTextView(context) {

    init {
        drawDelegate.dayView = this
    }

    var date: CalendarDay? = null
        private set


    private var formatter = DayFormatter.DEFAULT

    private var isInRange = true
    private var isInMonth = true
    private var isDecoratedDisabled = false
    @ShowOtherDates
    private var showOtherDates = MaterialCalendarView.SHOW_DEFAULTS

    init {
        gravity = Gravity.CENTER

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        setDay(day)
    }

    fun setDay(date: CalendarDay) {
        this.date = date
        text = label
    }

    /**
     * Set the new label formatter and reformat the current label. This preserves current spans.
     *
     * @param formatter new label formatter
     */
    fun setDayFormatter(formatter: DayFormatter?) {
        this.formatter = formatter ?: DayFormatter.DEFAULT
        val currentLabel = text
        var spans: Array<Any>? = null
        if (currentLabel is Spanned) {
            spans = currentLabel.getSpans(0, currentLabel.length, Any::class.java)
        }
        val newLabel = SpannableString(label)
        if (spans != null) {
            for (span in spans) {
                newLabel.setSpan(span, 0, newLabel.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        text = newLabel
    }

    val label: String
        get() = formatter.format(date!!)

    private fun setEnabled() {
        val enabled = isInMonth && isInRange
        super.setEnabled(isInRange)

        val showOtherMonths = MaterialCalendarView.showOtherMonths(showOtherDates)
        val showOutOfRange = MaterialCalendarView.showOutOfRange(showOtherDates) || showOtherMonths
        val showDecoratedDisabled = MaterialCalendarView.showDecoratedDisabled(showOtherDates)

        var shouldBeVisible = enabled

        if (!isInMonth && showOtherMonths) {
            shouldBeVisible = true
        }

        if (!isInRange && showOutOfRange) {
            shouldBeVisible = shouldBeVisible or isInMonth
        }

        if (isDecoratedDisabled && showDecoratedDisabled) {
            shouldBeVisible = shouldBeVisible or (isInMonth && isInRange)
        }

        if (!isInMonth && shouldBeVisible) {
            setTextColor(textColors.getColorForState(
                    intArrayOf(-android.R.attr.state_enabled), Color.GRAY))
        }
        visibility = if (shouldBeVisible) View.VISIBLE else View.INVISIBLE
    }

    fun setupSelection(@ShowOtherDates showOtherDates: Int, inRange: Boolean, inMonth: Boolean) {
        this.showOtherDates = showOtherDates
        this.isInMonth = inMonth
        this.isInRange = inRange
        setEnabled()
    }

    override fun onDraw(canvas: Canvas) {
        drawDelegate.onDraw(canvas)
        super.onDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        drawDelegate.calculateBounds(right - left, bottom - top)
    }

    fun setSelectionColor(color: Int) {
        drawDelegate.setSelectionColor(color)
    }

    fun setSelectionRangeColor(color: Int) {
        drawDelegate.setSelectionRangeColor(color)
    }

    fun setBottomTopDayPadding(padding: Int) {
        drawDelegate.setBottomTopDayPadding(padding)
    }
}
