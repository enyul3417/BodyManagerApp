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
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.MyDBHelper
import com.example.bodymanagerapp.R

class TimeRecyclerViewAdapter(var data : ArrayList<NotificationData>, val context: Context,
                              var item : RecyclerView, var itemClick:(NotificationData)->Unit)
    : RecyclerView.Adapter<TimeRecyclerViewAdapter.ItemViewHolder>() {
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
                    sqldb.execSQL("DELETE FROM time_table WHERE tId = $id;")
                    sqldb.close()

                    val intent = Intent(context, NotificationActivity::class.java)
                    context.startActivity(intent)
                    Activity().finish()

                    return@setOnMenuItemClickListener true
                }
            }
        }
        var timeTV : TextView = view.findViewById(R.id.tv_time_alarm)
        var daysTV : TextView = view.findViewById(R.id.tv_time_days)
        var memoTV : TextView = view.findViewById(R.id.tv_time_memo)
        var switch : Switch = view.findViewById(R.id.switch_time)

        //onBindViewHolder에서 호출할 bind 함수
        fun bind(data: NotificationData, position: Int) {
            val time = data.int
            val hour = time / 60
            val min = time % 60
            timeTV.text = "${hour}:${min}"
            daysTV.text = data.days
            memoTV.text = data.string
            switch.isChecked = data.isChecked

            itemView.setOnClickListener {
                itemClick(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.time_items, parent, false)
        return ItemViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

}