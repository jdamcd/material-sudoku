package com.jdamcd.sudoku.settings.user

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Settings @Inject
constructor(@ApplicationContext private val context: Context) {

    var timerEnabled: Boolean
        get() = preferences.getBoolean(SHOW_TIMER_ID, true)
        set(enabled) = preferenceEditor.putBoolean(SHOW_TIMER_ID, enabled).apply()

    var cheatsEnabled: Boolean
        get() = preferences.getBoolean(SHOW_CHEATS_ID, true)
        set(enabled) = preferenceEditor.putBoolean(SHOW_CHEATS_ID, enabled).apply()

    var showErrors: Boolean
        get() = preferences.getBoolean(SHOW_ERRORS_ID, false)
        set(isShowErrors) = preferenceEditor.putBoolean(SHOW_ERRORS_ID, isShowErrors).apply()

    var hideCompleted: Boolean
        get() = preferences.getBoolean(HIDE_COMPLETED_ID, false)
        set(isHideCompleted) = preferenceEditor.putBoolean(HIDE_COMPLETED_ID, isHideCompleted).apply()

    var useSystemTheme: Boolean
        get() = preferences.getBoolean(SYSTEM_THEME_ID, true)
        set(isSystemTheme) = preferenceEditor.putBoolean(SYSTEM_THEME_ID, isSystemTheme).apply()

    var nightMode: Boolean
        get() = preferences.getBoolean(NIGHT_MODE_ID, false)
        set(isNightMode) = preferenceEditor.putBoolean(NIGHT_MODE_ID, isNightMode).apply()

    var lastPlayed: Long
        get() = preferences.getLong(LAST_PLAYED_ID, NOT_SET)
        set(lastPlayed) = preferenceEditor.putLong(LAST_PLAYED_ID, lastPlayed).apply()

    var resumePrompt: Boolean
        get() = preferences.getBoolean(RESUME_PROMPT_ID, false)
        set(showResumePrompt) = preferenceEditor.putBoolean(RESUME_PROMPT_ID, showResumePrompt).apply()

    var ratingPromptShown: Boolean
        get() = preferences.getBoolean(RATE_SHOWN_ID, false)
        set(shown) = preferenceEditor.putBoolean(RATE_SHOWN_ID, shown).apply()

    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(context)

    private val preferenceEditor: Editor
        get() = preferences.edit()

    companion object {
        const val NOT_SET = -1L
        const val RATING_THRESHOLD = 3

        private const val SHOW_CHEATS_ID = "settings_cheat"
        private const val SHOW_TIMER_ID = "settings_timer"
        private const val HIDE_COMPLETED_ID = "settings_completed"
        private const val SHOW_ERRORS_ID = "settings_show_errors"
        private const val SYSTEM_THEME_ID = "settings_system_theme"
        private const val NIGHT_MODE_ID = "settings_night_mode"
        private const val LAST_PLAYED_ID = "last_played"
        private const val RESUME_PROMPT_ID = "resume_prompt"
        private const val RATE_SHOWN_ID = "shown_rate"
    }
}
