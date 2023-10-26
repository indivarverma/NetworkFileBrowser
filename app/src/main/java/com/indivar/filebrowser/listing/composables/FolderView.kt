package com.indivar.filebrowser.listing.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.indivar.filebrowser.R
import com.indivar.filebrowser.common.core.listing.DirectoryViewItem

@Composable
fun FolderView(item: DirectoryViewItem) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize(fraction = 1.0f)
            .clickable {
                item.onClick.invoke(item.data)
            }
            .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
        Image(
            modifier = Modifier
                .padding(end = 16.dp)
                .size(30.dp),
            painter = painterResource(id = R.drawable.folder),
            contentDescription = item.name,
        )
        Column(verticalArrangement = Arrangement.Center) {
            Text(text = item.name)
            Text(text = item.modifiedOn)
        }

    }
}