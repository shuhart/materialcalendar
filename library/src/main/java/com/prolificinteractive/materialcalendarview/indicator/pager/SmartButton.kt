package com.prolificinteractive.materialcalendarview.indicator.pager

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.Gravity
import com.prolificinteractive.materialcalendarview.R

/**
 * Created by Bogdan Kornev
 * on 23.09.2016, 9:38 PM.
 */
class SmartButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var mainTextColor = Color.WHITE
    private var pressedColor: Int? = null
    private var cornerRadius: Int = 0
    private var disabledColor = 0
    private var mainColor: Int = 0

    fun init(mainTextColor: Int = Color.WHITE,
             pressedColor: Int? = null,
             cornerRadius: Int = 0,
             mainColor: Int = Color.parseColor("#f8f9f9"),
             disabledColor: Int = mainColor) {

        this.mainTextColor = mainTextColor
        this.pressedColor = pressedColor
        this.cornerRadius = cornerRadius
        this.disabledColor = disabledColor
        this.mainColor = mainColor

        setTextColor(mainTextColor)
        gravity = Gravity.CENTER

        isClickable = true
        setBackground()
    }

    private fun setBackground() {
        if (isInEditMode) {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.mutate()
            shape.cornerRadius = cornerRadius.toFloat()
            shape.setColor(mainColor)
            setBackgroundDrawable(shape)
            return
        }

        if (!isEnabled) {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.mutate()
            shape.cornerRadius = cornerRadius.toFloat()
            shape.setColor(disabledColor)
            setBackgroundDrawable(shape)
            return
        }

        if (background != null) {
            val drawable = background
            if (drawable is ColorDrawable) {
                val color = drawable.color
                if (color != disabledColor) {
                    setBigButtonColor(color, mainTextColor)
                    return
                }
            }
        }

        if (mainColor == 0) {
            setBigButtonColor(disabledColor, Color.parseColor("#cacdd0"))
        } else {
            setBigButtonColor(mainColor, mainTextColor)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        setBackground()
    }

    fun setBigButtonColor(color: Int, textColor: Int = mainTextColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mainTextColor = textColor
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.mutate()
            shape.cornerRadius = cornerRadius.toFloat()
            shape.setColor(color)

            val rippleDrawable = ContextCompat.getDrawable(context, R.drawable.smart_button_background) as RippleDrawable
            if (pressedColor != null) {
                rippleDrawable.setColor(ColorStateList.valueOf(pressedColor!!))
            }
            rippleDrawable.setDrawableByLayerId(R.id.ripple_background_layer, shape)

            val states = StateListDrawable()
            states.addState(intArrayOf(), rippleDrawable)
            states.setExitFadeDuration(150)
            background = states
            setTextColor(textColor)
        } else {
            mainTextColor = textColor
            val states = StateListDrawable()

            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadius = cornerRadius.toFloat()
            shape.setColor(color)

            val pressedColorInternal: Int = if (pressedColor != null) {
                pressedColor!!
            } else {
                Color.argb(150, Color.red(color), Color.green(color), Color.blue(color))
            }

            val pressedDrawable = GradientDrawable()
            pressedDrawable.shape = GradientDrawable.RECTANGLE
            pressedDrawable.cornerRadius = cornerRadius.toFloat()
            pressedDrawable.setColor(pressedColorInternal)

            states.addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
            states.addState(intArrayOf(), shape)
            states.setExitFadeDuration(150)
            setBackgroundDrawable(states)
            setTextColor(textColor)
        }
    }
}
