package com.prolificinteractive.materialcalendarview.sample

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import com.prolificinteractive.materialcalendarview.utils.DpUtils
import java.text.SimpleDateFormat

class BottomSheetActivity : AppCompatActivity(), OnDateSelectedListener, OnMonthChangedListener {
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
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

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
