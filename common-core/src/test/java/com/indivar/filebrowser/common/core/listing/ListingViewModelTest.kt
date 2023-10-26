package com.indivar.filebrowser.common.core.listing

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.indivar.filebrowser.common.core.MainCoroutineRule
import com.indivar.filebrowser.common.core.utils.CredentialStore
import com.indivar.filebrowser.common.core.utils.DataSerializer
import com.indivar.filebrowser.common.core.utils.INetworkHandler
import com.indivar.filebrowser.common.core.utils.UserAccountStore
import com.indivar.filebrowser.common.core.utils.createMockedDirectory
import com.indivar.filebrowser.common.core.utils.createMockedFile
import com.indivar.filebrowser.common.core.utils.getOrAwaitValue
import com.indivar.filebrowser.models.DetailedServerError
import com.indivar.filebrowser.models.FileSystemElement
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class ListingViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var credentialStore: CredentialStore
    private lateinit var userAccountStore: UserAccountStore
    private lateinit var networkHandler: INetworkHandler
    private lateinit var dataSerializer: DataSerializer
    private lateinit var error: DetailedServerError
    private lateinit var topLevelDirectory: FileSystemElement.Directory

    @Before
    fun init() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) }.returns(0)
        topLevelDirectory = mockk(relaxed = true)
        error = mockk<DetailedServerError>(relaxed = true).apply {
            every { message }.returns(ERROR_MESSAGE)
        }
        dataSerializer = mockk<DataSerializer>(relaxed = true) {
            every {
                deserialize<FileSystemElement.Directory>(
                    match { it == "top_level_directory_json" },
                    any()
                )
            }
                .returns(topLevelDirectory)
        }
        credentialStore = mockk<CredentialStore>(relaxed = true)
        userAccountStore = mockk<UserAccountStore>(relaxed = true) {
            every { getTopDirectory() }.returns("top_level_directory_json")
        }
        networkHandler = mockk<INetworkHandler>(relaxed = true)
    }

    @Test
    fun `Confirm Empty screen shows when top directory is empty`() =
        runTest {
            setTopLevelDirectoryId("top_level_directory_id")
            setTopLevelDirectoryName("top_directory_name")
            setContentForDirectory("top_level_directory_id", emptyList())
            val delegate = ListingViewModelDelegate(
                iNetworkHandler = networkHandler,
                credentialStore = credentialStore,
                dataSerializer = dataSerializer,
                userAccountStore = userAccountStore
            )
            val viewmodel = ListingViewModel(delegate = delegate)
            val value1 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            Assert.assertEquals("top_level_directory_id", value1.currentDirectory?.id)
            Assert.assertEquals(DirectoryContent.Empty, value1.content)
        }

    @Test
    fun `Confirm contents show when top directory is not empty`() =
        runTest {
            setTopLevelDirectoryId("top_level_directory_id")
            setTopLevelDirectoryName("top_directory_name")
            val mockedDirectory1 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory2 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory3 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory4 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedFile1 = createMockedFile(parentIdentifier = "top_level_directory_id")
            val mockedFile2 = createMockedFile(parentIdentifier = "top_level_directory_id")
            setContentForDirectory(
                "top_level_directory_id", listOf(
                    mockedDirectory1,
                    mockedDirectory2,
                    mockedDirectory3,
                    mockedDirectory4,
                    mockedFile1,
                    mockedFile2,
                )
            )
            val delegate = ListingViewModelDelegate(
                iNetworkHandler = networkHandler,
                credentialStore = credentialStore,
                dataSerializer = dataSerializer,
                userAccountStore = userAccountStore
            )
            val viewmodel = ListingViewModel(delegate = delegate)
            val value1 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            Assert.assertEquals("top_level_directory_id", value1.currentDirectory?.id)
            Assert.assertTrue(value1.content is DirectoryContent.HasContent)
            Assert.assertEquals(4, (value1.content as DirectoryContent.HasContent).directories.size)
            Assert.assertEquals(2, (value1.content as DirectoryContent.HasContent).files.size)
        }

    @Test
    fun `Shows Retry button when network fetch is a failure and retry action fetches data`() =
        runTest {
            setTopLevelDirectoryId("top_level_directory_id")
            setTopLevelDirectoryName("top_directory_name")
            setErrorInContentForDirectory("top_level_directory_id", error)
            val delegate = ListingViewModelDelegate(
                iNetworkHandler = networkHandler,
                credentialStore = credentialStore,
                dataSerializer = dataSerializer,
                userAccountStore = userAccountStore
            )
            val viewmodel = ListingViewModel(delegate = delegate)
            val value1 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            Assert.assertEquals("top_level_directory_id", value1.currentDirectory?.id)
            Assert.assertTrue(value1.content is DirectoryContent.Error)
            Assert.assertEquals(
                ERROR_MESSAGE,
                (value1.content as DirectoryContent.Error).errorMessage
            )
            val mockedDirectory1 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory2 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory3 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory4 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedFile1 = createMockedFile(parentIdentifier = "top_level_directory_id")
            val mockedFile2 = createMockedFile(parentIdentifier = "top_level_directory_id")
            setContentForDirectory(
                "top_level_directory_id", listOf(
                    mockedDirectory1,
                    mockedDirectory2,
                    mockedDirectory3,
                    mockedDirectory4,
                    mockedFile1,
                    mockedFile2,
                )
            )
            (value1.content as? DirectoryContent.Error)?.retry?.invoke()
            val value2 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            Assert.assertEquals("top_level_directory_id", value2.currentDirectory?.id)
            Assert.assertTrue(value2.content is DirectoryContent.HasContent)
            Assert.assertEquals(4, (value2.content as DirectoryContent.HasContent).directories.size)
            Assert.assertEquals(2, (value2.content as DirectoryContent.HasContent).files.size)
        }

    @Test
    fun `Clicking Item the top level directory opens contents in second level directory`() =
        runTest {
            setTopLevelDirectoryId("top_level_directory_id")
            setTopLevelDirectoryName("top_directory_name")
            val mockedDirectory1 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory2 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory3 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory4 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedFile1 = createMockedFile(parentIdentifier = "top_level_directory_id")
            val mockedFile2 = createMockedFile(parentIdentifier = "top_level_directory_id")
            setContentForDirectory(
                "top_level_directory_id", listOf(
                    mockedDirectory1,
                    mockedDirectory2,
                    mockedDirectory3,
                    mockedDirectory4,
                    mockedFile1,
                    mockedFile2,
                )
            )
            val delegate = ListingViewModelDelegate(
                iNetworkHandler = networkHandler,
                credentialStore = credentialStore,
                dataSerializer = dataSerializer,
                userAccountStore = userAccountStore
            )
            val viewmodel = ListingViewModel(delegate = delegate)
            val value1 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            Assert.assertEquals(listOf("top_directory_name"), value1.path)
            val secondLevelDir1InMockDir1 = createMockedFile(parentIdentifier = mockedDirectory1.id)
            val secondLevelDir2InMockDir1 = createMockedFile(parentIdentifier = mockedDirectory1.id)
            val secondLevelDir3InMockDir1 = createMockedFile(parentIdentifier = mockedDirectory1.id)
            setContentForDirectory(
                mockedDirectory1.id, listOf(
                    secondLevelDir1InMockDir1,
                    secondLevelDir2InMockDir1,
                    secondLevelDir3InMockDir1,
                )
            )
            val foundChild =
                (value1.content as? DirectoryContent.HasContent)?.directories?.first { it.id == mockedDirectory1.id }
            foundChild?.onClick?.invoke(mockedDirectory1)
            val value2 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            Assert.assertEquals(listOf("top_directory_name", mockedDirectory1.name), value2.path)
            Assert.assertEquals(mockedDirectory1.id, value2.currentDirectory?.id)
            Assert.assertTrue(value2.content is DirectoryContent.HasContent)
            Assert.assertEquals(0, (value2.content as DirectoryContent.HasContent).directories.size)
            Assert.assertEquals(3, (value2.content as DirectoryContent.HasContent).files.size)
        }

    @Test
    fun `Creating a new folder updates list with new folder`() =
        runTest {
            setTopLevelDirectoryId("top_level_directory_id")
            setTopLevelDirectoryName("top_directory_name")
            val mockedDirectory1 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory2 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory3 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory4 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedFile1 = createMockedFile(parentIdentifier = "top_level_directory_id")
            val mockedFile2 = createMockedFile(parentIdentifier = "top_level_directory_id")
            setContentForDirectory(
                "top_level_directory_id", listOf(
                    mockedDirectory1,
                    mockedDirectory2,
                    mockedDirectory3,
                    mockedDirectory4,
                    mockedFile1,
                    mockedFile2,
                )
            )
            val delegate = ListingViewModelDelegate(
                iNetworkHandler = networkHandler,
                credentialStore = credentialStore,
                dataSerializer = dataSerializer,
                userAccountStore = userAccountStore
            )
            val viewmodel = ListingViewModel(delegate = delegate)
            val value1 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            val newDirectory = setNewFolderResponse("top_level_directory_id", "New Folder")
            value1.onCreateDirectory?.invoke("New Folder")
            val value2 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            Assert.assertNotNull((value2.content as? DirectoryContent.HasContent)?.directories?.find { it.id == newDirectory.id && it.name == "New Folder" && it.parentId == "top_level_directory_id" })
        }

    @Test
    fun `Creating a new folder with error response displays error dialog which dismisses on click`() =
        runTest {
            setTopLevelDirectoryId("top_level_directory_id")
            setTopLevelDirectoryName("top_directory_name")
            val mockedDirectory1 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory2 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory3 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory4 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedFile1 = createMockedFile(parentIdentifier = "top_level_directory_id")
            val mockedFile2 = createMockedFile(parentIdentifier = "top_level_directory_id")
            setContentForDirectory(
                "top_level_directory_id", listOf(
                    mockedDirectory1,
                    mockedDirectory2,
                    mockedDirectory3,
                    mockedDirectory4,
                    mockedFile1,
                    mockedFile2,
                )
            )
            val delegate = ListingViewModelDelegate(
                iNetworkHandler = networkHandler,
                credentialStore = credentialStore,
                dataSerializer = dataSerializer,
                userAccountStore = userAccountStore
            )
            val viewmodel = ListingViewModel(delegate = delegate)
            val value1 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            setNewFolderErrorResponse()
            value1.onCreateDirectory?.invoke("New Folder")
            val value2 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            Assert.assertNotNull(value2.createDirError)
            Assert.assertEquals(ERROR_MESSAGE, value2.createDirError?.errorMessage)
            value2.createDirError?.onClick?.invoke()
            val value3 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            Assert.assertNull(value3.createDirError)
        }

    @Test
    fun `Deleting a folder with error response displays error dialog which dismisses on click`() =
        runTest {
            setTopLevelDirectoryId("top_level_directory_id")
            setTopLevelDirectoryName("top_directory_name")
            val mockedDirectory1 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory2 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")

            setContentForDirectory(
                "top_level_directory_id", listOf(
                    mockedDirectory1,
                    mockedDirectory2,
                )
            )
            val delegate = ListingViewModelDelegate(
                iNetworkHandler = networkHandler,
                credentialStore = credentialStore,
                dataSerializer = dataSerializer,
                userAccountStore = userAccountStore
            )
            val viewmodel = ListingViewModel(delegate = delegate)
            val value1 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            val secondLevelDir1InMockDir1 = createMockedFile(parentIdentifier = mockedDirectory1.id)
            val secondLevelDir2InMockDir1 = createMockedFile(parentIdentifier = mockedDirectory1.id)
            setContentForDirectory(
                mockedDirectory1.id, listOf(
                    secondLevelDir1InMockDir1,
                    secondLevelDir2InMockDir1,
                )
            )
            val foundChild =
                (value1.content as? DirectoryContent.HasContent)?.directories?.first { it.id == mockedDirectory1.id }
            foundChild?.onClick?.invoke(mockedDirectory1)
            val value2 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            setDeleteFolderErrorResponse()
            value2.onDelete?.invoke()
            val value3 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            Assert.assertNotNull(value3.deleteError)
            Assert.assertEquals(ERROR_MESSAGE, value3.deleteError?.errorMessage)
            value3.deleteError?.onClick?.invoke()
            val value4 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            Assert.assertNull(value4.deleteError)
        }

    @Test
    fun `Deleting a folder with success response takes the user back one folder`() =
        runTest {
            setTopLevelDirectoryId("top_level_directory_id")
            setTopLevelDirectoryName("top_directory_name")
            val mockedDirectory1 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory2 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")

            setContentForDirectory(
                "top_level_directory_id", listOf(
                    mockedDirectory1,
                    mockedDirectory2,
                )
            )
            val delegate = ListingViewModelDelegate(
                iNetworkHandler = networkHandler,
                credentialStore = credentialStore,
                dataSerializer = dataSerializer,
                userAccountStore = userAccountStore
            )
            val viewmodel = ListingViewModel(delegate = delegate)
            val value1 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            val secondLevelDir1InMockDir1 = createMockedFile(parentIdentifier = mockedDirectory1.id)
            val secondLevelDir2InMockDir1 = createMockedFile(parentIdentifier = mockedDirectory1.id)
            setContentForDirectory(
                mockedDirectory1.id, listOf(
                    secondLevelDir1InMockDir1,
                    secondLevelDir2InMockDir1,
                )
            )
            val foundChild =
                (value1.content as? DirectoryContent.HasContent)?.directories?.first { it.id == mockedDirectory1.id }
            foundChild?.onClick?.invoke(mockedDirectory1)
            val value2 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            Assert.assertEquals(listOf("top_directory_name", mockedDirectory1.name), value2.path)
            setDeleteFolderResponse()
            value2.onDelete?.invoke()
            val value3 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            Assert.assertNull(value3.deleteError)
            Assert.assertEquals(listOf("top_directory_name"), value3.path)

        }

    @Test
    fun `Logout takes the user out of listings screen`() =
        runTest {
            setTopLevelDirectoryId("top_level_directory_id")
            setTopLevelDirectoryName("top_directory_name")
            val mockedDirectory1 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")
            val mockedDirectory2 =
                createMockedDirectory(parentIdentifier = "top_level_directory_id")

            setContentForDirectory(
                "top_level_directory_id", listOf(
                    mockedDirectory1,
                    mockedDirectory2,
                )
            )
            val delegate = ListingViewModelDelegate(
                iNetworkHandler = networkHandler,
                credentialStore = credentialStore,
                dataSerializer = dataSerializer,
                userAccountStore = userAccountStore
            )
            val viewmodel = ListingViewModel(delegate = delegate)
            val value1 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            value1.onLogoutClick.invoke()
            val value2 = viewmodel.state.getOrAwaitValue(1, TimeUnit.SECONDS)
            verify(exactly = 1) { userAccountStore.clear() }
            verify(exactly = 1) { credentialStore.clear() }
            Assert.assertTrue(value2.triggerLogout)
        }

    private fun setTopLevelDirectoryId(id: String) {
        every { topLevelDirectory.id }.returns(id)
    }

    private fun setTopLevelDirectoryName(name: String) {
        every { topLevelDirectory.name }.returns(name)
    }

    private fun setContentForDirectory(id: String, content: List<FileSystemElement>) {
        every { networkHandler.getItems(match { it.id == id }) }.returns(
            Observable.just(content)
        )
    }

    private fun setErrorInContentForDirectory(id: String, error: DetailedServerError) {
        every { networkHandler.getItems(match { it.id == id }) }.returns(
            Observable.error(error)
        )
    }

    private fun setNewFolderResponse(
        parentDirID: String,
        name: String
    ): FileSystemElement.Directory {
        val mockedDir = createMockedDirectory(dirname = name, parentIdentifier = parentDirID)
        every { networkHandler.createDir(any(), name) }.returns(
            Observable.just(mockedDir)
        )
        return mockedDir
    }

    private fun setNewFolderErrorResponse() {
        every { networkHandler.createDir(any(), any()) }.returns(
            Observable.error(error)
        )
    }

    private fun setDeleteFolderResponse() {
        every { networkHandler.delete(any()) }.returns(
            Observable.just(Any())
        )
    }

    private fun setDeleteFolderErrorResponse() {
        every { networkHandler.delete(any()) }.returns(
            Observable.error(error)
        )
    }

    companion object {
        const val CORRECT_CREDENTIALS = "Basic bm9lbDpmb29iYXI="
        const val ERROR_MESSAGE = "encountered error"
    }
}