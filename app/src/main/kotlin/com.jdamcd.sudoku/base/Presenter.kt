package com.jdamcd.sudoku.base

import androidx.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

@Deprecated("Migrate to ViewModel")
open class Presenter<V : PresenterView> {

    private val disposables = CompositeDisposable()
    protected var view: V? = null

    @CallSuper
    open fun start(view: V) {
        this.view = view
    }

    @CallSuper
    fun stop() {
        view = null
        disposables.dispose()
    }

    @CallSuper
    protected fun addSubscription(disposable: Disposable) {
        disposables.add(disposable)
    }

    protected fun removeSubscription(disposable: Disposable) {
        disposable.dispose()
        disposables.remove(disposable)
    }
}
