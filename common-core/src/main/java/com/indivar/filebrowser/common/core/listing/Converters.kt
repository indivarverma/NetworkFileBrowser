package com.indivar.filebrowser.common.core.listing

import com.indivar.filebrowser.common.core.utils.TimeUtils.printableTime
import com.indivar.filebrowser.models.FileSystemElement

fun FileSystemElement.Directory.getDirectoryViewItem(
    onClick: (FileSystemElement.Directory) -> Unit,
): DirectoryViewItem = DirectoryViewItem(
    id = this.id,
    parentId = this.parentId,
    name = this.name,
    modifiedOn = modifiedAt.printableTime,
    data = this,
    onClick = onClick
)

val FileSystemElement.File.fileViewItem: FileViewItem
    get() = FileViewItem(
        name = this.name,
        id = this.id,
        parentId = this.parentId,
        modifiedOn = modifiedAt.printableTime,
        isImage = this.contentType?.startsWith("image") == true || with(this.name.lowercase()) {
            endsWith("png") || endsWith("jpg") || endsWith("jpeg")
        },
    )