package com.jdamcd.sudoku.browse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.play.core.review.ReviewManagerFactory
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.app.IntentFactory
import com.jdamcd.sudoku.base.BaseActivity
import com.jdamcd.sudoku.databinding.ActivityPuzzleChoiceBinding
import com.jdamcd.sudoku.repository.Level
import com.jdamcd.sudoku.repository.Puzzle
import com.jdamcd.sudoku.settings.user.Settings
import com.jdamcd.sudoku.util.ViewUtil
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class PuzzleChoiceActivity : BaseActivity(), PuzzleChoicePresenter.View {

    @Inject internal lateinit var presenter: PuzzleChoicePresenter
    @Inject internal lateinit var intents: IntentFactory
    @Inject internal lateinit var settings: Settings

    private val toggleSubject = PublishSubject.create<Boolean>()
    private val fabView = RandomFabView()

    private lateinit var binding: ActivityPuzzleChoiceBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuzzleChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar(showUp = false)

        presenter.start(this)
        fabView.setup(binding.fab)
        configurePager()

        handleIntent()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
        handleIntent()
    }

    private fun handleIntent() {
        if (IntentFactory.isRandomShortcut(intent)) {
            presenter.playRandomPuzzle(this, Level.MEDIUM)
        } else {
            checkResumePuzzle()
        }
        intent.removeExtra(IntentFactory.EXTRA_SHORTCUT)
        intent.removeExtra(IntentFactory.EXTRA_RESUME_ID)
    }

    private fun checkResumePuzzle() {
        val resumeId = intent.getLongExtra(IntentFactory.EXTRA_RESUME_ID, Settings.NOT_SET)
        if (resumeId > 0L) {
            presenter.loadInProgressPuzzle(resumeId, !IntentFactory.isResumeShortcut(intent))
        }
    }

    override fun getContext() = this

    override fun onDestroy() {
        super.onDestroy()
        presenter.stop()
    }

    private fun configurePager() {
        val pagerAdapter = PuzzlePagerAdapter(this)
        val pager = binding.pager
        pager.adapter = pagerAdapter
        binding.indicator.setViewPager(pager)
        pager.offscreenPageLimit = pagerAdapter.itemCount - 1
        pager.currentItem = 1
        pager.setPageTransformer(MarginPageTransformer(ViewUtil.dpToPx(resources, 5)))
        pager.registerOnPageChangeCallback(fabView)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_puzzle_choice, menu)
        val item = menu.findItem(R.id.action_hide_completed)
        item.isChecked = settings.hideCompleted
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_scoreboard -> {
                startActivity(intents.getScoreboard())
                return true
            }
            R.id.action_bookmarks -> {
                startActivity(intents.getBookmarks())
                return true
            }
            R.id.action_hide_completed -> {
                toggleShowCompleted(item)
                return true
            }
            R.id.action_settings -> {
                startActivity(intents.getSettings())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onToggleCompleted(): PublishSubject<Boolean> = toggleSubject

    override fun onFabClick(): Observable<Level> {
        val subject = PublishSubject.create<View>()
        binding.fab.setOnClickListener { subject.onNext(it) }
        return subject
            .throttleFirst(2, TimeUnit.SECONDS)
            .map { PuzzlePagerAdapter.levels[binding.pager.currentItem] }
    }

    override fun showRandomError() {
        Log.i(PuzzleChoiceActivity::class.simpleName, "No unplayed puzzles")
    }

    override fun showRatingPrompt() {
        val reviewManager = ReviewManagerFactory.create(this)
        reviewManager.requestReviewFlow()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    reviewManager.launchReviewFlow(this, it.result)
                    settings.ratingPromptShown = true
                }
            }
    }

    override fun showResumePrompt(puzzle: Puzzle) {
        if (!isFinishing) {
            ResumePuzzleSheet
                .forPuzzle(puzzle)
                .show(supportFragmentManager, TAG_RESUME_PROMPT)
        }
    }

    override fun openPuzzle(puzzle: Puzzle) {
        startActivity(intents.getPuzzle(puzzle.id))
    }

    private fun toggleShowCompleted(item: MenuItem) {
        val toggled = !item.isChecked
        item.isChecked = toggled
        toggleSubject.onNext(toggled)
    }

    companion object {
        const val TAG_RESUME_PROMPT = "resume_prompt"
    }
}
