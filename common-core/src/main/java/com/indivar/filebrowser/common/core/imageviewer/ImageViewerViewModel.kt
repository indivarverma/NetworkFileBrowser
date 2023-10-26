package com.indivar.filebrowser.common.core.imageviewer

import com.indivar.filebrowser.common.core.mvvm.BaseViewModel

sealed class ViewState {
    data class Ready(
        val imageUrl: String,
        val baseEncodedCredentials: String?,
    ) : ViewState()

    object NotAuthorized : ViewState()
}

class ImageViewerViewModel(delegate: ImageViewerViewModelDelegate) :
    BaseViewModel<ViewState, ImageViewerViewModelDelegate.DataState>(delegate)
