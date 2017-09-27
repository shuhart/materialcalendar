package com.shuhart.materialcalendarview.indicator.basic

import android.animation.Animator
import android.text.TextUtils
import android.util.TypedValue
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.shuhart.materialcalendarview.CalendarDay
import com.shuhart.materialcalendarview.MaterialCalendarView
import com.shuhart.materialcalendarview.format.TitleFormatter

class TitleChanger(private val title: TextView) {
    var titleFormatter: TitleFormatter? = null

    private val animDelay: Int
    private val animDuration: Int
    private val translate: Int
    private val interpolator = DecelerateInterpolator(2f)

    var orientation = MaterialCalendarView.VERTICAL

    private var lastAnimTime: Long = 0
    private var previousMonth: CalendarDay? = null

    init {

        val res = title.resources

        animDelay = DEFAULT_ANIMATION_DELAY

        animDuration = res.getInteger(android.R.integer.config_shortAnimTime) / 2

        translate = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEFAULT_Y_TRANSLATION_DP.toFloat(), res.displayMetrics
        ).toInt()
    }

    fun change(currentMonth: CalendarDay?) {
        val currentTime = System.currentTimeMillis()

        if (currentMonth == null) {
            return
        }

        if (TextUtils.isEmpty(title.text) || currentTime - lastAnimTime < animDelay) {
            doChange(currentTime, currentMonth, false)
        }

        if (currentMonth == previousMonth || currentMonth.month == previousMonth!!.month && currentMonth.year == previousMonth!!.year) {
            return
        }

        doChange(currentTime, currentMonth, true)
    }

    private fun doChange(now: Long, currentMonth: CalendarDay, animate: Boolean) {

        title.animate().cancel()
        doTranslation(title, 0)

        title.alpha = 1f
        lastAnimTime = now

        val newTitle = titleFormatter!!.format(currentMonth)

        if (!animate) {
            title.text = newTitle
        } else {
            val translation = translate * if (previousMonth!!.isBefore(currentMonth)) 1 else -1
            val viewPropertyAnimator = title.animate()

            if (orientation == MaterialCalendarView.HORIZONTAL) {
                viewPropertyAnimator.translationX((translation * -1).toFloat())
            } else {
                viewPropertyAnimator.translationY((translation * -1).toFloat())
            }

            viewPropertyAnimator
                    .alpha(0f)
                    .setDuration(animDuration.toLong())
                    .setInterpolator(interpolator)
                    .setListener(object : AnimatorListener() {

                        override fun onAnimationCancel(animator: Animator) {
                            doTranslation(title, 0)
                            title.alpha = 1f
                        }

                        override fun onAnimationEnd(animator: Animator) {
                            title.text = newTitle
                            doTranslation(title, translation)

                            val titleAnimator = title.animate()
                            if (orientation == MaterialCalendarView.HORIZONTAL) {
                                titleAnimator.translationX(0f)
                            } else {
                                titleAnimator.translationY(0f)
                            }

                            titleAnimator
                                    .alpha(1f)
                                    .setDuration(animDuration.toLong())
                                    .setInterpolator(interpolator)
                                    .setListener(AnimatorListener())
                                    .start()
                        }
                    }).start()
        }

        previousMonth = currentMonth
    }

    private fun doTranslation(title: TextView, translate: Int) {
        if (orientation == MaterialCalendarView.HORIZONTAL) {
            title.translationX = translate.toFloat()
        } else {
            title.translationY = translate.toFloat()
        }
    }

    fun setPreviousMonth(previousMonth: CalendarDay) {
        this.previousMonth = previousMonth
    }

    companion object {

        val DEFAULT_ANIMATION_DELAY = 400
        val DEFAULT_Y_TRANSLATION_DP = 20
    }
}
