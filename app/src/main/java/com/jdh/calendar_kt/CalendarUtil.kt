package com.jdh.calendar_kt

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

class CalendarUtil {
    companion object{
        @RequiresApi(Build.VERSION_CODES.O)
        var selectedDate: LocalDate = LocalDate.now()
        @RequiresApi(Build.VERSION_CODES.O)
        var today: LocalDate = LocalDate.now()
    }
}