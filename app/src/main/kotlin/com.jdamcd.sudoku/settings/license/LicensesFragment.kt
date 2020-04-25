package com.jdamcd.sudoku.settings.license

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.jdamcd.sudoku.R

class LicensesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.licenses)
    }
}
