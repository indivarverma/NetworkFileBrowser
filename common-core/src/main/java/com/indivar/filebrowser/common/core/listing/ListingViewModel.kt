package com.indivar.filebrowser.common.core.listing

import com.indivar.filebrowser.common.core.mvvm.BaseViewModel
import com.indivar.filebrowser.common.core.utils.HandleableError
import com.indivar.filebrowser.models.FileSystemElement

data class ViewState(
    val userName: String,
    val content: DirectoryContent,
    val path: List<String>,
    val currentDirectory: DirectoryViewItem?,
    val goToParent: () -> Unit,
    val onDelete: (() -> Unit)?,
    val deleteError: HandleableError?,
    val createDirError: HandleableError?,
    val onCreateDirectory: ((String) -> Unit)?,
    val triggerClose: Boolean,
    val onLogoutClick: () -> Unit,
    val triggerLogout: Boolean,
)

sealed class RetrievedItems {
    data class Success(val items: List<FileSystemElement>) : RetrievedItems()
    data class Failure(val error: Throwable) : RetrievedItems()
}

sealed class DirectoryContent {
    data class HasContent(
        val directories: List<DirectoryViewItem>,
        val files: List<FileViewItem>,
    ) : DirectoryContent()

    data class Error(
        val errorMessage: String,
        val retry: () -> Unit,
    ) : DirectoryContent()

    object Empty : DirectoryContent()
    object Loading : DirectoryContent()
    object InvalidAccount : DirectoryContent()
}

sealed class DirectoryData {
    data class Fetched(
        val lists: List<FileSystemElement>,
    ) : DirectoryData()

    data class FetchFailure(
        val error: Throwable,
    ) : DirectoryData()

    object Loading : DirectoryData()
    object ErrorLoadingAccount : DirectoryData()
}

data class DirectoryViewItem(
    val id: String,
    val parentId: String?,
    val name: String,
    val modifiedOn: String,
    val data: FileSystemElement.Directory,
    val onClick: (FileSystemElement.Directory) -> Unit,
)

data class FileViewItem(
    val id: String,
    val parentId: String?,
    val name: String,
    val modifiedOn: String,
    val isImage: Boolean,
)

sealed class DeleteResult {
    object Success : DeleteResult()

    data class Failure(
        val error: Throwable
    ) : DeleteResult()
}

data class CreateDirectory(
    val parent: FileSystemElement.Directory,
    val name: String,
)

sealed class CreateDirectoryResult {
    data class Success(
        val item: FileSystemElement.Directory,
    ) : CreateDirectoryResult()

    data class Failure(
        val error: Throwable
    ) : CreateDirectoryResult()
}

class ListingViewModel(delegate: ListingViewModelDelegate) :
    BaseViewModel<ViewState, ListingViewModelDelegate.DataState>(delegate)
