package com.jdh.calendar_kt

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterComplete (private val dataPlanList: ArrayList<DataPlan>,
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
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        var plan = dataPlanList[holder.adapterPosition]

        if (plan.color == 1) holder.circleMain.setImageResource(R.drawable.circle_red)
        else if (plan.color == 2) holder.circleMain.setImageResource(R.drawable.circle_yellow)
        else if (plan.color == 3) holder.circleMain.setImageResource(R.drawable.circle_sky)

        holder.textviewPlanContent.text = plan.contentPlan

        holder.checkboxComplete.isChecked = plan.success

//        if (plan.success) {
//            onItemListener.onCheck(true, plan)
//        }

        holder.checkboxComplete.setOnCheckedChangeListener { buttonView, isChecked ->
            onItemListener.onCheck(isChecked, plan)
        }



//        holder.dayText.setBackgroundResource(R.drawable.today_background)
    }

    override fun getItemCount(): Int {
        return dataPlanList.size
    }

}