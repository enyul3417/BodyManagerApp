package com.example.bodymanagerapp.Preference

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class PreferenceUtil(context: Context) {
    private val prefs : SharedPreferences =
            context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun getInt(key : String, defValue : Int) : Int {
        return prefs.getInt(key, defValue)
    }

    fun setInt(key : String, i : Int) {
        prefs.edit().putInt(key, i).apply()
    }

    fun getLong(key: String, defValue: Long) : Long {
        return prefs.getLong(key, defValue)
    }

    fun setLong(key: String, l : Long) {
        prefs.edit().putLong(key, l).apply()
    }
}