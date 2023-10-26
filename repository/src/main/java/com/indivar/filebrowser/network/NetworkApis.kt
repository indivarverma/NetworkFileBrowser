package com.indivar.filebrowser.network

import com.indivar.filebrowser.network.models.CreateFolderRequest
import com.indivar.filebrowser.network.models.FileFolder
import com.indivar.filebrowser.network.models.UserDataResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface NetworkApis {
    @GET("/me")
    fun getMe(
        @Header("Authorization") authToken: String,
    ): Observable<UserDataResponse>

    @GET("/items/{itemId}")
    fun getItems(
        @Path("itemId") itemId: String,
    ): Observable<List<FileFolder>>

    @DELETE("/items/{itemId}")
    fun deleteItem(
        @Path("itemId") itemId: String,
    ): Observable<Any>

    @POST("/items/{itemId}")
    @Headers(
        "Content-Type: application/json"
    )
    fun createFolder(
        @Path("itemId") itemId: String,
        @Body request: CreateFolderRequest,
    ): Observable<FileFolder>

}
