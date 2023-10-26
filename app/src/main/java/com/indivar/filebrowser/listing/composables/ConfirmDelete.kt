package com.indivar.filebrowser.listing.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.indivar.filebrowser.R
import com.indivar.filebrowser.utils.runWith

@Composable
fun ConfirmDelete(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.delete)) },
        confirmButton = {
            Button(onClick = onConfirm.runWith(onDismiss)) {
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
            Text(text = stringResource(id = R.string.do_you_want_to_delete))
        }

    )
}

@Preview(showBackground = true)
@Composable
private fun ConfirmDeletePreview() {
    ConfirmDelete(onConfirm = {

    }, onDismiss = {})
}