package com.jdh.calendar_kt

import android.view.*

interface OnItemListener {
    fun onItemClick(dayText: String)
    fun onTouchEvent(view: View, event: MotionEvent, dayText: String): Boolean
    fun onDrag(view: View, event: DragEvent, dayText: String): Boolean
}