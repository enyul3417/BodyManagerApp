package com.example.bodymanagerapp.menu.Settings.Notification

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.MyDBHelper
import com.example.bodymanagerapp.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class GoalRecyclerViewAdapter(var data : ArrayList<NotificationData>, val context: Context,
                              var item : RecyclerView, var itemClick:(NotificationData)->Unit)
    : RecyclerView.Adapter<GoalRecyclerViewAdapter.ItemViewHolder>() {
    var myDBHelper: MyDBHelper = MyDBHelper(context)
    lateinit var sqldb: SQLiteDatabase

    var pos : Int = -1
    var id : Int = -1

    lateinit var delete : MenuItem

    inner class ItemViewHolder(view: View, itemClick: (NotificationData) -> Unit) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnLongClickListener {
                pos = layoutPosition
                id = data[pos].id
                return@setOnLongClickListener false
            }
            view.setOnCreateContextMenuListener { menu, v, menuInfo ->
                delete = menu.add("삭제")

                delete.setOnMenuItemClickListener {
                    sqldb = myDBHelper.writableDatabase
                    sqldb.execSQL("DELETE FROM goal_table WHERE gId = $id;")
                    sqldb.close()

                    val intent = Intent(context, NotificationActivity::class.java)
                    context.startActivity(intent)
                    Activity().finish()

                    return@setOnMenuItemClickListener true
                }
            }
        }
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
            var format = SimpleDateFormat("yyyyMMdd")
            var date1 = format.parse(date.toString())
            var date2 = format.parse(today.toString())
            dDayTV.text = "D${((date2.time - date1.time) / (60 * 60 * 24 * 1000)).toInt() + 1}"
            check.isChecked = data.isChecked
            itemView.setOnClickListener {
                itemClick(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.goal_items, parent, false)
        return ItemViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

}