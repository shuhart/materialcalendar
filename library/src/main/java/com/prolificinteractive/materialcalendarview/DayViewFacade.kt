package com.prolificinteractive.materialcalendarview

import android.graphics.drawable.Drawable

import java.util.Collections
import java.util.LinkedList

/**
 * Abstraction layer to help in decorating Day views
 */
class DayViewFacade constructor() {

    var isDecorated: Boolean = false
        private set

    private var backgroundDrawable: Drawable? = null
    private var selectionDrawable: Drawable? = null
    private val spans = LinkedList<Span>()
    private var daysDisabled = false

    init {
        isDecorated = false
    }

    /**
     * Set a drawable to draw behind everything else
     *
     * @param drawable Drawable to draw behind everything
     */
    fun setBackgroundDrawable(drawable: Drawable) {
        this.backgroundDrawable = drawable
        isDecorated = true
    }

    /**
     * Set a custom selection drawable
     * TODO: define states that can/should be used in StateListDrawables
     *
     * @param drawable the drawable for selection
     */
    fun setSelectionDrawable(drawable: Drawable) {
        selectionDrawable = drawable
        isDecorated = true
    }

    /**
     * Add a span to the entire text of a day
     *
     * @param span text span instance
     */
    fun addSpan(span: Any) {
        this.spans.add(Span(span))
        isDecorated = true
    }

    /**
     *
     * Set days to be in a disabled state, or re-enabled.
     *
     * Note, passing true here will **not** override minimum and maximum dates, if set.
     * This will only re-enable disabled dates.
     *
     * @param daysDisabled true to disable days, false to re-enable days
     */
    fun setDaysDisabled(daysDisabled: Boolean) {
        this.daysDisabled = daysDisabled
        this.isDecorated = true
    }

    fun reset() {
        backgroundDrawable = null
        selectionDrawable = null
        spans.clear()
        isDecorated = false
        daysDisabled = false
    }

    /**
     * Apply things set this to other
     *
     * @param other facade to apply our data to
     */
    fun applyTo(other: DayViewFacade) {
        if (selectionDrawable != null) {
            other.setSelectionDrawable(selectionDrawable!!)
        }
        if (backgroundDrawable != null) {
            other.setBackgroundDrawable(backgroundDrawable!!)
        }
        other.spans.addAll(spans)
        other.isDecorated = other.isDecorated or this.isDecorated
        other.daysDisabled = daysDisabled
    }

    fun getSelectionDrawable(): Drawable? = selectionDrawable

    fun getBackgroundDrawable(): Drawable? = backgroundDrawable

    fun getSpans(): List<Span> = Collections.unmodifiableList(spans)

    /**
     * Are days from this facade disabled
     *
     * @return true if disabled, false if not re-enabled
     */
    fun areDaysDisabled(): Boolean = daysDisabled

    class Span(val span: Any)
}
