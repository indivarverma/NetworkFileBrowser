package com.indivar.filebrowser.listing.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.indivar.filebrowser.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDirectoryDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember {
        mutableStateOf("")
    }
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.enter_folder_name)) },
        confirmButton = {
            Button(onClick = {
                onConfirm(text)
                onDismiss()
            }) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        onDismissRequest = onDismiss,
        text = {
            TextField(
                value = text,
                onValueChange = {
                    text = it
                }
            )
        }

    )
}

@Preview(showBackground = true)
@Composable
private fun CreateDirectoryDialogPreview() {
    CreateDirectoryDialog(
        onConfirm = {},
        onDismiss = {},
    )
}