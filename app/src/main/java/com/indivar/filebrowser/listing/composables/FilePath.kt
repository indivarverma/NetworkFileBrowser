package com.indivar.filebrowser.listing.composables

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilePath(path: List<String>) {
    FlowRow(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        path.forEach { pathItem ->
            Text(text = "> $pathItem")
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun FilePathPreview() {
    FilePath(path = listOf("Item 1", "Item 2", "Item 3"))
}