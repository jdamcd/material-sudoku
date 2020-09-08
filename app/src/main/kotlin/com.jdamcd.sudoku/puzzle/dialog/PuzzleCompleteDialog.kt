package com.jdamcd.sudoku.puzzle.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.base.BaseDialogFragment
import com.jdamcd.sudoku.settings.user.Settings
import com.jdamcd.sudoku.util.Strings
import java.util.Random
import javax.inject.Inject

class PuzzleCompleteDialog : BaseDialogFragment(), OnClickListener {

    @Inject lateinit var settings: Settings
    @Inject lateinit var res: Resources

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
            .setTitle(generateGreeting())
            .setMessage(generateMessage())
            .setPositiveButton(android.R.string.yes, this)
            .create()
    }

    private fun generateGreeting(): String {
        val greetings = res.getStringArray(R.array.complete_greetings)
        return greetings[Random().nextInt(greetings.size)]
    }

    private fun generateMessage(): String {
        val builder = StringBuilder()
        if (settings.isTimerEnabled) {
            val time = Strings.formatTime(requireArguments().getLong(ARGS_TIME))
            builder.append(resources.getString(R.string.puzzle_complete_time, time))
        } else {
            builder.append(getString(R.string.puzzle_complete_message))
        }
        val cheats = requireArguments().getInt(ARGS_CHEATS)
        if (cheats > 0) {
            builder.append(" ").append(resources.getQuantityString(R.plurals.puzzle_complete_cheats, cheats, cheats))
        }
        return builder.toString()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        dialog.dismiss()
        activity?.finish()
    }

    companion object {
        const val TAG = "puzzle_complete"

        private const val ARGS_TIME = "time"
        private const val ARGS_CHEATS = "cheats"

        fun newInstance(time: Long, cheats: Int): PuzzleCompleteDialog {
            val frag = PuzzleCompleteDialog()
            val args = Bundle()
            args.putLong(ARGS_TIME, time)
            args.putInt(ARGS_CHEATS, cheats)
            frag.arguments = args
            return frag
        }
    }
}
