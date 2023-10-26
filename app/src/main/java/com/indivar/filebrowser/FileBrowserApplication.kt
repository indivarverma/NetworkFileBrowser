package com.indivar.filebrowser

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import com.indivar.filebrowser.koininjections.bootupModules
import com.indivar.filebrowser.koininjections.imageViewerModules
import com.indivar.filebrowser.koininjections.koinModules
import com.indivar.filebrowser.koininjections.listingModules
import com.indivar.filebrowser.koininjections.loginModules
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FileBrowserApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(
                koinModules,
                bootupModules,
                loginModules,
                listingModules,
                imageViewerModules
            )

            androidContext(this@FileBrowserApplication.applicationContext)
        }
        SoLoader.init(this, false)

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(SharedPreferencesFlipperPlugin(this, "filebrowser"))
            client.addPlugin(getKoin().get<NetworkFlipperPlugin>())
            client.start()
        }
    }
}