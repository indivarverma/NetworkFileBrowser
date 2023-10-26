package com.indivar.filebrowser.network

import android.util.Log
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.indivar.filebrowser.common.core.utils.ApplicationConstants
import com.indivar.filebrowser.common.core.utils.CredentialStore
import com.indivar.filebrowser.models.DetailedServerError
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.nio.charset.Charset
import java.util.Locale

object RetrofitUtils {

    fun createNetworkFlipperPlugin(): NetworkFlipperPlugin {
        return NetworkFlipperPlugin()
    }

    fun createNetworkFlipperInterceptor(networkFlipperPlugin: NetworkFlipperPlugin): Interceptor {
        return FlipperOkhttpInterceptor(networkFlipperPlugin)
    }

    fun bindRetrofit(
        networkFlipperInterceptor: Interceptor,
        credentialStore: CredentialStore,
    ): Retrofit {
        val okhttpClientBuilder = OkHttpClient().newBuilder()
        okhttpClientBuilder.networkInterceptors().apply {
            add(Interceptor {
                credentialStore.getStoredCredentials()?.let { credentials ->
                    val requestBuilder = it.request().newBuilder()
                    val encodedUserNamePass = credentials.encodedCredentials
                    requestBuilder.addHeader("Authorization", encodedUserNamePass)
                    it.proceed(requestBuilder.build())
                } ?: it.proceed(it.request())

            })
        }
        okhttpClientBuilder.addNetworkInterceptor(networkFlipperInterceptor)
        okhttpClientBuilder.addNetworkInterceptor(Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            val errorCode = response.code
            val body = response.body
            if (body?.contentType()?.subtype?.lowercase(Locale.getDefault()) == "json") {
                var errorMessage = ""
                // Assume default OK
                try {
                    val source = body.source()
                    source.request(Long.MAX_VALUE) // Buffer the entire body.
                    val buffer = source.buffer
                    val charset = body.contentType()?.charset(Charset.forName("UTF-8"))
                        ?: Charset.forName("UTF-8")
                    // Clone the existing buffer is they can only read once so we still want to pass the original one to the chain.
                    val json: String = buffer.clone().readString(charset)
                    val obj = JsonParser.parseString(json)
                    if (obj is JsonObject && obj.has("error")) {
                        errorMessage = obj.get("error").getAsString()
                    }
                } catch (e: Exception) {
                    Log.e("filebrowser", "Error: " + e.message)
                }
                if (errorCode > 399) {
                    throw DetailedServerError(errorCode, errorMessage)
                }
            }
            response
        })

        val moshi = Moshi.Builder().add(DateTime::class.java, DateTimeAdapter())
            .addLast(KotlinJsonAdapterFactory()).build()
        return Retrofit.Builder().baseUrl(ApplicationConstants.SERVER_URL)
            .client(okhttpClientBuilder.build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }
}