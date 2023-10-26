package com.indivar.filebrowser.listing.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.indivar.filebrowser.R
import com.indivar.filebrowser.common.core.listing.DirectoryContent
import com.indivar.filebrowser.common.core.listing.FileViewItem

@Composable
fun DirectoryListView(
    content: DirectoryContent,
    onFileClick: (String, Boolean) -> Unit = { _, _ -> },
) {
    when (content) {
        is DirectoryContent.Error -> Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = content.errorMessage
                )
                Button(onClick = content.retry) {
                    Text(
                        text = stringResource(id = R.string.retry)
                    )
                }
            }

        }

        DirectoryContent.Empty -> Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center), text = stringResource(
                    id = R.string.no_files
                )
            )
        }

        is DirectoryContent.HasContent -> LazyColumn {
            items(items = content.directories) {
                FolderView(item = it)
            }
            items(items = content.files) {
                FileView(item = it, onClick = onFileClick)
            }
        }

        DirectoryContent.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        DirectoryContent.InvalidAccount -> Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center), text = stringResource(
                    id = R.string.invalid_account
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DirectoryListViewPreview() {
    DirectoryListView(
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
    )
}

@Preview(showBackground = true)
@Composable
private fun DirectoryListViewProgressPreview() {
    DirectoryListView(
        content = DirectoryContent.Loading,
    )
}
