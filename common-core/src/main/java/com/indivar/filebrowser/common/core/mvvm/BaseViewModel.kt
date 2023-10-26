package com.indivar.filebrowser.common.core.mvvm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

typealias Reducer<State> = (State) -> State
abstract class BaseViewModel<ViewState, State>(
    private val delegate: BaseViewModelDelegate<ViewState, State>
):
    ViewModel()  {
    val state: MutableLiveData<ViewState> = MutableLiveData<ViewState>()
    private val states = delegate.stateObs.publish()
    private val stateObs = states.distinctUntilChanged().map {
        delegate.mapToViewState(it)
    }
    private val compositeDisposable = CompositeDisposable()

    init {
        stateObs.doOnError {
            Log.e("filebrowser", "${it.message}")
        }.subscribe({
            state.postValue(it)
        }, {
            Log.d("filebrowser", "${it}")
        }).also {
            compositeDisposable.add(it)
        }
        val disposable: Disposable = states.connect()
        compositeDisposable.add(disposable)
    }
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}