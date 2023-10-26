package com.indivar.filebrowser.startup

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.indivar.filebrowser.koininjections.Scopes
import com.indivar.filebrowser.common.core.flash.FlashScreenCoordinator
import com.indivar.filebrowser.common.core.flash.FlashScreenViewModel
import com.indivar.filebrowser.common.core.flash.ViewState
import com.indivar.filebrowser.common.core.utils.ApplicationConstants
import com.indivar.filebrowser.injectScoped
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf
import java.util.UUID

class FlashScreen : ComponentActivity() {
    private val viewModel: FlashScreenViewModel by injectScoped<FlashScreenViewModel> { parametersOf(this) }
    private val flashScreenCoordinator by injectScoped<FlashScreenCoordinator> { parametersOf(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        warmUp()
        viewModel.state.observe(this) {
            it.render()
        }

    }

    private fun ViewState.render() = when (this) {
        ViewState.HasAuthorization -> flashScreenCoordinator.launchListingsScreen()
        ViewState.NotAuthorized -> flashScreenCoordinator.launchLogin()
    }.also {
        finish()
    }

    private fun warmUp() {
        val scopeID = UUID.randomUUID().toString()
        getKoin().getOrCreateScope(scopeID, Scopes.bootup)
        intent.putExtra(ApplicationConstants.KOIN_SCOPE_ID, scopeID)
    }
}