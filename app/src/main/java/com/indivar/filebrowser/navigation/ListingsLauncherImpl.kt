package com.indivar.filebrowser.navigation

import android.app.Activity
import com.indivar.filebrowser.common.core.flash.ListingsLauncher
import com.indivar.filebrowser.common.core.utils.ApplicationConstants
import com.indivar.filebrowser.koininjections.Scopes
import com.indivar.filebrowser.listing.MainActivity
import java.util.UUID
import org.koin.android.ext.android.getKoin

class ListingsLauncherImpl(
    private val activity: Activity
): ListingsLauncher {
    override fun launchListingsScreen() {
        val scopeID = UUID.randomUUID().toString()
        activity.getKoin().getOrCreateScope(scopeID, Scopes.listings)
        activity.startActivity(
            MainActivity.getIntent(
            context = activity,
        ).putExtra(ApplicationConstants.KOIN_SCOPE_ID, scopeID))
    }

}