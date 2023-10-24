package com.jdh.calendar_kt

// 달력에 원을 체크하기 위한 데이터
data class DataDay(var mapDate: HashMap<String, ArrayList<DataPlan>?> = HashMap())
