package com.jdamcd.sudokusolver.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.jdamcd.sudokusolver.R

class AboutFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.about)
        findPreference<Preference>(KEY_PLAY)?.onPreferenceClickListener = this
        findPreference<Preference>(KEY_RATE)?.onPreferenceClickListener = this
        findPreference<Preference>(KEY_VERSION)?.summary = getAppVersion()
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        if (preference.key == KEY_PLAY) {
            openLink("market://details?id=com.jdamcd.sudoku")
            return true
        } else if (preference.key == KEY_RATE) {
            openLink("market://details?id=com.jdamcd.sudokusolver")
            return true
        }
        return false
    }

    private fun openLink(link: String) {
        try {
            startActivity(Intent(ACTION_VIEW, Uri.parse(link)))
        } catch (e: ActivityNotFoundException) {}
    }

    private fun getAppVersion(): String {
        val thisContext = requireContext().applicationContext
        return try {
            thisContext.packageManager.getPackageInfo(thisContext.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }

    companion object {
        private const val KEY_PLAY = "about_play"
        private const val KEY_RATE = "about_rate"
        private const val KEY_VERSION = "about_version"
    }
}
