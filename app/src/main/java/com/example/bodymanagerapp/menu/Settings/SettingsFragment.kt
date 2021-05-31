package com.example.bodymanagerapp.menu.Settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.*
import com.example.bodymanagerapp.R
import kotlin.concurrent.fixedRateTimer

class SettingsFragment : PreferenceFragmentCompat() {
    lateinit var prefs : SharedPreferences

    lateinit var rmCalc : Preference
    lateinit var setGoal : Preference
    lateinit var alarmSwitch : SwitchPreference
    lateinit var themList : ListPreference
    lateinit var fontList : ListPreference
    lateinit var fontSize : ListPreference
    lateinit var backup : Preference
    lateinit var load : Preference

    var prefListener : SharedPreferences.OnSharedPreferenceChangeListener ?= null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        addPreferencesFromResource(R.xml.settings_preferences)
        rmCalc = findPreference("1rm_calc")!!
        setGoal = findPreference("set_goal")!!
        alarmSwitch = findPreference("alarm")!!
        themList = findPreference("theme_selection")!!
        fontList = findPreference("font_selection")!!
        fontSize = findPreference("font_size")!!
        backup = findPreference("backup")!!
        load = findPreference("load")!!

        prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        rmCalc.onPreferenceClickListener

        if(!prefs.getString("theme_selection", "").equals("")) {
            themList.summary = prefs.getString("theme_selection", "블랙")
        }

        if(!prefs.getString("font_selection", "").equals("")) {
            themList.summary = prefs.getString("font_selection", "폰트1")
        }

        if(!prefs.getString("font_size", "").equals("")) {
            themList.summary = prefs.getString("font_size", "보통")
        }

        if (prefs.getBoolean("alarm", true)) {
            alarmSwitch.summary = "허용"
        }

        prefs.registerOnSharedPreferenceChangeListener(prefListener)
        prefListener = 
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (themList.key.equals("블랙")) {
                    themList.summary = prefs.getString("theme_selection", "블랙")
                } else if (themList.key.equals("화이트")) {
                    themList.summary = prefs.getString("theme_selection", "화이트")
                }
            }
    }
    
}