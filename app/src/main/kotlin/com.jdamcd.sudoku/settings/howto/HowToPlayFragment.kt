package com.jdamcd.sudoku.settings.howto

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jdamcd.sudoku.Constants
import com.jdamcd.sudoku.IntentFactory
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.game.Sudoku
import com.jdamcd.sudoku.view.PreviewPuzzleView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("ValidFragment")
@AndroidEntryPoint
class HowToPlayFragment : Fragment() {

    @Inject internal lateinit var intents: IntentFactory

    private var easterEggCounter = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_how_to_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val heart = view.findViewById<PreviewPuzzleView>(R.id.puzzle_heart)
        heart.setPreview(Sudoku(PUZZLE_HEART))
        heart.setOnClickListener {
            if (++easterEggCounter % EASTER_EGG_THRESHOLD == 0) {
                openHeartPuzzle()
            }
        }
    }

    private fun openHeartPuzzle() {
        startActivity(intents.getPuzzle(Constants.EASTER_EGG_ID))
    }

    companion object {
        private const val PUZZLE_HEART = "000000000023000780100406009900050004200000008010000030008000300000209000000010000"
        private const val EASTER_EGG_THRESHOLD = 3
    }
}
