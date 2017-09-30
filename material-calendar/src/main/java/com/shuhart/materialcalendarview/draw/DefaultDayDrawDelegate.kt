package com.shuhart.materialcalendarview.draw

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.shuhart.materialcalendarview.utils.CalendarUtils
import com.shuhart.materialcalendarview.DayView
import com.shuhart.materialcalendarview.MaterialCalendarView

/**
 * Created by Bogdan Kornev
 * on 9/19/2017, 6:51 PM.
 */
class DefaultDayDrawDelegate(private val mcv: MaterialCalendarView) : DayDrawDelegate {
    private var selectionColor = Color.GRAY
    private var selectionRangeColor = Color.LTGRAY
    private var circlePaint: Paint = Paint().apply { color = Color.GRAY; style = Paint.Style.FILL; isAntiAlias = true }
    private var rangePaint: Paint = Paint().apply { color = Color.LTGRAY; style = Paint.Style.FILL; isAntiAlias = true }

    override fun onDraw(canvas: Canvas, dayDrawData: DayDrawData, dayView: DayView) {
        dayDrawData.apply {
            val selectedDays = mcv.selectedDates
            if (selectedDays.isEmpty()) {
                dayView.isChecked = false
                return
            }
            val date = dayView.date!!
            if (selectedDays.first() == date) {
                if (selectedDays.size > 1 && !CalendarUtils.isLastDayOfMonth(date) &&
                        !CalendarUtils.isLastDayOfWeek(date)) {
                    canvas.drawRect(firstRect, rangePaint)
                }
                canvas.drawCircle(cx, cy, radius, circlePaint)
                dayView.isChecked = true
            } else if (selectedDays.size > 1 && selectedDays.last() == date) {
                if (!CalendarUtils.isFirstDayOfMonth(date) &&
                        !CalendarUtils.isFirstDayOfWeek(date)) {
                    canvas.drawRect(lastRect, rangePaint)
                }
                canvas.drawCircle(cx, cy, radius, circlePaint)
                dayView.isChecked = true
            } else if (selectedDays.contains(date)) {
                when {
                    CalendarUtils.isFirstDayOfWeek(date) -> {
                        if (!CalendarUtils.isLastDayOfMonth(date)) {
                            canvas.drawRect(firstRect, rangePaint)
                        }
                        canvas.drawCircle(cx, cy, radius, rangePaint)
                    }
                    CalendarUtils.isLastDayOfWeek(date) -> {
                        if (!CalendarUtils.isFirstDayOfMonth(date)) {
                            canvas.drawRect(lastRect, rangePaint)
                        }
                        canvas.drawCircle(cx, cy, radius, rangePaint)
                    }
                    else -> {
                        when {
                            CalendarUtils.isFirstDayOfMonth(date) -> {
                                canvas.drawRect(firstRect, rangePaint)
                                canvas.drawCircle(cx, cy, radius, rangePaint)
                            }
                            CalendarUtils.isLastDayOfMonth(date) -> {
                                canvas.drawRect(lastRect, rangePaint)
                                canvas.drawCircle(cx, cy, radius, rangePaint)
                            }
                            else -> canvas.drawRect(rangeRect, rangePaint)
                        }
                    }
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