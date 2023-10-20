package com.jdh.calendar_kt

import android.view.*
import java.time.LocalDate

interface OnItemListener {
    fun onItemClick(dayText: LocalDate?)
    fun onTouchEvent(view: View, event: MotionEvent, dayText: LocalDate?): Boolean
    fun onDrag(view: View, event: DragEvent, dayText: String): Boolean
}