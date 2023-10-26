package com.indivar.filebrowser.common.core.login

class LoginScreenCoordinatorImpl(
    private val navigator: LoginScreenNavigator,
) : LoginScreenCoordinator {
    override fun onSuccessfulLogin() {
        navigator.launchListingsScreen()
    }
}