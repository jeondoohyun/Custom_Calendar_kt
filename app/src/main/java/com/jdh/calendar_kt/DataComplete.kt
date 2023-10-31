package com.jdh.calendar_kt

import java.time.LocalDate
import java.util.ArrayList

data class DataComplete(var localDate: LocalDate,   // 달력에 찍히는 날짜
                        var color: ArrayList<Int>?)
