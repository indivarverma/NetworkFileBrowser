package com.indivar.filebrowser.koininjections

import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.google.gson.Gson
import com.indivar.filebrowser.common.core.utils.AuthTokenCreator
import com.indivar.filebrowser.common.core.utils.AuthTokenCreatorImpl
import com.indivar.filebrowser.common.core.utils.Base64Encoder
import com.indivar.filebrowser.common.core.utils.Base64EncoderImpl
import com.indivar.filebrowser.common.core.utils.CredentialStore
import com.indivar.filebrowser.common.core.utils.DataSerializer
import com.indivar.filebrowser.common.core.utils.INetworkHandler
import com.indivar.filebrowser.common.core.utils.PersistentStore
import com.indivar.filebrowser.common.core.utils.UserAccountStore
import com.indivar.filebrowser.network.FileBrowserGson
import com.indivar.filebrowser.network.CredentialStoreImpl
import com.indivar.filebrowser.network.DataSerializerImpl
import com.indivar.filebrowser.network.JodaDateTimeAdapter
import com.indivar.filebrowser.network.NetworkApis
import com.indivar.filebrowser.network.NetworkHandler
import com.indivar.filebrowser.network.PersistentStoreImpl
import com.indivar.filebrowser.network.RetrofitUtils
import com.indivar.filebrowser.network.UserAccountStoreImpl
import okhttp3.Interceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import retrofit2.Retrofit

val koinModules = module {
    single<NetworkFlipperPlugin> {
        RetrofitUtils.createNetworkFlipperPlugin()
    }
    single<Interceptor>(qualifier = qualifier("flipper")) {
        RetrofitUtils.createNetworkFlipperInterceptor(
            get<NetworkFlipperPlugin>()
        )
    }

    single<PersistentStore> {
        PersistentStoreImpl(androidContext())
    }

    single<CredentialStore> {
        CredentialStoreImpl(
            get<PersistentStore> { it }
        )
    }
    single<UserAccountStore> {
        UserAccountStoreImpl(
            get<PersistentStore> { it }
        )
    }
    single<Base64Encoder> {
        Base64EncoderImpl()
    }
    single<AuthTokenCreator> {
        AuthTokenCreatorImpl(
            get<Base64Encoder>()
        )
    }
    single<Retrofit> {
        RetrofitUtils.bindRetrofit(
            get<Interceptor>(qualifier = qualifier("flipper")),
            get<CredentialStore>(),
        )
    }

    single<NetworkApis> {
        get<Retrofit>().create(NetworkApis::class.java)
    }

    single<INetworkHandler> {
        NetworkHandler(
            get<NetworkApis>()
        )
    }
    single<JodaDateTimeAdapter> {
        JodaDateTimeAdapter()
    }
    single<Gson> {
        FileBrowserGson.create(
            get<JodaDateTimeAdapter> { it }
        )

    }
    single<DataSerializer> {
        DataSerializerImpl(
            get<Gson>()
        )
    }

}

object Scopes {
    val bootup = named("bootup")
    val login = named("login")
    val listings = named("listing")
    val images = named("images")
}