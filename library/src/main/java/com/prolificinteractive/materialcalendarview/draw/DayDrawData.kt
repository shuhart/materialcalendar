package com.prolificinteractive.materialcalendarview.draw

import android.graphics.Rect

/**
 * Created by Bogdan Kornev
 * on 9/21/2017, 8:17 PM.
 */
class DayDrawData(var radius: Float = 0f,
                  var cx: Float = 0f,
                  var cy: Float = 0f,
                  val rangeRect: Rect = Rect(),
                  val firstRect: Rect = Rect(),
                  val lastRect: Rect = Rect(),
                  var bottomTopDayPadding: Int = 0)