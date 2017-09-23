package com.prolificinteractive.materialcalendarview.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import butterknife.ButterKnife
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import java.text.SimpleDateFormat

/**
 * Shows off the most basic usage
 */
class BasicActivity : AppCompatActivity(), OnDateSelectedListener, OnMonthChangedListener {
    lateinit var widget: MaterialCalendarView
    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)
        ButterKnife.bind(this)
        widget = findViewById(R.id.calendarView)

        widget.setOnDateChangedListener(this)
        widget.setOnMonthChangedListener(this)
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
