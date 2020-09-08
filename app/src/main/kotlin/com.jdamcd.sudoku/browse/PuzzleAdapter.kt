package com.jdamcd.sudoku.browse

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.repository.Level
import com.jdamcd.sudoku.repository.Puzzle
import com.jdamcd.sudoku.util.Strings
import com.jdamcd.sudoku.util.inflate
import com.jdamcd.sudoku.view.PreviewPuzzleView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.card_puzzle.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PuzzleAdapter @Inject constructor() :
    ListAdapter<Puzzle, PuzzleViewHolder>(PuzzleAdapter.diffCallback) {

    private val clickSubject = PublishSubject.create<Puzzle>()

    init {
        setHasStableIds(true)
    }

    fun itemClicked(): Observable<Puzzle> = clickSubject

    override fun onBindViewHolder(holder: PuzzleViewHolder, position: Int) = holder.bind(getItem(position), clickSubject)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PuzzleViewHolder(parent.inflate(R.layout.card_puzzle))

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Puzzle>() {
            override fun areItemsTheSame(@NonNull oldPuzzle: Puzzle, @NonNull newPuzzle: Puzzle) = oldPuzzle.id == newPuzzle.id
            override fun areContentsTheSame(@NonNull oldPuzzle: Puzzle, @NonNull newPuzzle: Puzzle) = oldPuzzle == newPuzzle
        }
    }
}

class PuzzleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val title: TextView = itemView.puzzle_title
    private val time: TextView = itemView.puzzle_time
    private val preview: PreviewPuzzleView = itemView.puzzle_preview
    private val progress: ProgressBar = itemView.puzzle_progress
    private val bookmark: View = itemView.puzzle_bookmark

    fun bind(item: Puzzle, clickSubject: Subject<Puzzle>) {
        setTitle(item)
        setTime(item)
        preview.setGame(item.game)
        progress.progress = item.game.getPercentageCorrect()
        progress.progressDrawable = getProgressDrawable(itemView.context, item.level)
        bookmark.visibility = if (item.isBookmarked) View.VISIBLE else View.INVISIBLE
        itemView.setOnClickListener { clickSubject.onNext(item) }
    }

    private fun setTitle(item: Puzzle) {
        val builder = SpannableStringBuilder(item.title)
        val numberLength = item.number.toString().length
        builder.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(itemView.context, R.color.primary)),
            item.title.length - (numberLength + 1),
            item.title.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        title.text = builder
    }

    private fun setTime(item: Puzzle) {
        val showTime = item.time > minDisplayTime
        time.text = if (showTime) Strings.formatTime(item.time) else Strings.EMPTY
        time.visibility = if (showTime) View.VISIBLE else View.GONE
    }

    private fun getProgressDrawable(context: Context, level: Level): Drawable {
        return ContextCompat.getDrawable(context, Level.progressDrawable(level))!!
    }

    companion object {
        private val minDisplayTime = TimeUnit.SECONDS.toMillis(5)
    }
}
