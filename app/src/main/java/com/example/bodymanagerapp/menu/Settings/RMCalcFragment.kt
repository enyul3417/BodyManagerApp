package com.example.bodymanagerapp.menu.Settings

import android.os.Bundle
import androidx.preference.PreferenceFragment
import androidx.fragment.app.Fragment
import com.example.bodymanagerapp.R

/**
 * A simple [Fragment] subclass.
 * Use the [RMCalcFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RMCalcFragment : PreferenceFragment() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_r_m_calc, rootKey)
    }

    companion object {
        val fagment = RMCalcFragment()
    }
}