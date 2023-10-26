package com.indivar.filebrowser.listing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.indivar.filebrowser.common.core.listing.DirectoryContent
import com.indivar.filebrowser.common.core.listing.FileViewItem
import com.indivar.filebrowser.common.core.listing.ListingViewModel
import com.indivar.filebrowser.common.core.listing.ViewState
import com.indivar.filebrowser.injectScoped
import com.indivar.filebrowser.listing.composables.ConfirmDelete
import com.indivar.filebrowser.listing.composables.CreateDirectoryDialog
import com.indivar.filebrowser.listing.composables.DirectoryListView
import com.indivar.filebrowser.listing.composables.ErrorDialog
import com.indivar.filebrowser.listing.composables.FilePath
import com.indivar.filebrowser.listing.composables.ListingsActionBar
import com.indivar.filebrowser.navigation.FileScreenCoordinator
import com.indivar.filebrowser.ui.theme.NetworkFileBrowserTheme
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {
    private val viewModel: ListingViewModel by injectScoped { parametersOf(this) }
    private val coordinator: FileScreenCoordinator by injectScoped { parametersOf(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.observe(this) {

            it.composeScreen()
        }

    }

    private fun ViewState.composeScreen() {
        when {
            this.triggerClose -> finish()
            this.triggerLogout -> {
                finish()
                coordinator.logout()
            }

            else -> setContent {
                NetworkFileBrowserTheme {
                    ListScreenView(
                        state = this,
                        onFileClick = coordinator::onFileItemClicked,
                    )
                }
            }
        }
    }

    @Suppress
    override fun onBackPressed() {
        viewModel.state.value?.goToParent?.invoke() ?: super.onBackPressed()
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, MainActivity::class.java)
    }
}

@Composable
fun ListScreenView(
    state: ViewState,
    onFileClick: (String, Boolean) -> Unit = { _, _ -> },
) {
    var askDeleteDialog by remember {
        mutableStateOf(false)
    }
    var askCreateDialog by remember {
        mutableStateOf(false)
    }


    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column {
            ListingsActionBar(
                state = state,
                askDelete = {
                    askDeleteDialog = true
                },
                askCreate = {
                    askCreateDialog = true
                },
            )
            FilePath(state.path)
            DirectoryListView(
                content = state.content,
                onFileClick = onFileClick,
            )
            if (askDeleteDialog) {
                state.onDelete?.let {
                    ConfirmDelete(it) {
                        askDeleteDialog = false
                    }
                }

            }
            if (askCreateDialog) {
                state.onCreateDirectory?.let {
                    CreateDirectoryDialog(it) {
                        askCreateDialog = false
                    }
                }

            }

            (state.createDirError ?: state.deleteError)?.let {
                ErrorDialog(it)
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
private fun ListScreenViewPreview() {
    ListScreenView(
        ViewState(
            userName = "John Doe",
            content = DirectoryContent.HasContent(
                directories = emptyList(),
                files = listOf(
                    FileViewItem(
                        id = "sample_id_1",
                        parentId = " parent 1",
                        name = "sample 1",
                        modifiedOn = "7 Oct 2021",
                        isImage = true,
                    )
                ),
            ),
            currentDirectory = null,
            goToParent = { },
            onDelete = null,
            triggerClose = false,
            path = listOf("parent 0", "parent 1"),
            onCreateDirectory = null,
            createDirError = null,
            deleteError = null,
            onLogoutClick = {},
            triggerLogout = false,
        )
    )
}