package com.prolificinteractive.materialcalendarview.draw

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.prolificinteractive.materialcalendarview.CalendarUtils
import com.prolificinteractive.materialcalendarview.DayView
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

/**
 * Created by Bogdan Kornev
 * on 9/19/2017, 6:51 PM.
 */
class DefaultDayDrawDelegate(private val mcv: MaterialCalendarView) : DayDrawDelegate {
    private var selectionColor = Color.GRAY
    private var selectionRangeColor = Color.LTGRAY
    private var circlePaint: Paint = Paint().apply { color = Color.GRAY; style = Paint.Style.FILL }
    private var rangePaint: Paint = Paint().apply { color = Color.LTGRAY; style = Paint.Style.FILL }

    override fun onDraw(canvas: Canvas, dayDrawData: DayDrawData, dayView: DayView) {
        dayDrawData.apply {
            val selectedDays = mcv.selectedDates
            if (selectedDays.isEmpty()) {
                dayView.isChecked = false
                return
            }
            if (selectedDays.first() == dayView.date) {
                if (selectedDays.size > 1 && !CalendarUtils.isLastDayOfMonth(dayView.date!!)) {
                    canvas.drawRect(firstRect, rangePaint)
                }
                canvas.drawCircle(cx, cy, radius, circlePaint)
                dayView.isChecked = true
            } else if (selectedDays.size > 1 && selectedDays.last() == dayView.date) {
                if (!CalendarUtils.isIFirstDayOfMonth(dayView.date!!)) {
                    canvas.drawRect(lastRect, rangePaint)
                }
                canvas.drawCircle(cx, cy, radius, circlePaint)
                dayView.isChecked = true
            } else if (selectedDays.contains(dayView.date)) {
                when {
                    CalendarUtils.isFirstDayOfWeek(dayView.date!!) -> {
                        if (!CalendarUtils.isLastDayOfMonth(dayView.date!!)) {
                            canvas.drawRect(firstRect, rangePaint)
                        }
                        canvas.drawCircle(cx, cy, radius, rangePaint)
                    }
                    CalendarUtils.isLastDayOfWeek(dayView.date!!) -> {
                        if (!CalendarUtils.isIFirstDayOfMonth(dayView.date!!)) {
                            canvas.drawRect(lastRect, rangePaint)
                        }
                        canvas.drawCircle(cx, cy, radius, rangePaint)
                    }
                    else -> canvas.drawRect(rangeRect, rangePaint)
                }
                dayView.isChecked = false
            } else {
                dayView.isChecked = false
            }
        }
    }

    override fun setSelectionColor(color: Int) {
        selectionColor = color
        circlePaint.color = color
    }

    override fun setSelectionRangeColor(color: Int) {
        selectionRangeColor = color
        rangePaint.color = color
    }
}