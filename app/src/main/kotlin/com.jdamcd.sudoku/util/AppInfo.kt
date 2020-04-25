package com.jdamcd.sudoku.util

import android.content.Context
import android.content.pm.PackageManager
import javax.inject.Inject

class AppInfo @Inject constructor(private val context: Context) {

    fun getVersion(): String {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }
}
