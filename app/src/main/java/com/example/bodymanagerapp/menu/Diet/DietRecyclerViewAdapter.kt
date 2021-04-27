package com.example.bodymanagerapp.menu.Diet

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.bodymanagerapp.R
import com.example.bodymanagerapp.myDBHelper

class DietRecyclerViewAdapter(var data : ArrayList<DietData>, val context : Context, var item : RecyclerView,
                                  var itemClick : (DietData, Int)->Unit):
    RecyclerView.Adapter<DietRecyclerViewAdapter.ItemViewHolder>() {

    var myDBHelper: myDBHelper = myDBHelper(context)
    lateinit var sqldb: SQLiteDatabase

    var pos : Int = -1
    var id : Int = 0

    lateinit var delete : MenuItem
    lateinit var update : MenuItem

    //뷰홀더 클래스 내부 클래스로 선언
    inner class ItemViewHolder(view: View, itemClick: (DietData, Int) -> Unit) : RecyclerView.ViewHolder(view) {

        init{
            // 길게 클릭 시
            view.setOnLongClickListener {
                pos = layoutPosition
                id = data[pos].id
                return@setOnLongClickListener false
            }
            view.setOnCreateContextMenuListener{ menu, v, menuinfo->
                delete = menu.add("삭제")
                update = menu.add("수정")

                delete.setOnMenuItemClickListener {
                    sqldb = myDBHelper.writableDatabase
                    sqldb.execSQL("DELETE FROM diet_record WHERE DId = $id;")
                    sqldb.close()

                    return@setOnMenuItemClickListener true
                }

                update.setOnMenuItemClickListener {
                    var intent = Intent(context, NewDietActivity::class.java)
                    intent.putExtra("ID", id)
                    context.startActivity(intent)
                    return@setOnMenuItemClickListener true

                }
            }

        }

        var time = view.findViewById<TextView>(R.id.text_diet_time_item)
        var image = view.findViewById<ImageView>(R.id.image_diet_item)
        var memo = view.findViewById<TextView>(R.id.diet_memo_item)

        //onBindViewHolder에서 호출할 bind 함수
        fun bind(data: DietData, num: Int) {
            time.text = data.time
            memo.text = data.memo

            if (data.image != null){ // 등록된 이미지가 있다면
                image.setImageBitmap(data.image)

            } else { // 등록된 이미지가 없다면
                image.visibility = View.GONE
            }

            itemView.setOnClickListener {
                itemClick(data, num)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.diet_items, parent, false)
        return ItemViewHolder(view,itemClick)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(data[position], position)
    }
    override fun getItemCount(): Int {
        return data.size
    }

}
