package com.prolificinteractive.materialcalendarview.draw

import android.util.Log
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

/**
 * Created by Bogdan Kornev
 * on 9/21/2017, 7:59 PM.
 */
class DayDrawProvider(val mcv: MaterialCalendarView) {
    private var clazz: Class<out DayDrawDelegate> = DefaultDayDrawDelegate::class.java

    fun registerDayDrawDelegate(clazz: Class<out DayDrawDelegate>) {
        try {
//            mcv.forceDayDrawDelegate(clazz.newInstance())
            this.clazz = clazz
        } catch (e: Throwable) {
            Log.e(DayDrawProvider::class.java.simpleName, e.message)
        }
    }
}