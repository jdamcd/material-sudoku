package com.jdamcd.sudoku.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.jdamcd.sudoku.BuildConfig
import com.jdamcd.sudoku.settings.user.Settings
import com.jdamcd.sudoku.util.DebugUtil
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject lateinit var settings: Settings

    override fun onCreate() {
        super.onCreate()

        setupTheme()

        if (BuildConfig.DEBUG) {
            DebugUtil.enableStrictMode()
        }
    }

    private fun setupTheme() {
        AppCompatDelegate.setDefaultNightMode(
            when {
                settings.isSystemTheme -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                settings.isNightMode -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
