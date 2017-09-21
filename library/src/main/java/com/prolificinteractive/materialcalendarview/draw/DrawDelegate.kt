package com.prolificinteractive.materialcalendarview.draw

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.prolificinteractive.materialcalendarview.CalendarUtils
import com.prolificinteractive.materialcalendarview.DayView
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

/**
 * Created by Bogdan Kornev
 * on 9/19/2017, 6:51 PM.
 */
class DrawDelegate(private val mcv: MaterialCalendarView) {
    private var selectionColor = Color.GRAY
    private var selectionRangeColor = Color.LTGRAY
    private val rangeRect = Rect()
    private val firstRect = Rect()
    private val lastRect = Rect()
    private var bottomTopDayPadding: Int = 0
    private var radius: Float = 0f
    private var cx: Float = 0f
    private var cy: Float = 0f
    private var circlePaint: Paint = Paint().apply { color = Color.GRAY; style = Paint.Style.FILL }
    private var rangePaint: Paint = Paint().apply { color = Color.LTGRAY; style = Paint.Style.FILL }
    lateinit var dayView: DayView

    fun onDraw(canvas: Canvas) {
        val selectedDays = mcv.selectedDates
        if (selectedDays.isEmpty()) return
        if (selectedDays.first() == dayView.date) {
            if (selectedDays.size > 1) {
                canvas.drawRect(firstRect, rangePaint)
            }
            canvas.drawCircle(cx, cy, radius, circlePaint)
            dayView.isChecked = true
        } else if (selectedDays.size > 1 && selectedDays.last() == dayView.date) {
            canvas.drawRect(lastRect, rangePaint)
            canvas.drawCircle(cx, cy, radius, circlePaint)
            dayView.isChecked = true
        } else if (selectedDays.contains(dayView.date)) {
            if (CalendarUtils.isFirstDayOfWeek(dayView.date!!)) {
                canvas.drawRect(firstRect, rangePaint)
                canvas.drawCircle(cx, cy, radius, rangePaint)
            } else if (CalendarUtils.isLastDayOfWeek(dayView.date!!)) {
                canvas.drawRect(lastRect, rangePaint)
                canvas.drawCircle(cx, cy, radius, rangePaint)
            } else {
                canvas.drawRect(rangeRect, rangePaint)
            }
            dayView.isChecked = false
        } else {
            dayView.isChecked = false
        }
    }

    fun calculateBounds(width: Int, height: Int) {
        val radius = Math.min(height, width)
        this.radius = radius / 2f - bottomTopDayPadding
        this.cx = width / 2f
        this.cy = height / 2f
        rangeRect.set(0, bottomTopDayPadding, width, height - bottomTopDayPadding)
        lastRect.set(0, bottomTopDayPadding, width / 2, height - bottomTopDayPadding)
        firstRect.set(width / 2, bottomTopDayPadding, width, height - bottomTopDayPadding)
    }

    fun setSelectionColor(color: Int) {
        selectionColor = color
        circlePaint.color = color
    }

    fun setSelectionRangeColor(color: Int) {
        selectionRangeColor = color
        rangePaint.color = color
    }

    fun setBottomTopDayPadding(padding: Int) {
        bottomTopDayPadding = padding
    }
}