package com.jdamcd.sudoku.browse

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jdamcd.sudoku.IntentFactory
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.repository.Puzzle
import com.jdamcd.sudoku.settings.user.Settings
import com.jdamcd.sudoku.util.Strings
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_resume_puzzle.*
import javax.inject.Inject

class ResumePuzzleSheet : BottomSheetDialogFragment() {

    @Inject lateinit var intents: IntentFactory
    @Inject lateinit var settings: Settings

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            dialog.window?.setLayout(determineWidth(), ViewGroup.LayoutParams.MATCH_PARENT)
            setPeekHeight()
        }
        return dialog
    }

    private fun determineWidth(): Int {
        val width = resources.getDimensionPixelSize(R.dimen.bottom_sheet_width)
        return if (width == 0) ViewGroup.LayoutParams.MATCH_PARENT else width
    }

    private fun setPeekHeight() {
        val behavior = BottomSheetBehavior.from(dialog?.findViewById(R.id.design_bottom_sheet)!!)
        behavior.peekHeight = resources.getDimensionPixelSize(R.dimen.resume_sheet_height)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return View.inflate(ContextThemeWrapper(context, R.style.SudokuTheme), R.layout.fragment_resume_puzzle, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val arguments = requireArguments()
        title.text = getString(R.string.resume_title, arguments[PARAM_NAME] as String)
        time_played.text = arguments[PARAM_TIME] as String
        progress_count.text = arguments[PARAM_PROGRESS] as String
        configureButton(arguments[PARAM_ID] as Long)
    }

    private fun configureButton(puzzleId: Long) {
        ok_button.setOnClickListener {
            startActivity(intents.getPuzzle(puzzleId))
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        settings.resumePrompt = false
    }

    companion object {
        fun forPuzzle(puzzle: Puzzle): ResumePuzzleSheet {
            val resumePrompt = ResumePuzzleSheet()
            resumePrompt.arguments = bundleOf(
                PARAM_ID to puzzle.id,
                PARAM_NAME to puzzle.title,
                PARAM_TIME to Strings.formatTime(puzzle.time),
                PARAM_PROGRESS to formatProgress(puzzle)
            )
            return resumePrompt
        }

        private fun formatProgress(puzzle: Puzzle) = "${puzzle.game.getNumberOfCorrectAnswers()} / ${puzzle.game.getAnswersNeeded()}"

        private const val PARAM_ID = "id"
        private const val PARAM_NAME = "name"
        private const val PARAM_TIME = "time"
        private const val PARAM_PROGRESS = "progress"
    }
}
