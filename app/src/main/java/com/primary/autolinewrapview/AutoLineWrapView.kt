package com.primary.autolinewrapview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/**
 * Created by David at 2023/3/27
 * right view could auto integral line wrap
 */
class AutoLineWrapView(context: Context, attributeSet: AttributeSet) :
    ViewGroup(context, attributeSet) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        if (childCount != 3) throw IllegalStateException("we just has 3 child (view divider view)")
        val left = getChildAt(0)
        val center = getChildAt(1)
        val right = getChildAt(2)
        measureChild(left, widthMeasureSpec, heightMeasureSpec)
        measureChild(
            center,
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(left.measuredHeight, MeasureSpec.EXACTLY)
        )
        measureChild(right, widthMeasureSpec, heightMeasureSpec)
        val leftWidth = left.measuredWidth
        val centerWidth =
            center.measuredWidth + (center.layoutParams as MarginLayoutParams).let { it.leftMargin + it.rightMargin }
        val rightWidth = right.measuredWidth
        val childrenTotalWidth = leftWidth + centerWidth + rightWidth
        if (childrenTotalWidth > widthSize) {
            // we need change right view to next line. so the height will be (left,center) height + right height
            setMeasuredDimension(widthSize, left.measuredHeight + right.measuredHeight)
        } else {
            val maxHeight = maxOf(left.measuredHeight, right.measuredHeight)
            setMeasuredDimension(widthSize, maxHeight)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val left = getChildAt(0)
        val center = getChildAt(1)
        val right = getChildAt(2)
        val leftWidth = left.measuredWidth
        val centerWidth =
            center.measuredWidth + (center.layoutParams as MarginLayoutParams).let { it.leftMargin + it.rightMargin }
        val rightWidth = right.measuredWidth
        val childrenTotalWidth = leftWidth + centerWidth + rightWidth
        left.layout(0, 0, left.measuredWidth, left.measuredHeight)
        center.layout(
            left.measuredWidth + (center.layoutParams as MarginLayoutParams).leftMargin,
            0,
            left.measuredWidth + (center.layoutParams as MarginLayoutParams).leftMargin + center.measuredWidth,
            center.measuredHeight
        )
        if (childrenTotalWidth > measuredWidth) {
            right.layout(0, left.measuredHeight, right.measuredWidth, left.measuredHeight + right.measuredHeight)
        } else if (center.visibility == View.GONE) {
            right.layout(0, 0, right.measuredWidth, right.measuredHeight)
        } else {
            right.layout(left.measuredWidth + centerWidth, 0, childrenTotalWidth, right.measuredHeight)
        }
    }
}