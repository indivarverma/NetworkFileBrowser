package com.indivar.filebrowser.navigation

import com.indivar.filebrowser.common.core.flash.ListingsLauncher
import com.indivar.filebrowser.common.core.login.LoginScreenNavigator

class LoginScreenNavigatorImpl(
    listingsLauncher: ListingsLauncher
) : LoginScreenNavigator, ListingsLauncher by listingsLauncher