package com.jdh.calendar_kt

import android.view.*
import java.time.LocalDate

interface OnItemListener {
    fun onItemClick(dayText: LocalDate?)
    fun onCheck(isChecked: Boolean, dataPlan: DataPlan)
    fun onTouchEvent(view: View, event: MotionEvent): Boolean
    fun onDrag(view: View, event: DragEvent, dayText: String): Boolean
}