package com.indivar.filebrowser.common.core.flash

import com.indivar.filebrowser.common.core.mvvm.BaseViewModelDelegate
import com.indivar.filebrowser.common.core.utils.CredentialStore
import com.indivar.filebrowser.models.FileBrowserCredentials

class FlashScreenViewModelDelegate(
    private val credentialStore: CredentialStore,
) : BaseViewModelDelegate<ViewState, FlashScreenViewModelDelegate.DataState>() {

    data class DataState(
        val credentials: FileBrowserCredentials?,
    )

    override val initialState: DataState
        get() = DataState(credentials = credentialStore.getStoredCredentials())

    override fun mapToViewState(state: DataState): ViewState =
        (state.credentials)?.let {
            ViewState.HasAuthorization
        } ?: ViewState.NotAuthorized

}
