package com.indivar.filebrowser.koininjections

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.indivar.filebrowser.common.core.listing.ListingViewModel
import com.indivar.filebrowser.common.core.listing.ListingViewModelDelegate
import com.indivar.filebrowser.common.core.utils.CredentialStore
import com.indivar.filebrowser.common.core.utils.DataSerializer
import com.indivar.filebrowser.common.core.utils.INetworkHandler
import com.indivar.filebrowser.common.core.utils.UserAccountStore
import com.indivar.filebrowser.navigation.FileScreenCoordinator
import com.indivar.filebrowser.navigation.FileScreenCoordinatorImpl
import com.indivar.filebrowser.navigation.FileScreenNavigator
import com.indivar.filebrowser.navigation.FileScreenNavigatorImpl
import org.koin.dsl.module

val listingModules = module {
    scope(Scopes.listings) {
        factory<ListingViewModel> {
            val (activity: ComponentActivity) = it
            val factoryToCreateAllViewModels = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val delegate = ListingViewModelDelegate(
                        get<INetworkHandler> { it },
                        get<CredentialStore> { it },
                        get<UserAccountStore> { it },
                        get<DataSerializer> { it }
                    )
                    return ListingViewModel(delegate) as T
                }
            }
            ViewModelProvider(
                activity.viewModelStore,
                factoryToCreateAllViewModels
            )[ListingViewModel::class.java]
        }
        factory<FileScreenNavigator> {
            val (activity: Activity) = it
            FileScreenNavigatorImpl(
                activity = activity
            )
        }
        scoped<FileScreenCoordinator> {
            FileScreenCoordinatorImpl(
                get<FileScreenNavigator> { it }
            )
        }
    }
}