package com.indivar.filebrowser.network.models

import com.squareup.moshi.Json

data class UserDataResponse(
    @Json(name = "firstName")
    val firstName: String,
    @Json(name = "lastName")
    val lastName: String,
    @Json(name = "rootItem")
    val rootItem: FileFolder,
)