package com.indivar.filebrowser.models

data class UserDetails(
    val firstName: String,
    val lastName: String,
    val topLevelDirectory: FileSystemElement.Directory,
    val authToken: String,
)
