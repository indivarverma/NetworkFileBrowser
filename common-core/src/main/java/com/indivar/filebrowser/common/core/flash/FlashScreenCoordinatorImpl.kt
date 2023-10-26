package com.indivar.filebrowser.common.core.flash

class FlashScreenCoordinatorImpl(
    private val navigator: FlashScreenNavigator,
): FlashScreenCoordinator {
    override fun launchListingsScreen() {
        navigator.launchListingsScreen()
    }

    override fun launchLogin() {
        navigator.launchLogin()
    }
}