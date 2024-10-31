package com.inmaeo.tictactwo.views.components

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.GridLayout

class SwipeDetectingGridLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    private var gestureDetector: GestureDetector? = null

    fun setGestureDetector(gestureDetector: GestureDetector) {
        this.gestureDetector = gestureDetector
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        gestureDetector?.onTouchEvent(ev)
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector?.onTouchEvent(event)

        if (event.action == MotionEvent.ACTION_UP) {
            performClick()
        }

        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    inner class TapGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            performClick()
            return true
        }
    }

    init {
        gestureDetector = GestureDetector(context, TapGestureListener())
    }
}
