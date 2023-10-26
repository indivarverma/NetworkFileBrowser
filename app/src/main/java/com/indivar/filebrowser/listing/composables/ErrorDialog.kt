package com.indivar.filebrowser.listing.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.indivar.filebrowser.R
import com.indivar.filebrowser.common.core.utils.HandleableError

@Composable
fun ErrorDialog(handleableError: HandleableError) {

    AlertDialog(
        title = { Text(text = stringResource(id = R.string.error)) },
        confirmButton = {
            Button(onClick = handleableError.onClick) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        onDismissRequest = handleableError.onClick,
        text = {
            Text(
                handleableError.errorMessage,
            )
        }

    )
}

@Preview(showBackground = true)
@Composable
private fun ErrorDialogPreview() {
    ErrorDialog(
        handleableError = HandleableError(
            errorMessage = "This is an error",
            onClick = {},
        )
    )
}
