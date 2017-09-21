package android.support.v4.view

import android.content.Context
import android.util.AttributeSet

/**
 * [.setChildrenDrawingOrderEnabledCompat] does some reflection that isn't needed.
 * And was making view creation time rather large. So lets override it and make it better!
 */
open class BetterViewPager : ViewPager {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    public override fun setChildrenDrawingOrderEnabledCompat(enable: Boolean) {
        isChildrenDrawingOrderEnabled = enable
    }
}
