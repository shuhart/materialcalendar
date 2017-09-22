package com.prolificinteractive.materialcalendarview

import android.os.Parcel
import android.os.Parcelable
import com.prolificinteractive.materialcalendarview.utils.CalendarUtils

import java.util.Calendar
import java.util.Date

/**
 * An imputable representation of a day on a calendar
 */
class CalendarDay
/**
 * @param year  new instance's year
 * @param month new instance's month as defined by [java.util.Calendar]
 * @param day   new instance's day of month
 * @see CalendarDay.from
 */
(
        /**
         * Get the year
         *
         * @return the year for this day
         */
        val year: Int,
        /**
         * Get the month, represented by values from [Calendar]
         *
         * @return the month of the year as defined by [Calendar]
         */
        val month: Int,
        /**
         * Get the day
         *
         * @return the day of the month for this day
         */
        val day: Int) : Parcelable {

    /**
     * Cache for calls to [.getCalendar]
     */
    @Transient private var mCalendar: Calendar? = null

    /**
     * Cache for calls to [.getDate]
     */
    @Transient private var mDate: Date? = null

    /**
     * @param calendar source to pull date information from for this instance
     * @see CalendarDay.from
     */
    @JvmOverloads constructor(calendar: Calendar = CalendarUtils.instance) : this(
            CalendarUtils.getYear(calendar),
            CalendarUtils.getMonth(calendar),
            CalendarUtils.getDay(calendar)
    )

    /**
     * @param date source to pull date information from for this instance
     * @see CalendarDay.from
     */
    @Deprecated("")
    constructor(date: Date) : this(CalendarUtils.getInstance(date))

    /**
     * Get this day as a [Date]
     *
     * @return a date with this days information
     */
    val date: Date
        get() {
            if (mDate == null) {
                mDate = calendar.time
            }
            return mDate!!
        }

    /**
     * Get this day as a [Calendar]
     *
     * @return a new calendar instance with this day information
     */
    val calendar: Calendar
        get() {
            if (mCalendar == null) {
                mCalendar = CalendarUtils.instance
                copyTo(mCalendar!!)
            }
            return mCalendar!!
        }

    fun copyToMonthOnly(calendar: Calendar) {
        calendar.clear()
        calendar.set(year, month, 1)
    }

    /**
     * Copy this day's information to the given calendar instance
     *
     * @param calendar calendar to set date information to
     */
    fun copyTo(calendar: Calendar) {
        calendar.clear()
        calendar.set(year, month, day)
    }

    /**
     * Determine if this day is within a specified range
     *
     * @param minDate the earliest day, may be null
     * @param maxDate the latest day, may be null
     * @return true if the between (inclusive) the min and max dates.
     */
    fun isInRange(minDate: CalendarDay?, maxDate: CalendarDay?): Boolean =
            !(minDate != null && minDate.isAfter(this)) && !(maxDate != null && maxDate.isBefore(this))

    /**
     * Determine if this day is before the given instance
     *
     * @param other the other day to test
     * @return true if this is before other, false if equal or after
     */
    fun isBefore(other: CalendarDay): Boolean {
        return if (year == other.year) {
            if (month == other.month) day < other.day else month < other.month
        } else {
            year < other.year
        }
    }

    /**
     * Determine if this day is after the given instance
     *
     * @param other the other day to test
     * @return true if this is after other, false if equal or before
     */
    fun isAfter(other: CalendarDay): Boolean {
        return if (year == other.year) {
            if (month == other.month) day > other.day else month > other.month
        } else {
            year > other.year
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }

        val that = other as CalendarDay?

        return day == that!!.day && month == that.month && year == that.year
    }

    override fun hashCode(): Int = hashCode(year, month, day)

    override fun toString(): String = "CalendarDay{$year-$month-$day}"

    /*
     * Parcelable Stuff
     */

    constructor(`in`: Parcel) : this(`in`.readInt(), `in`.readInt(), `in`.readInt()) {}

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(year)
        dest.writeInt(month)
        dest.writeInt(day)
    }

    companion object {

        /**
         * Get a new instance set to today
         *
         * @return CalendarDay set to today's date
         */
        fun today(): CalendarDay = from(CalendarUtils.instance)!!

        /**
         * Get a new instance set to the specified day
         *
         * @param year  new instance's year
         * @param month new instance's month as defined by [java.util.Calendar]
         * @param day   new instance's day of month
         * @return CalendarDay set to the specified date
         */
        fun from(year: Int, month: Int, day: Int): CalendarDay = CalendarDay(year, month, day)

        /**
         * Get a new instance set to the specified day
         *
         * @param calendar [Calendar] to pull date information from. Passing null will return null
         * @return CalendarDay set to the specified date
         */
        fun from(calendar: Calendar?): CalendarDay? {
            return if (calendar == null) {
                null
            } else from(
                    CalendarUtils.getYear(calendar),
                    CalendarUtils.getMonth(calendar),
                    CalendarUtils.getDay(calendar)
            )
        }

        /**
         * Get a new instance set to the specified day
         *
         * @param date [Date] to pull date information from. Passing null will return null.
         * @return CalendarDay set to the specified date
         */
        fun from(date: Date?): CalendarDay? {
            return if (date == null) {
                null
            } else from(CalendarUtils.getInstance(date))
        }

        private fun hashCode(year: Int, month: Int, day: Int): Int = //Should produce hashes like "20150401"
                year * 10000 + month * 100 + day
        @JvmField
        val CREATOR: Parcelable.Creator<CalendarDay> = object : Parcelable.Creator<CalendarDay> {
            override fun createFromParcel(`in`: Parcel): CalendarDay = CalendarDay(`in`)

            override fun newArray(size: Int): Array<CalendarDay?> = arrayOfNulls(size)
        }
    }
}
