package com.jdh.calendar_kt

import android.R
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.view.WindowManager
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


class MainActivity : AppCompatActivity() {

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

        selectedDate  = LocalDate.now()
        setMonthView()

        var touchPoint = 0f
        binding.recyclerView.setOnTouchListener(OnTouchListener { v, event ->
//            val transaction: FragmentTransaction =
//                getActivity().getSupportFragmentManager().beginTransaction()
//            val ff: Fragment
            when (event.action) {
                MotionEvent.ACTION_DOWN ->                 // 화면을 처음 터치한 x좌표값 저장
                    touchPoint = event.x

                MotionEvent.ACTION_UP -> {
                    // 손가락이 화면에서 떨어졌을때 x좌표와의 거리 비교
                    // 해당 거리가 100이 되지 않으면 리턴.
                    touchPoint = touchPoint - event.x
                    if (Math.abs(touchPoint) < 100) {
                        return@OnTouchListener false
                    }
                    if (touchPoint > 0) {
                        // 손가락을 우->좌로 움직였을때 오른쪽 화면 생성
                        Toast.makeText(this, "우",Toast.LENGTH_SHORT).show()
                        selectedDate = selectedDate.plusMonths(1)
                        setMonthView()
                    } else {
                        // 손가락을 좌->우로 움직였을때 왼쪽 화면 생성
                        Toast.makeText(this, "좌",Toast.LENGTH_SHORT).show()
                        selectedDate = selectedDate.minusMonths(1)
                        setMonthView()
                    }

                    // 손가락을 왼쪽으로 움직였으면 오른쪽 화면이 나타나야 한다.
//                    transaction.replace(R.id.fragment_container, ff)
//                    transaction.commit()
                }
            }
            true
        })

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
        binding.monthYearText.text = monthYearFromDate(selectedDate)

        //날짜 생성해서 리스트에 담기
        val dayList = dayInMonthArray(selectedDate)

        //어댑터 초기화
        val adapter = CalendarAdapter(dayList)

        //레이아웃 설정(열 7개)
        var manager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 7)

        //레이아웃 적용
        binding.recyclerView.layoutManager = manager

        //어댑터 적용
        binding.recyclerView.adapter = adapter
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
    private fun dayInMonthArray(date: LocalDate): ArrayList<String>{

        var dayList = ArrayList<String>()

        var yearMonth = YearMonth.from(date)

        //해당 월 마지막 날짜 가져오기(예: 28, 30, 31)
        var lastDay = yearMonth.lengthOfMonth()

        //해당 월의 첫 번째 날 가져오기(예: 4월 1일)
        var firstDay = selectedDate.withDayOfMonth(1)

        //첫 번째날 요일 가져오기(월:1, 일: 7)
        var dayOfWeek = firstDay.dayOfWeek.value

        var idx = 0
        if (dayOfWeek == 7) idx = 8 // 1일이 일요일일때 첫칸부터 채우도록 idx 8 설정
        else idx = 1
        for(i in idx..41){
            if(i <= dayOfWeek || i > (lastDay + dayOfWeek)){
                dayList.add("")
            }else{  // 날짜 추가
                dayList.add((i - dayOfWeek).toString())
            }
        }


        return dayList
    }

}