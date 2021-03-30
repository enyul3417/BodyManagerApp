package com.example.bodymanagerapp.menu

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.bodymanagerapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
    }
}