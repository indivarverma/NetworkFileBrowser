package com.indivar.filebrowser.common.core.utils

import com.indivar.filebrowser.models.FileSystemElement
import com.indivar.filebrowser.models.UserDetails
import io.reactivex.rxjava3.core.Observable

interface INetworkHandler {
    fun getMe(authToken: String): Observable<UserDetails>
    fun getItems(item: FileSystemElement.Directory): Observable<List<FileSystemElement>>
    fun createDir(
        parentDir: FileSystemElement.Directory,
        name: String,
    ): Observable<FileSystemElement.Directory>

    fun createFile(item: FileSystemElement.File): Observable<FileSystemElement.File>
    fun delete(item: FileSystemElement): Observable<Any>
    fun getFileContent(item: FileSystemElement.File): Observable<FileSystemElement.File>
}