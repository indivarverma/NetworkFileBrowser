package com.indivar.filebrowser.models

data class DetailedServerError(
    val errorCode: Int,
    val error: String?,
) : Exception("${error ?: "Encountered Error"} ($errorCode)")