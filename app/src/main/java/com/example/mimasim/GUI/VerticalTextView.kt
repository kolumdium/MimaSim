package com.example.mimasim.GUI

import android.content.Context
import android.graphics.Canvas
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity

/**
 * Created by Martin on 07.09.2017.
 */
// source https://stackoverflow.com/questions/1258275/vertical-rotated-label-in-android/7855852#7855852
class VerticalTextView(context: Context, attrs: AttributeSet) : android.support.v7.widget.AppCompatTextView(context, attrs) {
    internal val topDown: Boolean

    init {
        val gravity = gravity
        if (Gravity.isVertical(gravity) && gravity and Gravity.VERTICAL_GRAVITY_MASK == Gravity.BOTTOM) {
            setGravity(gravity and Gravity.HORIZONTAL_GRAVITY_MASK or Gravity.TOP)
            topDown = false
        } else
            topDown = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        val textPaint = paint
        textPaint.color = currentTextColor
        textPaint.drawableState = drawableState

        canvas.save()

        if (topDown) {
            canvas.translate(width.toFloat(), 0f)
            canvas.rotate(90f)
        } else {
            canvas.translate(0f, height.toFloat())
            canvas.rotate(-90f)
        }


        canvas.translate(compoundPaddingLeft.toFloat(), extendedPaddingTop.toFloat())

        layout.draw(canvas)
        canvas.restore()
    }
}
