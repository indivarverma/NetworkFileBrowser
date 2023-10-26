package com.indivar.filebrowser.network.models

import com.indivar.filebrowser.models.FileSystemElement
import com.indivar.filebrowser.models.UserDetails

fun UserDataResponse.createUserDetails(authToken: String): UserDetails
    = UserDetails(
        firstName = firstName,
        lastName = lastName,
        topLevelDirectory = rootItem.fileSystemDirectory,
        authToken = authToken,
    )

val FileFolder.fileSystemElement: FileSystemElement
    get() = when (this.isDir) {
        false -> FileSystemElement.File(
            id = this.id,
            parentId = parentId,
            name = name,
            size = size ?: 0,
            modifiedAt = modificationDate,
            content = null,
            contentType = contentType,
        )

        true -> fileSystemDirectory
    }

val FileFolder.fileSystemDirectory: FileSystemElement.Directory
    get() = FileSystemElement.Directory(
        id = this.id,
        parentId = parentId,
        name = name,
        modifiedAt = modificationDate,
        children = emptyList(),
    )