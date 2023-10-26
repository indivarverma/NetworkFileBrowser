package com.indivar.filebrowser.network.models

import com.squareup.moshi.Json
import org.joda.time.DateTime

data class FileFolder(
    @Json(name = "id")
    val id: String,
    @Json(name = "parentId")
    val parentId: String?,
    @Json(name = "name")
    val name: String,
    @Json(name = "size")
    val size: Long?,
    @Json(name = "isDir")
    val isDir: Boolean,
    @Json(name = "contentType")
    val contentType: String?,
    @Json(name = "modificationDate")
    val modificationDate: DateTime,
)