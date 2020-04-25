package com.jdamcd.sudoku.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.SimpleItemAnimator
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.base.BaseFragment
import com.jdamcd.sudoku.repository.Level
import com.jdamcd.sudoku.repository.Puzzle
import com.jdamcd.sudoku.view.OffsetDecoration
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_recycler_puzzles.*

class PuzzleListFragment : BaseFragment(), PuzzleListPresenter.View {

    @Inject internal lateinit var presenter: PuzzleListPresenter
    @Inject internal lateinit var adapter: PuzzleAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_recycler_puzzles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler_view.addItemDecoration(OffsetDecoration(requireContext(), R.dimen.half_gutter))
        (recycler_view.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        presenter.start(this)
    }

    override fun showPuzzles(puzzles: List<Puzzle>) {
        adapter.submitList(puzzles)
        if (recycler_view.adapter == null) {
            recycler_view.adapter = adapter
        }
        empty.visibility = if (puzzles.isEmpty()) View.VISIBLE else View.GONE
        loading.visibility = View.GONE
    }

    override fun onPuzzleClicked() = adapter.itemClicked()

    override fun getLevel() = requireArguments()[PARAM_KEY_LEVEL] as Level

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.stop()
    }

    companion object {
        private const val PARAM_KEY_LEVEL = "level"

        fun create(level: Level): PuzzleListFragment {
            val f = PuzzleListFragment()
            val args = Bundle()
            args.putSerializable(PARAM_KEY_LEVEL, level)
            f.arguments = args
            return f
        }
    }
}
