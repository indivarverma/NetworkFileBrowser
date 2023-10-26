package com.indivar.filebrowser.listing.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.indivar.filebrowser.R
import com.indivar.filebrowser.common.core.listing.FileViewItem

@Composable
fun FileView(item: FileViewItem, onClick: (String, Boolean) -> Unit = { _, _ -> }) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth(fraction = 1.0f)
            .clickable {
                onClick.invoke(item.id, item.isImage)
            }
            .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
        Image(
            modifier = Modifier
                .padding(end = 16.dp)
                .size(30.dp),
            painter = painterResource(id = item.icon),
            contentDescription = item.name
        )
        Column(verticalArrangement = Arrangement.Center) {
            Text(text = item.name)
            Text(text = item.modifiedOn)
        }
    }
}

private val FileViewItem.icon: Int
    get() = if (this.isImage) R.drawable.png else R.drawable.attachment

@Preview(showBackground = true)
@Composable
private fun FileViewPreview() {
    FileView(
        item = FileViewItem(
            id = "sample_id_1",
            parentId = " parent 1",
            name = "sample 1",
            modifiedOn = "7 Oct 2021",
            isImage = true,
        ),
    )

}