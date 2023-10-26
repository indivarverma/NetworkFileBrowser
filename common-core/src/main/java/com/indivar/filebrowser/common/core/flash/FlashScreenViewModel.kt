package com.indivar.filebrowser.common.core.flash

import com.indivar.filebrowser.common.core.mvvm.BaseViewModel

sealed class ViewState {
    object HasAuthorization : ViewState()

    object NotAuthorized : ViewState()
}

class FlashScreenViewModel(delegate: FlashScreenViewModelDelegate) :
    BaseViewModel<ViewState, FlashScreenViewModelDelegate.DataState>(delegate)