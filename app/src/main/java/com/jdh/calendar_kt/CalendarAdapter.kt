package com.jdh.calendar_kt

import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(private val dayList: ArrayList<String>,
                      private val onItemListener: OnItemListener):
    RecyclerView.Adapter<CalendarAdapter.ItemViewHolder>() {


    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val dayText: TextView = itemView.findViewById(R.id.dayText)

//        itemView.setOnTouchListener { view, event ->
//            onItemListener.onTouchEvent(view, event, day)
//            true
//        }
    }

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_cell, parent, false)

        return ItemViewHolder(view)
    }

    //데이터 설정
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        var day = dayList[holder.adapterPosition]

        holder.dayText.text = day

        //텍스트 색상 지정(토,일)
        if((position +1) % 7 == 0){ //토요일은 파랑
            holder.dayText.setTextColor(Color.BLUE)

        }else if( position == 0 || position % 7 == 0){ //일요일은 빨강
            holder.dayText.setTextColor(Color.RED)
        }

        //날짜 클릭, 드래그 이벤트(터치 x좌표이동이 별로 없다면 클릭으로 인식)
        holder.itemView.setOnClickListener {
            //인터페이스를 통해 날짜를 넘겨준다.
            onItemListener.onItemClick(day)
        }

//        holder.itemView.setOnTouchListener { view, event ->
//            onItemListener.onTouchEvent(view, event, day)
//        }

//        holder.itemView.setOnDragListener { view, dragEvent ->
//            Log.e("드래그시작","시작")
//            onItemListener.onDrag(view, dragEvent, day)
//        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }
}