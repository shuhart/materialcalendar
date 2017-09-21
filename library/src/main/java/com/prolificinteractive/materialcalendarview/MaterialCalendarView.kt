@file:Suppress("unused", "MemberVisibilityCanPrivate", "NAME_SHADOWING", "LeakingThis")

package com.prolificinteractive.materialcalendarview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.ArrayRes
import android.support.annotation.IntDef
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.SparseArray
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.prolificinteractive.materialcalendarview.draw.DayDrawDataProvider
import com.prolificinteractive.materialcalendarview.draw.DayDrawDelegate
import com.prolificinteractive.materialcalendarview.draw.DefaultDayDrawDataProvider
import com.prolificinteractive.materialcalendarview.draw.DefaultDayDrawDelegate
import com.prolificinteractive.materialcalendarview.format.*
import java.util.*

/**
 *
 *
 * This class is a calendar widget for displaying and selecting dates.
 * The range of dates supported by this calendar is configurable.
 * A user can select a date by taping on it and can page the calendar to a desired date.
 *
 *
 *
 * By default, the range of dates shown is from 200 years in the past to 200 years in the future.
 * This can be extended or shortened by configuring the minimum and maximum dates.
 *
 *
 *
 * When selecting a date out of range, or when the range changes so the selection becomes outside,
 * The date closest to the previous selection will become selected. This will also trigger the
 * [OnDateSelectedListener]
 *
 *
 *
 * **Note:** if this view's size isn't divisible by 7,
 * the contents will be centered inside such that the days in the calendar are equally square.
 * For example, 600px isn't divisible by 7, so a tile size of 85 is choosen, making the calendar
 * 595px wide. The extra 5px are distributed left and right to get to 600px.
 *
 */
open class MaterialCalendarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewGroup(context, attrs) {

    /**
     * [IntDef] annotation for selection mode.
     *
     * @see .setSelectionMode
     * @see .getSelectionMode
     */
    @Retention(AnnotationRetention.RUNTIME)
    @IntDef(SELECTION_MODE_NONE.toLong(), SELECTION_MODE_SINGLE.toLong(), SELECTION_MODE_MULTIPLE.toLong(), SELECTION_MODE_RANGE.toLong())
    annotation class SelectionMode

    /**
     * [IntDef] annotation for showOtherDates.
     *
     * @see .setShowOtherDates
     * @see .getShowOtherDates
     */
    @SuppressLint("UniqueConstants")
    @Retention(AnnotationRetention.RUNTIME)
    @IntDef(flag = true, value = *longArrayOf(SHOW_NONE.toLong(), SHOW_ALL.toLong(), SHOW_DEFAULTS.toLong(), SHOW_OUT_OF_RANGE.toLong(), SHOW_OTHER_MONTHS.toLong(), SHOW_DECORATED_DISABLED.toLong()))
    annotation class ShowOtherDates

    private val titleChanger: TitleChanger

    private val title: TextView
    private val buttonPast: DirectionButton
    private val buttonFuture: DirectionButton
    private val pager: CalendarPager?
    private var adapter: CalendarPagerAdapter<*>? = null
    private var currentMonth: CalendarDay? = null
    private var topbar: LinearLayout? = null
    private var calendarMode: CalendarMode? = null
    /**
     * By default, the calendar will take up all the space needed to show any month (6 rows).
     * By enabling dynamic height, the view will change height dependant on the visible month.
     *
     *
     * This means months that only need 5 or 4 rows to show the entire month will only take up
     * that many rows, and will grow and shrink as necessary.
     *
     * true to have the view different heights based on the visible month
     */
    var isDynamicHeightEnabled: Boolean = false

    private val dayViewDecorators = ArrayList<DayViewDecorator>()

    /**
     * @return the minimum selectable date for the calendar, if any
     */
    var minimumDate: CalendarDay? = null
        private set
    /**
     * @return the maximum selectable date for the calendar, if any
     */
    var maximumDate: CalendarDay? = null
        private set

    private var listener: OnDateSelectedListener? = null
    private var monthListener: OnMonthChangedListener? = null
    private var rangeListener: OnRangeSelectedListener? = null

    var calendarContentDescription: CharSequence? = null
        get() = if (field != null)
            field!!
        else
            context.getString(R.string.calendar)
    var selectionColor = 0
        set(color) {
            var color = color
            if (color == 0) {
                if (!isInEditMode) {
                    return
                } else {
                    color = Color.GRAY
                }
            }
            field = color
            dayDrawDelegate.setSelectionColor(color)
            invalidate()
        }
    var selectionRangeColor = 0
        set(color) {
            var color = color
            if (color == 0) {
                if (!isInEditMode) {
                    return
                } else {
                    color = Color.LTGRAY
                }
            }
            field = color
            dayDrawDelegate.setSelectionRangeColor(color)
            invalidate()
        }
    var bottomTopDayPadding = 0
        set(value) {
            field = value
            adapter!!.setBottomTopDayPadding(value)
            invalidate()
        }
    var dayDrawDelegate: DayDrawDelegate = DefaultDayDrawDelegate(this)
        set(value) {
            field = value
            adapter!!.setDayDrawDelegate(value)
            invalidate()
        }
    var dayDrawDataProvider: DayDrawDataProvider = DefaultDayDrawDataProvider()
        set(value) {
            field = value
            adapter!!.setDayDrawDataProvider(dayDrawDataProvider)
            invalidate()
        }
    var arrowColor = Color.BLACK
        set(color) {
            if (color == 0) {
                return
            }
            field = color
            buttonPast.setColor(color)
            buttonFuture.setColor(color)
            invalidate()
        }
    var leftArrowMask: Drawable? = null
        set(icon) {
            field = icon
            buttonPast.setImageDrawable(icon)
        }
    var rightArrowMask: Drawable? = null
        set(icon) {
            field = icon
            buttonFuture.setImageDrawable(icon)
        }
    private var tileHeight = INVALID_TILE_DIMENSION
    private var tileWidth = INVALID_TILE_DIMENSION
    var selectionMode = SELECTION_MODE_SINGLE
        @SuppressLint("SwitchIntDef")
        set(@SelectionMode mode) {
            @SelectionMode val oldMode = this.selectionMode
            field = mode
            when (mode) {
                SELECTION_MODE_RANGE -> clearSelection()
                SELECTION_MODE_MULTIPLE -> {
                }
                SELECTION_MODE_SINGLE -> if (oldMode == SELECTION_MODE_MULTIPLE || oldMode == SELECTION_MODE_RANGE) {
                    val dates = selectedDates
                    if (!dates.isEmpty()) {
                        setSelectedDate(selectedDate)
                    }
                }
                else -> {
                    field = SELECTION_MODE_NONE
                    if (oldMode != SELECTION_MODE_NONE) {
                        clearSelection()
                    }
                }
            }
            adapter!!.setSelectionEnabled(this.selectionMode != SELECTION_MODE_NONE)
        }
    private var allowClickDaysOutsideCurrentMonth = true
    /**
     * @return The first day of the week as a [Calendar] day constant.
     */
    var firstDayOfWeek: Int = 0
        private set

    private var state: State? = null

    /**
     * Set the size of each tile that makes up the calendar.
     * Each day is 1 tile, so the widget is 7 tiles wide and 7 or 8 tiles tall
     * depending on the visibility of the [.topbar].
     *
     * @param size the new size for each tile in pixels
     */
    var tileSize: Int
        @Deprecated("")
        get() = Math.max(tileHeight, tileWidth)
        set(size) {
            this.tileWidth = size
            this.tileHeight = size
            requestLayout()
        }

    /**
     * The default value is [.SHOW_DEFAULTS], which currently is just [.SHOW_DECORATED_DISABLED].
     * This means that the default visible days are of the current month, in the min-max range.
     *
     * @see .SHOW_ALL
     *
     * @see .SHOW_NONE
     *
     * @see .SHOW_DEFAULTS
     *
     * @see .SHOW_OTHER_MONTHS
     *
     * @see .SHOW_OUT_OF_RANGE
     *
     * @see .SHOW_DECORATED_DISABLED
     */
    var showOtherDates: Int
        @ShowOtherDates
        get() = adapter!!.getShowOtherDates()
        set(@ShowOtherDates showOtherDates) = adapter!!.setShowOtherDates(showOtherDates)

    init {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //If we're on good Android versions, turn off clipping for cool effects
            clipToPadding = false
            clipChildren = false
        } else {
            //Old Android does not like _not_ clipping view pagers, we need to clip
            clipChildren = true
            clipToPadding = true
        }

        buttonPast = DirectionButton(getContext())
        buttonPast.contentDescription = getContext().getString(R.string.previous)
        title = TextView(getContext())
        buttonFuture = DirectionButton(getContext())
        buttonFuture.contentDescription = getContext().getString(R.string.next)
        pager = CalendarPager(getContext())

        val onClickListener = OnClickListener { v ->
            if (v === buttonFuture) {
                pager.setCurrentItem(pager.currentItem + 1, true)
            } else if (v === buttonPast) {
                pager.setCurrentItem(pager.currentItem - 1, true)
            }
        }
        buttonPast.setOnClickListener(onClickListener)
        buttonFuture.setOnClickListener(onClickListener)

        titleChanger = TitleChanger(title)
        titleChanger.titleFormatter = DEFAULT_TITLE_FORMATTER

        val pageChangeListener = object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                currentMonth ?: return
                titleChanger.setPreviousMonth(currentMonth!!)
                currentMonth = adapter!!.getItem(position)
                updateUi()

                dispatchOnMonthChanged(currentMonth)
            }

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        }
        pager.addOnPageChangeListener(pageChangeListener)
        pager.setPageTransformer(false) { page, position ->
            var position = position
            position = Math.sqrt((1 - Math.abs(position)).toDouble()).toFloat()
            page.alpha = position
        }

        val a = context.theme
                .obtainStyledAttributes(attrs, R.styleable.MaterialCalendarView, 0, 0)
        try {
            val calendarModeIndex = a.getInteger(
                    R.styleable.MaterialCalendarView_mcv_calendarMode,
                    0
            )
            firstDayOfWeek = a.getInteger(
                    R.styleable.MaterialCalendarView_mcv_firstDayOfWeek,
                    -1
            )

            titleChanger.orientation = a.getInteger(R.styleable.MaterialCalendarView_mcv_titleAnimationOrientation,
                    VERTICAL)

            if (firstDayOfWeek < 0) {
                //Allowing use of Calendar.getInstance() here as a performance optimization
                firstDayOfWeek = Calendar.getInstance().firstDayOfWeek
            }

            newState()
                    .setFirstDayOfWeek(firstDayOfWeek)
                    .setCalendarDisplayMode(CalendarMode.values()[calendarModeIndex])
                    .commit()

            val tileSize = a.getLayoutDimension(R.styleable.MaterialCalendarView_mcv_tileSize, INVALID_TILE_DIMENSION)
            if (tileSize > INVALID_TILE_DIMENSION) {
                this.tileSize = tileSize
            }

            val tileWidth = a.getLayoutDimension(R.styleable.MaterialCalendarView_mcv_tileWidth, INVALID_TILE_DIMENSION)
            if (tileWidth > INVALID_TILE_DIMENSION) {
                setTileWidth(tileWidth)
            }

            val tileHeight = a.getLayoutDimension(R.styleable.MaterialCalendarView_mcv_tileHeight, INVALID_TILE_DIMENSION)
            if (tileHeight > INVALID_TILE_DIMENSION) {
                setTileHeight(tileHeight)
            }

            arrowColor = a.getColor(
                    R.styleable.MaterialCalendarView_mcv_arrowColor,
                    Color.BLACK
            )
            var leftMask = a.getDrawable(
                    R.styleable.MaterialCalendarView_mcv_leftArrowMask
            )
            if (leftMask == null) {
                leftMask = resources.getDrawable(R.drawable.mcv_action_previous)
            }
            leftArrowMask = leftMask
            var rightMask = a.getDrawable(
                    R.styleable.MaterialCalendarView_mcv_rightArrowMask
            )
            if (rightMask == null) {
                rightMask = resources.getDrawable(R.drawable.mcv_action_next)
            }
            rightArrowMask = rightMask

            selectionColor = a.getColor(
                    R.styleable.MaterialCalendarView_mcv_selectionColor,
                    getThemePrimaryColor(context)
            )

            selectionRangeColor = a.getColor(
                    R.styleable.MaterialCalendarView_mcv_selectionRangeColor,
                    getThemeAccentColor(context)
            )

            bottomTopDayPadding = a.getDimensionPixelSize(
                    R.styleable.MaterialCalendarView_mcv_bottomTopDayPadding,
                    0
            )

            var array: Array<CharSequence>? = a.getTextArray(R.styleable.MaterialCalendarView_mcv_weekDayLabels)
            if (array != null) {
                setWeekDayFormatter(ArrayWeekDayFormatter(array))
            }

            array = a.getTextArray(R.styleable.MaterialCalendarView_mcv_monthLabels)
            if (array != null) {
                setTitleFormatter(MonthArrayTitleFormatter(array))
            }

            setHeaderTextAppearance(a.getResourceId(
                    R.styleable.MaterialCalendarView_mcv_headerTextAppearance,
                    R.style.TextAppearance_MaterialCalendarWidget_Header
            ))
            setWeekDayTextAppearance(a.getResourceId(
                    R.styleable.MaterialCalendarView_mcv_weekDayTextAppearance,
                    R.style.TextAppearance_MaterialCalendarWidget_WeekDay
            ))
            setDateTextAppearance(a.getResourceId(
                    R.styleable.MaterialCalendarView_mcv_dateTextAppearance,
                    R.style.TextAppearance_MaterialCalendarWidget_Date
            ))

            showOtherDates = a.getInteger(
                    R.styleable.MaterialCalendarView_mcv_showOtherDates,
                    SHOW_DEFAULTS
            )

            setAllowClickDaysOutsideCurrentMonth(a.getBoolean(
                    R.styleable.MaterialCalendarView_mcv_allowClickDaysOutsideCurrentMonth,
                    true
            ))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            a.recycle()
        }

        // Adapter is created while parsing the TypedArray attrs, so setup has to happen after
        adapter!!.setTitleFormatter(DEFAULT_TITLE_FORMATTER)
        setupChildren()

        currentMonth = CalendarDay.today()
        setCurrentDate(currentMonth)

        if (isInEditMode) {
            removeView(pager)
            val monthView = MonthView(this, currentMonth!!, firstDayOfWeek)
            dayDrawDelegate.setSelectionColor(selectionColor)
            monthView.setDateTextAppearance(adapter!!.dateTextAppearance)
            monthView.setWeekDayTextAppearance(adapter!!.weekDayTextAppearance)
            monthView.showOtherDates = showOtherDates
            addView(monthView, LayoutParams(calendarMode!!.visibleWeeksCount + DAY_NAMES_ROW))
        }
    }

    private fun setupChildren() {
        topbar = LinearLayout(context)
        topbar!!.orientation = LinearLayout.HORIZONTAL
        topbar!!.clipChildren = false
        topbar!!.clipToPadding = false
        addView(topbar, LayoutParams(1))

        buttonPast.scaleType = ImageView.ScaleType.CENTER_INSIDE
        topbar!!.addView(buttonPast, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f))

        title.gravity = Gravity.CENTER
        topbar!!.addView(title, LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, (DEFAULT_DAYS_IN_WEEK - 2).toFloat()
        ))

        buttonFuture.scaleType = ImageView.ScaleType.CENTER_INSIDE
        topbar!!.addView(buttonFuture, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f))

        pager!!.id = R.id.mcv_pager
        pager.offscreenPageLimit = 1
        addView(pager, LayoutParams(calendarMode!!.visibleWeeksCount + DAY_NAMES_ROW))
    }

    private fun updateUi() {
        titleChanger.change(currentMonth)
        buttonPast.isEnabled = canGoBack()
        buttonFuture.isEnabled = canGoForward()
    }

    /**
     * Go to previous month or week without using the button [.buttonPast]. Should only go to
     * previous if [.canGoBack] is true, meaning it's possible to go to the previous month
     * or week.
     */
    fun goToPrevious() {
        if (canGoBack()) {
            pager!!.setCurrentItem(pager.currentItem - 1, true)
        }
    }

    /**
     * Go to next month or week without using the button [.buttonFuture]. Should only go to
     * next if [.canGoForward] is enabled, meaning it's possible to go to the next month or
     * week.
     */
    fun goToNext() {
        if (canGoForward()) {
            pager!!.setCurrentItem(pager.currentItem + 1, true)
        }
    }

    /**
     * @param tileSizeDp the new size for each tile in dips
     * @see .setTileSize
     */
    fun setTileSizeDp(tileSizeDp: Int) {
        tileSize = dpToPx(tileSizeDp)
    }

    /**
     * @return the height of tiles in pixels
     */
    fun getTileHeight(): Int = tileHeight

    /**
     * Set the height of each tile that makes up the calendar.
     *
     * @param height the new height for each tile in pixels
     */
    fun setTileHeight(height: Int) {
        this.tileHeight = height
        requestLayout()
    }

    /**
     * @param tileHeightDp the new height for each tile in dips
     * @see .setTileHeight
     */
    fun setTileHeightDp(tileHeightDp: Int) {
        setTileHeight(dpToPx(tileHeightDp))
    }

    /**
     * @return the width of tiles in pixels
     */
    fun getTileWidth(): Int = tileWidth

    /**
     * Set the width of each tile that makes up the calendar.
     *
     * @param width the new width for each tile in pixels
     */
    fun setTileWidth(width: Int) {
        this.tileWidth = width
        requestLayout()
    }

    /**
     * @param tileWidthDp the new width for each tile in dips
     * @see .setTileWidth
     */
    fun setTileWidthDp(tileWidthDp: Int) {
        setTileWidth(dpToPx(tileWidthDp))
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
        ).toInt()
    }

    /**
     * @return true if there is a future month that can be shown
     */
    fun canGoForward(): Boolean = pager!!.currentItem < adapter!!.count - 1

    /**
     * Pass all touch events to the pager so scrolling works on the edges of the calendar view.
     *
     * @param event
     * @return
     */
    override fun onTouchEvent(event: MotionEvent): Boolean = pager!!.dispatchTouchEvent(event)

    /**
     * @return true if there is a previous month that can be shown
     */
    fun canGoBack(): Boolean = pager!!.currentItem > 0

    fun setContentDescriptionArrowPast(description: CharSequence) {
        buttonPast.contentDescription = description
    }

    fun setContentDescriptionArrowFuture(description: CharSequence) {
        buttonFuture.contentDescription = description
    }

    fun setContentDescriptionCalendar(description: CharSequence) {
        calendarContentDescription = description
    }

    fun setHeaderTextAppearance(resourceId: Int) {
        title.setTextAppearance(context, resourceId)
    }

    fun setDateTextAppearance(resourceId: Int) {
        adapter!!.dateTextAppearance = resourceId
    }

    fun setWeekDayTextAppearance(resourceId: Int) {
        adapter!!.weekDayTextAppearance = resourceId
    }

    /**
     * @return the selected day, or null if no selection. If in multiple selection mode, this
     * will return the last selected date
     */
    val selectedDate: CalendarDay?
        get() {
            val dates = adapter!!.getSelectedDates()
            return if (dates.isEmpty()) {
                null
            } else {
                dates[dates.size - 1]
            }
        }

    /**
     * @return all of the currently selected dates
     */
    val selectedDates: List<CalendarDay>
        get() = adapter!!.getSelectedDates()

    /**
     * Clear the currently selected date(s)
     */
    fun clearSelection() {
        val dates = selectedDates
        adapter!!.clearSelections()
        for (day in dates) {
            dispatchOnDateSelected(day, false)
        }
    }

    /**
     * @param calendar a Calendar set to a day to select. Null to clear selection
     */
    fun setSelectedDate(calendar: Calendar?) {
        setSelectedDate(CalendarDay.from(calendar))
    }

    /**
     * @param date a Date to set as selected. Null to clear selection
     */
    fun setSelectedDate(date: Date?) {
        setSelectedDate(CalendarDay.from(date))
    }

    /**
     * @param date a Date to set as selected. Null to clear selection
     */
    fun setSelectedDate(date: CalendarDay?) {
        clearSelection()
        if (date != null) {
            setDateSelected(date, true)
        }
    }

    /**
     * @param calendar a Calendar to change. Passing null does nothing
     * @param selected true if day should be selected, false to deselect
     */
    fun setDateSelected(calendar: Calendar?, selected: Boolean) {
        setDateSelected(CalendarDay.from(calendar), selected)
    }

    /**
     * @param date     a Date to change. Passing null does nothing
     * @param selected true if day should be selected, false to deselect
     */
    fun setDateSelected(date: Date?, selected: Boolean) {
        setDateSelected(CalendarDay.from(date), selected)
    }

    /**
     * @param day      a CalendarDay to change. Passing null does nothing
     * @param selected true if day should be selected, false to deselect
     */
    fun setDateSelected(day: CalendarDay?, selected: Boolean) {
        if (day == null) {
            return
        }
        adapter!!.setDateSelected(day, selected)
    }

    /**
     * @param calendar a Calendar set to a day to focus the calendar on. Null will do nothing
     */
    fun setCurrentDate(calendar: Calendar?) {
        setCurrentDate(CalendarDay.from(calendar))
    }

    /**
     * @param date a Date to focus the calendar on. Null will do nothing
     */
    fun setCurrentDate(date: Date?) {
        setCurrentDate(CalendarDay.from(date))
    }

    /**
     * @return The current month shown, will be set to first day of the month
     */
    val currentDate: CalendarDay
        get() = adapter!!.getItem(pager!!.currentItem)

    /**
     * @param day             a CalendarDay to focus the calendar on. Null will do nothing
     * @param useSmoothScroll use smooth scroll when changing months.
     */
    @JvmOverloads
    fun setCurrentDate(day: CalendarDay?, useSmoothScroll: Boolean = true) {
        if (day == null) {
            return
        }
        val index = adapter!!.getIndexForDay(day)
        pager!!.setCurrentItem(index, useSmoothScroll)
        updateUi()
    }

    /**
     * Allow the user to click on dates from other months that are not out of range. Go to next or
     * previous month if a day outside the current month is clicked. The day still need to be
     * enabled to be selected.
     * Default value is true. Should be used with [.SHOW_OTHER_MONTHS].
     *
     * @param enabled True to allow the user to click on a day outside current month displayed
     */
    fun setAllowClickDaysOutsideCurrentMonth(enabled: Boolean) {
        this.allowClickDaysOutsideCurrentMonth = enabled
    }

    /**
     * Set a formatter for weekday labels.
     *
     * @param formatter the new formatter, null for default
     */
    fun setWeekDayFormatter(formatter: WeekDayFormatter?) {
        adapter!!.setWeekDayFormatter(formatter ?: WeekDayFormatter.DEFAULT)
    }

    /**
     * Set a formatter for day labels.
     *
     * @param formatter the new formatter, null for default
     */
    fun setDayFormatter(formatter: DayFormatter?) {
        adapter!!.setDayFormatter(formatter ?: DayFormatter.DEFAULT)
    }

    /**
     * Set a [com.prolificinteractive.materialcalendarview.format.WeekDayFormatter]
     * with the provided week day labels
     *
     * @param weekDayLabels Labels to use for the days of the week
     * @see com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
     *
     * @see .setWeekDayFormatter
     */
    fun setWeekDayLabels(weekDayLabels: Array<CharSequence>) {
        setWeekDayFormatter(ArrayWeekDayFormatter(weekDayLabels))
    }

    /**
     * Set a [com.prolificinteractive.materialcalendarview.format.WeekDayFormatter]
     * with the provided week day labels
     *
     * @param arrayRes String array resource of week day labels
     * @see com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
     *
     * @see .setWeekDayFormatter
     */
    fun setWeekDayLabels(@ArrayRes arrayRes: Int) {
        setWeekDayLabels(resources.getTextArray(arrayRes))
    }

    /**
     * @return true if allow click on days outside current month displayed
     */
    fun allowClickDaysOutsideCurrentMonth(): Boolean = allowClickDaysOutsideCurrentMonth

    @Suppress("NAME_SHADOWING")
            /**
             * Set a custom formatter for the month/year title
             *
             * @param titleFormatter new formatter to use, null to use default formatter
             */
    fun setTitleFormatter(titleFormatter: TitleFormatter?) {
        var titleFormatter = titleFormatter
        if (titleFormatter == null) {
            titleFormatter = DEFAULT_TITLE_FORMATTER
        }
        titleChanger.titleFormatter = titleFormatter
        adapter!!.setTitleFormatter(titleFormatter)
        updateUi()
    }

    /**
     * Set a [com.prolificinteractive.materialcalendarview.format.TitleFormatter]
     * using the provided month labels
     *
     * @param monthLabels month labels to use
     * @see com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
     *
     * @see .setTitleFormatter
     */
    fun setTitleMonths(monthLabels: Array<CharSequence>) {
        setTitleFormatter(MonthArrayTitleFormatter(monthLabels))
    }

    /**
     * Set a [com.prolificinteractive.materialcalendarview.format.TitleFormatter]
     * using the provided month labels
     *
     * @param arrayRes String array resource of month labels to use
     * @see com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
     *
     * @see .setTitleFormatter
     */
    fun setTitleMonths(@ArrayRes arrayRes: Int) {
        setTitleMonths(resources.getTextArray(arrayRes))
    }

    var titleAnimationOrientation: Int
        get() = titleChanger.orientation
        set(orientation) {
            titleChanger.orientation = orientation
        }

    /**
     * Sets the visibility [.topbar], which contains
     * the previous month button [.buttonPast], next month button [.buttonFuture],
     * and the month title [.title].
     */
    var topbarVisible: Boolean
        get() = topbar!!.visibility == View.VISIBLE
        set(visible) {
            topbar!!.visibility = if (visible) View.VISIBLE else View.GONE
            requestLayout()
        }

    override fun onSaveInstanceState(): Parcelable {
        val ss = SavedState(super.onSaveInstanceState())
        ss.color = selectionColor
        ss.dateTextAppearance = adapter!!.dateTextAppearance
        ss.weekDayTextAppearance = adapter!!.weekDayTextAppearance
        ss.showOtherDates = showOtherDates
        ss.allowClickDaysOutsideCurrentMonth = allowClickDaysOutsideCurrentMonth()
        ss.minDate = minimumDate
        ss.maxDate = maximumDate
        ss.selectedDates = selectedDates
        ss.firstDayOfWeek = firstDayOfWeek
        ss.orientation = titleAnimationOrientation
        ss.selectionMode = selectionMode
        ss.tileWidthPx = getTileWidth()
        ss.tileHeightPx = getTileHeight()
        ss.topbarVisible = topbarVisible
        ss.calendarMode = calendarMode
        ss.dynamicHeightEnabled = isDynamicHeightEnabled
        ss.currentMonth = currentMonth
        ss.cacheCurrentPosition = state!!.cacheCurrentPosition
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        newState().apply {
            firstDayOfWeek = ss.firstDayOfWeek
        }
                .setCalendarDisplayMode(ss.calendarMode)
                .setMinimumDate(ss.minDate)
                .setMaximumDate(ss.maxDate)
                .isCacheCalendarPositionEnabled(ss.cacheCurrentPosition)
                .commit()

        selectionColor = ss.color
        setDateTextAppearance(ss.dateTextAppearance)
        setWeekDayTextAppearance(ss.weekDayTextAppearance)
        showOtherDates = ss.showOtherDates
        setAllowClickDaysOutsideCurrentMonth(ss.allowClickDaysOutsideCurrentMonth)
        clearSelection()
        for (calendarDay in ss.selectedDates) {
            setDateSelected(calendarDay, true)
        }
        titleAnimationOrientation = ss.orientation
        setTileWidth(ss.tileWidthPx)
        setTileHeight(ss.tileHeightPx)
        topbarVisible = ss.topbarVisible
        selectionMode = ss.selectionMode
        isDynamicHeightEnabled = ss.dynamicHeightEnabled
        setCurrentDate(ss.currentMonth)
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    private fun setRangeDates(min: CalendarDay?, max: CalendarDay?) {
        val c = currentMonth
        adapter!!.setRangeDates(min, max)
        currentMonth = c
        if (min != null) {
            currentMonth = if (min.isAfter(currentMonth!!)) min else currentMonth
        }
        val position = adapter!!.getIndexForDay(c)
        pager!!.setCurrentItem(position, false)
        updateUi()
    }

    open class SavedState : View.BaseSavedState {

        var color = 0
        var dateTextAppearance = 0
        var weekDayTextAppearance = 0
        var showOtherDates = SHOW_DEFAULTS
        var allowClickDaysOutsideCurrentMonth = true
        var minDate: CalendarDay? = null
        var maxDate: CalendarDay? = null
        var selectedDates: List<CalendarDay> = ArrayList()
        var firstDayOfWeek = Calendar.SUNDAY
        var orientation = 0
        var tileWidthPx = -1
        var tileHeightPx = -1
        var topbarVisible = true
        var selectionMode = SELECTION_MODE_SINGLE
        var dynamicHeightEnabled = false
        var calendarMode: CalendarMode? = CalendarMode.MONTHS
        var currentMonth: CalendarDay? = null
        var cacheCurrentPosition: Boolean = false

        constructor(superState: Parcelable) : super(superState)

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(color)
            out.writeInt(dateTextAppearance)
            out.writeInt(weekDayTextAppearance)
            out.writeInt(showOtherDates)
            out.writeByte((if (allowClickDaysOutsideCurrentMonth) 1 else 0).toByte())
            out.writeParcelable(minDate, 0)
            out.writeParcelable(maxDate, 0)
            out.writeTypedList(selectedDates)
            out.writeInt(firstDayOfWeek)
            out.writeInt(orientation)
            out.writeInt(tileWidthPx)
            out.writeInt(tileHeightPx)
            out.writeInt(if (topbarVisible) 1 else 0)
            out.writeInt(selectionMode)
            out.writeInt(if (dynamicHeightEnabled) 1 else 0)
            out.writeInt(if (calendarMode === CalendarMode.WEEKS) 1 else 0)
            out.writeParcelable(currentMonth, 0)
            out.writeByte((if (cacheCurrentPosition) 1 else 0).toByte())
        }

        private constructor(`in`: Parcel) : super(`in`) {
            color = `in`.readInt()
            dateTextAppearance = `in`.readInt()
            weekDayTextAppearance = `in`.readInt()
            showOtherDates = `in`.readInt()
            allowClickDaysOutsideCurrentMonth = `in`.readByte().toInt() != 0
            val loader = CalendarDay::class.java.classLoader
            minDate = `in`.readParcelable(loader)
            maxDate = `in`.readParcelable(loader)
            `in`.readTypedList(selectedDates, CalendarDay.CREATOR)
            firstDayOfWeek = `in`.readInt()
            orientation = `in`.readInt()
            tileWidthPx = `in`.readInt()
            tileHeightPx = `in`.readInt()
            topbarVisible = `in`.readInt() == 1
            selectionMode = `in`.readInt()
            dynamicHeightEnabled = `in`.readInt() == 1
            calendarMode = if (`in`.readInt() == 1) CalendarMode.WEEKS else CalendarMode.MONTHS
            currentMonth = `in`.readParcelable(loader)
            cacheCurrentPosition = `in`.readByte().toInt() != 0
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState = SavedState(`in`)

                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }

    fun setOnDateChangedListener(listener: OnDateSelectedListener) {
        this.listener = listener
    }

    fun setOnMonthChangedListener(listener: OnMonthChangedListener) {
        this.monthListener = listener
    }

    fun setOnRangeSelectedListener(listener: OnRangeSelectedListener) {
        this.rangeListener = listener
    }

    fun setOnTitleClickListener(listener: View.OnClickListener) {
        title.setOnClickListener(listener)
    }

    protected fun dispatchOnDateSelected(day: CalendarDay, selected: Boolean) {
        val l = listener
        l?.onDateSelected(this@MaterialCalendarView, day, selected)
    }

    protected fun dispatchOnRangeSelected(firstDay: CalendarDay, lastDay: CalendarDay) {
        val listener = rangeListener
        val days = ArrayList<CalendarDay>()

        val counter = Calendar.getInstance()
        counter.time = firstDay.date  //  start from the first day and increment

        val end = Calendar.getInstance()
        end.time = lastDay.date  //  for comparison

        while (counter.before(end) || counter == end) {
            val current = CalendarDay.from(counter)
            adapter!!.setDateSelected(current!!, true)
            days.add(current)
            counter.add(Calendar.DATE, 1)
        }

        listener?.onRangeSelected(this@MaterialCalendarView, days)
    }

    protected fun dispatchOnMonthChanged(day: CalendarDay?) {
        val l = monthListener
        l?.onMonthChanged(this@MaterialCalendarView, day!!)
    }

    /**
     * Call by [CalendarPagerView] to indicate that a day was clicked and we should handle it.
     * This method will always process the click to the selected date.
     *
     * @param date        date of the day that was clicked
     * @param nowSelected true if the date is now selected, false otherwise
     */
    @SuppressLint("SwitchIntDef")
    protected fun onDateClicked(date: CalendarDay, nowSelected: Boolean) {
        when (this.selectionMode) {
            SELECTION_MODE_MULTIPLE -> {
                adapter!!.setDateSelected(date, nowSelected)
                dispatchOnDateSelected(date, nowSelected)
            }
            SELECTION_MODE_RANGE -> {
                adapter!!.setDateSelected(date, nowSelected)
                if (adapter!!.getSelectedDates().size > 2) {
                    adapter!!.clearSelections()
                    adapter!!.setDateSelected(date, nowSelected)  //  re-set because adapter has been cleared
                    dispatchOnDateSelected(date, nowSelected)
                } else if (adapter!!.getSelectedDates().size == 2) {
                    val dates = adapter!!.getSelectedDates()
                    if (dates[0].isAfter(dates[1])) {
                        dispatchOnRangeSelected(dates[1], dates[0])
                    } else {
                        dispatchOnRangeSelected(dates[0], dates[1])
                    }
                } else {
                    adapter!!.setDateSelected(date, nowSelected)
                    dispatchOnDateSelected(date, nowSelected)
                }
            }
            else -> {
                adapter!!.clearSelections()
                adapter!!.setDateSelected(date, true)
                dispatchOnDateSelected(date, true)
            }
        }
    }

    fun selectRange(firstDay: CalendarDay?, lastDay: CalendarDay?) {
        clearSelection()
        if (firstDay == null || lastDay == null) {
            return
        } else if (firstDay.isAfter(lastDay)) {
            dispatchOnRangeSelected(lastDay, firstDay)
        } else {
            dispatchOnRangeSelected(firstDay, lastDay)
        }
    }

    fun onDateClicked(dayView: DayView) {
        val currentDate = currentDate
        val selectedDate = dayView.date
        val currentMonth = currentDate.month
        val selectedMonth = selectedDate!!.month

        if (calendarMode === CalendarMode.MONTHS
                && allowClickDaysOutsideCurrentMonth
                && currentMonth != selectedMonth) {
            if (currentDate.isAfter(selectedDate)) {
                goToPrevious()
            } else if (currentDate.isBefore(selectedDate)) {
                goToNext()
            }
        }
        onDateClicked(dayView.date!!, !dayView.isChecked)
    }

    /**
     * Called by the adapter for cases when changes in state result in dates being unselected
     *
     * @param date date that should be de-selected
     */
    fun onDateUnselected(date: CalendarDay) {
        dispatchOnDateSelected(date, false)
    }

    override fun generateDefaultLayoutParams(): LayoutParams = LayoutParams(1)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val specWidthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val specWidthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val specHeightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val specHeightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        //We need to disregard padding for a while. This will be added back later
        val desiredWidth = specWidthSize - paddingLeft - paddingRight
        val desiredHeight = specHeightSize - paddingTop - paddingBottom

        val weekCount = weekCountBasedOnMode

        val viewTileHeight = if (topbarVisible) weekCount + 1 else weekCount

        //Calculate independent tile sizes for later
        val desiredTileWidth = desiredWidth / DEFAULT_DAYS_IN_WEEK
        val desiredTileHeight = desiredHeight / viewTileHeight

        var measureTileSize = -1
        var measureTileWidth = -1
        var measureTileHeight = -1

        if (this.tileWidth != INVALID_TILE_DIMENSION || this.tileHeight != INVALID_TILE_DIMENSION) {
            measureTileWidth = if (this.tileWidth > 0) {
                //We have a tileWidth set, we should use that
                this.tileWidth
            } else {
                desiredTileWidth
            }
            measureTileHeight = if (this.tileHeight > 0) {
                //We have a tileHeight set, we should use that
                this.tileHeight
            } else {
                desiredTileHeight
            }
        } else if (specWidthMode == View.MeasureSpec.EXACTLY || specWidthMode == View.MeasureSpec.AT_MOST) {
            measureTileSize = if (specHeightMode == View.MeasureSpec.EXACTLY) {
                //Pick the smaller of the two explicit sizes
                Math.min(desiredTileWidth, desiredTileHeight)
            } else {
                //Be the width size the user wants
                desiredTileWidth
            }
        } else if (specHeightMode == View.MeasureSpec.EXACTLY || specHeightMode == View.MeasureSpec.AT_MOST) {
            //Be the height size the user wants
            measureTileSize = desiredTileHeight
        }

        if (measureTileSize > 0) {
            //Use measureTileSize if set
            measureTileHeight = measureTileSize
            measureTileWidth = measureTileSize
        } else if (measureTileSize <= 0) {
            if (measureTileWidth <= 0) {
                //Set width to default if no value were set
                measureTileWidth = dpToPx(DEFAULT_TILE_SIZE_DP)
            }
            if (measureTileHeight <= 0) {
                //Set height to default if no value were set
                measureTileHeight = dpToPx(DEFAULT_TILE_SIZE_DP)
            }
        }

        //Calculate our size based off our measured tile size
        var measuredWidth = measureTileWidth * DEFAULT_DAYS_IN_WEEK
        var measuredHeight = measureTileHeight * viewTileHeight

        //Put padding back in from when we took it away
        measuredWidth += paddingLeft + paddingRight
        measuredHeight += paddingTop + paddingBottom

        //Contract fulfilled, setting out measurements
        setMeasuredDimension(
                //We clamp inline because we want to use un-clamped versions on the children
                clampSize(measuredWidth, widthMeasureSpec),
                clampSize(measuredHeight, heightMeasureSpec)
        )

        val count = childCount

        for (i in 0 until count) {
            val child = getChildAt(i)

            val p = child.layoutParams as LayoutParams

            val childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    DEFAULT_DAYS_IN_WEEK * measureTileWidth,
                    View.MeasureSpec.EXACTLY
            )

            val childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    p.height * measureTileHeight,
                    View.MeasureSpec.EXACTLY
            )

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }
    }

    private val weekCountBasedOnMode: Int
        get() {
            var weekCount = calendarMode!!.visibleWeeksCount
            val isInMonthsMode = calendarMode == CalendarMode.MONTHS
            if (isInMonthsMode && isDynamicHeightEnabled && adapter != null && pager != null) {
                val cal = adapter!!.getItem(pager.currentItem).calendar.clone() as Calendar
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                cal.firstDayOfWeek = firstDayOfWeek
                weekCount = cal.get(Calendar.WEEK_OF_MONTH)
            }
            return weekCount + DAY_NAMES_ROW
        }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val count = childCount

        val parentLeft = paddingLeft
        val parentWidth = right - left - parentLeft - paddingRight

        var childTop = paddingTop

        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue
            }

            val width = child.measuredWidth
            val height = child.measuredHeight

            val delta = (parentWidth - width) / 2
            val childLeft = parentLeft + delta

            child.layout(childLeft, childTop, childLeft + width, childTop + height)

            childTop += height
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams = LayoutParams(1)

    override fun shouldDelayChildPressedState(): Boolean = false

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean = p is LayoutParams

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams =
            LayoutParams(1)

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        event.className = MaterialCalendarView::class.java.name
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.className = MaterialCalendarView::class.java.name
    }

    class LayoutParams(tileHeight: Int) : ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, tileHeight)

    var isPagingEnabled: Boolean
        get() = pager!!.isPagingEnabled
        set(pagingEnabled) {
            pager!!.isPagingEnabled = pagingEnabled
            updateUi()
        }

    fun state(): State? = state

    fun newState(): StateBuilder = StateBuilder()

    inner class State(builder: StateBuilder) {
        val calendarMode: CalendarMode? = builder.getCalendarMode()
        val firstDayOfWeek: Int = builder.getFirstDayOfWeek()
        val minDate: CalendarDay? = builder.getMinDate()
        val maxDate: CalendarDay? = builder.getMaxDate()
        val cacheCurrentPosition: Boolean = builder.getCacheCurrentPosition()

        fun edit(): StateBuilder = StateBuilder(this)
    }

    inner class StateBuilder {
        private var calendarMode: CalendarMode? = CalendarMode.MONTHS
        private var firstDayOfWeek = Calendar.getInstance().firstDayOfWeek
        private var cacheCurrentPosition = false
        private var minDate: CalendarDay? = null
        private var maxDate: CalendarDay? = null

        constructor()

        constructor(state: State) {
            calendarMode = state.calendarMode
            firstDayOfWeek = state.firstDayOfWeek
            minDate = state.minDate
            maxDate = state.maxDate
            cacheCurrentPosition = state.cacheCurrentPosition
        }

        fun getCalendarMode(): CalendarMode? = calendarMode

        fun getFirstDayOfWeek(): Int = firstDayOfWeek

        fun getCacheCurrentPosition(): Boolean = cacheCurrentPosition

        fun getMinDate(): CalendarDay? = minDate

        fun getMaxDate(): CalendarDay? = maxDate

        /**
         * Sets the first day of the week.
         *
         *
         * Uses the java.util.Calendar day constants.
         *
         * @param day The first day of the week as a java.util.Calendar day constant.
         * @see java.util.Calendar
         */
        fun setFirstDayOfWeek(day: Int): StateBuilder {
            this.firstDayOfWeek = day
            return this
        }

        /**
         * Set calendar display mode. The default mode is Months.
         * When switching between modes will select todays date, or the selected date,
         * if selection mode is single.
         *
         * @param mode - calendar mode
         */
        fun setCalendarDisplayMode(mode: CalendarMode?): StateBuilder {
            this.calendarMode = mode
            return this
        }


        /**
         * @param calendar set the minimum selectable date, null for no minimum
         */
        fun setMinimumDate(calendar: Calendar?): StateBuilder {
            setMinimumDate(CalendarDay.from(calendar))
            return this
        }

        /**
         * @param date set the minimum selectable date, null for no minimum
         */
        fun setMinimumDate(date: Date?): StateBuilder {
            setMinimumDate(CalendarDay.from(date))
            return this
        }

        /**
         * @param calendar set the minimum selectable date, null for no minimum
         */
        fun setMinimumDate(calendar: CalendarDay?): StateBuilder {
            minDate = calendar
            return this
        }

        /**
         * @param calendar set the maximum selectable date, null for no maximum
         */
        fun setMaximumDate(calendar: Calendar?): StateBuilder {
            setMaximumDate(CalendarDay.from(calendar))
            return this
        }

        /**
         * @param date set the maximum selectable date, null for no maximum
         */
        fun setMaximumDate(date: Date?): StateBuilder {
            setMaximumDate(CalendarDay.from(date))
            return this
        }

        /**
         * @param calendar set the maximum selectable date, null for no maximum
         */
        fun setMaximumDate(calendar: CalendarDay?): StateBuilder {
            maxDate = calendar
            return this
        }

        /**
         * Use this method to enable saving the current position when switching
         * between week and month mode. By default, the calendar update to the latest selected date
         * or the current date. When set to true, the view will used the month that the calendar is
         * currently on.
         *
         * @param cacheCurrentPosition Set to true to cache the current position, false otherwise.
         */
        fun isCacheCalendarPositionEnabled(cacheCurrentPosition: Boolean): StateBuilder {
            this.cacheCurrentPosition = cacheCurrentPosition
            return this
        }

        fun commit() {
            this@MaterialCalendarView.commit(State(this))
        }
    }

    private fun commit(state: State) {
        // Use the calendarDayToShow to determine which date to focus on for the case of switching between month and week views
        var calendarDayToShow: CalendarDay? = null
        if (adapter != null && state.cacheCurrentPosition) {
            calendarDayToShow = adapter!!.getItem(pager!!.currentItem)
            if (calendarMode !== state.calendarMode) {
                val currentlySelectedDate = selectedDate
                if (calendarMode === CalendarMode.MONTHS && currentlySelectedDate != null) {
                    // Going from months to weeks
                    val lastVisibleCalendar = calendarDayToShow.calendar
                    lastVisibleCalendar.add(Calendar.MONTH, 1)
                    val lastVisibleCalendarDay = CalendarDay.from(lastVisibleCalendar)
                    if (currentlySelectedDate == calendarDayToShow || currentlySelectedDate.isAfter(calendarDayToShow) && currentlySelectedDate.isBefore(lastVisibleCalendarDay!!)) {
                        // Currently selected date is within view, so center on that
                        calendarDayToShow = currentlySelectedDate
                    }
                } else if (calendarMode === CalendarMode.WEEKS) {
                    // Going from weeks to months
                    val lastVisibleCalendar = calendarDayToShow.calendar
                    lastVisibleCalendar.add(Calendar.DAY_OF_WEEK, 6)
                    val lastVisibleCalendarDay = CalendarDay.from(lastVisibleCalendar)
                    calendarDayToShow = if (currentlySelectedDate != null && (currentlySelectedDate == calendarDayToShow || currentlySelectedDate == lastVisibleCalendarDay ||
                            currentlySelectedDate.isAfter(calendarDayToShow) && currentlySelectedDate.isBefore(lastVisibleCalendarDay!!))) {
                        // Currently selected date is within view, so center on that
                        currentlySelectedDate
                    } else {
                        lastVisibleCalendarDay
                    }
                }
            }
        }

        this.state = state
        // Save states parameters
        calendarMode = state.calendarMode
        firstDayOfWeek = state.firstDayOfWeek
        minimumDate = state.minDate
        maximumDate = state.maxDate

        // Recreate adapter
        val newAdapter: CalendarPagerAdapter<*>
        when (calendarMode) {
            CalendarMode.MONTHS -> newAdapter = MonthPagerAdapter(this)
            else -> throw IllegalArgumentException("Provided display mode which is not yet implemented")
        }
        adapter = if (adapter == null) {
            newAdapter
        } else {
            adapter!!.migrateStateAndReturn(newAdapter)
        }
        pager!!.adapter = adapter
        setRangeDates(minimumDate, maximumDate)

        // Reset height params after mode change
        pager.layoutParams = LayoutParams(calendarMode!!.visibleWeeksCount + DAY_NAMES_ROW)

        setCurrentDate(
                if (this.selectionMode == SELECTION_MODE_SINGLE && !adapter!!.getSelectedDates().isEmpty())
                    adapter!!.getSelectedDates()[0]
                else
                    CalendarDay.today())

        if (calendarDayToShow != null) {
            pager.currentItem = adapter!!.getIndexForDay(calendarDayToShow)
        }

        updateUi()
    }

    companion object {

        const val INVALID_TILE_DIMENSION = -10

        /**
         * Selection mode that disallows all selection.
         * When changing to this mode, current selection will be cleared.
         */
        const val SELECTION_MODE_NONE = 0

        /**
         * Selection mode that allows one selected date at one time. This is the default mode.
         * When switching from [.SELECTION_MODE_MULTIPLE], this will select the same date
         * as from [.getSelectedDate], which should be the last selected date
         */
        const val SELECTION_MODE_SINGLE = 1

        /**
         * Selection mode which allows more than one selected date at one time.
         */
        const val SELECTION_MODE_MULTIPLE = 2

        /**
         * Selection mode which allows selection of a range between two dates
         */
        const val SELECTION_MODE_RANGE = 3

        /**
         * Do not show any non-enabled dates
         */
        const val SHOW_NONE = 0

        /**
         * Show dates from the proceeding and successive months, in a disabled state.
         * This flag also enables the [.SHOW_OUT_OF_RANGE] flag to prevent odd blank areas.
         */
        const val SHOW_OTHER_MONTHS = 1

        /**
         * Show dates that are outside of the min-max range.
         * This will only show days from the current month unless [.SHOW_OTHER_MONTHS] is enabled.
         */
        const val SHOW_OUT_OF_RANGE = 2

        /**
         * Show days that are individually disabled with decorators.
         * This will only show dates in the current month and inside the minimum and maximum date range.
         */
        const val SHOW_DECORATED_DISABLED = 4

        /**
         * The default flags for showing non-enabled dates. Currently only shows [.SHOW_DECORATED_DISABLED]
         */
        const val SHOW_DEFAULTS = SHOW_DECORATED_DISABLED

        /**
         * Show all the days
         */
        const val SHOW_ALL = SHOW_OTHER_MONTHS or SHOW_OUT_OF_RANGE or SHOW_DECORATED_DISABLED

        /**
         * Use this orientation to animate the title vertically
         */
        const val VERTICAL = 0

        /**
         * Use this orientation to animate the title horizontally
         */
        const val HORIZONTAL = 1

        /**
         * Default tile size in DIPs. This is used in cases where there is no tile size specificed and the view is set to [WRAP_CONTENT][ViewGroup.LayoutParams.WRAP_CONTENT]
         */
        const val DEFAULT_TILE_SIZE_DP = 44

        private val DEFAULT_DAYS_IN_WEEK = 7
        private val DEFAULT_MAX_WEEKS = 6
        private val DAY_NAMES_ROW = 1

        private val DEFAULT_TITLE_FORMATTER = DateFormatTitleFormatter()

        private fun getThemeAccentColor(context: Context): Int {
            val colorAttr: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                android.R.attr.colorAccent
            } else {
                //Get colorAccent defined for AppCompat
                context.resources.getIdentifier("colorAccent", "attr", context.packageName)
            }
            val outValue = TypedValue()
            context.theme.resolveAttribute(colorAttr, outValue, true)
            return outValue.data
        }

        private fun getThemePrimaryColor(context: Context): Int {
            val colorAttr: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                android.R.attr.colorPrimary
            } else {
                //Get colorAccent defined for AppCompat
                context.resources.getIdentifier("colorPrimary", "attr", context.packageName)
            }
            val outValue = TypedValue()
            context.theme.resolveAttribute(colorAttr, outValue, true)
            return outValue.data
        }

        /**
         * @param showOtherDates int flag for show other dates
         * @return true if the other months flag is set
         */
        fun showOtherMonths(@ShowOtherDates showOtherDates: Int): Boolean =
                showOtherDates and SHOW_OTHER_MONTHS != 0

        /**
         * @param showOtherDates int flag for show other dates
         * @return true if the out of range flag is set
         */
        fun showOutOfRange(@ShowOtherDates showOtherDates: Int): Boolean =
                showOtherDates and SHOW_OUT_OF_RANGE != 0

        /**
         * @param showOtherDates int flag for show other dates
         * @return true if the decorated disabled flag is set
         */
        fun showDecoratedDisabled(@ShowOtherDates showOtherDates: Int): Boolean =
                showOtherDates and SHOW_DECORATED_DISABLED != 0

        /**
         * Clamp the size to the measure spec.
         *
         * @param size Size we want to be
         * @param spec Measure spec to clamp against
         * @return the appropriate size to pass to [View.setMeasuredDimension]
         */
        private fun clampSize(size: Int, spec: Int): Int {
            val specMode = View.MeasureSpec.getMode(spec)
            val specSize = View.MeasureSpec.getSize(spec)
            return when (specMode) {
                View.MeasureSpec.EXACTLY -> {
                    specSize
                }
                View.MeasureSpec.AT_MOST -> {
                    Math.min(size, specSize)
                }
                else -> {
                    size
                }
            }
        }
    }
}