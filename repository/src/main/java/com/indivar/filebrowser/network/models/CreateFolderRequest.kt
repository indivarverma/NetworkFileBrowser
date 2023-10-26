package com.indivar.filebrowser.network.models

import com.squareup.moshi.Json

data class CreateFolderRequest(
    @Json(name = "name")
    val name: String,
)