package com.indivar.filebrowser.common.core.listing

import com.indivar.filebrowser.common.core.mvvm.BaseViewModelDelegate
import com.indivar.filebrowser.common.core.mvvm.Reducer
import com.indivar.filebrowser.common.core.utils.CredentialStore
import com.indivar.filebrowser.common.core.utils.DataSerializer
import com.indivar.filebrowser.common.core.utils.HandleableError
import com.indivar.filebrowser.common.core.utils.INetworkHandler
import com.indivar.filebrowser.common.core.utils.UserAccountStore
import com.indivar.filebrowser.models.FileSystemElement
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class ListingViewModelDelegate(
    private val iNetworkHandler: INetworkHandler,
    private val credentialStore: CredentialStore,
    private val userAccountStore: UserAccountStore,
    private val dataSerializer: DataSerializer,
) : BaseViewModelDelegate<ViewState, ListingViewModelDelegate.DataState>() {
    private val currentDirectoryObs = BehaviorSubject.create<FileSystemElement.Directory>()
    private val refreshList = BehaviorSubject.create<Unit>()
    private val deleteObs = BehaviorSubject.create<FileSystemElement.Directory>()
    private val createObs = BehaviorSubject.create<CreateDirectory>()

    init {
        loadUserDetails()
        subscribeForFolderCreation()
        listenToAndFetchCurrentDirectory()
        listenForDeleteDirRequests()
    }

    private fun loadUserDetails() {
        enqueue { dataState ->
            userAccountStore.getTopDirectory()?.let {
                dataSerializer.deserialize<FileSystemElement.Directory>(
                    it,
                    FileSystemElement.Directory::class.java
                )
            }?.let { topLevelDir ->
                dataState.copy(
                    path = listOfNotNull(topLevelDir),
                    firstName = userAccountStore.geFirstName(),
                    lastName = userAccountStore.getLastName(),
                    currentDirectory = topLevelDir,
                    data = DirectoryData.Loading
                ).also {
                    currentDirectoryObs.onNext(topLevelDir)
                }
            } ?: dataState.copy(
                data = DirectoryData.ErrorLoadingAccount
            )


        }
    }

    private fun subscribeForFolderCreation() {
        createObs.switchMap { createDir ->
            iNetworkHandler.createDir(createDir.parent, createDir.name)
                .map<CreateDirectoryResult> { CreateDirectoryResult.Success(it) }.onErrorReturn {
                    CreateDirectoryResult.Failure(it)
                }
        }.map<Reducer<DataState>> { result ->
            { dataState ->
                when (result) {
                    is CreateDirectoryResult.Success -> dataState.copy(
                        createDirError = null,
                        data = if (dataState.currentDirectory?.id == result.item.parentId) {
                            when (val data = dataState.data) {
                                is DirectoryData.Fetched -> data.copy(lists = (data.lists + result.item).sortedBy { it.name.lowercase() })
                                is DirectoryData.FetchFailure, is DirectoryData.Loading -> data.also {
                                    retryListOrProfileFetch()
                                }

                                is DirectoryData.ErrorLoadingAccount -> data
                            }

                        } else {
                            dataState.data
                        },

                        )

                    is CreateDirectoryResult.Failure -> dataState.copy(
                        createDirError = result.error,
                    )
                }
            }

        }.enqueue()
    }


    private fun listenToAndFetchCurrentDirectory() {
        Observable.combineLatest(
            currentDirectoryObs, refreshList.startWithItem(Unit)
        ) { directory, _ -> directory }.switchMap {
            Observable.combineLatest(iNetworkHandler.getItems(it)
                .map<RetrievedItems> { items -> RetrievedItems.Success(items.sortedBy { item -> item.name.lowercase() }) }
                .onErrorReturn { error ->
                    RetrievedItems.Failure(error)
                }, Observable.just(it), ::Pair
            )
        }.map<Reducer<DataState>> { (retrieved, directory) ->
            { dataState ->
                dataState.copy(
                    currentDirectory = directory,
                    data = when (retrieved) {
                        is RetrievedItems.Success -> DirectoryData.Fetched(retrieved.items)
                        is RetrievedItems.Failure -> DirectoryData.FetchFailure(
                            retrieved.error,
                        )
                    },

                    )
            }
        }.enqueue()
    }

    private fun listenForDeleteDirRequests() {
        deleteObs.flatMap { directory ->
            iNetworkHandler.delete(directory).map<DeleteResult> {
                DeleteResult.Success
            }
                .onErrorReturn {
                    DeleteResult.Failure(it)
                }
        }.map<Reducer<DataState>> { result ->
            { dataState ->
                when (result) {
                    is DeleteResult.Failure -> {
                        dataState.copy(
                            deleteError = result.error,
                        )
                    }

                    is DeleteResult.Success -> {
                        dataState.copy(
                            deleteError = null,
                        ).also {
                            gotoParentDirectory()
                        }

                    }
                }
            }
        }.enqueue()
    }


    private fun onDirectoryClick(item: FileSystemElement.Directory) {
        enqueue { dataState ->
            val path = dataState.path
            val newPath = (if (path.isNotEmpty() && path.last().id != item.parentId) {
                path.dropLast(1)
            } else path) + item
            dataState.copy(
                path = newPath,
                data = DirectoryData.Loading,
            ).also {
                currentDirectoryObs.onNext(item)
            }
        }
    }

    private fun gotoParentDirectory() {
        enqueue { dataState ->
            val newPath = dataState.path.dropLast(1)
            dataState.copy(
                path = newPath,
                data = DirectoryData.Loading,
                triggerClose = newPath.isEmpty(),
            ).also {
                if (newPath.isNotEmpty()) {
                    val parentDir = newPath.last()
                    currentDirectoryObs.onNext(parentDir)
                }
            }
        }
    }

    private fun retryListOrProfileFetch() {
        enqueue { dataState ->
            (dataState.data as? DirectoryData.FetchFailure)?.let {
                refreshList.onNext(Unit)
                dataState.copy(
                    data = DirectoryData.Loading,
                )
            } ?: dataState
        }
    }

    private fun onCreateDirectory(name: String) {
        enqueue { dataState ->
            dataState.currentDirectory?.let {
                createObs.onNext(CreateDirectory(parent = it, name = name))
            }
            dataState
        }
    }

    private fun clearCreateDirError() {
        enqueue {
            it.copy(createDirError = null)
        }
    }

    private fun deleteDirectory() {
        enqueue { dataState ->
            dataState.currentDirectory?.let {
                deleteObs.onNext(it)
            }
            dataState
        }
    }

    private fun clearDeleteError() {
        enqueue {
            it.copy(deleteError = null)
        }
    }

    private fun onLogoutClick() {
        enqueue {
            credentialStore.clear()
            userAccountStore.clear()
            it.copy(
                triggerLogout = true
            )
        }
    }

    override fun mapToViewState(state: DataState): ViewState = ViewState(
        userName = "Hello ${state.firstName.orEmpty()} ${state.lastName.orEmpty()}",
        content = when (val data = state.data) {
            is DirectoryData.Loading -> DirectoryContent.Loading
            is DirectoryData.Fetched ->
                if (data.lists.isEmpty()) {
                    DirectoryContent.Empty
                } else {
                    DirectoryContent.HasContent(
                        directories = data.lists.mapNotNull { it as? FileSystemElement.Directory }
                            .map { it.getDirectoryViewItem(::onDirectoryClick) },
                        files = data.lists.mapNotNull { it as? FileSystemElement.File }
                            .map { it.fileViewItem })
                }

            is DirectoryData.FetchFailure -> DirectoryContent.Error(
                errorMessage = data.error.message ?: "Unknown error",
                retry = ::retryListOrProfileFetch
            )

            DirectoryData.ErrorLoadingAccount -> DirectoryContent.InvalidAccount
        },
        goToParent = ::gotoParentDirectory,
        currentDirectory = state.currentDirectory?.getDirectoryViewItem(::onDirectoryClick),
        onDelete = if ((state.currentDirectory?.parentId?.length ?: 0) > 0) {
            ::deleteDirectory
        } else null,
        onCreateDirectory = if (state.currentDirectory != null) {
            ::onCreateDirectory
        } else null,
        path = state.path.map { it.name },
        triggerClose = state.triggerClose,
        deleteError = state.deleteError?.let {
            HandleableError(
                it.message ?: "Unknown Error", ::clearDeleteError
            )
        },
        createDirError = state.createDirError?.let {
            HandleableError(
                it.message ?: "Unknown Error", ::clearCreateDirError
            )
        },
        triggerLogout = state.triggerLogout,
        onLogoutClick = ::onLogoutClick,
    )

    data class DataState(
        val firstName: String?,
        val lastName: String?,
        val path: List<FileSystemElement.Directory>,
        val currentDirectory: FileSystemElement.Directory?,
        val data: DirectoryData,
        val deleteError: Throwable?,
        val createDirError: Throwable?,
        val triggerClose: Boolean,
        val triggerLogout: Boolean,
    )

    override val initialState: DataState
        get() = DataState(
            firstName = null,
            lastName = null,
            currentDirectory = null,
            data = DirectoryData.Loading,
            deleteError = null,
            path = emptyList(),
            triggerClose = false,
            triggerLogout = false,
            createDirError = null,
        )

}
