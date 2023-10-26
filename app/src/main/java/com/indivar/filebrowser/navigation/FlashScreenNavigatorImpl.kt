package com.indivar.filebrowser.navigation

import com.indivar.filebrowser.common.core.flash.FlashScreenNavigator
import com.indivar.filebrowser.common.core.flash.ListingsLauncher
import com.indivar.filebrowser.common.core.flash.LoginLauncher

class FlashScreenNavigatorImpl(
    loginLauncher: LoginLauncher,
    listingsLauncher: ListingsLauncher,
) : FlashScreenNavigator, LoginLauncher by loginLauncher, ListingsLauncher by listingsLauncher