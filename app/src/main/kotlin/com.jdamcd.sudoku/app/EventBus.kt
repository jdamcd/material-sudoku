package com.jdamcd.sudoku.app

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventBus @Inject constructor() {

    private val publisher = PublishSubject.create<Any>()

    fun publish(event: Any) {
        publisher.onNext(event)
    }

    fun <T> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)
        .subscribeOn(AndroidSchedulers.mainThread())
}

enum class HideCompletedEvent { SHOW, HIDE }

