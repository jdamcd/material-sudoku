package com.jdamcd.sudoku.shortcut

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N_MR1
import androidx.core.content.getSystemService
import com.jdamcd.sudoku.IntentFactory
import com.jdamcd.sudoku.R
import java.util.Arrays.asList
import javax.inject.Inject

class ShortcutController @Inject constructor(
    val context: Context,
    private val intentFactory: IntentFactory
) {

    fun enableResume() {
        if (SDK_INT < N_MR1) return

        getManager()?.dynamicShortcuts = asList(createResumeShortcut())
    }

    @TargetApi(N_MR1)
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
        if (SDK_INT < N_MR1) return

        val manager = getManager()
        manager?.disableShortcuts(asList(ID_RESUME))
        manager?.removeAllDynamicShortcuts()
    }

    fun reportRandomUsed() {
        if (SDK_INT < N_MR1) return

        getManager()?.reportShortcutUsed(ID_RANDOM)
    }

    fun reportResumeUsed() {
        if (SDK_INT < N_MR1) return

        getManager()?.reportShortcutUsed(ID_RESUME)
    }

    @TargetApi(N_MR1)
    private fun getManager(): ShortcutManager? {
        return context.getSystemService<ShortcutManager>()
    }

    companion object {
        const val ID_RESUME = "resume"
        const val ID_RANDOM = "random"
    }
}
