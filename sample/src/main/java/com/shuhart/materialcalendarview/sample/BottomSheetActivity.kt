package com.shuhart.materialcalendarview.sample

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.text.format.DateUtils
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.shuhart.materialcalendarview.*
import com.shuhart.materialcalendarview.utils.CalendarUtils
import com.shuhart.materialcalendarview.utils.DpUtils
import java.text.SimpleDateFormat

class BottomSheetActivity : AppCompatActivity(), OnDateSelectedListener, OnMonthChangedListener, OnRangeSelectedListener {
    private lateinit var widget: MaterialCalendarView
    private lateinit var textView: TextView

    private lateinit var behavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_sheet)
        if (Build.VERSION.SDK_INT >= 21) {
            window?.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = Color.TRANSPARENT

                decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        }

        val content: View = findViewById(R.id.main_content)
        val screenWidth = DpUtils.getDisplayHeightInPx(this)
        val offset = (0.15 * screenWidth).toInt()
        behavior = CustomBottomSheetBehavior(offset)
        val lp = content.layoutParams as CoordinatorLayout.LayoutParams
        lp.height = screenWidth - offset
        content.layoutParams = lp
        behavior.isHideable = true
        lp.behavior = behavior

        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED ||
                        newState == BottomSheetBehavior.STATE_HIDDEN) {
                    finish()
                }
            }
        })

        findViewById<View>(R.id.coordinator).setOnClickListener {
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        widget = findViewById(R.id.calendarView)

        widget.addOnDateChangedListener(this)
        widget.addOnRangeSelectedListener(this)
        widget.addOnMonthChangedListener(this)
        widget.selectionMode = MaterialCalendarView.SELECTION_MODE_RANGE
        textView = findViewById(R.id.textView)
        //Setup initial text
        textView.text = selectedDatesString

        findViewById<View>(R.id.clear).setOnClickListener({
            widget.clearSelection()
        })

        findViewById<View>(R.id.done).setOnClickListener {
            val intent: Intent?
            val dates = widget.selectedDates
            if (dates.isNotEmpty()) {
                intent = Intent()
                intent.putExtras(Bundle())
            } else {
                val date = widget.selectedDate
                if (date != null) {
                    intent = Intent()
                    intent.putExtras(Bundle())
                } else {
                    intent = null
                }
            }
            if (intent != null) {
                setResult(Activity.RESULT_OK, intent)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
        textView.text = selectedDatesString
    }

    override fun onRangeSelected(widget: MaterialCalendarView, dates: List<CalendarDay>) {
        textView.text = selectedDatesString
    }

    override fun onMonthChanged(widget: MaterialCalendarView, date: CalendarDay) {
        supportActionBar?.title = FORMATTER.format(date.date)
    }

    private val selectedDatesString: String
        get() {
            val dates = widget.selectedDates
            if (dates.isNotEmpty()) {
                return format(start = dates.first(), end = dates.last())
            }
            val date = widget.selectedDate ?: return getString(R.string.lifetime)
            return format(date)
        }

    private fun format(start: CalendarDay, end: CalendarDay? = null): String {
        if (end == null || start == end) {
            return DateUtils.formatDateTime(this, start.date.time, 0).toString()
        }
        if (CalendarUtils.isFirstDayOfMonth(start) && CalendarUtils.isLastDayOfMonth(end)) {
            return DateUtils.formatDateTime(this, start.date.time, DateUtils.FORMAT_NO_MONTH_DAY or DateUtils.FORMAT_SHOW_YEAR)
        }
        val correctedEnd = end.date.time + end.calendar.timeZone.rawOffset
        return DateUtils.formatDateRange(this, start.date.time, correctedEnd, DateUtils.FORMAT_SHOW_YEAR)
    }

    companion object {

        private val FORMATTER = SimpleDateFormat.getDateInstance()
    }
}
