package com.jdh.calendar_kt

import android.annotation.SuppressLint
import android.os.Build
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

class AdapterPlan(private val planList: ArrayList<Plan>,
                  private val recyclerView: RecyclerView):
    RecyclerView.Adapter<AdapterPlan.ItemViewHolder>() {


    inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val circleMain: ImageView = itemView.findViewById(R.id.circleMain)
        val textviewPlanContent: TextView = itemView.findViewById(R.id.textviewPlanContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.plan_list, parent, false)

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


//        holder.dayText.setBackgroundResource(R.drawable.today_background)
    }

    override fun getItemCount(): Int {
        return planList.size
    }
}