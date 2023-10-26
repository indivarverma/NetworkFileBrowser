package com.indivar.filebrowser.network

import com.indivar.filebrowser.common.core.utils.INetworkHandler
import com.indivar.filebrowser.models.FileSystemElement
import com.indivar.filebrowser.models.UserDetails
import com.indivar.filebrowser.network.models.CreateFolderRequest
import com.indivar.filebrowser.network.models.createUserDetails
import com.indivar.filebrowser.network.models.fileSystemDirectory
import com.indivar.filebrowser.network.models.fileSystemElement
import io.reactivex.rxjava3.core.Observable

class NetworkHandler(
    private val networkApis: NetworkApis,
) : INetworkHandler {
    override fun getMe(
        authToken: String,
    ): Observable<UserDetails> =
        networkApis.getMe(authToken).map {
            it.createUserDetails(authToken)
        }

    override fun getItems(item: FileSystemElement.Directory): Observable<List<FileSystemElement>> =
        networkApis.getItems(
            itemId = item.id
        ).map { fileFolders ->
            fileFolders.map { fileFolder ->
                fileFolder.fileSystemElement
            }
        }

    override fun createDir(
        parentDir: FileSystemElement.Directory,
        name: String
    ): Observable<FileSystemElement.Directory> =
        networkApis.createFolder(
            itemId = parentDir.id,
            request = CreateFolderRequest(name)
        ).map { folder ->
            folder.fileSystemDirectory
        }

    override fun createFile(item: FileSystemElement.File): Observable<FileSystemElement.File> {
        TODO("Not yet implemented")
    }

    override fun delete(item: FileSystemElement): Observable<Any> =
        networkApis.deleteItem(
            itemId = item.id
        )

    override fun getFileContent(item: FileSystemElement.File): Observable<FileSystemElement.File> {
        TODO("Not yet implemented")
    }
}

