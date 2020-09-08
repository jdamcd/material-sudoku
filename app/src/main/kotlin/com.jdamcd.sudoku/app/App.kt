package com.jdamcd.sudoku.app

import androidx.appcompat.app.AppCompatDelegate
import com.jdamcd.sudoku.BuildConfig
import com.jdamcd.sudoku.settings.user.Settings
import com.jdamcd.sudoku.util.DebugUtil
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import javax.inject.Inject

class App : DaggerApplication() {

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
            if (settings.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }
}
