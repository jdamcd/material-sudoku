package com.jdamcd.sudoku.settings

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.Preference.OnPreferenceClickListener
import com.jdamcd.sudoku.IntentFactory
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.base.BasePreferenceFragment
import com.jdamcd.sudoku.util.AppInfo
import com.jdamcd.sudoku.util.snackbar
import javax.inject.Inject

class SettingsFragment : BasePreferenceFragment(), OnPreferenceClickListener, OnPreferenceChangeListener {

    @Inject internal lateinit var info: AppInfo
    @Inject internal lateinit var intents: IntentFactory

    private val handler = Handler()

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
        findPreference<Preference>(KEY_VERSION)?.summary = info.getVersion()
        setListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }

    private fun setListeners() {
        findPreference<Preference>(KEY_HOW_TO_PLAY)?.onPreferenceClickListener = this
        findPreference<Preference>(KEY_CHEATS)?.onPreferenceChangeListener = this
        findPreference<Preference>(KEY_TIMER)?.onPreferenceChangeListener = this
        findPreference<Preference>(KEY_SHOW_ERRORS)?.onPreferenceChangeListener = this
        findPreference<Preference>(KEY_DARK_MODE)?.onPreferenceChangeListener = this
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.activity_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            android.R.id.home -> {
                startActivity(intents.getPuzzleChoice())
                return true
            }
            R.id.action_licenses -> {
                startActivity(intents.getLicenses())
                return true
            }
            R.id.action_rate -> {
                launchPlayStore()
                return true
            }
        }
        return false
    }

    private fun launchPlayStore() {
        try {
            startActivity(intents.getRateApp())
        } catch (e: ActivityNotFoundException) {
            view?.snackbar(R.string.toast_missing_play)
        }
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        startActivity(intents.getHowToPlay())
        return true
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        when (preference.key) {
            KEY_DARK_MODE -> toggleDarkMode(newValue as Boolean)
        }
        return true
    }

    private fun toggleDarkMode(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )
    }

    companion object {
        const val KEY_TIMER = "settings_timer"
        const val KEY_CHEATS = "settings_cheat"
        const val KEY_SHOW_ERRORS = "settings_show_errors"
        const val KEY_DARK_MODE = "settings_dark_mode"
        const val KEY_HOW_TO_PLAY = "settings_how_to_play"
        const val KEY_VERSION = "settings_version"
    }
}
