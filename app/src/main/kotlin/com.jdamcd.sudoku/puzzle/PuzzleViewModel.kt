package com.jdamcd.sudoku.puzzle

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jdamcd.sudoku.repository.Puzzle
import com.jdamcd.sudoku.repository.PuzzleRepository
import com.jdamcd.sudoku.repository.database.PuzzleSave
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class PuzzleViewModel @Inject constructor(
    private val repository: PuzzleRepository
) : ViewModel(), LifecycleObserver {

    private var disposable = Disposables.empty()

    private val liveData = MutableLiveData<PuzzleState>()
    val uiModel = liveData as LiveData<PuzzleState>

    fun loadPuzzle(id: Long) {
        liveData.value = PuzzleState.Loading
        disposable = repository.getPuzzle(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data -> liveData.value = PuzzleState.Data(data) }
    }

    fun save(save: PuzzleSave) {
        repository.save(save)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun bookmark(id: Long, isBookmarked: Boolean) {
        repository.setBookmarked(id, isBookmarked)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}

sealed class PuzzleState {
    object Loading : PuzzleState()
    data class Data(val puzzle: Puzzle) : PuzzleState()
}
