


package com.jdh.calendar_kt

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jdh.calendar_kt.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Arrays


class MainActivity : AppCompatActivity(), OnItemListener {
    companion object {
        var recyclerView_calendar_height = 0
        lateinit var container: LinearLayout
        var dataPlanArray = ArrayList<DataPlan>()   // 메뉴에서 추가된 할일
        var completeArray = ArrayList<DataPlan>()
        lateinit var dataDay: DataDay   // 달력에 할일완료 표시에 쓰일 데이터. key 해당날짜, value 할일완료 데이터
        lateinit var adapterCalendar: AdapterCalendar
        lateinit var adapterPlan: AdapterPlan
    }

    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!
    lateinit var selectedDate: LocalDate

    private val PRE_NAME_PLAN = "QWER"
    private val PRE_KEY_PLAN = "PLAN"
    private val PRE_KEY_CALENDAR = "CAL"

//    lateinit var dataPlan: DataPlan
    var dayList = ArrayList<DataComplete?>()

    var tmpDataPlan: DataPlan = DataPlan()
    var tmpDayList: ArrayList<DataComplete?> = ArrayList<DataComplete?>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            var ad = AlertDialog.Builder(this)
            ad.setTitle("종료")
            ad.setMessage("안드로이드 버전이 너무 낮습니다. 앱이 종료됩니다.")
            ad.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                finishAffinity()
                System.exit(0)
            })
            ad.create().show()
        }

        // 상태바 투명화
        setStatusBarTransparent()

        binding.rootLinearLayout.setPadding(
            0,
            statusBarHeight(),
            0,
            navigationHeight()
        )
        dataDay = DataDay()
        container = LinearLayout(this)

        var s = PreferenceManager().getString(this, PRE_NAME_PLAN, PRE_KEY_PLAN)?.split(",")
        s?.let { stringToPlanArray(it) }
        var s1 = PreferenceManager().getString(this, PRE_NAME_PLAN, PRE_KEY_CALENDAR).toString().split("_")
        s1?.let { stringToCalendarArray(it) }

        binding.monthYearText.setOnClickListener {
            // 하드 코딩으로 내부저장소에 저장
            PreferenceManager().setString(this, PRE_NAME_PLAN, PRE_KEY_CALENDAR, "2023-10-31,1_2023-10-2_2023-10-3,1,2_2023-10-4,1_2023-10-5,1,2_2023-11-1,1")
        }

        binding.containerHeight.getViewTreeObserver()
            .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val height: Int = binding.containerHeight.getHeight()
                    recyclerView_calendar_height = height
                    binding.containerHeight.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                    setMonthView()
                }
            })

        binding.recyclerviewCalendar.getViewTreeObserver()
            .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.recyclerviewCalendar.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                    binding.progressbarByRecyclerview.visibility = View.INVISIBLE
                    binding.recyclerviewCalendar.visibility = View.VISIBLE
                }
            })

        // 이전 달 이동
        binding.preBtn.setOnClickListener {
            CalendarUtil.selectedDate = CalendarUtil.selectedDate.minusMonths(1)
            runOnUiThread {
                setMonthView()
            }
        }

        // 다음 달 이동
        binding.nextBtn.setOnClickListener {
            CalendarUtil.selectedDate = CalendarUtil.selectedDate.plusMonths(1)
            runOnUiThread {
                setMonthView()
            }
        }

        // 메뉴 열기
        binding.menuBtn.setOnClickListener {
            if (!binding.drawerView.isDrawerOpen(GravityCompat.START)) {
                binding.drawerView.openDrawer(GravityCompat.START)
            }
        }

        // 할일 알러트 생성
        binding.addBtn.setOnClickListener {
            // 커스텀 알러트
            var builder = AlertDialog.Builder(this)
            var view = LayoutInflater.from(this).inflate(R.layout.custom_alert_addplan, null)

            var editText = view.findViewById<EditText>(R.id.editText)
            var circleRedBack = view.findViewById<ImageView>(R.id.circleRedBack)
            var circleYellowBack = view.findViewById<ImageView>(R.id.circleYellowBack)
            var circleSkyBack = view.findViewById<ImageView>(R.id.circleSkyBack)
            var positiveButton = view.findViewById<TextView>(R.id.positiveButton)
            var negativeButton = view.findViewById<TextView>(R.id.negativeButton)

            circleRedBack.setOnClickListener {
                circleRedBack.isSelected = true
                circleYellowBack.isSelected = false
                circleSkyBack.isSelected = false
            }

            circleYellowBack.setOnClickListener {
                circleYellowBack.isSelected = true
                circleRedBack.isSelected = false
                circleSkyBack.isSelected = false
            }

            circleSkyBack.setOnClickListener {
                circleSkyBack.isSelected = true
                circleYellowBack.isSelected = false
                circleRedBack.isSelected = false
            }

            builder.setView(view)

            var ad = builder.create()

            positiveButton.setOnClickListener {
                if (editText.text.isEmpty()) {
                    Toast.makeText(this, "내용을 입력하세요",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (!circleRedBack.isSelected && !circleYellowBack.isSelected && !circleSkyBack.isSelected) {
                    Toast.makeText(this, "색상을 선택하세요",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                var color = 0
                if (circleRedBack.isSelected) color = 1
                else if (circleYellowBack.isSelected) color = 2
                else if (circleSkyBack.isSelected) color = 3

                var removeList = ArrayList<DataPlan>()
                dataPlanArray.forEach {
                    if (it.color == color) removeList.add(it)  // 반복문 돌리고 있는중 해당 리스트 요소를 삭제하면 index가 바뀌어 에러 발생. 해당 요소를 받아 놓고 반복문 이후에 삭제 해준다.
                }
                dataPlanArray.removeAll(removeList)
                dataPlanArray.add(DataPlan(editText.text.toString(), color, false))

                // recyclerviewPlan adapter 설정 하기
//                adapterPlan = AdapterPlan(dataPlanArray, binding.recyclerviewPlan)
//                binding.recyclerviewPlan.adapter = adapterPlan

                adapterPlan.setData(dataPlanArray)
                adapterPlan.notifyDataSetChanged()

                PreferenceManager().setString(this, PRE_NAME_PLAN, PRE_KEY_PLAN, planArrayToString(dataPlanArray))

                ad.dismiss()
            }

            negativeButton.setOnClickListener {
                ad.dismiss()
            }
            ad.show()
        }

        // plan complete
        binding.completeBtn.setOnClickListener {
            if (dataPlanArray.size == 0) Toast.makeText(this, "메뉴에서 할일을 추가하세요!", Toast.LENGTH_SHORT).show()
            else {
                var builder = AlertDialog.Builder(this)
                var view = LayoutInflater.from(this).inflate(R.layout.recyclerview_complete, null)

                var recyclerviewComplete = view.findViewById<RecyclerView>(R.id.recyclerviewComplete)
                builder.setView(view)

                val adapterComplete = AdapterComplete(dataPlanArray, this)

                recyclerviewComplete.adapter = adapterComplete
                adapterComplete.notifyDataSetChanged()

                builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->

                    var today = "${CalendarUtil.today.year}-${CalendarUtil.today.monthValue}-${CalendarUtil.today.dayOfMonth}"
                    Log.e("체크체크_2","$today")
                    if (!dataDay.mapDate.containsKey(today)) dataDay.mapDate[today] = ArrayList()

                    if (tmpDataPlan.success) {
                        dataDay.mapDate[today]?.add(tmpDataPlan.color)
                    } else {
                        dataDay.mapDate[today]?.remove(tmpDataPlan.color)
                    }

                    dataPlanArray.forEach {
                        if (it.color == tmpDataPlan.color) it.success = tmpDataPlan.success
                    }

                    dayList = dayInMonthArray(CalendarUtil.selectedDate, dataDay)

//                    dayList = tmpDayList

                    runOnUiThread {
                        adapterCalendar.setData(dayList)
                        adapterCalendar.notifyDataSetChanged()
                    }


                    // 2개 저장하기
                    PreferenceManager().setString(this, PRE_NAME_PLAN, PRE_KEY_PLAN, planArrayToString(dataPlanArray))
                    PreferenceManager().setString(this, PRE_NAME_PLAN, PRE_KEY_CALENDAR, calendarArrayToString(dataDay))

                    dialog.dismiss()
                })

                builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->  dialog.dismiss()})

                var ab = builder.create()
                ab.show()
            }
        }


//        binding.circleSkyBack.setOnClickListener {
//            Log.e("원클릭","${binding.circleSkyBack?.isSelected}")
//            binding.circleSkyBack?.isSelected = binding.circleSkyBack?.isSelected != true
////            binding.circleSkyBack.isSelected = true
//        }


        CalendarUtil.selectedDate = LocalDate.now()
        CalendarUtil.today = LocalDate.now()




    }   // onCreate..

//    fun planArrayToString(dataPlanArray: ArrayList<DataComplete?>): String {
//        // 여기가 에러
//        var s1 = ""
//        if (dataPlanArray.size > 0) {
//            dataPlanArray.forEach {
//                Log.e("ss","${it?.arrDataPlan?.size}")
//                if (it?.arrDataPlan != null) {
//                    it?.arrDataPlan?.forEach {
//                        s1+="${it.contentPlan},${it.color},${it.success},"
//                        Log.e("ss","${s1}")
//                    }
//                    s1 = s1.substring(0, s1.length-1)
//                }
//            }
//        }
//
//        return s1
//    }

    fun planArrayToString(dataPlanArray: ArrayList<DataPlan>): String {
        // 여기가 에러
        var s1 = ""
        if (dataPlanArray.size > 0) {
            dataPlanArray.forEach {
                s1+="${it!!.contentPlan},${it.color},${it.success},"
            }
            s1 = s1.substring(0, s1.length-1)
        }
        return s1
    }

    fun stringToPlanArray(s: List<String>) {
        if (s.isNotEmpty()) {
            for (i in 0 until s.size/3) {
                dataPlanArray.add(DataPlan(s[i*3],s[i*3+1].toInt(),s[i*3+2].toBoolean()))
            }

            runOnUiThread {
                adapterPlan = AdapterPlan(dataPlanArray, binding.recyclerviewPlan)
                binding.recyclerviewPlan.adapter = adapterPlan
                adapterPlan.notifyDataSetChanged()
            }
        }
    }

    fun calendarArrayToString(dataDay: DataDay): String {
        var s1 = ""
        dataDay.mapDate.forEach {
            Log.e("체크체크_d", "${it.key}, ${it.value}")
        }
        dataDay.mapDate.forEach {
            var key = it.key
            Log.e("체크체크_c","${key}")
            if (it.value?.isNotEmpty() == true) {
                s1 += "${key},"
                it.value?.forEach {
                    s1 += "${it},"
                }
                Log.e("체크체크_b","${s1}")
                s1 = s1.substring(0, s1.length-1)
                s1 += "_"
            }

//            s1 = s1.substring(0, s1.length-1)
            Log.e("체크체크_성공", "${s1}")
        }
        if (s1.isNotEmpty()) s1 = s1.substring(0, s1.length-1)
        Log.e("체크체크_실패", "${s1}")
        return s1
    }

    fun stringToCalendarArray(s1: List<String>) {
        s1.forEach {
            Log.e("eee","${it}, ${it.isNotEmpty()}, ${s1.isNotEmpty()}")
        }
        if (s1.isNotEmpty()) {
            for (i in 0 until s1.size) {
                if (s1[i].isNotEmpty()) {
                    var s2 = s1[i].split(",")
                    if (dataDay.mapDate.containsKey("${s2[0]}")) dataDay.mapDate.remove(s2[0])  // todo : 삭제 잘 되는지 테스트프로젝트에서 확인해볼것
                    dataDay.mapDate[s2[0]] = ArrayList()
                    for (j in 1 until s2.size) {
                        dataDay.mapDate[s2[0]]?.add(s2[j].toInt())
                    }
                }
            }
            dataDay.mapDate.forEach {
                Log.e("데이터로드", "${it.key}, ${it.value}")
            }
        }
    }

    fun addPlan(content: String, color: Int) {
        var inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // root에 바로 안붙이고(null) addView()로 추가 하니까 데이터 그대로 유지되면서 새로운 view 추가 가능
        var view = inflater.inflate(R.layout.plan_list, null, false)
        binding.containerMenu.addView(view)

        view.findViewById<TextView>(R.id.textviewPlanContent).text = content
        if (color == 1)  view.findViewById<ImageView>(R.id.circleBack).setImageResource(R.drawable.circle_red)
        else if (color == 2)  view.findViewById<ImageView>(R.id.circleBack).setImageResource(R.drawable.circle_yellow)
        else if (color == 3)  view.findViewById<ImageView>(R.id.circleBack).setImageResource(R.drawable.circle_sky)
    }

    override fun onBackPressed() {
        if (binding.drawerView.isDrawerOpen(GravityCompat.START)) {
            binding.drawerView.closeDrawer(GravityCompat.START)
        } else {
            // 알러트
            val builder = AlertDialog.Builder(this)
            builder.setTitle("종료")
                .setMessage("종료 하시겠습니까?")
                .setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->
                    finishAffinity()
                    System.exit(0)
                })
                .setNegativeButton("취소", null)
            builder.show()
        }
    }


    fun setStatusBarTransparent() {
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        if(Build.VERSION.SDK_INT >= 30) {	// API 30 에 적용
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    // 상태바 높이 구하기
    fun statusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else 0
    }

    // 네비게이션 바 높이 구하기
    fun navigationHeight(): Int {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")

        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else 0
    }

    //날짜 화면에 보여주기


    private fun setMonthView() {
        //년월 텍스트뷰 셋팅
        binding.monthYearText.text = monthYearFromDate(CalendarUtil.selectedDate)

        //날짜 생성해서 리스트에 담기
        dayList = dayInMonthArray(CalendarUtil.selectedDate, dataDay)

        //어댑터 초기화
        adapterCalendar = AdapterCalendar(dayList, this, binding.recyclerviewCalendar)

        //레이아웃 설정(열 7개)
        var manager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 7)

        //레이아웃 적용
        binding.recyclerviewCalendar.layoutManager = manager

        //어댑터 적용
        binding.recyclerviewCalendar.adapter = adapterCalendar

//        recyclerView_height = binding.recyclerView.height
//        Log.e("높이_2", "${recyclerView_height}")
    }

    //날짜 타입 설정

    private fun monthYearFromDate(date: LocalDate): String{

        var formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("M월 yyyy")
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        // 받아온 날짜를 해당 포맷으로 변경
        return date.format(formatter)
    }

    //날짜 생성

    private fun dayInMonthArray(date: LocalDate, dataDay: DataDay?): ArrayList<DataComplete?>{
        // date : 현재 날짜가 포함되어 있는 LocalDate
        var dayList = ArrayList<DataComplete?>()
        var yearMonth = YearMonth.from(date)

        //해당 월 마지막 날짜 가져오기(예: 28, 30, 31)Log.e("현재 날짜", "${day.localDate.toString()}, ${CalendarUtil.today.toString()}")   // 2023-10-31, 2023-10-24
        var lastDay = yearMonth.lengthOfMonth()

        //해당 월의 첫 번째 날 가져오기(예: 4월 1일)
        var firstDay = date.withDayOfMonth(1)

        //첫 번째날 요일 가져오기(월:1, 일: 7)
        var dayOfWeek = firstDay.dayOfWeek.value

        var idx = 0
        if (dayOfWeek == 7) idx = 8 // 첫시작날이 일요일일때 첫칸부터 채우도록 idx 8 설정
        else idx = 1

        for(i in idx..41){
            if(i <= dayOfWeek){ // i가 시작 요일 보다 작을때, 빈칸 채움
                dayList.add(null)
            } else if (i > (lastDay + dayOfWeek)) { // 마지막날 넘어갈때
                break
            } else {  // 날짜 추가
                // 해당일과 dataDay key확인해서 동일하면 데이터 추가
                var key = "${date.year}-${date.monthValue}-${i - dayOfWeek}"
                if (dataDay?.mapDate?.containsKey(key) == true) {
                    dayList.add(DataComplete(LocalDate.of(date.year, date.monthValue, i - dayOfWeek), dataDay.mapDate[key]))
                } else {
                    dayList.add(DataComplete(LocalDate.of(date.year, date.monthValue, i - dayOfWeek), null))
                }
            }
        }

        // 해당 줄까지는 데이터를 추가 하여 뷰 위에 줄을 그어준다
        if (dayList.size % 7 != 0) {
            for (i in 0 until  7 - (dayList.size % 7)) {
                dayList.add((null))
            }
        }

        return dayList
    }


    override fun onItemClick(dayText: LocalDate?) {
        Toast.makeText(this, "${dayText?.year}년 ${dayText?.monthValue}월 ${dayText?.dayOfMonth}일", Toast.LENGTH_SHORT).show()
    }



    override fun onCheck(isChecked: Boolean, dataPlan: DataPlan) {
        Log.e("check","${isChecked}, ${dataPlan.color}, ${dataPlan.contentPlan}, ${CalendarUtil.today.toString()}")
//        if (!dataDay.mapDate.containsKey("${CalendarUtil.today.toString()}")) dataDay.mapDate["${CalendarUtil.today.toString()}"] = ArrayList()
//        dayData.mapDate.set("${CalendarUtil.today.toString()}", )

        // sharedPreference에 저장 데이터 저장
        // 확인 버튼 누를시
        if (isChecked) {
//            dataPlan.success = true
//            dataDay.mapDate["${CalendarUtil.today.toString()}"]?.add(dataPlan.color)
        } else {
//            dataPlan.success = false
//            dataDay.mapDate["${CalendarUtil.today.toString()}"]?.remove(dataPlan.color)
        }

        tmpDataPlan.success = isChecked
        tmpDataPlan.color = dataPlan.color


        dataDay.mapDate.forEach {
            Log.e("mapDate","${it.key}, ${it.value}")
        }

//        dataPlanArray.forEach {
//            Log.e("dataPlanArray","${it.contentPlan}, ${it.color}, ${it.success}")
//        }

        // 확인 버튼 누를시
        dataPlanArray.forEach {
            if (it.color == dataPlan.color) it.success = dataPlan.success
        }

//        dataPlanArray.forEach {
//            Log.e("dataPlanArray","${it.contentPlan}, ${it.color}, ${it.success}")
//        }

//        this.dataPlan = DataPlan(dataPlan.contentPlan, dataPlan.color, dataPlan.success)

        // 확인 버튼 누를시
//        dayList = dayInMonthArray(CalendarUtil.selectedDate, dataDay)
//        tmpDayList = dayInMonthArray(CalendarUtil.selectedDate, dataDay)
    }

    var touchPoint = 0f

    override fun onTouchEvent(view: View, event: MotionEvent): Boolean {
//        var touchPoint = 0f

        //0 down
        //1 up
        //2 move
        //3 cancel
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {     // 화면을 처음 터치한 x좌표값 저장
                touchPoint = event.x
            }


            MotionEvent.ACTION_UP -> {
                // 손가락이 화면에서 떨어졌을때 x좌표와의 거리 비교
                // 해당 거리가 100이 되지 않으면 리턴.
                var xx = event.x
                touchPoint = touchPoint - xx
//                Log.e("드래그 업","${xx}, ${Math.abs(touchPoint)}")
//                if (Math.abs(touchPoint) < 100) {
//                    return false
//                }
                if (Math.abs(touchPoint) <= 30 ) {
                    runOnUiThread {
                        Toast.makeText(this, "일", Toast.LENGTH_SHORT).show()
                    }
                } else if (touchPoint > 0) {
                    // 손가락을 우->좌로 움직였을때 오른쪽 화면 생성
                    Toast.makeText(this, "우",Toast.LENGTH_SHORT).show()
                    CalendarUtil.selectedDate = CalendarUtil.selectedDate.plusMonths(1)
                    runOnUiThread {
                        setMonthView()
                    }
                } else {
                    // 손가락을 좌->우로 움직였을때 왼쪽 화면 생성
                    Toast.makeText(this, "좌",Toast.LENGTH_SHORT).show()
                    CalendarUtil.selectedDate = CalendarUtil.selectedDate.minusMonths(1)
                    runOnUiThread {
                        setMonthView()
                    }
                }
            }
        }
        return true
    }


    override fun onDrag(view: View, event: DragEvent, dayText: String): Boolean {
//        var touchPoint = 0f
        Log.e("드래그","${event.action}")
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {     // 화면을 처음 터치한 x좌표값 저장
                touchPoint = event.x
//                Log.e("드래그 다운","${touchPoint}")
            }

            DragEvent.ACTION_DROP -> {
                // 손가락이 화면에서 떨어졌을때 x좌표와의 거리 비교
                // 해당 거리가 100이 되지 않으면 리턴.
                var xx = event.x
//                Log.e("드래그 업","${xx}, ${Math.abs(touchPoint)}")
                touchPoint = touchPoint - xx
//                Log.e("드래그 업","${xx}, ${Math.abs(touchPoint)}")
//                if (Math.abs(touchPoint) < 100) {
//                    return false
//                }
                if (Math.abs(touchPoint) <= 16 ) {
                    runOnUiThread {
                        Toast.makeText(this, dayText, Toast.LENGTH_SHORT).show()
                    }
                } else if (touchPoint > 0) {
                    // 손가락을 우->좌로 움직였을때 오른쪽 화면 생성
                    Toast.makeText(this, "우",Toast.LENGTH_SHORT).show()
                    CalendarUtil.selectedDate = CalendarUtil.selectedDate.plusMonths(1)
                    runOnUiThread {
                        setMonthView()
                    }
                } else {
                    // 손가락을 좌->우로 움직였을때 왼쪽 화면 생성
                    Toast.makeText(this, "좌",Toast.LENGTH_SHORT).show()
                    CalendarUtil.selectedDate = CalendarUtil.selectedDate.minusMonths(1)
                    runOnUiThread {
                        setMonthView()
                    }
                }
            }
        }
        return true
    }

}
