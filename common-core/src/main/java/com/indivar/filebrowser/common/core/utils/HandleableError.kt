package com.indivar.filebrowser.common.core.utils

data class HandleableError(
    val errorMessage: String,
    val onClick: () -> Unit,
)