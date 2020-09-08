package com.jdamcd.sudoku.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.SimpleItemAnimator
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.browse.PuzzleAdapter
import com.jdamcd.sudoku.repository.Puzzle
import com.jdamcd.sudoku.view.OffsetDecoration
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_recycler_bookmarks.*
import javax.inject.Inject

@AndroidEntryPoint
class BookmarksFragment : Fragment(), BookmarksPresenter.View {

    @Inject internal lateinit var presenter: BookmarksPresenter
    @Inject lateinit var adapter: PuzzleAdapter

    private val removeAllSubject = PublishSubject.create<Any>()

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_recycler_bookmarks, container, false)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.activity_bookmarks, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_remove_all) {
            removeAllSubject.onNext(Unit)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRemoveAll(): Observable<Any> = removeAllSubject

    override fun onPuzzleClicked() = adapter.itemClicked()

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.stop()
    }
}
