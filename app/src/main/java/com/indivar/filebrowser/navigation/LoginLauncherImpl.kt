package com.indivar.filebrowser.navigation

import android.app.Activity
import com.indivar.filebrowser.common.core.flash.LoginLauncher
import com.indivar.filebrowser.common.core.utils.ApplicationConstants
import com.indivar.filebrowser.koininjections.Scopes
import com.indivar.filebrowser.login.LoginScreen
import org.koin.android.ext.android.getKoin
import java.util.UUID

class LoginLauncherImpl(
    private val activity: Activity,
): LoginLauncher {
    override fun launchLogin() {
        val scopeID = UUID.randomUUID().toString()
        activity.getKoin().getOrCreateScope(scopeID, Scopes.login)
        activity.startActivity(
            LoginScreen.getIntent(
            context = activity,
        ).putExtra(ApplicationConstants.KOIN_SCOPE_ID, scopeID))
    }
}