package com.jdamcd.sudoku.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.SimpleItemAnimator
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.databinding.FragmentRecyclerPuzzlesBinding
import com.jdamcd.sudoku.repository.Level
import com.jdamcd.sudoku.repository.Puzzle
import com.jdamcd.sudoku.view.OffsetDecoration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PuzzleListFragment : Fragment(), PuzzleListPresenter.View {

    @Inject internal lateinit var presenter: PuzzleListPresenter
    @Inject internal lateinit var adapter: PuzzleAdapter

    private var _binding: FragmentRecyclerPuzzlesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecyclerPuzzlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.addItemDecoration(OffsetDecoration(requireContext(), R.dimen.half_gutter))
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        presenter.start(this)
    }

    override fun showPuzzles(puzzles: List<Puzzle>) {
        adapter.submitList(puzzles)
        if (binding.recyclerView.adapter == null) {
            binding.recyclerView.adapter = adapter
        }
        binding.empty.visibility = if (puzzles.isEmpty()) View.VISIBLE else View.GONE
        binding.loading.root.visibility = View.GONE
    }

    override fun onPuzzleClicked() = adapter.itemClicked()

    override fun getLevel() = requireArguments()[PARAM_KEY_LEVEL] as Level

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.stop()
        _binding = null
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
