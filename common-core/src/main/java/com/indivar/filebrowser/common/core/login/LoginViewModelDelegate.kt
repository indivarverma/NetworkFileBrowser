package com.indivar.filebrowser.common.core.login

import com.indivar.filebrowser.common.core.mvvm.BaseViewModelDelegate
import com.indivar.filebrowser.common.core.mvvm.Reducer
import com.indivar.filebrowser.common.core.utils.AuthTokenCreator
import com.indivar.filebrowser.common.core.utils.CredentialStore
import com.indivar.filebrowser.common.core.utils.DataSerializer
import com.indivar.filebrowser.common.core.utils.HandleableError
import com.indivar.filebrowser.common.core.utils.INetworkHandler
import com.indivar.filebrowser.common.core.utils.UserAccountStore
import com.indivar.filebrowser.models.FileBrowserCredentials
import com.indivar.filebrowser.models.FileSystemElement
import io.reactivex.rxjava3.subjects.BehaviorSubject

class LoginViewModelDelegate(
    private val credentialStore: CredentialStore,
    private val userAccountStore: UserAccountStore,
    private val iNetworkHandler: INetworkHandler,
    private val authTokenCreator: AuthTokenCreator,
    private val dataSerializer: DataSerializer,
) : BaseViewModelDelegate<ViewState, LoginViewModelDelegate.DataState>() {

    private val refreshGetMe = BehaviorSubject.create<UsernamePassword>()

    init {
        subscribeForFreshProfile()
    }

    private fun subscribeForFreshProfile() {
        refreshGetMe.switchMap { usernamePassword ->
            iNetworkHandler.getMe(
                authTokenCreator.createAuthToken(
                    usernamePassword.userName,
                    usernamePassword.password
                )
            ).map<UserData> { UserData.Success(details = it) }.onErrorReturn {
                UserData.Failure(it)
            }
        }.map<Reducer<DataState>> { userData ->
            { dataState ->
                when (userData) {
                    is UserData.Success -> {
                        val userDetails = userData.details
                        credentialStore.setStoredCredentials(
                            FileBrowserCredentials(
                                userDetails.authToken,
                            )
                        )
                        userAccountStore.setFirstName(userDetails.firstName)
                        userAccountStore.setLastName(userDetails.lastName)
                        userAccountStore.setUserId(dataState.userName)
                        val serializedTop = dataSerializer.serialize(userDetails.topLevelDirectory)
                        userAccountStore.setTopDirectory(serializedTop)
                        dataState.copy(
                            firstName = userDetails.firstName,
                            lastName = userDetails.lastName,
                            currentDirectory = userDetails.topLevelDirectory,
                            loadingState = LoadingState.Idle,
                            loginResult = LoginResult.Success,
                        )
                    }

                    is UserData.Failure -> {
                        dataState.copy(
                            firstName = null,
                            lastName = null,
                            currentDirectory = null,
                            loadingState = LoadingState.Idle,
                            error = userData.error,
                            loginResult = LoginResult.Failure,
                        )
                    }
                }

            }
        }.enqueue()
    }

    override val initialState: DataState
        get() = DataState(
            userName = "",
            password = "",
            error = null,
            firstName = null,
            lastName = null,
            currentDirectory = null,
            loadingState = LoadingState.Idle,
            loginResult = LoginResult.Idle,
        )

    private fun onLoginTrigger(
        userName: String,
        password: String,
    ) {
        val trimmedUsername = userName.trim()
        val trimmedPassword = password.trim()
        enqueue {
            it.copy(
                userName = trimmedUsername,
                password = trimmedPassword,
                loadingState = LoadingState.Loading,
            ).also {
                refreshGetMe.onNext(UsernamePassword(trimmedUsername, trimmedPassword))
            }
        }

    }

    private fun onErrorAccepted() {
        enqueue { dataState ->
            dataState.copy(
                error = null
            )
        }
    }

    private fun onUsernameChange(value: String) {
        enqueue {
            it.copy(userName = value)
        }
    }

    private fun onPasswordChange(value: String) {
        enqueue {
            it.copy(password = value)
        }
    }

    override fun mapToViewState(state: DataState): ViewState = ViewState(
        userName = state.userName,
        password = state.password,
        loginError = state.error?.let {
            HandleableError(
                it.message ?: "Unknown Error", ::onErrorAccepted
            )
        },
        showLoading = state.loadingState == LoadingState.Loading,
        canLogin = state.userName.trim().length >= MIN_USER_NAME_LENGTH && state.password.trim().length >= MIN_PASSWORD_LENGTH,
        onUsernameChange = ::onUsernameChange,
        onPasswordChange = ::onPasswordChange,
        onLogin = ::onLoginTrigger,
        triggerListLaunch = state.loginResult == LoginResult.Success,

        )

    data class DataState(
        val userName: String,
        val password: String,
        val firstName: String?,
        val lastName: String?,
        val loadingState: LoadingState,
        val currentDirectory: FileSystemElement.Directory?,
        val error: Throwable?,
        val loginResult: LoginResult,
    )

    enum class LoginResult {
        Success,
        Failure,
        Idle
    }

    enum class LoadingState {
        Loading, Idle
    }

    companion object {
        const val MIN_USER_NAME_LENGTH = 3
        const val MIN_PASSWORD_LENGTH = 3
    }


}