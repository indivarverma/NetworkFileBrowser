package com.indivar.filebrowser.common.core.mvvm

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.UnicastSubject

abstract class BaseViewModelDelegate<ViewState, DataState> {
    abstract val initialState: DataState
    abstract fun mapToViewState(state: DataState): ViewState
    private val reducerObs = UnicastSubject.create<Observable<Reducer<DataState>>>()
    val stateObs: Observable<DataState>
        get() = reducerObs.flatMap {
            it
        }.scan(initialState) { state, reducer ->
            reducer.invoke(state)
        }

    fun Observable<Reducer<DataState>>.enqueue(): Unit {
        reducerObs.onNext(this)
    }

    fun enqueue(reducer: Reducer<DataState>) {
        reducerObs.onNext(Observable.just(reducer))
    }

}