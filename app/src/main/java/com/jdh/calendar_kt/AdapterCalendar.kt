package com.jdh.calendar_kt

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class AdapterCalendar(private var dayList: ArrayList<DataComplete?>,
                      private val onItemListener: OnItemListener,
                      private val recyclerView: RecyclerView):
    RecyclerView.Adapter<AdapterCalendar.ItemViewHolder>() {


    inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val container: LinearLayout = itemView.findViewById(R.id.container)
        val dayText: TextView = itemView.findViewById(R.id.dayText)
        val circle1: ImageView = itemView.findViewById(R.id.circle1)
        val circle2: ImageView = itemView.findViewById(R.id.circle2)
        val circle3: ImageView = itemView.findViewById(R.id.circle3)


        init {
//            recyclerView.setOnTouchListener { view, motionEvent ->
//                Log.e("터치_item","진입")
//                onItemListener.onTouchEvent(view, motionEvent)
//            }
        }
    }

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_cell, parent, false)

        return ItemViewHolder(view)
    }

    //데이터 설정
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        var day = dayList[holder.adapterPosition]

        if (dayList.size <= 35) {
            holder.container.layoutParams.height = MainActivity.recyclerView_height/5
        } else {    // > 35
            holder.container.layoutParams.height = MainActivity.recyclerView_height/6
        }


        //텍스트 색상 지정(토,일)
        if((position +1) % 7 == 0){ //토요일은 파랑
            holder.dayText.setTextColor(Color.BLUE)

        }else if( position == 0 || position % 7 == 0){ //일요일은 빨강
            holder.dayText.setTextColor(Color.RED)
        }

        if(day == null){
            holder.dayText.text = ""
        }else{
            //해당 일자를 넣는다.
            holder.dayText.text = day.localDate.dayOfMonth.toString()

            //현재 날짜 색상 칠하기
//            Log.e("현재 날짜", "${day.toString()}, ${CalendarUtil.today.toString()}")   // 2023-10-31, 2023-10-24
            if(day.localDate == CalendarUtil.today){
                holder.dayText.setBackgroundResource(R.drawable.today_background)
                holder.dayText.setPadding(10,0,10,0)
                holder.dayText.setTextColor(Color.WHITE)
            }
        }

        if (day != null) {
            if (day.arrDataPlan?.isNotEmpty() == true) {
                Log.e("원","${day.arrDataPlan!!.size}")
                for (i in 0 until day.arrDataPlan!!.size) {
                    var imageView = holder.itemView.findViewById<ImageView>(R.id.circle1 + i)
                    if (day.arrDataPlan!![i].color == 1) {
                        imageView.visibility = View.VISIBLE
                        imageView.setImageResource(R.drawable.circle_red)
                    }
                    else if (day.arrDataPlan!![i].color == 2) {
                        imageView.visibility = View.VISIBLE
                        imageView.setImageResource(R.drawable.circle_yellow)
                    }
                    else if (day.arrDataPlan!![i].color == 3) {
                        imageView.visibility = View.VISIBLE
                        imageView.setImageResource(R.drawable.circle_sky)
                    }
                }
            }
        }

        //날짜 클릭, 드래그 이벤트(터치 x좌표이동이 별로 없다면 클릭으로 인식)
        // todo : recyclerview에 터치 리스너랑 itemview에 클릭리스너 동시에 걸면 터치리스너가 좌밖에 이동을 안한다.
        holder.container.setOnClickListener {
            //인터페이스를 통해 날짜를 넘겨준다.
//            onItemListener.onItemClick(day)
//            Log.e("클릭_item","${day?.dayOfMonth}")
            if (MainActivity.container != null) MainActivity.container.setBackgroundResource(R.drawable.unselected_background)
            MainActivity.container = holder.container
            holder.container.setBackgroundResource(R.drawable.selected_background)

        }

//        holder.container.setOnTouchListener { view, event ->
//            onItemListener.onTouchEvent(view, event)
//        }

//        holder.itemView.setOnDragListener { view, dragEvent ->
//            Log.e("드래그시작","시작")
//            onItemListener.onDrag(view, dragEvent, day)
//        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

    fun setData(dayList: ArrayList<DataComplete?>) {
        this.dayList = dayList
    }
}