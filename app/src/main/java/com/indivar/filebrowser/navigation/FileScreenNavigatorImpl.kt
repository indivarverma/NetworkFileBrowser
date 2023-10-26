package com.indivar.filebrowser.navigation

import android.app.Activity
import com.indivar.filebrowser.koininjections.Scopes
import com.indivar.filebrowser.common.core.utils.ApplicationConstants
import com.indivar.filebrowser.image.ImageViewerActivity
import com.indivar.filebrowser.login.LoginScreen
import org.koin.android.ext.android.getKoin
import java.util.UUID

class FileScreenNavigatorImpl(
    private val activity: Activity
) : FileScreenNavigator {
    override fun openImageFile(id: String) {
        val scopeID = UUID.randomUUID().toString()
        activity.getKoin().getOrCreateScope(scopeID, Scopes.images)
        activity.startActivity(
            ImageViewerActivity.getIntent(
                context = activity,
                url = "${ApplicationConstants.SERVER_URL}/items/$id/data"
            ).putExtra(ApplicationConstants.KOIN_SCOPE_ID, scopeID)
        )
    }

    override fun logout() {
        val scopeID = UUID.randomUUID().toString()
        activity.getKoin().getOrCreateScope(scopeID, Scopes.login)
        activity.startActivity(
            LoginScreen.getIntent(
                context = activity,
            ).putExtra(ApplicationConstants.KOIN_SCOPE_ID, scopeID)
        )
    }

}