package com.jdh.calendar_kt

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

class AdapterComplete (private val planList: ArrayList<Plan>,
                       private val onItemListener: OnItemListener):
    RecyclerView.Adapter<AdapterComplete.ItemViewHolder>() {



    inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val checkboxComplete: CheckBox = itemView.findViewById(R.id.checkboxComplete)
        val circleMain: ImageView = itemView.findViewById(R.id.circleMain)
        val textviewPlanContent: TextView = itemView.findViewById(R.id.textviewPlanContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.complete_cell, parent, false)

        return ItemViewHolder(view)
    }

    //데이터 설정
    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        var plan = planList[holder.adapterPosition]

        if (plan.color == 1) holder.circleMain.setImageResource(R.drawable.circle_red)
        else if (plan.color == 2) holder.circleMain.setImageResource(R.drawable.circle_yellow)
        else if (plan.color == 3) holder.circleMain.setImageResource(R.drawable.circle_sky)

        holder.textviewPlanContent.text = plan.contentPlan

        holder.checkboxComplete.setOnClickListener {
            // todo mainActivity에서 구현해놓은 onCheck 사용해서 체크 되면 DayData를 추가 하여 LocalDate(오늘 날짜 CalendarUtil.today)도 추가 할것
            if (holder.checkboxComplete.isChecked) {
                // 달력에 원 표시
            } else {
                // 달력에 표시된 원 제거
            }
        }



//        holder.dayText.setBackgroundResource(R.drawable.today_background)
    }

    override fun getItemCount(): Int {
        return planList.size
    }

}