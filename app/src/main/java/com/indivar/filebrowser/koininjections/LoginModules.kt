package com.indivar.filebrowser.koininjections

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.indivar.filebrowser.common.core.flash.ListingsLauncher
import com.indivar.filebrowser.common.core.login.LoginScreenCoordinator
import com.indivar.filebrowser.common.core.login.LoginScreenCoordinatorImpl
import com.indivar.filebrowser.common.core.login.LoginScreenNavigator
import com.indivar.filebrowser.common.core.login.LoginViewModel
import com.indivar.filebrowser.common.core.login.LoginViewModelDelegate
import com.indivar.filebrowser.common.core.utils.AuthTokenCreator
import com.indivar.filebrowser.common.core.utils.CredentialStore
import com.indivar.filebrowser.common.core.utils.DataSerializer
import com.indivar.filebrowser.common.core.utils.INetworkHandler
import com.indivar.filebrowser.common.core.utils.UserAccountStore
import com.indivar.filebrowser.navigation.ListingsLauncherImpl
import com.indivar.filebrowser.navigation.LoginScreenNavigatorImpl
import org.koin.dsl.module

val loginModules = module {
    scope(Scopes.login) {
        factory<ListingsLauncher> {
            val (activity: Activity) = it
            ListingsLauncherImpl(activity)
        }
        factory<LoginScreenNavigator> {
            LoginScreenNavigatorImpl(get<ListingsLauncher> { it })
        }
        scoped<LoginScreenCoordinator> {
            LoginScreenCoordinatorImpl(
                get<LoginScreenNavigator> { it }
            )
        }
        factory<LoginViewModel> {
            val (activity: ComponentActivity) = it
            val factoryToCreateAllViewModels = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val delegate = LoginViewModelDelegate(
                        get<CredentialStore> { it },
                        get<UserAccountStore> { it },
                        get<INetworkHandler> { it },
                        get<AuthTokenCreator> { it },
                        get<DataSerializer> { it }
                    )
                    return LoginViewModel(delegate) as T
                }
            }
            ViewModelProvider(
                activity.viewModelStore,
                factoryToCreateAllViewModels
            )[LoginViewModel::class.java]

        }
    }
}