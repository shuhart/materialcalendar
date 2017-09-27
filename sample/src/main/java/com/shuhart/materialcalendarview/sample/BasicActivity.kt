package com.shuhart.materialcalendarview.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.shuhart.materialcalendarview.CalendarDay
import com.shuhart.materialcalendarview.MaterialCalendarView
import com.shuhart.materialcalendarview.OnDateSelectedListener
import com.shuhart.materialcalendarview.OnMonthChangedListener
import java.text.SimpleDateFormat

/**
 * Shows off the most basic usage
 */
class BasicActivity : AppCompatActivity(), OnDateSelectedListener, OnMonthChangedListener {
    private lateinit var widget: MaterialCalendarView
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)
        widget = findViewById(R.id.calendarView)

        widget.addOnDateChangedListener(this)
        widget.addOnMonthChangedListener(this)
        widget.selectionMode = MaterialCalendarView.SELECTION_MODE_RANGE
        textView = findViewById(R.id.textView)
        //Setup initial text
        textView.text = selectedDatesString
    }

    override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
        textView.text = selectedDatesString
    }

    override fun onMonthChanged(widget: MaterialCalendarView, date: CalendarDay) {
        supportActionBar?.title = FORMATTER.format(date.date)
    }

    private val selectedDatesString: String
        get() {
            val date = widget.selectedDate ?: return "No Selection"
            return FORMATTER.format(date.date)
        }

    companion object {

        private val FORMATTER = SimpleDateFormat.getDateInstance()
    }
}
