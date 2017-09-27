package com.shuhart.materialcalendarview.indicator.basic

import android.content.Context
import android.graphics.PorterDuff
import android.os.Build
import android.util.TypedValue
import android.widget.ImageView

/**
 * An [android.widget.ImageView] to pragmatically set the color of arrows
 * using a [android.graphics.ColorFilter]
 */
class DirectionButton(context: Context) : ImageView(context) {

    init {

        setBackgroundResource(getThemeSelectableBackgroundId(context))
    }

    fun setColor(color: Int) {
        setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        alpha = if (enabled) 1f else 0.1f
    }

    private fun getThemeSelectableBackgroundId(context: Context): Int {
        //Get selectableItemBackgroundBorderless defined for AppCompat
        var colorAttr = context.resources.getIdentifier(
                "selectableItemBackgroundBorderless", "attr", context.packageName)

        if (colorAttr == 0) {
            colorAttr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                android.R.attr.selectableItemBackgroundBorderless
            } else {
                android.R.attr.selectableItemBackground
            }
        }

        val outValue = TypedValue()
        context.theme.resolveAttribute(colorAttr, outValue, true)
        return outValue.resourceId
    }
}
