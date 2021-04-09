package com.jdamcd.sudoku.shortcut

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N_MR1
import androidx.core.content.getSystemService
import com.jdamcd.sudoku.app.IntentFactory
import com.jdamcd.sudoku.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@TargetApi(N_MR1)
class ShortcutController @Inject constructor(
    @ApplicationContext val context: Context,
    private val intentFactory: IntentFactory
) {

    private val isSupported = SDK_INT >= N_MR1

    fun enableResume() {
        if (isSupported) {
            getManager()?.dynamicShortcuts = listOf(createResumeShortcut())
        }
    }

    private fun createResumeShortcut(): ShortcutInfo {
        return ShortcutInfo.Builder(context, ID_RESUME)
            .setIntent(intentFactory.getResumeSplash())
            .setShortLabel(context.getString(R.string.shortcut_resume_label))
            .setLongLabel(context.getString(R.string.shortcut_resume_long_label))
            .setDisabledMessage(context.getString(R.string.shortcut_resume_disabled))
            .setIcon(Icon.createWithResource(context, R.drawable.ic_shortcut_bookmark))
            .build()
    }

    fun disableResume() {
        if (isSupported) {
            getManager()?.apply {
                disableShortcuts(listOf(ID_RESUME))
                removeAllDynamicShortcuts()
            }
        }
    }


    fun reportRandomUsed() {
        if (isSupported) {
            getManager()?.reportShortcutUsed(ID_RANDOM)
        }

    }

    fun reportResumeUsed() {
        if (isSupported) {
            getManager()?.reportShortcutUsed(ID_RESUME)
        }
    }

    private fun getManager(): ShortcutManager? {
        return context.getSystemService()
    }

    companion object {
        const val ID_RESUME = "resume"
        const val ID_RANDOM = "random"
    }
}
