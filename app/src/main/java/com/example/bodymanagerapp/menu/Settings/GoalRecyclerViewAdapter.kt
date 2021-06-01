package com.example.bodymanagerapp.menu.Settings

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.menu.Diet.DietData
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class GoalRecyclerViewAdapter(var data : ArrayList<NotificationData>, val context: Context,
                              var item : RecyclerView/*, var itemClick:(NotificationData, Int)->Unit*/)
    : RecyclerView.Adapter<GoalRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View/*, itemClick: (DietData, Int) -> Unit*/) : RecyclerView.ViewHolder(view) {
        var goalTV : TextView = view.findViewById(R.id.tv_gi_goal)
        var dDayTV : TextView = view.findViewById(R.id.tv_gi_days)
        var dateTV : TextView = view.findViewById(R.id.tv_gi_date)
        var check : CheckBox = view.findViewById(R.id.cb_gi)

        @RequiresApi(Build.VERSION_CODES.O)
        var now = LocalDate.now()
        @RequiresApi(Build.VERSION_CODES.O)
        var today = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        //onBindViewHolder에서 호출할 bind 함수
        fun bind(data: NotificationData, position: Int) {
            goalTV.text = data.string
            val date = data.int
            val year = date / 10000
            val month = date % 10000 / 100
            val day = date % 10000 % 100
            dateTV.text = "${year}년 ${month}월 ${day}일까지"
            dDayTV.text = "D-${date - today.toInt()}"
            check.isChecked = data.isChecked

            /*itemView.setOnClickListener {
                itemClick(data, position)
            }*/
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalRecyclerViewAdapter.ItemViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.goal_items, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalRecyclerViewAdapter.ItemViewHolder, position: Int) {
        holder.bind(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

}