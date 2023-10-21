package com.jdh.calendar_kt

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jdh.calendar_kt.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity(), OnItemListener {
    companion object {
        var recyclerView_height = 0
        lateinit var container: LinearLayout
    }

    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!
    lateinit var selectedDate: LocalDate
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 상태바 투명화
        setStatusBarTransparent()

        binding.activityCustomCalendar.setPadding(
            0,
            statusBarHeight(),
            0,
            navigationHeight()
        )

        container = LinearLayout(this)

        binding.containerHeight.getViewTreeObserver()
            .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val height: Int = binding.containerHeight.getHeight()
                    recyclerView_height = height
                    binding.containerHeight.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                    setMonthView()
                }
            })

        binding.preBtn.setOnClickListener {
            CalendarUtil.selectedDate = CalendarUtil.selectedDate.minusMonths(1)
            runOnUiThread {
                setMonthView()
            }
        }

        binding.nextBtn.setOnClickListener {
            CalendarUtil.selectedDate = CalendarUtil.selectedDate.plusMonths(1)
            runOnUiThread {
                setMonthView()
            }
        }

        // containerHeight에 리스너 추가 하면 동작 안함. containerHeight가 recyclerview 아래에 있어서 그런가?

//        binding.containerHeight.setOnClickListener {
//            Log.e("클릭_con","진입")
//        }

//        recyclerView_height = binding.recyclerView.height
//        recyclerView_height = binding.containerHeight.height
//        Log.e("높이_1", "${recyclerView_height}")

//        selectedDate = LocalDate.now()
        CalendarUtil.selectedDate = LocalDate.now()
        CalendarUtil.today = LocalDate.now()
//        setMonthView()


        // todo  recyclerview 어댑터의 클릭이벤트와 함께 쓸때 Action_down모션 이벤트가 씹힘, 위 아래로 드래그 하면 모션 캔슬 나면서 모션 up이 작동을 못함
        // 일단은 버튼으로 좌우로 움직이도록 하고 나중에 드래그 이동추가 할것.


        // 액션다운이 반응을 안함
        // 이거쓰면 왼쪽으로밖에 안감
//        var touchPoint = 0f
//        binding.recyclerView.setOnTouchListener(OnTouchListener { v, event ->
////            val transaction: FragmentTransaction =
////                getActivity().getSupportFragmentManager().beginTransaction()
////            val ff: Fragment
//            Log.e("드래그 다운","${event.action}")
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {     // 화면을 처음 터치한 x좌표값 저장
//                    touchPoint = event.x
////                    Log.e("드래그 다운","${touchPoint}")
//                }
//
//
//                MotionEvent.ACTION_UP -> {
//                    // 손가락이 화면에서 떨어졌을때 x좌표와의 거리 비교
//                    // 해당 거리가 100이 되지 않으면 리턴.
//                    var xx = event.x
//                    touchPoint = touchPoint - xx
////                    Log.e("드래그 업","${xx}, ${Math.abs(touchPoint)}")
//                    if (Math.abs(touchPoint) < 100) {
//                        return@OnTouchListener false
//                    }
//                    if (touchPoint > 0) {
//                        // 손가락을 우->좌로 움직였을때 오른쪽 화면 생성
//                        Toast.makeText(this, "우",Toast.LENGTH_SHORT).show()
//                        CalendarUtil.selectedDate = CalendarUtil.selectedDate.plusMonths(1)
//                        setMonthView()
//                    } else {
//                        // 손가락을 좌->우로 움직였을때 왼쪽 화면 생성
//                        Toast.makeText(this, "좌",Toast.LENGTH_SHORT).show()
//                        CalendarUtil.selectedDate = CalendarUtil.selectedDate.minusMonths(1)
//                        setMonthView()
//                    }
//
//                    // 손가락을 왼쪽으로 움직였으면 오른쪽 화면이 나타나야 한다.
////                    transaction.replace(R.id.fragment_container, ff)
////                    transaction.commit()
//                }
//            }
//            true
//        })

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
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView() {
        //년월 텍스트뷰 셋팅
        binding.monthYearText.text = monthYearFromDate(CalendarUtil.selectedDate)

        //날짜 생성해서 리스트에 담기
        val dayList = dayInMonthArray(CalendarUtil.selectedDate)

        //어댑터 초기화
        val adapter = CalendarAdapter(dayList, this, binding.recyclerView)

        //레이아웃 설정(열 7개)
        var manager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 7)

        //레이아웃 적용
        binding.recyclerView.layoutManager = manager

        //어댑터 적용
        binding.recyclerView.adapter = adapter
//        recyclerView_height = binding.recyclerView.height
//        Log.e("높이_2", "${recyclerView_height}")
    }

    //날짜 타입 설정
    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthYearFromDate(date: LocalDate): String{

        var formatter = DateTimeFormatter.ofPattern("M월 yyyy")

        // 받아온 날짜를 해당 포맷으로 변경
        return date.format(formatter)
    }

    //날짜 생성
    @RequiresApi(Build.VERSION_CODES.O)
    private fun dayInMonthArray(date: LocalDate): ArrayList<LocalDate?>{

        var dayList = ArrayList<LocalDate?>()

        var yearMonth = YearMonth.from(date)

        //해당 월 마지막 날짜 가져오기(예: 28, 30, 31)
        var lastDay = yearMonth.lengthOfMonth()

        //해당 월의 첫 번째 날 가져오기(예: 4월 1일)
        var firstDay = CalendarUtil.selectedDate.withDayOfMonth(1)

        //첫 번째날 요일 가져오기(월:1, 일: 7)
        var dayOfWeek = firstDay.dayOfWeek.value

        var idx = 0
        if (dayOfWeek == 7) idx = 8 // 첫시작날이 일요일일때 첫칸부터 채우도록 idx 8 설정
        else idx = 1
        for(i in idx..41){
            if(i <= dayOfWeek){
                dayList.add(null)
            } else if (i > (lastDay + dayOfWeek)) {
                break
            } else {  // 날짜 추가
                dayList.add(LocalDate.of(CalendarUtil.selectedDate.year,
                    CalendarUtil.selectedDate.monthValue, i - dayOfWeek))
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(dayText: LocalDate?) {
        Toast.makeText(this, "${dayText?.year}년 ${dayText?.monthValue}월 ${dayText?.dayOfMonth}일", Toast.LENGTH_SHORT).show()
    }

    var touchPoint = 0f
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTouchEvent(view: View, event: MotionEvent): Boolean {
//        var touchPoint = 0f

        //0 down
        //1 up
        //2 move
        //3 cancel
        Log.e("드래그","${event.action}")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {     // 화면을 처음 터치한 x좌표값 저장
                touchPoint = event.x
                Log.e("드래그클릭 다운","${touchPoint}")
            }


            MotionEvent.ACTION_UP -> {
                // 손가락이 화면에서 떨어졌을때 x좌표와의 거리 비교
                // 해당 거리가 100이 되지 않으면 리턴.
                var xx = event.x
                Log.e("드래그클릭 업","${xx}, ${Math.abs(touchPoint)}")
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

    @RequiresApi(Build.VERSION_CODES.O)
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