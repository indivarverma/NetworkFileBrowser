package com.indivar.filebrowser.common.core.login

import com.indivar.filebrowser.common.core.mvvm.BaseViewModel
import com.indivar.filebrowser.common.core.utils.HandleableError
import com.indivar.filebrowser.models.UserDetails

data class ViewState(
    val userName: String,
    val password: String,
    val showLoading: Boolean,
    val canLogin: Boolean,
    val loginError: HandleableError?,
    val triggerListLaunch: Boolean,
    val onLogin: (String, String) -> Unit,
    val onUsernameChange: (String) -> Unit,
    val onPasswordChange: (String) -> Unit,
)

sealed class UserData {
    data class Success(
        val details: UserDetails,
    ) : UserData()

    data class Failure(
        val error: Throwable,
    ) : UserData()
}

data class UsernamePassword(
    val userName: String,
    val password: String,
)

class LoginViewModel(delegate: LoginViewModelDelegate) :
    BaseViewModel<ViewState, LoginViewModelDelegate.DataState>(delegate)
