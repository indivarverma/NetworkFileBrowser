package com.indivar.filebrowser.models

import org.joda.time.DateTime
import java.io.ByteArrayInputStream
sealed class FileSystemElement {
    abstract val name: String
    abstract val id: String
    abstract val parentId: String?
    abstract val modifiedAt: DateTime?
    data class Directory(
        override val id: String,
        override val parentId: String?,
        override val name: String,
        override val modifiedAt: DateTime?,
        val children: List<FileSystemElement>,
    ): FileSystemElement()
    data class File(
        override val id: String,
        override val parentId: String?,
        override val name: String,
        val contentType: String?,
        val size: Long,
        override val modifiedAt: DateTime,
        val content: FileContent?
    ): FileSystemElement()
}

data class FileContent(
    val stream: ByteArrayInputStream,
)