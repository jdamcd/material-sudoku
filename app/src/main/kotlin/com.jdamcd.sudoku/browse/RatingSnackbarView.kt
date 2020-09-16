package com.jdamcd.sudoku.browse

import android.content.ActivityNotFoundException
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.jdamcd.sudoku.IntentFactory
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.settings.user.Settings
import com.jdamcd.sudoku.util.snackbar
import javax.inject.Inject

internal class RatingSnackbarView @Inject constructor(
    private val intents: IntentFactory,
    private val settings: Settings
) {

    fun show(view: View) {
        val rateBar = Snackbar.make(view, R.string.banner_action_call, Snackbar.LENGTH_INDEFINITE)
        rateBar.setAction(R.string.banner_rate) { onActionClick(rateBar, view) }
        rateBar.view.setOnClickListener { rateBar.dismiss() }
        rateBar.show()
        settings.ratingPromptShown = true
    }

    private fun onActionClick(rateBar: Snackbar, parent: View) {
        rateBar.dismiss()
        try {
            parent.context.startActivity(intents.getRateApp())
        } catch (e: ActivityNotFoundException) {
            parent.snackbar(R.string.toast_missing_play)
        }
    }
}
