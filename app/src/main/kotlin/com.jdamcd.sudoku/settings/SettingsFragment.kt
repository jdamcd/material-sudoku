package com.jdamcd.sudoku.settings

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.Preference.OnPreferenceClickListener
import androidx.preference.PreferenceFragmentCompat
import com.jdamcd.sudoku.IntentFactory
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.settings.user.Settings
import com.jdamcd.sudoku.util.AppInfo
import com.jdamcd.sudoku.util.snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(), OnPreferenceChangeListener {

    @Inject internal lateinit var info: AppInfo
    @Inject internal lateinit var intents: IntentFactory
    @Inject internal lateinit var settings: Settings

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
        findPreference<Preference>(KEY_VERSION)?.summary = info.getVersion()
        findPreference<Preference>(KEY_NIGHT_MODE)?.isVisible = !settings.isSystemTheme
        setListeners()
    }

    private fun setListeners() {
        listOf(KEY_CHEATS, KEY_TIMER, KEY_SHOW_ERRORS, KEY_SYSTEM_THEME, KEY_NIGHT_MODE)
            .forEach { findPreference<Preference>(it)?.onPreferenceChangeListener = this }

        findPreference<Preference>(KEY_HOW_TO_PLAY)?.onPreferenceClickListener = OnPreferenceClickListener {
            startActivity(intents.getHowToPlay())
            true
        }
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

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        when (preference.key) {
            KEY_SYSTEM_THEME -> toggleSystemTheme(newValue as Boolean)
            KEY_NIGHT_MODE -> toggleNightMode(newValue as Boolean)
        }
        return true
    }

    private fun toggleSystemTheme(enabled: Boolean) {
        findPreference<Preference>(KEY_NIGHT_MODE)?.isVisible = !enabled
        AppCompatDelegate.setDefaultNightMode(
            when {
                enabled -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                settings.isNightMode -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        findPreference<Preference>(KEY_NIGHT_MODE)?.isVisible = !enabled
    }

    private fun toggleNightMode(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        const val KEY_TIMER = "settings_timer"
        const val KEY_CHEATS = "settings_cheat"
        const val KEY_SHOW_ERRORS = "settings_show_errors"
        const val KEY_SYSTEM_THEME = "settings_system_theme"
        const val KEY_NIGHT_MODE = "settings_night_mode"
        const val KEY_HOW_TO_PLAY = "settings_how_to_play"
        const val KEY_VERSION = "settings_version"
    }
}
