package com.jdh.calendar_kt

import android.R
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.jdh.calendar_kt.databinding.ActivityMainBinding
import java.time.LocalDate
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
        binding.activityCustomCalendar.setOnTouchListener(OnTouchListener { v, event ->
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
                        selectedDate = selectedDate.plusMonths(1)
                        setMonthView()
                    } else {
                        // 손가락을 좌->우로 움직였을때 왼쪽 화면 생성
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
    }

    //날짜 타입 설정
    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthYearFromDate(date: LocalDate): String{

        var formatter = DateTimeFormatter.ofPattern("M월 yyyy")

        // 받아온 날짜를 해당 포맷으로 변경
        return date.format(formatter)
    }


}