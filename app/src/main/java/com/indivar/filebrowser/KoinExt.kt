package com.indivar.filebrowser

import android.app.Activity
import com.indivar.filebrowser.common.core.utils.ApplicationConstants
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

val Activity.scopeId: String
    get() = requireNotNull(intent.getStringExtra(ApplicationConstants.KOIN_SCOPE_ID))

inline fun <reified T : Any> Activity.injectScoped(
    qualifier: Qualifier? = null,
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    noinline parameters: ParametersDefinition? = null
) = lazy(mode) {
    getKoin().getScope(scopeId = scopeId)
        .get<T>(qualifier = qualifier, parameters = parameters)
}
