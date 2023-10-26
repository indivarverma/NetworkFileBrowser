package com.indivar.filebrowser.koininjections

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.indivar.filebrowser.common.core.flash.FlashScreenCoordinator
import com.indivar.filebrowser.common.core.flash.FlashScreenCoordinatorImpl
import com.indivar.filebrowser.common.core.flash.FlashScreenNavigator
import com.indivar.filebrowser.common.core.flash.FlashScreenViewModel
import com.indivar.filebrowser.common.core.flash.FlashScreenViewModelDelegate
import com.indivar.filebrowser.common.core.flash.ListingsLauncher
import com.indivar.filebrowser.common.core.flash.LoginLauncher
import com.indivar.filebrowser.common.core.utils.CredentialStore
import com.indivar.filebrowser.navigation.FlashScreenNavigatorImpl
import com.indivar.filebrowser.navigation.ListingsLauncherImpl
import com.indivar.filebrowser.navigation.LoginLauncherImpl
import org.koin.dsl.module

val bootupModules = module {
    scope(Scopes.bootup) {
        factory<LoginLauncher> {
            val (activity: Activity) = it
            LoginLauncherImpl(activity = activity)
        }
        factory<ListingsLauncher> {
            val (activity: Activity) = it
            ListingsLauncherImpl(activity = activity)
        }
        factory<FlashScreenNavigator> {
            FlashScreenNavigatorImpl(
                get<LoginLauncher> { it },
                get<ListingsLauncher> { it }
            )
        }
        scoped<FlashScreenCoordinator> {
            FlashScreenCoordinatorImpl(
                get<FlashScreenNavigator> { it }
            )
        }
        factory<FlashScreenViewModel> {
            val (activity: ComponentActivity) = it
            val factoryToCreateAllViewModels = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val delegate = FlashScreenViewModelDelegate(
                        get<CredentialStore> { it }
                    )
                    return FlashScreenViewModel(delegate) as T
                }
            }
            ViewModelProvider(
                activity.viewModelStore,
                factoryToCreateAllViewModels
            )[FlashScreenViewModel::class.java]

        }
    }
}