package com.indivar.filebrowser.listing.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.indivar.filebrowser.common.core.listing.DirectoryContent
import com.indivar.filebrowser.common.core.listing.FileViewItem
import com.indivar.filebrowser.common.core.listing.ViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingsActionBar(
    state: ViewState,
    askCreate: () -> Unit,
    askDelete: () -> Unit,
) {
    TopAppBar(title = { Text(text = state.userName) }, navigationIcon = {
        IconButton(onClick = { state.goToParent() }) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
        }
    }, actions = {
        state.onCreateDirectory?.let {
            IconButton(onClick = askCreate) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }
        state.onDelete?.let {
            IconButton(onClick = askDelete) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
            }
        }
        IconButton(onClick = state.onLogoutClick) {
            Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = null)
        }
    })
}

@Preview(showBackground = true)
@Composable
private fun ListingsActionBarPreview() {
    ListingsActionBar(
        state = ViewState(
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
            onDelete = {},
            triggerClose = false,
            path = listOf("parent 0", "parent 1"),
            onCreateDirectory = {},
            createDirError = null,
            deleteError = null,
            onLogoutClick = {},
            triggerLogout = false,
        ),
        askCreate = { },
        askDelete = {})
}