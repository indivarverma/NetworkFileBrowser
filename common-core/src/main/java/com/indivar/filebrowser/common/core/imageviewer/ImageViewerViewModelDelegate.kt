package com.indivar.filebrowser.common.core.imageviewer

import com.indivar.filebrowser.common.core.mvvm.BaseViewModelDelegate
import com.indivar.filebrowser.common.core.utils.CredentialStore

class ImageViewerViewModelDelegate(
    url: String,
    credentialStore: CredentialStore,
) : BaseViewModelDelegate<ViewState, ImageViewerViewModelDelegate.DataState>() {

    override val initialState: DataState = DataState(
        url = url,
        encodedCredentials = credentialStore.getStoredCredentials()?.encodedCredentials,
    )

    override fun mapToViewState(state: DataState): ViewState =
        state.encodedCredentials?.let {
            ViewState.Ready(
                imageUrl = state.url,
                baseEncodedCredentials = state.encodedCredentials
            )
        } ?: ViewState.NotAuthorized


    data class DataState(
        val url: String,
        val encodedCredentials: String?,
    )
}