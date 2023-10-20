package com.jdh.calendar_kt

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class CalendarAdapter(private val dayList: ArrayList<LocalDate?>,
                      private val onItemListener: OnItemListener):
    RecyclerView.Adapter<CalendarAdapter.ItemViewHolder>() {


    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val dayText: TextView = itemView.findViewById(R.id.dayText)
        val container: LinearLayout = itemView.findViewById(R.id.container)
    }

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_cell, parent, false)

        return ItemViewHolder(view)
    }

    //데이터 설정
    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
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
            holder.dayText.text = day.dayOfMonth.toString()

            //현재 날짜 색상 칠하기
            if(day == CalendarUtil.today){
                holder.dayText.setBackgroundResource(R.drawable.today_background)
                holder.dayText.setPadding(10,0,10,0)
                holder.dayText.setTextColor(Color.WHITE)
            }
        }

        //날짜 클릭, 드래그 이벤트(터치 x좌표이동이 별로 없다면 클릭으로 인식)
//        holder.itemView.setOnClickListener {
//            //인터페이스를 통해 날짜를 넘겨준다.
//            onItemListener.onItemClick(day)
//        }

        holder.itemView.setOnTouchListener { view, event ->
            onItemListener.onTouchEvent(view, event, day)
        }

//        holder.itemView.setOnDragListener { view, dragEvent ->
//            Log.e("드래그시작","시작")
//            onItemListener.onDrag(view, dragEvent, day)
//        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }
}