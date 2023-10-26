package com.indivar.filebrowser.koininjections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.indivar.filebrowser.common.core.imageviewer.ImageViewerViewModel
import com.indivar.filebrowser.common.core.imageviewer.ImageViewerViewModelDelegate
import com.indivar.filebrowser.common.core.utils.CredentialStore
import com.indivar.filebrowser.image.ImageViewerActivity
import org.koin.dsl.module

val imageViewerModules = module {
    scope(Scopes.images) {
        factory<ImageViewerViewModel> {
            val (activity: ImageViewerActivity) = it
            val factoryToCreateAllViewModels = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val delegate = ImageViewerViewModelDelegate(
                        activity.imageUrl,
                        get<CredentialStore> { it },
                    )
                    return ImageViewerViewModel(delegate) as T
                }
            }
            ViewModelProvider(
                activity.viewModelStore,
                factoryToCreateAllViewModels
            )[ImageViewerViewModel::class.java]
        }
    }
}